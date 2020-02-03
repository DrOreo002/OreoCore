package me.droreo002.oreocore.configuration;

import com.google.common.base.Charsets;
import lombok.Getter;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

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
    private String version;
    @Getter
    private ConfigMemory registeredMemory;
    @Getter
    private boolean updateAble;

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
                ODebug.log(plugin, "Failed to create custom config file! &7(&e" + getFilePath() + "&7)", true);
                return;
            }
        }
        this.config = YamlConfiguration.loadConfiguration(yamlFile);
    }

    /**
     * Save the config, after saving the config comments will get updated also
     *
     * @param updateMemory Should we update the memory?
     */
    public void saveConfig(boolean updateMemory) {
        if (updateMemory) {
            if (registeredMemory == null) throw new NullPointerException("Registered memory cannot be null!");
            ConfigMemoryManager.updateMemory(getPlugin(), registeredMemory);
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
                saveConfig(true);

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

        ConfigUpdater.update(plugin, getFileName(), getYamlFile(), ignoredSection);
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
}
