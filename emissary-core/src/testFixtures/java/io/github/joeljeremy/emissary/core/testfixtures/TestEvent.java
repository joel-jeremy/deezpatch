package io.github.joeljeremy.emissary.core.testfixtures;

import io.github.joeljeremy.emissary.core.Event;

public class TestEvent implements Event {
  private final String data;

  public TestEvent(String data) {
    this.data = data;
  }

  public String data() {
    return data;
  }
}
