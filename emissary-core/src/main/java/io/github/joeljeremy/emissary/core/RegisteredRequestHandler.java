package io.github.joeljeremy.emissary.core;

import java.util.Optional;

/**
 * Represents a registered (invocable) request handler.
 *
 * @param <T> The request type.
 * @param <R> The request result type.
 */
public interface RegisteredRequestHandler<T extends Request<R>, R> {
  /**
   * Invoke the request handler.
   *
   * @param request The dispatched request.
   * @return The request result.
   */
  Optional<R> invoke(T request);
}
