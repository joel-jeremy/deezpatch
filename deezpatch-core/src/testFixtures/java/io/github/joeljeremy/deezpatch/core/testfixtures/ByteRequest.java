package io.github.joeljeremy.deezpatch.core.testfixtures;

import io.github.joeljeremy.deezpatch.core.Request;

public class ByteRequest implements Request<Byte> {
  private final String parameter;

  public ByteRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
