package io.github.joeljeremy7.deezpatch.queries.dispatchers;

import java.util.logging.Logger;
import java.util.logging.Level;

import io.github.joeljeremy7.deezpatch.queries.Query;
import io.github.joeljeremy7.deezpatch.queries.QueryDispatcher;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class LoggingQueryDispatcher implements QueryDispatcher {
    private static final Logger LOGGER = 
        Logger.getLogger(LoggingQueryDispatcher.class.getName());

    private final QueryDispatcher decorated;

    public LoggingQueryDispatcher(QueryDispatcher decorated) {
        this.decorated = requireNonNull(decorated);
    }

    @Override
    public <TQuery extends Query<TResult>, TResult> Optional<TResult> send(
            TQuery query
    ) {
        requireNonNull(query);

        LOGGER.log(
            Level.INFO, 
            "Received query of type {0}: {1}", 
            new Object[] {
                query.getClass().getName(),
                query
            }
        );
        
        Optional<TResult> result = decorated.send(query);

        result.ifPresentOrElse(
            r -> LOGGER.log(
                Level.INFO, 
                "Query {0} result: {1}", 
                new Object[] { 
                    query,getClass().getName(), 
                    r
                }
            ), 
            () -> LOGGER.log(
                Level.WARNING, 
                "No result for query {0}.", 
                query.getClass().getName()
            )
        );

        return result;
    }
}
