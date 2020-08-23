package me.droreo002.oreocore.database;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.utils.io.FileUtils;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public abstract class FlatFileDatabase extends Database {

    @Getter
    private File dataFolder;
    @Getter
    private boolean loadDataOnStartup;
    @Getter
    private List<DataCache> dataCaches;

    public FlatFileDatabase(JavaPlugin plugin, String folderName, boolean loadDataOnStartup) {
        super(DatabaseType.FLAT_FILE, plugin);
        this.dataFolder = new File(plugin.getDataFolder(), folderName);
        this.dataCaches = new CopyOnWriteArrayList<>();
        this.loadDataOnStartup = loadDataOnStartup;
        init(); // You have to call this first!
    }

    public FlatFileDatabase(JavaPlugin plugin, File databaseFolder, boolean loadDataOnStartup) {
        super(DatabaseType.FLAT_FILE, plugin);
        this.dataFolder = databaseFolder;
        this.dataCaches = new CopyOnWriteArrayList<>();
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
                addData(new DataCache(YamlConfiguration.loadConfiguration(f), f));
            }
        }
    }

    @Override
    public void onDisable() {
        dataCaches.clear();
        ODebug.log(owningPlugin, "Database &bFlatFile &ffrom plugin &e" + owningPlugin.getName() + "&f has been disabled!", true);
    }

    /**
     * Load the data on first startup. User can call this
     * manually because sometimes they have to initialize something
     * on their constructor first
     */
    public void onInitialized() {}

    /**
     * Add defaults data into the data
     *
     * @param config Then config
     */
    public abstract void addDefaults(FileConfiguration config);

    /**
     * Add a new dataCache entry into the cache!
     *
     * @param dataCache The dataCache class
     */
    private void addData(DataCache dataCache) {
        String name = dataCache.getDataFileName();
        if (isDataCached(name)) return;
        dataCaches.add(dataCache);
    }

    /**
     * Add a new data entry into cache
     *
     * @param fileName The data's file name
     */
    public void addData(String fileName) {
        File file = new File(dataFolder, validateName(fileName));
        if (!file.exists()) return;
        addData(new DataCache(YamlConfiguration.loadConfiguration(file), file));
    }

    /**
     * Check if the data file exists or not
     *
     * @param fileName The file name
     * @return true if exists, false otherwise
     */
    public boolean isDataFileExists(String fileName) {
        return new File(dataFolder, validateName(fileName)).exists();
    }

    /**
     * Generate a new data
     *
     * @param fileName The file name
     */
    @SneakyThrows
    public void createData(@NotNull String fileName, boolean addDefault, @Nullable Consumer<CreateResult> createResultConsumer) {
        fileName = validateName(fileName);
        File file = new File(dataFolder, fileName);
        CreateResult createResult;
        if (file.exists()) {
            if (isDataCached(fileName)) return;
            addData(new DataCache(YamlConfiguration.loadConfiguration(file), file));
            createResult = CreateResult.LOADED;
        } else {
            file.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (addDefault) {
                addDefaults(config);
                config.save(file);
            }
            addData(new DataCache(config, file));
            createResult = CreateResult.CREATED_AND_LOADED;
        }
        if (createResultConsumer != null) createResultConsumer.accept(createResult);
    }

    /**
     * Generate a new data
     *
     * @param fileName The file name
     */
    public void createData(@NotNull String fileName) {
        createData(fileName, true, null);
    }

    /**
     * Get the data class
     *
     * @param fileName : The data file name
     * @return the data class if there's any. Null otherwise
     */
    @Nullable
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
     * @param dataCache The dataCache object
     * @param delete Should we delete it?
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
     * Remove the dataCache from the cache or delete it permanently
     *
     * @param fileName The file name
     * @param delete Should we delete it?
     */
    public void removeData(String fileName, boolean delete) {
        if (!isDataCached(fileName)) return;
        removeData(getDataCache(fileName), delete);
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
    public static class DataCache {
        private FileConfiguration config;
        private File dataFile;
        private String dataFileName;

        public DataCache(FileConfiguration config, File dataFile) {
            this.config = config;
            this.dataFile = dataFile;
            this.dataFileName = FileUtils.getFileName(dataFile, true);
        }
    }

    public enum CreateResult {
        CREATED_AND_LOADED,
        LOADED
    }
}
