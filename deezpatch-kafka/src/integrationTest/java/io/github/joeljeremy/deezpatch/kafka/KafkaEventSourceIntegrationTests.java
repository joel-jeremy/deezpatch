package io.github.joeljeremy.deezpatch.kafka;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.joeljeremy.deezpatch.core.Deezpatch;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestInstanceProviders;
import io.github.joeljeremy.deezpatch.kafka.testentities.TestKafkaEvent;
import io.github.joeljeremy.deezpatch.kafka.testentities.TestKafkaEventHandlers;
import io.github.joeljeremy.deezpatch.kafka.testentities.TestKafkaEventSerde;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public class KafkaEventSourceIntegrationTests {
  private static final Logger LOGGER =
      System.getLogger(KafkaEventSourceIntegrationTests.class.getName());
  private static final String KAFKA_TOPIC = "deezpatch-kafka-topic";
  private static final String KAFKA_CONSUMER_GROUP = "deezpatch-kafka-consumer-group";
  private static final Serde<Void> VOID_SERDE = Serdes.Void();
  private static final Serde<TestKafkaEvent> TEST_KAFKA_EVENT_SERDE = new TestKafkaEventSerde();
  static final DockerImageName KAFKA_DOCKER_IMAGE =
      DockerImageName.parse("confluentinc/cp-kafka:7.2.2");

  @Container
  static final KafkaContainer KAFKA_CONTAINER =
      new KafkaContainer(KAFKA_DOCKER_IMAGE)
          .withLogConsumer(o -> LOGGER.log(Level.INFO, o.getUtf8String()));

  static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @BeforeAll
  static void setup() throws Exception {
    LOGGER.log(Level.INFO, "Bootstrap servers={0}", KAFKA_CONTAINER.getBootstrapServers());
    LOGGER.log(Level.INFO, "First mapped port={0}", KAFKA_CONTAINER.getFirstMappedPort());
    LOGGER.log(Level.INFO, "Host={0}", KAFKA_CONTAINER.getHost());
  }

  @Nested
  class PollMethod {
    @Test
    @DisplayName("should consume and publish events from Kafka")
    void test1() throws Exception {
      var countDownLatch = new CountDownLatch(1);
      var eventHandler = TestKafkaEventHandlers.countDownLatchEventHandler(countDownLatch);
      var kafkaEventSource = newKafkaEventSource();

      Deezpatch.Builder builder =
          Deezpatch.builder()
              .instanceProvider(TestInstanceProviders.of(kafkaEventSource, eventHandler))
              .events(
                  config ->
                      config
                          .eventHandlers(eventHandler.getClass())
                          .eventSources(kafkaEventSource.getClass()));

      // Event sources get started on build.
      builder.build();

      produceKafkaEvent(new TestKafkaEvent("event"));

      assertTrue(countDownLatch.await(10, TimeUnit.SECONDS));
    }
  }

  static KafkaEventSource<Void, TestKafkaEvent> newKafkaEventSource() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_CONSUMER_GROUP);

    var consumer =
        new KafkaConsumer<>(
            props, VOID_SERDE.deserializer(), TEST_KAFKA_EVENT_SERDE.deserializer());

    consumer.subscribe(List.of(KAFKA_TOPIC));

    return KafkaEventSource.<Void, TestKafkaEvent>builder()
        .consumer(consumer)
        .consumerPollTimeout(Duration.ofSeconds(1))
        .build();
  }

  static KafkaProducer<Void, TestKafkaEvent> newKafkaProducer() {
    Map<String, Object> producerConfigs =
        Map.of(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers());

    return new KafkaProducer<>(
        producerConfigs, VOID_SERDE.serializer(), TEST_KAFKA_EVENT_SERDE.serializer());
  }

  static void produceKafkaEvent(TestKafkaEvent event) {
    ProducerRecord<Void, TestKafkaEvent> producerRecord = new ProducerRecord<>(KAFKA_TOPIC, event);
    KafkaProducer<Void, TestKafkaEvent> kafkaProducer = newKafkaProducer();
    kafkaProducer.send(producerRecord);
    kafkaProducer.flush();
    kafkaProducer.close();
  }
}
