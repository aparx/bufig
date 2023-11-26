package io.github.aparx.bufig;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing a specific location within a configuration, determined by an
 * array of
 * segments defining the final path (delimited by an external separator).
 * <p>Every segment of a path is ensured to not be null and not blank.
 * <p>A string path can be parsed into a {@code ConfigPath} through the
 * {@link #parse(String, char)} method, or {@link #parseAdd(String, char)} to add a string
 * path to a current path object.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-20 14:43
 * @see #parse(String, char)
 * @see #parseAdd(String, char)
 * @see #join(char)
 * @see #isValidSegment(String)
 * @since 1.0-SNAPSHOT
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigPath implements Iterable<@NonNull String> {

  public static final char DEFAULT_SEPARATOR = '.';

  private static final ConfigPath EMPTY = new ConfigPath(ArrayUtils.EMPTY_STRING_ARRAY);

  private final @NonNull String @NonNull [] segments;

  public static boolean isValidSegment(String segment) {
    return StringUtils.isNotBlank(segment);
  }

  public static ConfigPath of(String @NonNull [] segments) {
    if (segments.length == 0)
      return EMPTY;
    if (segments.length == 1)
      return of(segments[0]);
    String[] array = Arrays.stream(segments)
        .filter(ConfigPath::isValidSegment)
        .toArray(String[]::new);
    return ArrayUtils.isEmpty(array) ? EMPTY : new ConfigPath(array);
  }

  public static ConfigPath of(String segment, String... successors) {
    return of((String[]) ArrayUtils.add(successors, 0, segment));
  }

  public static ConfigPath of(String segment) {
    if (!isValidSegment(segment)) return EMPTY;
    return new ConfigPath(new String[]{segment});
  }

  public static ConfigPath of() {
    return EMPTY;
  }

  /**
   * Parses given {@code path} string into a path object by splitting it up into segments
   * using the given {@code pathSeparator} as the delimiter. Any blank segment is ignored.
   *
   * @param path          the string path to parse
   * @param pathSeparator the path separator, used to split given {@code path}
   * @return the path object, which contains non-null non-blank segments in order of split
   * @see #of(String[])
   * @see #isValidSegment(String)
   * @see String#split(String)
   */
  public static ConfigPath parse(String path, char pathSeparator) {
    if (path.isEmpty())
      return EMPTY;
    int index = path.indexOf(pathSeparator);
    if (index == -1)
      return of(path);
    Vector<String> vector = new Vector<>();
    do {
      String segment = path.substring(0, index);
      if (isValidSegment(segment)) vector.add(segment);
      path = path.substring(1 + index);
    } while ((index = path.indexOf(pathSeparator)) != -1);
    if (isValidSegment(path)) vector.add(path);
    else if (vector.isEmpty()) return EMPTY;
    //noinspection ToArrayCallWithZeroLengthArrayArgument
    return new ConfigPath(vector.toArray(new String[vector.size()]));
  }


  /** @deprecated Use ConfigPaths#isEmpty instead. */
  @Deprecated(forRemoval = true)
  public static boolean isEmpty(ConfigPath path) {
    return ConfigPaths.isEmpty(path);
  }

  /** @deprecated Use ConfigPaths#isNotEmpty instead. */
  @Deprecated(forRemoval = true)
  public static boolean isNotEmpty(ConfigPath path) {
    return ConfigPaths.isNotEmpty(path);
  }

  public int length() {
    return segments.length;
  }

  public boolean isEmpty() {
    return segments.length == 0;
  }

  /**
   * Joins all segments to a string using {@code pathSeparator}.
   * <p>This method is a kind of inverse to the {@code parse} method.
   *
   * @param pathSeparator the separator used to join all segments
   * @return a new string of all segments joined using {@code pathSeparator}
   * @see #join()
   * @see #parse(String, char)
   * @see #parseAdd(String, char)
   */
  public String join(char pathSeparator) {
    final int n = length();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < n; ++i) {
      if (i != 0) builder.append(pathSeparator);
      builder.append(Objects.requireNonNull(segments[i]));
    }
    return builder.toString();
  }

  public String join() {
    return join(DEFAULT_SEPARATOR);
  }

  public String first() {
    return get(0);
  }

  public String last() {
    return get(length() - 1);
  }

  public @NonNull String get(@NonNegative int index) {
    Preconditions.checkElementIndex(index, length());
    return Objects.requireNonNull(segments[index]);
  }

  @CheckReturnValue
  public @NonNull ConfigPath set(@NonNegative int index, String segment) {
    Preconditions.checkElementIndex(index, length());
    if (!isValidSegment(segment))
      return new ConfigPath((String[]) ArrayUtils.remove(segments, index));
    @NonNull String[] array = toArray();
    array[index] = segment;
    return new ConfigPath(array);
  }

  @CheckReturnValue
  public @NonNull ConfigPath add(@NonNull ConfigPath other) {
    if (other.isEmpty()) return this;
    if (isEmpty()) return other;
    return new ConfigPath((String[]) ArrayUtils.addAll(segments, other.segments));
  }

  @CheckReturnValue
  public @NonNull ConfigPath add(String @NonNull [] other) {
    if (ArrayUtils.isEmpty(other)) return this;
    if (isEmpty()) return of(other);
    return of((String[]) ArrayUtils.addAll(segments, other));
  }

  @CheckReturnValue
  public @NonNull ConfigPath add(String segment) {
    if (isEmpty()) return of(segment);
    if (!isValidSegment(segment)) return this;
    return new ConfigPath((String[]) ArrayUtils.add(segments, segment));
  }

  @CheckReturnValue
  public @NonNull ConfigPath parseAdd(String join, char pathSeparator) {
    return add(ConfigPath.parse(join, pathSeparator));
  }

  @CheckReturnValue
  public ConfigPath subpath(int startInclusiveIndex, int stopExclusiveIndex) {
    Preconditions.checkElementIndex(startInclusiveIndex, length());
    Preconditions.checkPositionIndex(stopExclusiveIndex, length());
    Preconditions.checkState(startInclusiveIndex <= stopExclusiveIndex,
        "startInclusiveIndex must be less or equal to stopExclusiveIndex");
    if (startInclusiveIndex == stopExclusiveIndex)
      return EMPTY;
    return new ConfigPath(Arrays.copyOfRange(segments, startInclusiveIndex,
        stopExclusiveIndex));
  }

  public ConfigPath subpath(int startInclusiveIndex) {
    return subpath(startInclusiveIndex, length());
  }

  public @NonNull Stream<@NonNull String> stream() {
    return Arrays.stream(segments);
  }

  public @NonNull String @NonNull [] toArray() {
    return (String[]) ArrayUtils.clone(segments);
  }

  @Override
  public @NonNull Iterator<@NonNull String> iterator() {
    return new Iterator<>() {
      int cursor = 0;

      @Override
      public boolean hasNext() {
        return cursor < length();
      }

      @Override
      public @NonNull String next() {
        return get(cursor++);
      }
    };
  }

  @Override
  public String toString() {
    return "ConfigPath{" + Arrays.toString(segments) + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ConfigPath strings = (ConfigPath) o;
    return Arrays.equals(segments, strings.segments);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(segments);
  }
}
