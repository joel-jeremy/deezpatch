package io.github.joeljeremy.deezpatch.core;

import java.util.List;

/** The event handler provider. */
public interface EventHandlerProvider {
  /**
   * Get event handlers for the specified event.
   *
   * @param <T> The event type.
   * @param eventType The event type.
   * @return The list of event handlers, if any are registered. Otherwise, an empty {@code List}.
   */
  <T> List<RegisteredEventHandler<T>> getEventHandlersFor(Class<T> eventType);
}
