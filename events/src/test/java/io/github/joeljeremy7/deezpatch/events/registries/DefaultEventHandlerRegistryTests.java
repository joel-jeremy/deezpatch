package io.github.joeljeremy7.deezpatch.events.registries;

import io.github.joeljeremy7.deezpatch.events.RegisteredEventHandler;
import io.github.joeljeremy7.deezpatch.events.testentities.InvalidEventHandler;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEvent;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEventHandlerInstanceProviders;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEventHandlers.TestEventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultEventHandlerRegistryTests {
    @Nested
    class Constructors {
        @Test
        @DisplayName(
            "should throw when event handler builder argument is null"
        )
        public void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> new DefaultEventHandlerRegistry(null)
            );
        }
    }

    @Nested
    class ScanMethod {
        @Test
        @DisplayName(
            "should throw when event handler class argument is null"
        )
        public void test1() {
            DefaultEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(new TestEventHandler());
            
            assertThrows(
                NullPointerException.class, 
                () -> eventHandlerRegistry.scan(null)
            );
        }

        @Test
        @DisplayName(
            "should detect and register methods annotated with @EventHandler"
        )
        public void test2() {
            DefaultEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(new TestEventHandler());

            List<RegisteredEventHandler<TestEvent>> eventHandlers = 
                eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

            assertNotNull(eventHandlers);
            assertEquals(2, eventHandlers.size());
        }

        @Test
        @DisplayName(
            "should throw when a method annotated with @EventHandler does not have a parameter"
        )
        public void test3() {
            DefaultEventHandlerRegistry eventHandlerRegistry = 
                new DefaultEventHandlerRegistry(
                    TestEventHandlerInstanceProviders.of(new InvalidEventHandler())
                );
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> eventHandlerRegistry.scan(InvalidEventHandler.class)
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
            DefaultEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(new TestEventHandler());
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
            var testEventHandler = new TestEventHandler();
            DefaultEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(testEventHandler);

            List<RegisteredEventHandler<TestEvent>> resolved = 
                eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

            assertNotNull(resolved);
            assertFalse(resolved.isEmpty());

            var testEvent = new TestEvent();
            resolved.forEach(h -> h.invoke(testEvent));

            assertTrue(testEventHandler.hasHandledEvent(testEvent));
        }

        @Test
        @DisplayName(
            "should return empty list when there is no registered event handler for event type"
        )
        public void test3() {
            DefaultEventHandlerRegistry eventHandlerRegistry = 
                buildEventHandlerRegistry(new TestEventHandler());

            // No registrations...

            List<RegisteredEventHandler<EventWithNoHandlers>> resolved = 
                eventHandlerRegistry.getEventHandlersFor(EventWithNoHandlers.class);

            assertNotNull(resolved);
            assertTrue(resolved.isEmpty());
        }
    }

    private <T> DefaultEventHandlerRegistry buildEventHandlerRegistry(
            T eventHandler
    ) {
        return new DefaultEventHandlerRegistry(
                TestEventHandlerInstanceProviders.of(eventHandler)
            )
            .scan(eventHandler.getClass());
    }

    public static class EventWithNoHandlers {}
}
