package me.droreo002.oreocore.configuration.annotations;

import me.droreo002.oreocore.configuration.SerializableConfigVariable;

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
    String path();

    /**
     * Should we throw error when the config value is null?
     *
     * @return true or false. Depend.
     */
    boolean errorWhenNull() default false;

    /**
     * Does it implements {@link SerializableConfigVariable}
     *
     * @return true or false. Depend
     */
    boolean isSerializableObject();
}
