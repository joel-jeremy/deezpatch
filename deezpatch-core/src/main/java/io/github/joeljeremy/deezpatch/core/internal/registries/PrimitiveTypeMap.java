package io.github.joeljeremy.deezpatch.core.internal.registries;

class PrimitiveTypeMap extends ClassValue<Class<?>> {
  /**
   * Map wrapper types to its primitive type. If type is not a wrapper type, the same type is
   * returned.
   */
  @Override
  protected Class<?> computeValue(Class<?> type) {
    if (Void.class.equals(type)) {
      return void.class;
    } else if (Integer.class.equals(type)) {
      return int.class;
    } else if (Short.class.equals(type)) {
      return short.class;
    } else if (Long.class.equals(type)) {
      return long.class;
    } else if (Float.class.equals(type)) {
      return float.class;
    } else if (Double.class.equals(type)) {
      return double.class;
    } else if (Byte.class.equals(type)) {
      return byte.class;
    } else if (Character.class.equals(type)) {
      return char.class;
    } else if (Boolean.class.equals(type)) {
      return boolean.class;
    }
    return type;
  }
}
