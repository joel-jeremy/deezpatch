package io.github.joeljeremy7.deezpatch.queries;

/**
 * An exception thrown in cases of query handling errors.
 */
public class QueryHandlingException extends RuntimeException {
    /**
     * Constructor.
     * 
     * @param message The exception message.
     */
    public QueryHandlingException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * 
     * @param message The exception message.
     * @param cause The exception cause.
     */
    public QueryHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
