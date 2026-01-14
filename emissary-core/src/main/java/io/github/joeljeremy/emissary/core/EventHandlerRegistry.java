package io.github.joeljeremy.emissary.core;

/** The event handler registry. */
public interface EventHandlerRegistry {
  /**
   * Scan classes for methods annotated with supported event handler annotations and register them
   * as event handlers. The {@link EventHandler} annotation is supported by default.
   *
   * @param eventHandlerClasses The classes to scan for supported event handler annotations. The
   *     {@link EventHandler} annotation is supported by default.
   * @return Deez registry.
   */
  EventHandlerRegistry register(Class<?>... eventHandlerClasses);
}
