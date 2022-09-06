package io.github.joeljeremy7.deezpatch.queries.dispatchers;

import io.github.joeljeremy7.deezpatch.queries.Query;
import io.github.joeljeremy7.deezpatch.queries.QueryDispatcher;
import io.github.joeljeremy7.deezpatch.queries.QueryHandlerProvider;
import io.github.joeljeremy7.deezpatch.queries.QueryHandlingException;
import io.github.joeljeremy7.deezpatch.queries.QueryType;
import io.github.joeljeremy7.deezpatch.queries.RegisteredQueryHandler;

import java.util.Optional;

/**
 * The default query dispatcher.
 */
public class DefaultQueryDispatcher implements QueryDispatcher {
    private final QueryHandlerProvider queryHandlerProvider;

    /**
     * Constructor.
     * 
     * @param queryHandlerProvider The query handler provider.
     */
    public DefaultQueryDispatcher(QueryHandlerProvider queryHandlerProvider) {
        this.queryHandlerProvider = queryHandlerProvider;
    }

    /** {@inheritDoc} */
    @Override
    public <Q extends Query<R>, R> Optional<R> send(Q query) {
        QueryType<Q, R> queryType = QueryType.from(query);
        RegisteredQueryHandler<Q, R> queryHandler =
            queryHandlerProvider.getQueryHandlerFor(queryType)
                .orElseThrow(() -> new QueryHandlingException(
                    "No query handler found for query of type: " + queryType
                ));
        
        return Optional.ofNullable(queryHandler.invoke(query));
    }
}
