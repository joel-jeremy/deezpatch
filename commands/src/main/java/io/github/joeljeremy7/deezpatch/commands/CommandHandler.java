package io.github.joeljeremy7.deezpatch.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Methods annotated with this annotation are registered as command handlers.
 * 
 * @implSpec The annotated methods must have a {@code void} return type and 
 * must accept a single method parameter which is the command object.
 * 
 * <p>Examples:</p>
 * 
 * <blockquote><pre>
 * public class CommandHandlers {
 *     {@code @}CommandHandler
 *     public void handle(CommandOne command) {
 *         {@code // Handle}
 *     }
 * 
 *     {@code @}CommandHandler
 *     public void handle(CommandTwo command) {
 *         {@code // Handle}
 *     }
 * }
 * </pre></blockquote>
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface CommandHandler {}
