package me.droreo002.oreocore.debugging;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

// Private class, copy and paste this instead
public final class ODebug {

    public static void log(String text, boolean addPrefix) {
        if (addPrefix) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[ &aOreoCore &7]&f " + text));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', text));
        }
    }

    public static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}
