package io.github.joeljeremy7.deezpatch.core;

/** The request handler registry. */
public interface RequestHandlerRegistry {
  /**
   * Scan class for methods annotated with {@link RequestHandler} and register them as request
   * handlers.
   *
   * @param requestHandlerClasses The classes to scan for {@link RequestHandler} annotations.
   * @return Deez registry.
   */
  RequestHandlerRegistry register(Class<?>... requestHandlerClasses);
}
