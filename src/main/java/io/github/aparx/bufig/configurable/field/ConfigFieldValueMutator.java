package io.github.aparx.bufig.configurable.field;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.configurable.Configurable;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.BiFunction;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-26 02:34
 * @since 1.0
 */
public interface ConfigFieldValueMutator {

  Object read(Configurable accessor, ConfigField<?> field, Object value);

  Object write(Configurable accessor, ConfigField<?> field, Object value);

  static <L, R> ConfigFieldValueMutator newMapper(
      @NonNull Class<L> targetType,
      @NonNull Class<R> originType,
      @NonNull BiFunction<ConfigField<?>, L, R> reader,
      @NonNull BiFunction<ConfigField<?>, R, L> writer) {
    Preconditions.checkNotNull(targetType, "Value type must not be null");
    Preconditions.checkNotNull(targetType, "Field type must not be null");
    Preconditions.checkNotNull(reader, "Reader must not be null");
    Preconditions.checkNotNull(writer, "Writer must not be null");
    return new ConfigFieldValueMutator() {
      @Override
      public Object read(Configurable accessor, ConfigField<?> field, Object value) {
        if (value != null
            && targetType.isAssignableFrom(value.getClass())
            && originType.isAssignableFrom(field.getType()))
          return reader.apply(field, targetType.cast(value));
        return value;
      }

      @Override
      public Object write(Configurable accessor, ConfigField<?> field, Object value) {
        if (value != null
            && originType.isAssignableFrom(value.getClass())
            && targetType.isAssignableFrom(field.getType()))
          return writer.apply(field, originType.cast(value));
        return value;
      }
    };
  }

}

