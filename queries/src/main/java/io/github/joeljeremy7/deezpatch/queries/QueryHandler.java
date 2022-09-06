package io.github.joeljeremy7.deezpatch.queries;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Methods annotated with this annotation are registered as query handlers.
 * 
 * @implSpec The annotated methods must have a non-{@code void} return type 
 * and must accept a single method parameter which is the query object.
 * 
 * <p>Examples:</p>
 * 
 * <blockquote><pre>
 * public class QueryHandlers {
 *     {@code @}QueryHandler
 *     public Merchant handle(GetMerchantById query) {
 *         return merchantRepository.getById(query.merchantId());
 *     }
 * 
 *     {@code @}QueryHandler
 *     public Merchant handle(GetMerchantByName query) {
 *         return merchantRepository.getByName(query.merchantName());
 *     }
 * }
 * </pre></blockquote>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface QueryHandler {}
