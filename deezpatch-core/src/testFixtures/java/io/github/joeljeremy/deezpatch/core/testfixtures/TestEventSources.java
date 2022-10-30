package io.github.joeljeremy.deezpatch.core.testfixtures;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventSource;
import io.github.joeljeremy.deezpatch.core.Publisher;
import io.github.joeljeremy.deezpatch.core.internal.DaemonThreadFactory;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TestEventSources {
  public static TestScheduledEventSource testScheduledEventSource() {
    return testScheduledEventSource(Duration.ofSeconds(1), () -> new TestEvent("event"));
  }

  public static TestScheduledEventSource testScheduledEventSource(
      Duration eventInterval, Supplier<? extends Event> eventSupplier) {
    return new TestScheduledEventSource(eventInterval, eventSupplier);
  }

  public static NoArgEventSource noArgEventSource() {
    return new NoArgEventSource();
  }

  public static InvalidReturnTypeEventSource invalidReturnTypeEventSource() {
    return new InvalidReturnTypeEventSource();
  }

  public static InvalidArgEventSource invalidArgEventSource() {
    return new InvalidArgEventSource();
  }

  public static ThrowingEventSource throwingEventSource(RuntimeException toThrow) {
    return new ThrowingEventSource(toThrow);
  }

  public static DelegatingEventSource delegatingEventSource(Consumer<Publisher> delegate) {
    return new DelegatingEventSource(delegate);
  }

  public static class TestScheduledEventSource {
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
        Executors.newSingleThreadScheduledExecutor(DaemonThreadFactory.INSTANCE);

    private final Duration eventInterval;
    private final Supplier<? extends Event> eventSupplier;

    private TestScheduledEventSource(
        Duration eventInterval, Supplier<? extends Event> eventSupplier) {
      this.eventInterval = eventInterval;
      this.eventSupplier = eventSupplier;
    }

    @EventSource
    public void scheduledPublish(Publisher publisher) {
      SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(
          () -> {
            publisher.publish(eventSupplier.get());
          },
          0,
          eventInterval.toMillis(),
          TimeUnit.MILLISECONDS);
    }
  }

  public static class NoArgEventSource {
    private NoArgEventSource() {}

    @EventSource
    public void noArg() {
      // Invalid.
    }
  }

  public static class InvalidReturnTypeEventSource {
    private InvalidReturnTypeEventSource() {}

    @EventSource
    public int invalidReturnType(Publisher publisher) {
      return -1;
    }
  }

  public static class InvalidArgEventSource {
    private InvalidArgEventSource() {}

    @EventSource
    public void eventSource(String notAPublisher) {
      // Invalid. Parameter is not a Publisher.
    }
  }

  public static class ThrowingEventSource {
    private final RuntimeException toThrow;

    private ThrowingEventSource(RuntimeException toThrow) {
      this.toThrow = toThrow;
    }

    @EventSource
    public void willThrow(Publisher publisher) {
      throw toThrow;
    }
  }

  public static class DelegatingEventSource {
    private final Consumer<Publisher> delegate;

    private DelegatingEventSource(Consumer<Publisher> delegate) {
      this.delegate = delegate;
    }

    @EventSource
    public void delegated(Publisher publisher) {
      delegate.accept(publisher);
    }
  }
}
