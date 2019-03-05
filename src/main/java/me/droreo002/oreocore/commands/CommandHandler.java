package me.droreo002.oreocore.commands;

import me.droreo002.oreocore.utils.strings.StringUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CommandHandler implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Map<JavaPlugin, List<CustomCommand>> commandsMap = CustomCommandManager.getCommands();
        for (List<CustomCommand> cmds : commandsMap.values()) {
            for (CustomCommand cmd : cmds) {
                if (cmd.isCommand(command.getName())) {
                    if (args.length > 0) {
                        CommandArg argument = cmd.getArgument(args[0]);
                        if (argument != null) {
                            if (commandSender instanceof Player) {
                                if (argument.isConsoleOnly()) {
                                    argument.error(commandSender);
                                    commandSender.sendMessage(StringUtil.color(argument.getConsoleOnlyMessage()));
                                    return true;
                                }
                                if (argument.getPermission() != null) {
                                    Player player = (Player) commandSender;
                                    if (!player.hasPermission(argument.getPermission())) {
                                        player.sendMessage(StringUtil.color(argument.getNoPermissionMessage()));
                                        argument.error(player);
                                        return true;
                                    }
                                }
                            } else {
                                if (argument.isPlayerOnly()) {
                                    argument.error(commandSender);
                                    commandSender.sendMessage(StringUtil.color(argument.getPlayerOnlyMessage()));
                                    return true;
                                }
                            }
                            argument.execute(commandSender, args);
                            return true;
                        } else {
                            if (cmd.getArgumentNotFoundMessage() != null) {
                                commandSender.sendMessage(cmd.getArgumentNotFoundMessage());
                                if (commandSender instanceof Player) cmd.errorSound(commandSender);
                            }
                            return true;
                        }
                    } else {
                        cmd.execute(commandSender, args);
                        return true;
                    }
                }
            }
        }
        return false;
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
        Map<JavaPlugin, List<CustomCommand>> commandsMap = CustomCommandManager.getCommands();
        for (List<CustomCommand> cmds : commandsMap.values()) {
            for (CustomCommand cmd : cmds) {
                if (cmd.isCommand(command.getName())) {
                    // Is the correct command
                    return cmd.onTabComplete(sender, command, alias, args);
                }
            }
        }
        return null;
    }
}
