package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Event;

public class TestEvent implements Event {
  private final String data;

  public TestEvent(String data) {
    this.data = data;
  }

  public String data() {
    return data;
  }
}
