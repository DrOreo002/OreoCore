package me.droreo002.oreocore.configuration;

import lombok.Getter;
import me.droreo002.oreocore.debugging.Debug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class CustomConfig {

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

    public void saveConfig() {
        try {
            config.save(yamlFile);
        } catch (IOException e) {
            e.printStackTrace();
            Debug.log("Failed to save custom config file! &7(&e" + getFilePath() + "&7)", true);
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(yamlFile);
        InputStream configData = plugin.getResource(getFileName());
        if (configData != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(configData)));
        }
    }
}
