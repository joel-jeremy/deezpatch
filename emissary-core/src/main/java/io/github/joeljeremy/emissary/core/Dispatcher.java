package io.github.joeljeremy.emissary.core;

import java.util.Optional;

/** Dispatcher interface. */
public interface Dispatcher {
  /**
   * Dispatch request to registered request handler and return result.
   *
   * @param <T> The request type.
   * @param <R> The result type.
   * @param request The request to dispatch.
   * @return The result.
   */
  <T extends Request<R>, R> Optional<R> send(T request);
}
