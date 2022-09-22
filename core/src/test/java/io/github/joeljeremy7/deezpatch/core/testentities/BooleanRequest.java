package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class BooleanRequest implements Request<Boolean> {
  private final String parameter;

  public BooleanRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
