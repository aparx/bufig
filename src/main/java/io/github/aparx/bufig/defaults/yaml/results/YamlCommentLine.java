package io.github.aparx.bufig.defaults.yaml.results;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bufig.processors.results.ScannedLine;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 12:26
 * @since 1.0-SNAPSHOT
 */
@Getter
@CheckReturnValue
@Accessors(makeFinal = true)
public class YamlCommentLine extends ScannedLine {

  private final @NonNegative int nestDepth;
  private final @NonNull String content;

  public YamlCommentLine(
      @NonNegative int index,
      @NonNegative int nestDepth,
      @NonNull String line,
      @NonNull String content) {
    super(index, line);
    Preconditions.checkNotNull(content, "Content must not be null");
    this.nestDepth = nestDepth;
    this.content = content;
  }

  @Override
  public String toString() {
    return "YamlCommentLine{" +
        "nestDepth=" + nestDepth +
        ", content='" + content + '\'' +
        ", line='" + getLine() + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof YamlCommentLine)) return false;
    if (!super.equals(o)) return false;
    YamlCommentLine that = (YamlCommentLine) o;
    return nestDepth == that.nestDepth && Objects.equals(content, that.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), nestDepth, content);
  }
}
