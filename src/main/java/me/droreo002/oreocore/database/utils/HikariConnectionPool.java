package me.droreo002.oreocore.database.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.database.DatabaseType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public final class HikariConnectionPool {

    @Getter
    private final JavaPlugin owningPlugin;
    @Getter
    private HikariConfig config;
    @Getter
    private HikariDataSource dataSource;
    @Getter
    private DatabaseType databaseType;
    @Getter
    private SQLConfiguration connectionAddress;

    @SneakyThrows
    public HikariConnectionPool(JavaPlugin owningPlugin, DatabaseType databaseType, SQLConfiguration connectionAddress) {
        this.config = new HikariConfig();
        this.owningPlugin = owningPlugin;
        this.databaseType = databaseType;
        this.connectionAddress = connectionAddress;

        if (databaseType == DatabaseType.MYSQL) {
            this.config.setJdbcUrl(String.format("jdbc:sqlite:%s/%s", connectionAddress.getAddress(), connectionAddress.getDatabase(databaseType)));
            this.config.setUsername(connectionAddress.getUsername());
            this.config.setPassword(connectionAddress.getPassword());
        } else {
            File databaseFile = new File(owningPlugin.getDataFolder(), connectionAddress.getDatabase(databaseType));
            if (!databaseFile.exists()) databaseFile.createNewFile();
            this.config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            this.config.setJdbcUrl(String.format("jdbc:sqlite:%s", connectionAddress.getDatabase(databaseType)));
        }

        this.config.setPoolName(owningPlugin.getName() + " : HikariCP");

        this.config.addDataSourceProperty("useUnicode", "true");
        this.config.addDataSourceProperty("characterEncoding", "utf8");
        this.config.setMaximumPoolSize(10);
        this.config.setMinimumIdle(10);
        this.config.setConnectionInitSql("SELECT 1;");
        this.config.setMaxLifetime(1800000);
        this.config.setConnectionTimeout(5000);

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
