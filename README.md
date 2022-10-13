# Deezpatch

[![Gradle Build](https://github.com/joel-jeremy/deezpatch/actions/workflows/gradle-build.yaml/badge.svg)](https://github.com/joel-jeremy/deezpatch/actions/workflows/gradle-build.yaml)
[![Code QL](https://github.com/joel-jeremy/deezpatch/actions/workflows/codeql.yaml/badge.svg)](https://github.com/joel-jeremy/deezpatch/actions/workflows/codeql.yaml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.joeljeremy.deezpatch/core/badge.svg)](https://search.maven.org/search?q=g:%22io.github.joeljeremy.deezpatch%22)
[![Coverage Status](https://coveralls.io/repos/github/joel-jeremy/deezpatch/badge.svg?branch=main)](https://coveralls.io/github/joel-jeremy/deezpatch?branch=main)
[![Known Vulnerabilities](https://snyk.io/test/github/joel-jeremy/deezpatch/badge.svg)](https://snyk.io/test/github/joel-jeremy/deezpatch)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/joel-jeremy/deezpatch/blob/main/LICENSE)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy.deezpatch&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy.deezpatch)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy.deezpatch&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy.deezpatch)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy.deezpatch&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy.deezpatch)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy.deezpatch&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy.deezpatch)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy.deezpatch&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy.deezpatch)
[![Discord](https://img.shields.io/discord/1025648239162175578.svg?logo=discord&logoColor=white&logoWidth=20&labelColor=7289DA&label=Discord&color=17cf48)](https://discord.gg/TMmVtcfu)

A simple dispatch library.

The library aims to help build applications which apply the [Command Query Responsibility Segregation](https://martinfowler.com/bliki/CQRS.html) (CQRS) pattern.

## 🛠️ Get Deezpatch

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

### 🧩 Java 9 Module Names

Deezpatch jars are published with Automatic-Module-Name manifest attribute:

- Core - `io.github.joeljeremy.deezpatch.core`

Module authors can use above module names in their module-info.java:

```java
module foo.bar {
    requires io.github.joeljeremy.deezpatch.core;
}
```

## 🚀 Performance

What differentiates Deezpatch from other messaging/dispatch libraries? The library takes advantage of the benefits provided by [java.lang.invoke.LambdaMetafactory](https://docs.oracle.com/javase/8/docs/api/java/lang/invoke/LambdaMetafactory.html) to avoid the cost of invoking methods reflectively. This results in performance close to directly invoking the request handler and event handler methods!

### [Java 11 Benchmarks](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/joel-jeremy/deezpatch/main/deezpatch-core/src/jmh/java/io/github/joeljeremy/deezpatch/core/benchmarks/results-java11.json)

### [Java 17 Benchmarks](https://jmh.morethan.io/?source=https://raw.githubusercontent.com/joel-jeremy/deezpatch/main/deezpatch-core/src/jmh/java/io/github/joeljeremy/deezpatch/core/benchmarks/results-java17.json)

## ✉️ Requests

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

## 📨  Request Handlers

Requests are handled by request handlers. Request handlers can be registered through the use of the [@RequestHandler](core/src/main/java/io/github/joeljeremy/deezpatch/core/RequestHandler.java) annotation.

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

## 🏤 Request Dispatcher

Requests are dispatched to a single request handler and this can be done through a dispatcher.

```java
public static void main(String[] args) {
    // Deezpatch implements the Dispatcher interface.
    Deezpatch deezpatch = Deezpatch.builder()
        .instanceProvider(applicationContext::getBean)
        .requests(config -> config.register(
            GreetCommandHandler.java,
            PingQueryHandler.java
        ))
        .build();

    // Send command!
    deezpatch.send(new GreetCommand("Deez"));

    // Send query!
    Optional<Pong> pong = deezpatch.send(new PingQuery());
}
```

## ✉️ Events

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

## 📨 Event Handlers

Events are handled by event handlers. Event handlers can be registered through the use of the [@EventHandler](core/src/main/java/io/github/joeljeremy/deezpatch/core/EventHandler.java) annotation.

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

## 📣 Event Publisher

Events are dispatched to zero or more event handlers and this can be done through a publisher.

```java
public static void main(String[] args) {
    // Deezpatch implements the Publisher interface.
    Deezpatch deezpatch = Deezpatch.builder()
        .instanceProvider(applicationContext::getBean)
        .events(config -> config.register(
            GreetedEventHandler.java
        ))
        .build();

    // Publish event!
    deezpatch.publish(new GreetedEvent("Hi from Deez!"));
}
```

## 🔩 Easy Integration with Dependency Injection (DI) Frameworks

The library provides a [InstanceProvider](core/src/main/java/io/github/joeljeremy/deezpatch/core/InstanceProvider.java) to let users customize how request/event handler instances should be instantiated. This can be as simple as `new`-ing up request/event handlers or getting instances from a DI framework such as Spring's `ApplicationContext`, Guice's `Injector`, etc.

### Example with No DI frameworks

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
