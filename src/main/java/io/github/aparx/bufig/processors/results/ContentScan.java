package io.github.aparx.bufig.processors.results;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Abstract class representing a scan of content that can always be redone from start to
 * finish, not being the result of a scan but a way to invoke it.
 * <p>All (default) operations executed through a scan will have no effect on the scan
 * itself, providing the ability to reuse it as often as needed.
 * <p>A scan contains a method called {@code createProcess}, which allocates a new
 * iterator that can be used to manually map each line to a {@code ScannedLine} (being the
 * token for a literal content line).
 * <p>To avoid having to collect all values of an iterator by yourself, there also is a
 * {@code collectProcess} method, which by default creates a new iterator using {@code
 * createIterator} to then collect each iteration into a list.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 00:30
 * @see #createProcess()
 * @see #collectProcess()
 * @since 1.0-SNAPSHOT
 */
@Getter
@Accessors(makeFinal = true)
public abstract class ContentScan implements Iterable<@NonNull String> {

  private final @NonNull String content;
  private final ImmutableList<@NonNull String> lines;

  public ContentScan(@NonNull String content) {
    this.content = content;
    this.lines = ImmutableList.copyOf(toLines(content));
  }

  public static String @NonNull [] toLines(@NonNull String content) {
    Preconditions.checkNotNull(content, "Content must not be null");
    return content.split("\r?\n");
  }

  /**
   * Returns a new iterator which tokenizes the next iterating line into a {@code ScannedLine},
   * which then can be used for further processing.
   *
   * @return the new scanning process iterator
   */
  public abstract @NonNull Iterator<? extends @NonNull ScannedLine> createProcess();

  /**
   * Creates a new process iterator and iterates it until it is ending while collecting all
   * scanned lines into a list (in occurring order).
   *
   * @return a new process as a collected list
   * @implNote Overriding implementations may want to cache the returning list, since the
   * lines themselves are deterministic.
   */
  public @NonNull List<ScannedLine> collectProcess() {
    ArrayList<ScannedLine> lineList = new ArrayList<>(lines.size());
    for (var process = createProcess(); process.hasNext(); )
      lineList.add(Objects.requireNonNull(process.next()));
    lineList.trimToSize();
    return lineList;
  }

  @Override
  public @NonNull Iterator<@NonNull String> iterator() {
    return lines.iterator();
  }
}
