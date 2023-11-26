package io.github.aparx.bufig.configurable;

import io.github.aparx.bufig.ConfigPath;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.dataflow.qual.Deterministic;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:20
 * @since 1.0-SNAPSHOT
 */
public interface Configurable {

  @NonNull ConfigurableHandle<?> getHandle();

}
