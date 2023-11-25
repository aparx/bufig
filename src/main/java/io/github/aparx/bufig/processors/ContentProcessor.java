package io.github.aparx.bufig.processors;

import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.processors.results.ContentScan;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 00:29
 * @since 1.0-SNAPSHOT
 */
@CheckReturnValue
public interface ContentProcessor<T extends Config> {

  String save(@NonNull T caller, @NonNull ContentScan scan);

  String load(@NonNull T caller, @NonNull ContentScan scan);

}
