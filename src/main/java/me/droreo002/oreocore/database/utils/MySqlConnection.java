package me.droreo002.oreocore.database.utils;

import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MySqlConnection implements SerializableConfigVariable {

    @Getter
    private String host, databaseName, password, user;
    @Getter
    private int port;

    /**
     * Allow null for @ConfigVariable support
     */
    public MySqlConnection() {

    }

    public MySqlConnection(String host, int port, String databaseName, String password, String user) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.password = password;
        this.user = user;
    }

    public static MySqlConnection deserialize(ConfigurationSection section) {
        String host = section.getString("host");
        int port = section.getInt("port");
        String databaseName = section.getString("databaseName");
        String password = section.getString("password");
        String user = section.getString("user");
        return new MySqlConnection(host, port, databaseName, password, user);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("host", host);
        map.put("port", port);
        map.put("databaseName", databaseName);
        map.put("password", password);
        map.put("user", user);
        return map;
    }
}
