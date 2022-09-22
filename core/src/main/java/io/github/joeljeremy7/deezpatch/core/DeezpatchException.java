package io.github.joeljeremy7.deezpatch.core;

/** An exception thrown in cases of Deezpatch errors. */
public class DeezpatchException extends RuntimeException {
  /**
   * Constructor.
   *
   * @param message The exception message.
   */
  public DeezpatchException(String message) {
    super(message);
  }

  /**
   * Constructor.
   *
   * @param message The exception message.
   * @param cause The exception cause.
   */
  public DeezpatchException(String message, Throwable cause) {
    super(message, cause);
  }
}
