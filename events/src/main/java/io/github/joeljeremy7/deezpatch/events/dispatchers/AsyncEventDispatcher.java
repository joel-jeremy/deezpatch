package io.github.joeljeremy7.deezpatch.events.dispatchers;

import io.github.joeljeremy7.deezpatch.events.EventDispatcher;

import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * An async implementation of {@link EventDispatcher}.
 */
public class AsyncEventDispatcher implements EventDispatcher {
    private static final Logger LOGGER = 
        Logger.getLogger(AsyncEventDispatcher.class.getName());
    
    private final EventDispatcher decorated;
    private final ExecutorService executorService;
    private final ExceptionHandler exceptionHandler;

    /**
     * Constructor.
     * 
     * @param decorated The decorated event dispatcher.
     * @param executorService The executor service to execute the event dispatch.
     */
    public AsyncEventDispatcher(
            EventDispatcher decorated, 
            ExecutorService executorService
    ) {
        this(decorated, executorService, (event, exception) -> {});
    }

    /**
     * Constructor.
     * 
     * @param decorated The decorated event dispatcher.
     * @param executorService The executor service to execute the event dispatch.
     * @param exceptionHandler The exception handler to delegate event dispatch
     * exceptions to.
     */
    public AsyncEventDispatcher(
            EventDispatcher decorated,
            ExecutorService executorService,
            ExceptionHandler exceptionHandler
    ) {
        this.decorated = requireNonNull(decorated);
        this.executorService = requireNonNull(executorService);
        this.exceptionHandler = requireNonNull(exceptionHandler);
    }

    /**
     * Asynchronously dispatch event to registered event handlers.
     * 
     * @param <T> The event type.
     * @param event The event to dispatch.
     */
    @Override
    public <T> void send(T event) {
        requireNonNull(event);
        asyncSend(event);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    private <T> void asyncSend(T event) {
        executorService.submit(() -> {
            try {
                decorated.send(event);
            } catch (RuntimeException ex) {
                LOGGER.log(
                    Level.SEVERE, 
                    ex,
                    () -> "Error occurred while asynchronously dispatching event " + 
                        event.getClass() + "."
                );
                exceptionHandler.handle(event, ex);
            }
        });
    }

    /**
     * The exception handler to handle event dispatch exceptions.
     */
    public static interface ExceptionHandler {
        /**
         * Handle event dispatch exception.
         * 
         * @param event The dispatched event.
         * @param exception The event dispatch exception.
         */
        void handle(Object event, Throwable exception);
    }
}
