package io.github.aparx.bufig.handler;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.ConfigPath;
import io.github.aparx.bufig.ConfigPaths;
import io.github.aparx.bufig.ConfigSection;
import io.github.aparx.bufig.configurable.Configurable;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Deterministic;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 18:23
 * @since 1.0-SNAPSHOT
 */
public class ConfigProxy implements Config {

  private final @NonNull Function<@NonNull ConfigProxy, @NonNull Config> configSupplier;

  public ConfigProxy(@NonNull Function<@NonNull ConfigProxy, @NonNull Config> configSupplier) {
    Preconditions.checkNotNull(configSupplier, "Supplier must not be null");
    this.configSupplier = configSupplier;
  }

  public ConfigProxy(@NonNull String configId, @NonNull ConfigHandler<?> handler) {
    this((proxy) -> handler.getOrCreate(configId));
  }

  public final @NonNull Config getConfig() {
    Config config = Objects.requireNonNull(configSupplier.apply(this));
    Preconditions.checkState(this != config, "Config supplier returned calling proxy");
    return config;
  }

  /**
   * Returns the path offset applied as a prefix to operations done through this proxy.
   * <p>If no offset is present, the returning path is empty.
   * <p>The returning path is supposed to be deterministic and not null.
   * <p>The returning path is the <strong>leading</strong> path prefix (offset), and not the
   * tailing. Thus, this offset is used at the beginning onto which all other paths or
   * segments are added to form the final path to be used on operations within a
   * configuration.
   *
   * @return the target path offset of this configurable
   * @apiNote The path offset is especially useful for inheritance, for whenever a base
   * object (for example being a {@code ConfigObject} or {@code ConfigProxy}) is being
   * extended to represent different areas (so-called sections) within the underlying
   * configuration (the "base").
   * @since 1.0
   */
  @Deterministic
  public @NonNull ConfigPath getOffsetPath() {
    return ConfigPath.of();
  }

  @Override
  public @NonNull String getId() {
    return getConfig().getId();
  }

  @Override
  public @NonNull File getFile() {
    return getConfig().getFile();
  }

  @Override
  public @NonNull FileConfiguration getOutput() {
    return getConfig().getOutput();
  }

  @Override
  public void save() {
    getConfig().save();
  }

  @Override
  public String saveToString() {
    return getConfig().saveToString();
  }

  @Override
  public void load() {
    getConfig().load();
  }

  @Override
  public void loadFromString(String contents) {
    getConfig().loadFromString(contents);
  }

  @Override
  public @NonNull ConfigurationSection getSection() {
    return getConfig().getSection();
  }

  @Override
  public @NonNull ConfigPath getPath() {
    return getConfig().getPath();
  }

  @Override
  public @NonNull Config getRoot() {
    return getConfig().getRoot();
  }

  @Override
  public @Nullable ConfigSection getParent() {
    return getConfig().getParent();
  }

  @Override
  public boolean hasDocs(@NonNull ConfigPath path) {
    return getConfig().hasDocs(createOffsetPath(path));
  }

  @Override
  public boolean hasDocs(@NonNull String path) {
    return getConfig().hasDocs(createOffsetPath(path));
  }

  @Override
  public String @Nullable [] getDocs(@NonNull ConfigPath path) {
    return getConfig().getDocs(createOffsetPath(path));
  }

  @Override
  public String @Nullable [] getDocs(@NonNull String path) {
    return getConfig().getDocs(createOffsetPath(path));
  }

  @Override
  public void setDocs(@NonNull ConfigPath path, String... docs) {
    getConfig().setDocs(createOffsetPath(path), docs);
  }

  @Override
  public void setDocs(@NonNull String path, String... docs) {
    getConfig().setDocs(createOffsetPath(path), docs);
  }

  @Override
  public boolean setDocsIfAbsent(@NonNull ConfigPath path, String... docs) {
    return getConfig().setDocsIfAbsent(createOffsetPath(path), docs);
  }

  @Override
  public boolean setDocsIfAbsent(@NonNull String path, String... docs) {
    return getConfig().setDocsIfAbsent(createOffsetPath(path), docs);
  }

  @Override
  public Object get(@NonNull ConfigPath path) {
    return getConfig().get(createOffsetPath(path));
  }

  @Override
  public Object get(@NonNull String path) {
    return getConfig().get(createOffsetPath(path));
  }

  @Override
  public void set(@NonNull ConfigPath path, Object value, String... docs) {
    getConfig().set(createOffsetPath(path), value, docs);
  }

  @Override
  public void set(@NonNull String path, Object value, String... docs) {
    getConfig().set(createOffsetPath(path), value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull ConfigPath path, Object value, String... docs) {
    return getConfig().setIfAbsent(createOffsetPath(path), value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull String path, Object value, String... docs) {
    return getConfig().setIfAbsent(createOffsetPath(path), value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull ConfigPath path, Supplier<?> value, String... docs) {
    return getConfig().setIfAbsent(createOffsetPath(path), value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull String path, Supplier<?> value, String... docs) {
    return getConfig().setIfAbsent(createOffsetPath(path), value, docs);
  }

  @Override
  public boolean contains(@NonNull ConfigPath path) {
    return getConfig().contains(createOffsetPath(path));
  }

  @Override
  public boolean contains(@NonNull String path) {
    return getConfig().contains(createOffsetPath(path));
  }

  @Override
  public ConfigSection getSection(@NonNull ConfigPath path) {
    return getConfig().getSection(createOffsetPath(path));
  }

  @Override
  public ConfigSection getSection(@NonNull String path) {
    return getConfig().getSection(createOffsetPath(path));
  }

  @Override
  public boolean isSection(@NonNull ConfigPath path) {
    return getConfig().isSection(createOffsetPath(path));
  }

  @Override
  public boolean isSection(@NonNull String path) {
    return getConfig().isSection(createOffsetPath(path));
  }

  @Override
  public Set<String> getKeys(boolean deep) {
    return getConfig().getKeys(deep);
  }

  @Override
  public Map<String, Object> getValues(boolean deep) {
    return getConfig().getValues(deep);
  }

  @Override
  public String getString(String path) {
    return getConfig().getString(createOffsetPath(path));
  }

  @Override
  public String getString(String path, String def) {
    return getConfig().getString(createOffsetPath(path), def);
  }

  @Override
  public boolean isString(String path) {
    return getConfig().isString(createOffsetPath(path));
  }

  @Override
  public int getInt(@NonNull String path) {
    return getConfig().getInt(createOffsetPath(path));
  }

  @Override
  public int getInt(@NonNull String path, int def) {
    return getConfig().getInt(createOffsetPath(path), def);
  }

  @Override
  public boolean isInt(String path) {
    return getConfig().isInt(createOffsetPath(path));
  }

  @Override
  public double getDouble(@NonNull String path) {
    return getConfig().getDouble(createOffsetPath(path));
  }

  @Override
  public double getDouble(@NonNull String path, double def) {
    return getConfig().getDouble(createOffsetPath(path), def);
  }

  @Override
  public boolean isDouble(String path) {
    return getConfig().isDouble(createOffsetPath(path));
  }

  @Override
  public long getLong(@NonNull String path) {
    return getConfig().getLong(createOffsetPath(path));
  }

  @Override
  public long getLong(@NonNull String path, long def) {
    return getConfig().getLong(createOffsetPath(path), def);
  }

  @Override
  public boolean isLong(String path) {
    return getConfig().isLong(createOffsetPath(path));
  }

  @Override
  public boolean getBoolean(String path) {
    return getConfig().getBoolean(createOffsetPath(path));
  }

  @Override
  public boolean getBoolean(String path, boolean def) {
    return getConfig().getBoolean(createOffsetPath(path), def);
  }

  @Override
  public boolean isBoolean(String path) {
    return getConfig().isBoolean(createOffsetPath(path));
  }

  @Override
  public List<?> getList(String path) {
    return getConfig().getList(createOffsetPath(path));
  }

  @Override
  public List<?> getList(String path, List<?> def) {
    return getConfig().getList(createOffsetPath(path), def);
  }

  @Override
  public boolean isList(String path) {
    return getConfig().isList(createOffsetPath(path));
  }

  @Override
  public List<Map<?, ?>> getMapList(String path) {
    return getConfig().getMapList(createOffsetPath(path));
  }

  @Override
  public Color getColor(String path) {
    return getConfig().getColor(createOffsetPath(path));
  }

  @Override
  public Color getColor(String path, Color def) {
    return getConfig().getColor(createOffsetPath(path), def);
  }

  @Override
  public boolean isColor(String path) {
    return getConfig().isColor(createOffsetPath(path));
  }

  @Override
  public ItemStack getItemStack(String path) {
    return getConfig().getItemStack(createOffsetPath(path));
  }

  @Override
  public ItemStack getItemStack(String path, ItemStack def) {
    return getConfig().getItemStack(createOffsetPath(path), def);
  }

  @Override
  public boolean isItemStack(String path) {
    return getConfig().isItemStack(createOffsetPath(path));
  }

  @Override
  public Vector getVector(String path) {
    return getConfig().getVector(createOffsetPath(path));
  }

  @Override
  public Vector getVector(String path, Vector def) {
    return getConfig().getVector(createOffsetPath(path), def);
  }

  @Override
  public boolean isVector(String path) {
    return getConfig().isVector(createOffsetPath(path));
  }

  public ConfigPath createOffsetPath(@NonNull ConfigPath input) {
    return ConfigPaths.concat(getOffsetPath(), input);
  }

  public String createOffsetPath(@NonNull String path) {
    return ConfigPaths.parseConcatJoin(getOffsetPath(), path, getPathSeparator());
  }

}
