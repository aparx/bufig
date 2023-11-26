package io.github.aparx.bufig;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Deterministic;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Interface representing an adapter for {@code ConfigurationSection} that is a node within
 * a tree of sections, where a path determines the current location relative to the root.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-20 14:42
 * @see ConfigurationSection
 * @see ConfigPath
 * @since 1.0-SNAPSHOT
 */
public interface ConfigSection {

  @NonNull ConfigurationSection getSection();

  /**
   * Returns the current location in segments relative to the root.
   *
   * @return this section's location (relative to the root)
   * @implSpec This method is supposed to be pure or just deterministic.
   */
  @NonNull ConfigPath getPath();

  @NonNull Config getRoot();

  @Nullable ConfigSection getParent();

  boolean hasDocs(@NonNull ConfigPath path);

  boolean hasDocs(@NonNull String path);

  String @Nullable [] getDocs(@NonNull ConfigPath path);

  String @Nullable [] getDocs(@NonNull String path);

  void setDocs(@NonNull ConfigPath path, String... docs);

  void setDocs(@NonNull String path, String... docs);

  @CanIgnoreReturnValue
  boolean setDocsIfAbsent(@NonNull ConfigPath path, String... docs);

  @CanIgnoreReturnValue
  boolean setDocsIfAbsent(@NonNull String path, String... docs);

  Object get(@NonNull ConfigPath path);

  Object get(@NonNull String path);

  void set(@NonNull ConfigPath path, Object value, String... docs);

  void set(@NonNull String path, Object value, String... docs);

  @CanIgnoreReturnValue
  boolean setIfAbsent(@NonNull ConfigPath path, Object value, String... docs);

  @CanIgnoreReturnValue
  boolean setIfAbsent(@NonNull String path, Object value, String... docs);

  @CanIgnoreReturnValue
  boolean setIfAbsent(@NonNull ConfigPath path, Supplier<?> value, String... docs);

  @CanIgnoreReturnValue
  boolean setIfAbsent(@NonNull String path, Supplier<?> value, String... docs);

  boolean contains(@NonNull ConfigPath path);

  boolean contains(@NonNull String path);

  ConfigSection getSection(@NonNull ConfigPath path);

  ConfigSection getSection(@NonNull String path);

  boolean isSection(@NonNull ConfigPath path);

  boolean isSection(@NonNull String path);

  default @NonNull FileConfigurationOptions options() {
    if (isRoot()) return ((Config) this).getOutput().options();
    return getRoot().options();
  }

  default char getPathSeparator() {
    return options().pathSeparator();
  }

  /**
   * Returns true if this section is the root.
   * <p>This implicitly means, that this instance is an instance of {@code Config}.
   *
   * @return true if this section is the root (the configuration)
   */
  @Deterministic
  default boolean isRoot() {
    return getRoot() == this;
  }

  // BEGIN RETRIEVERS (since 1.0)

  /** @since 1.0 */
  Set<String> getKeys(boolean deep);

  /** @since 1.0 */
  Map<String, Object> getValues(boolean deep);

  /** @since 1.0 */
  String getString(String path);

  /** @since 1.0 */
  String getString(String path, String def);

  /** @since 1.0 */
  boolean isString(String path);

  /** @since 1.0 */
  int getInt(@NonNull String path);

  /** @since 1.0 */
  int getInt(@NonNull String path, int def);

  /** @since 1.0 */
  boolean isInt(String path);

  /** @since 1.0 */
  double getDouble(@NonNull String path);

  /** @since 1.0 */
  double getDouble(@NonNull String path, double def);

  /** @since 1.0 */
  boolean isDouble(String path);

  /** @since 1.0 */
  long getLong(@NonNull String path);

  /** @since 1.0 */
  long getLong(@NonNull String path, long def);

  /** @since 1.0 */
  boolean isLong(String path);

  /** @since 1.0 */
  boolean getBoolean(String path);

  /** @since 1.0 */
  boolean getBoolean(String path, boolean def);

  /** @since 1.0 */
  boolean isBoolean(String path);

  /** @since 1.0 */
  List<?> getList(String path);

  /** @since 1.0 */
  List<?> getList(String path, List<?> def);

  /** @since 1.0 */
  boolean isList(String path);

  /** @since 1.0 */
  List<Map<?, ?>> getMapList(String path);

  /** @since 1.0 */
  Color getColor(String path);

  /** @since 1.0 */
  Color getColor(String path, Color def);

  /** @since 1.0 */
  boolean isColor(String path);

  /** @since 1.0 */
  ItemStack getItemStack(String path);

  /** @since 1.0 */
  ItemStack getItemStack(String path, ItemStack def);

  /** @since 1.0 */
  boolean isItemStack(String path);

  /** @since 1.0 */
  Vector getVector(String path);

  /** @since 1.0 */
  Vector getVector(String path, Vector def);

  /** @since 1.0 */
  boolean isVector(String path);

  // END RETRIEVERS

}
