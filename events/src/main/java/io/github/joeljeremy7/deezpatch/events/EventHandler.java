package io.github.joeljeremy7.deezpatch.events;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Methods annotated with this annotation are registered as event handlers.
 * 
 * @implSpec The annotated methods must have a {@code void} return type and 
 * must accept a single method parameter which is the event object.
 * 
 * <p>Examples:</p>
 * 
 * <blockquote><pre>
 * public class EventHandlers {
 *     {@code @}EventHandler
 *     public void handle(EventOne event) {
 *         {@code // Handle}
 *     }
 * 
 *     {@code @}EventHandler
 *     public void handle(EventTwo event) {
 *         {@code // Handle}
 *     }
 * }
 * </pre></blockquote>
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface EventHandler {}
