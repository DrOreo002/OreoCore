package me.droreo002.oreocore.database;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class Database {

    @Getter
    protected DatabaseType databaseType;
    @Getter
    protected JavaPlugin owningPlugin;

    public Database(@NotNull JavaPlugin owningPlugin, @NotNull DatabaseType databaseType) {
        this.databaseType = databaseType;
        this.owningPlugin = owningPlugin;
        DatabaseRegistry.register(this);
    }

    public abstract void init();
    public abstract void onDisable();
}
