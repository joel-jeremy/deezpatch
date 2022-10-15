package io.github.joeljeremy.deezpatch.core.testentities;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class TrackableHandler {
  private List<Object> trackedMessages = new CopyOnWriteArrayList<>();

  protected <T> void track(T message) {
    requireNonNull(message);
    trackedMessages.add(message);
  }

  public List<Object> handledMessages() {
    return List.copyOf(trackedMessages);
  }

  public <T> boolean hasHandled(T message) {
    return trackedMessages.contains(message);
  }
}
