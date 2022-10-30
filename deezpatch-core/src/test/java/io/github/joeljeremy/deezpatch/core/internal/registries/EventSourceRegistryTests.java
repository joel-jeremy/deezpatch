package io.github.joeljeremy.deezpatch.core.internal.registries;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventSource;
import io.github.joeljeremy.deezpatch.core.Publisher;
import io.github.joeljeremy.deezpatch.core.RegisteredEventSource;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventSources;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventSources.InvalidArgEventSource;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventSources.InvalidReturnTypeEventSource;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventSources.NoArgEventSource;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventSources.TestScheduledEventSource;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestInstanceProviders;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class EventSourceRegistryTests {
  @Nested
  class Constructors {
    @Test
    @DisplayName("should throw when instance provider argument is null")
    void test1() {
      assertThrows(NullPointerException.class, () -> new EventSourceRegistry(null));
    }
  }

  @Nested
  class RegisterMethod {
    @Test
    @DisplayName("should throw when event source class argument is null")
    void test1() {
      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(TestEventSources.testScheduledEventSource());

      assertThrows(
          NullPointerException.class, () -> eventSourceRegistry.register((Class<?>[]) null));
    }

    @Test
    @DisplayName("should detect and register methods annotated with @EventSource")
    void test2() {
      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(TestEventSources.testScheduledEventSource());

      eventSourceRegistry.register(TestScheduledEventSource.class);

      List<RegisteredEventSource> eventSources = eventSourceRegistry.getEventSources();

      assertNotNull(eventSources);

      int numberOfEventHandlers =
          (int)
              Arrays.stream(TestScheduledEventSource.class.getMethods())
                  .filter(m -> m.isAnnotationPresent(EventSource.class))
                  .count();

      assertEquals(numberOfEventHandlers, eventSources.size());
    }

    @Test
    @DisplayName("should throw when a method annotated with @EventSource does not have a parameter")
    void test3() {
      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(TestEventSources.noArgEventSource());

      assertThrows(
          IllegalArgumentException.class,
          () -> eventSourceRegistry.register(NoArgEventSource.class));
    }

    @Test
    @DisplayName(
        "should ignore method with correct method signature but not annotated with @EventSource")
    void test4() {
      var eventSource =
          new Object() {
            @SuppressWarnings("unused")
            public void eventSource(Publisher publisher) {
              // Valid method signature but no @EventSource annotation.
            }
          };

      EventSourceRegistry eventSourceRegistry = eventSourceRegistry(eventSource);

      eventSourceRegistry.register(eventSource.getClass());

      assertTrue(eventSourceRegistry.getEventSources().isEmpty());
    }

    @Test
    @DisplayName("should throw when a method annotated with @Event does not return void")
    void test5() {
      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(TestEventSources.invalidReturnTypeEventSource());

      assertThrows(
          IllegalArgumentException.class,
          () -> eventSourceRegistry.register(InvalidReturnTypeEventSource.class));
    }

    @Test
    @DisplayName(
        "should throw when a method annotated with @EventSource has an invalid parameter "
            + "(parameter does not implement Publisher)")
    void test6() {
      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(TestEventSources.invalidArgEventSource());

      assertThrows(
          IllegalArgumentException.class,
          () -> eventSourceRegistry.register(InvalidArgEventSource.class));
    }
  }

  @Nested
  class GetEventSourcesMethod {
    @Test
    @DisplayName("should return all registered event sources")
    void test1() {
      var testScheduledEventSource = TestEventSources.testScheduledEventSource();

      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(testScheduledEventSource)
              .register(testScheduledEventSource.getClass());

      List<RegisteredEventSource> resolved = eventSourceRegistry.getEventSources();

      assertNotNull(resolved);
      assertFalse(resolved.isEmpty());
    }

    @Test
    @DisplayName("should return empty list when there is no registered event sources")
    void test2() {
      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(TestEventSources.testScheduledEventSource());

      // No registrations...

      List<RegisteredEventSource> resolved = eventSourceRegistry.getEventSources();

      assertNotNull(resolved);
      assertTrue(resolved.isEmpty());
    }

    @Test
    @DisplayName(
        "should return registered event sources whose toString() method "
            + "returns the event source method string")
    void test3() {
      var testScheduledEventSource = TestEventSources.testScheduledEventSource();

      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(testScheduledEventSource)
              .register(testScheduledEventSource.getClass());

      List<RegisteredEventSource> resolved = eventSourceRegistry.getEventSources();

      assertNotNull(resolved);
      assertFalse(resolved.isEmpty());

      // TestScheduledEventSource only has one @EventSource method so this should be safe.
      Method eventSourceMethod =
          Stream.of(TestScheduledEventSource.class.getMethods())
              .filter(m -> m.isAnnotationPresent(EventSource.class))
              .findFirst()
              .orElseThrow();

      RegisteredEventSource eventSource = resolved.get(0);
      assertEquals(eventSourceMethod.toGenericString(), eventSource.toString());
    }

    @Test
    @DisplayName("should return registered event sources which start in a separate daemon thread")
    void test4() throws InterruptedException {
      Thread testThread = Thread.currentThread();

      var countDownLatch = new CountDownLatch(1);
      var eventSourceThreadRef = new AtomicReference<Thread>();
      var blockingEventSource =
          TestEventSources.delegatingEventSource(
              publisher -> {
                // Set thread which event source currently runs on.
                eventSourceThreadRef.set(Thread.currentThread());
                // Make sure we set the event source thread first before proceeding.
                countDownLatch.countDown();
              });

      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(blockingEventSource).register(blockingEventSource.getClass());

      List<RegisteredEventSource> resolved = eventSourceRegistry.getEventSources();

      assertNotNull(resolved);
      assertFalse(resolved.isEmpty());

      Publisher mockPublisher =
          new Publisher() {
            @Override
            public <T extends Event> void publish(T event) {
              // Do-nothing.
            }
          };

      RegisteredEventSource eventSource = resolved.get(0);
      eventSource.start(mockPublisher);

      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));

      // Different threads.
      Thread eventSourceThread = eventSourceThreadRef.get();
      assertNotNull(eventSourceThread);
      assertNotEquals(testThread, eventSourceThread);
      // Daemon thread.
      assertTrue(eventSourceThread.isDaemon());
    }

    @Test
    @DisplayName("should return registered event sources which do not propagate thrown exceptions")
    void test5() {
      var toThrow = new RuntimeException("Oops!");
      var testScheduledEventSource = TestEventSources.throwingEventSource(toThrow);

      EventSourceRegistry eventSourceRegistry =
          eventSourceRegistry(testScheduledEventSource)
              .register(testScheduledEventSource.getClass());

      List<RegisteredEventSource> resolved = eventSourceRegistry.getEventSources();

      assertNotNull(resolved);
      assertFalse(resolved.isEmpty());

      Publisher mockPublisher =
          new Publisher() {
            @Override
            public <T extends Event> void publish(T event) {
              // Do-nothing.
            }
          };

      RegisteredEventSource eventSource = resolved.get(0);
      assertDoesNotThrow(() -> eventSource.start(mockPublisher));
    }
  }

  static EventSourceRegistry eventSourceRegistry(Object... eventHandlers) {
    return new EventSourceRegistry(TestInstanceProviders.of(eventHandlers));
  }
}
