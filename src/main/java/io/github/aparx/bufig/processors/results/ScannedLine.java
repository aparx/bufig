package io.github.aparx.bufig.processors.results;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
