package io.github.joeljeremy7.deezpatch.queries;

import java.util.Optional;

/**
 * The query dispatcher.
 */
public interface QueryDispatcher {
    /**
     * Dispatch query to registered query handlers and return query result.
     * 
     * @param <Q> The query type.
     * @param <R> The query result type.
     * @param query The query object.
     * @return The query result.
     */
    <Q extends Query<R>, R> Optional<R> send(Q query);
}
