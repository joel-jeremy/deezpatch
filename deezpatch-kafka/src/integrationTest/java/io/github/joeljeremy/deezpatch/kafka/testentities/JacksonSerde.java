package io.github.joeljeremy.deezpatch.kafka.testentities;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.joeljeremy.deezpatch.core.Event;
import io.github.joeljeremy.deezpatch.core.TypeUtilities;
import java.io.IOException;
import java.util.Map;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public abstract class JacksonSerde<E extends Event>
    implements Serializer<E>, Deserializer<E>, Serde<E> {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Class<? extends Event> eventType;

  @Override
  public void configure(Map<String, ?> configs, boolean isKey) {}

  @SuppressWarnings("unchecked")
  protected JacksonSerde() {
    // This is type parameter E.
    this.eventType =
        (Class<? extends Event>)
            TypeUtilities.getRawType(
                TypeUtilities.getTypeParameters(getClass().getGenericSuperclass())[0]);
  }

  @Override
  @SuppressWarnings("unchecked")
  public E deserialize(String topic, byte[] data) {
    try {
      return (E) OBJECT_MAPPER.readValue(data, eventType);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to deserialize data.", e);
    }
  }

  @Override
  public byte[] serialize(String topic, E data) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(data);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to serialize data.", e);
    }
  }

  @Override
  public Serializer<E> serializer() {
    return this;
  }

  @Override
  public Deserializer<E> deserializer() {
    return this;
  }

  @Override
  public void close() {}
}
