// package io.github.joeljeremy7.deezpatch.queries.proxy;

// import io.github.joeljeremy7.deezpatch.queries.QueryHandlerRegistry;

// import java.lang.reflect.Proxy;
// import java.util.Arrays;

// import static java.util.Objects.requireNonNull;

// public class QueryProxyFactory {
//     private final QueryHandlerRegistry queryHandlerRegistry;

//     public QueryProxyFactory(QueryHandlerRegistry queryHandlerRegistry) {
//         this.queryHandlerRegistry = requireNonNull(queryHandlerRegistry);
//     }

//     public <T> T createQueryProxy(Class<T> queryProxyInterface) {
//         requireNonNull(queryProxyInterface);

//         if (Arrays.stream(queryProxyInterface.getMethods())
//                 .anyMatch(m -> m.getParameterCount() == 0)) {
//             throw new IllegalArgumentException(
//                 "Methods in query proxy interfaces must accept a query object. " +
//                 "Invalid query proxy interface: " + queryProxyInterface.getName() + "."
//             );
//         }

//         if (Arrays.stream(queryProxyInterface.getMethods())
//                 .anyMatch(m -> void.class.equals(m.getReturnType()))) {
//             throw new IllegalArgumentException(
//                 "Methods in query proxy interfaces must not return void. " + 
//                 "Invalid query proxy interface: " + queryProxyInterface.getName()
//             );
//         }

//         @SuppressWarnings("unchecked")
//         T proxyInstance = (T)Proxy.newProxyInstance(
//             queryProxyInterface.getClassLoader(),
//             new Class<?>[] { queryProxyInterface },
//             new QueryProxyInvocationHandler(queryHandlerRegistry)
//         );
//         return proxyInstance;
//     }
// }
