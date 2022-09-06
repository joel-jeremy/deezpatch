package io.github.joeljeremy7.deezpatch.queries;

/**
 * The query handler instance provider.
 */
public interface QueryHandlerInstanceProvider {
    /**
     * Get an instance of the specified query handler class.
     * 
     * @param queryHandlerClass The query handler class to get an instance for.
     * @return The retrieved query handler instance.
     * @throws IllegalStateException if an instance cannot be successfully retrieved.
     */
    Object getInstance(Class<?> queryHandlerClass);
}
