package io.github.joeljeremy.deezpatch.core.internal;

import io.github.joeljeremy.deezpatch.core.EventHandler;
import java.lang.invoke.LambdaMetafactory;

/** Functional interface used in building lambdas via {@link LambdaMetafactory}. */
@FunctionalInterface
@Internal
public interface EventHandlerMethod {
  /**
   * Invoke the actual method annotated with {@link EventHandler}.
   *
   * @param eventHandlerInstance The event handler instance.
   * @param event The published event.
   */
  void invoke(Object eventHandlerInstance, Object event);
}
