package io.github.joeljeremy.emissary.core.internal;

import io.github.joeljeremy.emissary.core.RequestHandler;

/**
 * Functional interface used in building lambdas via {@code LambdaMetafactory}. This is for
 * non-void-returning request handler methods.
 */
@FunctionalInterface
@Internal
public interface RequestHandlerMethod {
  /**
   * Invoke the actual method annotated with {@link RequestHandler}.
   *
   * @param requestHandlerInstance The request handler instance.
   * @param request The dispatched request.
   * @return The request result.
   */
  Object invoke(Object requestHandlerInstance, Object request);
}
