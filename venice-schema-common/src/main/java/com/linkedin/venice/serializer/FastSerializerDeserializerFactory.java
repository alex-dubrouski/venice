package com.linkedin.venice.serializer;

import com.linkedin.avro.fastserde.FastSerdeCache;
import com.linkedin.venice.exceptions.VeniceException;
import com.linkedin.venice.read.protocol.response.MultiGetResponseRecordV1;
import com.linkedin.venice.utils.concurrent.VeniceConcurrentHashMap;
import java.util.Arrays;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;

public class FastSerializerDeserializerFactory extends SerializerDeserializerFactory {
  /**
   * This is used to indicate whether the sanity check has done or not for fast class generation.
   */
  private static boolean fastAvroSanityCheckDone = false;

  private static FastSerdeCache cache = FastSerdeCache.getDefaultInstance();

  private static Map<SchemaPairAndClassContainer, AvroGenericDeserializer<Object>>
      avroFastGenericDeserializerMap = new VeniceConcurrentHashMap<>();
  private static Map<SchemaPairAndClassContainer, AvroSpecificDeserializer<? extends SpecificRecord>>
      avroFastSpecificDeserializerMap = new VeniceConcurrentHashMap<>();

  private static Map<Schema, AvroSerializer<Object>>
      avroFastGenericSerializerMap = new VeniceConcurrentHashMap<>();

  /**
   * Verify whether fast-avro could generate a fast specific deserializer, but there is no guarantee that
   * the success of all other fast specific deserializer generation in the future.
   *
   * The verification of fast-avro will only happen once, and if we allow it per store client, some of the verification could fail
   * since they will try to write to the same class file.
   */
  public synchronized static void verifyWhetherFastSpecificDeserializerWorks(Class<? extends SpecificRecord> specificClass) {
    if (fastAvroSanityCheckDone) {
      return;
    }
    for (Class<? extends SpecificRecord> c : Arrays.asList(specificClass, MultiGetResponseRecordV1.class)){
      Schema schema = SpecificData.get().getSchema(c);
      try {
        cache.buildFastSpecificDeserializer(schema, schema);
      } catch (Exception e) {
        throw new VeniceException("Failed to generate fast specific de-serializer for class: " + c, e);
      }
    }
    fastAvroSanityCheckDone = true;
  }

  // Verify whether fast-avro could generate a fast generic deserializer, but there is no guarantee that
  // the success of all other fast generic deserializer generation in the future.
  public static void verifyWhetherFastGenericDeserializerWorks() {
    // Leverage the following schema as the input for testing
    Schema schema = MultiGetResponseRecordV1.SCHEMA$;
    try {
      cache.buildFastGenericDeserializer(schema, schema);
    } catch (Exception e) {
      throw new VeniceException("Failed to generate fast generic de-serializer for Avro schema: " + schema, e);
    }
  }

  public static <V> RecordDeserializer<V> getFastAvroGenericDeserializer(Schema writer, Schema reader) {
    SchemaPairAndClassContainer container = new SchemaPairAndClassContainer(writer, reader, Object.class);
    return (AvroGenericDeserializer<V>) avroFastGenericDeserializerMap.computeIfAbsent(
        container, key -> new FastAvroGenericDeserializer(key.writer, key.reader, cache));
  }

  public static <V extends SpecificRecord> RecordDeserializer<V> getFastAvroSpecificDeserializer(Schema writer, Class<V> c) {
    return getFastAvroSpecificDeserializer(writer, c, null);
  }

  public static <V extends SpecificRecord> RecordDeserializer<V> getFastAvroSpecificDeserializer(Schema writer, Class<V> c,
      AvroGenericDeserializer.IterableImpl multiGetEnvelopeIterableImpl) {
    return getAvroSpecificDeserializerInternal(writer, c,
        container -> avroFastSpecificDeserializerMap.computeIfAbsent(container,
            k -> new FastAvroSpecificDeserializer<V>(container.writer, container.c, cache, multiGetEnvelopeIterableImpl)));
  }

  public static <K> RecordSerializer<K> getFastAvroGenericSerializer(Schema schema) {
    return (RecordSerializer<K>) avroFastGenericSerializerMap.computeIfAbsent(schema, (s) -> new FastAvroSerializer<>(s, cache));
  }

  public static <K> RecordSerializer<K> getFastAvroGenericSerializer(Schema schema, boolean buffered) {
    return (RecordSerializer<K>) avroFastGenericSerializerMap.computeIfAbsent(schema, (s) -> new FastAvroSerializer<>(s, cache, buffered));
  }

}