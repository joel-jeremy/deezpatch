package io.github.joeljeremy.deezpatch.core.internal.registries;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventHandler;
import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.RegisteredEventHandler;
import io.github.joeljeremy.deezpatch.core.internal.EventHandlerMethod;
import io.github.joeljeremy.deezpatch.core.internal.Internal;
import io.github.joeljeremy.deezpatch.core.internal.LambdaFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The event handler registry. */
@Internal
public class EventHandlerRegistry {

  private final RegisteredEventHandlersByEventType eventHandlersByEventType =
      new RegisteredEventHandlersByEventType();

  private final InstanceProvider instanceProvider;

  /**
   * Constructor.
   *
   * @param instanceProvider The instance provider.
   */
  public EventHandlerRegistry(InstanceProvider instanceProvider) {
    this.instanceProvider = requireNonNull(instanceProvider);
  }

  /**
   * Scan class for methods annotated with {@link EventHandler} and register them as event handlers.
   *
   * @param eventHandlerClasses The classes to scan for {@link EventHandler} annotations.
   * @return Deez registry.
   */
  public EventHandlerRegistry register(Class<?>... eventHandlerClasses) {
    requireNonNull(eventHandlerClasses);

    for (Class<?> eventHandlerClass : eventHandlerClasses) {
      Method[] methods = eventHandlerClass.getMethods();
      // Register all methods marked with @EventHandler.
      for (Method method : methods) {
        if (!method.isAnnotationPresent(EventHandler.class)) {
          continue;
        }

        validateParameters(method);
        validateReturnType(method);

        // First parameter in the method is the event object.
        register(method.getParameterTypes()[0], method);
      }
    }

    return this;
  }

  /**
   * Get event handlers for the specified event.
   *
   * @param <T> The event type.
   * @param eventType The event type.
   * @return The list of event handlers, if any are registered. Otherwise, an empty {@code List}.
   */
  public <T> List<RegisteredEventHandler<T>> getEventHandlersFor(Class<T> eventType) {
    requireNonNull(eventType);

    @SuppressWarnings({"unchecked", "rawtypes"})
    List<RegisteredEventHandler<T>> eventHandlers = (List) eventHandlersByEventType.get(eventType);

    // Internal list is mutable. Always wrap in an unmodifiable list here.
    return Collections.unmodifiableList(eventHandlers);
  }

  private void register(Class<?> eventType, Method eventHandlerMethod) {
    List<RegisteredEventHandler<?>> handlers = eventHandlersByEventType.get(eventType);

    handlers.add(buildEventHandler(eventHandlerMethod, instanceProvider));
  }

  private static RegisteredEventHandler<?> buildEventHandler(
      Method eventHandlerMethod, InstanceProvider instanceProvider) {

    EventHandlerMethod eventHandlerMethodLambda =
        LambdaFactory.createLambdaFunction(eventHandlerMethod, EventHandlerMethod.class);

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredDeezpatchEventHandler<>(
        instanceProvider,
        eventHandlerMethod.getDeclaringClass(),
        eventHandlerMethodLambda,
        eventHandlerMethod.toGenericString());
  }

  private static void validateParameters(Method method) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    if (parameterTypes.length != 1 || !Event.class.isAssignableFrom(parameterTypes[0])) {
      throw new IllegalArgumentException(
          "Methods marked with @EventHandler must accept a single parameter which is the Event"
              + " object.");
    }
  }

  private void validateReturnType(Method method) {
    if (!void.class.equals(method.getReturnType())) {
      throw new IllegalArgumentException(
          "Methods marked with @EventHandler must have a void return type.");
    }
  }

  private static class RegisteredEventHandlersByEventType
      extends ClassValue<List<RegisteredEventHandler<?>>> {
    @Override
    protected List<RegisteredEventHandler<?>> computeValue(Class<?> eventType) {
      return new ArrayList<>();
    }
  }

  private static class RegisteredDeezpatchEventHandler<T extends Event>
      implements RegisteredEventHandler<T> {

    private final InstanceProvider instanceProvider;
    private final Class<?> eventHandlerClass;
    private final EventHandlerMethod eventHandlerMethodLambda;
    private final String eventHandlerMethodString;

    public RegisteredDeezpatchEventHandler(
        InstanceProvider instanceProvider,
        Class<?> eventHandlerClass,
        EventHandlerMethod eventHandlerMethodLambda,
        String eventHandlerMethodString) {
      this.instanceProvider = instanceProvider;
      this.eventHandlerClass = eventHandlerClass;
      this.eventHandlerMethodLambda = eventHandlerMethodLambda;
      this.eventHandlerMethodString = eventHandlerMethodString;
    }

    @Override
    public void invoke(T event) {
      eventHandlerMethodLambda.invoke(instanceProvider.getInstance(eventHandlerClass), event);
    }

    @Override
    public String toString() {
      return eventHandlerMethodString;
    }
  }
}
