package io.github.joeljeremy.deezpatch.core.internal.registries;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.RegisteredRequestHandler;
import io.github.joeljeremy.deezpatch.core.Request;
import io.github.joeljeremy.deezpatch.core.RequestHandler;
import io.github.joeljeremy.deezpatch.core.RequestHandlerProvider;
import io.github.joeljeremy.deezpatch.core.RequestHandlerRegistry;
import io.github.joeljeremy.deezpatch.core.RequestKey;
import io.github.joeljeremy.deezpatch.core.internal.Internal;
import io.github.joeljeremy.deezpatch.core.internal.LambdaFactory;
import io.github.joeljeremy.deezpatch.core.internal.RequestHandlerMethod;
import io.github.joeljeremy.deezpatch.core.internal.VoidRequestHandlerMethod;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

/** The default request handler registry. */
@Internal
public class DeezpatchRequestHandlerRegistry
    implements RequestHandlerRegistry, RequestHandlerProvider {

  private static final PrimitiveTypeMap PRIMITIVE_TYPE_MAP = new PrimitiveTypeMap();

  /**
   * Map key is the request type. It returns another map whose key is the result type. The second
   * map returns the request handler mapped to the result type.
   */
  private final Map<Type, Map<Type, RegisteredRequestHandler<?, ?>>> mappingsByRequestType =
      new WeakHashMap<>();

  private final InstanceProvider instanceProvider;

  /**
   * Constructor.
   *
   * @param instanceProvider The instance provider.
   */
  public DeezpatchRequestHandlerRegistry(InstanceProvider instanceProvider) {
    this.instanceProvider = requireNonNull(instanceProvider);
  }

  /** {@inheritDoc} */
  @Override
  public DeezpatchRequestHandlerRegistry register(Class<?>... requestHandlerClasses) {
    requireNonNull(requestHandlerClasses);

    for (Class<?> requestHandlerClass : requestHandlerClasses) {
      Method[] methods = requestHandlerClass.getMethods();
      // Register all methods marked with @RequestHandler.
      for (Method requestHandlerMethod : methods) {
        if (!requestHandlerMethod.isAnnotationPresent(RequestHandler.class)) {
          continue;
        }

        validateMethodParameters(requestHandlerMethod);

        // First parameter in the method is the request object.
        RequestKey<?, ?> requestType =
            RequestKey.from(requestHandlerMethod.getGenericParameterTypes()[0]);

        validateMethodReturnType(requestHandlerMethod, requestType);

        register(requestType, requestHandlerMethod);
      }
    }

    return this;
  }

  /** {@inheritDoc} */
  @Override
  public <T extends Request<R>, R> Optional<RegisteredRequestHandler<T, R>> getRequestHandlerFor(
      RequestKey<T, R> requestKey) {

    requireNonNull(requestKey);

    Map<Type, RegisteredRequestHandler<?, ?>> handlersByResultType =
        mappingsByRequestType.get(requestKey.requestType());

    if (handlersByResultType == null) {
      return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    RegisteredRequestHandler<T, R> handler =
        (RegisteredRequestHandler<T, R>) handlersByResultType.get(requestKey.resultType());

    return Optional.ofNullable(handler);
  }

  private void register(RequestKey<?, ?> requestType, Method requestHandlerMethod) {
    requireNonNull(requestType);
    requireNonNull(requestHandlerMethod);

    RegisteredRequestHandler<?, ?> builtHandler =
        buildRequestHandler(requestHandlerMethod, instanceProvider);

    Map<Type, RegisteredRequestHandler<?, ?>> handlersByResultType =
        mappingsByRequestType.computeIfAbsent(requestType.requestType(), k -> new WeakHashMap<>());

    if (handlersByResultType.putIfAbsent(requestType.resultType(), builtHandler) != null) {
      throw new UnsupportedOperationException(
          "Duplicate request handler registration for request type: "
              + requestType
              + ". Please note that primitive and wrapper result types "
              + "are considered the same.");
    }
  }

  private static RegisteredRequestHandler<?, ?> buildRequestHandler(
      Method requestHandlerMethod, InstanceProvider instanceProvider) {

    requireNonNull(requestHandlerMethod);
    requireNonNull(instanceProvider);

    // We build different lambdas for void-returning request handlers
    // methods and non-void returning ones.
    if (void.class.equals(requestHandlerMethod.getReturnType())) {
      // For void-returning methods, we automatically handle return of
      // an empty Optional to the dispatcher.
      return buildRequestHandlerWithVoidReturnType(requestHandlerMethod, instanceProvider);
    } else {
      return buildRequestHandlerWithReturnType(requestHandlerMethod, instanceProvider);
    }
  }

  private static RegisteredRequestHandler<?, ?> buildRequestHandlerWithReturnType(
      Method requestHandlerMethod, InstanceProvider instanceProvider) {

    RequestHandlerMethod requestHandlerMethodLambda =
        LambdaFactory.createLambdaFunction(requestHandlerMethod, RequestHandlerMethod.class);

    final Class<?> requestHandlerClass = requestHandlerMethod.getDeclaringClass();
    final String requestHandlerString = requestHandlerMethod.toGenericString();

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredRequestHandler<Request<Object>, Object>() {
      @Override
      public Optional<Object> invoke(Request<Object> request) {
        return Optional.ofNullable(
            requestHandlerMethodLambda.invoke(
                instanceProvider.getInstance(requestHandlerClass), request));
      }

      @Override
      public String toString() {
        return requestHandlerString;
      }
    };
  }

  private static RegisteredRequestHandler<?, ?> buildRequestHandlerWithVoidReturnType(
      Method requestHandlerMethod, InstanceProvider instanceProvider) {

    VoidRequestHandlerMethod requestHandlerMethodLambda =
        LambdaFactory.createLambdaFunction(requestHandlerMethod, VoidRequestHandlerMethod.class);

    final Class<?> requestHandlerClass = requestHandlerMethod.getDeclaringClass();
    final String requestHandlerString = requestHandlerMethod.toGenericString();

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredRequestHandler<Request<Object>, Object>() {
      @Override
      public Optional<Object> invoke(Request<Object> request) {
        requestHandlerMethodLambda.invoke(
            instanceProvider.getInstance(requestHandlerClass), request);
        // Method return type is void so always return an empty result.
        return Optional.empty();
      }

      @Override
      public String toString() {
        return requestHandlerString;
      }
    };
  }

  private static void validateMethodParameters(Method method) {
    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException(
          "Methods marked with @RequestHandler must accept a single parameter which is the request"
              + " object.");
    }
  }

  private static void validateMethodReturnType(
      Method requestHandlerMethod, RequestKey<?, ?> requestKey) {

    Type resultType = requestKey.resultType();
    Type methodReturnType = requestHandlerMethod.getGenericReturnType();

    // Attempt to convert result type and request handler method return type to
    // a primitive type before comparing because we treat wrappers and primitives
    // as interchangeable.
    Class<?> rawResultType = PRIMITIVE_TYPE_MAP.get(requestKey.rawResultType());
    Class<?> rawMethodReturnType = PRIMITIVE_TYPE_MAP.get(requestHandlerMethod.getReturnType());
    if (rawResultType.isPrimitive() && rawMethodReturnType.isPrimitive()) {
      resultType = rawResultType;
      methodReturnType = rawMethodReturnType;
    }

    if (!resultType.equals(methodReturnType)) {
      throw new UnsupportedOperationException(
          String.format(
              "Mismatch between request's result type '%s' and request "
                  + "handler method's return type '%s'. Please adjust accordingly.",
              resultType.getTypeName(), requestHandlerMethod.getGenericReturnType().getTypeName()));
    }
  }

  private static class PrimitiveTypeMap extends ClassValue<Class<?>> {
    /**
     * Map wrapper types to its primitive type. If type is not a wrapper type, the same type is
     * returned.
     */
    @Override
    protected Class<?> computeValue(Class<?> type) {
      if (Void.class.equals(type)) {
        return void.class;
      } else if (Integer.class.equals(type)) {
        return int.class;
      } else if (Short.class.equals(type)) {
        return short.class;
      } else if (Long.class.equals(type)) {
        return long.class;
      } else if (Float.class.equals(type)) {
        return float.class;
      } else if (Double.class.equals(type)) {
        return double.class;
      } else if (Byte.class.equals(type)) {
        return byte.class;
      } else if (Character.class.equals(type)) {
        return char.class;
      } else if (Boolean.class.equals(type)) {
        return boolean.class;
      }
      return type;
    }
  }
}
