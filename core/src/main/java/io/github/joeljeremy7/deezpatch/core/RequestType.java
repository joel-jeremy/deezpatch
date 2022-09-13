package io.github.joeljeremy7.deezpatch.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Uniquely identifies a request based on the combination of the request type and 
 * the result type.
 * 
 * @apiNote This needs to be instantiated as an anonymous class in order for the 
 * type parameters to be detected e.g. 
 * <code>new RequestType{@literal <}GetMerchantById, Merchant{@literal >}() {}</code>
 * (Note the trailing <code>{}</code>).
 * 
 * @apiNote Factory methods are also provided for convenience: 
 * <ul>
 *   <li>{@link #from(Request)}</li>
 *   <li>{@link #from(Type)}</li>
 *   <li>{@link #from(Type, Type)}</li>
 * </ul>
 * 
 * @param <T> The request type.  
 * @param <R> The result type.
 */
public abstract class RequestType<T extends Request<R>, R> {
    private static final ResultTypeByRequestType RESULT_TYPE_BY_REQUEST_TYPE = 
        new ResultTypeByRequestType();

    private final Type requestType;
    private final Type resultType;

    /**
     * Default constructor. Auto-detect the request type and result type
     * from the generic type parameters {@link T} and {@link R}.
     */
    protected RequestType() {
        ParameterizedType superClass = 
            (ParameterizedType)getClass().getGenericSuperclass();
        Type[] typeParams = superClass.getActualTypeArguments();
        // This is T.
        requestType = typeParams[0];
        // This is R.
        resultType = typeParams[1];
    }

    /**
     * Used by {@link RequestType#from(Type, Type)}.
     * 
     * @param requestType The request type.
     * @param resultType The result type.
     */
    private RequestType(Type requestType, Type resultType) {
        this.requestType = requireNonNull(requestType);
        this.resultType = requireNonNull(resultType);
    }

    /**
     * The request type.
     * @return The request type.
     */
    public Type requestType() {
        return requestType;
    }

    /**
     * The request's result type.
     * @return The request's result type.
     */
    public Type resultType() {
        return resultType;
    }

    /**
     * @implNote Two {@link RequestType}s are equal when their request types and 
     * request result types are equal.
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RequestType) {
            RequestType<?, ?> other = (RequestType<?, ?>)obj;
            return Objects.equals(requestType(), other.requestType()) &&
                Objects.equals(resultType(), other.resultType());
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(requestType(), resultType());
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[requestType=" + requestType().getTypeName() + "," + 
            "resultType=" + resultType().getTypeName() + "]";
    }

    /**
     * Create a {@link RequestType} from the {@link Request} object.
     * 
     * @param <T> The request type.
     * @param <R> The request result type.
     * @param request The request object.
     * @return The request type.
     */
    public static <T extends Request<R>, R> RequestType<T, R> from(T request) {
        requireNonNull(request);
        return new RequestType<>(
            request.getClass(), 
            determineResultType(request.getClass())
        ) {};
    }

    /**
     * Create a {@link RequestType} from the request type.
     * 
     * @param <T> The request type.
     * @param <R> The result type.
     * @param requestType The request type.
     * @return The request type.
     */
    public static <T extends Request<R>, R> RequestType<T, R> from(Class<T> requestType) {
        @SuppressWarnings("unchecked")
        RequestType<T, R> casted = (RequestType<T,R>)from(
            requestType, 
            determineResultType(requestType)    
        );
        return casted;
    }

    /**
     * Create a {@link RequestType} from the request type.
     * 
     * @param requestType The request type.
     * @return The request type.
     */
    public static RequestType<?, ?> from(Type requestType) {
        return from(
            requestType, 
            determineResultType(TypeUtilities.getRawType(requestType))    
        );
    }

    /**
     * Create a {@link RequestType} from the request type and the result type.
     * 
     * @param requestType The request type.
     * @param resultType The result type.
     * @return The request type.
     */
    private static RequestType<?, ?> from(Type requestType, Type resultType) {
        requireNonNull(requestType);
        requireNonNull(resultType);
        return new RequestType<>(requestType, resultType) {};
    }

    private static Type determineResultType(Class<?> requestType) {
        if (!Request.class.isAssignableFrom(requestType)) {
            throw new IllegalArgumentException(
                requestType + " does not implement " + Request.class.getName() + "."
            );
        }
        return RESULT_TYPE_BY_REQUEST_TYPE.get(requestType);
    }

    /**
     * Determine result type of types implementing {@link Request} 
     * i.e. {@code T} in {@code Request<T>}.
     */
    private static class ResultTypeByRequestType extends ClassValue<Type> {
        /**
         * Determine the result of the specified request type.
         */
        @Override
        protected Type computeValue(Class<?> requestType) {
            /**
             * Note: Does not support parameterized request types yet 
             * e.g. public class MyRequest<T> implements Request<T> {}
             */

            return determineResultType(requestType);
        }

        /**
         * Determine the result type of the specified request type.
         * 
         * @param requestType The request type.
         * @return The result type.
         */
        private static Type determineResultType(Class<?> requestType) {
            ParameterizedType requestInterface = resolveRequestInterface(requestType);
            // In case of Request<Integer>, this is the type parameter Integer.
            Type requestTypeParameter = requestInterface.getActualTypeArguments()[0];
            if (requestTypeParameter instanceof TypeVariable<?>) {
                // Resolve actual type of T in Request<T>.
                return resolveActualTypeParameter(
                    requestType, 
                    (TypeVariable<?>)requestTypeParameter
                );
            } else {
                return requestTypeParameter;
            }
        }

        /**
         * Check the type parameters of the entire inheritance hierarchy to
         * determine the actual type of the type variable e.g. {@code <T>}.
         * 
         * @param requestType The request type.
         * @param requestTypeParameter The request type parameter e.g. 
         * {@code T} in {@code Request<T>}.
         * @return The actual type of the type variable.
         */
        private static Type resolveActualTypeParameter(
                Type requestType, 
                TypeVariable<?> requestTypeParameter
        ) {
            Map<TypeVariable<?>, Type> typeVariableMapping = 
                resolveTypeVariableMapping(requestType, new HashMap<>());
            Type resolved = typeVariableMapping.get(requestTypeParameter);
            while (resolved instanceof TypeVariable<?>) {
                resolved = typeVariableMapping.get(resolved);
            }
            return requireNonNull(resolved);
        }

        /**
         * Resolve the mapping of type variables to actual types.
         * 
         * @param type The request type.
         * @param typeVariableMapping The mutable map to put the mappings into.
         * @return The mapping of type variables to actual types.
         */
        private static Map<TypeVariable<?>, Type> resolveTypeVariableMapping(
                Type type,
                Map<TypeVariable<?>, Type> typeVariableMapping
        ) {
            if (type == null) {
                return typeVariableMapping;
            }

            Class<?> clazz = TypeUtilities.getRawType(type);
            TypeVariable<?>[] typeVariables = clazz.getTypeParameters();
            if (typeVariables.length > 0) {
                ParameterizedType parameterizedType = (ParameterizedType)type;
                Type[] actualTypeParams = parameterizedType.getActualTypeArguments();
                for (int i = 0; i < typeVariables.length; i++) {
                    TypeVariable<?> typeVariable = typeVariables[i];
                    Type actualTypeParam = actualTypeParams[i];
                    typeVariableMapping.put(typeVariable, actualTypeParam);
                }
            }

            for (Type intf : clazz.getGenericInterfaces()) {
                resolveTypeVariableMapping(
                    intf, 
                    typeVariableMapping
                );
            }

            return resolveTypeVariableMapping(
                clazz.getGenericSuperclass(), 
                typeVariableMapping
            );
        }

        /**
         * Resolve the {@link Request} interface.
         * 
         * @param type The request type.
         * @return The resolved {@link Request} interface.
         */
        private static ParameterizedType resolveRequestInterface(Class<?> type) {
            Type[] interfaces = type.getGenericInterfaces();
            for (Type intf : interfaces) {
                if (!(intf instanceof ParameterizedType)) {
                    continue;
                }

                ParameterizedType parameterizedType = (ParameterizedType)intf;
                if (Request.class.equals(parameterizedType.getRawType())) {
                    return parameterizedType;
                }
                
                if (Request.class.isAssignableFrom((Class<?>)parameterizedType.getRawType())) {
                    return resolveRequestInterface((Class<?>)parameterizedType.getRawType());
                }
            }

            return resolveRequestInterface(type.getSuperclass());
        }
    }
}