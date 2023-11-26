package io.github.aparx.bufig.defaults.yaml.results;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bufig.processors.results.ScannedLine;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 12:26
 * @since 1.0-SNAPSHOT
 */
@Getter
@CheckReturnValue
@Accessors(makeFinal = true)
public class YamlMappingLine extends ScannedLine {

  private final @NonNegative int nestDepth;
  private final @NonNull String key;
  private final @Nullable String value;

  public YamlMappingLine(
      @NonNegative int index,
      @NonNegative int nestDepth,
      @NonNull String line,
      @NonNull String key,
      @Nullable String value) {
    super(index, line);
    Preconditions.checkNotNull(key, "Key must not be null");
    this.nestDepth = nestDepth;
    this.key = key;
    this.value = value;
  }

  public boolean hasValueInLine() {
    return value != null;
  }

  @Override
  public String toString() {
    return "YamlMappingLine{" +
        "nestDepth=" + nestDepth +
        ", key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", line='" + getLine() + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof YamlMappingLine)) return false;
    if (!super.equals(o)) return false;
    YamlMappingLine that = (YamlMappingLine) o;
    return nestDepth == that.nestDepth && Objects.equals(key, that.key) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nestDepth, key, value);
  }
}
