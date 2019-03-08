package me.droreo002.oreocore.utils.bridge;

import me.droreo002.oreocore.enums.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils {

    public static MinecraftVersion getServerVersion() {
        for (MinecraftVersion version : MinecraftVersion.values()) {
            String currentVersion = getVersion();
            if (currentVersion == null) continue;
            if (currentVersion.equals(version.toString())) {
                return version;
            }
        }
        return null;
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
