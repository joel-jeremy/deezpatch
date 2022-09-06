package io.github.joeljeremy7.deezpatch.commands.testentities;

import io.github.joeljeremy7.deezpatch.commands.CommandHandlerInstanceProvider;

public class TestCommandHandlerInstanceProviders {
    private TestCommandHandlerInstanceProviders() {}

    public static CommandHandlerInstanceProvider of(Object commandHandler) {
        return clazz -> {
            if (clazz.equals(commandHandler.getClass())) {
                return commandHandler;
            }
            
            throw new IllegalStateException("No instance found for command handler: " + clazz);
        };
    }
}
