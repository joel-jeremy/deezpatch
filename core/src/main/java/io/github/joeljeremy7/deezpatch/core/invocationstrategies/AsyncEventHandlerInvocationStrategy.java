package io.github.joeljeremy7.deezpatch.core.invocationstrategies;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy7.deezpatch.core.Deezpatch.EventHandlerInvocationStrategy;
import io.github.joeljeremy7.deezpatch.core.Event;
import io.github.joeljeremy7.deezpatch.core.RegisteredEventHandler;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * A {@link EventHandlerInvocationStrategy} implementation which invokes the event handlers
 * asynchronously.
 */
public class AsyncEventHandlerInvocationStrategy implements EventHandlerInvocationStrategy {

  private static final Logger LOGGER =
      System.getLogger(AsyncEventHandlerInvocationStrategy.class.getName());

  private final ExecutorService executorService;
  private final ExceptionHandler exceptionHandler;

  /**
   * Constructor.
   *
   * @param executorService The executor service to execute event handlers with.
   * @param exceptionHandler The exception handler to handle event handler exceptions.
   */
  public AsyncEventHandlerInvocationStrategy(
      ExecutorService executorService, ExceptionHandler exceptionHandler) {
    this.executorService = requireNonNull(executorService);
    this.exceptionHandler = requireNonNull(exceptionHandler);
  }

  /** {@inheritDoc} */
  @Override
  public <T extends Event> void invokeAll(List<RegisteredEventHandler<T>> eventHandlers, T event) {
    for (RegisteredEventHandler<T> eventHandler : eventHandlers) {
      asyncInvoke(eventHandler, event);
    }
  }

  private <T extends Event> void asyncInvoke(RegisteredEventHandler<T> eventHandler, T event) {
    executorService.execute(
        () -> {
          try {
            eventHandler.invoke(event);
          } catch (Exception ex) {
            LOGGER.log(
                Level.ERROR,
                () ->
                    "Exception occurred while asynchronously dispatching event "
                        + event.getClass().getName()
                        + " to event handler "
                        + eventHandler
                        + ".",
                ex);
            exceptionHandler.handleException(event, ex);
          }
        });
  }

  /** The exception handler to handle event handler exceptions. */
  public static interface ExceptionHandler {
    /**
     * Handle event handler exception.
     *
     * @param event The dispatched event.
     * @param exception The event handler exception.
     */
    void handleException(Event event, Throwable exception);
  }
}
