package io.github.joeljeremy.emissary.core;

/**
 * Marker interface to represent an event.
 *
 * <p>Example:
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
 * </pre>
 *
 * </blockquote>
 */
public interface Event {}
