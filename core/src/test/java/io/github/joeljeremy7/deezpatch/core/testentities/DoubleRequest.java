package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class DoubleRequest implements Request<Double> {
  private final String parameter;

  public DoubleRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
