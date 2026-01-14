package io.github.joeljeremy.emissary.core.testfixtures;

import io.github.joeljeremy.emissary.core.Request;

public class IntegerRequest implements Request<Integer> {
  private final String parameter;

  public IntegerRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
