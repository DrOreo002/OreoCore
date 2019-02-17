package me.droreo002.oreocore.database.object;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.utils.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class DatabaseFlatFile extends Database {

    @Getter
    private final File dataFolder;

    /**
     * Where the string is the file name
     */
    @Getter
    private Map<String, Data> dataCache;

    public DatabaseFlatFile(JavaPlugin plugin, File databaseFolder) {
        super(DatabaseType.FLAT_FILE, plugin);
        this.dataFolder = databaseFolder;
        this.dataCache = new HashMap<>();
        init(); // You have to call this first!
    }

    /*
    Init the thing that the data will need using the Database's initializer
     */
    @Override
    public void init() {
        File fileDataFolder = getPlugin().getDataFolder();
        if (!fileDataFolder.exists()) fileDataFolder.mkdir();
        if (!dataFolder.exists()) dataFolder.mkdir();

        File[] files = getDataFolder().listFiles();
        if (files == null) return;
        for (File f : files) {
            FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(f);
            addData(new DatabaseFlatFile.Data(fileConfig, f));
        }
    }

    /**
     * Load the data on first startup. Only recommended to call it at that time
     */
    public abstract void loadData();
    public abstract void addDefaults(FileConfiguration config);

    /**
     * Add a new data entry into the cache!
     *
     * @param data : The data class
     */
    public void addData(Data data) {
        String name = FileUtils.getFileName(data.getDataFile(), false);
        if (dataCache.containsKey(name)) return;
        dataCache.put(name, data);
    }

    /**
     * Add a new data entry into cache
     *
     * @param fileName : The data's file name
     */
    public void addData(String fileName) {
        if (!fileName.contains(".yml")) throw new IllegalStateException("Filename must contains .yml!");
        File file = new File(dataFolder, fileName);
        if (!file.exists()) throw new NullPointerException("Cannot add data from a null file!");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        addData(new Data(config, file));
    }

    /**
     * Setup the FileConfiguration, this will also call the setup method. This is different from the load data
     *
     * @param fileName : The file name`
     */
    public void setup(String fileName, boolean addDefault) {
        if (!fileName.contains(".yml")) throw new IllegalStateException("Filename must contains .yml!");
        String keyName = fileName.replace(".yml", "");
        if (isDataCached(keyName)) return;
        File file = new File(dataFolder, fileName);
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            addData(new Data(config, file));
            return;
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (addDefault) {
            addDefaults(config);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        addData(new Data(config, file));
    }

    /**
     * Get the data class
     *
     * @param fileName : The data file name
     * @return the data class if there's any. Null otherwise
     */
    public Data getDataClass(String fileName) {
        return dataCache.get(fileName);
    }

    /**
     * Save the data with that specified name
     *
     * @param data : The data object
     */
    public void saveData(Data data) {
        FileConfiguration config = data.getConfig();
        File file = data.getDataFile();
        String fileName = FileUtils.getFileName(file, false);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Update the HashMap
        dataCache.put(fileName, new Data(config, file));
    }

    /**
     * Remove the data from the cache or delete it permanently
     *
     * @param data : The data object
     * @param delete : Should we delete it?
     */
    public void removeData(Data data, boolean delete) {
        if (delete) {
            File file = data.getDataFile();
            if (!file.exists()) throw new IllegalStateException("File is not exist!. Cannot delete it!, file path is " + file.getAbsolutePath());
            file.delete();
            dataCache.remove(FileUtils.getFileName(data.getDataFile(), false));
        } else {
            dataCache.remove(FileUtils.getFileName(data.getDataFile(), false));
        }
    }

    /**
     * Check if the data is cached
     *
     * @param dataName : The file name
     * @return true if its cached, false otherwise
     */
    public boolean isDataCached(String dataName) {
        return dataCache.containsKey(dataName);
    }

    public class Data {

        @Getter
        @Setter
        private FileConfiguration config;
        @Getter
        @Setter
        private File dataFile;

        public Data(FileConfiguration config, File dataFile) {
            this.config = config;
            this.dataFile = dataFile;
        }
    }
}
