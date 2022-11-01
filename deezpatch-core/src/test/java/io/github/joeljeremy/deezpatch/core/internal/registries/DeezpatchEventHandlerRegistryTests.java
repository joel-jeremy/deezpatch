package io.github.joeljeremy.deezpatch.core.internal.registries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventHandler;
import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.RegisteredEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.CustomEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEvent;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers.CustomAnnotationEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers.InvalidEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers.InvalidReturnTypeEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEventHandlers.TestEventHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestInstanceProviders;
import io.github.joeljeremy.deezpatch.core.testfixtures.TrackableHandler;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DeezpatchEventHandlerRegistryTests {
  @Nested
  class Constructors {
    @Test
    @DisplayName("should throw when instance provider argument is null")
    void test1() {
      Set<Class<? extends Annotation>> eventHandlerAnnotations = Set.of();

      assertThrows(
          NullPointerException.class,
          () -> new DeezpatchEventHandlerRegistry(null, eventHandlerAnnotations));
    }

    @Test
    @DisplayName("should throw when event handler annotations argument is null")
    void test2() {
      InstanceProvider instanceProvider = TestInstanceProviders.of();

      assertThrows(
          NullPointerException.class,
          () -> new DeezpatchEventHandlerRegistry(instanceProvider, null));
    }
  }

  @Nested
  class RegisterMethod {
    @Test
    @DisplayName("should throw when event handler class argument is null")
    void test1() {
      DeezpatchEventHandlerRegistry eventHandlerRegistry = buildEventHandlerRegistry();

      assertThrows(
          NullPointerException.class, () -> eventHandlerRegistry.register((Class<?>[]) null));
    }

    @Test
    @DisplayName("should detect and register methods annotated with @EventHandler")
    void test2() {
      TestEventHandler eventHandler = TestEventHandlers.testEventHandler();
      DeezpatchEventHandlerRegistry eventHandlerRegistry = buildEventHandlerRegistry(eventHandler);

      eventHandlerRegistry.register(eventHandler.getClass());

      List<RegisteredEventHandler<TestEvent>> eventHandlers =
          eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

      assertNotNull(eventHandlers);

      int numberOfEventHandlers =
          (int)
              Arrays.stream(eventHandler.getClass().getMethods())
                  .filter(m -> m.isAnnotationPresent(EventHandler.class))
                  .count();

      assertEquals(numberOfEventHandlers, eventHandlers.size());
    }

    @Test
    @DisplayName(
        "should detect and register methods annotated with custom event handler annotation")
    void test3() {
      Class<CustomEventHandler> customEventHandlerAnnotation = CustomEventHandler.class;
      CustomAnnotationEventHandler customAnnotationEventHandler = 
          TestEventHandlers.customAnnotationEventHandler();
      
      DeezpatchEventHandlerRegistry eventHandlerRegistry = 
          buildEventHandlerRegistry(
              Set.of(customEventHandlerAnnotation), customAnnotationEventHandler);

      eventHandlerRegistry.register(customAnnotationEventHandler.getClass());

      List<RegisteredEventHandler<TestEvent>> eventHandlers =
          eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

      assertNotNull(eventHandlers);

      int numberOfEventHandlers =
          (int)
              Arrays.stream(customAnnotationEventHandler.getClass().getMethods())
                  .filter(m -> m.isAnnotationPresent(customEventHandlerAnnotation))
                  .count();

      assertEquals(numberOfEventHandlers, eventHandlers.size());
    }

    @Test
    @DisplayName(
        "should throw when a method annotated with @EventHandler does not have a parameter")
    void test4() {
      InvalidEventHandler invalidEventHandler = TestEventHandlers.invalidEventHandler();
      DeezpatchEventHandlerRegistry eventHandlerRegistry =
          buildEventHandlerRegistry(invalidEventHandler);

      assertThrows(
          IllegalArgumentException.class,
          () -> eventHandlerRegistry.register(invalidEventHandler.getClass()));
    }

    @Test
    @DisplayName(
        "should ignore method with correct method signature but not annotated with @EventHandler")
    void test5() {
      var eventHandler =
          new TrackableHandler() {
            @SuppressWarnings("unused")
            public void handle(TestEvent event) {
              track(event);
            }
          };

      DeezpatchEventHandlerRegistry eventHandlerRegistry = buildEventHandlerRegistry(eventHandler);

      eventHandlerRegistry.register(eventHandler.getClass());

      assertTrue(eventHandlerRegistry.getEventHandlersFor(TestEvent.class).isEmpty());
    }

    @Test
    @DisplayName("should throw when a method annotated with @EventHandler does not return void")
    void test6() {
      InvalidReturnTypeEventHandler invalidReturnTypeEventHandler =
          TestEventHandlers.invalidReturnTypeEventHandler();

      DeezpatchEventHandlerRegistry eventHandlerRegistry =
          buildEventHandlerRegistry(invalidReturnTypeEventHandler);

      assertThrows(
          IllegalArgumentException.class,
          () -> eventHandlerRegistry.register(invalidReturnTypeEventHandler.getClass()));
    }
  }

  @Nested
  class GetEventHandlersForMethod {
    @Test
    @DisplayName("should throw when event type argument is null")
    void test1() {
      TestEventHandler eventHandler = TestEventHandlers.testEventHandler();
      DeezpatchEventHandlerRegistry eventHandlerRegistry =
          buildEventHandlerRegistry(eventHandler).register(eventHandler.getClass());

      assertThrows(
          NullPointerException.class, () -> eventHandlerRegistry.getEventHandlersFor(null));
    }

    @Test
    @DisplayName("should return registered event handler for event type")
    void test2() {
      TestEventHandler eventHandler = TestEventHandlers.testEventHandler();
      DeezpatchEventHandlerRegistry eventHandlerRegistry =
          buildEventHandlerRegistry(eventHandler).register(eventHandler.getClass());

      List<RegisteredEventHandler<TestEvent>> resolved =
          eventHandlerRegistry.getEventHandlersFor(TestEvent.class);

      assertNotNull(resolved);
      assertFalse(resolved.isEmpty());

      var testEvent = new TestEvent("Test");
      resolved.forEach(h -> h.invoke(testEvent));

      assertTrue(eventHandler.hasHandled(testEvent));
    }

    @Test
    @DisplayName(
        "should return empty list when there is no registered event handler for event type")
    void test3() {
      DeezpatchEventHandlerRegistry eventHandlerRegistry = buildEventHandlerRegistry();

      // No registrations...

      List<RegisteredEventHandler<EventWithNoHandlers>> resolved =
          eventHandlerRegistry.getEventHandlersFor(EventWithNoHandlers.class);

      assertNotNull(resolved);
      assertTrue(resolved.isEmpty());
    }
  }

  private DeezpatchEventHandlerRegistry buildEventHandlerRegistry(Object... eventHandlers) {
    return new DeezpatchEventHandlerRegistry(TestInstanceProviders.of(eventHandlers), Set.of());
  }

  private DeezpatchEventHandlerRegistry buildEventHandlerRegistry(
      Set<Class<? extends Annotation>> eventHandlerAnnotations,  
      Object... eventHandlers) {
    return new DeezpatchEventHandlerRegistry(
        TestInstanceProviders.of(eventHandlers), eventHandlerAnnotations);
  }

  public static class EventWithNoHandlers implements Event {}
}
