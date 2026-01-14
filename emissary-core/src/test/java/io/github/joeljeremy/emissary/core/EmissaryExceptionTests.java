package io.github.joeljeremy.emissary.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class EmissaryExceptionTests {
  @Nested
  class Constructors {
    @Test
    @DisplayName("should set exception message")
    void test1() {
      EmissaryException ex = new EmissaryException("My message");

      assertEquals("My message", ex.getMessage());
    }

    @Test
    @DisplayName("should set exception message and cause")
    void test2() {
      Throwable cause = new RuntimeException();
      EmissaryException ex = new EmissaryException("My message", cause);

      assertEquals("My message", ex.getMessage());
      assertEquals(cause, ex.getCause());
    }
  }
}
