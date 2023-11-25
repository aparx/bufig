package io.github.aparx.bufig.configurable;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.ConfigPath;
import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.dataflow.qual.Deterministic;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:20
 * @since 1.0-SNAPSHOT
 */
public interface ConfigurableValue<A extends Configurable, V> {

  @Deterministic
  @NonNull Class<? extends V> getType();

  @Deterministic
  @NonNull String getName();

  String @NonNull [] getDocs();

  V get(A accessor);

  void set(A accessor, V value);

  default void unsafeSet(A accessor, Object value) {
    set(accessor, getType().cast(value));
  }

  default ConfigPath toPath(char pathSeparator) {
    return ConfigPath.parse(getName(), pathSeparator);
  }

  static <A extends Configurable, T> ConfigurableValue<A, T> of(
      @NonNull Class<T> type, @NonNull String name, T initialValue, String... docs) {
    Preconditions.checkNotNull(type, "Type must not be null");
    Preconditions.checkNotNull(name, "Name must not be null");
    String[] docsCopy = (String[]) ArrayUtils.clone(ArrayUtils.nullToEmpty(docs));
    AtomicReference<T> reference = new AtomicReference<>(initialValue);
    return new ConfigurableValue<>() {

      @Override
      public @NonNull Class<T> getType() {
        return type;
      }

      @Override
      public @NonNull String getName() {
        return name;
      }

      @Override
      public String @NonNull [] getDocs() {
        return (String[]) ArrayUtils.clone(docsCopy);
      }

      @Override
      public T get(A accessor) {
        return reference.get();
      }

      @Override
      public void set(A accessor, T value) {
        reference.set(value);
      }

      @Override
      public String toString() {
        return getClass().getName() + '{'
            + "type=" + type
            + ", name=" + name
            + ", docs=" + Arrays.toString(getDocs())
            + ", value=" + reference.get()
            + '}';
      }
    };
  }

}
