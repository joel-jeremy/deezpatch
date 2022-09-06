package io.github.joeljeremy7.deezpatch.queries;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * The base class for all query objects. For queries to be dispatchable via 
 * {@link QueryDispatcher}, they must extend this class.
 * 
 * <blockquote><pre>
 * public class GetMerchantByIdQuery extends Query{@code <}Merchant{@code >} {
 *     private final String merchantId;
 * 
 *     public MyQuery(String merchantId) {
 *         this.merchantId = merchantId;
 *     }
 * 
 *     public String merchantId() {
 *         return merchantId;
 *     }
 * }
 * </pre></blockquote>
 */
public abstract class Query<R> {
    private final Type resultType;

    protected Query() {
        ParameterizedType superClass = 
            (ParameterizedType)getClass().getGenericSuperclass();
        // This is TResult.
        resultType = superClass.getActualTypeArguments()[0];
    }

    public final Type resultType() {
        return resultType;
    }
}
