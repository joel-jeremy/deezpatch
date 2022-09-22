package io.github.joeljeremy7.deezpatch.core;

/**
 * Marker interface for all events. For events to be dispatchable via {@link Publisher}, they must
 * implement this interface.
 *
 * <p>Example:
 *
 * <blockquote>
 *
 * <pre>
 * public class MerchantCreated implements Event {
 *   private final String merchantId;
 *
 *   public MerchantCreated(String merchantId) {
 *     this.merchantId = merchantId;
 *   }
 *
 *   public String merchantId() {
 *     return merchantId;
 *   }
 * }
 * </pre>
 *
 * </blockquote>
 */
public interface Event {}
