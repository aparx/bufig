package io.github.aparx.bufig.configurable.object;

import com.google.common.base.Preconditions;
import io.github.aparx.bufig.Config;
import io.github.aparx.bufig.configurable.Configurable;
import io.github.aparx.bufig.configurable.ConfigurableHandle;
import io.github.aparx.bufig.configurable.field.Document;
import io.github.aparx.bufig.configurable.populator.ConfigFieldPopulator;
import io.github.aparx.bufig.handler.ConfigHandler;
import io.github.aparx.bufig.handler.ConfigProxy;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.print.Doc;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 18:30
 * @since 1.0-SNAPSHOT
 */
public class ConfigObject extends ConfigProxy implements Configurable {

  private final String configId;

  @Getter
  private final ConfigurableHandle<? super ConfigObject> handle;

  public ConfigObject(
      @NonNull Function<@NonNull ConfigProxy, @NonNull Config> configSupplier,
      @NonNull ConfigurableHandle<? super ConfigObject> handle) {
    super(configSupplier);
    Preconditions.checkNotNull(handle, "Handle must not be null");
    this.configId = null;
    this.handle = handle;
  }

  public ConfigObject(
      @NonNull ConfigHandler<?> handler,
      @NonNull ConfigurableHandle<? super ConfigObject> handle) {
    super((proxy) -> handler.getOrCreate(proxy.getId()));
    Preconditions.checkNotNull(handler, "Handler must not be null");
    Preconditions.checkNotNull(handle, "Handle must not be null");
    this.configId = getStaticConfigId(getClass());
    this.handle = handle;
  }

  public ConfigObject(
      @NonNull String configId,
      @NonNull ConfigHandler<?> handler,
      @NonNull ConfigurableHandle<? super ConfigObject> handle) {
    super(configId, handler);
    Preconditions.checkNotNull(configId, "ID must not be null");
    Preconditions.checkNotNull(handler, "Handler must not be null");
    Preconditions.checkNotNull(handle, "Handle must not be null");
    this.configId = configId;
    this.handle = handle;
  }

  public ConfigObject(@NonNull Function<@NonNull ConfigProxy, @NonNull Config> configSupplier) {
    this(configSupplier, createFieldLookupHandle());
  }

  public ConfigObject(@NonNull ConfigHandler<?> handler) {
    this(handler, createFieldLookupHandle());
  }

  public ConfigObject(@NonNull String configId, @NonNull ConfigHandler<?> handler) {
    this(configId, handler, createFieldLookupHandle());
  }

  public static String getStaticConfigId(Class<?> cls) {
    return Optional.ofNullable(cls.getAnnotation(ConfigId.class))
        .map(ConfigId::value)
        .orElseThrow(() -> new IllegalStateException("Missing 'ConfigId' annotation"));
  }

  protected static ConfigurableHandle<ConfigObject> createFieldLookupHandle() {
    return new ConfigurableHandle<>(new ConfigFieldPopulator<>(ConfigObject.class));
  }

  @Override
  public final @NonNull String getId() {
    return configId == null ? super.getId() : configId;
  }

  @Override
  public void save() {
    super.save();
    getHandle().getValues(this).forEach(value -> {
      value.unsafeSet(this, get(value.toPath(getPathSeparator())));
    });
  }

  @Override
  public void load() {
    super.load();
    Document document = getClass().getAnnotation(Document.class);
    if (document != null && (document.force() || ArrayUtils.isEmpty(getHeader())))
      setHeader(document.value());
    getHandle().getValues(this).forEach(value -> {
      setIfAbsent(value.toPath(getPathSeparator()), () -> value.get(this), value.getDocs());
    });
    save();
  }
}
