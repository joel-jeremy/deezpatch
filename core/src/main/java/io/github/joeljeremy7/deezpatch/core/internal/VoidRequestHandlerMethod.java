package io.github.joeljeremy7.deezpatch.core.internal;

import io.github.joeljeremy7.deezpatch.core.RequestHandler;

/**
 * Functional interface used in building lambdas via {@code LambdaMetafactory}.
 * This is for void-returning request handler methods.
 */
@FunctionalInterface
public interface VoidRequestHandlerMethod {
    /**
     * Invoke the actual method annotated with {@link RequestHandler}.
     * 
     * @param requestHandlerInstance The request handler instance.
     * @param request The dispatched request.
     */
    void invoke(Object requestHandlerInstance, Object request);
}