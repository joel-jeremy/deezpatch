package io.github.joeljeremy7.deezpatch.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Methods annotated with this annotation are registered as {@link Event} 
 * handlers.
 * 
 * @implSpec The annotated methods accept a single method parameter which is the 
 * {@link Event} object. The method's return type must be {@code void}.
 * 
 * <h3>Given the following events:</h3>
 * 
 * <blockquote><pre>
 * public class MerchantCreated implements Event {
 *     private final String merchantId;
 * 
 *     public MerchantCreated(String merchantId) {
 *         this.merchantId = merchantId;
 *     }
 * 
 *     public String merchantId() {
 *         return merchantId;
 *     }
 * }
 * 
 * public class MerchantSuspended implements Event {
 *     private final String merchantId;
 * 
 *     public MerchantCreated(String merchantId) {
 *         this.merchantId = merchantId;
 *     }
 * 
 *     public String merchantId() {
 *         return merchantId;
 *     }
 * }
 * </pre></blockquote>
 * 
 * 
 * <h3>Valid event handlers are:</h3>
 * 
 * <blockquote><pre>
 * public class EventHandlers {
 *     {@code @}EventHandler
 *     public void handle(MerchantCreated event) {
 *         {@code // Handle}
 *     }
 * 
 *     {@code @}EventHandler
 *     public void handle(MerchantSuspended event) {
 *         {@code // Handle}
 *     }
 * }
 * </pre></blockquote>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface EventHandler {}
