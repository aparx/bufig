package io.github.aparx.bufig.configurable.populator;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.configurable.Configurable;
import io.github.aparx.bufig.configurable.ConfigurableHandle;
import io.github.aparx.bufig.configurable.field.ConfigField;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 18:04
 * @since 1.0-SNAPSHOT
 */
@Getter
public class ConfigFieldPopulator<T extends Configurable> implements ConfigurableValuePopulator<T> {

  private final @NonNull Class<? super T> baseClass;

  private final Set<Class<?>> classesIterated = new HashSet<>();

  public ConfigFieldPopulator(@NonNull Class<? super T> baseClass) {
    Preconditions.checkNotNull(baseClass, "Class must not be null");
    this.baseClass = baseClass;
  }

  @Override
  public void populate(@NonNull ConfigurableHandle<? extends T> handle, @NonNull T accessor) {
    Class<?> cls = accessor.getClass();
    Preconditions.checkArgument(baseClass.isAssignableFrom(cls),
        "Accessor {0} is not assignable to base class {1}",
        new Object[]{cls, baseClass});
    if (classesIterated.contains(cls)) return;
    for (; cls != Object.class; cls = cls.getSuperclass()) {
      Arrays.stream(cls.getDeclaredFields())
          .filter(ConfigField::isValidField)
          .map(ConfigField::new)
          .forEach(handle::addValue);
      classesIterated.add(cls);
    }
  }

}
