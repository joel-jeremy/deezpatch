package io.github.joeljeremy7.deezpatch.events.testentities;

import io.github.joeljeremy7.deezpatch.events.EventHandlerInstanceProvider;

public class TestEventHandlerInstanceProviders {
    private TestEventHandlerInstanceProviders() {}

    public static EventHandlerInstanceProvider of(Object eventHandler) {
        return clazz -> {
            if (clazz.equals(eventHandler.getClass())) {
                return eventHandler;
            }
            
            throw new IllegalStateException("No instance found for event handler: " + clazz);
        };
    }
}
