package io.github.joeljeremy7.deezpatch.events;

/**
 * The event dispatcher.
 */
public interface EventDispatcher {
    /**
     * Dispatch event to the registered event handlers.
     * 
     * @param <T> The event type.
     * @param event The event to dispatch.
     */
    <T> void send(T event);
}
