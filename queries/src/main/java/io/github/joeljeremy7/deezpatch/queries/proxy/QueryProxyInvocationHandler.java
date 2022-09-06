// package io.github.joeljeremy7.deezpatch.queries.proxy;

// import io.github.joeljeremy7.deezpatch.queries.Query;
// import io.github.joeljeremy7.deezpatch.queries.QueryHandlerRegistry;
// import io.github.joeljeremy7.deezpatch.queries.QueryHandlingException;
// import io.github.joeljeremy7.deezpatch.queries.QueryType;
// import io.github.joeljeremy7.deezpatch.queries.RegisteredQueryHandler;

// import java.lang.reflect.InvocationHandler;
// import java.lang.reflect.Method;
// import java.util.function.Function;

// import static java.util.Objects.requireNonNull;

// public class QueryProxyInvocationHandler implements InvocationHandler {

//     private final QueryHandlerRegistry queryHandlerRegistry;

//     public QueryProxyInvocationHandler(QueryHandlerRegistry queryHandlerRegistry) {
//         this.queryHandlerRegistry = requireNonNull(queryHandlerRegistry);
//     }

//     @Override
//     public Object invoke(Object proxy, Method method, Object[] args) 
//         throws Throwable 
//     {
//         // First parameter is always assumed to be the query object.
//         Query<?, ?> query = (Query<?, ?>)requireNonNull(args[0]);

//         QueryType queryType = new QueryType(
//             method.getParameterTypes()[0], 
//             method.getReturnType()
//         );

//         RegisteredQueryHandler<?, ?> queryHandler = queryHandlerRegistry.getQueryHandlerFor(queryType)
//             .orElseThrow(() -> 
//                 new QueryHandlingException("No query handler found for query of type: " + queryType)
//             );

//         @SuppressWarnings("unchecked")
//         Object result = queryHandler.invoke(query);
//         return result;
//     }
// }
