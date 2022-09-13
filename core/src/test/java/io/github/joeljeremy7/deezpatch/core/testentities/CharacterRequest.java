package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class CharacterRequest implements Request<Character> {
    private final String parameter;

    public CharacterRequest(String parameter) {
        this.parameter = parameter;
    }
    
    public String parameter() {
        return parameter;
    }
}