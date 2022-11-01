package io.github.joeljeremy.deezpatch.core.internal.registries;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventHandler;
import io.github.joeljeremy.deezpatch.core.EventHandlerProvider;
import io.github.joeljeremy.deezpatch.core.EventHandlerRegistry;
import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.RegisteredEventHandler;
import io.github.joeljeremy.deezpatch.core.internal.EventHandlerMethod;
import io.github.joeljeremy.deezpatch.core.internal.Internal;
import io.github.joeljeremy.deezpatch.core.internal.LambdaFactory;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The default event handler registry. */
@Internal
public class DeezpatchEventHandlerRegistry implements EventHandlerRegistry, EventHandlerProvider {

  private final RegisteredEventHandlersByEventType eventHandlersByEventType =
      new RegisteredEventHandlersByEventType();

  private final InstanceProvider instanceProvider;

  /**
   * Constructor.
   *
   * @param instanceProvider The instance provider.
   */
  public DeezpatchEventHandlerRegistry(InstanceProvider instanceProvider) {
    this.instanceProvider = requireNonNull(instanceProvider);
  }

  /** {@inheritDoc} */
  @Override
  public DeezpatchEventHandlerRegistry register(Class<?>... eventHandlerClasses) {
    requireNonNull(eventHandlerClasses);

    for (Class<?> eventHandlerClass : eventHandlerClasses) {
      Method[] methods = eventHandlerClass.getMethods();
      // Register all methods marked with @EventHandler.
      for (Method method : methods) {
        if (!method.isAnnotationPresent(EventHandler.class)) {
          continue;
        }

        validateMethodParameters(method);
        validateReturnType(method);

        register(
            // First parameter in the method is the event object.
            method.getParameterTypes()[0], method);
      }
    }

    return this;
  }

  /** {@inheritDoc} */
  @Override
  public <T extends Event> List<RegisteredEventHandler<T>> getEventHandlersFor(Class<T> eventType) {
    requireNonNull(eventType);

    @SuppressWarnings({"unchecked", "rawtypes"})
    List<RegisteredEventHandler<T>> eventHandlers = (List) eventHandlersByEventType.get(eventType);

    // Internal list is mutable. Always wrap in an unmodifiable list here.
    return Collections.unmodifiableList(eventHandlers);
  }

  private void register(Class<?> eventType, Method eventHandlerMethod) {
    requireNonNull(eventType);
    requireNonNull(eventHandlerMethod);

    List<RegisteredEventHandler<?>> handlers = eventHandlersByEventType.get(eventType);

    handlers.add(buildEventHandler(eventHandlerMethod, instanceProvider));
  }

  private static RegisteredEventHandler<?> buildEventHandler(
      Method eventHandlerMethod, InstanceProvider instanceProvider) {

    requireNonNull(eventHandlerMethod);

    EventHandlerMethod eventHandlerMethodLambda =
        LambdaFactory.createLambdaFunction(eventHandlerMethod, EventHandlerMethod.class);

    final Class<?> eventHandlerClass = eventHandlerMethod.getDeclaringClass();
    final String eventHandlerString = eventHandlerMethod.toGenericString();

    // Only request event handler instance when invoked instead of during registration time.
    return new RegisteredEventHandler<Event>() {
      @Override
      public void invoke(Event event) {
        eventHandlerMethodLambda.invoke(instanceProvider.getInstance(eventHandlerClass), event);
      }

      @Override
      public String toString() {
        return eventHandlerString;
      }
    };
  }

  private static void validateMethodParameters(Method method) {
    if (method.getParameterCount() != 1) {
      throw new IllegalArgumentException(
          "Methods marked with @EventHandler must accept a single parameter which is the event"
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
}
