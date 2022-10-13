package io.github.joeljeremy.deezpatch.core.internal;

import io.github.joeljeremy.deezpatch.core.RequestHandler;

/**
 * Functional interface used in building lambdas via {@code LambdaMetafactory}. This is for
 * void-returning request handler methods.
 */
@FunctionalInterface
@Internal
public interface VoidRequestHandlerMethod {
  /**
   * Invoke the actual method annotated with {@link RequestHandler}.
   *
   * @param requestHandlerInstance The request handler instance.
   * @param request The dispatched request.
   */
  void invoke(Object requestHandlerInstance, Object request);
}
