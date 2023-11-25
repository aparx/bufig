package io.github.aparx.bufig.handler;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.defaults.yaml.YamlConfig;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 14:11
 * @see ConfigMap
 * @see ConfigHandlers
 * @since 1.0-SNAPSHOT
 */
@CheckReturnValue
public interface ConfigHandler<T extends Config> extends Iterable<T> {

  @NonNull T get(@NonNull String configId);

  @NonNull Optional<T> find(@NonNull String configId);

  boolean contains(@NonNull String configId);

  boolean contains(@NonNull Config config);

  @CanIgnoreReturnValue
  boolean add(@NonNull T config);

  @CanIgnoreReturnValue
  boolean remove(@NonNull Config config);

  @CanIgnoreReturnValue
  @Nullable T remove(@NonNull String configId);

  @CanIgnoreReturnValue
  @NonNull Config getOrCreate(
      @NonNull String configId,
      @NonNull Function<@NonNull String, @NonNull ? extends T> factory);

  @CanIgnoreReturnValue
  @NonNull T getOrCreate(@NonNull String configId);

  /**
   * Returns a backed version of this handler, where configs are mapped to their identifier.
   * Changes made within the returning map will have an effect on this handler and vice-versa.
   *
   * @return this handler as a backed map
   */
  @NonNull Map<@NonNull String, @NonNull T> asMap();


  static ConfigHandler<? super YamlConfig> of(@NonNull Plugin plugin) {
    return ConfigHandlers.ofDefault(plugin);
  }

  static ConfigHandler<? super YamlConfig> of(
      @NonNull Plugin plugin,
      @NonNull BiFunction<@NonNull String, @NonNull File, @NonNull ? extends Config> factory) {
    return ConfigHandlers.ofDefault(plugin, factory);
  }

}
