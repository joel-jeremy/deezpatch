package io.github.joeljeremy.emissary.core.testfixtures;

import io.github.joeljeremy.emissary.core.Request;

public class VoidRequest implements Request<Void> {
  private final String parameter;

  public VoidRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
