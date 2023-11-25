package io.github.aparx.bufig.defaults;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.AbstractConfig;
import io.github.aparx.bufig.processors.ContentProcessor;
import io.github.aparx.bufig.processors.ContentScanner;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.File;

/**
 * @param <S> self type, being the implementation type itself (primarily for processors)
 * @author aparx (Vinzent Z.)
 * @version 2023-11-20 23:04
 * @since 1.0-SNAPSHOT
 */
@Getter
public class DefaultConfig<S extends DefaultConfig<S>> extends AbstractConfig {

  private final @NonNull String id;
  private final @NonNull File file;

  private final ContentScanner<S> scanner;
  private final ContentProcessor<S> processor;

  public DefaultConfig(
      @NonNull FileConfiguration output,
      @NonNull String id,
      @NonNull File file,
      @NonNull ContentScanner<S> scanner,
      @NonNull ContentProcessor<S> processor) {
    super(output);
    Validate.notEmpty(id, "ID must not be empty");
    Preconditions.checkNotNull(file, "File must not be null");
    Preconditions.checkNotNull(scanner, "Scanner must not be null");
    Preconditions.checkNotNull(processor, "Processor must not be null");
    this.id = id;
    this.file = file;
    this.scanner = scanner;
    this.processor = processor;
  }

  @Override
  @SuppressWarnings("unchecked") // OK? assume the user passed right generic
  public synchronized String saveToString() {
    return processor.save((S) this, scanner.scan((S) this, getOutput().saveToString()));
  }

  @Override
  @SuppressWarnings("unchecked") // OK? assume the user passed right generic
  public synchronized void loadFromString(String contents) {
    try {
      getOutput().loadFromString(processor.load((S) this, scanner.scan((S) this, contents)));
    } catch (InvalidConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

}
