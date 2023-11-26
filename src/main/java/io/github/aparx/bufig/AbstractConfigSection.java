package io.github.aparx.bufig;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.configurable.Configurable;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

  @Override
  public Set<String> getKeys(boolean deep) {
    return getSection().getKeys(deep);
  }

  @Override
  public Map<String, Object> getValues(boolean deep) {
    return getSection().getValues(deep);
  }

  @Override
  public String getString(String path) {
    if (isRoot()) return getSection().getString(path);
    return getRoot().getString(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public String getString(String path, String def) {
    if (isRoot()) return getSection().getString(path, def);
    return getRoot().getString(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isString(String path) {
    if (isRoot()) return getSection().isString(path);
    return getRoot().isString(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public int getInt(@NonNull String path) {
    if (isRoot()) return getSection().getInt(path);
    return getRoot().getInt(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public int getInt(@NonNull String path, int def) {
    System.out.println("GET INT " + path + "/" + get(path));
    if (isRoot()) return getSection().getInt(path, def);
    return getRoot().getInt(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isInt(String path) {
    if (isRoot()) return getSection().isInt(path);
    return getRoot().isInt(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public double getDouble(@NonNull String path) {
    if (isRoot()) return getSection().getDouble(path);
    return getRoot().getDouble(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public double getDouble(@NonNull String path, double def) {
    if (isRoot()) return getSection().getDouble(path, def);
    return getRoot().getDouble(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isDouble(String path) {
    if (isRoot()) return getSection().isDouble(path);
    return getRoot().isDouble(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public long getLong(@NonNull String path) {
    if (isRoot()) return getSection().getLong(path);
    return getRoot().getLong(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public long getLong(@NonNull String path, long def) {
    if (isRoot()) return getSection().getLong(path, def);
    return getRoot().getLong(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isLong(String path) {
    if (isRoot()) return getSection().isLong(path);
    return getRoot().isLong(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public boolean getBoolean(String path) {
    if (isRoot()) return getSection().getBoolean(path);
    return getRoot().getBoolean(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public boolean getBoolean(String path, boolean def) {
    if (isRoot()) return getSection().getBoolean(path, def);
    return getRoot().getBoolean(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isBoolean(String path) {
    if (isRoot()) return getSection().isBoolean(path);
    return getRoot().isBoolean(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public List<?> getList(String path) {
    if (isRoot()) return getSection().getList(path);
    return getRoot().getList(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public List<?> getList(String path, List<?> def) {
    if (isRoot()) return getSection().getList(path, def);
    return getRoot().getList(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isList(String path) {
    if (isRoot()) return getSection().isList(path);
    return getRoot().isList(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public List<Map<?, ?>> getMapList(String path) {
    if (isRoot()) return getSection().getMapList(path);
    return getRoot().getMapList(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public Color getColor(String path) {
    if (isRoot()) return getSection().getColor(path);
    return getRoot().getColor(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public Color getColor(String path, Color def) {
    if (isRoot()) return getSection().getColor(path, def);
    return getRoot().getColor(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isColor(String path) {
    if (isRoot()) return getSection().isColor(path);
    return getRoot().isColor(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public ItemStack getItemStack(String path) {
    if (isRoot()) return getSection().getItemStack(path);
    return getRoot().getItemStack(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public ItemStack getItemStack(String path, ItemStack def) {
    if (isRoot()) return getSection().getItemStack(path, def);
    return getRoot().getItemStack(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isItemStack(String path) {
    if (isRoot()) return getSection().isItemStack(path);
    return getRoot().isItemStack(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public Vector getVector(String path) {
    if (isRoot()) return getSection().getVector(path);
    return getRoot().getVector(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }

  @Override
  public Vector getVector(String path, Vector def) {
    if (isRoot()) return getSection().getVector(path, def);
    return getRoot().getVector(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()), def);
  }

  @Override
  public boolean isVector(String path) {
    if (isRoot()) return getSection().isVector(path);
    return getRoot().isVector(ConfigPaths.parseConcatJoin(
        getPath(), path, getPathSeparator()));
  }
}
