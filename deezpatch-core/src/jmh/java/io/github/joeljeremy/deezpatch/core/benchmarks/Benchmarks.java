package io.github.joeljeremy.deezpatch.core.benchmarks;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipelinr;
import an.awesome.pipelinr.Voidy;
import io.github.joeljeremy.deezpatch.core.Deezpatch;
import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.EventHandler;
import io.github.joeljeremy.deezpatch.core.Request;
import io.github.joeljeremy.deezpatch.core.RequestHandler;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;

@Warmup(time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 2)
public abstract class Benchmarks {
  @State(Scope.Benchmark)
  public static class BenchmarkState {
    private DeezpatchRequest deezpatchRequest;
    private DeezpatchRequestHandler deezpatchRequestHandler;
    private Deezpatch deezpatchRequestDispatcher;
    private DeezpatchEvent deezpatchEvent;
    private DeezpatchEventHandler deezpatchEventHandler;
    private Deezpatch deezpatchEventPublisher;

    private SpringEvent springEvent;
    private SpringEventListener springEventListener;
    private ApplicationEventPublisher springEventPublisher;

    private PipelinrCommand pipelinrCommand;
    private PipelinrCommandHandler pipelinrCommandHandler;
    private Pipelinr commandPipelinr;
    private PipelinrNotification pipelinrNotification;
    private PipelinrNotificationHandler pipelinrNotificationHandler;
    private Pipelinr notificationPipelinr;

    private EventBusMessage eventBusMessage;
    private EventBusSubsriber eventBusSubscriber;
    private EventBus eventBus;

    @Setup
    public void setup() throws Throwable {
      // Deezpatch.

      deezpatchRequest = new DeezpatchRequest();
      deezpatchRequestHandler = new DeezpatchRequestHandler();
      deezpatchRequestDispatcher =
          Deezpatch.builder()
              .instanceProvider(c -> deezpatchRequestHandler)
              .requests(config -> config.handlers(DeezpatchRequestHandler.class))
              .build();

      deezpatchEvent = new DeezpatchEvent();
      deezpatchEventHandler = new DeezpatchEventHandler();
      deezpatchEventPublisher =
          Deezpatch.builder()
              .instanceProvider(c -> deezpatchEventHandler)
              .events(config -> config.handlers(DeezpatchEventHandler.class))
              .build();

      // Spring

      springEvent = new SpringEvent();
      springEventListener = new SpringEventListener();
      var context = new GenericApplicationContext();
      context.registerBean(SpringEventListener.class, () -> springEventListener);
      context.refresh();
      springEventPublisher = context;

      // Pipelinr

      pipelinrCommandHandler = new PipelinrCommandHandler();
      pipelinrCommand = new PipelinrCommand();
      commandPipelinr = new Pipelinr().with(() -> Stream.of(pipelinrCommandHandler));

      pipelinrNotificationHandler = new PipelinrNotificationHandler();
      pipelinrNotification = new PipelinrNotification();
      notificationPipelinr = new Pipelinr().with(() -> Stream.of(pipelinrNotificationHandler));

      // EventBus
      eventBusMessage = new EventBusMessage();
      eventBusSubscriber = new EventBusSubsriber();
      eventBus = EventBus.getDefault();
      eventBus.register(eventBusSubscriber);
    }
  }

  /** Benchmarks that measure average time. */
  @BenchmarkMode(Mode.AverageTime)
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  public static class BenchmarksAvgt extends Benchmarks {}

  /** Benchmarks that measure throughput. */
  @BenchmarkMode(Mode.Throughput)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  public static class BenchmarksThrpt extends Benchmarks {}

  @Benchmark
  public void deezpatchEvent(BenchmarkState state, Blackhole blackhole) {
    state.deezpatchEventPublisher.publish(state.deezpatchEvent);
    blackhole.consume(state.deezpatchEvent);
  }

  @Benchmark
  public void springEvent(BenchmarkState state, Blackhole blackhole) {
    state.springEventPublisher.publishEvent(state.springEvent);
    blackhole.consume(state.springEvent);
  }

  @Benchmark
  public void pipelinrNotification(BenchmarkState state, Blackhole blackhole) {
    state.notificationPipelinr.send(state.pipelinrNotification);
    blackhole.consume(state.pipelinrNotification);
  }

  @Benchmark
  public void eventBusEvent(BenchmarkState state, Blackhole blackhole) {
    state.eventBus.post(state.eventBusMessage);
    blackhole.consume(state.eventBusMessage);
  }

  // Single dispatch benchmarks.

  @Benchmark
  public void deezpatchRequest(BenchmarkState state, Blackhole blackhole) {
    state.deezpatchRequestDispatcher.send(state.deezpatchRequest);
    blackhole.consume(state.deezpatchRequest);
  }

  @Benchmark
  public void pipelinrCommand(BenchmarkState state, Blackhole blackhole) {
    state.commandPipelinr.send(state.pipelinrCommand);
    blackhole.consume(state.pipelinrCommand);
  }

  public static class DeezpatchRequest implements Request<Void> {}

  public static class DeezpatchRequestHandler {
    @RequestHandler
    public void handle(DeezpatchRequest request) {
      // No-op.
    }
  }

  public static class DeezpatchEvent implements Event {}

  public static class DeezpatchEventHandler {
    @EventHandler
    public void handle(DeezpatchEvent event) {
      // No-op.
    }
  }

  public static class SpringEvent {}

  public static class SpringEventListener {
    @EventListener
    public void handle(SpringEvent event) {
      // No-op.
    }
  }

  public static class PipelinrCommand implements Command<Voidy> {}

  public static class PipelinrCommandHandler implements Command.Handler<PipelinrCommand, Voidy> {
    @Override
    public Voidy handle(PipelinrCommand command) {
      // No-op.
      return null;
    }
  }

  public static class PipelinrNotification implements Notification {}

  public static class PipelinrNotificationHandler
      implements Notification.Handler<PipelinrNotification> {
    @Override
    public void handle(PipelinrNotification notification) {
      // No-op.
    }
  }

  public static class EventBusMessage {}

  public static class EventBusSubsriber {
    @Subscribe
    public void handle(EventBusMessage event) {
      // No-op.
    }
  }
}
