package io.github.joeljeremy7.deezpatch.core;

/**
 * Marker interface for all requests. For requests to be dispatchable via {@link Dispatcher}, they
 * must implement this interface.
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
 * public class CreateOrder implements Request{@literal <}Void{@literal >} {
 *   private final String orderId;
 *
 *   public CreateOrder(String orderId) {
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
