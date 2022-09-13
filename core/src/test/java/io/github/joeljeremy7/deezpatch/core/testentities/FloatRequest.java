package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class FloatRequest implements Request<Float> {
    private final String parameter;

    public FloatRequest(String parameter) {
        this.parameter = parameter;
    }
    
    public String parameter() {
        return parameter;
    }
}