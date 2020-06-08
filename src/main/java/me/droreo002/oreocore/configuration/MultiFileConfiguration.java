package me.droreo002.oreocore.configuration;

import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.droreo002.oreocore.utils.io.FileUtils.*;

public class MultiFileConfiguration implements Configuration {

    @Getter
    private JavaPlugin plugin;
    @Getter
    private File configFolder;
    @Getter
    private List<File> configFiles;
    @Getter
    private Map<String, FileConfiguration> loadedConfigs;
    @Getter
    private String parentPath;
    @Getter
    private ConfigurationMemory registeredMemory;

    public MultiFileConfiguration(JavaPlugin plugin, File configFolder) {
        this.plugin = plugin;
        this.configFolder = configFolder;
        this.configFiles = new ArrayList<>();
        this.parentPath = "plugins" + File.separator + plugin.getName() + File.separator + configFolder.getName() + File.separator;
        this.loadedConfigs = new HashMap<>();
        setupConfig();
    }

    @Override
    public @NotNull FileConfiguration getConfig(@Nullable String filePath) {
        if (filePath == null) throw new NullPointerException("File path cannot be null in MultiFile mode!");
        if (!filePath.contains(".yml")) throw new IllegalStateException("Path must contains .yml");
        return this.loadedConfigs.computeIfAbsent(((!ServerUtils.isLinux()) ? filePath.replace("/", "\\") : filePath), (s) -> {
            throw new NullPointerException("Cannot find config at " + filePath.replace("/", "\\") + ", please make sure the main folder name is not included.");
        });
    }

    @Override
    public void saveConfig(boolean updateMemory) {
        // TODO: 03/06/2020 Config saving for this?. Not really needed tho.., because we only uses this at AdvancedLuckpermsGUI..
    }

    @Override
    public void registerMemory(ConfigurationMemory memory) {
        this.registeredMemory = memory;
        ConfigMemoryManager.processMemory(memory);
    }

    @Override
    public void reloadConfig() {
        this.loadedConfigs.clear();
        for (File file : this.configFiles) {
            this.loadedConfigs.put(file.getPath().replace(getParentPath(), ""), YamlConfiguration.loadConfiguration(file));
        }

        if (this.registeredMemory != null) ConfigMemoryManager.processMemory(this.registeredMemory);
    }

    @Override
    @SneakyThrows
    public void setupConfig() {
        if (!configFolder.exists()) {
            configFolder.mkdirs();
        }
        copyResourcesRecursively(super.getClass().getResource("/" + configFolder.getName()), configFolder);
        getListFile(configFolder, this.configFiles);
        reloadConfig();
    }
}
