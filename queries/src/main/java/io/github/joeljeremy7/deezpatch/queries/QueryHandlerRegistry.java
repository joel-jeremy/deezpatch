package io.github.joeljeremy7.deezpatch.queries;

/**
 * The query handler registry.
 */
public interface QueryHandlerRegistry {
    /**
     * Scan class for methods annotated with {@link QueryHandler} and 
     * register them as query handlers.
     * 
     * @param queryHandlerClass The class to scan for {@link QueryHandler}
     * annotations.
     * @return This registry.
     */
    QueryHandlerRegistry scan(Class<?> queryHandlerClass);
}
