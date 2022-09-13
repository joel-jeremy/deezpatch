package io.github.joeljeremy7.deezpatch.core;

import io.github.joeljeremy7.deezpatch.core.testentities.IntegerRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RequestTypeTests {
    @Nested
    class Constructors {
        @Test
        @DisplayName("should set request type based on type parameter (T)")
        void test1() {
            RequestType<IntegerRequest, Integer> requestType = new RequestType<>(){};
            assertEquals(IntegerRequest.class, requestType.requestType());
        }

        @Test
        @DisplayName("should set result type based on type parameter (R)")
        void test2() {
            RequestType<IntegerRequest, Integer> requestType = new RequestType<>(){};
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on type parameter (T) " +
            "(request extends a base class)"
        )
        void test3() {
            RequestType<TestRequestWithBaseClass, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on type parameter (R) " + 
            "(request extends a base class)"
        )
        void test4() {
            RequestType<TestRequestWithBaseClass, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on type parameter (T) " +
            "(request implements a base interface)"
        )
        void test5() {
            RequestType<TestRequestWithBaseInterface, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on type parameter (R) " + 
            "(request implements base interface)"
        )
        void test6() {
            RequestType<TestRequestWithBaseInterface, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on type parameter (T) " +
            "(request extends a base class which extends another)"
        )
        void test7() {
            RequestType<TestRequestWithAnotherBaseClass, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(
                TestRequestWithAnotherBaseClass.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on type parameter (R) " + 
            "(request extends base class which extends another)"
        )
        void test8() {
            RequestType<TestRequestWithAnotherBaseClass, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on type parameter (T) " +
            "(request implements a base interface which extends another)"
        )
        void test9() {
            RequestType<TestRequestWithAnotherBaseInterface, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(
                TestRequestWithAnotherBaseInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on type parameter (R) " + 
            "(request implements a base interface which extends another)"
        )
        void test10() {
            RequestType<TestRequestWithAnotherBaseInterface, Integer> requestType = 
                new RequestType<>(){};
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on type parameter (T) " + 
            "(request implements other interfaces aside from Request)"
        )
        void test11() {
            RequestType<TestRequestWithOtherNonRequestInterface, Void> requestType = 
                new RequestType<>(){};
            assertEquals(
                TestRequestWithOtherNonRequestInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on type parameter (R) " + 
            "(request implements other interfaces aside from Request)"
        )
        void test12() {
            RequestType<TestRequestWithOtherNonRequestInterface, Void> requestType = 
                new RequestType<>(){};
            assertEquals(Void.class, requestType.resultType());
        }
    }

    /** {@link RequestType#from(Request)} */
    @Nested
    class FromFactoryMethodWithRequestOverload {
        @Test
        @DisplayName("should throw when request argument is null")
        void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> RequestType.from((Request<?>)null)
            );
        }

        @Test
        @DisplayName("should set request type based on request argument's type")
        void test2() {
            RequestType<IntegerRequest, Integer> requestType = 
                RequestType.from(new IntegerRequest("1"));
            assertEquals(IntegerRequest.class, requestType.requestType());
        }

        @Test
        @DisplayName("should set result type based on request argument's type")
        void test3() {
            RequestType<IntegerRequest, Integer> requestType = 
                RequestType.from(new IntegerRequest("1"));
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request argument's type " +
            "(request has base class)"
        )
        void test4() {
            RequestType<TestRequestWithBaseClass, Integer> requestType = 
                RequestType.from(new TestRequestWithBaseClass());
            assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on request argument's type " + 
            "(request has base class)"
        )
        void test5() {
            RequestType<TestRequestWithBaseClass, Integer> requestType = 
                RequestType.from(new TestRequestWithBaseClass());
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request argument's type " +
            "(request has base interface)"
        )
        void test6() {
            RequestType<TestRequestWithBaseInterface, Integer> requestType = 
                RequestType.from(new TestRequestWithBaseInterface());
            assertEquals(
                TestRequestWithBaseInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request argument's type " + 
            "(request has base interface)"
        )
        void test7() {
            RequestType<TestRequestWithBaseInterface, Integer> requestType = 
                RequestType.from(new TestRequestWithBaseInterface());
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request argument's type " +
            "(request extends a base class which extends another)"
        )
        void test8() {
            RequestType<TestRequestWithAnotherBaseClass, Integer> requestType = 
                RequestType.from(new TestRequestWithAnotherBaseClass());
            assertEquals(
                TestRequestWithAnotherBaseClass.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request argument's type " + 
            "(request extends base class which extends another)"
        )
        void test9() {
            RequestType<TestRequestWithAnotherBaseClass, Integer> requestType = 
                RequestType.from(new TestRequestWithAnotherBaseClass());
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request argument's type " +
            "(request implements a base interface which extends another)"
        )
        void test10() {
            RequestType<TestRequestWithAnotherBaseInterface, Integer> requestType = 
                RequestType.from(new TestRequestWithAnotherBaseInterface());
            assertEquals(
                TestRequestWithAnotherBaseInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request argument's type " + 
            "(request implements a base interface which extends another)"
        )
        void test11() {
            RequestType<TestRequestWithAnotherBaseInterface, Integer> requestType = 
                RequestType.from(new TestRequestWithAnotherBaseInterface());
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request argument's type " + 
            "(request implements other interfaces aside from Request)"
        )
        void test12() {
            RequestType<TestRequestWithOtherNonRequestInterface, Void> requestType = 
                RequestType.from(new TestRequestWithOtherNonRequestInterface());
            assertEquals(
                TestRequestWithOtherNonRequestInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request argument's type " + 
            "(request implements other interfaces aside from Request)"
        )
        void test13() {
            RequestType<TestRequestWithOtherNonRequestInterface, Void> requestType = 
                RequestType.from(new TestRequestWithOtherNonRequestInterface());
            assertEquals(Void.class, requestType.resultType());
        }
    }

    /** {@link RequestType#from(Class)} */
    @Nested
    class FromFactoryMethodWithClassOverload {
        @Test
        @DisplayName("should throw when request type argument is null")
        void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> RequestType.from((Class<?>)null)
            );
        }

        @Test
        @DisplayName("should set request type based on request type/class argument")
        void test2() {
            RequestType<IntegerRequest, Integer> requestType = 
                RequestType.from(IntegerRequest.class);
            assertEquals(IntegerRequest.class, requestType.requestType());
        }

        @Test
        @DisplayName("should set result type based on request type/class argument")
        void test3() {
            RequestType<IntegerRequest, Integer> requestType = 
                RequestType.from(IntegerRequest.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type/class argument " +
            "(request has base class)"
        )
        void test4() {
            RequestType<TestRequestWithBaseClass, Integer> requestType = 
                RequestType.from(TestRequestWithBaseClass.class);
            assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on request type/class argument " + 
            "(request has base class)"
        )
        void test5() {
            RequestType<TestRequestWithBaseClass, Integer> requestType = 
                RequestType.from(TestRequestWithBaseClass.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type/class argument " +
            "(request has base interface)"
        )
        void test6() {
            RequestType<TestRequestWithBaseInterface, Integer> requestType = 
                RequestType.from(TestRequestWithBaseInterface.class);
            assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on request type/class argument " + 
            "(request has base interface)"
        )
        void test7() {
            RequestType<TestRequestWithBaseInterface, Integer> requestType = 
                RequestType.from(TestRequestWithBaseInterface.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type/class argument " +
            "(request extends a base class which extends another)"
        )
        void test8() {
            RequestType<TestRequestWithAnotherBaseClass, Integer> requestType = 
                RequestType.from(TestRequestWithAnotherBaseClass.class);
            assertEquals(
                TestRequestWithAnotherBaseClass.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request type/class argument " + 
            "(request extends base class which extends another)"
        )
        void test9() {
            RequestType<TestRequestWithAnotherBaseClass, Integer> requestType = 
                RequestType.from(TestRequestWithAnotherBaseClass.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type/class argument " +
            "(request implements a base interface which extends another)"
        )
        void test10() {
            RequestType<TestRequestWithAnotherBaseInterface, Integer> requestType = 
                RequestType.from(TestRequestWithAnotherBaseInterface.class);
            assertEquals(
                TestRequestWithAnotherBaseInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request type/class argument " + 
            "(request implements a base interface which extends another)"
        )
        void test11() {
            RequestType<TestRequestWithAnotherBaseInterface, Integer> requestType = 
                RequestType.from(TestRequestWithAnotherBaseInterface.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type/class argument " + 
            "(request implements other interfaces aside from Request)"
        )
        void test12() {
            RequestType<TestRequestWithOtherNonRequestInterface, Void> requestType = 
                RequestType.from(TestRequestWithOtherNonRequestInterface.class);
            assertEquals(
                TestRequestWithOtherNonRequestInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request type/class argument " + 
            "(request implements other interfaces aside from Request)"
        )
        void test13() {
            RequestType<TestRequestWithOtherNonRequestInterface, Void> requestType = 
                RequestType.from(TestRequestWithOtherNonRequestInterface.class);
            assertEquals(Void.class, requestType.resultType());
        }
    }

    /** {@link RequestType#from(Type)} */
    @Nested
    class FromFactoryMethodWithTypeOverload {
        @Test
        @DisplayName("should throw when request type argument is null")
        void test1() {
            assertThrows(
                NullPointerException.class, 
                () -> RequestType.from((Type)null)
            );
        }

        @Test
        @DisplayName("should set request type based on request type argument")
        void test2() {
            RequestType<?, ?> requestType = RequestType.from((Type)IntegerRequest.class);
            assertEquals(IntegerRequest.class, requestType.requestType());
        }

        @Test
        @DisplayName("should set result type based on request type argument")
        void test3() {
            RequestType<?, ?> requestType = RequestType.from((Type)IntegerRequest.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type argument " +
            "(request has base class)"
        )
        void test4() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithBaseClass.class);
            assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on request type argument " + 
            "(request has base class)"
        )
        void test5() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithBaseClass.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type argument " +
            "(request has base interface)"
        )
        void test6() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithBaseInterface.class);
            assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
        }

        @Test
        @DisplayName(
            "should set result type based on request type argument " + 
            "(request has base interface)"
        )
        void test7() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithBaseInterface.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type argument " +
            "(request extends a base class which extends another)"
        )
        void test8() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithAnotherBaseClass.class);
            assertEquals(
                TestRequestWithAnotherBaseClass.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request type argument " + 
            "(request extends base class which extends another)"
        )
        void test9() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithAnotherBaseClass.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type argument " +
            "(request implements a base interface which extends another)"
        )
        void test10() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithAnotherBaseInterface.class);
            assertEquals(
                TestRequestWithAnotherBaseInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request type argument " + 
            "(request implements a base interface which extends another)"
        )
        void test11() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithAnotherBaseInterface.class);
            assertEquals(Integer.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should set request type based on request type argument " + 
            "(request implements other interfaces aside from Request)"
        )
        void test12() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithOtherNonRequestInterface.class);
            assertEquals(
                TestRequestWithOtherNonRequestInterface.class, 
                requestType.requestType()
            );
        }

        @Test
        @DisplayName(
            "should set result type based on request type argument " + 
            "(request implements other interfaces aside from Request)"
        )
        void test13() {
            RequestType<?, ?> requestType = 
                RequestType.from((Type)TestRequestWithOtherNonRequestInterface.class);
            assertEquals(Void.class, requestType.resultType());
        }

        @Test
        @DisplayName(
            "should throw when request type argument does not implement Request"
        )
        void test14() {
            assertThrows(
                IllegalArgumentException.class, 
                () -> RequestType.from((Type)String.class)
            );
        }
    }

    @Nested
    class EqualsMethod {
        @Test
        @DisplayName("should return true when request and result type values are the same")
        void test1() {
            RequestType<IntegerRequest, Integer> requestType1 =
                new RequestType<IntegerRequest, Integer>(){};
            RequestType<IntegerRequest, Integer> requestType2 =
                RequestType.from(IntegerRequest.class);

            assertTrue(requestType1.equals(requestType2));
        }

        @Test
        @DisplayName(
            "should return false when request type values are not the same"
        )
        void test2() {
            RequestType<IntegerRequest, Integer> requestType1 =
                new RequestType<IntegerRequest,Integer>(){};
            RequestType<TestRequestWithVoidResult, Void> requestType2 =
                RequestType.from(TestRequestWithVoidResult.class);

            assertFalse(requestType1.equals(requestType2));
        }

        @Test
        @DisplayName(
            "should return false when result type values are not the same"
        )
        void test3() {
            RequestType<IntegerRequest, Integer> requestType1 =
                new RequestType<IntegerRequest,Integer>(){
                    @Override
                    public Type resultType() {
                        // For some reason, result type resolved to a different type.
                        // This should not happen but let's include tests for it.
                        return Void.class;
                    }
                };
            RequestType<IntegerRequest, Integer> requestType2 =
                RequestType.from(IntegerRequest.class);

            assertFalse(requestType1.equals(requestType2));
        }

        @Test
        @DisplayName("should return false when the other object is null")
        void test4() {
            RequestType<IntegerRequest, Integer> requestType1 =
                new RequestType<IntegerRequest,Integer>(){};

            assertFalse(requestType1.equals(null));
        }

        @Test
        @DisplayName("should return false when the other object is not a RequestType object")
        void test5() {
            RequestType<IntegerRequest, Integer> requestType1 =
                new RequestType<IntegerRequest,Integer>(){};

            Object notARequestType = "Not a RequestType<?>!";

            assertFalse(requestType1.equals(notARequestType));
        }
    }

    @Nested
    class HashCodeMethod {
        @Test
        @DisplayName("should return hash code of request and result types")
        void test1() {
            RequestType<IntegerRequest, Integer> requestType =
                new RequestType<IntegerRequest, Integer>(){};

            int hashCode = Objects.hash(
                requestType.requestType(),
                requestType.resultType()
            );

            assertEquals(hashCode, requestType.hashCode());
        }

        @Test
        @DisplayName("should return the same hash code when invoked multiple times")
        void test2() {
            RequestType<IntegerRequest, Integer> requestType =
                new RequestType<IntegerRequest, Integer>(){};

            int hashCode1 = requestType.hashCode();
            int hashCode2 = requestType.hashCode();
                
            assertEquals(hashCode1, hashCode2);
        }
    }

    /** Has other interfaces other than {@link Request}. */
    static class TestRequestWithOtherNonRequestInterface 
            implements Serializable, Supplier<String>, Request<Void> {
        @Override
        public String get() {
            return "Dummy";
        }
    }
    /** Request with Void result. */
    static class TestRequestWithVoidResult implements Request<Void> {}
    /** Request with Integer result. */
    static class TestRequestWithIntegerResult implements Request<Integer> {}

    static abstract class BaseRequestClass<T> implements Request<T> {}
    static abstract class AnotherBaseRequestClass<T> extends BaseRequestClass<T> {}

    /** Request which extends a base class that implements {@link Request}. */
    static class TestRequestWithBaseClass extends BaseRequestClass<Integer> {}
    /** 
     * Request which extends a base class that extends another class that 
     * implements {@link Request}. 
     */
    static class TestRequestWithAnotherBaseClass 
            extends AnotherBaseRequestClass<Integer> {}

    static interface BaseRequestInterface<T> extends Request<T> {}
    static interface AnotherBaseRequestInterface<T> extends BaseRequestInterface<T> {}
    
    /** Request which implements an interface that extends {@link Request}. */
    static class TestRequestWithBaseInterface 
            implements BaseRequestInterface<Integer> {}
    /** 
     * Request which implements an interface that extends other interfaces
     * that extends {@link Request}. 
     */
    static class TestRequestWithAnotherBaseInterface 
            implements AnotherBaseRequestInterface<Integer> {}
}
