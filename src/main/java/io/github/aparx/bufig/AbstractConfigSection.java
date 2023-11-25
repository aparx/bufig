package io.github.aparx.bufig;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-20 15:22
 * @since 1.0-SNAPSHOT
 */
@Getter
public abstract class AbstractConfigSection implements ConfigSection {

  private final @NonNull ConfigPath path;
  private final @NonNull Config root;
  private final @Nullable ConfigSection parent;

  @Getter(AccessLevel.NONE)
  private @Nullable ConfigurationSection section;

  public AbstractConfigSection() {
    Preconditions.checkState(this instanceof Config, "Root must be a config");
    this.parent = null;
    this.path = ConfigPath.of();
    this.root = (Config) this;
  }

  public AbstractConfigSection(
      @NonNull ConfigSection parent,
      @NonNull ConfigurationSection section) {
    final String segment = section.getName();
    Preconditions.checkNotNull(parent, "Parent must not be null");
    Preconditions.checkNotNull(section, "Section must not be null");
    Preconditions.checkArgument(ConfigPath.isValidSegment(segment), "Invalid segment");
    this.parent = parent;
    this.section = section;
    this.path = parent.getPath().add(segment);
    this.root = parent.getRoot();
  }

  private Object visitValue(Object value) {
    // TODO replace with visitors
    if (value instanceof ConfigSection)
      value = ((ConfigSection) value).getSection();
    // TODO this may be added in future versions
    // if (value instanceof Configurable)
    // ((ConfigurableHandle<Configurable>) ((Configurable) value).getHandle())
    //    .getValues((Configurable) value)
    //    .forEach(val -> setDocs(val.toPath(getPathSeparator()), val.getDocs()));
    return value;
  }

  public @NonNull ConfigurationSection getSection() {
    if (section == null && isRoot())
      return ((Config) this).getOutput();
    return Objects.requireNonNull(section);
  }

  @Override
  public boolean setDocsIfAbsent(@NonNull String path, String... docs) {
    if (hasDocs(path)) return false;
    setDocs(path, docs);
    return true;
  }

  @Override
  public boolean setDocsIfAbsent(@NonNull ConfigPath path, String... docs) {
    if (hasDocs(path)) return false;
    setDocs(path, docs);
    return true;
  }

  @Override
  public Object get(@NonNull ConfigPath path) {
    if (isRoot())
      return ((Config) this).getOutput().get(path.join(getPathSeparator()));
    return getRoot().get(getPath().add(path));
  }

  @Override
  public Object get(@NonNull String path) {
    if (isRoot()) return ((Config) this).getOutput().get(path);
    return getRoot().get(getPath().parseAdd(path, getPathSeparator()));
  }

  @Override
  public void set(@NonNull ConfigPath path, Object value, String... docs) {
    value = visitValue(value);
    if (!isRoot()) getRoot().set(getPath().add(path), value, docs);
    else {
      ((Config) this).set(path.join(getPathSeparator()), value);
      setDocs(path, docs);
    }
  }

  @Override
  public void set(@NonNull String path, Object value, String... docs) {
    value = visitValue(value);
    if (!isRoot()) getRoot().set(getPath().add(path), value, docs);
    else {
      ((Config) this).getOutput().set(path, value);
      setDocs(path, docs);
    }
  }

  @Override
  public boolean setIfAbsent(@NonNull ConfigPath path, Object value, String... docs) {
    if (!isRoot()) return getRoot().setIfAbsent(getPath().add(path), value, docs);
    if (contains(path)) return false;
    set(path, value, docs);
    return true;
  }

  @Override
  public boolean setIfAbsent(@NonNull String path, Object value, String... docs) {
    return setIfAbsent(ConfigPath.parse(path, getPathSeparator()), value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull ConfigPath path, Supplier<?> value, String... docs) {
    if (!isRoot()) return getRoot().setIfAbsent(getPath().add(path), value, docs);
    if (contains(path)) return false;
    set(path, value.get(), docs);
    return true;
  }

  @Override
  public boolean setIfAbsent(@NonNull String path, Supplier<?> value, String... docs) {
    return setIfAbsent(ConfigPath.parse(path, getPathSeparator()), value, docs);
  }

  @Override
  public boolean contains(@NonNull ConfigPath path) {
    if (isRoot())
      return ((Config) this).getOutput().contains(path.join(getPathSeparator()));
    return getRoot().contains(getPath().add(path));
  }

  @Override
  public boolean contains(@NonNull String path) {
    Config root = getRoot();
    if (this == root)
      return ((Config) this).getOutput().contains(path);
    return root.contains(getPath().parseAdd(path, getPathSeparator()));
  }

  @Override
  public boolean isSection(@NonNull ConfigPath path) {
    if (!isRoot())
      return getRoot().isSection(getPath().add(path));
    return ((Config) this).getOutput().isConfigurationSection(path.join(getPathSeparator()));
  }

  @Override
  public boolean isSection(@NonNull String path) {
    if (!isRoot())
      return getRoot().isSection(getPath().parseAdd(path, getPathSeparator()));
    return ((Config) this).getOutput().isConfigurationSection(path);
  }

}
