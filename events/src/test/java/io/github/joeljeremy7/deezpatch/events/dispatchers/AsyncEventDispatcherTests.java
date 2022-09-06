package io.github.joeljeremy7.deezpatch.events.dispatchers;

import io.github.joeljeremy7.deezpatch.events.EventHandler;
import io.github.joeljeremy7.deezpatch.events.registries.DefaultEventHandlerRegistry;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEvent;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEventHandlerInstanceProviders;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEventHandlers.CountDownLatchEventHandler;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEventHandlers.TestEventHandler;
import io.github.joeljeremy7.deezpatch.events.testentities.TestEventHandlers.ThrowingEventHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsyncEventDispatcherTests {
    private static final ExecutorService EXECUTOR_SERVICE = 
        Executors.newSingleThreadExecutor();
    
    @Nested
    class Constructors {
        @Test
        @DisplayName("should throw when event handler provider argument is null.")
        public void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> new AsyncEventDispatcher(
                    null, 
                    EXECUTOR_SERVICE
                )
            );
        }

        @Test
        @DisplayName("should throw when executor service argument is null.")
        public void test2() {
            var registry = new DefaultEventHandlerRegistry(
                TestEventHandlerInstanceProviders.of(new TestEventHandler())
            );
            var dispatcher = new DefaultEventDispatcher(registry);

            assertThrows(
                NullPointerException.class, 
                () -> new AsyncEventDispatcher(
                    dispatcher, 
                    null
                )
            );
        }

        @Test
        @DisplayName("should throw when exception handler argument is null.")
        public void test3() {
            var registry = new DefaultEventHandlerRegistry(
                TestEventHandlerInstanceProviders.of(new TestEventHandler())
            );
            var dispatcher = new DefaultEventDispatcher(registry);

            assertThrows(
                NullPointerException.class, 
                () -> new AsyncEventDispatcher(
                    dispatcher, 
                    EXECUTOR_SERVICE,
                    null
                )
            );
        }
    }

    @Nested
    class SendMethod {
        @Test
        @DisplayName("should throw when event argument is null")
        public void test1() {
            AsyncEventDispatcher eventDispatcher = buildEventDispatcher(new TestEventHandler());
            
            assertThrows(
                NullPointerException.class, 
                () -> eventDispatcher.send(null)
            );
        }

        @Test
        @DisplayName("should not throw when no event handler is registered")
        public void test2() {
            AsyncEventDispatcher eventDispatcher = buildEventDispatcher(new TestEventHandler());
            assertDoesNotThrow(() -> eventDispatcher.send(UnregisteredEvent.class));
        }

        @Test
        @DisplayName("should invoke all registered event handlers")
        public void test3() throws InterruptedException {
            int numberOfHandlers = 
                (int)Arrays.stream(CountDownLatchEventHandler.class.getMethods())
                    .filter(m -> m.isAnnotationPresent(EventHandler.class))
                    .count();

            // Use latch since event dispatch is async.
            // If no latch is used, test execution will most likely finish 
            // before the events are dispatched and will fail.
            var latch = new CountDownLatch(numberOfHandlers);
            var countDownLatchEventHandler = new CountDownLatchEventHandler(latch);

            AsyncEventDispatcher eventDispatcher = buildEventDispatcher(
                countDownLatchEventHandler
            );

            var testEvent = new TestEvent();
            eventDispatcher.send(testEvent);

            latch.await(10, TimeUnit.SECONDS);

            assertEquals(numberOfHandlers, countDownLatchEventHandler.handledEvents().size());
            assertTrue(countDownLatchEventHandler.hasHandledEvent(testEvent));
        }

        @Test
        @DisplayName("should not propagate exception thrown by event handler")
        public void test4() {
            var exception = new RuntimeException("Oops!");
            AsyncEventDispatcher eventDispatcher = buildEventDispatcher(
                new ThrowingEventHandler(exception)
            );

            assertDoesNotThrow(() -> eventDispatcher.send(new TestEvent()));
        }
    }


    private <T> AsyncEventDispatcher buildEventDispatcher(
            T eventHandler
    ) {
        var registry = new DefaultEventHandlerRegistry(
                TestEventHandlerInstanceProviders.of(eventHandler)
            )
            .scan(eventHandler.getClass());
        
        return new AsyncEventDispatcher(
            new DefaultEventDispatcher(registry), 
            EXECUTOR_SERVICE
        );
    }

    public static class UnregisteredEvent {}
}
