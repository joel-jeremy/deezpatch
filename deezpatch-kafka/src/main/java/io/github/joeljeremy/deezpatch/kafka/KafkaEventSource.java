package io.github.joeljeremy.deezpatch.kafka;

import static java.util.Objects.requireNonNull;

import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventSource;
import io.github.joeljeremy.deezpatch.core.Publisher;
import java.time.Duration;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

/** An event source which polls Kafka for events. */
public class KafkaEventSource<K, V extends Event> {
  private final Consumer<K, V> consumer;
  private final Duration consumerPollTimeout;
  private final OffsetCommitStrategy<K, V> offsetCommitStrategy;

  /**
   * {@link KafkaEventSource} builder.
   *
   * <p>A type witness can be used to specify the type arguments:
   *
   * <blockquote>
   *
   * <pre>
   * KafkaEventSource.Builder{@code <}String, MyEvent{@code >} builder =
   *    KafkaEventSource.{@code <}String, MyEvent{@code >}builder();
   * </pre>
   *
   * </blockquote>
   *
   * @param <K> The type of key.
   * @param <V> The type of value.
   * @return The {@link KafkaEventSource} builder.
   */
  public static <K, V extends Event> Builder<K, V> builder() {
    return new Builder<>();
  }

  /**
   * Constructor.
   *
   * @param consumer The Kafka consumer. This consumer must already be fully configured (including
   *     topic subscriptions) and ready to be used for polling.
   * @param consumerPollTimeout The Kafka consumer poll timeout.
   * @param offsetCommitStrategy The offset commit strategy.
   */
  private KafkaEventSource(
      Consumer<K, V> consumer,
      Duration consumerPollTimeout,
      OffsetCommitStrategy<K, V> offsetCommitStrategy) {
    this.consumer = consumer;
    this.consumerPollTimeout = consumerPollTimeout;
    this.offsetCommitStrategy = offsetCommitStrategy;
  }

  /**
   * Poll Kafka for events.
   *
   * @param publisher The publisher to publish received events with.
   */
  @EventSource
  public void poll(Publisher publisher) {
    while (!Thread.currentThread().isInterrupted()) {
      ConsumerRecords<K, V> records = consumer.poll(consumerPollTimeout);
      for (ConsumerRecord<K, V> record : records) {
        publisher.publish(record.value());
      }
      if (!records.isEmpty()) {
        offsetCommitStrategy.commit(consumer, records);
      }
    }
  }

  /** The builder for {@link KafkaEventSource}. */
  public static class Builder<K, V extends Event> {
    private Consumer<K, V> consumer;
    private Duration consumerPollTimeout;
    private OffsetCommitStrategy<K, V> offsetCommitStrategy = OffsetCommitStrategy.noop();

    @SuppressWarnings("NullAway.Init")
    private Builder() {}

    /**
     * The Kafka consumer.
     *
     * @apiNote The consumer must already be fully configured (including topic subscriptions) and
     *     ready to be used for polling.
     * @apiNote Closing of the consumer will not be handled by the event source. The calling code
     *     shall take care of closing consumer instances (ideally automatically via DI frameworks).
     * @param consumer The Kafka consumer.
     * @return This builder.
     */
    public Builder<K, V> consumer(Consumer<K, V> consumer) {
      this.consumer = requireNonNull(consumer);
      return this;
    }

    /**
     * The Kafka consumer poll timeout.
     *
     * @param consumerPollTimeout The Kafka consumer poll timeout.
     * @return This builder.
     */
    public Builder<K, V> consumerPollTimeout(Duration consumerPollTimeout) {
      this.consumerPollTimeout = requireNonNull(consumerPollTimeout);
      return this;
    }

    /**
     * The offset commit strategy to handle offset commits in case auto offset commit is disabled
     * for the consumer. This will get invoked on every consumer poll (after all records/events have
     * been published). By default, this is set to a no-op implementation which aligns with the
     * default Kafka consumer behaviour that has auto offset commit enabled.
     *
     * @param offsetCommitStrategy The offset commit strategy.
     * @return This builder.
     */
    public Builder<K, V> offsetCommitStrategy(OffsetCommitStrategy<K, V> offsetCommitStrategy) {
      this.offsetCommitStrategy = requireNonNull(offsetCommitStrategy);
      return this;
    }

    /**
     * Build the {@link KafkaEventSource}.
     *
     * @return The {@link KafkaEventSource}.
     */
    public KafkaEventSource<K, V> build() {
      if (consumer == null) {
        throw new IllegalStateException("Kafka consumer is required.");
      }
      if (consumerPollTimeout == null) {
        throw new IllegalStateException("Kafka consumer poll timeout is required.");
      }

      return new KafkaEventSource<>(consumer, consumerPollTimeout, offsetCommitStrategy);
    }
  }

  /** An offset commit strategy in case auto offset commit is disabled for the consumer. */
  public interface OffsetCommitStrategy<K, V extends Event> {
    /**
     * A no-op {@link OffsetCommitStrategy} implementation.
     *
     * @param <K> The type of key.
     * @param <V> The type of value.
     * @return A no-op {@link OffsetCommitStrategy} implementation.
     */
    static <K, V extends Event> OffsetCommitStrategy<K, V> noop() {
      return new NoopOffsetCommitStrategy<>();
    }

    /**
     * Handle offset commit.
     *
     * @param consumer The Kafka consumer.
     * @param records The last polled consumer records.
     */
    void commit(Consumer<K, V> consumer, ConsumerRecords<K, V> records);
  }

  /** A no-op {@link OffsetCommitStrategy} implementation. */
  private static class NoopOffsetCommitStrategy<K, V extends Event>
      implements OffsetCommitStrategy<K, V> {
    /** Do nothing. */
    @Override
    public void commit(Consumer<K, V> consumer, ConsumerRecords<K, V> records) {
      // No-op.
    }
  }
}
