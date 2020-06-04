package me.droreo002.oreocore.configuration.annotations;

import me.droreo002.oreocore.configuration.ValueType;

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
     * Check if its a serialize able object
     *
     * @return true or false. Depend
     */
    boolean isSerializableObject() default false;

    /**
     * Does it support for value updating?
     *
     * @return true or false. Depend
     */
    boolean isUpdateAbleObject() default false;

    /**
     * Get the yaml file name for this
     * config variable
     *
     * @return The yaml file name
     */
    String yamlFileName() default "";
    
    /**
     * The value type of this config, use auto for auto getting
     *
     * @return the type
     */
    ValueType valueType() default ValueType.AUTO;

    /**
     * Get the load priority of this config variable
     * memory manager will check if there's any priority set
     * and get the greater value first to execute
     *
     * @return Load priority as integer
     */
    int loadPriority() default 0;
}
