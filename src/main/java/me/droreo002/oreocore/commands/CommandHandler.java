package me.droreo002.oreocore.commands;

import me.droreo002.oreocore.enums.MinecraftVersion;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

public class CommandHandler implements CommandExecutor, TabCompleter {

    private final CustomCommand cmd;

    CommandHandler(CustomCommand cmd) {
        this.cmd = cmd;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command vanillaCommand, String s, String[] args) {
        if (args.length > 0 && !cmd.getArgs().isEmpty()) {
            CommandArg argument = cmd.getArgument(args[0]);
            if (argument != null) {
                if (commandSender instanceof Player) {
                    if (argument.isConsoleOnly()) {
                        cmd.sendMessage(commandSender, argument.getConsoleOnlyMessage());
                        argument.error(commandSender);
                        return true;
                    }
                    if (argument.getPermission() != null) {
                        Player player = (Player) commandSender;
                        if (!player.hasPermission(argument.getPermission())) {
                            cmd.sendMessage(commandSender, argument.getNoPermissionMessage());
                            argument.error(player);
                            return true;
                        }
                    }
                } else {
                    if (argument.isPlayerOnly()) {
                        cmd.sendMessage(commandSender, argument.getPlayerOnlyMessage());
                        argument.error(commandSender);
                        return true;
                    }
                }
                argument.execute(commandSender, args);
                return true;
            } else {
                if (cmd.getArgumentNotFoundMessage() != null) {
                    cmd.sendMessage(commandSender, cmd.getArgumentNotFoundMessage());
                    if (commandSender instanceof Player) cmd.errorSound(commandSender);
                }
                return true;
            }
        } else {
            if (commandSender instanceof Player) {
                if (cmd.isConsoleOnly()) {
                    cmd.sendMessage(commandSender, cmd.getConsoleOnlyMessage());
                    cmd.errorSound(commandSender);
                    return true;
                }
                if (cmd.getPermission() != null) {
                    Player player = (Player) commandSender;
                    if (!player.hasPermission(cmd.getPermission())) {
                        cmd.sendMessage(commandSender, cmd.getNoPermissionMessage());
                        cmd.errorSound(player);
                        return true;
                    }
                }
            } else {
                if (cmd.isPlayerOnly()) {
                    cmd.sendMessage(commandSender, cmd.getPlayerOnlyMessage());
                    cmd.errorSound(commandSender);
                    return true;
                }
            }
            cmd.execute(commandSender, args);
            return true;
        }
    }

    /**
     * The listener for the TabCompleter. This code are taken from Slimefun 4
     *
     * @param sender : Command sender
     * @param command : The command object that is executed
     * @param alias : The alias of the command
     * @param args : The current argument that the command has
     *
     * @return List of string that will be used for the tab completer
     */
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!cmd.getTabCompletePermission().equals("")) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                if (!player.hasPermission(cmd.getTabCompletePermission())) {
                    // Fix spam message
                    switch (ServerUtils.getServerVersion()) {
                        case v1_13_R1:
                        case v1_13_R2:
                        case v1_14_R1:
                            return null;
                    }
                    cmd.sendMessage(sender, cmd.getTabCompleteNoPermissionMessage());
                    cmd.errorSound(sender);
                    return null;
                }
            }
        }
        return cmd.onTabComplete(sender, command, alias, args);
    }
}
