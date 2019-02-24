package me.droreo002.oreocore.database.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.database.SQLType;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionPoolManager {

    @Getter
    private final JavaPlugin owningPlugin;
    @Getter
    private HikariConfig config;
    @Getter
    private HikariDataSource dataSource;
    @Getter
    private String jdbcUrl;

    @Getter
    @Setter
    private boolean mysql;
    @Getter
    @Setter
    private MySqlConnection addressData;

    public ConnectionPoolManager(String jdbcUrl, JavaPlugin owningPlugin) {
        this.config = new HikariConfig();
        this.jdbcUrl = jdbcUrl;
        this.mysql = false;
        this.owningPlugin = owningPlugin;
    }

    public void setup() {
        if (mysql) {
            Validate.notNull(addressData, "MySQLConnection cannot be null!");
            config.setJdbcUrl(jdbcUrl);
            config.setPoolName(owningPlugin.getName() + " : Data Pool");

            config.addDataSourceProperty("useUnicode", "true");
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(10);
            config.setConnectionInitSql("SELECT 1;");
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(5000);
            config.setPassword(addressData.getPassword());
            config.setUsername(addressData.getUser());
        } else {
            config.setJdbcUrl(jdbcUrl);
            config.setPoolName(owningPlugin.getName() + " : Data Pool");

            config.addDataSourceProperty("useUnicode", "true");
            config.addDataSourceProperty("characterEncoding", "utf8");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(10);
            config.setConnectionInitSql("SELECT 1;");
            config.setMaxLifetime(1800000);
            config.setConnectionTimeout(5000);
        }
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
