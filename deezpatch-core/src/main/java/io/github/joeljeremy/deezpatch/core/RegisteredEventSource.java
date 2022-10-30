package io.github.joeljeremy.deezpatch.core;

/** Represents an event source. */
public interface RegisteredEventSource {
  /**
   * Start receiving events and publish using the provided publisher.
   *
   * @param publisher The publisher which the event source should publish received events with.
   */
  void start(Publisher publisher);
}
