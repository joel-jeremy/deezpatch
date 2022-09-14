# Deezpatch

[![Gradle Build](https://github.com/joeljeremy7/deezpatch/actions/workflows/gradle-build.yaml/badge.svg)](https://github.com/joeljeremy7/deezpatch/actions/workflows/gradle-build.yaml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.joeljeremy7.deezpatch/core/badge.svg)](https://search.maven.org/search?q=g:%22io.github.joeljeremy7.deezpatch%22)
[![Coverage Status](https://coveralls.io/repos/github/joeljeremy7/deezpatch/badge.svg?branch=main)](https://coveralls.io/github/joeljeremy7/deezpatch?branch=main)
[![Known Vulnerabilities](https://snyk.io/test/github/joeljeremy7/deezpatch/badge.svg)](https://snyk.io/test/github/joeljeremy7/deezpatch)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/joeljeremy7/deezpatch/blob/main/LICENSE)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy7.deezpatch&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy7.deezpatch)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy7.deezpatch&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy7.deezpatch)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy7.deezpatch&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy7.deezpatch)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy7.deezpatch&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy7.deezpatch)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=io.github.joeljeremy7.deezpatch&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=io.github.joeljeremy7.deezpatch)

A simple dispatch library.

This library aims to help build applications which apply the [Command Query Responsibility Segregation](https://martinfowler.com/bliki/CQRS.html) (CQRS) pattern.

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

### Requests

Requests are messages that either:

1. Initiate a state change/mutation
    - Commands in [CQRS](https://martinfowler.com/bliki/CQRS.html)
2. Retrieve/query data
    - Queries in [CQRS](https://martinfowler.com/bliki/CQRS.html)

Requests are dispatched to a single request handler.

### Events

Events are messages that indicate that something has occurred in the system.

Events are dispatched to zero or more event handlers.
