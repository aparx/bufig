package io.github.aparx.bufig.configurable.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

/**
 * Annotation applied to a field makes it a viable and serializable config field that can be
 * automatically added to a {@code ConfigurableHandle} within a {@code Configurable} instance.
 * <p>This annotation should only be applied to fields which declaring class implement {@code
 * Configurable}. This is just optional, fields whose declaring class is not a configurable
 * may still be considered valid config fields.
 * <p>The target field is just viable to be a config field, but must follow other
 * requirements to fully be a recognized as a valid config field. These requirements are
 * listed in {@link ConfigField} and {@link ConfigField#isValidField(Field)}.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:40
 * @see io.github.aparx.bufig.configurable.ConfigurableHandle
 * @see io.github.aparx.bufig.configurable.Configurable
 * @since 1.0-SNAPSHOT
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigMapping {

  String value() default "";

}
