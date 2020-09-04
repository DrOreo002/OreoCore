package me.droreo002.oreocore.database.connection;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.utils.SQLConfiguration;
import me.droreo002.oreocore.dependencies.Dependency;
import me.droreo002.oreocore.dependencies.OCoreDependency;
import me.droreo002.oreocore.dependencies.classloader.IsolatedClassLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class LegacyConnection extends SQLConnection {

    private Method createConnectionMethod;
    private HikariConnection hikariConnection;
    private Connection connection;
    private File databaseFile;

    public LegacyConnection(JavaPlugin owningPlugin, DatabaseType databaseType, SQLConfiguration sqlConfiguration) {
        super(owningPlugin, databaseType, sqlConfiguration);
        this.databaseFile = new File(owningPlugin.getDataFolder(), sqlConfiguration.getDatabase(databaseType));

        if (databaseType == DatabaseType.SQL) {
            try {
                if (!this.databaseFile.exists()) this.databaseFile.createNewFile();
                IsolatedClassLoader classLoader = OreoCore.getInstance().getDependencyManager().getIsolatedClassLoader(OCoreDependency.SQLITE_DRIVER.getDependency());
                Class<?> jdcbClass = classLoader.loadClass("org.sqlite.JDBC");
                this.createConnectionMethod = jdcbClass.getMethod("createConnection", String.class, Properties.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            hikariConnection = new HikariConnection(owningPlugin, databaseType, sqlConfiguration);
        }
    }

    @NotNull
    @Override
    public Connection getConnection() throws SQLException {
        if (this.hikariConnection != null) return hikariConnection.getConnection();
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = createConnection("jdbc:sqlite:" + this.databaseFile);
        }

        if (this.connection == null) {
            throw new SQLException("Unable to get a connection.");
        }

        return this.connection;
    }

    @Override
    public void close() {
        if (this.hikariConnection != null) {
            hikariConnection.close();
            return;
        }
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection createConnection(String url) throws SQLException {
        try {
            return (Connection) this.createConnectionMethod.invoke(null, url, new Properties());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof SQLException) {
                throw ((SQLException) e.getCause());
            }
            throw new RuntimeException(e);
        }
    }
}
