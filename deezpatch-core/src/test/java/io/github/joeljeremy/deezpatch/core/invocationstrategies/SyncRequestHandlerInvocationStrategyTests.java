package io.github.joeljeremy.deezpatch.core.invocationstrategies;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.testfixtures.IntegerRequest;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class SyncRequestHandlerInvocationStrategyTests {
  @Nested
  class InvokeMethod {
    @Test
    @DisplayName("should synchronously invoke the request handler")
    void test1() {
      AtomicBoolean handlerInvoked = new AtomicBoolean();
      var strategy = new SyncRequestHandlerInvocationStrategy();

      strategy.invoke(
          req -> {
            handlerInvoked.set(true);
            return Optional.of(Integer.parseInt(req.parameter()));
          },
          new IntegerRequest("1"));

      assertTrue(handlerInvoked.get());
    }

    @Test
    @DisplayName("should pass the request to the registered request handler")
    void test2() {
      AtomicReference<IntegerRequest> requestRef = new AtomicReference<>();
      var strategy = new SyncRequestHandlerInvocationStrategy();

      var request = new IntegerRequest("1");
      strategy.invoke(
          req -> {
            requestRef.set(req);
            return Optional.of(Integer.parseInt(req.parameter()));
          },
          request);

      assertSame(request, requestRef.get());
    }

    @Test
    @DisplayName("should propagate request handler exceptions")
    void test3() {
      var exception = new RuntimeException();
      var strategy = new SyncRequestHandlerInvocationStrategy();
      var request = new IntegerRequest("1");

      RuntimeException thrown =
          assertThrows(
              RuntimeException.class,
              () ->
                  strategy.invoke(
                      req -> {
                        throw exception;
                      },
                      request));

      assertSame(exception, thrown);
    }
  }
}
