package io.github.joeljeremy.deezpatch.core.testfixtures;

import io.github.joeljeremy.deezpatch.core.EventHandler;
import java.util.concurrent.CountDownLatch;

public class TestEventHandlers {
  private TestEventHandlers() {}

  public static TestEventHandler testEventHandler() {
    return new TestEventHandler();
  }

  public static ThrowingEventHandler throwingEventHandler(RuntimeException toThrow) {
    return new ThrowingEventHandler(toThrow);
  }

  public static NoArgEventHandler noArgEventHandler() {
    return new NoArgEventHandler();
  }

  public static InvalidArgEventHandler invalidArgEventHandler() {
    return new InvalidArgEventHandler();
  }

  public static InvalidReturnTypeEventHandler invalidReturnTypeEventHandler() {
    return new InvalidReturnTypeEventHandler();
  }

  public static CountDownLatchEventHandler countDownLatchEventHandler(
      CountDownLatch countDownLatch) {
    return new CountDownLatchEventHandler(countDownLatch);
  }

  public static class TestEventHandler extends TrackableHandler {
    private TestEventHandler() {}

    @EventHandler
    public void handle1(TestEvent event) {
      track(event);
    }

    @EventHandler
    public void handle2(TestEvent event) {
      track(event);
    }
  }

  public static class ThrowingEventHandler extends TrackableHandler {
    private final RuntimeException toThrow;

    private ThrowingEventHandler(RuntimeException toThrow) {
      this.toThrow = toThrow;
    }

    @EventHandler
    public void handle(TestEvent event) {
      track(event);
      throw toThrow;
    }
  }

  public static class NoArgEventHandler {
    private NoArgEventHandler() {}

    @EventHandler
    public void handle() {
      // Invalid.
    }
  }

  public static class InvalidArgEventHandler {
    private InvalidArgEventHandler() {}

    @EventHandler
    public void handle(String notAnEvent) {
      // Invalid. Parameter is not an event.
    }
  }

  public static class InvalidReturnTypeEventHandler {
    private InvalidReturnTypeEventHandler() {}

    @EventHandler
    public int handle(TestEvent event) {
      return -1;
    }
  }

  public static class CountDownLatchEventHandler extends TrackableHandler {
    private final CountDownLatch countDownLatch;

    private CountDownLatchEventHandler(CountDownLatch countDownLatch) {
      this.countDownLatch = countDownLatch;
    }

    @EventHandler
    public void handle(TestEvent event) {
      track(event);
      countDownLatch.countDown();
    }
  }
}
