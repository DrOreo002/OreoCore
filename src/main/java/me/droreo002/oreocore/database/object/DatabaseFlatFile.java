package me.droreo002.oreocore.database.object;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.utils.io.FileUtils;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DatabaseFlatFile extends Database {

    @Getter
    private final File dataFolder;
    @Getter
    private final boolean loadDataOnStartup;

    /**
     * Where the string is the file name
     */
    @Getter
    private Set<DataCache> dataCaches;

    public DatabaseFlatFile(JavaPlugin plugin, File databaseFolder, boolean loadDataOnStartup) {
        super(DatabaseType.FLAT_FILE, plugin);
        this.dataFolder = databaseFolder;
        this.dataCaches = new HashSet<>();
        this.loadDataOnStartup = loadDataOnStartup;
        init(); // You have to call this first!
    }

    /*
    Init the thing that the data will need using the Database's initializer
     */
    @Override
    public void init() {
        File fileDataFolder = getOwningPlugin().getDataFolder();
        if (!fileDataFolder.exists()) fileDataFolder.mkdir();
        if (!dataFolder.exists()) dataFolder.mkdir();

        // Performance issue may occur
        if (loadDataOnStartup) {
            File[] files = getDataFolder().listFiles();
            if (files == null) return;
            for (File f : files) {
                FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(f);
                addData(new DataCache(fileConfig, f));
            }
        }
    }

    @Override
    public void onDisable() {
        dataCaches.clear();
        ODebug.log("Database &bFlatFile &ffrom plugin &e" + owningPlugin.getName() + "&f has been disabled!");
    }

    /**
     * Load the data on first startup. Only recommended to call it at that time useful for 'final' data
     */
    public void loadData() {}

    /**
     * Add defaults data into the data
     *
     * @param config Then config
     */
    public abstract void addDefaults(FileConfiguration config);

    /**
     * Add a new dataCache entry into the cache!
     *
     * @param dataCache : The dataCache class
     */
    private void addData(DataCache dataCache) {
        String name = dataCache.getDataFileName();
        if (isDataCached(name)) return;
        dataCaches.add(dataCache);
    }

    /**
     * Add a new data entry into cache
     *
     * @param fileName : The data's file name
     * @return true if succeeded, false otherwise
     */
    public boolean addData(String fileName) {
        if (!fileName.contains(".yml")) fileName += ".yml";
        File file = new File(dataFolder, fileName);
        if (!file.exists()) return false;
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        addData(new DataCache(config, file));
        return true;
    }

    /**
     * Check if the data file exists or not
     *
     * @param fileName The file name
     * @return true if exists, false otherwise
     */
    public boolean isDataFileExists(String fileName) {
        if (!fileName.contains(".yml")) fileName += ".yml";
        return new File(dataFolder, fileName).exists();
    }

    /**
     * Setup the FileConfiguration, this will also call the SetupCallback method. This is different from the load data
     *
     * @param fileName : The file name
     */
    public void setup(String fileName, boolean addDefault, SetupCallback callback) {
        fileName = validateName(fileName);
        File file = new File(dataFolder, fileName);
        if (file.exists()) {
            if (isDataCached(fileName)) return;
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            addData(new DataCache(config, file));
            callback.callBack(SetupCallbackType.LOADED);
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
        addData(new DataCache(config, file));
        callback.callBack(SetupCallbackType.CREATED_AND_LOADED);
    }

    /**
     * Setup the FileConfiguration, without calling the SetupCallback method
     *
     * @param fileName : The file name
     */
    public void setup(String fileName, boolean addDefault) {
        File file = new File(dataFolder, fileName.replace(".yml", "") + ".yml"); // Making sure it would not be .yml.yml
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            addData(new DataCache(config, file));
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
        addData(new DataCache(config, file));
    }

    /**
     * Get the data class
     *
     * @param fileName : The data file name
     * @return the data class if there's any. Null otherwise
     */
    public DataCache getDataCache(String fileName) {
        String newName = validateName(fileName);
        if (!isDataCached(newName)) return null;
        return dataCaches.stream().filter(dataCache -> dataCache.getDataFileName().equals(newName)).findAny().orElse(null);
    }

    /**
     * Save the dataCache with that specified name
     *
     * @param dataCache : The dataCache object
     */
    public void saveData(DataCache dataCache) {
        FileConfiguration config = dataCache.getConfig();
        File file = dataCache.getDataFile();
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        removeData(dataCache, false);
        addData(dataCache);
    }

    /**
     * Remove the dataCache from the cache or delete it permanently
     *
     * @param dataCache : The dataCache object
     * @param delete : Should we delete it?
     */
    public void removeData(DataCache dataCache, boolean delete) {
        if (delete) {
            File file = dataCache.getDataFile();
            if (!file.exists()) throw new IllegalStateException("File is not exist!. Cannot delete it!, file path is " + file.getAbsolutePath());
            file.delete();
        }
        dataCaches.removeIf(cache -> cache.getDataFileName().equals(dataCache.getDataFileName()));
    }

    /**
     * Check if the data is cached
     *
     * @param fileName The file name
     * @return true if its cached, false otherwise
     */
    public boolean isDataCached(String fileName) {
        return dataCaches.stream().filter(dataCache -> dataCache.getDataFileName().equals(validateName(fileName))).findAny().orElse(null) != null;
    }

    /**
     * Validate the name
     *
     * @param fileName The file name
     * @return The validated file name
     */
    private String validateName(String fileName) {
        return (fileName.contains(".yml")) ? fileName : fileName + ".yml";
    }

    @Data
    public class DataCache {
        private FileConfiguration config;
        private File dataFile;
        private String dataFileName;

        public DataCache(FileConfiguration config, File dataFile) {
            this.config = config;
            this.dataFile = dataFile;
            this.dataFileName = FileUtils.getFileName(dataFile, true);
        }
    }

    public interface SetupCallback {
        void callBack(SetupCallbackType type);
    }

    public enum SetupCallbackType {
        CREATED_AND_LOADED,
        LOADED
    }
}
