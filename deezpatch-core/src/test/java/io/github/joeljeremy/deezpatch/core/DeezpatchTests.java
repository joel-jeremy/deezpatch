package io.github.joeljeremy.deezpatch.core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.Deezpatch.EventHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.Deezpatch.EventHandlingConfigurator;
import io.github.joeljeremy.deezpatch.core.Deezpatch.RequestHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.Deezpatch.RequestHandlingConfigurator;
import io.github.joeljeremy.deezpatch.core.invocationstrategies.SyncEventHandlerInvocationStrategy;
import io.github.joeljeremy.deezpatch.core.testfixtures.CustomEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.CustomRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.IntegerRequest;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEvent;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers.CountDownLatchEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers.TestEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestInstanceProviders;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequest;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.TestRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.VoidRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestResult;
import io.github.joeljeremy.deezpatch.core.testfixtures.VoidRequest;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
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
      @DisplayName("should throw when request configurator argument is null")
      void test1() {
        Deezpatch.Builder builder = Deezpatch.builder();

        assertThrows(NullPointerException.class, () -> builder.requests(null));
      }
    }

    @Nested
    class EventsMethod {
      @Test
      @DisplayName("should throw when event configurator argument is null")
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
      class RequestHandlingConfigurationTests {
        @Nested
        class HandlerAnnotationsMethod {
          @Test
          @DisplayName("should throw when request handler annotations argument is null")
          void test1() {
            RequestHandlingConfigurator configurator =
                config -> config.handlerAnnotations((Class<? extends Annotation>[]) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when request handler annotations argument contains null")
          void test2() {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation>[] requestHandlerAnnotations =
                (Class<? extends Annotation>[]) new Class<?>[] {CustomRequestHandler.class, null};

            RequestHandlingConfigurator configurator =
                config -> config.handlerAnnotations(requestHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName(
              "should not throw when request handler annotations argument contains valid items")
          void test3() {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation>[] requestHandlerAnnotations =
                (Class<? extends Annotation>[])
                    new Class<?>[] {CustomRequestHandler.class, RequestHandler.class};

            RequestHandlingConfigurator configurator =
                config -> config.handlerAnnotations(requestHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class HandlerAnnotationsMethodWithCollectionOverload {
          @Test
          @DisplayName("should throw when request handler annotations argument is null")
          void test1() {
            RequestHandlingConfigurator configurator =
                config -> config.handlerAnnotations((Collection<Class<? extends Annotation>>) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when request handler annotations argument contains null")
          void test2() {
            Collection<Class<? extends Annotation>> requestHandlerAnnotations =
                Arrays.asList(CustomRequestHandler.class, null);

            RequestHandlingConfigurator configurator =
                config -> config.handlerAnnotations(requestHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName(
              "should not throw when request handler annotations argument contains valid items")
          void test3() {
            Collection<Class<? extends Annotation>> requestHandlerAnnotations =
                Arrays.asList(CustomRequestHandler.class, RequestHandler.class);

            RequestHandlingConfigurator configurator =
                config -> config.handlerAnnotations(requestHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class HandlersMethod {
          @Test
          @DisplayName("should throw when request handler classes argument is null")
          void test1() {
            RequestHandlingConfigurator configurator = config -> config.handlers((Class<?>[]) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when request handler classes argument contains null")
          void test2() {
            Class<?>[] requestHandlerClasses = new Class<?>[] {TestRequestHandler.class, null};

            RequestHandlingConfigurator configurator =
                config -> config.handlers(requestHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName(
              "should not throw when request handler classes argument contains valid items")
          void test3() {
            Class<?>[] requestHandlerClasses =
                new Class<?>[] {TestRequestHandler.class, VoidRequestHandler.class};

            RequestHandlingConfigurator configurator =
                config -> config.handlers(requestHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class HandlersMethodWithCollectionOverload {
          @Test
          @DisplayName("should throw when request handler classes argument is null")
          void test1() {
            RequestHandlingConfigurator configurator =
                config -> config.handlers((Collection<Class<?>>) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when request handler classes argument contains null")
          void test2() {
            Collection<Class<?>> requestHandlerClasses =
                Arrays.asList(TestRequestHandler.class, null);

            RequestHandlingConfigurator configurator =
                config -> config.handlers(requestHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName(
              "should not throw when request handler classes argument contains valid items")
          void test3() {
            Collection<Class<?>> requestHandlerClasses =
                Arrays.asList(TestRequestHandler.class, VoidRequestHandler.class);

            RequestHandlingConfigurator configurator =
                config -> config.handlers(requestHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class InvocationStrategyMethod {
          @Test
          @DisplayName("should throw when request handler invocation strategy argument is null")
          void test1() {
            RequestHandlingConfigurator configurator = config -> config.invocationStrategy(null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .requests(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }
        }
      }

      @Nested
      class EventHandlingConfigurationTests {
        @Nested
        class HandlerAnnotationsMethod {
          @Test
          @DisplayName("should throw when event handler annotations argument is null")
          void test1() {
            EventHandlingConfigurator configurator =
                config -> config.handlerAnnotations((Class<? extends Annotation>[]) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when event handler annotations argument contains null")
          void test2() {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation>[] eventHandlerAnnotations =
                (Class<? extends Annotation>[]) new Class<?>[] {CustomEventHandler.class, null};

            EventHandlingConfigurator configurator =
                config -> config.handlerAnnotations(eventHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName(
              "should not throw when event handler annotations argument contains valid items")
          void test3() {
            @SuppressWarnings("unchecked")
            Class<? extends Annotation>[] eventHandlerAnnotations =
                (Class<? extends Annotation>[])
                    new Class<?>[] {CustomEventHandler.class, EventHandler.class};

            EventHandlingConfigurator configurator =
                config -> config.handlerAnnotations(eventHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class HandlerAnnotationsMethodWithCollectionOverload {
          @Test
          @DisplayName("should throw when event handler annotations argument is null")
          void test1() {
            EventHandlingConfigurator configurator =
                config -> config.handlerAnnotations((Collection<Class<? extends Annotation>>) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when event handler annotations argument contains null")
          void test2() {
            Collection<Class<? extends Annotation>> eventHandlerAnnotations =
                Arrays.asList(CustomEventHandler.class, null);

            EventHandlingConfigurator configurator =
                config -> config.handlerAnnotations(eventHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName(
              "should not throw when event handler annotations argument contains valid items")
          void test3() {
            Collection<Class<? extends Annotation>> eventHandlerAnnotations =
                Arrays.asList(CustomEventHandler.class, EventHandler.class);

            EventHandlingConfigurator configurator =
                config -> config.handlerAnnotations(eventHandlerAnnotations);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class HandlersMethod {
          @Test
          @DisplayName("should throw when event handler classes argument is null")
          void test1() {
            EventHandlingConfigurator configurator = config -> config.handlers((Class<?>[]) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when event handler classes argument contains null")
          void test2() {
            Class<?>[] eventHandlerClasses = new Class<?>[] {TestEventHandler.class, null};

            EventHandlingConfigurator configurator = config -> config.handlers(eventHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should not throw when event handler classes argument contains valid items")
          void test3() {
            Class<?>[] eventHandlerClasses =
                new Class<?>[] {TestEventHandler.class, CountDownLatchEventHandler.class};

            EventHandlingConfigurator configurator = config -> config.handlers(eventHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class HandlersMethodWithCollectionOverload {
          @Test
          @DisplayName("should throw when event handler classes argument is null")
          void test1() {
            EventHandlingConfigurator configurator =
                config -> config.handlers((Collection<Class<?>>) null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should throw when event handler classes argument contains null")
          void test2() {
            Collection<Class<?>> eventHandlerClasses = Arrays.asList(TestEventHandler.class, null);

            EventHandlingConfigurator configurator = config -> config.handlers(eventHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should not throw when event handler classes argument contain valid items")
          void test3() {
            Collection<Class<?>> eventHandlerClasses =
                Arrays.asList(TestEventHandler.class, CountDownLatchEventHandler.class);

            EventHandlingConfigurator configurator = config -> config.handlers(eventHandlerClasses);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertDoesNotThrow(() -> builder.build());
          }
        }

        @Nested
        class InvocationStrategyMethod {
          @Test
          @DisplayName("should throw when event handler invocation strategy argument is null")
          void test1() {
            EventHandlingConfigurator configurator = config -> config.invocationStrategy(null);

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertThrows(NullPointerException.class, () -> builder.build());
          }

          @Test
          @DisplayName("should not when event handler invocation strategy argument is valid")
          void test2() {
            EventHandlingConfigurator configurator =
                config -> config.invocationStrategy(new SyncEventHandlerInvocationStrategy());

            Deezpatch.Builder builder =
                Deezpatch.builder()
                    .instanceProvider(TestInstanceProviders.of())
                    .events(configurator);

            assertDoesNotThrow(() -> builder.build());
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
      var requestHandler = TestRequestHandlers.primitiveRequestHandler();
      var instanceProvider = TestInstanceProviders.of(requestHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(config -> config.handlers(requestHandler.getClass()))
              .build();

      assertThrows(NullPointerException.class, () -> deezpatch.send(null));
    }

    @Test
    @DisplayName("should send request to registered request handler")
    void test2() {
      var requestHandler = TestRequestHandlers.primitiveRequestHandler();
      var instanceProvider = TestInstanceProviders.of(requestHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(config -> config.handlers(requestHandler.getClass()))
              .build();

      var request = new IntegerRequest("1");
      Optional<Integer> result = deezpatch.send(request);

      assertEquals(1, requestHandler.handledMessages().size());
      assertTrue(requestHandler.hasHandled(request));

      assertNotNull(result);
      assertTrue(result.isPresent());
      assertEquals(1, result.get());
    }

    @Test
    @DisplayName(
        "should send request to registered request handler (custom request handler annotation)")
    void test3() {
      var customAnnotationRequestHandler = TestRequestHandlers.customAnnotationRequestHandler();
      var instanceProvider = TestInstanceProviders.of(customAnnotationRequestHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(
                  config ->
                      config
                          .handlers(customAnnotationRequestHandler.getClass())
                          .handlerAnnotations(CustomRequestHandler.class))
              .build();

      var request = new TestRequest("test3");
      Optional<TestResult> result = deezpatch.send(request);

      assertEquals(1, customAnnotationRequestHandler.handledMessages().size());
      assertTrue(customAnnotationRequestHandler.hasHandled(request));

      assertNotNull(result);
      assertTrue(result.isPresent());
      // The handler just returns the parameter value. Let's assert that.
      assertEquals(request.parameter(), result.get().value());
    }

    @Test
    @DisplayName("should throw when no request handler is registered")
    void test4() {
      var instanceProvider = TestInstanceProviders.of();
      var deezpatch = Deezpatch.builder().instanceProvider(instanceProvider).build();

      var unhandledRequest = new UnhandledRequest();

      assertThrows(DeezpatchException.class, () -> deezpatch.send(unhandledRequest));
    }

    @Test
    @DisplayName("should propagate exception thrown by request handler")
    // This test is true unless a custom request handler invocation strategy is used.
    void test5() {
      var exception = new RuntimeException("Oops!");
      var throwingRequestHandler = TestRequestHandlers.throwingIntegerRequestHandler(exception);
      var instanceProvider = TestInstanceProviders.of(throwingRequestHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(config -> config.handlers(throwingRequestHandler.getClass()))
              .build();

      var request = new IntegerRequest("This will throw.");
      RuntimeException thrown = assertThrows(RuntimeException.class, () -> deezpatch.send(request));

      assertTrue(throwingRequestHandler.hasHandled(request));
      assertSame(exception, thrown);
    }

    @Test
    @DisplayName("should use request handler invocation strategy when sending requests")
    void test6() {
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

      var instanceProvider = TestInstanceProviders.of(requestHandler);

      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(
                  config ->
                      config
                          .handlers(requestHandler.getClass())
                          .invocationStrategy(invocationStrategy))
              .build();

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
    void test7() {
      var voidRequestHandler = TestRequestHandlers.voidRequestHandler();
      var instanceProvider = TestInstanceProviders.of(voidRequestHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(config -> config.handlers(voidRequestHandler.getClass()))
              .build();

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
    void test8() {
      var exception = new RuntimeException("Oops!");
      var throwingVoidRequestHandler = TestRequestHandlers.throwingVoidRequestHandler(exception);
      var instanceProvider = TestInstanceProviders.of(throwingVoidRequestHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .requests(config -> config.handlers(throwingVoidRequestHandler.getClass()))
              .build();

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
      var eventHandler = TestEventHandlers.testEventHandler();
      var instanceProvider = TestInstanceProviders.of(eventHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .events(config -> config.handlers(eventHandler.getClass()))
              .build();

      assertThrows(NullPointerException.class, () -> deezpatch.send(null));
    }

    @Test
    @DisplayName("should publish event to all registered event handlers")
    void test2() {
      var eventHandler = TestEventHandlers.testEventHandler();
      var instanceProvider = TestInstanceProviders.of(eventHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .events(config -> config.handlers(eventHandler.getClass()))
              .build();

      var testEvent = new TestEvent("Test");
      deezpatch.publish(testEvent);

      assertTrue(eventHandler.hasHandled(testEvent));

      int numberOfEventHandlers =
          (int)
              Arrays.stream(eventHandler.getClass().getMethods())
                  .filter(m -> m.isAnnotationPresent(EventHandler.class))
                  .count();

      assertEquals(numberOfEventHandlers, eventHandler.handledMessages().size());
    }

    @Test
    @DisplayName(
        "should publish event to all registered event handlers (custom event handler annotation)")
    void test3() {
      var customAnnotationEventHandler = TestEventHandlers.customAnnotationEventHandler();
      var instanceProvider = TestInstanceProviders.of(customAnnotationEventHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .events(
                  config ->
                      config
                          .handlers(customAnnotationEventHandler.getClass())
                          .handlerAnnotations(CustomEventHandler.class))
              .build();

      var testEvent = new TestEvent("Test");
      deezpatch.publish(testEvent);

      assertTrue(customAnnotationEventHandler.hasHandled(testEvent));

      int numberOfEventHandlers =
          (int)
              Arrays.stream(customAnnotationEventHandler.getClass().getMethods())
                  .filter(m -> m.isAnnotationPresent(CustomEventHandler.class))
                  .count();

      assertEquals(numberOfEventHandlers, customAnnotationEventHandler.handledMessages().size());
    }

    @Test
    @DisplayName("should not throw when no event handlers are registered")
    void test4() {
      var instanceProvider = TestInstanceProviders.of();
      // No event handlers
      var deezpatch = Deezpatch.builder().instanceProvider(instanceProvider).build();

      var unhandledEvent = new UnhandledEvent();
      assertDoesNotThrow(() -> deezpatch.publish(unhandledEvent));
    }

    @Test
    @DisplayName("should propagate exception thrown by event handler")
    // This test is true unless a custom event handler invocation strategy is used.
    void test5() {
      var exception = new RuntimeException("Oops!");
      var throwingEventHandler = TestEventHandlers.throwingEventHandler(exception);
      var instanceProvider = TestInstanceProviders.of(throwingEventHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .events(config -> config.handlers(throwingEventHandler.getClass()))
              .build();

      var testEvent = new TestEvent("Test");
      RuntimeException thrown =
          assertThrows(RuntimeException.class, () -> deezpatch.publish(testEvent));

      assertTrue(throwingEventHandler.hasHandled(testEvent));
      assertSame(exception, thrown);
    }

    @Test
    @DisplayName("should use event handler invocation strategy when publishing events")
    void test6() {
      var eventHandler = TestEventHandlers.testEventHandler();
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

      var instanceProvider = TestInstanceProviders.of(eventHandler);
      var deezpatch =
          Deezpatch.builder()
              .instanceProvider(instanceProvider)
              .events(
                  config ->
                      config
                          .handlers(eventHandler.getClass())
                          .invocationStrategy(invocationStrategy))
              .build();

      var testEvent = new TestEvent("Test");
      deezpatch.publish(testEvent);

      assertTrue(eventHandler.hasHandled(testEvent));
      assertTrue(invocationStrategyInvoked.get());
    }
  }

  static class UnhandledRequest implements Request<Void> {}

  static class UnhandledEvent implements Event {}
}
