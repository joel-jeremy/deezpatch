package io.github.joeljeremy.deezpatch.kafka;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.Publisher;
import io.github.joeljeremy.deezpatch.core.internal.DaemonThreadFactory;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestEvent;
import io.github.joeljeremy.deezpatch.kafka.KafkaEventSource.OffsetCommitStrategy;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class KafkaEventSourceTests {
  static final String KAFKA_TOPIC = "deezpatch_kafka_topic";
  static final int KAFKA_PARTITION = 1;
  static final TopicPartition KAFKA_TOPIC_PARTITION =
      new TopicPartition(KAFKA_TOPIC, KAFKA_PARTITION);
  static final long KAFKA_BEGINNING_OFFSET = 1;

  @Nested
  class PollMethod {
    @Test
    @DisplayName("should publish received Kafka events")
    void test1() throws InterruptedException {
      var testEvent = new TestEvent("Deezpatch");
      var testConsumerRecord =
          new ConsumerRecord<Void, TestEvent>(
              KAFKA_TOPIC_PARTITION.topic(),
              KAFKA_TOPIC_PARTITION.partition(),
              KAFKA_BEGINNING_OFFSET,
              null,
              testEvent);

      var mockConsumer =
          mockKafkaConsumer(KAFKA_TOPIC_PARTITION, KAFKA_BEGINNING_OFFSET, testConsumerRecord);

      KafkaEventSource<Void, TestEvent> kafkaEventSource =
          KafkaEventSource.<Void, TestEvent>builder()
              .consumer(mockConsumer)
              .consumerPollTimeout(Duration.ofMillis(100))
              .build();

      var countDownLatch = new CountDownLatch(1);
      var publishedEventRef = new AtomicReference<Event>();
      Publisher mockPublisher =
          new Publisher() {
            @Override
            public <T extends Event> void publish(T event) {
              publishedEventRef.set(event);
              countDownLatch.countDown();
            }
          };

      // Need to run in a separate thread because poll method blocks indefinitely.
      ExecutorService executor = newSingleThreadExecutor();
      executor.execute(() -> kafkaEventSource.poll(mockPublisher));

      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
      assertEquals(testEvent, publishedEventRef.get());
    }

    @Test
    @DisplayName("should stop polling when thread is interrupted")
    void test2() throws InterruptedException {
      var testEvent = new TestEvent("Deezpatch");
      var testConsumerRecord =
          new ConsumerRecord<Void, TestEvent>(
              KAFKA_TOPIC_PARTITION.topic(),
              KAFKA_TOPIC_PARTITION.partition(),
              KAFKA_BEGINNING_OFFSET,
              null,
              testEvent);

      var mockConsumer =
          mockKafkaConsumer(KAFKA_TOPIC_PARTITION, KAFKA_BEGINNING_OFFSET, testConsumerRecord);

      KafkaEventSource<Void, TestEvent> kafkaEventSource =
          KafkaEventSource.<Void, TestEvent>builder()
              .consumer(mockConsumer)
              .consumerPollTimeout(Duration.ofMillis(100))
              .build();

      var countDownLatch = new CountDownLatch(1);
      var publishedEventRef = new AtomicReference<Event>();
      Publisher mockPublisher =
          new Publisher() {
            @Override
            public <T extends Event> void publish(T event) {
              publishedEventRef.set(event);
              countDownLatch.countDown();
            }
          };

      // Need to run in a separate thread because poll method blocks indefinitely.
      ExecutorService executor = newSingleThreadExecutor();
      Future<?> future = executor.submit(() -> kafkaEventSource.poll(mockPublisher));

      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
      assertEquals(testEvent, publishedEventRef.get());

      assertTimeoutPreemptively(
          Duration.ofSeconds(10),
          () -> {
            while (!future.isDone()) {
              // Cancel. This should interrupt thread the event source is running in.
              future.cancel(true);
            }
          });
    }
  }

  @Nested
  class BuilderTests {
    @Nested
    class ConsumerMethod {
      @Test
      @DisplayName("should throw when consumer argument is null")
      void test1() {
        var builder = KafkaEventSource.<Void, TestEvent>builder();
        assertThrows(NullPointerException.class, () -> builder.consumer(null));
      }
    }

    @Nested
    class ConsumerPollTimeoutMethod {
      @Test
      @DisplayName("should throw when consumer poll timeout argument is null")
      void test1() {
        var builder = KafkaEventSource.<Void, TestEvent>builder();
        assertThrows(NullPointerException.class, () -> builder.consumerPollTimeout(null));
      }
    }

    @Nested
    class OffsetComitStrategyMethod {
      @Test
      @DisplayName("should throw when offset commit strategy argument is null")
      void test1() {
        var builder = KafkaEventSource.<Void, TestEvent>builder();
        assertThrows(NullPointerException.class, () -> builder.offsetCommitStrategy(null));
      }
    }

    @Nested
    class BuildMethod {
      @Test
      @DisplayName("should throw when no consumer is provided")
      void test1() {
        KafkaEventSource.Builder<Void, TestEvent> builder =
            KafkaEventSource.<Void, TestEvent>builder()
                .consumerPollTimeout(Duration.ofSeconds(1))
                .offsetCommitStrategy(OffsetCommitStrategy.noop());

        assertThrows(IllegalStateException.class, () -> builder.build());
      }

      @Test
      @DisplayName("should throw when no consumer poll duration timeout is provided")
      void test2() {
        KafkaEventSource.Builder<Void, TestEvent> builder =
            KafkaEventSource.<Void, TestEvent>builder()
                .consumer(new MockConsumer<>(OffsetResetStrategy.EARLIEST))
                .offsetCommitStrategy(OffsetCommitStrategy.noop());

        assertThrows(IllegalStateException.class, () -> builder.build());
      }
    }
  }

  @Nested
  class OffsetCommitStrategyTests {
    @Nested
    class NoopFactoryMethod {
      @Test
      @DisplayName("should never return null")
      void test1() {
        assertNotNull(OffsetCommitStrategy.noop());
      }
    }
  }

  @SafeVarargs
  static <K, V extends Event> MockConsumer<K, V> mockKafkaConsumer(
      TopicPartition topicPartition, long beginningOffset, ConsumerRecord<K, V>... records) {
    var mockConsumer = new MockConsumer<K, V>(OffsetResetStrategy.EARLIEST);
    mockConsumer.assign(List.of(topicPartition));
    mockConsumer.updateBeginningOffsets(Map.of(topicPartition, beginningOffset));
    for (ConsumerRecord<K, V> record : records) {
      mockConsumer.addRecord(record);
    }
    return mockConsumer;
  }

  static ExecutorService newSingleThreadExecutor() {
    return Executors.newSingleThreadExecutor(DaemonThreadFactory.INSTANCE);
  }
}
