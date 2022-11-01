package io.github.joeljeremy.deezpatch.core.internal.registries;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.RegisteredRequestHandler;
import io.github.joeljeremy.deezpatch.core.Request;
import io.github.joeljeremy.deezpatch.core.RequestKey;
import io.github.joeljeremy.deezpatch.core.testfixtures.IntegerRequest;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestInstanceProviders;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.IncompatibleRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.InvalidRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.InvalidRequestHandlerMultipleParams;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.PrimitiveRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.ThrowingVoidRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TestRequestHandlers.VoidRequestHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.TrackableHandler;
import io.github.joeljeremy.deezpatch.core.testfixtures.VoidRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DeezpatchRequestHandlerRegistryTests {
  @Nested
  class Constructors {
    @Test
    @DisplayName("should throw when instance provider argument is null")
    void test1() {
      assertThrows(NullPointerException.class, () -> new DeezpatchRequestHandlerRegistry(null));
    }
  }

  @Nested
  class RegisterMethod {
    @Test
    @DisplayName("should throw when request handler class argument is null")
    void test1() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.primitiveRequestHandler());

      assertThrows(
          NullPointerException.class, () -> requestHandlerRegistry.register((Class<?>[]) null));
    }

    @Test
    @DisplayName("should detect and register methods annotated with @RequestHandler")
    void test2() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.primitiveRequestHandler());

      requestHandlerRegistry.register(PrimitiveRequestHandler.class);

      RequestKey<IntegerRequest, Integer> requestType = new RequestKey<>() {};
      assertTrue(requestHandlerRegistry.getRequestHandlerFor(requestType).isPresent());
    }

    @Test
    @DisplayName(
        "should support methods which declares primitive method return types in lieu of wrapper"
            + " types")
    void test3() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.primitiveRequestHandler());

      requestHandlerRegistry.register(PrimitiveRequestHandler.class);

      RequestKey<IntegerRequest, Integer> requestType = new RequestKey<>() {};
      assertTrue(requestHandlerRegistry.getRequestHandlerFor(requestType).isPresent());
    }

    @Test
    @DisplayName("should support methods which declare (primitive) void method return types")
    void test4() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.voidRequestHandler());

      requestHandlerRegistry.register(VoidRequestHandler.class);

      RequestKey<VoidRequest, Void> requestType = new RequestKey<>() {};
      assertTrue(requestHandlerRegistry.getRequestHandlerFor(requestType).isPresent());
    }

    @Test
    @DisplayName(
        "should throw when a method annotated with @RequestHandler does not have a parameter")
    void test5() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.invalidRequestHandler());

      assertThrows(
          IllegalArgumentException.class,
          () -> requestHandlerRegistry.register(InvalidRequestHandler.class));
    }

    @Test
    @DisplayName(
        "should throw when a method annotated with @RequestHandler has more than one parameter")
    void test6() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.invalidRequestHandlerMultipleParams());

      assertThrows(
          IllegalArgumentException.class,
          () -> requestHandlerRegistry.register(InvalidRequestHandlerMultipleParams.class));
    }

    @Test
    @DisplayName(
        "should throw when there are multiple @RequestHandler methods that handle the same request")
    void test7() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(
              TestRequestHandlers.primitiveRequestHandler(),
              TestRequestHandlers.throwingIntegerRequestHandler(new RuntimeException("Oops!")));

      // Register a TestRequest handler.
      requestHandlerRegistry.register(VoidRequestHandler.class);

      assertThrows(
          UnsupportedOperationException.class,
          // Register another TestRequest handler.
          () -> requestHandlerRegistry.register(ThrowingVoidRequestHandler.class));
    }

    @Test
    @DisplayName(
        "should ignore method with correct method signature but not annotated with @RequestHandler")
    void test8() {
      var requestHandler =
          new TrackableHandler() {
            @SuppressWarnings("unused")
            public int handle(IntegerRequest request) {
              track(request);
              return Integer.parseInt(request.parameter());
            }
          };

      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(requestHandler);

      requestHandlerRegistry.register(requestHandler.getClass());

      RequestKey<IntegerRequest, Integer> requestType = new RequestKey<>() {};

      assertFalse(requestHandlerRegistry.getRequestHandlerFor(requestType).isPresent());
    }

    @Test
    @DisplayName(
        "should throw when result type is the same as the request handler method's return type")
    void test9() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.incompatibleRequestHandler());

      assertThrows(
          UnsupportedOperationException.class,
          () -> requestHandlerRegistry.register(IncompatibleRequestHandler.class));
    }
  }

  @Nested
  class GetRequestHandlerForMethod {
    @Test
    @DisplayName("should throw when request type argument is null")
    void test1() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.primitiveRequestHandler());

      assertThrows(
          NullPointerException.class, () -> requestHandlerRegistry.getRequestHandlerFor(null));
    }

    @Test
    @DisplayName("should return registered request handler for request type")
    void test2() {
      var requestHandler = TestRequestHandlers.primitiveRequestHandler();
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(requestHandler).register(PrimitiveRequestHandler.class);

      RequestKey<IntegerRequest, Integer> requestType = new RequestKey<>() {};
      Optional<RegisteredRequestHandler<IntegerRequest, Integer>> resolved =
          requestHandlerRegistry.getRequestHandlerFor(requestType);

      assertNotNull(resolved);
      assertTrue(resolved.isPresent());

      // When registered request handler is invoked, it must invoke original
      // request handler instance.
      var request = new IntegerRequest("1");
      resolved.get().invoke(request);

      assertTrue(requestHandler.hasHandled(request));
    }

    @Test
    @DisplayName(
        "should return empty Optional when there is no registered request handler for request type")
    void test3() {
      DeezpatchRequestHandlerRegistry requestHandlerRegistry =
          buildRequestHandlerRegistry(TestRequestHandlers.primitiveRequestHandler());

      // No registrations...

      RequestKey<RequestWithNoHandler, Void> requestType = new RequestKey<>() {};
      Optional<RegisteredRequestHandler<RequestWithNoHandler, Void>> resolved =
          requestHandlerRegistry.getRequestHandlerFor(requestType);

      assertNotNull(resolved);
      assertTrue(resolved.isEmpty());
    }
  }

  private static DeezpatchRequestHandlerRegistry buildRequestHandlerRegistry(
      Object... requestHandlers) {
    return new DeezpatchRequestHandlerRegistry(TestInstanceProviders.of(requestHandlers));
  }

  static class RequestWithNoHandler implements Request<Void> {}
}
