package io.github.joeljeremy7.deezpatch.queries;

/**
 * Represents a registered (invocable) query handler.
 * 
 * @param <Q> The query type.
 * @param <R> The query result type.
 */
public interface RegisteredQueryHandler<Q extends Query<R>, R> {
    /**
     * Invoke the query handler.
     * 
     * @param query The dispatched query.
     * @return The query result.
     */
    R invoke(Q query);
}
