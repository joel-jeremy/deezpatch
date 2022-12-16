package io.github.joeljeremy.deezpatch.core;

import java.util.Optional;

/**
 * Represents a registered (invocable) middleware.
 *
 * @param <T> The request type.
 * @param <R> The request result type.
 */
public interface RegisteredMiddleware<R> {
  /**
   * Invoke the request handler.
   *
   * @param request The dispatched request.
   * @param next The next middleware in the request pipeline.
   * @return The request result.
   */
  Optional<R> invoke(Request<R> request, Middleware.Next<R> next);
}
