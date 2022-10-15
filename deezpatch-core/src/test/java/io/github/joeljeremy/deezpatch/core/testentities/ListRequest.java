package io.github.joeljeremy.deezpatch.core.testentities;

import io.github.joeljeremy.deezpatch.core.Request;
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
