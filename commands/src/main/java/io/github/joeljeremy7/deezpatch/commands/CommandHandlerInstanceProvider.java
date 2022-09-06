package io.github.joeljeremy7.deezpatch.commands;

/**
 * The command handler instance provider.
 */
public interface CommandHandlerInstanceProvider {
    /**
     * Get an instance of the specified command handler class.
     * 
     * @param commandHandlerClass The command handler class to get an instance for.
     * @return The retrieved command handler instance.
     * @throws IllegalStateException if an instance cannot be successfully retrieved.
     */
    Object getInstance(Class<?> commandHandlerClass);
}
