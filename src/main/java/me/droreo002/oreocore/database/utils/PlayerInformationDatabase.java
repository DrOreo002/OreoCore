package me.droreo002.oreocore.database.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.dummy.PluginConfig;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.SQLDatabase;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.droreo002.oreocore.database.utils.SQLDataKey.create;

public class PlayerInformationDatabase extends SQLDatabase {

    @Getter
    private final Set<PlayerInformation> playerInformation = new HashSet<>();
    @Getter
    private PluginConfig memory;

    public PlayerInformationDatabase(OreoCore plugin) {
        super(plugin, DatabaseType.SQL, SQLConfiguration.sql("playerdata.db"), SQLTableBuilder.of("playerData")
                .addKey(create("playerName", SQLDataKey.KeyType.MINECRAFT_USERNAME).primary())
                .addKey(create("uuid", SQLDataKey.KeyType.UUID)));
        this.memory = plugin.getPluginConfig();
        loadAllData();
    }

    @SneakyThrows
    public void loadAllData() {
        try (Connection connection = getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `playerData`;")) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        playerInformation.add(new PlayerInformation(resultSet.getString("playerName"), UUID.fromString(resultSet.getString("uuid"))));
                    }
                }
            }
        }
    }

    /**
     * Load the player
     *
     * @param player The player, must be online
     */
    @SneakyThrows
    public void loadPlayer(Player player) {
        if (!player.isOnline()) return;
        if (getPlayerInformation(player.getUniqueId()) != null) return;
        executeUpdate("INSERT INTO `playerData` (playerName,uuid) VALUES ('" + player.getName() + "','" + player.getUniqueId().toString() + "');");
        playerInformation.add(new PlayerInformation(player));
    }

    /**
     * Get the player's information by name
     *
     * @param name The player name
     * @return the PlayerInformation if there's any, null otherwise
     */
    public PlayerInformation getPlayerInformation(String name) {
        if (!memory.isCachePlayerInformation()) throw new UnsupportedOperationException("Player information cache is disabled at this server!");
        return playerInformation.stream().filter(playerInformation -> playerInformation.getPlayerName().equals(name)).findAny().orElse(null);
    }

    /**
     * Get the player's information by uuid
     *
     * @param uuid The player uuid
     * @return the PlayerInformation if there's any, null otherwise
     */
    public PlayerInformation getPlayerInformation(UUID uuid) {
        if (!memory.isCachePlayerInformation()) throw new UnsupportedOperationException("Player information cache is disabled at this server!");
        return playerInformation.stream().filter(playerInformation -> playerInformation.getPlayerUuid().equals(uuid)).findAny().orElse(null);
    }

    @Override
    public void onDisable() { }
}
