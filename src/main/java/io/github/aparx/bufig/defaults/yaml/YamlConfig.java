package io.github.aparx.bufig.defaults.yaml;

import io.github.aparx.bufig.defaults.DefaultConfig;
import io.github.aparx.bufig.processors.ContentProcessor;
import io.github.aparx.bufig.processors.ContentScanner;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 12:02
 * @since 1.0-SNAPSHOT
 */
public class YamlConfig extends DefaultConfig<YamlConfig> {

  public YamlConfig(
      @NonNull String id,
      @NonNull File file,
      @NonNull ContentScanner<YamlConfig> scanner,
      @NonNull ContentProcessor<YamlConfig> processor) {
    super(new YamlConfiguration(), id, file, scanner, processor);
  }

  public YamlConfig(@NonNull String id, @NonNull File file) {
    this(id, file, YamlProcessors.newScanner(), YamlProcessors.newProcessor());
  }

  @Override
  public @NonNull YamlConfiguration getOutput() {
    return (YamlConfiguration) super.getOutput();
  }

  @Override
  public @NonNull YamlConfigurationOptions options() {
    return (YamlConfigurationOptions) super.options();
  }
}
