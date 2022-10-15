package io.github.joeljeremy.deezpatch.core;

/** The event handler registry. */
public interface EventHandlerRegistry {
  /**
   * Scan class for methods annotated with {@link EventHandler} and register them as event handlers.
   *
   * @param eventHandlerClasses The classes to scan for {@link EventHandler} annotations.
   * @return Deez registry.
   */
  EventHandlerRegistry register(Class<?>... eventHandlerClasses);
}
