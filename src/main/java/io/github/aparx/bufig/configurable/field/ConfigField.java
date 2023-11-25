package io.github.aparx.bufig.configurable.field;

import com.google.common.base.Defaults;
import com.google.common.base.Preconditions;
import io.github.aparx.bufig.ConfigPath;
import io.github.aparx.bufig.configurable.Configurable;
import io.github.aparx.bufig.configurable.ConfigurableValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * A config field which contains a referenced member field, that is declared to be
 * serialized into a config.
 * <p>The initial value of a field may represent its default value.
 * <p>A valid config field must follow these requirements:
 * <ol>
 *   <li>The field annotates {@link ConfigMapping}</li>
 *   <li>The field is neither static, transient nor final</li>
 * </ol>
 * <p>The field can optionally be documented through the {@link Document} annotation.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:39
 * @since 1.0-SNAPSHOT
 */
@Getter
@Accessors(makeFinal = true)
public class ConfigField<A extends Configurable> implements ConfigurableValue<A, Object> {

  private final @NonNull Field field;

  private final @NonNull String name;

  @Getter(AccessLevel.NONE)
  private final String @NonNull [] docs;

  public ConfigField(@NonNull Field field) {
    Preconditions.checkNotNull(field, "Field must not be null");
    Preconditions.checkState(isValidField(field), "Field not a valid config field");
    this.field = field;
    this.name = Optional.ofNullable(field.getAnnotation(ConfigMapping.class))
        .map(ConfigMapping::value)
        .filter(ConfigPath::isValidSegment)
        .orElse(field.getName());
    this.docs = Optional.ofNullable(field.getAnnotation(Document.class))
        .map(Document::value)
        .orElse(ArrayUtils.EMPTY_STRING_ARRAY);
  }

  public static boolean isValidField(@NonNull Field field) {
    return field.isAnnotationPresent(ConfigMapping.class)
        && !Modifier.isStatic(field.getModifiers())
        && !Modifier.isTransient(field.getModifiers())
        && !Modifier.isFinal(field.getModifiers());
  }

  @Override
  public @NonNull Class<?> getType() {
    return field.getType();
  }

  @Override
  public String @NonNull [] getDocs() {
    if (ArrayUtils.isNotEmpty(docs))
      return (String[]) ArrayUtils.clone(docs);
    return ArrayUtils.EMPTY_STRING_ARRAY;
  }

  @Override
  @SneakyThrows
  public Object get(@NonNull A accessor) {
    Object value = read(accessor);
    if (value instanceof ConfigurationSerializable)
      return ((ConfigurationSerializable) value).serialize();
    return value;
  }

  @Override
  @SneakyThrows
  public void set(@NonNull A accessor, Object value) {
    unsafeSet(accessor, value);
  }

  @Override
  @SneakyThrows
  @SuppressWarnings("unchecked")
  public void unsafeSet(A accessor, Object value) {
    if (value instanceof ConfigurationSection && Map.class.isAssignableFrom(getType()))
      unsafeSet(accessor, ((ConfigurationSection) value).getValues(false));
    else if (!ConfigurationSerializable.class.isAssignableFrom(getType()))
      writeSafely(accessor, value);
    else if (value instanceof Map)
      writeSafely(accessor, ConfigurationSerialization.deserializeObject(
          (Map<String, ?>) value, (Class<? extends ConfigurationSerializable>) getType()));
    else if (value instanceof ConfigurationSection)
      unsafeSet(accessor, ((ConfigurationSection) value).getValues(false));
    else
      writeSafely(accessor, value);
  }

  private Object read(@NonNull A accessor) throws IllegalAccessException {
    Preconditions.checkState(field.trySetAccessible());
    return field.get(accessor);
  }

  private void write(@NonNull A accessor, Object value) throws IllegalAccessException {
    Preconditions.checkState(field.trySetAccessible());
    field.set(accessor, value);
  }

  private void writeSafely(@NonNull A accessor, Object value) throws IllegalAccessException {
    write(accessor, value != null ? value : Defaults.defaultValue(getType()));
  }

  @Override
  public String toString() {
    return "ConfigField{" +
        "field=" + field +
        ", name='" + name + '\'' +
        ", docs=" + Arrays.toString(docs) +
        '}';
  }
}
