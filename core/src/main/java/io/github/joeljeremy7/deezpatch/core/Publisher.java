package io.github.joeljeremy7.deezpatch.core;

/**
 * Publisher interface.
 */
public interface Publisher {
    /**
     * Publish event to the registered event handlers.
     * 
     * @param <T> The event type.
     * @param event The event to publish.
     */
    <T extends Event> void publish(T event);
}
