package io.github.joeljeremy7.deezpatch.core;

import io.github.joeljeremy7.deezpatch.core.invocationstrategies.SyncEventHandlerInvocationStrategy;
import io.github.joeljeremy7.deezpatch.core.invocationstrategies.SyncRequestHandlerInvocationStrategy;
import io.github.joeljeremy7.deezpatch.core.registries.DeezpatchEventHandlerRegistry;
import io.github.joeljeremy7.deezpatch.core.registries.DeezpatchRequestHandlerRegistry;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * Checkout deezpatch!
 */
public class Deezpatch implements Dispatcher, Publisher {
    private static final Logger LOGGER = System.getLogger(Deezpatch.class.getName());

    private final RequestHandlerProvider requestHandlerProvider;
    private final RequestHandlerInvocationStrategy requestHandlerInvocationStrategy;
    private final EventHandlerProvider eventHandlerProvider;
    private final EventHandlerInvocationStrategy eventHandlerInvocationStrategy;

    /**
     * Constructor.
     * 
     * @param requestHandlerProvider The request handler provider.
     * @param eventHandlerProvider The event handler provider.
     */
    private Deezpatch(
            RequestHandlerProvider requestHandlerProvider,
            RequestHandlerInvocationStrategy requestHandlerInvocationStrategy,
            EventHandlerProvider eventHandlerProvider,
            EventHandlerInvocationStrategy eventHandlerInvocationStrategy
    ) {
        this.requestHandlerProvider = requestHandlerProvider;
        this.requestHandlerInvocationStrategy = requestHandlerInvocationStrategy;
        this.eventHandlerProvider = eventHandlerProvider;
        this.eventHandlerInvocationStrategy = eventHandlerInvocationStrategy;
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Request<R>, R> Optional<R> send(T request) {
        RequestKey<T, R> requestType = RequestKey.from(request);
        RegisteredRequestHandler<T, R> requestHandler =
            requestHandlerProvider.getRequestHandlerFor(requestType)
                .orElseThrow(() -> new DeezpatchException(
                    "No request handler found for request type: " + requestType + "."
                ));
        
        try {
            return requestHandlerInvocationStrategy.invoke(requestHandler, request);
        } catch (RuntimeException ex) {
            LOGGER.log(
                Level.ERROR, 
                () -> "Exception occurred while dispatching request " + 
                    request.getClass().getName() + " to request handler " + 
                    requestHandler + ".",
                ex
            );

            throw ex;
        }
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Event> void publish(T event) {
        @SuppressWarnings("unchecked")
        Class<T> eventType = (Class<T>)event.getClass();

        List<RegisteredEventHandler<T>> eventHandlers = 
            eventHandlerProvider.getEventHandlersFor(eventType);
        
        eventHandlers.forEach(eventHandler -> {
            try {
                eventHandlerInvocationStrategy.invoke(eventHandler, event);
            } catch (RuntimeException ex) {
                LOGGER.log(
                    Level.ERROR, 
                    () -> "Exception occurred while dispatching event " + 
                        event.getClass().getName() + " to event handler " + 
                        eventHandler + ".",
                    ex
                );

                throw ex;
            }
        });
    }

    /**
     * {@link Deezpatch} builder.
     * 
     * @return {@link Deezpatch} builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * The builder for {@link Deezpatch}.
     */
    public static class Builder {
        private final List<Consumer<RequestConfiguration>> requestConfigurers =
            new ArrayList<>();
        private final List<Consumer<EventConfiguration>> eventConfigurers =
            new ArrayList<>();
        private InstanceProvider instanceProvider;

        @SuppressWarnings("NullAway.Init")
        private Builder() {}

        /**
         * The instance provider to get handler instances from.
         * 
         * @param instanceProvider The instance provider to get handler instances from.
         * @return Deez builder.
         */
        public Builder instanceProvider(InstanceProvider instanceProvider) {
            this.instanceProvider = requireNonNull(instanceProvider);
            return this;
        }

        /**
         * Register a request handling configurer. Registered configurers will be
         * executed during build time in the order they were registered.
         * 
         * @param requestConfigurer Register a request handling configurer. 
         * Registered configurers will be executed during build time in the order 
         * they were registered.
         * @return Deez builder.
         */
        public Builder requests(Consumer<RequestConfiguration> requestConfigurer) {
            requireNonNull(requestConfigurer);
            requestConfigurers.add(requestConfigurer);
            return this;
        }

        /**
         * Register a event handling configurer. Registered configurers will be
         * executed during build time in the order they were registered.
         * 
         * @param eventConfigurer Register a event handling configurer. 
         * Registered configurers will be executed during build time in the order 
         * they were registered.
         * @return Deez builder.
         */
        public Builder events(Consumer<EventConfiguration> eventConfigurer) {
            requireNonNull(eventConfigurer);
            eventConfigurers.add(eventConfigurer);
            return this;
        }

        /**
         * Build {@link Deezpatch}.
         * 
         * @return {@link Deezpatch}!
         */
        public Deezpatch build() {
            if (instanceProvider == null) {
                throw new IllegalStateException("Instance provider is required.");
            }

            var requestConfiguration = new RequestConfiguration(instanceProvider);
            requestConfigurers.forEach(rc -> rc.accept(requestConfiguration));

            var eventConfiguration = new EventConfiguration(instanceProvider);
            eventConfigurers.forEach(rc -> rc.accept(eventConfiguration));

            return new Deezpatch(
                requestConfiguration.requestHandlerRegistry, 
                requestConfiguration.requestHandlerInvocationStrategy, 
                eventConfiguration.eventHandlerRegistry, 
                eventConfiguration.eventHandlerInvocationStrategy
            );
        }

        /**
         * Request handling configuration.
         */
        public static class RequestConfiguration {
            private final DeezpatchRequestHandlerRegistry requestHandlerRegistry;
            private RequestHandlerInvocationStrategy requestHandlerInvocationStrategy =
                new SyncRequestHandlerInvocationStrategy();

            private RequestConfiguration(InstanceProvider instanceProvider) {
                requestHandlerRegistry = new DeezpatchRequestHandlerRegistry(instanceProvider);
            }

            /**
             * Scan class for methods annotated with {@link RequestHandler} and 
             * register them as request handlers.
             * 
             * @param requestHandlerClasses The classes to scan for {@link RequestHandler}
             * annotations.
             * @return Deez request configuration.
             */
            public RequestConfiguration register(Class<?>... requestHandlerClasses) {
                requireNonNull(requestHandlerClasses);
                requestHandlerRegistry.register(requestHandlerClasses);
                return this;
            }

            /**
             * The request handler invocation strategy to use.
             * 
             * @param requestHandlerInvocationStrategy The request handler invocation 
             * strategy to use.
             * @return Deez request configuration.
             */
            public RequestConfiguration invocationStrategy(
                    RequestHandlerInvocationStrategy requestHandlerInvocationStrategy
            ) {
                requireNonNull(requestHandlerInvocationStrategy);
                this.requestHandlerInvocationStrategy = requestHandlerInvocationStrategy;
                return this;
            }
        }
    
        /**
         * Event handling configuration.
         */
        public static class EventConfiguration {
            private final DeezpatchEventHandlerRegistry eventHandlerRegistry;
            private EventHandlerInvocationStrategy eventHandlerInvocationStrategy =
                new SyncEventHandlerInvocationStrategy();

            private EventConfiguration(InstanceProvider instanceProvider) {
                eventHandlerRegistry = 
                    new DeezpatchEventHandlerRegistry(instanceProvider);
            }

            /**
             * Scan class for methods annotated with {@link EventHandler} and 
             * register them as event handlers.
             * 
             * @param eventHandlerClasses The classes to scan for {@link EventHandler}
             * annotations.
             * @return Deez event configuration.
             */
            public EventConfiguration register(Class<?>... eventHandlerClasses) {
                requireNonNull(eventHandlerClasses);
                eventHandlerRegistry.register(eventHandlerClasses);
                return this;
            }

            /**
             * The event handler invocation strategy to use.
             * 
             * @param eventHandlerInvocationStrategy The event handler invocation 
             * strategy to use.
             * @return Deez event configuration.
             */
            public EventConfiguration invocationStrategy(
                    EventHandlerInvocationStrategy eventHandlerInvocationStrategy
            ) {
                requireNonNull(eventHandlerInvocationStrategy);
                this.eventHandlerInvocationStrategy = eventHandlerInvocationStrategy;
                return this;
            }
        }
    }

    /**
     * Determines the strategy to use in executing request handlers.
     */
    public static interface RequestHandlerInvocationStrategy {
        /**
         * Invoke the request handler.
         * 
         * @param <T> The request type.
         * @param <R> The result type.
         * @param requestHandler The request handler.
         * @param request The dispatched request.
         * @return The request result.
         */
        <T extends Request<R>, R> Optional<R> invoke(
            RegisteredRequestHandler<T, R> requestHandler,
            T request
        );
    }

    /**
     * Determines the strategy to use in executing event handlers.
     */
    public static interface EventHandlerInvocationStrategy {
        /**
         * Invoke the event handler.
         * 
         * @param <T> The event type.
         * @param eventHandler The event handler.
         * @param event The dispatched event.
         */
        <T extends Event> void invoke(
            RegisteredEventHandler<T> eventHandler,
            T event
        );
    }
}
