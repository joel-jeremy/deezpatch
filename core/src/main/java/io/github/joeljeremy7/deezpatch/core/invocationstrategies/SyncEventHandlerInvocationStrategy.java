package io.github.joeljeremy7.deezpatch.core.invocationstrategies;

import io.github.joeljeremy7.deezpatch.core.Deezpatch;
import io.github.joeljeremy7.deezpatch.core.Deezpatch.EventHandlerInvocationStrategy;
import io.github.joeljeremy7.deezpatch.core.Event;
import io.github.joeljeremy7.deezpatch.core.RegisteredEventHandler;

/**
 * The default {@link EventHandlerInvocationStrategy} which invokes
 * the event handlers synchronously.
 */
public class SyncEventHandlerInvocationStrategy 
        implements Deezpatch.EventHandlerInvocationStrategy {
    
    /** {@inheritDoc} */
    @Override
    public <T extends Event> void invoke(
            RegisteredEventHandler<T> eventHandler,
            T event
    ) {
        eventHandler.invoke(event);
    }
}