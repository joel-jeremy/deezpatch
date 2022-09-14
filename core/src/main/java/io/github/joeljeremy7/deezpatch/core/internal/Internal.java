package io.github.joeljeremy7.deezpatch.core.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotated elements must not be used by any external code. 
 * They might be changed or removed without prior notice.
 */
@Target({ TYPE, PACKAGE })
@Retention(RUNTIME)
public @interface Internal {}