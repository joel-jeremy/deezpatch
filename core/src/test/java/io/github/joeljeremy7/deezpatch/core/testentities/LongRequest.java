package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class LongRequest implements Request<Long> {
    private final String parameter;

    public LongRequest(String parameter) {
        this.parameter = parameter;
    }
    
    public String parameter() {
        return parameter;
    }
}