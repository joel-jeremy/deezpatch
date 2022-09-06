package io.github.joeljeremy7.deezpatch.commands.dispatchers;

import java.util.logging.Logger;

import io.github.joeljeremy7.deezpatch.commands.CommandDispatcher;

import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

/**
 * A {@link CommandDispatcher} implementation which logs all dispatched commands.
 */
public class LoggingCommandDispatcher implements CommandDispatcher {
    private static final Logger LOGGER = 
        Logger.getLogger(LoggingCommandDispatcher.class.getName());
    
    private final CommandDispatcher decorated;

    /**
     * Constructor.
     * 
     * @param decorated The decorated command dispatcher.
     */
    public LoggingCommandDispatcher(CommandDispatcher decorated) {
        this.decorated = requireNonNull(decorated);
    }

    /** {@inheritDoc} */
    @Override
    public <T> void send(T command) {
        requireNonNull(command);

        LOGGER.log(
            Level.INFO, 
            "Received command of type {0}: {1}", 
            new Object[] {
                command.getClass().getName(),
                command
            }
        );

        decorated.send(command);
    }
}
