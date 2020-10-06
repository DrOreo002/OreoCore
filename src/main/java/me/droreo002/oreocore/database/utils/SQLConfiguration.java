package me.droreo002.oreocore.database.utils;

import lombok.Getter;
import lombok.ToString;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.database.DatabaseType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ToString
public class SQLConfiguration implements SerializableConfigVariable {

    @NotNull
    private String database;
    @Getter
    @Nullable
    private String address, username, password, characterEncoding;
    @Getter
    private int maxPoolSize, minIdle, maxLifetime, connectionTimeout;
    @Getter
    private boolean useUnicode;

    /**
     * Allow null for @ConfigVariable support
     */
    public SQLConfiguration() {
        this.database = "database";
    }

    public SQLConfiguration(@NotNull String database, @Nullable String address, @Nullable String username,
                            @Nullable String password, @Nullable String characterEncoding, int maxPoolSize, int minIdle, int maxLifetime,
                            int connectionTimeout, boolean useUnicode) {
        this.database = database;
        this.address = address;
        this.username = username;
        this.password = password;
        this.characterEncoding = characterEncoding;
        this.maxPoolSize = maxPoolSize;
        this.minIdle = minIdle;
        this.maxLifetime = maxLifetime;
        this.connectionTimeout = connectionTimeout;
        this.useUnicode = useUnicode;
    }

    public SQLConfiguration(@NotNull String database, @Nullable String address, @Nullable String username, @Nullable String password) {
        this.database = database;
        this.address = address;
        this.username = username;
        this.password = password;
        this.maxPoolSize = 10;
        this.minIdle = 10;
        this.maxLifetime = 1800000;
        this.connectionTimeout = 5000;
        this.useUnicode = true;
        this.characterEncoding = "utf8";
    }

    public static SQLConfiguration sql(@NotNull String database) {
        return new SQLConfiguration(database, null, null, null);
    }

    public static SQLConfiguration mysql(@NotNull String database, @NotNull String address,
                                         @NotNull String username, @NotNull String password) {
        return new SQLConfiguration(database, address, username, password);
    }

    public static SQLConfiguration deserialize(ConfigurationSection section) {
        String database = section.getString("database", "database");
        String address = section.getString("address");
        String username = section.getString("username");
        String password = section.getString("password");
        int maxPoolSize = section.getInt("pool-settings.max-pool-size", 10);
        int minIdle = section.getInt("pool-settings.minimum-idle", 10);
        int maxLifetime = section.getInt("pool-settings.maximum-lifetime", 1800000);
        int connectionTimeout = section.getInt("pool-settings.connection-timeout", 5000);
        boolean useUnicode = section.getBoolean("pool-settings.properties.use-unicode", true);
        String characterEncoding = section.getString("pool-settings.properties.character-encoding", "utf8");
        return new SQLConfiguration(Objects.requireNonNull(database), address, username, password, characterEncoding,
                maxPoolSize, minIdle, maxLifetime, connectionTimeout, useUnicode);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("database", this.database);
        map.put("address", this.address);
        map.put("username", this.username);
        map.put("password", this.password);
        map.put("pool-settings.max-pool-size", this.maxPoolSize);
        map.put("pool-settings.minimum-idle", this.minIdle);
        map.put("pool-settings.maximum-lifetime", this.maxLifetime);
        map.put("pool-settings.connection-timeout", this.connectionTimeout);
        map.put("pool-settings.properties.use-unicode", this.useUnicode);
        map.put("pool-settings.properties.character-encoding", this.characterEncoding);
        return map;
    }

    public String getDatabase(DatabaseType databaseType) {
        if (databaseType == DatabaseType.SQL) {
            return (this.database.contains(".db")) ? this.database : this.database + ".db";
        }
        return this.database;
    }
}
