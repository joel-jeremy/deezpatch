package io.github.joeljeremy.deezpatch.core;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.Deezpatch.Builder.EventConfiguration;
import io.github.joeljeremy.deezpatch.core.Deezpatch.Builder.RequestConfiguration;
import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchEventHandlerRegistry;
import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchRequestHandlerRegistry;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncEventHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncRequestHandlerInvocationStrategy;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/** Checkout Deezpatch! */
public class Deezpatch implements Dispatcher, Publisher {
  private static final Logger LOGGER = System.getLogger(Deezpatch.class.getName());

  private final RequestHandlerProvider requestHandlerProvider;
  private final RequestHandlerInvocationStrategy requestHandlerInvocationStrategy;
  private final EventHandlerProvider eventHandlerProvider;
  private final EventHandlerInvocationStrategy eventHandlerInvocationStrategy;

  /**
   * Constructor.
   *
   * @param requestConfiguration The request configuration.
   * @param eventHandlerProvider The event configuration.
   */
  private Deezpatch(
      RequestConfiguration requestConfiguration, EventConfiguration eventConfiguration) {
    this.requestHandlerProvider = requestConfiguration.requestHandlerRegistry;
    this.requestHandlerInvocationStrategy = requestConfiguration.requestHandlerInvocationStrategy;
    this.eventHandlerProvider = eventConfiguration.eventHandlerRegistry;
    this.eventHandlerInvocationStrategy = eventConfiguration.eventHandlerInvocationStrategy;
  }

  /** {@inheritDoc} */
  @Override
  public <T extends Request<R>, R> Optional<R> send(T request) {
    RequestKey<T, R> requestKey = RequestKey.from(request);

    RegisteredRequestHandler<T, R> requestHandler =
        requestHandlerProvider
            .getRequestHandlerFor(requestKey)
            .orElseThrow(
                () ->
                    new DeezpatchException(
                        "No request handler found for request key: " + requestKey + "."));

    try {
      return requestHandlerInvocationStrategy.invoke(requestHandler, request);
    } catch (Exception ex) {
      LOGGER.log(
          Level.ERROR,
          () ->
              "Exception occurred while dispatching request "
                  + request.getClass().getName()
                  + " to request handler "
                  + requestHandler
                  + ".",
          ex);

      throw ex;
    }
  }

  /** {@inheritDoc} */
  @Override
  public <T extends Event> void publish(T event) {
    @SuppressWarnings("unchecked")
    Class<T> eventType = (Class<T>) event.getClass();

    List<RegisteredEventHandler<T>> eventHandlers =
        eventHandlerProvider.getEventHandlersFor(eventType);

    try {
      eventHandlerInvocationStrategy.invokeAll(eventHandlers, event);
    } catch (Exception ex) {
      LOGGER.log(
          Level.ERROR,
          () ->
              "Exception occurred while publishing event "
                  + event.getClass().getName()
                  + " to event handlers "
                  + eventHandlers
                  + ".",
          ex);

      throw ex;
    }
  }

  /**
   * {@link Deezpatch} builder.
   *
   * @return {@link Deezpatch} builder.
   */
  public static Builder builder() {
    return new Builder();
  }

  /** The builder for {@link Deezpatch}. */
  public static class Builder {
    private final List<Consumer<RequestConfiguration>> requestConfigurers = new ArrayList<>();
    private final List<Consumer<EventConfiguration>> eventConfigurers = new ArrayList<>();
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
     * Register a request handling configurer. Registered configurers will be executed during build
     * time in the order they were registered.
     *
     * @param requestConfigurer Register a request handling configurer. Registered configurers will
     *     be executed during build time in the order they were registered.
     * @return Deez builder.
     */
    public Builder requests(Consumer<RequestConfiguration> requestConfigurer) {
      requireNonNull(requestConfigurer);
      requestConfigurers.add(requestConfigurer);
      return this;
    }

    /**
     * Register a event handling configurer. Registered configurers will be executed during build
     * time in the order they were registered.
     *
     * @param eventConfigurer Register a event handling configurer. Registered configurers will be
     *     executed during build time in the order they were registered.
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
      eventConfigurers.forEach(ec -> ec.accept(eventConfiguration));

      return new Deezpatch(requestConfiguration, eventConfiguration);
    }

    /** Request handling configuration. */
    public static class RequestConfiguration {
      private final DeezpatchRequestHandlerRegistry requestHandlerRegistry;
      private RequestHandlerInvocationStrategy requestHandlerInvocationStrategy =
          new SyncRequestHandlerInvocationStrategy();

      private RequestConfiguration(InstanceProvider instanceProvider) {
        requestHandlerRegistry = new DeezpatchRequestHandlerRegistry(instanceProvider);
      }

      /**
       * Scan class for methods annotated with {@link RequestHandler} and register them as request
       * handlers.
       *
       * @param requestHandlerClasses The classes to scan for {@link RequestHandler} annotations.
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
       * @param requestHandlerInvocationStrategy The request handler invocation strategy to use.
       * @return Deez request configuration.
       */
      public RequestConfiguration invocationStrategy(
          RequestHandlerInvocationStrategy requestHandlerInvocationStrategy) {
        requireNonNull(requestHandlerInvocationStrategy);
        this.requestHandlerInvocationStrategy = requestHandlerInvocationStrategy;
        return this;
      }
    }

    /** Event handling configuration. */
    public static class EventConfiguration {
      private final DeezpatchEventHandlerRegistry eventHandlerRegistry;
      private EventHandlerInvocationStrategy eventHandlerInvocationStrategy =
          new SyncEventHandlerInvocationStrategy();

      private EventConfiguration(InstanceProvider instanceProvider) {
        eventHandlerRegistry = new DeezpatchEventHandlerRegistry(instanceProvider);
      }

      /**
       * Scan class for methods annotated with {@link EventHandler} and register them as event
       * handlers.
       *
       * @param eventHandlerClasses The classes to scan for {@link EventHandler} annotations.
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
       * @param eventHandlerInvocationStrategy The event handler invocation strategy to use.
       * @return Deez event configuration.
       */
      public EventConfiguration invocationStrategy(
          EventHandlerInvocationStrategy eventHandlerInvocationStrategy) {
        requireNonNull(eventHandlerInvocationStrategy);
        this.eventHandlerInvocationStrategy = eventHandlerInvocationStrategy;
        return this;
      }
    }
  }

  /** Determines the strategy to use in executing request handlers. */
  public static interface RequestHandlerInvocationStrategy {
    /**
     * Invoke the request handler.
     *
     * @param <T> The request type.
     * @param <R> The result type.
     * @param requestHandler The registered request handler to invoke.
     * @param request The dispatched request.
     * @return The request result.
     */
    <T extends Request<R>, R> Optional<R> invoke(
        RegisteredRequestHandler<T, R> requestHandler, T request);
  }

  /** Determines the strategy to use in executing event handlers. */
  public static interface EventHandlerInvocationStrategy {
    /**
     * Invoke all the event handlers.
     *
     * @param <T> The event type.
     * @param eventHandlers The registered event handlers to invoke.
     * @param event The published event.
     */
    <T extends Event> void invokeAll(List<RegisteredEventHandler<T>> eventHandlers, T event);
  }
}
