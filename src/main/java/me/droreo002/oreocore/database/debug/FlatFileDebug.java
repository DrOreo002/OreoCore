package me.droreo002.oreocore.database.debug;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import me.droreo002.oreocore.utils.logging.Debug;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public class FlatFileDebug extends DatabaseFlatFile {

    public FlatFileDebug() {
        super(OreoCore.getInstance(), OreoCore.getInstance().getDataFolder());
        DatabaseManager.registerDatabase(OreoCore.getInstance(), this);
    }

    @Override
    public void loadData() {
        Debug.log("Data loading...", true);
        if (getDataFolder().listFiles() == null) {
            Debug.log("Cannot load data because the data folder is empty!", true);
            return;
        }
    }

    @Override
    public void addDefaults(FileConfiguration config) {
        // Default value
        config.set("Data.bangsat", true);
    }
}
