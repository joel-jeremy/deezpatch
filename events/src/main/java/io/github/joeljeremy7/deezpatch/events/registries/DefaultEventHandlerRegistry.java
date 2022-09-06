package io.github.joeljeremy7.deezpatch.events.registries;

import io.github.joeljeremy7.deezpatch.events.EventHandler;
import io.github.joeljeremy7.deezpatch.events.EventHandlerInstanceProvider;
import io.github.joeljeremy7.deezpatch.events.EventHandlerProvider;
import io.github.joeljeremy7.deezpatch.events.EventHandlerRegistry;
import io.github.joeljeremy7.deezpatch.events.RegisteredEventHandler;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

/**
 * The default command handler registry.
 */
public class DefaultEventHandlerRegistry 
        implements EventHandlerRegistry, EventHandlerProvider {

    private final RegisteredEventHandlersByEventType eventHandlersByEventType =
        new RegisteredEventHandlersByEventType();

    private final EventHandlerInstanceProvider eventHandlerInstanceProvider;

    /**
     * Constructor.
     * 
     * @param eventHandlerInstanceProvider The event handler instance provider.
     */
    public DefaultEventHandlerRegistry(
            EventHandlerInstanceProvider eventHandlerInstanceProvider
    ) {
        this.eventHandlerInstanceProvider = requireNonNull(eventHandlerInstanceProvider);
    }

    /** {@inheritDoc} */
    @Override
    public DefaultEventHandlerRegistry scan(Class<?> eventHandlerClass) {
        requireNonNull(eventHandlerClass);

        // Add all beans with methods marked with @EventHandler to map.
        Arrays.stream(eventHandlerClass.getMethods())
            .filter(method -> 
                method.isAnnotationPresent(EventHandler.class) &&
                // Has atleast 1 parameter. First parameter must be the event.
                hasAtLeastOneParameter(method)
            )
            .forEach(eventHandlerMethod -> register(
                // First parameter in the method is the event object.
                eventHandlerMethod.getParameterTypes()[0], 
                eventHandlerMethod
            ));

        return this;
    }

    /** {@inheritDoc} */
    @Override
    public <T> List<RegisteredEventHandler<T>> getEventHandlersFor(Class<T> eventType) {
        requireNonNull(eventType);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<RegisteredEventHandler<T>> eventHandlers = 
            (List)eventHandlersByEventType.get(eventType);
        
        // Map's list is mutable. Always return an immutable copy here.
        return List.copyOf(eventHandlers);
    }
    
    private void register(Class<?> eventType, Method... eventHandlerMethods) {
        requireNonNull(eventType);
        requireNonNull(eventHandlerMethods);

        List<RegisteredEventHandler<?>> handlers = 
            eventHandlersByEventType.get(eventType);
        
        RegisteredEventHandler<?>[] builtHandlers = Arrays.stream(eventHandlerMethods)
            .map(eventHandlerMethod -> buildEventHandler(
                eventHandlerMethod,
                eventHandlerInstanceProvider
            ))
            .toArray(RegisteredEventHandler[]::new);
        
        Collections.addAll(handlers, builtHandlers);
    }

    private static RegisteredEventHandler<?> buildEventHandler(
            Method eventHandlerMethod,
            EventHandlerInstanceProvider eventHandlerInstanceProvider
    ) {
        requireNonNull(eventHandlerMethod);
        
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle eventHandlerMethodHandle = lookup
                .in(eventHandlerMethod.getDeclaringClass())
                .unreflect(eventHandlerMethod);

            MethodType methodType = eventHandlerMethodHandle.type();
            Class<?> eventHandlerClass = methodType.parameterType(0);
            Class<?> eventParameterType = methodType.parameterType(1);

            CallSite callSite = LambdaMetafactory.metafactory(
                lookup, 
                "invoke", 
                MethodType.methodType(EventHandlerMethod.class), 
                MethodType.methodType(void.class, Object.class, Object.class),
                eventHandlerMethodHandle,
                MethodType.methodType(void.class, eventHandlerClass, eventParameterType)
            );

            EventHandlerMethod eventHandlerMethodLambda = 
                (EventHandlerMethod)callSite.getTarget().invoke();

            // Only request event handler instance when invoked instead of during registration time.
            return new RegisteredEventHandler<Object>() {
                @Override
                public void invoke(Object event) {
                    eventHandlerMethodLambda.invoke(
                        eventHandlerInstanceProvider.getInstance(eventHandlerClass), 
                        event
                    );
                }

                @Override
                public String toString() {
                    return eventHandlerMethod.toGenericString();
                }
            };
        } catch (Throwable e) {
            throw new IllegalStateException(
                "Failed to build event handler for method: " + eventHandlerMethod.toGenericString(),
                e
            );
        }
    }

    private static boolean hasAtLeastOneParameter(Method method) {
        if (method.getParameterCount() < 1) {
            throw new IllegalArgumentException(
                "Methods marked with @EventHandler must accept a single parameter which is the event object."
            );
        }
        return true;
    }

    /**
     * Used in building lambdas via {@link LambdaMetafactory}.
     */
    private static interface EventHandlerMethod {
        /**
         * Invoke the actual method annotated with {@link EventHandler}.
         * 
         * @param eventHandlerInstance The event handler instance.
         * @param event The dispatched event.
         */
        void invoke(Object eventHandlerInstance, Object event);
    }

    private static class RegisteredEventHandlersByEventType 
            extends ClassValue<List<RegisteredEventHandler<?>>> {
        @Override
        protected List<RegisteredEventHandler<?>> computeValue(Class<?> eventType) {
            return new CopyOnWriteArrayList<>();
        }
    }
}
