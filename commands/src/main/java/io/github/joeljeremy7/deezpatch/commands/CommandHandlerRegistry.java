package io.github.joeljeremy7.deezpatch.commands;

/**
 * The command handler registry.
 */
public interface CommandHandlerRegistry {
    /**
     * Scan class for methods annotated with {@link CommandHandler} and
     * register them as command handlers.
     * 
     * @param handlerClass The class to scan for {@link CommandHandler}
     * annotations.
     * @return This registry.
     */
    CommandHandlerRegistry scan(Class<?> handlerClass);
}
