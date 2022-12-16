package io.github.joeljeremy.deezpatch.core.internal.registries;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.Middleware;
import io.github.joeljeremy.deezpatch.core.MiddlewarePipelineProvider;
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
import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchMiddlewareRegistry.MiddlewarePipelineFactory;
import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchMiddlewareRegistry.RegisteredMiddlewarePipeline;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
  private final MiddlewarePipelineProvider middlewarePipelineProvider;
  private final Set<Class<? extends Annotation>> requestHandlerAnnotations;

  /**
   * Constructor.
   *
   * @param instanceProvider The instance provider.
   * @param middlewarePipelineProvider The middleware pipeline provider.
   * @param requestHandlerAnnotations The supported request handler annotations.
   */
  public DeezpatchRequestHandlerRegistry(
      InstanceProvider instanceProvider,
      MiddlewarePipelineProvider middlewarePipelineProvider,
      Set<Class<? extends Annotation>> requestHandlerAnnotations) {
    this.instanceProvider = requireNonNull(instanceProvider);
    this.middlewarePipelineProvider = requireNonNull(middlewarePipelineProvider);
    this.requestHandlerAnnotations =
        withNativeRequestHandler(requireNonNull(requestHandlerAnnotations));
  }

  /** {@inheritDoc} */
  @Override
  public DeezpatchRequestHandlerRegistry register(Class<?>... requestHandlerClasses) {
    requireNonNull(requestHandlerClasses);

    for (Class<?> requestHandlerClass : requestHandlerClasses) {
      Method[] methods = requestHandlerClass.getMethods();
      // Register all methods marked with @RequestHandler.
      for (Method method : methods) {
        if (!isRequestHandler(method)) {
          continue;
        }

        validateMethodParameters(method);

        // First parameter in the method is the request object.
        RequestKey<Request<Object>, Object> requestType =
            RequestKey.from(method.getGenericParameterTypes()[0]);

        validateMethodReturnType(method, requestType);

        register(requestType, method);
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

  private void register(RequestKey<?, ?> requestKey, Method requestHandlerMethod) {
    requireNonNull(requestKey);
    requireNonNull(requestHandlerMethod);

    MiddlewarePipelineFactory<?> pipelineFactory =
        middlewarePipelineProvider.getPipelineFactoryFor(requestKey);

    RegisteredRequestHandler<?, ?> builtHandler =
        buildRequestHandler(requestHandlerMethod, pipelineFactory);

    Map<Type, RegisteredRequestHandler<?, ?>> handlersByResultType =
        mappingsByRequestType.computeIfAbsent(requestKey.requestType(), k -> new WeakHashMap<>());

    if (handlersByResultType.putIfAbsent(requestKey.resultType(), builtHandler) != null) {
      throw new UnsupportedOperationException(
          "Duplicate request handler registration for request type: "
              + requestKey
              + ". Please note that primitive and wrapper result types "
              + "are considered the same.");
    }
  }

  private boolean isRequestHandler(Method method) {
    for (Annotation annotation : method.getAnnotations()) {
      if (requestHandlerAnnotations.contains(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }

  private RegisteredRequestHandler<?, ?> buildRequestHandler(
      Method requestHandlerMethod, MiddlewarePipelineFactory<?> pipelineFactory) {

    requireNonNull(requestHandlerMethod);

    // We build different lambdas for void-returning request handlers
    // methods and non-void returning ones.
    if (void.class.equals(requestHandlerMethod.getReturnType())) {
      // For void-returning methods, we automatically handle return of
      // an empty Optional to the dispatcher.
      return buildRequestHandlerWithVoidReturnType(requestHandlerMethod, pipelineFactory);
    } else {
      return buildRequestHandlerWithReturnType(requestHandlerMethod, pipelineFactory);
    }
  }

  private RegisteredRequestHandler<?, ?> buildRequestHandlerWithReturnType(
      Method requestHandlerMethod, MiddlewarePipelineFactory<?> pipelineFactory) {

    RequestHandlerMethod requestHandlerMethodLambda =
        LambdaFactory.createLambdaFunction(requestHandlerMethod, RequestHandlerMethod.class);

    final Class<?> requestHandlerClass = requestHandlerMethod.getDeclaringClass();
    final String requestHandlerString = requestHandlerMethod.toGenericString();

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredRequestHandler<Request<Object>, Object>() {
      @Override
      public Optional<Object> invoke(Request<Object> request) {
        Middleware.Next<Object> next =
            () -> {
              return Optional.ofNullable(
                  requestHandlerMethodLambda.invoke(
                      instanceProvider.getInstance(requestHandlerClass), request));
            };

        @SuppressWarnings("unchecked")
        MiddlewarePipelineFactory<Object> pf = (MiddlewarePipelineFactory<Object>) pipelineFactory;

        RegisteredMiddlewarePipeline<Object> middlewares = pf.create(request, next);

        return middlewares.invoke();
      }

      @Override
      public String toString() {
        return requestHandlerString;
      }
    };
  }

  private RegisteredRequestHandler<?, ?> buildRequestHandlerWithVoidReturnType(
      Method requestHandlerMethod, MiddlewarePipelineFactory<?> pipelineFactory) {

    VoidRequestHandlerMethod requestHandlerMethodLambda =
        LambdaFactory.createLambdaFunction(requestHandlerMethod, VoidRequestHandlerMethod.class);

    final Class<?> requestHandlerClass = requestHandlerMethod.getDeclaringClass();
    final String requestHandlerMethodString = requestHandlerMethod.toGenericString();

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredRequestHandler<Request<Object>, Object>() {
      @Override
      public Optional<Object> invoke(Request<Object> request) {
        @SuppressWarnings("unchecked")
        MiddlewarePipelineFactory<Object> pf = (MiddlewarePipelineFactory<Object>) pipelineFactory;

        Middleware.Next<Object> next =
            () -> {
              requestHandlerMethodLambda.invoke(
                  instanceProvider.getInstance(requestHandlerClass), request);
              return Optional.empty();
            };

        RegisteredMiddlewarePipeline<Object> middlewares = pf.create(request, next);

        // Execute.
        middlewares.invoke();

        // Method return type is void so always return an empty result.
        return Optional.empty();
      }

      @Override
      public String toString() {
        return requestHandlerMethodString;
      }
    };
  }

  private static void validateMethodParameters(Method method) {
    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException(
          "Methods marked with @RequestHandler (or any of the supported request handler"
              + " annotations) must accept a single parameter which is the request object.");
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

  private static Set<Class<? extends Annotation>> withNativeRequestHandler(
      Set<Class<? extends Annotation>> requestHandlerAnnotations) {
    Set<Class<? extends Annotation>> merged = new HashSet<>(requestHandlerAnnotations);
    // The native @RequestHandler annotation.
    merged.add(RequestHandler.class);
    return Collections.unmodifiableSet(merged);
  }
}
