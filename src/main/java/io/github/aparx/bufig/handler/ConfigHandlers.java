package io.github.aparx.bufig.handler;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.defaults.yaml.YamlConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 14:38
 * @since 1.0-SNAPSHOT
 */
@UtilityClass
public final class ConfigHandlers {

  public static <T extends Config> ConfigHandler<T> of(
      @NonNull Function<@NonNull String, @NonNull ? extends T> configFactory) {
    return new ConfigMap<>(configFactory);
  }

  public static <T extends YamlConfig> ConfigHandler<T> ofIdAsFileName(
      @NonNull Plugin plugin,
      @NonNull BiFunction<@NonNull String, @NonNull File, @NonNull ? extends T> configFactory) {
    return ofMultistep((x) -> new File(plugin.getDataFolder(), x), configFactory);
  }

  public static <T extends Config> ConfigHandler<T> ofIdWithExtension(
      @NonNull Plugin plugin,
      @NonNull String extension,
      @NonNull BiFunction<@NonNull String, @NonNull File, @NonNull ? extends T> configFactory) {
    return ofMultistep((x) -> new File(plugin.getDataFolder(), x + extension), configFactory);
  }

  public static <T extends Config> ConfigHandler<T> ofMultistep(
      @NonNull Function<@NonNull String, @NonNull File> fileFactory,
      @NonNull BiFunction<@NonNull String, @NonNull File, @NonNull ? extends T> configFactory) {
    return new ConfigMap<>((x) -> {
      File target = fileFactory.apply(x);
      Preconditions.checkNotNull(target, "File must not be null");
      return configFactory.apply(x, target);
    });
  }

  public static ConfigHandler<? super YamlConfig> ofDefault(@NonNull Plugin plugin) {
    return ConfigHandlers.ofIdWithExtension(plugin, ".yml", YamlConfig::new);
  }

  public static ConfigHandler<? super YamlConfig> ofDefault(
      @NonNull Plugin plugin,
      @NonNull BiFunction<@NonNull String, @NonNull File, @NonNull ? extends Config> factory) {
    return ConfigHandlers.ofIdWithExtension(plugin, ".yml", factory);
  }

}
