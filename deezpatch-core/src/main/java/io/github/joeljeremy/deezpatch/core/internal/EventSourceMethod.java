package io.github.joeljeremy.deezpatch.core.internal;

import io.github.joeljeremy.deezpatch.core.EventSource;
import java.lang.invoke.LambdaMetafactory;

/** Functional interface used in building lambdas via {@link LambdaMetafactory}. */
@FunctionalInterface
@Internal
public interface EventSourceMethod {
  /**
   * Invoke the actual method annotated with {@link EventSource}.
   *
   * @param eventSourceInstance The event source class instance.
   * @param publisher The publisher.
   */
  void invoke(Object eventSourceInstance, Object publisher);
}
