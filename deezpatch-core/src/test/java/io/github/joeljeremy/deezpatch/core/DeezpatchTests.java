package io.github.joeljeremy.deezpatch.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.Deezpatch.Builder.EventConfiguration;
import io.github.joeljeremy.deezpatch.core.Deezpatch.Builder.RequestConfiguration;
import io.github.joeljeremy.deezpatch.core.Deezpatch.EventHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.Deezpatch.RequestHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncEventHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncRequestHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.testentities.IntegerRequest;
import io.github.joeljeremy.deezpatch.core.testentities.TestEvent;
import io.github.joeljeremy.deezpatch.core.testentities.TestEventHandlers;
import io.github.joeljeremy.deezpatch.core.testentities.TestEventHandlers.TestEventHandler;
import io.github.joeljeremy.deezpatch.core.testentities.TestInstanceProviders;
import io.github.joeljeremy.deezpatch.core.testentities.TestRequestHandlers;
import io.github.joeljeremy.deezpatch.core.testentities.TestRequestHandlers.TestRequestHandler;
import io.github.joeljeremy.deezpatch.core.testentities.VoidRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DeezpatchTests {
  @Nested
  class BuilderTests {
    @Nested
    class InstanceProviderMethod {
      @Test
      @DisplayName("should throw when instance provider argument is null")
      void test1() {
        Deezpatch.Builder builder = Deezpatch.builder();

        assertThrows(NullPointerException.class, () -> builder.instanceProvider(null));
      }
    }

    @Nested
    class RequestsMethod {
      @Test
      @DisplayName("should throw when request configurer argument is null")
      void test1() {
        Deezpatch.Builder builder = Deezpatch.builder();

        assertThrows(NullPointerException.class, () -> builder.requests(null));
      }
    }

    @Nested
    class EventsMethod {
      @Test
      @DisplayName("should throw when event configurer argument is null")
      void test1() {
        Deezpatch.Builder builder = Deezpatch.builder();

        assertThrows(NullPointerException.class, () -> builder.events(null));
      }
    }

    @Nested
    class BuildMethod {
      @Test
      @DisplayName("should throw when no instance provider is configured")
      void test1() {
        Deezpatch.Builder builder = Deezpatch.builder();

        assertThrows(IllegalStateException.class, () -> builder.build());
      }

      @Nested
      class RequestConfigurationTests {
        @Nested
        class RegisterTests {
          @Test
          @DisplayName("should throw when request handler classes argument is null")
          void test1() {
            Consumer<RequestConfiguration> configurer =
                config -> config.register((Class<?>[]) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurer);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when request handler classes contains null")
          void test2() {
            Consumer<RequestConfiguration> configurer =
                config -> config.register(new Class<?>[] {TestRequestHandler.class, null});

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurer);

            assertThrows(NullPointerException.class, () -> builder.build());
          }
        }

        @Nested
        class InvocationStrategyTests {
          @Test
          @DisplayName("should throw when request handler invocation strategy argument is null")
          void test1() {
            Consumer<RequestConfiguration> configurer = config -> config.invocationStrategy(null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurer);

            assertThrows(NullPointerException.class, () -> builder.build());
          }
        }
      }

      @Nested
      class EventConfigurationTests {
        @Nested
        class RegisterTests {
          @Test
          @DisplayName("should throw when event handler classes argument is null")
          void test1() {
            Consumer<EventConfiguration> configurer = config -> config.register((Class<?>[]) null);

            Deezpatch.Builder builder =
                Deezpatch.builder().instanceProvider(TestInstanceProviders.of()).events(configurer);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when event handler classes contains null")
          void test2() {
            Consumer<EventConfiguration> configurer =
                config -> config.register(new Class<?>[] {TestEventHandler.class, null});

            Deezpatch.Builder builder =
                Deezpatch.builder().instanceProvider(TestInstanceProviders.of()).events(configurer);

            assertThrows(NullPointerException.class, () -> builder.build());
          }
        }

        @Nested
        class InvocationStrategyTests {
          @Test
          @DisplayName("should throw when event handler invocation strategy argument is null")
          void test1() {
            Consumer<EventConfiguration> configurer = config -> config.invocationStrategy(null);

            Deezpatch.Builder builder =
                Deezpatch.builder().instanceProvider(TestInstanceProviders.of()).events(configurer);

            assertThrows(NullPointerException.class, () -> builder.build());
          }
        }
      }
    }
  }

  @Nested
  class BuilderMethod {
    @Test
    @DisplayName("should never return null")
    void test1() {
      assertNotNull(Deezpatch.builder());
    }
  }

  @Nested
  class SendMethod {
    @Test
    @DisplayName("should throw when request argument is null")
    void test1() {
      var deezpatch =
          buildDeezpatch(
              List.of(TestRequestHandlers.primitiveRequestHandler()), Collections.emptyList());

      assertThrows(NullPointerException.class, () -> deezpatch.send(null));
    }

    @Test
    @DisplayName("should send request to registered request handler")
    void test2() {
      var requestHandler = TestRequestHandlers.primitiveRequestHandler();
      var deezpatch = buildDeezpatch(List.of(requestHandler), Collections.emptyList());

      var request = new IntegerRequest("1");
      Optional<Integer> result = deezpatch.send(request);

      assertEquals(1, requestHandler.handledMessages().size());
      assertTrue(requestHandler.hasHandled(request));

      assertNotNull(result);
      assertTrue(result.isPresent());
      assertEquals(1, result.get());
    }

    @Test
    @DisplayName("should throw when no request handler is registered")
    void test3() {
      var deezpatch =
          buildDeezpatch(
              Collections.emptyList(), // No request handlers.
              Collections.emptyList());

      var unhandledRequest = new UnhandledRequest();

      assertThrows(DeezpatchException.class, () -> deezpatch.send(unhandledRequest));
    }

    @Test
    @DisplayName("should propagate exception thrown by request handler")
    // This test is true unless a custom request handler invocation strategy is used.
    void test4() {
      var exception = new RuntimeException("Oops!");
      var throwingRequestHandler = TestRequestHandlers.throwingIntegerRequestHandler(exception);
      var deezpatch = buildDeezpatch(List.of(throwingRequestHandler), Collections.emptyList());

      var request = new IntegerRequest("This will throw.");
      RuntimeException thrown = assertThrows(RuntimeException.class, () -> deezpatch.send(request));

      assertTrue(throwingRequestHandler.hasHandled(request));
      assertSame(exception, thrown);
    }

    @Test
    @DisplayName("should use request handler invocation strategy when sending requests")
    void test5() {
      var requestHandler = TestRequestHandlers.primitiveRequestHandler();
      var invocationStrategyInvoked = new AtomicBoolean();

      RequestHandlerInvocationStrategy invocationStrategy =
          new RequestHandlerInvocationStrategy() {
            @Override
            public <T extends Request<R>, R> Optional<R> invoke(
                RegisteredRequestHandler<T, R> requestHandler, T request) {
              invocationStrategyInvoked.set(true);
              return requestHandler.invoke(request);
            }
          };

      var deezpatch =
          buildDeezpatch(
              List.of(requestHandler),
              Collections.emptyList(),
              invocationStrategy, // Custom request handler invocation strategy
              new SyncEventHandlerInvocationStrategy());

      var request = new IntegerRequest("1");
      Optional<Integer> result = deezpatch.send(request);

      assertTrue(requestHandler.hasHandled(request));
      assertTrue(invocationStrategyInvoked.get());

      assertNotNull(result);
      assertTrue(result.isPresent());
      assertEquals(1, result.get());
    }

    @Test
    @DisplayName("should return empty Optional when registered request handler has void result")
    void test6() {
      var voidRequestHandler = TestRequestHandlers.voidRequestHandler();
      var deezpatch = buildDeezpatch(List.of(voidRequestHandler), Collections.emptyList());

      var request = new VoidRequest("Fire!");
      Optional<Void> result = deezpatch.send(request);

      assertEquals(1, voidRequestHandler.handledMessages().size());
      assertTrue(voidRequestHandler.hasHandled(request));

      assertNotNull(result);
      assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("should propagate exception thrown by request handler that has void result")
    // This test is true unless a custom request handler invocation strategy is used.
    void test7() {
      var exception = new RuntimeException("Oops!");
      var throwingVoidRequestHandler = TestRequestHandlers.throwingVoidRequestHandler(exception);
      var deezpatch = buildDeezpatch(List.of(throwingVoidRequestHandler), Collections.emptyList());

      var fireAndForgetRequest = new VoidRequest("This will throw.");
      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> deezpatch.send(fireAndForgetRequest));

      assertTrue(throwingVoidRequestHandler.hasHandled(fireAndForgetRequest));
      assertSame(exception, thrown);
    }
  }

  @Nested
  class PublishMethod {
    @Test
    @DisplayName("should throw when event argument is null")
    void test1() {
      var deezpatch =
          buildDeezpatch(Collections.emptyList(), List.of(TestEventHandlers.testEventHandler()));

      assertThrows(NullPointerException.class, () -> deezpatch.send(null));
    }

    @Test
    @DisplayName("should publish event to all registered event handlers")
    void test2() {
      var testEventHandler = TestEventHandlers.testEventHandler();
      var deezpatch = buildDeezpatch(Collections.emptyList(), List.of(testEventHandler));

      var testEvent = new TestEvent("Test");
      deezpatch.publish(testEvent);

      assertTrue(testEventHandler.hasHandled(testEvent));

      int numberOfEventHandlers =
          (int)
              Arrays.stream(testEventHandler.getClass().getMethods())
                  .filter(m -> m.isAnnotationPresent(EventHandler.class))
                  .count();

      assertEquals(numberOfEventHandlers, testEventHandler.handledMessages().size());
    }

    @Test
    @DisplayName("should not throw when no event handlers are registered")
    void test3() {
      var deezpatch =
          buildDeezpatch(
              Collections.emptyList(), Collections.emptyList() // No event handlers
              );

      var unhandledEvent = new UnhandledEvent();
      assertDoesNotThrow(() -> deezpatch.publish(unhandledEvent));
    }

    @Test
    @DisplayName("should propagate exception thrown by event handler")
    // This test is true unless a custom event handler invocation strategy is used.
    void test4() {
      var exception = new RuntimeException("Oops!");
      var throwingEventHandler = TestEventHandlers.throwingEventHandler(exception);
      var deezpatch = buildDeezpatch(Collections.emptyList(), List.of(throwingEventHandler));

      var testEvent = new TestEvent("Test");
      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> deezpatch.publish(testEvent));

      assertTrue(throwingEventHandler.hasHandled(testEvent));
      assertSame(exception, thrown);
    }

    @Test
    @DisplayName("should use event handler invocation strategy when publishing events")
    void test5() {
      var testEventHandler = TestEventHandlers.testEventHandler();
      var invocationStrategyInvoked = new AtomicBoolean();

      EventHandlerInvocationStrategy invocationStrategy =
          new EventHandlerInvocationStrategy() {
            @Override
            public <T extends Event> void invokeAll(
                List<RegisteredEventHandler<T>> eventHandlers, T event) {
              invocationStrategyInvoked.set(true);
              eventHandlers.forEach(e -> e.invoke(event));
            }
          };

      var deezpatch =
          buildDeezpatch(
              Collections.emptyList(),
              List.of(testEventHandler),
              new SyncRequestHandlerInvocationStrategy(),
              invocationStrategy // Custom event handler invocation strategy
              );

      var testEvent = new TestEvent("Test");
      deezpatch.publish(testEvent);

      assertTrue(testEventHandler.hasHandled(testEvent));
      assertTrue(invocationStrategyInvoked.get());
    }
  }

  private static Deezpatch buildDeezpatch(
      Collection<Object> requestHandlers, Collection<Object> eventHandlers) {
    return buildDeezpatch(
        requestHandlers,
        eventHandlers,
        // Default request handler invocation strategy.
        new SyncRequestHandlerInvocationStrategy(),
        // Default event handler invocation strategy.
        new SyncEventHandlerInvocationStrategy());
  }

  private static Deezpatch buildDeezpatch(
      Collection<Object> requestHandlers,
      Collection<Object> eventHandlers,
      RequestHandlerInvocationStrategy requestHandlerInvocationStrategy,
      EventHandlerInvocationStrategy eventHandlerInvocationStrategy) {
    Object[] instances = Stream.concat(requestHandlers.stream(), eventHandlers.stream()).toArray();

    InstanceProvider instanceProvider = TestInstanceProviders.of(instances);

    return Deezpatch.builder()
        .instanceProvider(instanceProvider)
        .requests(
            config ->
                config
                    .register(requestHandlers.stream().map(Object::getClass).toArray(Class[]::new))
                    .invocationStrategy(requestHandlerInvocationStrategy))
        .events(
            config ->
                config
                    .register(eventHandlers.stream().map(Object::getClass).toArray(Class[]::new))
                    .invocationStrategy(eventHandlerInvocationStrategy))
        .build();
  }

  static class UnhandledRequest implements Request<Void> {}

  static class UnhandledEvent implements Event {}
}
