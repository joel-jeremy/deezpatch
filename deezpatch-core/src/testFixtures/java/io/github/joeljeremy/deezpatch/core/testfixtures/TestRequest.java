package io.github.joeljeremy.deezpatch.core.testfixtures;

import io.github.joeljeremy.deezpatch.core.Request;

public class TestRequest implements Request<TestResult> {
  private final String parameter;

  public TestRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
