package io.github.joeljeremy7.deezpatch.commands.registries;

import io.github.joeljeremy7.deezpatch.commands.RegisteredCommandHandler;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommand;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommandHandlerInstanceProviders;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommandHandlers.InvalidCommandHandler;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommandHandlers.TestCommandHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultCommandHandlerRegistryTests {
    @Nested
    class ScanMethod {
        @Test
        @DisplayName(
            "should throw when command handler class argument is null"
        )
        public void test1() {
            DefaultCommandHandlerRegistry commandHandlerRegistry = 
                buildCommandHandlerRegistry(new TestCommandHandler());
            
            assertThrows(
                NullPointerException.class, 
                () -> commandHandlerRegistry.scan(null)
            );
        }

        @Test
        @DisplayName(
            "should detect and register methods annotated with @CommandHandler"
        )
        public void test2() {
            DefaultCommandHandlerRegistry commandHandlerRegistry = 
                buildCommandHandlerRegistry(new TestCommandHandler());
            
            assertTrue(commandHandlerRegistry.getCommandHandlerFor(TestCommand.class).isPresent());
        }

        @Test
        @DisplayName(
            "should throw when a method annotated with @CommandHandler does not have a parameter"
        )
        public void test3() {
            DefaultCommandHandlerRegistry commandHandlerRegistry = 
                new DefaultCommandHandlerRegistry(TestCommandHandlerInstanceProviders.of(new InvalidCommandHandler()));
            
            assertThrows(
                IllegalArgumentException.class, 
                () -> commandHandlerRegistry.scan(InvalidCommandHandler.class)
            );
        }
    }

    @Nested
    class GetCommandHandlerForMethod {
        @Test
        @DisplayName(
            "should throw when command type argument is null"
        )
        public void test1() {
            DefaultCommandHandlerRegistry commandHandlerRegistry = 
                buildCommandHandlerRegistry(new TestCommandHandler());
            
            assertThrows(
                NullPointerException.class, 
                () -> commandHandlerRegistry.getCommandHandlerFor(null)
            );
        }

        @Test
        @DisplayName(
            "should return registered command handler for command type"
        )
        public void test2() {
            var testCommandHandler = new TestCommandHandler();
            DefaultCommandHandlerRegistry commandHandlerRegistry = 
                buildCommandHandlerRegistry(testCommandHandler);

            Optional<RegisteredCommandHandler<TestCommand>> resolved = 
                commandHandlerRegistry.getCommandHandlerFor(TestCommand.class);

            assertNotNull(resolved);
            assertTrue(resolved.isPresent());
            
            // When registered command handler is invoked, it must invoke original
            // command handler instance.
            var testCommand = new TestCommand();
            resolved.get().invoke(testCommand);

            assertTrue(testCommandHandler.hasHandledCommand(testCommand));
        }

        @Test
        @DisplayName(
            "should return empty Optional when there is no registered command handler for command type"
        )
        public void test3() {
            DefaultCommandHandlerRegistry commandHandlerRegistry = 
                buildCommandHandlerRegistry(new TestCommandHandler());

            // No registrations...

            Optional<RegisteredCommandHandler<CommandWithNoHandler>> resolved = 
                commandHandlerRegistry.getCommandHandlerFor(CommandWithNoHandler.class);

            assertNotNull(resolved);
            assertTrue(resolved.isEmpty());
        }
    }

    private static <T> DefaultCommandHandlerRegistry buildCommandHandlerRegistry(
            T commandHandler
    ) { 
        return new DefaultCommandHandlerRegistry(
                TestCommandHandlerInstanceProviders.of(commandHandler)
            )
            .scan(commandHandler.getClass());
    }

    public static class CommandWithNoHandler {}
}
