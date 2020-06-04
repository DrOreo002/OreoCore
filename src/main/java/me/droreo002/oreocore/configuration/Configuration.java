package me.droreo002.oreocore.configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Configuration {

    /**
     * Get config by that file path
     *
     * @param filePath The file path
     * @return FileConfiguration
     */
    @NotNull
    FileConfiguration getConfig(@Nullable String filePath);

    /**
     * Get the plugin owner
     *
     * @return The plugin
     */
    JavaPlugin getPlugin();

    /**
     * Save the config
     *
     * @param updateMemory Should we update the memory?
     */
    void saveConfig(boolean updateMemory);

    /**
     * Reload the config
     */
    void reloadConfig();

    /**
     * Setup the config
     */
    void setupConfig();

    /**
     * Register a ConfigurationMemory
     *
     * @param memory The memory
     */
    void registerMemory(ConfigurationMemory memory);

    /**
     * Get config, by null path
     *
     * @return FileConfiguration
     */
    @NotNull
    default FileConfiguration getConfig() {
        return getConfig(null);
    }
}
