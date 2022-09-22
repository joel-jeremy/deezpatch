package io.github.joeljeremy7.deezpatch.core.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class WeakConcurrentMapTests {
  @Nested
  class WeakKeyReferenceTests {
    @Test
    @DisplayName("should automatically remove mapping when key references are cleared")
    void test1() {
      TestKey key1 = new TestKey("map.key.1");
      TestKey key2 = new TestKey("map.key.2");

      WeakConcurrentMap<TestKey, String> map = new WeakConcurrentMap<>();

      // Put and verify.
      map.putIfAbsent(key1, "map.value.1");
      map.putIfAbsent(key2, "map.value.2");
      assertNotNull(map.get(key1));
      assertNotNull(map.get(key2));

      // Clear references.
      key1 = null;
      key2 = null;

      // Original key references were nulled so create a new reference
      // with matching string keys for lookups.
      TestKey key1Lookup = new TestKey("map.key.1");
      TestKey key2Lookup = new TestKey("map.key.2");

      assertTimeoutPreemptively(
          Duration.ofMinutes(10),
          () -> {
            // Wait for GC to clear references.
            while (map.get(key1Lookup) != null || map.get(key2Lookup) != null) {
              System.gc();
            }
          });

      assertNull(map.get(key1Lookup));
      assertNull(map.get(key2Lookup));
    }
  }

  @Nested
  class PutIfAbsentMethod {
    @Test
    @DisplayName("should put value to the map when no mapping exists")
    void test1() {
      String key = "map.key";
      String value = "map.value";

      WeakConcurrentMap<String, String> map = new WeakConcurrentMap<>();
      map.putIfAbsent(key, value);

      assertEquals(value, map.get(key));
    }

    @Test
    @DisplayName("should not put value when a mapping already exists")
    void test2() {
      String key = "map.key";
      String value = "map.value";

      WeakConcurrentMap<String, String> map = new WeakConcurrentMap<>();
      // Add an existing mapping.
      map.putIfAbsent(key, value);

      map.putIfAbsent(key, "new.value");

      // Returns existing value.
      assertEquals(value, map.get(key));
    }
  }

  @Nested
  class ComputeIfAbsentMethod {
    @Test
    @DisplayName("should compute value and put to the map when no mapping exists")
    void test1() {
      AtomicBoolean mappingFunctionInvoked = new AtomicBoolean();
      String key = "map.key";
      String value = "map.value";

      WeakConcurrentMap<String, String> map = new WeakConcurrentMap<>();
      map.computeIfAbsent(
          key,
          k -> {
            mappingFunctionInvoked.set(true);
            return value;
          });

      assertTrue(mappingFunctionInvoked.get());
      assertEquals(value, map.get(key));
    }

    @Test
    @DisplayName("should not compute value when a mapping already exists")
    void test2() {
      AtomicBoolean mappingFunctionInvoked = new AtomicBoolean();
      String key = "map.key";
      String value = "map.value";

      WeakConcurrentMap<String, String> map = new WeakConcurrentMap<>();
      // Add an existing mapping.
      map.putIfAbsent(key, value);

      map.computeIfAbsent(
          key,
          k -> {
            mappingFunctionInvoked.set(true);
            return "computed.value";
          });

      // Mapping function not invoked and returns existing value.
      assertFalse(mappingFunctionInvoked.get());
      assertEquals(value, map.get(key));
    }
  }

  @Nested
  class GetMethod {
    @Test
    @DisplayName("should return value associated to the key")
    void test1() {
      String key = "map.key";
      String value = "map.value";

      WeakConcurrentMap<String, String> map = new WeakConcurrentMap<>();
      map.putIfAbsent(key, value);

      // Retrieve.
      String retrievedValue = map.get(key);
      assertSame(value, retrievedValue);
    }

    @Test
    @DisplayName("should return null when key is not found in map")
    void test2() {
      String key = "map.key";

      // Empty map.
      WeakConcurrentMap<String, String> map = new WeakConcurrentMap<>();

      String retrievedValue = map.get(key);
      assertNull(retrievedValue);
    }
  }

  @Nested
  class WeakKeyTests {
    @Nested
    class HashCodeMethod {
      @Test
      @DisplayName("should return hash code of the referent")
      void test1() {
        String referent = "referent";
        WeakConcurrentMap.WeakKey<String> key = WeakConcurrentMap.WeakKey.forLookup(referent);
        assertEquals(referent.hashCode(), key.hashCode());
      }

      @Test
      @DisplayName("should return the same of hash code everytime")
      void test2() {
        String referent = "referent";
        WeakConcurrentMap.WeakKey<String> key = WeakConcurrentMap.WeakKey.forLookup(referent);
        int hashCode1 = key.hashCode();
        int hashCode2 = key.hashCode();
        assertEquals(hashCode1, hashCode2);
      }

      @Test
      @DisplayName("should return different hash codes for different referents")
      void test3() {
        String referent1 = "referent1";
        WeakConcurrentMap.WeakKey<String> key = WeakConcurrentMap.WeakKey.forLookup(referent1);

        String referent2 = "referent2";
        WeakConcurrentMap.WeakKey<String> otherKey = WeakConcurrentMap.WeakKey.forLookup(referent2);

        assertNotEquals(key.hashCode(), otherKey.hashCode());
      }
    }

    @Nested
    class EqualsMethod {
      @Test
      @DisplayName("should return true when WeakKey referents are equal")
      void test1() {
        String referent = "referent";
        WeakConcurrentMap.WeakKey<String> key = WeakConcurrentMap.WeakKey.forLookup(referent);

        WeakConcurrentMap.WeakKey<String> sameReferentKey =
            WeakConcurrentMap.WeakKey.forLookup(referent);

        assertTrue(key.equals(sameReferentKey));
      }

      @Test
      @DisplayName("should return false when WeakKey referents are not equal")
      void test2() {
        String referent1 = "referent1";
        WeakConcurrentMap.WeakKey<String> key = WeakConcurrentMap.WeakKey.forLookup(referent1);

        String referent2 = "referent2";
        WeakConcurrentMap.WeakKey<String> otherKey = WeakConcurrentMap.WeakKey.forLookup(referent2);

        assertFalse(key.equals(otherKey));
      }

      @Test
      @DisplayName("should return false when object is not a WeakKey")
      void test3() {
        String referent = "referent";
        WeakConcurrentMap.WeakKey<String> key = WeakConcurrentMap.WeakKey.forLookup(referent);

        assertFalse(key.equals(new Object()));
      }
    }
  }

  public static class TestKey {
    private final String key;

    public TestKey(String key) {
      this.key = key;
    }

    public String key() {
      return key;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }

      if (obj instanceof TestKey) {
        TestKey other = (TestKey) obj;
        return Objects.equals(other.key, key);
      }

      return false;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(key);
    }
  }
}
