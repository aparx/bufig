package io.github.aparx.bufig.defaults.yaml;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import io.github.aparx.bufig.ConfigPath;
import io.github.aparx.bufig.defaults.yaml.results.YamlCommentLine;
import io.github.aparx.bufig.defaults.yaml.results.YamlMappingLine;
import io.github.aparx.bufig.processors.ContentProcessor;
import io.github.aparx.bufig.processors.RegexScanner;
import io.github.aparx.bufig.processors.results.ContentScan;
import io.github.aparx.bufig.processors.results.ScannedLine;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 11:59
 * @since 1.0-SNAPSHOT
 */
@UtilityClass
public final class YamlProcessors {

  public static final Pattern SCANNER_COMMENT_PATTERN =
      Pattern.compile("^(?<w> *)# ?(?<v>.+)$");

  public static final Pattern SCANNER_MAPPING_PATTERN =
      Pattern.compile("^(?<w> *)(?<k>(?!^-)\\w+( +\\w+)*): *(?<v>.+)?$");

  public static final RegexScanner.LineMatcher<YamlConfig, YamlCommentLine>
      SCANNER_COMMENT_MATCHER = RegexScanner.LineMatcher.of(SCANNER_COMMENT_PATTERN,
      (ctx) -> new YamlCommentLine(ctx.getIndex(),
          StringUtils.length(ctx.getMatcher().group("w")) / ctx.getCaller().options().indent(),
          ctx.getLine(), ctx.getMatcher().group("v")));

  public static final RegexScanner.LineMatcher<YamlConfig, YamlMappingLine>
      SCANNER_MAPPING_MATCHER = RegexScanner.LineMatcher.of(SCANNER_MAPPING_PATTERN,
      (ctx) -> new YamlMappingLine(ctx.getIndex(),
          StringUtils.length(ctx.getMatcher().group("w")) / ctx.getCaller().options().indent(),
          ctx.getLine(), ctx.getMatcher().group("k"), ctx.getMatcher().group("v")));

  // @formatter:off
  public static final ImmutableSet<RegexScanner.LineMatcher<YamlConfig, ? extends ScannedLine>>
      SCANNER_LINE_MATCHERS = ImmutableSet.of(SCANNER_COMMENT_MATCHER, SCANNER_MAPPING_MATCHER);

  public static final Function<RegexScanner.ScanContext<? extends YamlConfig>, @NonNull ? extends ScannedLine>
      SCANNER_LINE_FALLBACK = (ctx) -> new ScannedLine(ctx.getIndex(), ctx.getLine());
  // @formatter:on

  private static final RegexScanner<YamlConfig> DEFAULT_SCANNER =
      new RegexScanner<>(SCANNER_LINE_MATCHERS, SCANNER_LINE_FALLBACK);

  private static final ContentProcessor<YamlConfig> DEFAULT_PROCESSOR =
      new YamlContentProcessor<>();

  @SuppressWarnings("unchecked")
  public static <T extends YamlConfig> RegexScanner<T> newScanner() {
    return (RegexScanner<T>) DEFAULT_SCANNER;
  }

  @SuppressWarnings("unchecked")
  public static <T extends YamlConfig> ContentProcessor<T> newProcessor() {
    return (ContentProcessor<T>) DEFAULT_PROCESSOR;
  }

  private static final class YamlContentProcessor<T extends YamlConfig> implements ContentProcessor<T> {

    /** To not have a too large memory footprint */
    private static final int MAX_NEST_DEPTH = 100;

    /** Initial buffer length for segments */
    private static final int INITIAL_SEGMENT_BUFFER_LENGTH = MAX_NEST_DEPTH / 10;

    private static String createCommentLine(String content) {
      return "# " + content;
    }

    private static String[] ensureNestDepthInArray(String[] array, int depth) {
      if (depth < array.length) return array;
      int newCapacity = 1 + Math.min((int) Math.ceil(1.75 * depth), MAX_NEST_DEPTH);
      Preconditions.checkArgument(depth < newCapacity,
          "Depth {0} too large (max: {1})",
          new Object[]{depth, MAX_NEST_DEPTH});
      String[] newArray = new String[newCapacity];
      System.arraycopy(array, 0, newArray, 0, array.length);
      return newArray;
    }

    @Override
    public String save(@NonNull T caller, @NonNull ContentScan scan) {
      StringBuilder builder = new StringBuilder();
      String[] segbuf = new String[INITIAL_SEGMENT_BUFFER_LENGTH];
      Iterator<? extends ScannedLine> process = scan.createProcess();
      final int indent = caller.options().indent();
      while (process.hasNext()) {
        if (builder.length() != 0)
          builder.append(System.lineSeparator());
        ScannedLine line = process.next();
        if (line instanceof YamlMappingLine) {
          YamlMappingLine mapping = (YamlMappingLine) line;
          int nestDepth = mapping.getNestDepth();
          segbuf = ensureNestDepthInArray(segbuf, nestDepth);
          segbuf[nestDepth] = mapping.getKey();
          String whitespace = " ".repeat(nestDepth * indent);
          ConfigPath path = ConfigPath.of(Arrays.copyOfRange(segbuf, 0, 1 + nestDepth));
          Arrays.stream(ArrayUtils.nullToEmpty(caller.getDocs(path)))
              .map(x -> whitespace + createCommentLine(x))
              .forEach(x -> builder.append(x).append(System.lineSeparator()));
        }
        builder.append(line.getLine());
      }

      return builder.toString();
    }

    @Override
    public String load(@NonNull T caller, @NonNull ContentScan scan) {
      StringBuilder builder = new StringBuilder();
      String[] segbuf = new String[INITIAL_SEGMENT_BUFFER_LENGTH];
      boolean isHeaderArea = true;
      ArrayList<String> docs = new ArrayList<>();
      Iterator<? extends ScannedLine> process = scan.createProcess();
      while (process.hasNext()) {
        ScannedLine line = process.next();
        if (line instanceof YamlCommentLine) {
          docs.add(((YamlCommentLine) line).getContent());
          continue;
        }
        if (builder.length() != 0)
          builder.append(System.lineSeparator());
        if (line instanceof YamlMappingLine) {
          YamlMappingLine mapping = (YamlMappingLine) line;
          int nestDepth = mapping.getNestDepth();
          segbuf = ensureNestDepthInArray(segbuf, nestDepth);
          segbuf[nestDepth] = mapping.getKey();
          ConfigPath path = ConfigPath.of(Arrays.copyOfRange(segbuf, 0, 1 + nestDepth));
          caller.setDocsIfAbsent(path, docs.toArray(String[]::new));
        } else if (isHeaderArea)
          docs.forEach(headerLine -> builder
              .append(createCommentLine(headerLine))
              .append(System.lineSeparator()));
        docs.clear();
        isHeaderArea = false;
        builder.append(line.getLine());
      }
      return builder.toString();
    }

  }

}
