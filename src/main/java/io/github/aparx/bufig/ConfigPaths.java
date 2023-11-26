package io.github.aparx.bufig;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;

/**
 * Set of utilities for testing and allocating {@code ConfigPath} objects.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-26 01:59
 * @since 1.0
 */
@UtilityClass
public final class ConfigPaths {

  public static boolean isEmpty(ConfigPath path) {
    return path == null || path.isEmpty();
  }

  public static boolean isEmpty(String path) {
    return path == null || StringUtils.isBlank(path);
  }

  public static boolean isNotEmpty(ConfigPath path) {
    return path != null && !path.isEmpty();
  }

  public static boolean isNotEmpty(String path) {
    return path != null && !StringUtils.isBlank(path);
  }

  public static ConfigPath concat(ConfigPath pathA, ConfigPath pathB) {
    boolean isEmptyA = isEmpty(pathA);
    boolean isEmptyB = isEmpty(pathB);
    if (isEmptyA && isEmptyB)
      return ConfigPath.of();
    if (isEmptyA) return pathB;
    if (isEmptyB) return pathA;
    return pathA.add(pathB);
  }

  public static ConfigPath parseConcat(ConfigPath pathA, String pathB, char pathSeparator) {
    boolean isEmptyA = isEmpty(pathA);
    boolean isEmptyB = isEmpty(pathB);
    if (isEmptyA && isEmptyB)
      return ConfigPath.of();
    if (isEmptyA)
      return ConfigPath.parse(pathB, pathSeparator);
    if (isEmptyB)
      return pathA;
    return pathA.add(ConfigPath.parse(pathB, pathSeparator));
  }

  public static ConfigPath parseConcat(String pathA, ConfigPath pathB, char pathSeparator) {
    boolean isEmptyA = isEmpty(pathA);
    boolean isEmptyB = isEmpty(pathB);
    if (isEmptyA && isEmptyB)
      return ConfigPath.of();
    if (isEmptyA)
      return pathB;
    if (isEmptyB)
      return ConfigPath.parse(pathA, pathSeparator);
    return ConfigPath.parse(pathA, pathSeparator).add(pathB);
  }

  public static ConfigPath parseConcat(String pathA, String pathB, char pathSeparator) {
    boolean isEmptyA = isEmpty(pathA);
    boolean isEmptyB = isEmpty(pathB);
    if (isEmptyA && isEmptyB)
      return ConfigPath.of();
    if (isEmptyA)
      return ConfigPath.parse(pathB, pathSeparator);
    if (isEmptyB)
      return ConfigPath.parse(pathA, pathSeparator);
    return ConfigPath.parse(pathA, pathSeparator)
        .add(ConfigPath.parse(pathB, pathSeparator));
  }

  public static String parseConcatJoin(ConfigPath pathA, String pathB, char pathSeparator) {
    if (isEmpty(pathA)) return pathB;
    return parseConcat(pathA, pathB, pathSeparator).join(pathSeparator);
  }

  public static String parseConcatJoin(String pathA, ConfigPath pathB, char pathSeparator) {
    if (isEmpty(pathB)) return pathA;
    return parseConcat(pathA, pathB, pathSeparator).join(pathSeparator);
  }

  public static String parseConcatJoin(String pathA, String pathB, char pathSeparator) {
    return parseConcat(pathA, pathB, pathSeparator).join(pathSeparator);
  }

}
