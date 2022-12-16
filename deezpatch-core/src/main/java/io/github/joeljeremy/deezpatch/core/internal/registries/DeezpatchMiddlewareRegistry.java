package io.github.joeljeremy.deezpatch.core.internal.registries;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.Middleware;
import io.github.joeljeremy.deezpatch.core.MiddlewarePipelineProvider;
import io.github.joeljeremy.deezpatch.core.MiddlewareRegistry;
import io.github.joeljeremy.deezpatch.core.RegisteredMiddleware;
import io.github.joeljeremy.deezpatch.core.Request;
import io.github.joeljeremy.deezpatch.core.RequestKey;
import io.github.joeljeremy.deezpatch.core.TypeUtilities;
import io.github.joeljeremy.deezpatch.core.internal.LambdaFactory;
import io.github.joeljeremy.deezpatch.core.internal.MiddlewareMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

public class DeezpatchMiddlewareRegistry implements MiddlewareRegistry, MiddlewarePipelineProvider {
  private static final PrimitiveTypeMap PRIMITIVE_TYPE_MAP = new PrimitiveTypeMap();
  private static final MiddlewarePipelineFactory<?> EMPTY =
      new DequeMiddlewarePipelineFactory<>(new ArrayDeque<>(0));

  private final Map<Type, Map<Type, Deque<RegisteredMiddleware<?>>>> mappingsByRequestType =
      new WeakHashMap<>();
  private final InstanceProvider instanceProvider;
  private final Set<Class<? extends Annotation>> middlewareAnnotations;

  /**
   * Constructor.
   *
   * @param instanceProvider The instance provider.
   * @param middlewareAnnotations The supported middleware annotations.
   */
  public DeezpatchMiddlewareRegistry(
      InstanceProvider instanceProvider, Set<Class<? extends Annotation>> middlewareAnnotations) {
    this.instanceProvider = requireNonNull(instanceProvider);
    this.middlewareAnnotations = withNativeMiddleware(requireNonNull(middlewareAnnotations));
  }

  @Override
  public DeezpatchMiddlewareRegistry register(Class<?>... middlewareClasses) {
    requireNonNull(middlewareClasses);

    for (Class<?> middlewareClass : middlewareClasses) {
      Method[] methods = middlewareClass.getMethods();
      // Register all methods marked with @Middleware.
      for (Method method : methods) {
        if (!isMiddleware(method)) {
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

  @Override
  public <T extends Request<R>, R> MiddlewarePipelineFactory<R> getPipelineFactoryFor(
      RequestKey<T, R> requestKey) {

    requireNonNull(requestKey);

    Map<Type, Deque<RegisteredMiddleware<?>>> middlewaresByResultType =
        mappingsByRequestType.get(requestKey.requestType());

    if (middlewaresByResultType == null) {
      @SuppressWarnings("unchecked")
      MiddlewarePipelineFactory<R> emptyPipeline = (MiddlewarePipelineFactory<R>) EMPTY;
      return emptyPipeline;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    Deque<RegisteredMiddleware<R>> middlewares =
        (Deque) middlewaresByResultType.get(requestKey.resultType());

    return new DequeMiddlewarePipelineFactory<>(middlewares);
  }

  private void register(RequestKey<?, ?> requestType, Method middlewareMethod) {
    requireNonNull(requestType);
    requireNonNull(middlewareMethod);

    Map<Type, Deque<RegisteredMiddleware<?>>> middlewaresByResultType =
        mappingsByRequestType.computeIfAbsent(requestType.requestType(), k -> new WeakHashMap<>());

    Deque<RegisteredMiddleware<?>> middlewares =
        middlewaresByResultType.computeIfAbsent(requestType.resultType(), k -> new ArrayDeque<>());

    RegisteredMiddleware<?> builtMiddleware = buildMiddleware(middlewareMethod);
    middlewares.offerLast(builtMiddleware);
  }

  private boolean isMiddleware(Method method) {
    for (Annotation annotation : method.getAnnotations()) {
      if (middlewareAnnotations.contains(annotation.annotationType())) {
        return true;
      }
    }
    return false;
  }

  private RegisteredMiddleware<?> buildMiddleware(Method middlewareMethod) {
    MiddlewareMethod middlewareMethodLambda =
        LambdaFactory.createLambdaFunction(middlewareMethod, MiddlewareMethod.class);

    final Class<?> requestHandlerClass = middlewareMethod.getDeclaringClass();
    final String middlewareMethodString = middlewareMethod.toGenericString();

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredMiddleware<Object>() {
      @Override
      public Optional<Object> invoke(Request<Object> request, Middleware.Next<Object> next) {
        return Optional.ofNullable(
            middlewareMethodLambda.invoke(
                instanceProvider.getInstance(requestHandlerClass), request, next));
      }

      @Override
      public String toString() {
        return middlewareMethodString;
      }
    };
  }

  private static void validateMethodParameters(Method method) {
    if (method.getParameterCount() != 2) {
      throw new IllegalArgumentException(
          "Methods marked with @Middleware (or any of the supported middleware annotations) must"
              + " accept two parameters which is the request object and a reference to the next"
              + " middleware in the pipeline (via Middleware.Next<R> where <R> is the request's"
              + " result type).");
    }

    Class<?>[] parameterTypes = method.getParameterTypes();
    Class<?> secondParameter = parameterTypes[1];
    if (!Middleware.Next.class.isAssignableFrom(secondParameter)) {
      throw new IllegalArgumentException(
          "Methods marked with @Middleware (or any of the supported middleware annotations) must"
              + " accept a reference to the next middleware in the pipeline (via Middleware.Next<R>"
              + " where <R> is the request's result type).");
    }
  }

  private static void validateMethodReturnType(
      Method middlewareMethod, RequestKey<?, ?> requestKey) {

    Type resultType = requestKey.resultType();
    Type methodReturnType = middlewareMethod.getGenericReturnType();

    // If method return type is an optional, compare Optional type parameter to
    // request's expected result type.
    ParameterizedType parameterizedMethodReturnType =
        TypeUtilities.asParameterizedType(methodReturnType);
    if (parameterizedMethodReturnType != null
        && Optional.class.equals(parameterizedMethodReturnType.getRawType())) {
      methodReturnType = parameterizedMethodReturnType.getActualTypeArguments()[0];
    }

    // Attempt to convert result type and middleware method return type to
    // a primitive type before comparing because we treat wrappers and primitives
    // as interchangeable.
    Class<?> rawResultType = PRIMITIVE_TYPE_MAP.get(requestKey.rawResultType());
    Class<?> rawMethodReturnType = PRIMITIVE_TYPE_MAP.get(middlewareMethod.getReturnType());
    if (rawResultType.isPrimitive() && rawMethodReturnType.isPrimitive()) {
      resultType = rawResultType;
      methodReturnType = rawMethodReturnType;
    }

    if (!resultType.equals(methodReturnType)) {
      throw new UnsupportedOperationException(
          String.format(
              "Mismatch between request's result type '%s' and middleware "
                  + "method's return type '%s'. Please adjust accordingly.",
              resultType.getTypeName(), middlewareMethod.getGenericReturnType().getTypeName()));
    }
  }

  private static Set<Class<? extends Annotation>> withNativeMiddleware(
      Set<Class<? extends Annotation>> middlewareAnnotations) {
    Set<Class<? extends Annotation>> merged = new HashSet<>(middlewareAnnotations);
    // The native @Middleware annotation.
    merged.add(Middleware.class);
    return Collections.unmodifiableSet(merged);
  }

  public static interface MiddlewarePipelineFactory<R> {
    <T extends Request<R>> RegisteredMiddlewarePipeline<R> create(
        T request, Middleware.Next<R> next);
  }

  public static interface RegisteredMiddlewarePipeline<R> {
    Optional<R> invoke();
  }

  private static class DequeMiddlewarePipelineFactory<R> implements MiddlewarePipelineFactory<R> {

    private final Iterator<RegisteredMiddleware<R>> descendingMiddlewares;

    public DequeMiddlewarePipelineFactory(Deque<RegisteredMiddleware<R>> middlewares) {
      this.descendingMiddlewares = middlewares.descendingIterator();
    }

    public <T extends Request<R>> RegisteredMiddlewarePipeline<R> create(
        T request, Middleware.Next<R> next) {
      while (descendingMiddlewares.hasNext()) {
        RegisteredMiddleware<R> middleware = descendingMiddlewares.next();
        next = chain(middleware, request, next);
      }
      return next::invoke;
    }

    private Middleware.Next<R> chain(
        RegisteredMiddleware<R> middleware, Request<R> request, Middleware.Next<R> next) {
      return () -> middleware.invoke(request, next);
    }
  }
}
