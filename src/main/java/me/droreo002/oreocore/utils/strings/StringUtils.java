package me.droreo002.oreocore.utils.strings;

import org.bukkit.ChatColor;

import java.util.UUID;

public final class StringUtils {

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isUUID(String toCheck) {
        try {
            UUID lel = UUID.fromString(toCheck); // Declare to remove that stupid yellow warning (IntelliJ)
        } catch (IllegalStateException e) {
            return false;
        }
        return true;
    }
}
