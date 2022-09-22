package io.github.joeljeremy7.deezpatch.core.invocationstrategies;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy7.deezpatch.core.Event;
import io.github.joeljeremy7.deezpatch.core.RegisteredEventHandler;
import io.github.joeljeremy7.deezpatch.core.testentities.TestEvent;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AsyncEventHandlerInvocationStrategyTests {
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();

  @Nested
  class InvokeAllMethod {
    @Test
    @DisplayName("should asynchronously invoke the registered event handler")
    void test1() throws InterruptedException {
      var handlerInvoked = new AtomicBoolean();
      var countDownLatch = new CountDownLatch(1);
      var strategy = new AsyncEventHandlerInvocationStrategy(EXECUTOR_SERVICE, (e, ex) -> {});

      strategy.invokeAll(
          List.of(
              e -> {
                handlerInvoked.set(true);
                countDownLatch.countDown();
              }),
          new TestEvent("Test"));

      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
      assertTrue(handlerInvoked.get());
    }

    @Test
    @DisplayName("should pass the event to the registered event handler")
    void test2() throws InterruptedException {
      var eventRef = new AtomicReference<>();
      var countDownLatch = new CountDownLatch(1);
      var strategy = new AsyncEventHandlerInvocationStrategy(EXECUTOR_SERVICE, (e, ex) -> {});

      var event = new TestEvent("Test");
      strategy.invokeAll(
          List.of(
              e -> {
                eventRef.set(e);
                countDownLatch.countDown();
              }),
          event);

      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
      assertSame(event, eventRef.get());
    }

    @Test
    @DisplayName("should not propagate event handler exceptions")
    void test3() throws InterruptedException {
      var exception = new RuntimeException();
      var strategy = new AsyncEventHandlerInvocationStrategy(EXECUTOR_SERVICE, (e, ex) -> {});

      List<RegisteredEventHandler<TestEvent>> eventHandlers =
          List.of(
              e -> {
                throw exception;
              });

      assertDoesNotThrow(() -> strategy.invokeAll(eventHandlers, new TestEvent("Test")));
    }

    @Test
    @DisplayName("should delegate event handler exceptions to the exception handler")
    void test4() throws InterruptedException {
      var countDownLatch = new CountDownLatch(1);
      AtomicReference<Throwable> handledException = new AtomicReference<>();
      AsyncEventHandlerInvocationStrategy.ExceptionHandler exceptionHandler =
          (e, ex) -> {
            handledException.set(ex);
            countDownLatch.countDown();
          };
      var exception = new RuntimeException();
      var strategy = new AsyncEventHandlerInvocationStrategy(EXECUTOR_SERVICE, exceptionHandler);

      List<RegisteredEventHandler<TestEvent>> eventHandlers =
          List.of(
              e -> {
                throw exception;
              });

      assertDoesNotThrow(() -> strategy.invokeAll(eventHandlers, new TestEvent("Test")));
      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
      assertSame(exception, handledException.get());
    }

    @Test
    @DisplayName("should delegate event which caused the exception to the exception handler")
    void test5() throws InterruptedException {
      var countDownLatch = new CountDownLatch(1);
      AtomicReference<Event> causeEvent = new AtomicReference<>();
      AsyncEventHandlerInvocationStrategy.ExceptionHandler exceptionHandler =
          (e, ex) -> {
            causeEvent.set(e);
            countDownLatch.countDown();
          };
      var exception = new RuntimeException();
      var strategy = new AsyncEventHandlerInvocationStrategy(EXECUTOR_SERVICE, exceptionHandler);

      var event = new TestEvent("Test");

      List<RegisteredEventHandler<TestEvent>> eventHandlers =
          List.of(
              e -> {
                throw exception;
              });

      assertDoesNotThrow(() -> strategy.invokeAll(eventHandlers, event));
      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
      assertSame(event, causeEvent.get());
    }
  }
}
