package io.github.joeljeremy7.deezpatch.commands.testentities;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class CommandTracker {
    private final List<Object> handledCommands = new ArrayList<>();

    protected <T> void track(T command) {
        requireNonNull(command);
        handledCommands.add(command);
    }

    public List<?> handledCommands() {
        return List.copyOf(handledCommands);
    }

    public boolean hasHandledCommand(Object command) {
        return handledCommands.stream().anyMatch(c -> c.equals(command));
    }
}
