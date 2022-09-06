package io.github.joeljeremy7.deezpatch.commands.dispatchers;

import io.github.joeljeremy7.deezpatch.commands.CommandDispatcher;
import io.github.joeljeremy7.deezpatch.commands.CommandHandlerProvider;
import io.github.joeljeremy7.deezpatch.commands.CommandHandlingException;
import io.github.joeljeremy7.deezpatch.commands.RegisteredCommandHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * The default (synchronous) {@link CommandDispatcher} implementation.
 */
public class DefaultCommandDispatcher implements CommandDispatcher {
    private static final Logger LOGGER = 
        Logger.getLogger(DefaultCommandDispatcher.class.getName());

    private final CommandHandlerProvider commandHandlerProvider;

    /**
     * Constructor.
     * 
     * @param commandHandlerProvider The command handler provider.
     */
    public DefaultCommandDispatcher(CommandHandlerProvider commandHandlerProvider) {
        this.commandHandlerProvider = requireNonNull(commandHandlerProvider);
    }

    /** {@inheritDoc} */
    @Override
    public <T> void send(T command) {
        requireNonNull(command);

        @SuppressWarnings("unchecked")
        Class<T> commandType = (Class<T>)command.getClass();
        RegisteredCommandHandler<T> handler = commandHandlerProvider.getCommandHandlerFor(commandType)
            .orElseThrow(
                () -> new CommandHandlingException(
                    "No command handler found for command of type: " + commandType.getName()
                )
            );
        try {
            handler.invoke(command);
        } catch (RuntimeException ex) {
            LOGGER.log(
                Level.SEVERE, 
                ex,
                () -> "Error occurred while dispatching command " + command.getClass() + 
                    " to command handler " + handler + "."
            );

            throw ex;
        }
    }
}
