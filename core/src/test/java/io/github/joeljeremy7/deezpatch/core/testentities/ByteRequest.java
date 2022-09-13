package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.Request;

public class ByteRequest implements Request<Byte> {
    private final String parameter;

    public ByteRequest(String parameter) {
        this.parameter = parameter;
    }
    
    public String parameter() {
        return parameter;
    }
}