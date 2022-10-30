package io.github.joeljeremy.deezpatch.core.internal.registries;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.EventSource;
import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.Publisher;
import io.github.joeljeremy.deezpatch.core.RegisteredEventSource;
import io.github.joeljeremy.deezpatch.core.internal.DaemonThreadFactory;
import io.github.joeljeremy.deezpatch.core.internal.EventSourceMethod;
import io.github.joeljeremy.deezpatch.core.internal.Internal;
import io.github.joeljeremy.deezpatch.core.internal.LambdaFactory;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** The event source registry. */
@Internal
public class EventSourceRegistry {
  private final List<RegisteredEventSource> eventSources = new ArrayList<>();
  private final InstanceProvider instanceProvider;

  /**
   * Constructor.
   *
   * @param instanceProvider The instance provider.
   */
  public EventSourceRegistry(InstanceProvider instanceProvider) {
    this.instanceProvider = requireNonNull(instanceProvider);
  }

  /**
   * Scan class for methods annotated with {@link EventSource} and register them as event sources.
   *
   * @param eventSourceClasses The classes to scan for {@link EventSource} annotations.
   * @return Deez registry.
   */
  public EventSourceRegistry register(Class<?>... eventSourceClasses) {
    requireNonNull(eventSourceClasses);

    for (Class<?> eventSourceClass : eventSourceClasses) {
      Method[] methods = eventSourceClass.getMethods();
      for (Method method : methods) {
        if (!method.isAnnotationPresent(EventSource.class)) {
          continue;
        }

        validateParameters(method);
        validateReturnType(method);

        register(method);
      }
    }

    return this;
  }

  /**
   * Get all registered event sources.
   *
   * @return The registered event sources.
   */
  public List<RegisteredEventSource> getEventSources() {
    // Internal list is mutable. Always wrap in an unmodifiable list here.
    return Collections.unmodifiableList(eventSources);
  }

  private void register(Method eventSourceMethod) {
    eventSources.add(buildEventSource(eventSourceMethod, instanceProvider));
  }

  private static RegisteredEventSource buildEventSource(
      Method eventSourceMethod, InstanceProvider instanceProvider) {

    EventSourceMethod eventSourceMethodLambda =
        LambdaFactory.createLambdaFunction(eventSourceMethod, EventSourceMethod.class);
    ExecutorService executor = newSingleThreadExecutor();
    String eventSourceMethodString = eventSourceMethod.toGenericString();

    return new RegisteredDeezpatchEventSource(
        instanceProvider,
        eventSourceMethod.getDeclaringClass(),
        eventSourceMethodLambda,
        executor,
        eventSourceMethodString);
  }

  private static ExecutorService newSingleThreadExecutor() {
    return shutdownOnJvmShutdown(Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE));
  }

  private static ExecutorService shutdownOnJvmShutdown(ExecutorService executorService) {
    Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdownNow));
    return executorService;
  }

  private static void validateParameters(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1 || !Publisher.class.isAssignableFrom(parameterTypes[0])) {
      throw new IllegalArgumentException(
          "Methods marked with @EventSource must accept a single parameter which is the Publisher"
              + " instance.");
    }
  }

  private static void validateReturnType(Method method) {
    if (!void.class.equals(method.getReturnType())) {
      throw new IllegalArgumentException(
          "Methods marked with @EventSource must return a type that is a subclass/subinterface "
              + "of Event.");
    }
  }

  private static class RegisteredDeezpatchEventSource implements RegisteredEventSource {
    private static final Logger LOGGER =
        System.getLogger(RegisteredDeezpatchEventSource.class.getName());

    private final Object eventSourceInstance;
    private final EventSourceMethod eventSourceMethodLambda;
    private final ExecutorService executorService;
    private final String eventSourceMethodString;

    public RegisteredDeezpatchEventSource(
        InstanceProvider instanceProvider,
        Class<?> eventSourceClass,
        EventSourceMethod eventSourceMethodLambda,
        ExecutorService executorService,
        String eventSourceMethodString) {
      this.eventSourceInstance = instanceProvider.getInstance(eventSourceClass);
      this.eventSourceMethodLambda = eventSourceMethodLambda;
      this.executorService = executorService;
      this.eventSourceMethodString = eventSourceMethodString;
    }

    @Override
    public void start(Publisher publisher) {
      executorService.execute(
          () -> {
            try {
              eventSourceMethodLambda.invoke(eventSourceInstance, publisher);
            } catch (Throwable ex) {
              LOGGER.log(
                  Level.ERROR,
                  "Registered event source \"{0}\" has thrown an exception.",
                  eventSourceMethodString);
            }
          });
    }

    @Override
    public String toString() {
      return eventSourceMethodString;
    }
  }
}
