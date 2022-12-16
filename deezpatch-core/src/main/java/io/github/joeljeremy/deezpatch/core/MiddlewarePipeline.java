package io.github.joeljeremy.deezpatch.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotations annotated with this annotation are registered as middleware pipelines. */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MiddlewarePipeline {
  /**
   * The middlewares.
   *
   * @return The middlewares.
   */
  Class<?>[] value();
}
