package io.github.joeljeremy.deezpatch.core;

/** The instance provider. */
public interface InstanceProvider {
  /**
   * Get an instance of the specified class.
   *
   * @param clazz The class to get an instance for.
   * @return The retrieved instance.
   * @throws IllegalStateException if an instance cannot be successfully retrieved.
   */
  Object getInstance(Class<?> clazz);
}
