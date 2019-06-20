package me.droreo002.oreocore.configuration;

import lombok.Getter;
import me.droreo002.oreocore.debugging.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CustomConfig {

    @Getter
    private JavaPlugin plugin;
    @Getter
    private FileConfiguration config;
    @Getter
    private File yamlFile;
    @Getter
    private String fileName;
    @Getter
    private String filePath;
    @Getter
    private ConfigMemory registeredMemory;

    /**
     * Extend this class into another class. And cache it somewhere as a field
     * every time the class the initialized. The config will get created!
     *
     * @param plugin : The JavaPlugin class
     * @param yamlFile : The yaml file object
     */
    public CustomConfig(JavaPlugin plugin, File yamlFile) {
        this.plugin = plugin;
        this.yamlFile = yamlFile;
        this.fileName = yamlFile.getName();
        this.filePath = yamlFile.getAbsolutePath();
        setupConfig();
    }

    /**
     * Setup the config
     */
    private void setupConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        if (!yamlFile.exists()) {
            try {
                boolean success = yamlFile.createNewFile();
                if (success) {
                    plugin.saveResource(getFileName(), true);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Debug.log("Failed to create custom config file! &7(&e" + getFilePath() + "&7)", true);
                return;
            }
        }
        this.config = YamlConfiguration.loadConfiguration(yamlFile);
    }

    /**
     * Save the config
     *
     * @param updateMemory Should we update the memory?
     */
    public void saveConfig(boolean updateMemory) {
        try {
            config.save(yamlFile);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.log("Failed to save custom config file! &7(&e" + getFilePath() + "&7)", true);
        }
        if (updateMemory) {
            if (registeredMemory != null) ConfigMemoryManager.updateMemory(getPlugin(), registeredMemory);
        }
    }

    /**
     * Reload the config, will also keep the comments!
     */
    public void reloadConfig() {
        yamlFile = new File(filePath);
        if (!yamlFile.exists()) {
            setupConfig();
        }
        config = YamlConfiguration.loadConfiguration(yamlFile);
        if (registeredMemory != null) ConfigMemoryManager.updateMemory(getPlugin(), registeredMemory);

        saveConfig(false);
        InputStream configData = plugin.getResource(getFileName());
        if (configData != null) ConfigUpdater.update(yamlFile, configData);
    }

    /**
     * Register a memory
     *
     * @param memory The memory to register
     */
    public void registerMemory(ConfigMemory memory) {
        this.registeredMemory = memory;
        ConfigMemoryManager.registerMemory(getPlugin(), memory);
    }
}
