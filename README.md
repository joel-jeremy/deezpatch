# Deezpatch

This library aims to help build applications which apply the [Command Query Responsibility Segregation](https://martinfowler.com/bliki/CQRS.html) pattern.

## Performance

The library utilizes the benefits provided by `Lambdametafactory` to avoid the cost of invoking methods reflectively. This results in performance similar to directly invoking the methods.
