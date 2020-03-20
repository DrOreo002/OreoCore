package me.droreo002.oreocore.database.debug;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.configuration.file.FileConfiguration;

public class FlatFileDebug extends DatabaseFlatFile {

    public FlatFileDebug() {
        super(OreoCore.getInstance(), OreoCore.getInstance().getDataFolder(), true);
        DatabaseManager.registerDatabase(OreoCore.getInstance(), this);
    }

    @Override
    public void onInitialized() {
        ODebug.log(owningPlugin,"DataCache loading...", true);
        if (getDataFolder().listFiles() == null) {
            ODebug.log(owningPlugin,"Cannot load data because the data folder is empty!", true);
        }
    }

    @Override
    public void addDefaults(FileConfiguration config) {
        // Default value
        config.set("DataCache.bangsat", true);
    }
}
