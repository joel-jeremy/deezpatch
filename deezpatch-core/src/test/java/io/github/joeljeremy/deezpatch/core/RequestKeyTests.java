package io.github.joeljeremy.deezpatch.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.joeljeremy.deezpatch.core.testentities.IntegerRequest;
import io.github.joeljeremy.deezpatch.core.testentities.ListRequest;
import io.github.joeljeremy.deezpatch.core.testentities.TestRequest;
import io.github.joeljeremy.deezpatch.core.testentities.TestResult;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class RequestKeyTests {
  @Nested
  class Constructors {
    @Test
    @DisplayName("should set request type based on type parameter (T)")
    void test1() {
      RequestKey<IntegerRequest, Integer> requestType = new RequestKey<>() {};

      assertEquals(IntegerRequest.class, requestType.requestType());
    }

    @Test
    @DisplayName("should set result type based on type parameter (R)")
    void test2() {
      RequestKey<IntegerRequest, Integer> requestType = new RequestKey<>() {};

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on type parameter (T) " + "(request extends a base class)")
    void test3() {
      RequestKey<TestRequestWithBaseClass, Integer> requestType = new RequestKey<>() {};

      assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on type parameter (R) " + "(request extends a base class)")
    void test4() {
      RequestKey<TestRequestWithBaseClass, Integer> requestType = new RequestKey<>() {};

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on type parameter (T) "
            + "(request implements a base interface)")
    void test5() {
      RequestKey<TestRequestWithBaseInterface, Integer> requestType = new RequestKey<>() {};

      assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on type parameter (R) "
            + "(request implements base interface)")
    void test6() {
      RequestKey<TestRequestWithBaseInterface, Integer> requestType = new RequestKey<>() {};

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on type parameter (T) "
            + "(request extends a base class which extends another)")
    void test7() {
      RequestKey<TestRequestWithAnotherBaseClass, Integer> requestType = new RequestKey<>() {};

      assertEquals(TestRequestWithAnotherBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on type parameter (R) "
            + "(request extends base class which extends another)")
    void test8() {
      RequestKey<TestRequestWithAnotherBaseClass, Integer> requestType = new RequestKey<>() {};

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on type parameter (T) "
            + "(request implements a base interface which extends another)")
    void test9() {
      RequestKey<TestRequestWithAnotherBaseInterface, Integer> requestType = new RequestKey<>() {};

      assertEquals(TestRequestWithAnotherBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on type parameter (R) "
            + "(request implements a base interface which extends another)")
    void test10() {
      RequestKey<TestRequestWithAnotherBaseInterface, Integer> requestType = new RequestKey<>() {};

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on type parameter (T) "
            + "(request implements other interfaces aside from Request)")
    void test11() {
      RequestKey<TestRequestWithOtherNonRequestInterface, Void> requestType = new RequestKey<>() {};

      assertEquals(TestRequestWithOtherNonRequestInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on type parameter (R) "
            + "(request implements other interfaces aside from Request)")
    void test12() {
      RequestKey<TestRequestWithOtherNonRequestInterface, Void> requestType = new RequestKey<>() {};

      assertEquals(Void.class, requestType.resultType());
    }
  }

  /** {@link RequestKey#from(Request)} */
  @Nested
  class FromFactoryMethodWithRequestOverload {
    @Test
    @DisplayName("should throw when request argument is null")
    void test1() {
      assertThrows(NullPointerException.class, () -> RequestKey.from((Request<?>) null));
    }

    @Test
    @DisplayName("should set request type based on request argument's type")
    void test2() {
      RequestKey<IntegerRequest, Integer> requestType = RequestKey.from(new IntegerRequest("1"));

      assertEquals(IntegerRequest.class, requestType.requestType());
    }

    @Test
    @DisplayName("should set result type based on request argument's type")
    void test3() {
      RequestKey<IntegerRequest, Integer> requestType = RequestKey.from(new IntegerRequest("1"));

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request argument's type " + "(request has base class)")
    void test4() {
      RequestKey<TestRequestWithBaseClass, Integer> requestType =
          RequestKey.from(new TestRequestWithBaseClass());

      assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request argument's type " + "(request has base class)")
    void test5() {
      RequestKey<TestRequestWithBaseClass, Integer> requestType =
          RequestKey.from(new TestRequestWithBaseClass());

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request argument's type "
            + "(request has base interface)")
    void test6() {
      RequestKey<TestRequestWithBaseInterface, Integer> requestType =
          RequestKey.from(new TestRequestWithBaseInterface());

      assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request argument's type " + "(request has base interface)")
    void test7() {
      RequestKey<TestRequestWithBaseInterface, Integer> requestType =
          RequestKey.from(new TestRequestWithBaseInterface());

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request argument's type "
            + "(request extends a base class which extends another)")
    void test8() {
      RequestKey<TestRequestWithAnotherBaseClass, Integer> requestType =
          RequestKey.from(new TestRequestWithAnotherBaseClass());

      assertEquals(TestRequestWithAnotherBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request argument's type "
            + "(request extends base class which extends another)")
    void test9() {
      RequestKey<TestRequestWithAnotherBaseClass, Integer> requestType =
          RequestKey.from(new TestRequestWithAnotherBaseClass());

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request argument's type "
            + "(request implements a base interface which extends another)")
    void test10() {
      RequestKey<TestRequestWithAnotherBaseInterface, Integer> requestType =
          RequestKey.from(new TestRequestWithAnotherBaseInterface());

      assertEquals(TestRequestWithAnotherBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request argument's type "
            + "(request implements a base interface which extends another)")
    void test11() {
      RequestKey<TestRequestWithAnotherBaseInterface, Integer> requestType =
          RequestKey.from(new TestRequestWithAnotherBaseInterface());

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request argument's type "
            + "(request implements other interfaces aside from Request)")
    void test12() {
      RequestKey<TestRequestWithOtherNonRequestInterface, Void> requestType =
          RequestKey.from(new TestRequestWithOtherNonRequestInterface());

      assertEquals(TestRequestWithOtherNonRequestInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request argument's type "
            + "(request implements other interfaces aside from Request)")
    void test13() {
      RequestKey<TestRequestWithOtherNonRequestInterface, Void> requestType =
          RequestKey.from(new TestRequestWithOtherNonRequestInterface());

      assertEquals(Void.class, requestType.resultType());
    }
  }

  /** {@link RequestKey#from(Class)} */
  @Nested
  class FromFactoryMethodWithClassOverload {
    @Test
    @DisplayName("should throw when request type argument is null")
    void test1() {
      assertThrows(NullPointerException.class, () -> RequestKey.from((Class<?>) null));
    }

    @Test
    @DisplayName("should set request type based on request type/class argument")
    void test2() {
      RequestKey<IntegerRequest, Integer> requestType = RequestKey.from(IntegerRequest.class);

      assertEquals(IntegerRequest.class, requestType.requestType());
    }

    @Test
    @DisplayName("should set result type based on request type/class argument")
    void test3() {
      RequestKey<IntegerRequest, Integer> requestType = RequestKey.from(IntegerRequest.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type/class argument "
            + "(request has base class)")
    void test4() {
      RequestKey<TestRequestWithBaseClass, Integer> requestType =
          RequestKey.from(TestRequestWithBaseClass.class);

      assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type/class argument " + "(request has base class)")
    void test5() {
      RequestKey<TestRequestWithBaseClass, Integer> requestType =
          RequestKey.from(TestRequestWithBaseClass.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type/class argument "
            + "(request has base interface)")
    void test6() {
      RequestKey<TestRequestWithBaseInterface, Integer> requestType =
          RequestKey.from(TestRequestWithBaseInterface.class);

      assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type/class argument "
            + "(request has base interface)")
    void test7() {
      RequestKey<TestRequestWithBaseInterface, Integer> requestType =
          RequestKey.from(TestRequestWithBaseInterface.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type/class argument "
            + "(request extends a base class which extends another)")
    void test8() {
      RequestKey<TestRequestWithAnotherBaseClass, Integer> requestType =
          RequestKey.from(TestRequestWithAnotherBaseClass.class);

      assertEquals(TestRequestWithAnotherBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type/class argument "
            + "(request extends base class which extends another)")
    void test9() {
      RequestKey<TestRequestWithAnotherBaseClass, Integer> requestType =
          RequestKey.from(TestRequestWithAnotherBaseClass.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type/class argument "
            + "(request implements a base interface which extends another)")
    void test10() {
      RequestKey<TestRequestWithAnotherBaseInterface, Integer> requestType =
          RequestKey.from(TestRequestWithAnotherBaseInterface.class);

      assertEquals(TestRequestWithAnotherBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type/class argument "
            + "(request implements a base interface which extends another)")
    void test11() {
      RequestKey<TestRequestWithAnotherBaseInterface, Integer> requestType =
          RequestKey.from(TestRequestWithAnotherBaseInterface.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type/class argument "
            + "(request implements other interfaces aside from Request)")
    void test12() {
      RequestKey<TestRequestWithOtherNonRequestInterface, Void> requestType =
          RequestKey.from(TestRequestWithOtherNonRequestInterface.class);

      assertEquals(TestRequestWithOtherNonRequestInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type/class argument "
            + "(request implements other interfaces aside from Request)")
    void test13() {
      RequestKey<TestRequestWithOtherNonRequestInterface, Void> requestType =
          RequestKey.from(TestRequestWithOtherNonRequestInterface.class);

      assertEquals(Void.class, requestType.resultType());
    }
  }

  /** {@link RequestKey#from(Type)} */
  @Nested
  class FromFactoryMethodWithTypeOverload {
    @Test
    @DisplayName("should throw when request type argument is null")
    void test1() {
      assertThrows(NullPointerException.class, () -> RequestKey.from((Type) null));
    }

    @Test
    @DisplayName("should set request type based on request type argument")
    void test2() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) IntegerRequest.class);

      assertEquals(IntegerRequest.class, requestType.requestType());
    }

    @Test
    @DisplayName("should set result type based on request type argument")
    void test3() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) IntegerRequest.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type argument " + "(request has base class)")
    void test4() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) TestRequestWithBaseClass.class);

      assertEquals(TestRequestWithBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type argument " + "(request has base class)")
    void test5() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) TestRequestWithBaseClass.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type argument " + "(request has base interface)")
    void test6() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) TestRequestWithBaseInterface.class);

      assertEquals(TestRequestWithBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type argument " + "(request has base interface)")
    void test7() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) TestRequestWithBaseInterface.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type argument "
            + "(request extends a base class which extends another)")
    void test8() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) TestRequestWithAnotherBaseClass.class);

      assertEquals(TestRequestWithAnotherBaseClass.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type argument "
            + "(request extends base class which extends another)")
    void test9() {
      RequestKey<?, ?> requestType = RequestKey.from((Type) TestRequestWithAnotherBaseClass.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type argument "
            + "(request implements a base interface which extends another)")
    void test10() {
      RequestKey<?, ?> requestType =
          RequestKey.from((Type) TestRequestWithAnotherBaseInterface.class);

      assertEquals(TestRequestWithAnotherBaseInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type argument "
            + "(request implements a base interface which extends another)")
    void test11() {
      RequestKey<?, ?> requestType =
          RequestKey.from((Type) TestRequestWithAnotherBaseInterface.class);

      assertEquals(Integer.class, requestType.resultType());
    }

    @Test
    @DisplayName(
        "should set request type based on request type argument "
            + "(request implements other interfaces aside from Request)")
    void test12() {
      RequestKey<?, ?> requestType =
          RequestKey.from((Type) TestRequestWithOtherNonRequestInterface.class);

      assertEquals(TestRequestWithOtherNonRequestInterface.class, requestType.requestType());
    }

    @Test
    @DisplayName(
        "should set result type based on request type argument "
            + "(request implements other interfaces aside from Request)")
    void test13() {
      RequestKey<?, ?> requestType =
          RequestKey.from((Type) TestRequestWithOtherNonRequestInterface.class);

      assertEquals(Void.class, requestType.resultType());
    }

    @Test
    @DisplayName("should throw when request type argument does not implement Request")
    void test14() {
      assertThrows(IllegalArgumentException.class, () -> RequestKey.from((Type) String.class));
    }
  }

  @Nested
  class RequestTypeMethod {
    @Test
    @DisplayName("should return request type")
    void test1() {
      RequestKey<TestRequest, TestResult> requestType = new RequestKey<>() {};

      assertEquals(TestRequest.class, requestType.requestType());
    }
  }

  @Nested
  class ResultTypeMethod {
    @Test
    @DisplayName("should return result type")
    void test1() {
      RequestKey<TestRequest, TestResult> requestType = new RequestKey<>() {};

      assertEquals(TestResult.class, requestType.resultType());
    }
  }

  @Nested
  class RawRequestTypeMethod {
    @Test
    @DisplayName("should return raw request type")
    void test1() {
      RequestKey<TestRequest, TestResult> requestType = new RequestKey<>() {};

      assertEquals(TestRequest.class, requestType.rawRequestType());
    }
  }

  @Nested
  class RawResultTypeMethod {
    @Test
    @DisplayName("should return raw result type")
    void test1() {
      RequestKey<ListRequest, List<String>> requestType = new RequestKey<>() {};

      // Raw List.class
      assertEquals(List.class, requestType.rawResultType());
    }
  }

  @Nested
  class EqualsMethod {
    @Test
    @DisplayName("should return true when request and result type values are the same")
    void test1() {
      RequestKey<IntegerRequest, Integer> requestType1 =
          new RequestKey<IntegerRequest, Integer>() {};

      RequestKey<IntegerRequest, Integer> requestType2 = RequestKey.from(IntegerRequest.class);

      assertTrue(requestType1.equals(requestType2));
    }

    @Test
    @DisplayName("should return false when request type values are not the same")
    void test2() {
      RequestKey<IntegerRequest, Integer> requestType1 =
          new RequestKey<IntegerRequest, Integer>() {};

      RequestKey<TestRequestWithVoidResult, Void> requestType2 =
          RequestKey.from(TestRequestWithVoidResult.class);

      assertFalse(requestType1.equals(requestType2));
    }

    @Test
    @DisplayName("should return false when result type values are not the same")
    void test3() {
      RequestKey<IntegerRequest, Integer> requestType1 =
          new RequestKey<IntegerRequest, Integer>() {
            @Override
            public Type resultType() {
              // For some reason, result type resolved to a different type.
              // This should not happen but let's include tests for it.
              return Void.class;
            }
          };
      RequestKey<IntegerRequest, Integer> requestType2 = RequestKey.from(IntegerRequest.class);

      assertFalse(requestType1.equals(requestType2));
    }

    @Test
    @DisplayName("should return false when the other object is null")
    void test4() {
      RequestKey<IntegerRequest, Integer> requestType1 =
          new RequestKey<IntegerRequest, Integer>() {};

      assertFalse(requestType1.equals(null));
    }

    @Test
    @DisplayName("should return false when the other object is not a RequestType object")
    void test5() {
      RequestKey<IntegerRequest, Integer> requestType1 =
          new RequestKey<IntegerRequest, Integer>() {};

      Object notARequestType = "Not a RequestType<?>!";

      assertFalse(requestType1.equals(notARequestType));
    }
  }

  @Nested
  class HashCodeMethod {
    @Test
    @DisplayName("should return hash code of request and result types")
    void test1() {
      RequestKey<IntegerRequest, Integer> requestType =
          new RequestKey<IntegerRequest, Integer>() {};

      int hashCode = Objects.hash(requestType.requestType(), requestType.resultType());

      assertEquals(hashCode, requestType.hashCode());
    }

    @Test
    @DisplayName("should return the same hash code when invoked multiple times")
    void test2() {
      RequestKey<IntegerRequest, Integer> requestType =
          new RequestKey<IntegerRequest, Integer>() {};

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

  static class TestRequestWithVoidResult implements Request<Void> {}

  static class TestRequestWithIntegerResult implements Request<Integer> {}

  abstract static class BaseRequestClass<T> implements Request<T> {}

  abstract static class AnotherBaseRequestClass<T> extends BaseRequestClass<T> {}

  /** Request which extends a base class that implements {@link Request}. */
  static class TestRequestWithBaseClass extends BaseRequestClass<Integer> {}

  /**
   * Request which extends a base class that extends another class that implements {@link Request}.
   */
  static class TestRequestWithAnotherBaseClass extends AnotherBaseRequestClass<Integer> {}

  static interface BaseRequestInterface<T> extends Request<T> {}

  static interface AnotherBaseRequestInterface<T> extends BaseRequestInterface<T> {}

  /** Request which implements an interface that extends {@link Request}. */
  static class TestRequestWithBaseInterface implements BaseRequestInterface<Integer> {}

  /**
   * Request which implements an interface that extends other interfaces that extends {@link
   * Request}.
   */
  static class TestRequestWithAnotherBaseInterface
      implements AnotherBaseRequestInterface<Integer> {}
}
