package io.github.aparx.bufig.configurable;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.aparx.bufig.configurable.populator.ConfigFieldPopulator;
import io.github.aparx.bufig.configurable.populator.ConfigurableValuePopulator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;

/**
 * @param <T> the base of this handle
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:21
 * @since 1.0-SNAPSHOT
 */
public class ConfigurableHandle<T extends Configurable> {

  private final Map<@NonNull String, @NonNull ConfigurableValue<? super T, ?>> map =
      Collections.synchronizedMap(new LinkedHashMap<>());

  private final @NonNull ConfigurableValuePopulator<? super T> valuePopulator;

  public ConfigurableHandle(@NonNull ConfigurableValuePopulator<T> valuePopulator) {
    Preconditions.checkNotNull(valuePopulator, "Populator must not be null");
    this.valuePopulator = valuePopulator;
  }

  public ConfigurableHandle(@NonNull Class<T> baseClass) {
    this(new ConfigFieldPopulator<>(baseClass));
  }

  public Collection<@NonNull ConfigurableValue<? super T, ?>> getValues(@NonNull T accessor) {
    valuePopulator.populate(this, accessor);
    return map.values();
  }

  public ConfigurableValue<? super T, ?> getValue(@NonNull String name) {
    return findValue(name).orElseThrow();
  }

  public ConfigurableValue<? super T, ?> getValue(
      @NonNull T accessor, @NonNull String name) {
    valuePopulator.populate(this, accessor);
    return getValue(name);
  }

  public Optional<ConfigurableValue<? super T, ?>> findValue(@NonNull String name) {
    return Optional.ofNullable(map.get(name));
  }

  public Optional<ConfigurableValue<? super T, ?>> findValue(
      @NonNull T accessor, @NonNull String name) {
    valuePopulator.populate(this, accessor);
    return findValue(name);
  }

  public void registerValue(@NonNull ConfigurableValue<? super T, ?> value) {
    if (!this.addValue(value))
      throw new IllegalStateException(String.format("Cannot add value %s", value));
  }

  public void registerValue(
      @NonNull T accessor, @NonNull ConfigurableValue<? super T, ?> value) {
    valuePopulator.populate(this, accessor);
    registerValue(value);
  }

  @CanIgnoreReturnValue
  public boolean addValue(@NonNull ConfigurableValue<? super T, ?> value) {
    Preconditions.checkNotNull(value, "Value most not be null");
    return map.putIfAbsent(value.getName(), value) == null;
  }

  @CanIgnoreReturnValue
  public boolean addValue(
      @NonNull T accessor, @NonNull ConfigurableValue<? super T, ?> value) {
    valuePopulator.populate(this, accessor);
    return addValue(value);
  }

  @CanIgnoreReturnValue
  public boolean removeValue(@NonNull ConfigurableValue<? super T, ?> value) {
    Preconditions.checkNotNull(value, "Value most not be null");
    return map.remove(value.getName(), value);
  }

  @CanIgnoreReturnValue
  public boolean removeValue(
      @NonNull T accessor, @NonNull ConfigurableValue<? super T, ?> value) {
    valuePopulator.populate(this, accessor);
    return removeValue(value);
  }

  @CanIgnoreReturnValue
  public @Nullable ConfigurableValue<? super T, ?> removeValue(@NonNull String name) {
    return map.remove(name);
  }

  @CanIgnoreReturnValue
  public @Nullable ConfigurableValue<? super T, ?> removeValue(
      @NonNull T accessor, @NonNull String name) {
    valuePopulator.populate(this, accessor);
    return map.remove(name);
  }

  public boolean contains(@NonNull ConfigurableValue<? super T, ?> value) {
    Preconditions.checkNotNull(value, "Value most not be null");
    return value.equals(map.get(value.getName()));
  }

  public boolean contains(
      @NonNull T accessor, @NonNull ConfigurableValue<? super T, ?> value) {
    valuePopulator.populate(this, accessor);
    return contains(value);
  }

  public boolean contains(@NonNull String name) {
    return map.containsKey(name);
  }

  public boolean contains(@NonNull T accessor, @NonNull String name) {
    valuePopulator.populate(this, accessor);
    return contains(name);
  }

}
