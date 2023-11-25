package io.github.aparx.bufig.handler;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.Config;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 14:18
 * @since 1.0-SNAPSHOT
 */
public class ConfigMap<T extends Config> implements ConfigHandler<T> {

  private final @NonNull Map<@NonNull String, @NonNull T> map;

  private final @NonNull Function<@NonNull String, @NonNull ? extends T> defaultFactory;

  public ConfigMap(@NonNull Function<@NonNull String, @NonNull ? extends T> defaultFactory) {
    Preconditions.checkNotNull(defaultFactory, "Factory must not be null");
    this.map = new ConcurrentHashMap<>(); // TODO move to a separate constructor (?)
    this.defaultFactory = defaultFactory;
  }

  @Override
  public @NonNull T get(@NonNull String configId) {
    Preconditions.checkNotNull(configId, "ID must not be null");
    return find(configId).orElseThrow();
  }

  @Override
  public @NonNull Optional<T> find(@NonNull String configId) {
    Preconditions.checkNotNull(configId, "ID must not be null");
    return Optional.ofNullable(map.get(configId));
  }

  @Override
  public boolean contains(@NonNull String configId) {
    Preconditions.checkNotNull(configId, "ID must not be null");
    return map.containsKey(configId);
  }

  @Override
  public boolean contains(@NonNull Config config) {
    Preconditions.checkNotNull(config, "Config must not be null");
    return config.equals(map.get(config.getId()));
  }

  @Override
  public boolean add(@NonNull T config) {
    Preconditions.checkNotNull(config, "Config must not be null");
    return map.putIfAbsent(config.getId(), config) == null;
  }

  @Override
  @SuppressWarnings("SuspiciousMethodCalls")
  public boolean remove(@NonNull Config config) {
    Preconditions.checkNotNull(config, "Config must not be null");
    return map.remove(config.getId(), config);
  }

  @Override
  public @Nullable T remove(@NonNull String configId) {
    Preconditions.checkNotNull(configId, "ID must not be null");
    return map.remove(configId);
  }

  @Override
  public @NonNull T getOrCreate(
      @NonNull String configId,
      @NonNull Function<@NonNull String, @NonNull ? extends T> factory) {
    Preconditions.checkNotNull(configId, "ID must not be null");
    Preconditions.checkNotNull(factory, "Factory must not be null");
    return Objects.requireNonNull(map.computeIfAbsent(configId, (id) -> {
      T config = factory.apply(id);
      Objects.requireNonNull(config, "Config must not be null");
      config.load();
      return config;
    }));
  }

  @Override
  public @NonNull T getOrCreate(@NonNull String configId) {
    return getOrCreate(configId, defaultFactory);
  }

  @Override
  public @NonNull Map<@NonNull String, @NonNull T> asMap() {
    return map;
  }

  @Override
  public @NonNull Iterator<T> iterator() {
    return map.values().iterator();
  }
}
