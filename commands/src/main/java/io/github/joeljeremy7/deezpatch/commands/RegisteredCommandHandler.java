package io.github.joeljeremy7.deezpatch.commands;

/**
 * Represents a registered (invocable) command handler.
 * 
 * @param <T> The command type.
 */
public interface RegisteredCommandHandler<T> {
    /**
     * Invoke command handler.
     * 
     * @param command The dispatched command.
     */
    void invoke(T command);
}
