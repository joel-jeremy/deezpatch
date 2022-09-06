package io.github.joeljeremy7.deezpatch.events.dispatchers;

import java.util.logging.Logger;

import io.github.joeljeremy7.deezpatch.events.EventDispatcher;

import java.util.logging.Level;

import static java.util.Objects.requireNonNull;

/**
 * A {@link EventDispatcher} implementation which logs all dispatched events.
 */
public class LoggingEventDispatcher implements EventDispatcher {
    private static final Logger LOGGER = 
        Logger.getLogger(LoggingEventDispatcher.class.getName());

    private final EventDispatcher decorated;

    /**
     * Constructor.
     * 
     * @param decorated The decorated event dispatcher.
     */
    public LoggingEventDispatcher(EventDispatcher decorated) {
        this.decorated = requireNonNull(decorated);
    }

    /** {@inheritDoc} */
    @Override
    public <T> void send(T event) {
        requireNonNull(event);

        LOGGER.log(
            Level.INFO, 
            "Received event of type {0}: {1}",
            new Object[] {
                event.getClass().getName(),
                event
            }
        );
        
        decorated.send(event);
    }
}
