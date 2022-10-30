package io.github.joeljeremy.deezpatch.core.internal;

import java.util.concurrent.ThreadFactory;

/** Daemon thread factory. */
public enum DaemonThreadFactory implements ThreadFactory {
  INSTANCE;

  /**
   * Create a new daemon thread.
   *
   * @param r The runnable.
   * @return The daemon thread.
   */
  @Override
  public Thread newThread(Runnable r) {
    Thread thread = new Thread(r);
    thread.setDaemon(true);
    return thread;
  }
}
