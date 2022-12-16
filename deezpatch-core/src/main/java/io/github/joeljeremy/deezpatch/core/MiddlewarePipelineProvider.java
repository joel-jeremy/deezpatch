package io.github.joeljeremy.deezpatch.core;

import io.github.joeljeremy.deezpatch.core.internal.registries.DeezpatchMiddlewareRegistry.MiddlewarePipelineFactory;

public interface MiddlewarePipelineProvider {
  <T extends Request<R>, R> MiddlewarePipelineFactory<R> getPipelineFactoryFor(
      RequestKey<T, R> requestKey);
}
