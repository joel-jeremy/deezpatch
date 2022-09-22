package io.github.joeljeremy7.deezpatch.core.invocationstrategies;

import io.github.joeljeremy7.deezpatch.core.Deezpatch;
import io.github.joeljeremy7.deezpatch.core.Deezpatch.EventHandlerInvocationStrategy;
import io.github.joeljeremy7.deezpatch.core.Event;
import io.github.joeljeremy7.deezpatch.core.RegisteredEventHandler;
import java.util.List;

/**
 * The default {@link EventHandlerInvocationStrategy} which invokes the event handlers
 * synchronously.
 */
public class SyncEventHandlerInvocationStrategy
    implements Deezpatch.EventHandlerInvocationStrategy {

  /** {@inheritDoc} */
  @Override
  public <T extends Event> void invokeAll(List<RegisteredEventHandler<T>> eventHandlers, T event) {
    for (RegisteredEventHandler<T> eventHandler : eventHandlers) {
      eventHandler.invoke(event);
    }
  }
}
