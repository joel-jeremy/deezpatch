package io.github.joeljeremy.emissary.core.benchmarks;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.Notification;
import an.awesome.pipelinr.Pipelinr;
import an.awesome.pipelinr.Voidy;
import io.github.joeljeremy.emissary.core.Emissary;
import io.github.joeljeremy.emissary.core.Event;
import io.github.joeljeremy.emissary.core.EventHandler;
import io.github.joeljeremy.emissary.core.Request;
import io.github.joeljeremy.emissary.core.RequestHandler;
import io.github.joeljeremy.emissary.core.benchmarks.Benchmarks.EmissaryEventHandler;
import io.github.joeljeremy.emissary.core.benchmarks.Benchmarks.EmissaryRequestHandler;
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
    private EmissaryRequest emissaryRequest;
    private EmissaryRequestHandler emissaryRequestHandler;
    private Emissary emissaryRequestDispatcher;
    private EmissaryEvent emissaryEvent;
    private EmissaryEventHandler emissaryEventHandler;
    private Emissary emissaryEventPublisher;

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
      // Emissary.

      emissaryRequest = new EmissaryRequest();
      emissaryRequestHandler = new EmissaryRequestHandler();
      emissaryRequestDispatcher =
          Emissary.builder()
              .instanceProvider(c -> emissaryRequestHandler)
              .requests(config -> config.handlers(EmissaryRequestHandler.class))
              .build();

      emissaryEvent = new EmissaryEvent();
      emissaryEventHandler = new EmissaryEventHandler();
      emissaryEventPublisher =
          Emissary.builder()
              .instanceProvider(c -> emissaryEventHandler)
              .events(config -> config.handlers(EmissaryEventHandler.class))
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
  public void emissaryEvent(BenchmarkState state, Blackhole blackhole) {
    state.emissaryEventPublisher.publish(state.emissaryEvent);
    blackhole.consume(state.emissaryEvent);
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
  public void emissaryRequest(BenchmarkState state, Blackhole blackhole) {
    state.emissaryRequestDispatcher.send(state.emissaryRequest);
    blackhole.consume(state.emissaryRequest);
  }

  @Benchmark
  public void pipelinrCommand(BenchmarkState state, Blackhole blackhole) {
    state.commandPipelinr.send(state.pipelinrCommand);
    blackhole.consume(state.pipelinrCommand);
  }

  public static class EmissaryRequest implements Request<Void> {}

  public static class EmissaryRequestHandler {
    @RequestHandler
    public void handle(EmissaryRequest request) {
      // No-op.
    }
  }

  public static class EmissaryEvent implements Event {}

  public static class EmissaryEventHandler {
    @EventHandler
    public void handle(EmissaryEvent event) {
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
