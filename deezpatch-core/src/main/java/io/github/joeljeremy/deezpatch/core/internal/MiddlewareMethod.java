package io.github.joeljeremy.deezpatch.core.internal;

/** Functional interface used in building lambdas via {@code LambdaMetafactory}. */
@FunctionalInterface
@Internal
public interface MiddlewareMethod {
  /**
   * Invoke the actual method annotated with {@link Middleware}.
   *
   * @param middlewareInstance The middleware instance.
   * @param request The dispatched request.
   * @param nextMiddleware The next middleware in the pipeline.
   * @return The request result.
   */
  Object invoke(Object middlewareInstance, Object request, Object nextMiddleware);
}
