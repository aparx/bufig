package io.github.aparx.bufig.configurable.populator;

import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bufig.configurable.Configurable;
import io.github.aparx.bufig.configurable.ConfigurableHandle;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:31
 * @since 1.0-SNAPSHOT
 */
@CheckReturnValue
public interface ConfigurableValuePopulator<T extends Configurable> {

  /**
   * Adds or registers values to {@code handle} if needed. The implementation may want to
   * make use of the {@code accessor}, whose class members may be used as the values.
   * <p>It may only be decided to add values when a certain condition is met.
   *
   * @param handle   the handle to (optionally) populate with new values
   * @param accessor the accessor that requests {@code handle} to be populated (if wanted)
   */
  void populate(@NonNull ConfigurableHandle<? extends T> handle, @NonNull T accessor);

}
