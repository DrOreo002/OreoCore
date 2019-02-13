package me.droreo002.oreocore.database;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {

    @Getter
    private final DatabaseType databaseType;
    @Getter
    private final JavaPlugin plugin;

    public Database(DatabaseType databaseType, JavaPlugin plugin) {
        this.databaseType = databaseType;
        this.plugin = plugin;
    }

    public abstract void init();
}
