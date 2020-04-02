package me.droreo002.oreocore.database.utils;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.dummy.PluginConfig;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.object.DatabaseSQL;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PlayerInformationDatabase extends DatabaseSQL {

    @Getter
    private final Set<PlayerInformation> playerInformation = new HashSet<>();
    @Getter
    private PluginConfig memory;

    public PlayerInformationDatabase(OreoCore plugin) {
        super(plugin, "playerdata", plugin.getDataFolder(), SQLType.SQL_BASED);
        this.memory = plugin.getPluginConfig();

        loadAllData();
        DatabaseManager.registerDatabase(plugin, this);
    }

    /**
     * Load the player
     *
     * @param player The player, must be online
     */
    public void loadPlayer(Player player) {
        if (!player.isOnline()) return;
        if (getPlayerInformation(player.getUniqueId()) != null) return;
        executeAsync("INSERT INTO `playerData` (playerName,uuid) VALUES ('" + player.getName() + "','" + player.getUniqueId().toString() + "');");
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
    public void loadAllData() {
        final List<Object> query = queryRow("SELECT `playerName` FROM `playerData`;", "playerName");
        final List<String> primaryKey = query.stream().map(o -> (String) o).collect(Collectors.toList());

        for (String playerName : primaryKey) {
            final UUID uuid = UUID.fromString((String) queryValue("SELECT `uuid` FROM `playerData` WHERE `playerName` IS '" + playerName + "';", "uuid"));
            playerInformation.add(new PlayerInformation(playerName, uuid));
        }
    }

    @Override
    public SqlDatabaseTable getSqlDatabaseTable() {
        return new SqlDatabaseTable("playerData")
                .addKey(new SqlDataKey("playerName", true, SqlDataKey.KeyType.MINECRAFT_USERNAME, false, null))
                .addKey(new SqlDataKey("uuid", false, SqlDataKey.KeyType.UUID, false, null));
    }
}
