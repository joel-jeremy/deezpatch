package io.github.joeljeremy.deezpatch.core;

/** The request handler registry. */
public interface RequestHandlerRegistry {
  /**
   * Scan classes for methods annotated with supported request handler annotations and register them
   * as request handlers. The {@link RequestHandler} annotation is supported by default.
   *
   * @param requestHandlerClasses The classes to scan for supported request handler annotations. The
   *     {@link RequestHandler} annotation is supported by default.
   * @return Deez registry.
   */
  RequestHandlerRegistry register(Class<?>... requestHandlerClasses);
}
