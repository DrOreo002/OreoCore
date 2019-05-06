package me.droreo002.oreocore.utils.bridge;

import me.droreo002.oreocore.enums.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils {

    /**
     * Get the server version as enum
     *
     * @return : The server version as enum
     */
    public static MinecraftVersion getServerVersion() {
        for (MinecraftVersion version : MinecraftVersion.values()) {
            String currentVersion = getVersion();
            if (currentVersion == null) continue;
            if (currentVersion.equals(version.toString())) {
                return version;
            }
        }
        return MinecraftVersion.FAILED_TO_GET;
    }

    public static boolean isPluginInstalled(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    public static void disablePlugin(JavaPlugin plugin) {
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    public static JavaPlugin getPlugin(String name) {
        return (JavaPlugin) Bukkit.getPluginManager().getPlugin(name);
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    public static void registerListener(JavaPlugin plugin, Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Get the minecraft server version as String
     *
     * Deprecated, please use getServerVersion instead
     * @return : The string valur
     */
    @Deprecated
    private static String getVersion() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
        return version;
    }
}
