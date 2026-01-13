# Deezpatch

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/joel-jeremy/deezpatch/blob/main/LICENSE)
[![Gradle Build](https://github.com/joel-jeremy/deezpatch/actions/workflows/gradle-build.yaml/badge.svg)](https://github.com/joel-jeremy/deezpatch/actions/workflows/gradle-build.yaml)
[![Code QL](https://github.com/joel-jeremy/deezpatch/actions/workflows/codeql.yaml/badge.svg)](https://github.com/joel-jeremy/deezpatch/actions/workflows/codeql.yaml)
[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/io.github.joel-jeremy.deezpatch/deezpatch-core/badge.svg)](https://maven-badges.sml.io/maven-central/io.github.joel-jeremy.deezpatch/deezpatch-core)
[![codecov](https://codecov.io/gh/joel-jeremy/deezpatch/graph/badge.svg?token=1KVQMOXYHT)](https://codecov.io/gh/joel-jeremy/deezpatch)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=coverage)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=bugs)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=io.github.joel-jeremy.externalized-properties&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=io.github.joel-jeremy.deezpatch)
[![Discord](https://img.shields.io/discord/1025648239162175578.svg?logo=discord&logoColor=white&logoWidth=20&labelColor=7289DA&label=Discord&color=17cf48)](https://discord.gg/bAfgXRVx3T)
<!-- Commenting out until issue gets fixed: https://github.com/snyk/cli/issues/668 -->
<!-- [![Known Vulnerabilities](https://snyk.io/test/github/joel-jeremy/deezpatch/badge.svg)](https://snyk.io/test/github/joel-jeremy/deezpatch) -->

A simple yet 🗲FAST🗲 library to dispatch requests and events to corresponding handlers 🚀

The library aims to take advantage of the intuitiveness of using the annotations for handlers (e.g. `@RequestHandler`/`@EventHandler`) without the drawbacks of reflection.

The library aims to help build applications which apply the [Command Query Responsibility Segregation](https://martinfowler.com/bliki/CQRS.html) (CQRS) pattern.  

## Like the project?

Please consider giving the repository a ⭐. It means a lot! Thank you :)

## Get Deezpatch

### Gradle

```groovy
implementation "io.github.joel-jeremy.deezpatch:deezpatch-core:${version}"
```

### Maven

```xml
<dependency>
    <groupId>io.github.joel-jeremy.deezpatch</groupId>
    <artifactId>deezpatch-core</artifactId>
    <version>${version}</version>
</dependency>
```

### Java 9 Module Names

Deezpatch jars are published with Automatic-Module-Name manifest attribute:

- Core - `io.github.joeljeremy.deezpatch.core`

Module authors can use above module names in their module-info.java:

```java
module foo.bar {
    requires io.github.joeljeremy.deezpatch.core;
}
```

## Performance

What differentiates Deezpatch from other messaging/dispatch libraries? The library takes advantage of the benefits provided by [java.lang.invoke.LambdaMetafactory](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/LambdaMetafactory.html) to avoid the cost of invoking methods reflectively. This results in performance close to directly invoking the request handler and event handler methods!

### ~ 1000% more throughput compared to other similar libraries (Spring Events, Pipelinr, etc)
### ~ 90% less time compared to other similar libraries (Spring Events, Pipelinr, etc)

### [Java 11 Benchmarks](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/joel-jeremy/deezpatch/main/deezpatch-core/src/jmh/java/io/github/joeljeremy/deezpatch/core/benchmarks/results-java11.json)

### [Java 17 Benchmarks](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/joel-jeremy/deezpatch/main/deezpatch-core/src/jmh/java/io/github/joeljeremy/deezpatch/core/benchmarks/results-java17.json)

## Requests

Requests are messages that either:

1. Initiate a state change/mutation
    - Commands in [CQRS](https://martinfowler.com/bliki/CQRS.html)
2. Retrieve/query data
    - Queries in [CQRS](https://martinfowler.com/bliki/CQRS.html)

```java
public class GreetCommand implements Request<Void> {
    private final String name;
    
    public GreetRequest(String name) {
        this.name = name;
    }
    
    public String name() {
        return name;
    }
}

public class PingQuery implements Request<Pong> {}
```

## Request Handlers

Requests are handled by request handlers. Request handlers can be registered through the use of the [@RequestHandler](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/RequestHandler.java) annotation.

A request must only have a single request handler.

**(`@RequestHandler`s fully support methods with `void` return types! No need to set method return type to `Void` and return `null` for no reason.)**

```java
public class GreetCommandHandler {
    @RequestHandler
    public void handle(GreetCommand command) {
        sayHi(command.name());
    }
}

public class PingQueryHandler {
    @RequestHandler
    public Pong handle(PingQuery query) {
        return new Pong("Here's your pong!");
    }
}
```

## Request Dispatcher

Requests are dispatched to a single request handler and this can be done through a dispatcher.

```java
public static void main(String[] args) {
    // Use Spring's application context as InstanceProvider in this example
    // but any other DI framework can be used e.g. Guice, Dagger, etc.
    ApplicationContext applicationContext = springApplicationContext();

    // Deezpatch implements the Dispatcher interface.
    Dispatcher dispatcher = Deezpatch.builder()
        .instanceProvider(applicationContext::getBean)
        .requests(config -> config.handlers(
            GreetCommandHandler.java,
            PingQueryHandler.java
        ))
        .build();

    // Send command!
    dispatcher.send(new GreetCommand("Deez"));

    // Send query!
    Optional<Pong> pong = dispatcher.send(new PingQuery());
}
```

## Events

Events are messages that indicate that something has occurred in the system.

```java
public class GreetedEvent implements Event {
    private final String greeting;

    public GreetedEvent(String greeting) {
        this.greeting = greeting;
    }

    public String greeting() {
        return greeting;
    }
}
```

## Event Handlers

Events are handled by event handlers. Event handlers can be registered through the use of the [@EventHandler](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/EventHandler.java) annotation.

An event can have zero or more event handlers.

```java
public class GreetedEventHandler {
    @EventHandler
    public void sayHello(GreetedEvent event) {
        // Well, hello!
    }

    @EventHandler
    public void sayKumusta(GreetedEvent event) {
        // Well, kumusta?
    }

    @EventHandler
    public void sayGotEm(GreetedEvent event) {
        // Got 'em! 
    }
}
```

## Event Publisher

Events are dispatched to zero or more event handlers and this can be done through a publisher.

```java
public static void main(String[] args) {
    // Use Spring's application context as InstanceProvider in this example
    // but any other DI framework can be used e.g. Guice, Dagger, etc.
    ApplicationContext applicationContext = springApplicationContext();

    // Deezpatch implements the Publisher interface.
    Publisher publisher = Deezpatch.builder()
        .instanceProvider(applicationContext::getBean)
        .events(config -> config.handlers(
            GreetedEventHandler.java
        ))
        .build();

    // Publish event!
    publisher.publish(new GreetedEvent("Hi from Deez!"));
}
```

## Easy Integration with Dependency Injection (DI) Frameworks

The library provides an [InstanceProvider](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/InstanceProvider.java) interface as an extension point to let users customize how request/event handler instances should be instantiated. This can be as simple as `new`-ing up request/event handlers or getting instances from a DI framework such as Spring's `ApplicationContext`, Guice's `Injector`, etc.

### Example with No DI framework

```java
// Application.java

public static void main(String[] args) {
  Deezpatch deezpatch = Deezpatch.builder()
      .instanceProvider(Application::getInstance)
      .requests(...)
      .events(...)
      .build();
}

private static Object getInstance(Class<?> handlerType) {
  if (MyRequestHandler.class.equals(handlerType)) {
    return new MyRequestHandler();
  } else if (MyEventHandler.class.equals(handlerType)) {
    return new MyEventHandler();
  }

  throw new IllegalStateException("Failed to get instance for " + handlerType.getName() + ".");
}
```

### Example with Spring's ApplicationContext

```java
public static void main(String[] args) {
  ApplicationContext applicationContext = springApplicationContext();
  Deezpatch deezpatch = Deezpatch.builder()
      .instanceProvider(applicationContext::getBean)
      .requests(...)
      .events(...)
      .build();
}
```

### Example with Guice's Injector

```java
public static void main(String[] args) {
  Injector injector = guiceInjector();
  Deezpatch deezpatch = Deezpatch.builder()
      .instanceProvider(injector::getInstance)
      .requests(...)
      .events(...)
      .build();
}
```

## Custom Request/Event Handler Annotations

In cases where a project is built in such a way that bringing in external dependencies is considered a bad practice (e.g. domain layer/package in a Hexagonal (Ports and Adapters) architecture), Deezpatch provides a way to use custom request/event handler annotations (in addition to the built-in [RequestHandler](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/RequestHandler.java) and [EventHandler](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/EventHandler.java) annotations) to annotate request/event handlers.

This way, Deezpatch can still be used without adding the core Deezpatch library as a dependency of a project's domain layer/package. Instead, it may be used in the outer layers/packages to wire things up.

```java
// Let's say below classes are declared in a project's core/domain package:

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AwesomeRequestHandler {}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AwesomeEventHandler {}

public class MyRequestHandler {
  @AwesomeRequestHandler
  public void handle(TestRequest request) {
    // Handle.
  }
}

public class MyEventHandler {
  @AwesomeEventHandler
  public void handle(TestEvent event) {
    // Handle.
  }
}

// To wire things up:

public static void main(String[] args) {
  // Use Spring's application context as InstanceProvider in this example
  // but any other DI framework can be used e.g. Guice, Dagger, etc.
  ApplicationContext applicationContext = springApplicationContext();

  // Register handlers and custom annotations.
  Deezpatch deezpatch = Deezpatch.builder()
      .instanceProvider(applicationContext::getBean)
      .requests(config -> 
          config.handlerAnnotations(AwesomeRequestHandler.class)
              .handlers(MyRequestHandler.class))
      .events(config -> 
          config.handlerAnnotations(AwesomeEventHandler.java)
              .handlers(MyEventHandler.class))
      .build();
}
```

## Custom Invocation Strategies

The library provides [Deezpatch.RequestHandlerInvocationStrategy](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/Deezpatch.java) and [Deezpatch.EventHandlerInvocationStrategy](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/Deezpatch.java) interfaces as extension points to let users customize how request/event handler methods are invoked by the Dispatcher and Publisher.

Built-in implementations are:
- [SyncRequestHandlerInvocationStrategy](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/invocationstrategies/SyncRequestHandlerInvocationStrategy.java) (Default)
- [SyncEventHandlerInvocationStrategy](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/invocationstrategies/SyncEventHandlerInvocationStrategy.java) (Default)
- [AsyncEventHandlerInvocationStrategy](deezpatch-core/src/main/java/io/github/joeljeremy/deezpatch/core/invocationstrategies/AsyncEventHandlerInvocationStrategy.java)

Users can create a new implementation and override the defaults by:
```java
// Register custom invocation strategy.
Deezpatch deezpatch = Deezpatch.builder()
    .requests(config -> 
        config.invocationStrategy(
            new LoggingInvocationStrategy(
                new RetryOnErrorInvocationStrategy())))
    .events(config -> 
        config.invocationStrategy(
            new LoggingInvocationStrategy(
                new OrderGuaranteedInvocationStrategy())))
      .build();
```

---

[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=joel-jeremy_deezpatch)
