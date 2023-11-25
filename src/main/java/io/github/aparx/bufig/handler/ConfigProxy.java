package io.github.aparx.bufig.handler;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.ConfigPath;
import io.github.aparx.bufig.ConfigSection;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.util.Objects;
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
    return getConfig().hasDocs(path);
  }

  @Override
  public boolean hasDocs(@NonNull String path) {
    return getConfig().hasDocs(path);
  }

  @Override
  public String @Nullable [] getDocs(@NonNull ConfigPath path) {
    return getConfig().getDocs(path);
  }

  @Override
  public String @Nullable [] getDocs(@NonNull String path) {
    return getConfig().getDocs(path);
  }

  @Override
  public void setDocs(@NonNull ConfigPath path, String... docs) {
    getConfig().setDocs(path, docs);
  }

  @Override
  public void setDocs(@NonNull String path, String... docs) {
    getConfig().setDocs(path, docs);
  }

  @Override
  public boolean setDocsIfAbsent(@NonNull ConfigPath path, String... docs) {
    return getConfig().setDocsIfAbsent(path, docs);
  }

  @Override
  public boolean setDocsIfAbsent(@NonNull String path, String... docs) {
    return getConfig().setDocsIfAbsent(path, docs);
  }

  @Override
  public Object get(@NonNull ConfigPath path) {
    return getConfig().get(path);
  }

  @Override
  public Object get(@NonNull String path) {
    return getConfig().get(path);
  }

  @Override
  public void set(@NonNull ConfigPath path, Object value, String... docs) {
    getConfig().set(path, value, docs);
  }

  @Override
  public void set(@NonNull String path, Object value, String... docs) {
    getConfig().set(path, value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull ConfigPath path, Object value, String... docs) {
    return getConfig().setIfAbsent(path, value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull String path, Object value, String... docs) {
    return getConfig().setIfAbsent(path, value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull ConfigPath path, Supplier<?> value, String... docs) {
    return getConfig().setIfAbsent(path, value, docs);
  }

  @Override
  public boolean setIfAbsent(@NonNull String path, Supplier<?> value, String... docs) {
    return getConfig().setIfAbsent(path, value, docs);
  }

  @Override
  public boolean contains(@NonNull ConfigPath path) {
    return getConfig().contains(path);
  }

  @Override
  public boolean contains(@NonNull String path) {
    return getConfig().contains(path);
  }

  @Override
  public ConfigSection getSection(@NonNull ConfigPath path) {
    return getConfig().getSection(path);
  }

  @Override
  public ConfigSection getSection(@NonNull String path) {
    return getConfig().getSection(path);
  }

  @Override
  public boolean isSection(@NonNull ConfigPath path) {
    return getConfig().isSection(path);
  }

  @Override
  public boolean isSection(@NonNull String path) {
    return getConfig().isSection(path);
  }
}
