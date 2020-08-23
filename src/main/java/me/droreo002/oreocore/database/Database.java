package me.droreo002.oreocore.database;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Database {

    @Getter
    protected final DatabaseType databaseType;
    @Getter
    protected final JavaPlugin owningPlugin;

    public Database(DatabaseType databaseType, JavaPlugin owningPlugin) {
        Validate.notNull(owningPlugin, "Plugin cannot be null!");
        Validate.notNull(databaseType, "DatabaseType cannot be null!");
        this.databaseType = databaseType;
        this.owningPlugin = owningPlugin;
        DatabaseRegistry.register(this);
    }

    public abstract void init();
    public abstract void onDisable();
}
