package me.droreo002.oreocore.database.connection;

import lombok.Getter;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.utils.SQLConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLConnection {

    @Getter
    private final JavaPlugin owningPlugin;
    @Getter
    private DatabaseType databaseType;
    @Getter
    private SQLConfiguration connectionAddress;

    public SQLConnection(JavaPlugin owningPlugin, DatabaseType databaseType, SQLConfiguration sqlConfiguration) {
        this.owningPlugin = owningPlugin;
        this.databaseType = databaseType;
        this.connectionAddress = sqlConfiguration;
    }

    @NotNull
    public abstract Connection getConnection() throws SQLException;
    public abstract void close();
}
