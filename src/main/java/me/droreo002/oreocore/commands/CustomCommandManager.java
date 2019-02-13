package me.droreo002.oreocore.commands;

import lombok.Getter;
import me.droreo002.oreocore.utils.logging.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class CustomCommandManager {

    private static final Map<JavaPlugin, List<CustomCommand>> COMMANDS = new HashMap<>();

    public static void registerCommand(JavaPlugin plugin, CustomCommand command) {
        Validate.notNull(plugin, "Plugin cannot be null!");
        Validate.notNull(command, "Command cannot be null!");
        if (isPluginRegistered(plugin)) {
            List<CustomCommand> list = COMMANDS.get(plugin);
            list.add(command);
            COMMANDS.put(plugin, list);
        } else {
            COMMANDS.put(plugin, new ArrayList<>(Collections.singletonList(command)));
        }
        PluginCommand pluginCommand = Bukkit.getPluginCommand(command.getCommandBase());
        if (pluginCommand == null) {
            Debug.log("&cWarning &f: Cannot register this command properly because it was not inside the PluginCommand cache (" + command.getCommandBase() + ")", true);
            return;
        }
        pluginCommand.setExecutor(new CommandHandler());
        pluginCommand.setTabCompleter(new CommandHandler());
        Debug.log("Base command with the name of &e" + command.getCommandBase() + "&f from plugin &e" + plugin.getName() + "&f. Has been registered successfully!", true);
    }

    public static void unregisterCommand(JavaPlugin plugin, CustomCommand command) {
        Validate.notNull(plugin, "Plugin cannot be null!");
        Validate.notNull(command, "Command cannot be null!");
        if (!isPluginRegistered(plugin)) throw new NullPointerException("Cannot register command because the plugin " + plugin.getName() + " doesn't have any command registered!");
    }

    public static boolean isPluginRegistered(JavaPlugin plugin) {
        Validate.notNull(plugin, "Plugin cannot be null!");
        return COMMANDS.containsKey(plugin);
    }

    public static boolean isCommandRegistered(JavaPlugin plugin, CustomCommand command) {
        Validate.notNull(plugin, "Plugin cannot be null!");
        Validate.notNull(command, "Command cannot be null!");
        if (!isPluginRegistered(plugin)) return false;
        for (CustomCommand cmd : COMMANDS.get(plugin)) {
            if (cmd.getCommandBase().equalsIgnoreCase(command.getCommandBase())) return true;
        }
        return false;
    }

    public static Map<JavaPlugin, List<CustomCommand>> getCommands() {
        return COMMANDS;
    }
}
