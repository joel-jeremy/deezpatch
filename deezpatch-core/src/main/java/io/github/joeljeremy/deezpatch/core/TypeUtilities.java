package io.github.joeljeremy.deezpatch.core;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Type-related utility methods. This provides useful utility methods to extract type information
 * from Java's {@link Type} class.
 */
public class TypeUtilities {

  // To prevent repeated creation of arrays just to get the array class.
  private static final ClassValue<Class<?>> ARRAY_TYPE_CACHE =
      new ClassValue<Class<?>>() {
        @Override
        protected Class<?> computeValue(Class<?> type) {
          return Array.newInstance(type, 0).getClass();
        }
      };

  private TypeUtilities() {}

  /**
   * Extract raw class of the given type.
   *
   * @param type The type to derive the raw class from.
   * @return The raw class of the given type.
   * @throws IllegalArgumentException if the type null or is unsupported.
   */
  public static Class<?> getRawType(Type type) {
    requireNonNull(type, "type");

    if (isClass(type)) {
      return (Class<?>) type;
    }

    ParameterizedType pt = asParameterizedType(type);
    if (pt != null) {
      // Return raw type.
      // For example, if type is List<String>, raw List shall be returned.
      return getRawType(pt.getRawType());
    }

    GenericArrayType gat = asGenericArrayType(type);
    if (gat != null) {
      // Return raw component type of array.
      // For example, if type is List<String>[], raw List[] shall be returned.
      Type genericArrayComponentType = gat.getGenericComponentType();
      return getRawArrayType(genericArrayComponentType);
    }

    TypeVariable<?> tv = asTypeVariable(type);
    if (tv != null) {
      // Return type variable upper bound.
      // For example, if type is <T>, Object shall be returned.
      // If type is <T extends Number>, Number shall be returned.
      // If type upper bound is a generic type <T extends List<String>>, return raw List.

      // Only get first because, Java doesn't allow multiple extends as of writing.
      return getRawType(tv.getBounds()[0]);
    }

    WildcardType wt = asWildcardType(type);
    if (wt != null) {
      // Return type variable lower bound or upper bound.
      // If type is <? extends String>, return upper bound which is String.
      // If type if <? super String>, return lower bound which is String.

      // Only get first of the bounds because Java doesn't allow multiple extends/super
      // as of writing.

      // Return lower bound i.e for <? super String>, return String.
      if (wt.getLowerBounds().length > 0) {
        return getRawType(wt.getLowerBounds()[0]);
      }

      // Return upper bound i.e for <? extends String>, return String.
      // If no upper bound is explicitly declared, the upper bound is Object.
      return getRawType(wt.getUpperBounds()[0]);
    }

    throw new IllegalArgumentException("Illegal or unsupported type: " + type.getTypeName());
  }

  /**
   * Extract the specified type's generic type parameters, if it has any.
   *
   * @param type The type to extract type parameters from.
   * @return The array of generic type parameters, if the specified type has any.
   */
  public static Type[] getTypeParameters(Type type) {
    ParameterizedType pt = asParameterizedType(type);
    if (pt != null) {
      // Return generic type parameters.
      // For example, if type is List<String>, String shall be returned.
      return pt.getActualTypeArguments();
    }

    // Class has no generic type parameters.
    return new Type[0];
  }

  /**
   * Check is type is a {@link Class} instance.
   *
   * @param type The type to check.
   * @return {@code true}, if type is a {@link Class}. Otherwise, {@code false}.
   */
  public static boolean isClass(Type type) {
    return type instanceof Class<?>;
  }

  /**
   * Attempt to cast the type to a {@link Class} if it's a {@link Class} instance. Otherwise, {@code
   * null} is returned.
   *
   * @param type The type to cast.
   * @return The {@link Class} instance if the given type is a {@link Class} instance. Otherwise,
   *     {@code null}.
   */
  public static @Nullable Class<?> asClass(Type type) {
    if (type instanceof Class<?>) {
      return (Class<?>) type;
    }
    return null;
  }

  /**
   * Check is type is a {@link ParameterizedType} instance.
   *
   * @param type The type to check.
   * @return {@code true}, if type is a {@link ParameterizedType}. Otherwise, {@code false}.
   */
  public static boolean isParameterizedType(Type type) {
    return type instanceof ParameterizedType;
  }

  /**
   * Attempt to cast the type to a {@link ParameterizedType} if it's a {@link ParameterizedType}
   * instance. Otherwise, {@code null} is returned.
   *
   * @param type The type to cast.
   * @return The {@link ParameterizedType} instance if the given type is a {@link ParameterizedType}
   *     instance. Otherwise, {@code null}.
   */
  public static @Nullable ParameterizedType asParameterizedType(Type type) {
    if (type instanceof ParameterizedType) {
      return (ParameterizedType) type;
    }
    return null;
  }

  /**
   * Check is type is a {@link GenericArrayType} instance.
   *
   * @param type The type to check.
   * @return {@code true}, if type is a {@link GenericArrayType}. Otherwise, {@code false}.
   */
  public static boolean isGenericArrayType(Type type) {
    return type instanceof GenericArrayType;
  }

  /**
   * Attempt to cast the type to a {@link GenericArrayType} if it's a {@link GenericArrayType}
   * instance. Otherwise, {@code null} is returned.
   *
   * @param type The type to cast.
   * @return The {@link GenericArrayType} instance if the given type is a {@link GenericArrayType}
   *     instance. Otherwise, {@code null}.
   */
  public static @Nullable GenericArrayType asGenericArrayType(Type type) {
    if (type instanceof GenericArrayType) {
      return (GenericArrayType) type;
    }
    return null;
  }

  /**
   * Check is type is a {@link TypeVariable} instance.
   *
   * @param type The type to check.
   * @return {@code true}, if type is a {@link TypeVariable}. Otherwise, {@code false}.
   */
  public static boolean isTypeVariable(Type type) {
    return type instanceof TypeVariable<?>;
  }

  /**
   * Attempt to cast the type to a {@link TypeVariable} if it's a {@link TypeVariable} instance.
   * Otherwise, {@code null} is returned.
   *
   * @param type The type to cast.
   * @return The {@link TypeVariable} instance if the given type is a {@link TypeVariable} instance.
   *     Otherwise, {@code null}.
   */
  public static @Nullable TypeVariable<?> asTypeVariable(Type type) {
    if (type instanceof TypeVariable<?>) {
      return (TypeVariable<?>) type;
    }
    return null;
  }

  /**
   * Check is type is a {@link WildcardType} instance.
   *
   * @param type The type to check.
   * @return {@code true}, if type is a {@link WildcardType}. Otherwise, {@code false}.
   */
  public static boolean isWildcardType(Type type) {
    return type instanceof WildcardType;
  }

  /**
   * Attempt to cast the type to a {@link WildcardType} if it's a {@link WildcardType} instance.
   * Otherwise, {@code null} is returned.
   *
   * @param type The type to cast.
   * @return The {@link WildcardType} instance if the given type is a {@link WildcardType} instance.
   *     Otherwise, {@code null}.
   */
  public static @Nullable WildcardType asWildcardType(Type type) {
    if (type instanceof WildcardType) {
      return (WildcardType) type;
    }
    return null;
  }

  /**
   * Get array type of the given type.
   *
   * @param type The type to derive the type from.
   * @return The array type of the given type.
   * @throws IllegalArgumentException if the type is unsupported.
   */
  public static Type getArrayType(Type type) {
    requireNonNull(type, "type");

    if (isParameterizedType(type) || isTypeVariable(type) || isGenericArrayType(type)) {
      return new GenericArrayType() {
        @Override
        public Type getGenericComponentType() {
          return type;
        }
      };
    }

    // Wildcards cannot be arrays, so we use its upper/lower bounds to
    // determine the array type.
    WildcardType wildcardType = asWildcardType(type);
    if (wildcardType != null) {
      // Only get first of the bounds because Java doesn't allow multiple
      // extends/super as of writing.
      if (wildcardType.getLowerBounds().length > 0) {
        // For example, if wildcard type is <? super List<String>>,
        // array type should be List<String>[].
        return getArrayType(wildcardType.getLowerBounds()[0]);
      }

      // For example, if wildcard type is <? extends List<String>>,
      // array type should be List<String>[].
      // If no upper bound is explicitly declared, the upper bound is Object.
      return getArrayType(wildcardType.getUpperBounds()[0]);
    }

    return getRawArrayType(type);
  }

  /**
   * Get raw array class of the given type.
   *
   * @param type The type to derive the raw class from.
   * @return The raw array class of the given type.
   * @throws IllegalArgumentException if the type is unsupported.
   */
  public static Class<?> getRawArrayType(Type type) {
    requireNonNull(type, "type");

    Class<?> rawType = getRawType(type);
    return ARRAY_TYPE_CACHE.get(rawType);
  }
}
