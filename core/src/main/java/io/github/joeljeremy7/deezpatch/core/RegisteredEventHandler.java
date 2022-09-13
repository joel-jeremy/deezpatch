package io.github.joeljeremy7.deezpatch.core;

/**
 * Represents a registered (invocable) event handler.
 * 
 * @param <T> The event type.
 */
public interface RegisteredEventHandler<T> {
    /**
     * Invoke event handler.
     * 
     * @param event The dispatched event.
     */
    void invoke(T event);
}
