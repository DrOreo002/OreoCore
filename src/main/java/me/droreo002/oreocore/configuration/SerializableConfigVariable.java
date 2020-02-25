package me.droreo002.oreocore.configuration;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Define a serialize able config variable
 * anything that implement this must have
 * static method called deserialize that accept {@link org.bukkit.configuration.ConfigurationSection}
 * and returns itself
 */
public interface SerializableConfigVariable {

    /**
     * Serialize the object to a map
     *
     * @return The HashMap
     */
    @NotNull
    default Map<String, Object> serialize() {
        return new HashMap<>();
    }
}
