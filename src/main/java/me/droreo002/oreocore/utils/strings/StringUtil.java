package me.droreo002.oreocore.utils.strings;

import org.bukkit.ChatColor;

public final class StringUtil {

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
