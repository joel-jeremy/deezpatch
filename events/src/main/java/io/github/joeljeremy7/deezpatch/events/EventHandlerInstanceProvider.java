package io.github.joeljeremy7.deezpatch.events;

/**
 * The event handler instance provider.
 */
public interface EventHandlerInstanceProvider {
    /**
     * Get an instance of the specified event handler class.
     * 
     * @param eventHandlerClass The event handler class to get an instance for.
     * @return The retrieved event handler instance.
     * @throws IllegalStateException if an instance cannot be successfully retrieved.
     */
    Object getInstance(Class<?> eventHandlerClass);
}
