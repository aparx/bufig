package io.github.aparx.bufig.configurable.object;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aparx (Vinzent Z.)
 * @version 2023-11-22 18:31
 * @since 1.0-SNAPSHOT
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigId {
  
  String value();

}
