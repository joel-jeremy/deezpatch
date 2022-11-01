package io.github.joeljeremy.deezpatch.core;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Methods annotated with this annotation are registered as {@link Request} handlers.
 *
 * <h3>Given the following requests:</h3>
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
 *
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
 *
 * public class GetOrderCount implements Request{@literal <}Integer{@literal >} {}
 * </pre>
 *
 * </blockquote>
 *
 * <h3>Valid request handlers are:</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class RequestHandlers {
 *   {@code // PlaceOrder request has Void result type.}
 *   {@code // Method return type must either be void.}
 *   {@code @}RequestHandler
 *   public void handle(PlaceOrder request) {
 *     placeOrder(request);
 *   }
 *
 *   {@code // GetOrderById request has Order result type.}
 *   {@code // Method return type must be Order.}
 *   {@code @}RequestHandler
 *   public Order handle(GetOrderById request) {
 *     return getOrder(request.orderId());
 *   }
 *
 *   {@code // GetOrderCount request has Integer result type.}
 *   {@code // Method return type must either be Integer or int.}
 *   {@code @}RequestHandler
 *   public int handle(GetOrderCount request) {
 *     return countOrders();
 *   }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @implSpec The annotated methods must accept a single method parameter which is the {@link
 *     Request} object. The method's return type must match the request's result type (primitives
 *     and wrapper types (including {@code Void}/{@code void}) are intercheangable).
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RequestHandler {}
