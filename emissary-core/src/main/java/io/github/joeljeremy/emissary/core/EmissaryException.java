package io.github.joeljeremy.emissary.core;

/** An exception thrown in cases of Emissary errors. */
public class EmissaryException extends RuntimeException {
  /**
   * Constructor.
   *
   * @param message The exception message.
   */
  public EmissaryException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message The exception message.
   * @param cause The exception cause.
   */
  public EmissaryException(String message, Throwable cause) {
    super(message, cause);
  }
}
