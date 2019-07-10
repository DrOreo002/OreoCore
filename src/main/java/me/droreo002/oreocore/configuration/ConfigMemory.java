package me.droreo002.oreocore.configuration;

import lombok.NonNull;

public interface ConfigMemory {

    /**
     * Get the parent of this ConfigMemory
     *
     * @return The parent
     */
    @NonNull
    CustomConfig getParent();

    /**
     * Is this ConfigMemory's variable update-able?
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
