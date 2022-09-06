package io.github.joeljeremy7.deezpatch.queries.registries;

import io.github.joeljeremy7.deezpatch.queries.Query;
import io.github.joeljeremy7.deezpatch.queries.QueryHandler;
import io.github.joeljeremy7.deezpatch.queries.QueryHandlerInstanceProvider;
import io.github.joeljeremy7.deezpatch.queries.QueryHandlerProvider;
import io.github.joeljeremy7.deezpatch.queries.QueryHandlerRegistry;
import io.github.joeljeremy7.deezpatch.queries.QueryType;
import io.github.joeljeremy7.deezpatch.queries.RegisteredQueryHandler;
import io.github.joeljeremy7.deezpatch.queries.WeakConcurrentHashMap;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

import static java.util.Objects.requireNonNull;

public class DefaultQueryHandlerRegistry 
        implements QueryHandlerRegistry, QueryHandlerProvider {

    /**
     * Map key is the query type. It returns another map whose key is the query return type.
     * The second map returns the query function mapped to the query return type.
     */
    private final ConcurrentMap<Type, ConcurrentMap<Type, RegisteredQueryHandler<?, ?>>> 
        handlersByQueryType = new WeakConcurrentHashMap<>();
    
    private final QueryHandlerInstanceProvider queryHandlerInstanceProvider;

    public DefaultQueryHandlerRegistry(
            QueryHandlerInstanceProvider queryHandlerInstanceProvider
    ) {
        this.queryHandlerInstanceProvider = requireNonNull(queryHandlerInstanceProvider);
    }

    @Override
    public DefaultQueryHandlerRegistry scan(Class<?> queryHandlerClass) {
        requireNonNull(queryHandlerClass);

        // Register all methods marked with @QueryHandler.
        Arrays.stream(queryHandlerClass.getMethods())
            .filter(method -> 
                method.isAnnotationPresent(QueryHandler.class) &&
                // Has atleast 1 parameter. First parameter must be the query.
                hasAtLeastOneParameter(method)
            )
            .forEach(queryHandlerMethod -> register(
                // First parameter in the method is the query object.
                QueryType.from(
                    queryHandlerMethod.getGenericParameterTypes()[0],
                    queryHandlerMethod.getGenericReturnType()
                ),
                queryHandlerMethod
            ));

        return this;
    }

    @Override
    public <Q extends Query<R>, R> Optional<RegisteredQueryHandler<Q, R>> getQueryHandlerFor(
            QueryType<Q, R> queryType
    ) {
        requireNonNull(queryType);

        ConcurrentMap<Type, RegisteredQueryHandler<?, ?>> handlersByReturnType = 
            handlersByQueryType.get(queryType.queryType());
        
        if (handlersByReturnType == null) {
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        RegisteredQueryHandler<Q, R> handler =
            (RegisteredQueryHandler<Q, R>)handlersByReturnType.get(queryType.resultType());

        return Optional.ofNullable(handler);
    }

    private void register(QueryType<?, ?> queryType, Method queryHandlerMethod) {
        requireNonNull(queryType);
        requireNonNull(queryHandlerMethod);

        ConcurrentMap<Type, RegisteredQueryHandler<?, ?>> handlerByReturnType = 
            handlersByQueryType.computeIfAbsent(
                queryType.queryType(), 
                k -> new WeakConcurrentHashMap<>()
            );

        RegisteredQueryHandler<?, ?> builtHandler = buildQueryHandler(
            queryHandlerMethod,
            queryHandlerInstanceProvider
        );

        if (handlerByReturnType.putIfAbsent(queryType.resultType(), builtHandler) != null) {
            throw new UnsupportedOperationException(
                "Duplicate query handler registration for query: " + queryType
            );
        }
    }

    private static RegisteredQueryHandler<?, ?> buildQueryHandler(
            Method queryHandlerMethod,
            QueryHandlerInstanceProvider queryHandlerInstanceProvider
    ) {
        requireNonNull(queryHandlerMethod);
        requireNonNull(queryHandlerInstanceProvider);
        
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle queryHandlerMethodHandle = lookup
                .in(queryHandlerMethod.getDeclaringClass())
                .unreflect(queryHandlerMethod);

            MethodType methodType = queryHandlerMethodHandle.type();
            Class<?> queryHandlerClass = methodType.parameterType(0);
            Class<?> queryParameterType = methodType.parameterType(1);
            Class<?> queryResultType = methodType.returnType();
                
            CallSite callSite = LambdaMetafactory.metafactory(
                lookup, 
                "invoke", 
                MethodType.methodType(QueryHandlerMethod.class), 
                MethodType.methodType(Object.class, Object.class, Object.class), 
                queryHandlerMethodHandle, 
                MethodType.methodType(queryResultType, queryHandlerClass, queryParameterType)
            );

            QueryHandlerMethod queryHandlerMethodLambda =
                (QueryHandlerMethod)callSite.getTarget().invoke();

            // Only request event handler instance when invoked instead of during registration time.
            return new RegisteredQueryHandler<Query<Object>, Object>() {
                @Override
                public Object invoke(Query<Object> query) {
                    return queryHandlerMethodLambda.invoke(
                        queryHandlerInstanceProvider.getInstance(queryHandlerClass), 
                        query
                    );
                }

                @Override
                public String toString() {
                    return queryHandlerMethod.toGenericString();
                }
                
            };
        } catch (Throwable e) {
            throw new IllegalStateException(
                "Failed to build handler for method: " + queryHandlerMethod.toGenericString()
            );
        }
    }

    private static boolean hasAtLeastOneParameter(Method method) {
        if (method.getParameterCount() != 1) {
            throw new IllegalArgumentException(
                "Methods marked with @QueryHandler must accept a single parameter which is the query object."
            );
        }
        return true;
    }

    /**
     * Used in building lambdas via {@link LambdaMetafactory}.
     */
    private static interface QueryHandlerMethod {
        /**
         * Invoke the actual method annotated with {@link QueryHandler}.
         * 
         * @param queryHandlerInstance The query handler instance.
         * @param query The dispatched query.
         * @return The query result.
         */
        Object invoke(Object queryHandlerInstance, Object query);
    }
}