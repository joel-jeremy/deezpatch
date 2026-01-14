package io.github.joeljeremy.emissary.core.testfixtures;

import io.github.joeljeremy.emissary.core.Request;

public class CharacterRequest implements Request<Character> {
  private final String parameter;

  public CharacterRequest(String parameter) {
    this.parameter = parameter;
  }

  public String parameter() {
    return parameter;
  }
}
