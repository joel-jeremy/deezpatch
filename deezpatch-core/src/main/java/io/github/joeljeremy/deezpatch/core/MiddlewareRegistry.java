package io.github.joeljeremy.deezpatch.core;

public interface MiddlewareRegistry {
  MiddlewareRegistry register(Class<?>... middlewareClasses);
}
