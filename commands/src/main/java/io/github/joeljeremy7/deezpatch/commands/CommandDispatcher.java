package io.github.joeljeremy7.deezpatch.commands;

/**
 * The command dispatcher.
 */
public interface CommandDispatcher {
    /**
     * Dispatch command to the registered command handlers.
     * 
     * @param <T> The command type.
     * @param command The command to dispatch.
     */
    <T> void send(T command);
}
