package io.github.joeljeremy7.deezpatch.queries;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Uniquely identifies a query based on the combination of the query type and 
 * the query result type.
 * 
 * @apiNote This needs to be instantiated as an anonymous class in order for the 
 * type parameters to be detected e.g. 
 * <code>new QueryType{@literal <}GetMerchantById, Merchant{@literal >}(){}</code>.
 * 
 * @param <Q> The query type.  
 * @param <R> The query result type.
 */
public abstract class QueryType<Q, R> {
    private final Type queryType;
    private final Type resultType;

    /**
     * Default constructor. Auto-detect the query type and result type
     * from the generic type parameters {@link Q} and {@link R}.
     */
    protected QueryType() {
        ParameterizedType superClass = 
            (ParameterizedType)getClass().getGenericSuperclass();
        Type[] typeParams = superClass.getActualTypeArguments();
        // This is Q.
        queryType = typeParams[0];
        // This is R.
        resultType = typeParams[1];
    }

    /**
     * Used by {@link QueryType#from(Type, Type)}.
     * 
     * @param queryType The query type.
     * @param resultType The result type.
     */
    private QueryType(Type queryType, Type resultType) {
        this.queryType = requireNonNull(queryType);
        this.resultType = requireNonNull(resultType);
    }

    /**
     * The query type.
     * @return The query type.
     */
    public Type queryType() {
        return queryType;
    }

    /**
     * The query's result type.
     * @return The query's result type.
     */
    public Type resultType() {
        return resultType;
    }

    /**
     * @implNote Two {@link QueryType}s are equal when their query type and 
     * query result type are the same.
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QueryType) {
            QueryType<?, ?> q = (QueryType<?, ?>)obj;
            return Objects.equals(queryType, q.queryType) &&
                Objects.equals(resultType, q.resultType);
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return Objects.hash(queryType, resultType);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "[queryType=" + queryType.getTypeName() + "," + 
            "resultType=" + resultType.getTypeName() + "]";
    }

    /**
     * Create a {@link QueryType} from the {@link Query} object.
     * 
     * @param <Q> The query type.
     * @param <R> The query result type.
     * @param query The query object.
     * @return The query type.
     */
    public static <Q extends Query<R>, R> QueryType<Q, R> from(Query<R> query) {
        return new QueryType<>(query.getClass(), query.resultType()){};
    }

    /**
     * Create a {@link QueryType} from a query and query result type.
     * 
     * @param queryType The query type.
     * @param resultType The query result type.
     * @return The query type.
     */
    public static QueryType<?, ?> from(Type queryType, Type resultType) {
        return new QueryType<>(queryType, resultType){};
    }
}