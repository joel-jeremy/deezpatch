package io.github.joeljeremy7.deezpatch.core;

/**
 * Marker interface for all requests. For requests to be dispatchable via {@link Dispatcher}, they
 * must implement this interface.
 *
 * @apiNote In cases where no results are expected, {@code Void} must be used as result type e.g.
 *     <blockquote>
 *     <pre>
 * public class CreateMerchant implements Request{@literal <}Void{@literal >} {
 *     private final String merchantId;
 *
 *     public CreateMerchant(String merchantId) {
 *         this.merchantId = merchantId;
 *     }
 *
 *     public String merchantId() {
 *         return merchantId;
 *     }
 * }
 * </pre>
 *     </blockquote>
 *
 * @apiNote Request implementations with type parameters/type variables are not permitted e.g.
 *     <blockquote>
 *     <pre>
 * public class GetMerchantById{@literal <}T{@literal >} implements Request{@literal <}T{@literal >} {
 *     private final String merchantId;
 *
 *     public GetMerchantById(String merchantId) {
 *         this.merchantId = merchantId;
 *     }
 *
 *     public String merchantId() {
 *         return merchantId;
 *     }
 * }
 * </pre>
 *     </blockquote>
 */
public interface Request<R> {}
