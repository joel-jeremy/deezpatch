package io.github.joeljeremy7.deezpatch.commands.testentities;

import io.github.joeljeremy7.deezpatch.commands.CommandHandler;

import java.util.concurrent.CountDownLatch;

import static java.util.Objects.requireNonNull;

public class TestCommandHandlers {
    private TestCommandHandlers() {}

    public static TestCommandHandler testCommandHandler() {
        return new TestCommandHandler();
    }

    public static InvalidCommandHandler invalidCommandHandler() {
        return new InvalidCommandHandler();
    }

    public static ThrowingCommandHandler throwingCommandHandler(
            RuntimeException exceptionToThrow
    ) {
        return new ThrowingCommandHandler(exceptionToThrow);
    }

    public static CountDownLatchCommandHandler countDownLatchCommandHandler(
            CountDownLatch countDownLatch
    ) {
        return new CountDownLatchCommandHandler(countDownLatch);
    }

    public static class TestCommandHandler extends CommandTracker {
        @CommandHandler
        public void handle(TestCommand command) {
            requireNonNull(command);
            track(command);
        }
    }

    public static class ThrowingCommandHandler extends CommandTracker {
        private final RuntimeException toThrow;
    
        public ThrowingCommandHandler(RuntimeException toThrow) {
            this.toThrow = toThrow;
        }
    
        @CommandHandler
        public void handle(TestCommand command) {
            throw toThrow;
        }
    }

    public static class InvalidCommandHandler {
        @CommandHandler
        public void invalid() {
            // Invalid
        }
    }

    public static class CountDownLatchCommandHandler extends CommandTracker {
        private final CountDownLatch countDownLatch;
    
        public CountDownLatchCommandHandler(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }
    
        @CommandHandler
        public void handle(TestCommand command) {
            requireNonNull(command);
            track(command);
            countDownLatch.countDown();
        }
    }
}
