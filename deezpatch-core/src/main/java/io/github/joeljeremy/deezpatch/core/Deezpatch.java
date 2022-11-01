package io.github.joeljeremy.deezpatch.core;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.Deezpatch.Builder.EventHandlingConfiguration;
import io.github.joeljeremy.deezpatch.core.Deezpatch.Builder.RequestHandlingConfiguration;
import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchEventHandlerRegistry;
import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchRequestHandlerRegistry;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncEventHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncRequestHandlerInvocationStrategy;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
   * @param instanceProvider The instance provider.
   * @param requestConfiguration The request configuration.
   * @param eventConfiguration The event configuration.
   */
  private Deezpatch(
      InstanceProvider instanceProvider,
      RequestHandlingConfiguration requestConfiguration,
      EventHandlingConfiguration eventConfiguration) {
    this.requestHandlerProvider =
        requestConfiguration.buildRequestHandlerProvider(instanceProvider);
    this.requestHandlerInvocationStrategy = requestConfiguration.requestHandlerInvocationStrategy;
    this.eventHandlerProvider = eventConfiguration.buildEventHandlerProvider(instanceProvider);
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
    private final List<RequestHandlingConfigurator> requestConfigurators = new ArrayList<>();
    private final List<EventHandlingConfigurator> eventConfigurators = new ArrayList<>();
    private InstanceProvider instanceProvider;

    @SuppressWarnings("NullAway.Init")
    private Builder() {}

    /**
     * The instance provider to get instances from.
     *
     * @param instanceProvider The instance provider to get instances from.
     * @return Deez builder.
     */
    public Builder instanceProvider(InstanceProvider instanceProvider) {
      this.instanceProvider = requireNonNull(instanceProvider);
      return this;
    }

    /**
     * Register a request handling configurator. Registered configurators will be executed during
     * build time in the order they were registered.
     *
     * @param requestHandlingConfigurator Register a request handling configurator. Registered
     *     configurators will be executed during build time in the order they were registered.
     * @return Deez builder.
     */
    public Builder requests(RequestHandlingConfigurator requestHandlingConfigurator) {
      requireNonNull(requestHandlingConfigurator);
      requestConfigurators.add(requestHandlingConfigurator);
      return this;
    }

    /**
     * Register a event handling configurator. Registered configurators will be executed during
     * build time in the order they were registered.
     *
     * @param eventHandlingConfigurator Register a event handling configurator. Registered
     *     configurators will be executed during build time in the order they were registered.
     * @return Deez builder.
     */
    public Builder events(EventHandlingConfigurator eventHandlingConfigurator) {
      requireNonNull(eventHandlingConfigurator);
      eventConfigurators.add(eventHandlingConfigurator);
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

      var requestHandlingConfiguration = new RequestHandlingConfiguration();
      requestConfigurators.forEach(rhc -> rhc.configure(requestHandlingConfiguration));

      var eventHandlingConfiguration = new EventHandlingConfiguration();
      eventConfigurators.forEach(ehc -> ehc.configure(eventHandlingConfiguration));

      return new Deezpatch(
          instanceProvider, requestHandlingConfiguration, eventHandlingConfiguration);
    }

    private static <T> void requireNonNullElements(Collection<T> collection) {
      requireNonNull(collection);
      for (T element : collection) {
        requireNonNull(element);
      }
    }

    private static <T> void requireNonNullElements(T[] array) {
      requireNonNull(array);
      for (T element : array) {
        requireNonNull(element);
      }
    }

    /** Request handling configuration. */
    public static final class RequestHandlingConfiguration {
      private final Set<Class<?>> requestHandlerClasses = new HashSet<>();
      private final Set<Class<? extends Annotation>> requestHandlerAnnotations = new HashSet<>();
      private RequestHandlerInvocationStrategy requestHandlerInvocationStrategy =
          new SyncRequestHandlerInvocationStrategy();

      /**
       * Register supported request handler annotations. Methods annotated with any of these
       * annotations will be treated as request handlers. The {@link RequestHandler} annotation is
       * supported by default.
       *
       * @param requestHandlerAnnotations The request handler annotations to support.
       * @return Deez request configuration.
       */
      @SafeVarargs
      public final RequestHandlingConfiguration handlerAnnotations(
          Class<? extends Annotation>... requestHandlerAnnotations) {
        requireNonNullElements(requestHandlerAnnotations);
        Collections.addAll(this.requestHandlerAnnotations, requestHandlerAnnotations);
        return this;
      }

      /**
       * Register supported request handler annotations. Methods annotated with any of these
       * annotations will be treated as request handlers. The {@link RequestHandler} annotation is
       * supported by default.
       *
       * @param requestHandlerAnnotations The request handler annotations to support.
       * @return Deez request configuration.
       */
      public final RequestHandlingConfiguration handlerAnnotations(
          Collection<Class<? extends Annotation>> requestHandlerAnnotations) {
        requireNonNullElements(requestHandlerAnnotations);
        this.requestHandlerAnnotations.addAll(requestHandlerAnnotations);
        return this;
      }

      /**
       * Scan class for methods annotated with supported request handler annotations and register
       * them as request handlers.
       *
       * @param requestHandlerClasses The classes to scan for supported request handler annotations.
       * @return Deez request configuration.
       */
      public final RequestHandlingConfiguration handlers(Class<?>... requestHandlerClasses) {
        requireNonNullElements(requestHandlerClasses);
        Collections.addAll(this.requestHandlerClasses, requestHandlerClasses);
        return this;
      }

      /**
       * Scan class for methods annotated with supported request handler annotations and register
       * them as request handlers.
       *
       * @param requestHandlerClasses The classes to scan for supported request handler annotations.
       * @return Deez request configuration.
       */
      public final RequestHandlingConfiguration handlers(
          Collection<Class<?>> requestHandlerClasses) {
        requireNonNullElements(requestHandlerClasses);
        this.requestHandlerClasses.addAll(requestHandlerClasses);
        return this;
      }

      /**
       * The request handler invocation strategy to use.
       *
       * @param requestHandlerInvocationStrategy The request handler invocation strategy to use.
       * @return Deez request configuration.
       */
      public final RequestHandlingConfiguration invocationStrategy(
          RequestHandlerInvocationStrategy requestHandlerInvocationStrategy) {
        requireNonNull(requestHandlerInvocationStrategy);
        this.requestHandlerInvocationStrategy = requestHandlerInvocationStrategy;
        return this;
      }

      private RequestHandlerProvider buildRequestHandlerProvider(
          InstanceProvider instanceProvider) {
        var requestHandlerRegistry =
            new DeezpatchRequestHandlerRegistry(instanceProvider, requestHandlerAnnotations);
        return requestHandlerRegistry.register(requestHandlerClasses.toArray(Class<?>[]::new));
      }
    }

    /** Event handling configuration. */
    public static final class EventHandlingConfiguration {
      private final Set<Class<?>> eventHandlerClasses = new HashSet<>();
      private final Set<Class<? extends Annotation>> eventHandlerAnnotations = new HashSet<>();
      private EventHandlerInvocationStrategy eventHandlerInvocationStrategy =
          new SyncEventHandlerInvocationStrategy();

      /**
       * Register supported event handler annotations. Methods annotated with these annotations will
       * be treated as event handlers. The {@link EventHandler} annotation is supported by default.
       *
       * @param eventHandlerAnnotations The event handler annotations to support. The {@link
       *     EventHandler} annotation is supported by default.
       * @return Deez request configuration.
       */
      @SafeVarargs
      public final EventHandlingConfiguration handlerAnnotations(
          Class<? extends Annotation>... eventHandlerAnnotations) {
        requireNonNullElements(eventHandlerAnnotations);
        Collections.addAll(this.eventHandlerAnnotations, eventHandlerAnnotations);
        return this;
      }

      /**
       * Register supported event handler annotations. Methods annotated with these annotations will
       * be treated as event handlers. The {@link EventHandler} annotation is supported by default.
       *
       * @param eventHandlerAnnotations The event handler annotations to support. The {@link
       *     EventHandler} annotation is supported by default.
       * @return Deez request configuration.
       */
      public final EventHandlingConfiguration handlerAnnotations(
          Collection<Class<? extends Annotation>> eventHandlerAnnotations) {
        requireNonNullElements(eventHandlerAnnotations);
        this.eventHandlerAnnotations.addAll(eventHandlerAnnotations);
        return this;
      }

      /**
       * Scan class for methods annotated with supported event handler annotations and register them
       * as event handlers.
       *
       * @param eventHandlerClasses The classes to scan for supported event handler annotations.
       * @return Deez event configuration.
       */
      public final EventHandlingConfiguration handlers(Class<?>... eventHandlerClasses) {
        requireNonNullElements(eventHandlerClasses);
        Collections.addAll(this.eventHandlerClasses, eventHandlerClasses);
        return this;
      }

      /**
       * Scan class for methods annotated with supported event handler annotations and register them
       * as event handlers.
       *
       * @param eventHandlerClasses The classes to scan for supported event handler annotations.
       * @return Deez event configuration.
       */
      public final EventHandlingConfiguration handlers(Collection<Class<?>> eventHandlerClasses) {
        requireNonNullElements(eventHandlerClasses);
        this.eventHandlerClasses.addAll(eventHandlerClasses);
        return this;
      }

      /**
       * The event handler invocation strategy to use.
       *
       * @param eventHandlerInvocationStrategy The event handler invocation strategy to use.
       * @return Deez event configuration.
       */
      public final EventHandlingConfiguration invocationStrategy(
          EventHandlerInvocationStrategy eventHandlerInvocationStrategy) {
        requireNonNull(eventHandlerInvocationStrategy);
        this.eventHandlerInvocationStrategy = eventHandlerInvocationStrategy;
        return this;
      }

      private EventHandlerProvider buildEventHandlerProvider(InstanceProvider instanceProvider) {
        var eventHandlerRegistry =
            new DeezpatchEventHandlerRegistry(instanceProvider, eventHandlerAnnotations);
        return eventHandlerRegistry.register(eventHandlerClasses.toArray(Class<?>[]::new));
      }
    }
  }

  /** Request handling configurator. */
  public static interface RequestHandlingConfigurator {
    /**
     * Configure request handling.
     *
     * @param config The request handling configuration.
     */
    void configure(RequestHandlingConfiguration config);
  }

  /** Event handling configurator. */
  public static interface EventHandlingConfigurator {
    /**
     * Configure event handling.
     *
     * @param config The event handling configuration.
     */
    void configure(EventHandlingConfiguration config);
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
