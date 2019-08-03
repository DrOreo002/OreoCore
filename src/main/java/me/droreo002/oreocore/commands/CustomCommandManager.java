package me.droreo002.oreocore.commands;

import me.droreo002.oreocore.debugging.ODebug;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class CustomCommandManager {

    private static final Map<JavaPlugin, List<CustomCommand>> COMMANDS = new HashMap<>();

    /**
     * Register a new custom command
     *
     * @param plugin The command owner / source
     * @param command The command class
     */
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
            ODebug.log("&4&lWarning &8> &fCannot register this command properly because it was not inside the PluginCommand cache &7(&e" + command.getCommandBase() + "&7)", true);
            return;
        }
        if (!pluginCommand.getAliases().containsAll(Arrays.asList(command.getAliases()))) ODebug.log("&4&lWarning &8> &fCommand with the base of &e" + command.getCommandBase() + "&f has aliases on it but some of the aliases is not inside the &eplugin.yml! &fplease add!", true);

        pluginCommand.setExecutor(new CommandHandler(command));
        pluginCommand.setTabCompleter(new CommandHandler(command));
        ODebug.log("Base command with the name of &e" + command.getCommandBase() + "&f from plugin &e" + plugin.getName() + "&f. Has been registered successfully!", true);
    }

    /**
     * Unregister a custom command
     *
     * @param command The command to unregister
     */
    public static void unregisterCommand(CustomCommand command) {
        final JavaPlugin plugin = command.getOwner();
        Validate.notNull(plugin, "Plugin cannot be null!");
        Validate.notNull(command, "Command cannot be null!");
        if (!isPluginRegistered(plugin)) throw new NullPointerException("Cannot register command because the plugin " + plugin.getName() + " doesn't have any command registered!");
        COMMANDS.get(plugin).removeIf(customCommand -> customCommand.getCommandBase().equals(command.getCommandBase()));
    }

    /**
     * Check if the plugin is registered on the cache
     *
     * @param plugin The plugin to check
     * @return true if registered, false otherwise
     */
    public static boolean isPluginRegistered(JavaPlugin plugin) {
        Validate.notNull(plugin, "Plugin cannot be null!");
        return COMMANDS.containsKey(plugin);
    }

    /**
     * Check if the command is registered
     *
     * @param command The command to check
     * @return true if registered, false otherwise
     */
    public static boolean isCommandRegistered( CustomCommand command) {
        final JavaPlugin plugin = command.getOwner();
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
