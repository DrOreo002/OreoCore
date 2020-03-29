package me.droreo002.oreocore.database.object;

import lombok.Getter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.SQLDatabase;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.utils.ConnectionPoolManager;
import me.droreo002.oreocore.database.utils.MySqlConnection;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public abstract class DatabaseMySQL extends Database implements SQLDatabase {

    @Getter
    private ConnectionPoolManager connectionPoolManager;
    @Getter
    private Connection connection;
    @Getter
    private final MySqlConnection addressData;
    @Getter
    private final int updateTimeSecond;
    @Getter
    private int connectionCheckerTaskID;
    @Getter
    private SQLType sqlType;
    @Getter
    private boolean initialized;

    public DatabaseMySQL(JavaPlugin plugin, MySqlConnection addressData, int updateTimeSecond, SQLType sqlType) {
        super(DatabaseType.MYSQL, plugin);
        this.addressData = addressData;
        this.updateTimeSecond = updateTimeSecond;
        this.sqlType = sqlType;
        this.initialized = false;

        connectionPoolManager = new ConnectionPoolManager( "jdbc:mysql://" + addressData.getHost() + ":" + addressData.getPort() + "/" + addressData.getDatabaseName(), owningPlugin);
        connectionPoolManager.setMysql(true);
        connectionPoolManager.setAddressData(addressData);
        connectionPoolManager.setup();

        init();
    }

    @Override
    public void init() {
        if (initialized) throw new IllegalStateException("Database is already initialized!");
        initialized = true;

        if (checkConnection()) {
            if (execute(getSqlDatabaseTable().getCreateCommand())) {
                ODebug.log(owningPlugin,"&eMySQL &fConnection for plugin &c" + getOwningPlugin().getName() + "&f has been created!. DataCache address is &e" + addressData.getHost() + ":" + addressData.getPort() + "&f data is currently stored at &e" + addressData.getDatabaseName() + " &fdatabase&f, database type is &e" + sqlType, true);
            } else {
                ODebug.log(owningPlugin, "&cFailed to initialize the &bMySQL&f connection on plugin &e" + getOwningPlugin().getName() + "&c Please contact the dev!", false);
            }
        } else {
            throw new IllegalStateException("MySQL Connection for plugin " + getOwningPlugin().getName() + " cannot be proceeded!, please contact the dev!");
        }
        if (sqlType.equals(SQLType.SQL_BASED))
            this.connectionCheckerTaskID = new MysqlConnectionChecker().runTaskTimerAsynchronously(getOwningPlugin(), updateTimeSecond * 20, updateTimeSecond * 20).getTaskId();
    }

    @Override
    public void onDisable() {
        try {
            close();
            ODebug.log(owningPlugin,"&fDatabase &rMySQL &ffrom plugin &e" + owningPlugin.getName() + "&f has been disabled!", true);
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
                    Class.forName("com.mysql.jdbc.Driver");

                    String url = "jdbc:mysql://" + addressData.getHost() + ":" + addressData.getPort() + "/" + addressData.getDatabaseName();
                    return DriverManager.getConnection(url, addressData.getUser(), addressData.getPassword());
                } catch (ClassNotFoundException | SQLException e) {
                    // Handle possible Exception where connection can not be established
                    e.printStackTrace();
                }
            case HIKARI_CP:
                try {
                    return connectionPoolManager.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            case MARIA_DB:
                // TODO : Make
                return null;
            default:
                return null;
        }
    }

    // To ping the MySql Connection
    private class MysqlConnectionChecker extends BukkitRunnable {

        private MysqlConnectionChecker() {
            ODebug.log(owningPlugin,"&bMySQL Connection &fchecker for plugin &b" + getOwningPlugin().getName() + "&f has been started!", true);
        }

        @Override
        public void run() {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.createStatement().execute("SELECT 1"); // Dummy statement
                }
            } catch (SQLException e) {
                connection = getNewConnection();
            }
        }
    }
}
