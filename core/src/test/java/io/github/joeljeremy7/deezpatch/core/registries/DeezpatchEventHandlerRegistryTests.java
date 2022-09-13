package io.github.joeljeremy7.deezpatch.core.registries;

import io.github.joeljeremy7.deezpatch.core.EventHandler;
import io.github.joeljeremy7.deezpatch.core.RegisteredEventHandler;
import io.github.joeljeremy7.deezpatch.core.testentities.TrackableHandler;
import io.github.joeljeremy7.deezpatch.core.testentities.TestEvent;
import io.github.joeljeremy7.deezpatch.core.testentities.TestInstanceProviders;
import io.github.joeljeremy7.deezpatch.core.testentities.TestEventHandlers;
import io.github.joeljeremy7.deezpatch.core.testentities.TestEventHandlers.InvalidEventHandler;
import io.github.joeljeremy7.deezpatch.core.testentities.TestEventHandlers.InvalidReturnTypeEventHandler;
import io.github.joeljeremy7.deezpatch.core.testentities.TestEventHandlers.TestEventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeezpatchEventHandlerRegistryTests {
    @Nested
    class Constructors {
        @Test
        @DisplayName(
            "should throw when event handler builder argument is null"
        )
        public void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> new DeezpatchEventHandlerRegistry(null)
            );
        }
    }

    @Nested
    class RegisterMethod {
        @Test
        @DisplayName(
            "should throw when event handler class argument is null"
        )
        public void test1() {
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(TestEventHandlers.testEventHandler());
            
            assertThrows(
                NullPointerException.class, 
                () -> eventHandlerRegistry.register((Class<?>[])null)
            );
        }

        @Test
        @DisplayName(
            "should detect and register methods annotated with @EventHandler"
        )
        public void test2() {
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(TestEventHandlers.testEventHandler());

            eventHandlerRegistry.register(TestEventHandler.class);

            List<RegisteredEventHandler<TestEvent>> eventHandlers = 
                eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

            assertNotNull(eventHandlers);

            int numberOfEventHandlers =
                (int)Arrays.stream(TestEventHandler.class.getMethods())
                    .filter(m -> m.isAnnotationPresent(EventHandler.class))
                    .count();

            assertEquals(numberOfEventHandlers, eventHandlers.size());
        }

        @Test
        @DisplayName(
            "should throw when a method annotated with @EventHandler does not have a parameter"
        )
        public void test3() {
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(TestEventHandlers.invalidEventHandler());
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> eventHandlerRegistry.register(InvalidEventHandler.class)
            );
        }

        @Test
        @DisplayName(
            "should ignore method with correct method signature " +
            "but not annotated with @EventHandler"
        )
        void test4() {
            var eventHandler = new TrackableHandler() {
                @SuppressWarnings("unused")
                public void handle(TestEvent event) {
                    track(event);
                }
            };

            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(eventHandler);

            eventHandlerRegistry.register(eventHandler.getClass());
            
            assertTrue(
                eventHandlerRegistry.getEventHandlersFor(TestEvent.class).isEmpty()
            );
        }

        @Test
        @DisplayName(
            "should throw when a method annotated with @EventHandler does not return void"
        )
        public void test5() {
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(TestEventHandlers.invalidReturnTypeEventHandler());
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> eventHandlerRegistry
                    .register(InvalidReturnTypeEventHandler.class)
            );
        }
    }

    @Nested
    class GetEventHandlersForMethod {
        @Test
        @DisplayName(
            "should throw when event type argument is null"
        )
        public void test1() {
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(TestEventHandlers.testEventHandler())
                    .register(TestEventHandler.class);
            
            assertThrows(
                NullPointerException.class, 
                () -> eventHandlerRegistry.getEventHandlersFor(null)
            );
        }

        @Test
        @DisplayName(
            "should return registered event handler for event type"
        )
        public void test2() {
            var testEventHandler = TestEventHandlers.testEventHandler();
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(testEventHandler)
                    .register(testEventHandler.getClass());

            List<RegisteredEventHandler<TestEvent>> resolved = 
                eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

            assertNotNull(resolved);
            assertFalse(resolved.isEmpty());

            var testEvent = new TestEvent("Test");
            resolved.forEach(h -> h.invoke(testEvent));

            assertTrue(testEventHandler.hasHandled(testEvent));
        }

        @Test
        @DisplayName(
            "should return empty list when there is no registered event handler for event type"
        )
        public void test3() {
            DeezpatchEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(TestEventHandlers.testEventHandler());

            // No registrations...

            List<RegisteredEventHandler<EventWithNoHandlers>> resolved = 
                eventHandlerRegistry.getEventHandlersFor(EventWithNoHandlers.class);

            assertNotNull(resolved);
            assertTrue(resolved.isEmpty());
        }
    }

    private DeezpatchEventHandlerRegistry buildEventHandlerRegistry(
            Object... eventHandlers
    ) {
        return new DeezpatchEventHandlerRegistry(
            TestInstanceProviders.of(eventHandlers)
        );
    }

    public static class EventWithNoHandlers {}
}
