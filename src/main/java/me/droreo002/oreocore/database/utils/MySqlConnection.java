package me.droreo002.oreocore.database.utils;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.FileConfiguration;

public class MySqlConnection {

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

    public MySqlConnection(String host, int port, String databaseName, String password, String user) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.password = password;
        this.user = user;
    }

    /**
     * Read from the config
     *
     * @param config : The config class
     * @param path : The main path. Example : Setting.mysql
     * @return a new MySqlConnection object if valid
     */
    public static MySqlConnection readFromConfig(FileConfiguration config, String path) {
        Validate.notNull(config, "Config cannot be null!");
        Validate.notNull(path, "Path cannot be null!");
        Validate.isTrue(path.charAt(path.length() - 1) == '.', ". (dot) at the end of the path is not allowed!");

        String host = config.getString(path + ".host");
        int port = config.getInt(path + ".port");
        String databaseName = config.getString(path + ".databaseName");
        String password = config.getString(path + ".password");
        String user = config.getString(path + ".user");

        return new MySqlConnection(host, port, databaseName, password, user);
    }
}
