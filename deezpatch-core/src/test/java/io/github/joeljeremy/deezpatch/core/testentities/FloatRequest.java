package io.github.joeljeremy.deezpatch.core.testentities;

import io.github.joeljeremy.deezpatch.core.Request;

public class FloatRequest implements Request<Float> {
  private final String parameter;

  public FloatRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
