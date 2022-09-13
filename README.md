# Deezpatch

A simple dispatch library.

This library aims to help build applications which apply the [Command Query Responsibility Segregation](https://martinfowler.com/bliki/CQRS.html) pattern.

## 🛠️ Get Deezpatch

### Gradle

```groovy
implementation "io.github.joeljeremy7.deezpatch:core:${version}"
```

### Maven

```xml
<dependency>
    <groupId>io.github.joeljeremy7.deezpatch</groupId>
    <artifactId>core</artifactId>
    <version>${version}</version>
</dependency>
```

### 🧩 Java 9 Module Names

Deezpatch jars are published with Automatic-Module-Name manifest attribute:

- Core - `io.github.joeljeremy7.deezpatch.core`

Module authors can use above module names in their module-info.java:

```java
module foo.bar {
    requires io.github.joeljeremy7.deezpatch.core;
}
```

## 🚀 Performance

What differentiates Deezpatch from other messaging/dispatch libraries? The library utilizes the benefits provided by `Lambdametafactory` to avoid the cost of invoking methods reflectively. This results in performance similar to directly invoking the methods.

## 📨 Requests and Events

### Request

Requests are messages that either:

1. Initiate a state change/mutation (commands)
2. Retrieve data (queries)

Requests are dispatched to a single request handler.

### Event

Events are messages that indicate that something has occurred in the system.

Events are dispatched to zero or more event handlers.
