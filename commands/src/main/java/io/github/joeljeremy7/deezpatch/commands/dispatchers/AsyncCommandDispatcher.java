package io.github.joeljeremy7.deezpatch.commands.dispatchers;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.joeljeremy7.deezpatch.commands.CommandDispatcher;

import java.util.concurrent.ExecutorService;

import static java.util.Objects.requireNonNull;

/**
 * An async implementation of {@link CommandDispatcher}.
 */
public class AsyncCommandDispatcher implements CommandDispatcher {
    private static final Logger LOGGER = 
        Logger.getLogger(AsyncCommandDispatcher.class.getName());

    private final CommandDispatcher decorated;
    private final ExecutorService executorService;
    private final ExceptionHandler exceptionHandler;

    /**
     * Constructor.
     * 
     * @param decorated The decorated command dispatcher.
     * @param executorService The executor service to execute the command dispatch.
     */
    public AsyncCommandDispatcher(
            CommandDispatcher decorated,
            ExecutorService executorService
    ) {
        this(decorated, executorService, (command, ex) -> {});
    }

    /**
     * Constructor.
     * 
     * @param decorated The decorated command dispatcher.
     * @param executorService The executor service to execute the command dispatch.
     * @param exceptionHandler The exception handler to delegate command dispatch
     * exceptions to.
     */
    public AsyncCommandDispatcher(
            CommandDispatcher decorated,
            ExecutorService executorService,
            ExceptionHandler exceptionHandler
    ) {
        this.decorated = requireNonNull(decorated);
        this.executorService = requireNonNull(executorService);
        this.exceptionHandler = requireNonNull(exceptionHandler);
    }

    /**
     * Asynchronously dispatch command to registered command handlers.
     * 
     * @param <T> The command type.
     * @param command The command to dispatch.
     */
    @Override
    public <T> void send(T command) {
        requireNonNull(command);
        asyncSend(command);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    private <T> void asyncSend(T command) {
        executorService.submit(() -> {
            try {
                decorated.send(command);
            } catch (RuntimeException ex) {
                LOGGER.log(
                    Level.SEVERE, 
                    ex,
                    () -> "Error occurred while asynchronously dispatching command " + 
                        command.getClass() + "."
                );
                exceptionHandler.handle(command, ex);
            }
        });
    }

    /**
     * The exception handler to handle command dispatch exceptions.
     */
    public static interface ExceptionHandler {
        /**
         * Handle command dispatch exception.
         * 
         * @param command The dispatched command.
         * @param exception The command dispatch exception.
         */
        void handle(Object command, Throwable exception);
    }
}
