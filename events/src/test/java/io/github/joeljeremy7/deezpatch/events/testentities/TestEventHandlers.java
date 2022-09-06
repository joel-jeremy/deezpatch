package io.github.joeljeremy7.deezpatch.events.testentities;

import io.github.joeljeremy7.deezpatch.events.EventHandler;

import java.util.concurrent.CountDownLatch;

import static java.util.Objects.requireNonNull;

public class TestEventHandlers {
    private TestEventHandlers() {}

    public static TestEventHandler testEventHandler() {
        return new TestEventHandler();
    }

    public static ThrowingEventHandler throwingEventHandler(
            RuntimeException toThrow
    ) {
        return new ThrowingEventHandler(toThrow);
    }

    public static InvalidEventHandler invalidEventHandler() {
        return new InvalidEventHandler();
    }

    public static CountDownLatchEventHandler countDownLatchEventHandler(
            CountDownLatch countDownLatch
    ) {
        return new CountDownLatchEventHandler(countDownLatch);
    }

    public static class TestEventHandler extends EventTracker {
        @EventHandler
        public void handle(TestEvent event) {
            requireNonNull(event);
            track(event);
        }
    
        @EventHandler
        public void handle2(TestEvent event) {
            requireNonNull(event);
            track(event);
        }
    }

    public static class ThrowingEventHandler {
        private final RuntimeException toThrow;
    
        public ThrowingEventHandler(RuntimeException toThrow) {
            this.toThrow = toThrow;
        }
    
        @EventHandler
        public void handle(TestEvent event) {
            throw toThrow;
        }
    }
    
    public static class InvalidEventHandler {
        @EventHandler
        public void handle() {
            // Invalid.
        }
    }
    
    public static class CountDownLatchEventHandler extends EventTracker {
        private final CountDownLatch countDownLatch;
    
        public CountDownLatchEventHandler(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
    
        @EventHandler
        public void handle(TestEvent event) {
            requireNonNull(event);
            track(event);
            countDownLatch.countDown();
        }
    
        @EventHandler
        public void handle2(TestEvent event) {
            requireNonNull(event);
            track(event);
            countDownLatch.countDown();
        }
    
        @EventHandler
        public void handle3(TestEvent event) {
            requireNonNull(event);
            track(event);
            countDownLatch.countDown();
        }
    }    
}
