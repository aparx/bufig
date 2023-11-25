package io.github.aparx.bufig.configurable;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:20
 * @since 1.0-SNAPSHOT
 */
public interface Configurable {

  @NonNull ConfigurableHandle<?> getHandle();

}
