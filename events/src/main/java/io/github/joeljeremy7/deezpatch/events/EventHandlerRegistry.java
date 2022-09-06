package io.github.joeljeremy7.deezpatch.events;

/**
 * The event handler registry.
 */
public interface EventHandlerRegistry {
    /**
     * Scan class for methods annotated with {@link EventHandler} and
     * register them as event handlers.
     * 
     * @param eventHandlerClass The class to scan for {@link EventHandler}
     * annotations.
     * @return This registry.
     */
    EventHandlerRegistry scan(Class<?> eventHandlerClass);
}
