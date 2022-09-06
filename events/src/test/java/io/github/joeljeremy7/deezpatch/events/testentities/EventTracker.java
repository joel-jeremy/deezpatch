package io.github.joeljeremy7.deezpatch.events.testentities;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class EventTracker {
    private final List<Object> handledEvents = new ArrayList<>();

    protected <T> void track(T event) {
        requireNonNull(event);
        handledEvents.add(event);
    }

    public List<?> handledEvents() {
        return List.copyOf(handledEvents);
    }

    public boolean hasHandledEvent(Object event) {
        return handledEvents.stream().anyMatch(c -> c.equals(event));
    }
}
