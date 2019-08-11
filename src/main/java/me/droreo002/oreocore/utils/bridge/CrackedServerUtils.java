package me.droreo002.oreocore.utils.bridge;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.utils.PlayerInformationDatabase;

import java.util.UUID;

/**
 * Useful utilities for a cracked server a.k.a offline mode
 */
public final class CrackedServerUtils {

    private static final PlayerInformationDatabase database = OreoCore.getInstance().getPlayerInformationDatabase();

    /**
     * Get the player uuid via name
     *
     * @param name The player name
     * @return the UUID if exists, null otherwise
     */
    public static UUID getPlayerUuid(String name) {
        if (database == null) return null;
        return database.getPlayerInformation(name).getPlayerUuid();
    }

    /**
     * Get the player name via uuid
     *
     * @param uuid The player uuid
     * @return the player name if exists, null otherwise
     */
    public static String getPlayerName(UUID uuid) {
        if (database == null) return null;
        return database.getPlayerInformation(uuid).getPlayerName();
    }
}
