package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;
import java.util.List;

public class ListRequest implements Request<List<String>> {
  private final String parameter;

  public ListRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
