package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class IntegerRequest implements Request<Integer> {
    private final String parameter;

    public IntegerRequest(String parameter) {
        this.parameter = parameter;
    }
    
    public String parameter() {
        return parameter;
    }
}