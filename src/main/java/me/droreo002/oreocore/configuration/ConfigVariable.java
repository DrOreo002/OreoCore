package me.droreo002.oreocore.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigVariable {

    /**
     * The config path
     *
     * @return The config path specified
     */
    String path() default "";

    /**
     * Should we throw error when the config value is null?
     *
     * @return Should?
     */
    boolean errorWhenNull() default false;
}
