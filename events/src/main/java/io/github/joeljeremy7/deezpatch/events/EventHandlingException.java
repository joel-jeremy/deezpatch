package io.github.joeljeremy7.deezpatch.events;

/**
 * An exception thrown in cases of event handling errors.
 */
public class EventHandlingException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message The exception message.
     */
    public EventHandlingException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message The exception message.
     * @param cause The exception cause.
     */
    public EventHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
