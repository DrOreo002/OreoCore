package me.droreo002.oreocore.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public interface SerializableConfigVariable<T> {

    /**
     * Get the object or class from config
     *
     * @param section The configuration section
     * @return The object
     */
    T getFromConfig(ConfigurationSection section);

    /**
     * Save the object or class to the config
     *
     * @param path The path
     * @param config The config
     */
    void saveToConfig(String path, FileConfiguration config);
}
