package io.github.joeljeremy.deezpatch.kafka.testentities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.joeljeremy.deezpatch.core.Event;

public class TestKafkaEvent implements Event {
  private final String test;

  @JsonCreator
  public TestKafkaEvent(@JsonProperty("test") String test) {
    this.test = test;
  }

  @JsonProperty
  public String test() {
    return test;
  }
}
