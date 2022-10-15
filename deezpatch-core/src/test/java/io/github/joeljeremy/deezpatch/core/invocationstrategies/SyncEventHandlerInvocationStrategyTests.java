package io.github.joeljeremy.deezpatch.core.invocationstrategies;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.RegisteredEventHandler;
import io.github.joeljeremy.deezpatch.core.testentities.TestEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SyncEventHandlerInvocationStrategyTests {
  @Nested
  class InvokeAllMethod {
    @Test
    @DisplayName("should synchronously invoke the registered event handler")
    void test1() {
      AtomicBoolean handlerInvoked = new AtomicBoolean();
      var strategy = new SyncEventHandlerInvocationStrategy();

      strategy.invokeAll(List.of(e -> handlerInvoked.set(true)), new TestEvent("Test"));

      assertTrue(handlerInvoked.get());
    }

    @Test
    @DisplayName("should pass the event to the registered event handler")
    void test2() {
      AtomicReference<TestEvent> eventRef = new AtomicReference<>();
      var strategy = new SyncEventHandlerInvocationStrategy();

      var event = new TestEvent("Test");
      strategy.invokeAll(List.of(e -> eventRef.set(e)), event);

      assertSame(event, eventRef.get());
    }

    @Test
    @DisplayName("should propagate event handler exceptions")
    void test3() {
      var exception = new RuntimeException();
      var strategy = new SyncEventHandlerInvocationStrategy();
      var event = new TestEvent("Test");

      List<RegisteredEventHandler<TestEvent>> eventHandlers =
          List.of(
              e -> {
                throw exception;
              });

      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> strategy.invokeAll(eventHandlers, event));

      assertSame(exception, thrown);
    }
  }
}
