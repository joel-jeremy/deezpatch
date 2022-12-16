package io.github.joeljeremy.deezpatch.core.internal.registries;

import io.github.joeljeremy.deezpatch.core.InstanceProvider;
import io.github.joeljeremy.deezpatch.core.Middleware;
import io.github.joeljeremy.deezpatch.core.MiddlewarePipeline;
import io.github.joeljeremy.deezpatch.core.RegisteredRequestHandler;
import io.github.joeljeremy.deezpatch.core.Request;
import io.github.joeljeremy.deezpatch.core.RequestHandler;
import io.github.joeljeremy.deezpatch.core.RequestKey;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.Optional;

public class MiddlewareTest {

  public static void main(String[] args) {
    InstanceProvider ip =
        type -> {
          try {
            return type.getConstructor().newInstance();
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        };

    DeezpatchMiddlewareRegistry mr = new DeezpatchMiddlewareRegistry(ip, Collections.emptySet());
    DeezpatchRequestHandlerRegistry rhr =
        new DeezpatchRequestHandlerRegistry(ip, mr, Collections.emptySet());

    mr.register(TransactionalMiddleware.class, LoggedMiddleware.class);
    rhr.register(TestRequestHandler.class);

    RegisteredRequestHandler<TestRequest, Integer> handler =
        rhr.getRequestHandlerFor(new RequestKey<TestRequest, Integer>() {}).orElseThrow();

    handler.invoke(new TestRequest("1")).ifPresent(System.out::print);
  }

  public static class TestRequest implements Request<Integer> {
    private final String parameter;

    public TestRequest(String parameter) {
      this.parameter = parameter;
    }

    public String parameter() {
      return parameter;
    }
  }

  public static class TestRequestHandler {
    @RequestHandler
    public Integer invoke(TestRequest request) {
      System.out.println("Converting to int.");
      return Integer.parseInt(request.parameter());
    }
  }

  @MiddlewarePipeline(TransactionalMiddleware.class)
  @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Transactional {}

  @MiddlewarePipeline(LoggedMiddleware.class)
  @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface Logged {}

  @Transactional
  @Logged
  @MiddlewarePipeline({Transactional.class, LoggedMiddleware.class})
  public static @interface LoggedTransactional {}

  public static class TransactionalMiddleware {
    @Middleware
    public Optional<Integer> invoke(Request<Integer> request, Middleware.Next<Integer> next) {
      // Start transaction.
      System.out.println("Starting transaction");
      Optional<Integer> result = next.invoke();
      System.out.println("Closing transaction");
      // Close transaction.
      return result;
    }
  }

  public static class LoggedMiddleware {
    @Middleware
    public Optional<Integer> invoke(Request<Integer> request, Middleware.Next<Integer> next) {
      // Log request.
      System.out.println("Logged request: " + request);
      Optional<Integer> result = next.invoke();
      System.out.println("Logged result: " + result);
      // Log response.
      return result;
    }
  }
}
