package io.github.aparx.bufig.configurable.field;

import io.github.aparx.bufig.configurable.object.ConfigObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that when applied to a {@code ConfigObject} or field, being a valid {@code
 * ConfigField}, will result in the target being documented within a configuration.
 *
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 15:40
 * @see ConfigObject
 * @see ConfigField
 * @since 1.0-SNAPSHOT
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Document {

  String[] value();

  /**
   * For documentation applied to a type, means the documentation (meaning the header for
   * a configuration) is always forced to be updated to the here defined value.
   *
   * @return true to force the documentation change
   * @apiNote Only works for headers in the default implementations!
   */
  boolean force() default true;

}
