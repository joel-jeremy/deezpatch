package io.github.joeljeremy.deezpatch.core;

/**
 * Marker interface to represent a request.
 *
 * <h3>Requests can have a result and the result type is determined by {@link TResult} e.g.</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class GetOrderById implements Request{@literal <}Order{@literal >} {
 *   private final String orderId;
 *
 *   public GetOrderById(String orderId) {
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
 * <h3>In cases where no results are expected, {@code Void} must be used as result type e.g.</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class PlaceOrder implements Request{@literal <}Void{@literal >} {
 *   private final OrderItems orderItems;
 *
 *   public PlaceOrder(OrderItems orderItems) {
 *     this.orderItems = orderItems;
 *   }
 *
 *   public OrderItems orderItems() {
 *     return orderItems;
 *   }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * <h3>Request implementations with type parameters/type variables are not permitted e.g.</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class GetOrderById{@literal <}T{@literal >} implements Request{@literal <}T{@literal >} {
 *   private final String orderId;
 *
 *   public GetOrderById(String orderId) {
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
public interface Request<TResult> {}
