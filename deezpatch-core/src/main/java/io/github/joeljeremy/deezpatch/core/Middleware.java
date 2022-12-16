package io.github.joeljeremy.deezpatch.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Methods annotated with this annotation are registered as middlewares. Middlewares are configured
 * as part of pipelines through {@link MiddlewarePipeline}.
 *
 * <p>TODO: Examples
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
  /** The next middleware in the pipeline. */
  static interface Next<R> {
    /**
     * Invoke the next middleware in the pipeline.
     *
     * @return The result of the next middleware.
     */
    Optional<R> invoke();
  }
}
