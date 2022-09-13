package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.InstanceProvider;

import java.util.Arrays;
import java.util.Collection;

public class TestInstanceProviders {
    private TestInstanceProviders() {}

    public static InstanceProvider of(Object... instances) {
        return of(Arrays.asList(instances));
    }

    public static InstanceProvider of(Collection<?> instances) {
        return clazz -> {
            for (Object instance : instances) {
                if (instance.getClass().equals(clazz)) {
                    return instance;
                }
            }

            throw new IllegalStateException("No instance found for class: " + clazz);
        };
    }
}
