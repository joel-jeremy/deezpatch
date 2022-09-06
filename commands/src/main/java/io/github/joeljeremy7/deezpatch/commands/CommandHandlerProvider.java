package io.github.joeljeremy7.deezpatch.commands;

import java.util.Optional;

/**
 * The command handler provider.
 */
public interface CommandHandlerProvider {
    /**
     * Get command handler for the specified command.
     * 
     * @param <T> The command type.
     * @param commandType The command type.
     * @return The command handler, if any is registered. Otherwise,
     * an empty {@code Optional}.
     */
    <T> Optional<RegisteredCommandHandler<T>> getCommandHandlerFor(Class<T> commandType);
}
