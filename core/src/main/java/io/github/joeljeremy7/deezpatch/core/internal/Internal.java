package io.github.joeljeremy7.deezpatch.core.internal;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotated elements must not be used by any external code.
 *
 * <h2>Annotated elements might be changed or removed without prior notice.</h2>
 */
@Documented
@Target({TYPE, PACKAGE})
@Retention(RUNTIME)
@Internal
public @interface Internal {}
