package io.github.joeljeremy7.deezpatch.commands;

/**
 * An exception thrown in cases of command handling errors.
 */
public class CommandHandlingException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message The exception message.
     */
    public CommandHandlingException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message The exception message.
     * @param cause The exception cause.
     */
    public CommandHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
