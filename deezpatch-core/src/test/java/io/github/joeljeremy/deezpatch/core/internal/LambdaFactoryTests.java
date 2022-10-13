package io.github.joeljeremy.deezpatch.core.internal;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.DeezpatchException;
import io.github.joeljeremy.deezpatch.core.testentities.TestRequest;
import io.github.joeljeremy.deezpatch.core.testentities.TestRequestHandlers;
import io.github.joeljeremy.deezpatch.core.testentities.TestRequestHandlers.TestRequestHandler;
import io.github.joeljeremy.deezpatch.core.testentities.TestResult;
import java.lang.reflect.Method;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LambdaFactoryTests {
  @Nested
  class CreateLambdaFunctionMethod {
    @Test
    @DisplayName("should throw when functional interface argument is not a functional interface")
    void test1() throws Throwable {
      Method handlerMethod = TestRequestHandler.class.getMethod("handle", TestRequest.class);

      assertThrows(
          IllegalArgumentException.class,
          () -> LambdaFactory.createLambdaFunction(handlerMethod, NotAFunctionalInterface.class));
    }

    @Test
    @DisplayName(
        "should throw when functional interface argument is not compatible with the target method "
            + "e.g. not the same method signature")
    void test2() throws Throwable {
      Method handlerMethod = TestRequestHandler.class.getMethod("handle", TestRequest.class);

      assertThrows(
          DeezpatchException.class,
          () -> LambdaFactory.createLambdaFunction(handlerMethod, Supplier.class));
    }

    @Test
    @DisplayName("should create a lambda function targeting the target method")
    void test3() throws Throwable {
      var handler = TestRequestHandlers.testRequestHandler();
      Method handlerMethod = handler.getClass().getMethod("handle", TestRequest.class);
      RequestHandlerMethod lambda =
          LambdaFactory.createLambdaFunction(handlerMethod, RequestHandlerMethod.class);

      assertNotNull(lambda);

      // Invoke and verify.
      var request = new TestRequest("Test");
      var result = lambda.invoke(handler, request);

      assertTrue(handler.hasHandled(request));
      assertNotNull(result);
      assertInstanceOf(TestResult.class, result);
    }
  }

  static interface NotAFunctionalInterface extends RequestHandlerMethod {
    // Additional method so it will be an invalid functional interface.
    void notAFunctionalInterface();
  }
}
