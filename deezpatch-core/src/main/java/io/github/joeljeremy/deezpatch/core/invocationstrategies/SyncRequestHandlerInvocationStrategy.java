package io.github.joeljeremy.deezpatch.core.invocationstrategies;

import io.github.joeljeremy.deezpatch.core.Deezpatch;
import io.github.joeljeremy.deezpatch.core.Deezpatch.RequestHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.RegisteredRequestHandler;
import io.github.joeljeremy.deezpatch.core.Request;
import java.util.Optional;

/**
 * The default {@link RequestHandlerInvocationStrategy} which invokes the request handlers
 * synchronously.
 */
public class SyncRequestHandlerInvocationStrategy
    implements Deezpatch.RequestHandlerInvocationStrategy {

  /** {@inheritDoc} */
  @Override
  public <T extends Request<R>, R> Optional<R> invoke(
      RegisteredRequestHandler<T, R> requestHandler, T request) {

    return requestHandler.invoke(request);
  }
}
