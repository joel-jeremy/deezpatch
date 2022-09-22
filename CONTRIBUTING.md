# Contributing Guidelines

First of all, thank you for having the interest in contributing to this project!

## Found a bug?

Please create an issue describing the following:

- The version the bug was discovered.
- A scenario to reproduce the bug.

## Enhancement ideas?

Got an idea to enhance the library? Please feel free to create an issue describing the feature proposal. Any ideas are welcome! :)

## Build

The project requires Java 11.

To build the project, run the command:

```sh
./gradlew build
```

To check code formatting, run the command:

```sh
./gradlew build -Dspotless
```

To create reports, run the commands:

```sh
./gradlew build testAggregateTestReport
```

```sh
./gradlew build testCodeCoverageReport
```

## Development Guidelines

### Code Format

The project adheres to the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

To easily fix code formatting warning/errors, the Spotless Gradle plugin can be used to apply [Google Java Format](https://github.com/google/google-java-format). Just run the command:

- `./gradlew build -Dspotless` to check for formatting errors.  
- `./gradlew spotlessApply` to automatically fix formatting errors.

CheckStyle extension/plugin can also be installed to your favorite IDE ([VS Code](https://marketplace.visualstudio.com/items?itemName=shengchen.vscode-checkstyle), [IntelliJ](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea), [Eclipse](https://checkstyle.org/eclipse-cs/#!/)) to highlight code when it does not adhere to [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) (see [CheckStyle Google Style Docs](https://checkstyle.sourceforge.io/google_style.html) and [google_checks.xml](https://github.com/checkstyle/checkstyle/blob/master/src/main/resources/google_checks.xml)).

### Git Branching Strategy

The project follows the [GitHub flow](https://docs.github.com/en/get-started/quickstart/github-flow) branching strategy.

### Unit Test Structure

Unit tests in this project follow a specific structure.

- Classes must have a corresponding test class i.e. `Deezpatch` -> `DeezpatchTests`. The test class must be in the exact same java package as the class it corresponds to.
- Test classes are nested in structure. Each method in the class under test must have a corresponding `@Nested` test class.
- Each `@Nested` test class must test scenarios that is supported by the method it corresponds to via `@Test`.
- Use `@DisplayName` to describe the scenario being tested by the `@Test` method e.g. `@DisplayName("should throw when x argument is null)`.

    ```java
    // Class under test: io.github.joeljeremy7.deezpatcher.core.Deezpatch
    public class Deezpatch implements Dispatcher, Publisher {
        public Deezpatcher(...) {
            ...
        }
        
        public <T extends Request<R>, R> Optional<R> send(T request) {
            ...
        }

        public <T extends Event> void publish(T event) {
            ...
        }

        public static class Builder {
            ...
            public Deezpatcher build() {
                ...
            }
        }
    }

    // Test class: io.github.joeljeremy7.deezpatch.core.DeezpatchTests
    class DeezpatchTests {
        @Nested
        class Constructors {
            // @Test methods here...
        }

        @Nested
        class SendMethod {
            // @Test methods here...
        }

        @Nested
        class PublishMethod {
            // @Test methods here...
        }

        // Nested class must also have corresponding test classes
        @Nested
        class BuilderTests {
            ...
            @Nested
            class BuildMethod {
                // @Test methods here...
            }
        }
    }
    ```

## Release Guidelines

This project follows [Semantic Versioning](https://semver.org/).

When releasing, the following steps must be followed:

1. Create a release and tag via GitHub Releases e.g. `1.0.0`.
    - The release description should include a changelog.
2. After release pipeline completes, bump up the version in root `build.gradle` to the next development version by creating a pull request.
    - By default, the next development version is a minor version bump i.e. `1.0.0` --> `1.1.0`.
3. Merge the pull request to `main`.
