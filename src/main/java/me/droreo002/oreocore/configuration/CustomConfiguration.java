package me.droreo002.oreocore.configuration;

import lombok.Getter;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class CustomConfiguration implements Configuration {

    private FileConfiguration config;

    @Getter
    private JavaPlugin plugin;
    @Getter
    private File yamlFile;
    @Getter
    private String fileName, filePath, version;
    @Getter
    private ConfigurationMemory registeredMemory;
    @Getter
    private boolean updateAble;

    public CustomConfiguration(JavaPlugin plugin, File yamlFile) {
        this.plugin = plugin;
        this.yamlFile = yamlFile;
        this.fileName = yamlFile.getName();
        this.filePath = yamlFile.getAbsolutePath();
        setupConfig();
    }

    @Override
    public void setupConfig() {
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
                ODebug.log(plugin, "Failed to create custom config file! &7(&e" + getFilePath() + "&7)", true);
                return;
            }
        }
        this.config = YamlConfiguration.loadConfiguration(yamlFile);
    }

    @Override
    public void saveConfig(boolean updateMemory) {
        if (updateMemory) {
            if (registeredMemory == null) throw new NullPointerException("Registered memory cannot be null!");
            ConfigMemoryManager.updateMemory(registeredMemory);
        } else {
            try {
                config.save(yamlFile);
                updateComments();
            } catch (IOException e) {
                e.printStackTrace();
                ODebug.log(plugin, "Failed to save custom config file! &7(&e" + getFilePath() + "&7)", true);
            }
        }
    }

    @Override
    public void reloadConfig() {
        yamlFile = new File(filePath);
        if (!yamlFile.exists()) {
            setupConfig();
        }
        config = YamlConfiguration.loadConfiguration(yamlFile);
        if (registeredMemory != null) ConfigMemoryManager.updateMemory(registeredMemory);

        saveConfig(false);
    }

    /**
     * Mark the config as UpdateAble config
     *
     * @param configVersionPath The config version yaml path
     * @param latestVersion The latest version of the config
     * @return true if successfully updated, false otherwise
     */
    public boolean tryUpdate(String configVersionPath, String latestVersion) {
        this.updateAble = true;
        this.version = getConfig().getString(configVersionPath, "0.0");

        if (!version.equals(latestVersion)) {
            try {
                updateComments();
                getConfig().set(configVersionPath, latestVersion);
                saveConfig(registeredMemory != null);

                this.version = latestVersion;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Update config comments and containers
     *
     * @throws IOException If something goes wrong
     */
    private void updateComments() throws IOException {
        FileConfiguration source = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(getFileName())));
        List<String> ignoredSection = new ArrayList<>(getNewPaths(source, config));

        ConfigurationUpdater.update(plugin, getFileName(), getYamlFile(), ignoredSection);
    }

    /**
     * Register a memory
     *
     * @param memory The memory to register
     */
    @Override
    public void registerMemory(ConfigurationMemory memory) {
        this.registeredMemory = memory;
        ConfigMemoryManager.processMemory(memory);
    }

    /**
     * Get the new paths of config
     *
     * @param config The source config or the non edited
     * @param config2 The target config or the edited one
     * @return A list of new section containing a new path
     */
    private List<String> getNewPaths(FileConfiguration config, FileConfiguration config2) {
        List<String> newPaths = new ArrayList<>();

        for (String s : config2.getKeys(true)) {
            if (!config.getKeys(true).contains(s)) {
                String[] dat = s.split("\\.");
                String sectionData = dat[dat.length - 1];
                String newPath = s.replace("." + sectionData, "");
                if (newPaths.contains(newPath)) continue;
                newPaths.add(newPath);
            }
        }

        return newPaths;
    }

    /**
     * Write to file
     *
     * @param writer The writer
     * @param line Line to write
     * @throws IOException If there's something bad happens
     */
    private void write(Writer writer, String line) throws IOException {
        writer.write(line + System.lineSeparator());
    }

    @Override
    public @NotNull FileConfiguration getConfig(@Nullable String filePath) {
        return this.config;
    }
}
