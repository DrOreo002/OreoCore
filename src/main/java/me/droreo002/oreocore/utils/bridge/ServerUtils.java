package me.droreo002.oreocore.utils.bridge;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.commands.CustomCommand;
import me.droreo002.oreocore.enums.MinecraftVersion;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

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

    /**
     * Check if the plugin is installed or not
     *
     * @param plugin The plugin name to check
     * @return true if installed, false otherwise
     */
    public static boolean isPluginInstalled(String plugin) {
        return Bukkit.getPluginManager().getPlugin(plugin) != null;
    }

    /**
     * Disable the plugin
     *
     * @param plugin Plugin to disable
     */
    public static void disablePlugin(JavaPlugin plugin) {
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    /**
     * Get plugin by name
     *
     * @param name The plugin name to get
     * @return the plugin class if found, null otherwise
     */
    public static JavaPlugin getPlugin(String name) {
        return (JavaPlugin) Bukkit.getPluginManager().getPlugin(name);
    }

    /**
     * Call the event
     *
     * @param event The event to call
     */
    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Register the listener
     *
     * @param plugin The owner of the listener
     * @param listener The listener to register
     */
    public static void registerListener(JavaPlugin plugin, Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Force unregister the command from bukkit's command map
     *
     * @param commands The commands to unregister
     * @param fromPlugin The plugin source
     * @param callback Callback, will get called if execution is complete
     */
    @SuppressWarnings("deprecation")
    public static void forceUnregisterCommand(String fromPlugin, SimpleCallback<Void> callback, String... commands) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (ServerUtils.getPlugin(fromPlugin) != null) {
                    try {
                        for (String command : commands) {
                            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

                            bukkitCommandMap.setAccessible(true);
                            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

                            commandMap.getCommand(command).unregister(commandMap);
                        }
                        cancel();
                        callback.success(null);
                    } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
                        callback.error(e);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(OreoCore.getInstance(), 20L, 20L * 20L);
    }

    /**
     * Get the minecraft server version as String
     *
     * @return : The string value
     */
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
