package io.github.aparx.bufig;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Deterministic;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-20 15:46
 * @since 1.0-SNAPSHOT
 */
public abstract class AbstractConfig extends AbstractConfigSection implements Config {

  private final Map<ConfigPath, String[]> docMap = new HashMap<>();

  private final Map<ConfigPath, ConfigSection> sectionMap = new HashMap<>();

  @Getter(onMethod_ = {@Deterministic})
  private final @NonNull FileConfiguration output;

  public AbstractConfig(@NonNull FileConfiguration output) {
    Preconditions.checkNotNull(output, "Output must not be null");
    this.output = output;
  }

  public AbstractConfig(
      @NonNull ConfigSection parent,
      @NonNull ConfigurationSection section,
      @NonNull FileConfiguration output) {
    super(parent, section);
    Preconditions.checkNotNull(output, "Output must not be null");
    this.output = output;
  }

  @Override
  public synchronized void save() {
    createFileIfNotExisting();
    try (FileWriter writer = new FileWriter(getFile())) {
      writer.write(saveToString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public synchronized void load() {
    File file = getFile();
    if (!file.exists()) return;
    try (BufferedReader reader = new BufferedReader(new FileReader(getFile()))) {
      StringBuilder builder = new StringBuilder();
      for (String line; (line = reader.readLine()) != null; )
        builder.append(line).append(System.lineSeparator());
      String contents = builder.toString();
      loadFromString(contents);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @CanIgnoreReturnValue
  @SneakyThrows(IOException.class)
  public boolean createFileIfNotExisting() {
    File file = getFile();
    if (file.exists()) return false;
    File parent = file.getParentFile();
    if (parent != null && !parent.exists())
      Preconditions.checkState(parent.mkdirs(), "Cannot create parents: {0}", parent);
    return file.createNewFile();
  }

  @Override
  public boolean hasDocs(@NonNull ConfigPath path) {
    return docMap.containsKey(path);
  }

  @Override
  public boolean hasDocs(@NonNull String path) {
    return hasDocs(ConfigPath.parse(path, getPathSeparator()));
  }

  @Override
  public String @Nullable [] getDocs(@NonNull ConfigPath path) {
    return (String[]) ArrayUtils.clone(docMap.get(path));
  }

  @Override
  public String @Nullable [] getDocs(@NonNull String path) {
    return getDocs(ConfigPath.parse(path, getPathSeparator()));
  }

  @Override
  public void setDocs(@NonNull ConfigPath path, String... docs) {
    String[] array = Arrays.stream(docs).filter(Objects::nonNull).toArray(String[]::new);
    if (ArrayUtils.isEmpty(array)) docMap.remove(path);
    else docMap.put(path, array);
  }

  @Override
  public void setDocs(@NonNull String path, String... docs) {
    setDocs(ConfigPath.parse(path, getPathSeparator()), docs);
  }

  @Override
  public ConfigSection getSection(@NonNull ConfigPath path) {
    String stringPath = path.join(getPathSeparator());
    ConfigurationSection section = getOutput().getConfigurationSection(stringPath);
    if (section == null) sectionMap.remove(path);
    Preconditions.checkState(section != null, "Path {0} is not a section", stringPath);
    return sectionMap.computeIfAbsent(path, (ignored) -> createSubsection(section));
  }

  @Override
  public ConfigSection getSection(@NonNull String path) {
    return getSection(ConfigPath.parse(path, getPathSeparator()));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (!(o instanceof Config)) return false;
    Config that = (Config) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getFile(), that.getFile());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getFile());
  }

  protected ConfigSection createSubsection(ConfigurationSection section) {
    return new ConfigSubsection(this, section);
  }

  protected static class ConfigSubsection extends AbstractConfigSection {

    public ConfigSubsection(
        @NonNull ConfigSection parent,
        @NonNull ConfigurationSection section) {
      super(parent, section);
    }

    @Override
    public boolean hasDocs(@NonNull ConfigPath path) {
      return getRoot().hasDocs(getPath().add(path));
    }

    @Override
    public boolean hasDocs(@NonNull String path) {
      return getRoot().hasDocs(getPath().parseAdd(path, getPathSeparator()));
    }

    @Override
    public String @Nullable [] getDocs(@NonNull ConfigPath path) {
      return getRoot().getDocs(getPath().add(path));
    }

    @Override
    public String @Nullable [] getDocs(@NonNull String path) {
      return getRoot().getDocs(getPath().parseAdd(path, getPathSeparator()));
    }

    @Override
    public void setDocs(@NonNull ConfigPath path, String... docs) {
      getRoot().setDocs(getPath().add(path), docs);
    }

    @Override
    public void setDocs(@NonNull String path, String... docs) {
      getRoot().setDocs(getPath().parseAdd(path, getPathSeparator()), docs);
    }

    @Override
    public ConfigSection getSection(@NonNull ConfigPath path) {
      return getRoot().getSection(getPath().add(path));
    }

    @Override
    public ConfigSection getSection(@NonNull String path) {
      return getRoot().getSection(getPath().parseAdd(path, getPathSeparator()));
    }
  }
}
