package io.github.joeljeremy.emissary.core;

import java.util.Optional;

/** The request handler provider. */
public interface RequestHandlerProvider {
  /**
   * Get request handler for the specified request type.
   *
   * @param <T> The request type.
   * @param <R> The request result type.
   * @param requestKey The request key.
   * @return The request handler, if any is registered. Otherwise, an empty {@code Optional}.
   */
  <T extends Request<R>, R> Optional<RegisteredRequestHandler<T, R>> getRequestHandlerFor(
      RequestKey<T, R> requestKey);
}
