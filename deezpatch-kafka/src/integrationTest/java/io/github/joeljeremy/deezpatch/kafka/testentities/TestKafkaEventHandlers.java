package io.github.joeljeremy.deezpatch.kafka.testentities;

import io.github.joeljeremy.deezpatch.core.EventHandler;
import java.util.concurrent.CountDownLatch;

public class TestKafkaEventHandlers {
  public static CountDownLatchEventHandler countDownLatchEventHandler(
      CountDownLatch countDownLatch) {
    return new CountDownLatchEventHandler(countDownLatch);
  }

  public static class CountDownLatchEventHandler {
    private final CountDownLatch countDownLatch;

    public CountDownLatchEventHandler(CountDownLatch countDownLatch) {
      this.countDownLatch = countDownLatch;
    }

    @EventHandler
    public void handle(TestKafkaEvent event) {
      countDownLatch.countDown();
    }
  }
}
