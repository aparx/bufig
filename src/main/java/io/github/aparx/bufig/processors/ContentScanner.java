package io.github.aparx.bufig.processors;

import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.processors.results.ContentScan;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.CheckReturnValue;

/**
 * Lexical analysis scanner for each line of (raw) configuration content.
 * <p>A scanner is scanning each line by line and returns an appropriate token for each line.
 * Such a scanner returns a {@code ScanProcess}, which can be used to perform each scan
 * manually to avoid unnecessary operations.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 00:38
 * @see ContentScan
 * @since 1.0-SNAPSHOT
 */
@CheckReturnValue
public interface ContentScanner<T extends Config> {

  ContentScan scan(@NonNull T caller, @NonNull String content);

}
