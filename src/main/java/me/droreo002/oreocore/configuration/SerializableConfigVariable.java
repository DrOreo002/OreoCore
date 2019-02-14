package me.droreo002.oreocore.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public interface SerializableConfigVariable<T> {

    T getFromConfig(ConfigurationSection section);

    void saveToConfig(String path, FileConfiguration config);
}
