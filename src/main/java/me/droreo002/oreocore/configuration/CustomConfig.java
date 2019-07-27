package me.droreo002.oreocore.configuration;

import com.google.common.base.Charsets;
import lombok.Getter;
import me.droreo002.oreocore.debugging.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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
     * Save the config, after saving the config comments will get updated also
     *
     * @param updateMemory Should we update the memory?
     */
    public void saveConfig(boolean updateMemory) {
        if (updateMemory) {
            if (registeredMemory != null) ConfigMemoryManager.updateMemory(getPlugin(), registeredMemory);
        }
        try {
            config.save(yamlFile);
            updateComments(plugin.getResource(getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
            Debug.log("Failed to save custom config file! &7(&e" + getFilePath() + "&7)", true);
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
        if (configData != null) {
            try {
                updateComments(configData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
     * Update config comments, currently private usage only
     *
     * @param commentSource Config comment source
     * @throws IOException If there's something bad happens
     */
    private void updateComments(InputStream commentSource) throws IOException {
        final File toUpdate = getYamlFile();
        if (!toUpdate.exists()) throw new NullPointerException("File cannot be null!");
        Reader baseReader = new InputStreamReader(commentSource, Charsets.UTF_8);
        FileConfiguration config = YamlConfiguration.loadConfiguration(toUpdate);
        BufferedReader reader = new BufferedReader(baseReader);

        final Writer writer = new OutputStreamWriter(new FileOutputStream(toUpdate), Charsets.UTF_8);
        final List<String> checked = new ArrayList<>();
        String line;
        outer: while ((line = reader.readLine()) != null) {

            if (line.startsWith("#")) {
                write(writer, line);
                continue;
            }

            for (String key : config.getKeys(true)) {
                if (checked.contains(key)) continue;
                String[] keyArray = key.split("\\.");
                String keyString = keyArray[keyArray.length - 1];

                if (line.trim().startsWith(keyString + ":")) {
                    checked.add(key);
                    if (config.isConfigurationSection(key)) {
                        write(writer, line);
                        continue outer;
                    }

                    String[] array = line.split(": ");

                    if (array.length > 1) {
                        if (array[1].startsWith("\"") || array[1].startsWith("'")) {
                            char c = array[1].charAt(0);
                            String s = config.getString(key);
                            if (s.contains("'")) {
                                s = s.replace("'", "''");
                            }
                            line = array[0] + ": " + c + s + c;
                        } else {
                            line = array[0] + ": " + config.get(key);
                        }
                    }
                    write(writer, line);
                    continue outer;
                }
            }
            write(writer, line);
        }
        writer.flush();
        writer.close();
        reader.close();
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
