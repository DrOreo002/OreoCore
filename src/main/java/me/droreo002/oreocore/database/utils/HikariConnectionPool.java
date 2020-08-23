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
import java.util.concurrent.TimeUnit;

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
    public HikariConnectionPool(JavaPlugin owningPlugin, DatabaseType databaseType, SQLConfiguration sqlConfig) {
        this.config = new HikariConfig();
        this.owningPlugin = owningPlugin;
        this.databaseType = databaseType;
        this.connectionAddress = sqlConfig;

        if (databaseType == DatabaseType.MYSQL) {
            this.config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            String[] addr = sqlConfig.getAddress().split(":");
            this.config.addDataSourceProperty("serverName", addr[0]);
            this.config.addDataSourceProperty("port", addr[1]);
            this.config.addDataSourceProperty("databaseName", sqlConfig.getDatabase(databaseType));
            this.config.setUsername(sqlConfig.getUsername());
            this.config.setPassword(sqlConfig.getPassword());

            // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
            this.config.addDataSourceProperty("cachePrepStmts", "true");
            this.config.addDataSourceProperty("prepStmtCacheSize", "250");
            this.config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            this.config.addDataSourceProperty("useServerPrepStmts", "true");
            this.config.addDataSourceProperty("useLocalSessionState", "true");
            this.config.addDataSourceProperty("rewriteBatchedStatements", "true");
            this.config.addDataSourceProperty("cacheResultSetMetadata", "true");
            this.config.addDataSourceProperty("cacheServerConfiguration", "true");
            this.config.addDataSourceProperty("elideSetAutoCommits", "true");
            this.config.addDataSourceProperty("maintainTimeStats", "false");
            this.config.addDataSourceProperty("alwaysSendSetIsolation", "false");
            this.config.addDataSourceProperty("cacheCallableStmts", "true");
        } else {
            this.config.setDataSourceClassName(null);
            File databaseFile = new File(owningPlugin.getDataFolder(), sqlConfig.getDatabase(databaseType));
            if (!databaseFile.exists()) databaseFile.createNewFile();
            this.config.setJdbcUrl(String.format("jdbc:sqlite:%s", sqlConfig.getDatabase(databaseType)));
            this.config.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        }

        this.config.setPoolName(owningPlugin.getName() + " : HikariCP");

        this.config.addDataSourceProperty("useUnicode", "true");
        this.config.addDataSourceProperty("characterEncoding", "utf8");
        this.config.setMaximumPoolSize(sqlConfig.getMaxPoolSize());
        this.config.setMinimumIdle(sqlConfig.getMinIdle());
        this.config.setMaxLifetime(sqlConfig.getMaxLifetime());
        this.config.setConnectionTimeout(sqlConfig.getConnectionTimeout());

        this.dataSource = new HikariDataSource(this.config);
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
}
