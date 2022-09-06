package io.github.joeljeremy7.deezpatch.commands.dispatchers;

import io.github.joeljeremy7.deezpatch.commands.CommandHandlingException;
import io.github.joeljeremy7.deezpatch.commands.registries.DefaultCommandHandlerRegistry;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommand;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommandHandlerInstanceProviders;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommandHandlers.TestCommandHandler;
import io.github.joeljeremy7.deezpatch.commands.testentities.TestCommandHandlers.ThrowingCommandHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultCommandDispatcherTests {
    @Nested
    class Constructors {
        @Test
        @DisplayName("should throw when command handler registry argument is null.")
        public void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> new DefaultCommandDispatcher(null)
            );
        }
    }

    @Nested
    class SendMethod {
        @Test
        @DisplayName("should throw when command argument is null")
        public void test1() {
            DefaultCommandDispatcher commandDispatcher = buildCommandDispatcher(
                new TestCommandHandler()
            );
            
            assertThrows(
                NullPointerException.class, 
                () -> commandDispatcher.send(null)
            );
        }

        @Test
        @DisplayName("should throw when no command handler is registered")
        public void test2() {
            DefaultCommandDispatcher commandDispatcher = buildCommandDispatcher(
                new TestCommandHandler()
            );
            
            assertThrows(
                CommandHandlingException.class, 
                () -> commandDispatcher.send(UnregisteredCommand.class)
            );
        }

        @Test
        @DisplayName("should invoke registered command handler method")
        public void test3() {
            var testCommandHandler = new TestCommandHandler();
            DefaultCommandDispatcher commandDispatcher = buildCommandDispatcher(
                testCommandHandler
            );

            var testCommand = new TestCommand();
            commandDispatcher.send(testCommand);

            assertEquals(1, testCommandHandler.handledCommands().size());
            assertTrue(testCommandHandler.hasHandledCommand(testCommand));
        }

        @Test
        @DisplayName("should propagate exception thrown by command handler")
        public void test4() {
            var exception = new RuntimeException("Oops!");
            DefaultCommandDispatcher commandDispatcher = buildCommandDispatcher(
                new ThrowingCommandHandler(exception)
            );

            RuntimeException thrown = assertThrows(
                RuntimeException.class, 
                () -> commandDispatcher.send(new TestCommand())
            );

            assertSame(exception, thrown);
        }
    }

    private static <T> DefaultCommandDispatcher buildCommandDispatcher(
            T commandHandler
    ) {
        var registry = 
            new DefaultCommandHandlerRegistry(TestCommandHandlerInstanceProviders.of(commandHandler))
                .scan(commandHandler.getClass());

        return new DefaultCommandDispatcher(registry);
    }

    public static class UnregisteredCommand {}
}
