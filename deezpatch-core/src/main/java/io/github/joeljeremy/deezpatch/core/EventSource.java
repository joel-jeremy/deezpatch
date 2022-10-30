package io.github.joeljeremy.deezpatch.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Methods annotated with this annotation are registered as event sources.
 *
 * <p>Event sources are startable and are started only once upon building the {@link Deezpatch}
 * instance they are registered in. Upon start, an event source may choose to configure where they
 * will get events from such as (but not limited to) polling Kafka, polling AMQP servers, running an
 * HTTP server/endpoint, etc. Event sources are allowed to block the current thread as they are run
 * in a dedicated thread.
 *
 * <h3>Example:</h3>
 *
 * <blockquote>
 *
 * <pre>
 *
 * public class EventSources {
 *   {@code @EventSource}
 *   public void runHttpServer(Publisher publisher) {
 *     {@code // Run a basic HTTP server that accepts events via a POST /events endpoint. }
 *     var httpServer = httpServer(8080);
 *     httpServer.post(
 *         "/events",
 *         (request, response) {@code ->} {
 *           TestEvent event = deserialize(request.getBody());
 *           publisher.publish(event);
 *           sendOkResponse(response);
 *         });
 *     {@code // OK to block the thread.}
 *     httpServer.start();
 *   }
 *
 *   {@code @EventSource}
 *   public void pollKafka(Publisher publisher) {
 *     {@code // Poll Kafka for events. }
 *     {@code // OK to block the thread.}
 *     while (!Thread.currentThread().isInterrupted()) {
 *       ConsumerRecords{@code <}String, MyEvent{@code >} records =
 *           kafkaConsumer.poll(Duration.ofSeconds(1));
 *       for (ConsumerRecord{@code <}String, MyEvent{@code >} record : records) {
 *         publisher.publish(record.value());
 *       }
 *     }
 *   }
 * }
 *
 * </pre>
 *
 * </blockquote>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventSource {}
