package io.github.joeljeremy7.deezpatch.events.dispatchers;

import io.github.joeljeremy7.deezpatch.events.EventDispatcher;
import io.github.joeljeremy7.deezpatch.events.EventHandlerProvider;
import io.github.joeljeremy7.deezpatch.events.RegisteredEventHandler;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * The default (synchronous) event dispatcher.
 */
public class DefaultEventDispatcher implements EventDispatcher {

    private final EventHandlerProvider eventHandlerProvider;

    /**
     * Constructor.
     * 
     * @param eventHandlerProvider The event handler provider.
     */
    public DefaultEventDispatcher(EventHandlerProvider eventHandlerProvider) {
        this.eventHandlerProvider = requireNonNull(eventHandlerProvider);
    }

    /** {@inheritDoc} */
    @Override
    public <T> void send(T event) {
        requireNonNull(event);

        @SuppressWarnings("unchecked")
        Class<T> eventType = (Class<T>)event.getClass();

        List<RegisteredEventHandler<T>> eventHandlers = 
            eventHandlerProvider.getEventHandlersFor(eventType);
    
        eventHandlers.forEach(handler -> handler.invoke(event));
    }
}
