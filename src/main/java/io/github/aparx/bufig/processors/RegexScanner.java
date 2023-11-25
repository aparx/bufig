package io.github.aparx.bufig.processors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.errorprone.annotations.CheckReturnValue;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.processors.results.ContentScan;
import io.github.aparx.bufig.processors.results.ScannedLine;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-21 11:54
 * @since 1.0-SNAPSHOT
 */
@Getter
public class RegexScanner<T extends Config> implements ContentScanner<T> {

  private final @NonNull ImmutableSet<@NonNull LineMatcher<T, ? extends ScannedLine>> matchers;
  private final Function<ScanContext<? extends T>, @NonNull ? extends ScannedLine> fallback;

  public RegexScanner(
      @NonNull ImmutableSet<@NonNull LineMatcher<T, ? extends ScannedLine>> matchers,
      Function<ScanContext<? extends T>, @NonNull ? extends ScannedLine> fallback) {
    Preconditions.checkNotNull(matchers, "Matchers must not be null");
    Validate.notEmpty(matchers, "Matchers must not be empty");
    this.matchers = matchers;
    this.fallback = fallback;
  }

  @Override
  public ContentScan scan(@NonNull T caller, @NonNull String content) {
    Preconditions.checkNotNull(caller, "Caller must not be null");
    Preconditions.checkNotNull(content, "Content must not be null");
    return new ContentScan(content) {

      @Override
      public @NonNull Iterator<? extends ScannedLine> createProcess() {
        return new RegexLineIterator(caller, this);
      }
    };
  }

  @Getter
  @RequiredArgsConstructor
  public static final class ScanContext<T extends Config> {
    private final @NonNull ContentScan scan;
    private final @NonNull T caller;
    private int index;
    private String line;
    private Matcher matcher;
  }

  @RequiredArgsConstructor
  public static abstract class LineMatcher<T extends Config, R extends ScannedLine> {
    private final @lombok.NonNull Pattern pattern;

    public static <T extends Config, R extends ScannedLine> LineMatcher<T, R> of(
        @NonNull Pattern pattern, @NonNull Function<ScanContext<? extends T>, R> mapper) {
      return new LineMatcher<>(pattern) {
        @Override
        public R map(@NonNull ScanContext<? extends T> context) {
          return mapper.apply(context);
        }
      };
    }

    public abstract R map(@NonNull ScanContext<? extends T> context);
  }

  @CheckReturnValue
  private final class RegexLineIterator implements Iterator<ScannedLine> {

    @Getter
    private final @NonNull ScanContext<? extends T> context;
    private final @NonNull Iterator<@NonNull String> lineIterator;

    private int cursor;

    public RegexLineIterator(@NonNull T caller, @NonNull ContentScan scan) {
      Preconditions.checkNotNull(scan, "Process must not be null");
      Preconditions.checkNotNull(caller, "Caller must not be null");
      this.context = new ScanContext<>(scan, caller);
      this.lineIterator = scan.iterator();
    }

    @Override
    public boolean hasNext() {
      return lineIterator.hasNext();
    }

    @Override
    public @NonNull ScannedLine next() {
      context.matcher = null;
      context.line = lineIterator.next();
      context.index = cursor++;
      ScannedLine mapped = null;
      for (LineMatcher<? super T, ? extends ScannedLine> lineMatcher : matchers) {
        context.matcher = lineMatcher.pattern.matcher(context.line);
        if (!context.matcher.matches()) continue;
        mapped = lineMatcher.map(context);
        if (mapped != null) break;
      }
      if (mapped != null) return mapped;
      Preconditions.checkNotNull(fallback, "Fallback function is null");
      ScannedLine fallback = RegexScanner.this.fallback.apply(context);
      Preconditions.checkNotNull(fallback, "Fallback must not return null");
      return fallback;
    }
  }

}
