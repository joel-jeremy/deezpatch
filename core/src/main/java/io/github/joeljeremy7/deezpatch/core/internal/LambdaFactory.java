package io.github.joeljeremy7.deezpatch.core.internal;

import io.github.joeljeremy7.deezpatch.core.DeezpatchException;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.stream.Stream;

/** Utility to create lambda functions using {@code LambdaMetafactory}. */
@Internal
public class LambdaFactory {

  private static final FunctionalInterfaceMethodMap FUNCTIONAL_INTERFACE_METHOD_MAP =
      new FunctionalInterfaceMethodMap();

  private LambdaFactory() {}

  /**
   * Create a lambda function using {@code LambdaMetafactory}.
   *
   * @param <T> The functional interface.
   * @param targetMethod The method which will be targeted by the lambda function.
   * @param functionalInterface The interface to serve as the functional interface.
   * @return The instantiated lambda function which targets the specified target method.
   */
  public static <T> T createLambdaFunction(Method targetMethod, Class<T> functionalInterface) {
    Method samMethod = FUNCTIONAL_INTERFACE_METHOD_MAP.get(functionalInterface);

    try {
      Class<?> requestHandlerClass = targetMethod.getDeclaringClass();

      MethodHandles.Lookup lookup = MethodHandles.lookup();
      MethodHandle requestHandlerMethodHandle =
          lookup.in(requestHandlerClass).unreflect(targetMethod);

      MethodType instantiatedMethodType =
          MethodType.methodType(
              targetMethod.getReturnType(), requestHandlerClass, targetMethod.getParameterTypes());

      MethodType samMethodType =
          MethodType.methodType(samMethod.getReturnType(), samMethod.getParameterTypes());

      CallSite callSite =
          LambdaMetafactory.metafactory(
              lookup,
              samMethod.getName(),
              MethodType.methodType(functionalInterface),
              samMethodType,
              requestHandlerMethodHandle,
              instantiatedMethodType);

      return (T) callSite.getTarget().invoke();
    } catch (Throwable ex) {
      throw new DeezpatchException(
          "Failed to build lambda function ("
              + functionalInterface.getName()
              + ") targeting method "
              + targetMethod.getName()
              + ".",
          ex);
    }
  }

  private static class FunctionalInterfaceMethodMap extends ClassValue<Method> {
    /** Get the single abstract method (SAM) of the functional interface. */
    @Override
    protected Method computeValue(Class<?> functionalInterface) {
      Method[] methods =
          Stream.of(functionalInterface)
              .filter(Class::isInterface)
              .flatMap(m -> Stream.of(m.getMethods()))
              .filter(m -> Modifier.isAbstract(m.getModifiers()))
              .toArray(Method[]::new);

      if (methods.length != 1) {
        throw new IllegalArgumentException(
            "Class is not a functional interface: " + functionalInterface.getName());
      }

      return methods[0];
    }
  }
}
