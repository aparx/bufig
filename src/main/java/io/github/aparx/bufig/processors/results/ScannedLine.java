package io.github.aparx.bufig.processors.results;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 00:30
 * @since 1.0-SNAPSHOT
 */
@Getter
@RequiredArgsConstructor
public class ScannedLine {
  private final int index;
  private final String line;

  @Override
  public String toString() {
    return "ScannedLine{" +
        "index=" + index +
        ", line='" + line + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ScannedLine)) return false;
    ScannedLine that = (ScannedLine) o;
    return index == that.index && Objects.equals(line, that.line);
  }

  @Override
  public int hashCode() {
    return Objects.hash(index, line);
  }
}
