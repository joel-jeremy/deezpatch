package io.github.joeljeremy7.deezpatch.queries;

import java.util.Optional;

/**
 * The query handler provider.
 */
public interface QueryHandlerProvider {
    /**
     * Get query handler for the specified query type.
     * 
     * @param <Q> The query type.
     * @param <R> The query result type.
     * @param queryType The query type.
     * @return The query handler, if any is registered. Otherwise,
     * an empty {@code Optional}.
     */
    <Q extends Query<R>, R> Optional<RegisteredQueryHandler<Q, R>> getQueryHandlerFor(
        QueryType<Q, R> queryType
    );
}
