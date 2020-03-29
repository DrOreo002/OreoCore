package me.droreo002.oreocore.database.object;

import lombok.Getter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.SQLDatabase;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.utils.ConnectionPoolManager;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public abstract class DatabaseSQL extends Database implements SQLDatabase {

    @Getter
    private ConnectionPoolManager connectionPoolManager;
    @Getter
    private Connection connection;
    @Getter
    private String databaseName;
    @Getter
    private File databaseFolder;
    @Getter
    private SQLType sqlType;
    @Getter
    private File databaseFile;
    @Getter
    private boolean initialized;

    public DatabaseSQL(JavaPlugin plugin, String databaseName, File databaseFolder, SQLType sqlType) {
        super(DatabaseType.SQL, plugin);
        this.databaseName = databaseName.replace(".db", "");
        this.databaseFolder = databaseFolder;
        this.sqlType = sqlType;
        this.initialized = false;

        if (!databaseFolder.exists()) databaseFolder.mkdir();
        init();
    }

    @Override
    public void init() {
        if (initialized) throw new IllegalStateException("Database is already initialized!");

        initialized = true;
        databaseFile = new File(databaseFolder, databaseName + ".db");
        if (!databaseFile.exists()) {
            try {
                if (databaseFile.createNewFile())
                    ODebug.log(owningPlugin, "&eSQL &fConnection for plugin &c" + getOwningPlugin().getName() + "&f has been created!. Database will now be stored at &e" + databaseFolder.getAbsolutePath() + "\\" + databaseName + ".db &f, database type is currently &e" + sqlType, true);
            } catch (IOException e) {
                ODebug.log(owningPlugin, "&fFailed to create database file on &b" + databaseFile.getAbsolutePath() + "&f. Plugin &e" + getOwningPlugin().getName() + "&f will now disabling itself!", true);
                Bukkit.getPluginManager().disablePlugin(getOwningPlugin());
                return;
            }
        }

        switch (sqlType) {
            case SQL_BASED:
                getNewConnection(); // Will also load it
                break;
            case HIKARI_CP:
                connectionPoolManager = new ConnectionPoolManager("jdbc:sqlite:" + databaseFile, owningPlugin);
                connectionPoolManager.setup();
                break;
            case MARIA_DB:
                // TODO: 23/10/2019 Make
                break;
        }

        if (checkConnection()) {
            if (execute(getSqlDatabaseTable().getCreateCommand())) {
                ODebug.log(owningPlugin, "&eSQL &fConnection for plugin &c" + getOwningPlugin().getName() + "&f has been initialized!", true);
            } else {
                ODebug.log(owningPlugin, "&cFailed to initialize the SQL connection on plugin &e" + getOwningPlugin().getName() + "&c Please contact the dev!", false);
            }
        } else {
            throw new IllegalStateException("SQL Connection for plugin " + getOwningPlugin().getName() + " cannot be proceeded!, please contact the dev!");
        }
    }

    @Override
    public void onDisable() {
        try {
            close();
            ODebug.log(owningPlugin, "&fDatabase &bSQL &ffrom plugin &e" + owningPlugin.getName() + "&f has been disabled!", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public Connection getNewConnection() {
        switch (sqlType) {
            case SQL_BASED:
                try {
                    if (this.connection != null && !this.connection.isClosed()) {
                        return this.connection;
                    }
                    Class.forName("org.sqlite.JDBC");

                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
                    return this.connection;
                } catch (SQLException | ClassNotFoundException e) {
                    ODebug.log(owningPlugin, "&fFailed to create database file on &b" + databaseFile.getAbsolutePath() + "&f. Plugin &e" + getOwningPlugin().getName() + "&f will now disabling itself!", true);
                    e.printStackTrace();

                    Bukkit.getPluginManager().disablePlugin(getOwningPlugin());
                    return null;
                }
            case HIKARI_CP:
                try {
                    return connectionPoolManager.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            case MARIA_DB:
                // TODO : Make
                return null;
            default:
                return null;
        }
    }
}
