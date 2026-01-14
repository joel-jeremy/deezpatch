package io.github.joeljeremy.emissary.core.testfixtures;

import io.github.joeljeremy.emissary.core.Request;

public class BooleanRequest implements Request<Boolean> {
  private final String parameter;

  public BooleanRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
