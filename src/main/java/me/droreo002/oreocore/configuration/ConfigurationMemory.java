package me.droreo002.oreocore.configuration;

import lombok.NonNull;

public interface ConfigurationMemory {

    /**
     * Get the parent of this ConfigurationMemory
     *
     * @return The parent
     */
    @NonNull
    Configuration getParent();

    /**
     * Is this ConfigurationMemory's variable update-able?
     * default is false since updating all variable on config will create some lag
     * if this true then every value will try to change the config value
     * as the value they have.
     *
     * @return default is false
     */
    @NonNull
    default boolean isUpdatable() {
        return false;
    }
}
