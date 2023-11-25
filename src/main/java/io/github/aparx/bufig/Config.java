package io.github.aparx.bufig;

import com.google.errorprone.annotations.CheckReturnValue;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Deterministic;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-20 14:42
 * @since 1.0-SNAPSHOT
 */
public interface Config extends ConfigSection {

  @Deterministic
  @NonNull String getId();

  @NonNull File getFile();

  @NonNull FileConfiguration getOutput();

  void save();

  @CheckReturnValue
  String saveToString();

  void load();

  void loadFromString(String contents);

  default @Nullable String @Nullable [] getHeader() {
    String header = options().header();
    if (StringUtils.isEmpty(header))
      return ArrayUtils.EMPTY_STRING_ARRAY;
    return header.split("\r?\n");
  }

  default void setHeader(String @Nullable [] header) {
    options().header(ArrayUtils.isNotEmpty(header)
        ? Arrays.stream(header).collect(Collectors.joining(System.lineSeparator()))
        : null);
  }

}
