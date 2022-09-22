package io.github.joeljeremy7.deezpatch.core;

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
 * public class CreateMerchant implements Request{@code <}Void{@code >} {
 *   private final String merchantName;
 *
 *   public CreateMerchant(String merchantName) {
 *     this.merchantName = merchantName;
 *   }
 *
 *   public String merchantName() {
 *     return merchantName;
 *   }
 * }
 *
 * public class GetMerchantById implements Request{@code <}Merchant{@code >} {
 *   private final String merchantId;
 *
 *   public GetMerchantById(String merchantId) {
 *     this.merchantId = merchantId;
 *   }
 *
 *   public String merchantId() {
 *     return merchantId;
 *   }
 * }
 *
 * public class GetMerchantCount implements Request{@code <}Integer{@code >} {}
 *     </pre>
 *
 * </blockquote>
 *
 * <h3>Valid request handlers are:</h3>
 *
 * <blockquote>
 *
 * <pre>
 * public class RequestHandlers {
 *   {@code // CreateMerchant request has Void result type.}
 *   {@code // Method return type must either be Void or void.}
 *   {@code // (void is recommended since you won't need to return null.)}
 *   {@code @}RequestHandler
 *   public void handle(CreateMerchant request) {
 *     merchantRepository.save(createMerchant(request));
 *   }
 *
 *   {@code // GetMerchantById request has Merchant result type.}
 *   {@code // Method return type must be Merchant.}
 *   {@code @}RequestHandler
 *   public Merchant handle(GetMerchantById request) {
 *     return merchantRepository.getById(request.merchantId());
 *   }
 *
 *   {@code // GetMerchantCount request has Integer result type.}
 *   {@code // Method return type must either be Integer or int.}
 *   {@code @}RequestHandler
 *   public int handle(GetMerchantCount request) {
 *     return merchantRepository.count();
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
