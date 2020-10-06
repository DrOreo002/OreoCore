package me.droreo002.oreocore.database.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.utils.SQLConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public final class HikariConnection extends SQLConnection {

    @Getter
    private HikariConfig config;
    @Getter
    private HikariDataSource dataSource;

    @SneakyThrows
    public HikariConnection(JavaPlugin owningPlugin, DatabaseType databaseType, SQLConfiguration sqlConfiguration) {
        super(owningPlugin, databaseType, sqlConfiguration);
        this.config = new HikariConfig();

        if (databaseType == DatabaseType.MYSQL) {
            this.config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            String[] addr = sqlConfiguration.getAddress().split(":");
            this.config.addDataSourceProperty("serverName", addr[0]);
            this.config.addDataSourceProperty("port", addr[1]);
            this.config.addDataSourceProperty("databaseName", sqlConfiguration.getDatabase(databaseType));
            this.config.setUsername(sqlConfiguration.getUsername());
            this.config.setPassword(sqlConfiguration.getPassword());

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
            File databaseFile = new File(owningPlugin.getDataFolder(), sqlConfiguration.getDatabase(databaseType));
            if (!databaseFile.exists()) databaseFile.createNewFile();
            this.config.setJdbcUrl("jdbc:sqlite:" + databaseFile);
            this.config.addDataSourceProperty("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        }

        this.config.setPoolName(owningPlugin.getName() + " : HikariCP");

        this.config.addDataSourceProperty("useUnicode", sqlConfiguration.isUseUnicode());
        this.config.addDataSourceProperty("characterEncoding", sqlConfiguration.getCharacterEncoding());
        this.config.setMaximumPoolSize(sqlConfiguration.getMaxPoolSize());
        this.config.setMinimumIdle(sqlConfiguration.getMinIdle());
        this.config.setMaxLifetime(sqlConfiguration.getMaxLifetime());
        this.config.setConnectionTimeout(sqlConfiguration.getConnectionTimeout());

        this.dataSource = new HikariDataSource(this.config);
    }

    @NotNull
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public void close() {
        this.dataSource.close();
    }
}
