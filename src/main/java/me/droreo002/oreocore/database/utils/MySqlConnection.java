package me.droreo002.oreocore.database.utils;

import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class MySqlConnection implements SerializableConfigVariable<MySqlConnection> {

    @Getter
    private String host;
    @Getter
    private int port;
    @Getter
    private String databaseName;
    @Getter
    private String password;
    @Getter
    private String user;

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

    @Override
    public MySqlConnection getFromConfig(ConfigurationSection section) {
        String host = section.getString("host");
        int port = section.getInt("port");
        String databaseName = section.getString("databaseName");
        String password = section.getString("password");
        String user = section.getString("user");
        return new MySqlConnection(host, port, databaseName, password, user);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".host", host);
        config.set(path + ".port", port);
        config.set(path + ".databaseName", databaseName);
        config.set(path + ".password", password);
        config.set(path + ".user", user);
    }
}
