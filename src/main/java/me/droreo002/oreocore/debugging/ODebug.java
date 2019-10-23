package me.droreo002.oreocore.debugging;

import me.droreo002.oreocore.DependedPluginProperties;
import me.droreo002.oreocore.OreoCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ODebug {

    public static void log(JavaPlugin source, String text, boolean addPrefix) {
        if (source != null && !source.getName().equals(OreoCore.getInstance().getName())) {
            final Map<JavaPlugin, DependedPluginProperties> hook = OreoCore.getInstance().getHookedPlugin();
            if (hook.containsKey(source)) if (!hook.get(source).isEnableLogging()) return;
        }
        if (addPrefix) {
            log("&7[ &aOreoCore &7]&f " + text);
        } else {
            log(text);
        }
    }

    private static void log(String text) {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }
}
