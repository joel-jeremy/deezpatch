package io.github.joeljeremy.deezpatch.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Methods annotated with this annotation are registered as {@link Event} handlers.
 *
 * <h3>Given the following events:</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class OrderPlaced implements Event {
 *   private final String orderId;
 *
 *   public OrderPlaced(String orderId) {
 *     this.orderId = orderId;
 *   }
 *
 *   public String orderId() {
 *     return orderId;
 *   }
 * }
 *
 * public class OrderCancelled implements Event {
 *   private final String orderId;
 *
 *   public OrderCancelled(String orderId) {
 *     this.orderId = orderId;
 *   }
 *
 *   public String orderId() {
 *     return orderId;
 *   }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * <h3>Valid event handlers are:</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class EventHandlers {
 *   {@code @}EventHandler
 *   public void handle(OrderPlaced event) {
 *     {@code // Handle}
 *   }
 *
 *   {@code @}EventHandler
 *   public void handle(OrderCancelled event) {
 *     {@code // Handle}
 *   }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @implSpec The annotated methods accept a single method parameter which is the {@link Event}
 *     object. The method's return type must be {@code void}.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {}
