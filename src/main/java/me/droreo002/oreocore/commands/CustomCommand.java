package me.droreo002.oreocore.commands;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.debugging.Debug;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class CustomCommand {

    @Getter
    private JavaPlugin owner;
    @Getter @Setter
    private SoundObject successSound, errorSound;
    @Getter @Setter
    private String argumentNotFoundMessage, commandBase;
    @Getter
    private String tabCompletePermission, tabCompleteNoPermissionMessage;
    @Getter
    private String[] aliases;
    @Getter
    private List<CommandArg> args;

    public CustomCommand(JavaPlugin owner, String commandBase, String... aliases) {
        this.owner = owner;
        this.commandBase = commandBase;
        this.args = new ArrayList<>();
        this.aliases = aliases;
        this.tabCompletePermission = "";
        this.tabCompleteNoPermissionMessage = "";
    }

    /**
     * Check if the given argument is equals to aliases or base command
     *
     * @param argument : Given argument
     * @return true if correct, false otherwise
     */
    public boolean isCommand(String argument) {
        for (String s : aliases) {
            if (s.equalsIgnoreCase(argument)) return true;
        }
        // Or
        return commandBase.equalsIgnoreCase(argument);
    }

    /**
     * Add a argument into the command
     *
     * @param arg : The argument class
     */
    public void addArgument(CommandArg arg) {
        if (isArgumentRegistered(arg.getTrigger())) return;
        args.add(arg);
    }

    /**
     * Play the success sound to the CommandSender
     *
     * @param sender : The target sound
     */
    public void successSound(CommandSender sender) {
        Validate.notNull(successSound, "Success sound is null!");
        if (!(sender instanceof Player)) return;
        successSound.send((Player) sender);
    }

    /**
     * Play the error sound to the CommandSender
     *
     * @param sender : The target sound
     */
    public void errorSound(CommandSender sender) {
        Validate.notNull(errorSound, "Error sound is null!");
        if (!(sender instanceof Player)) return;
        errorSound.send((Player) sender);
    }

    /**
     * Set the tab complete permission
     *
     * @param tabCompletePermission : The tab complete permission
     * @param tabCompleteNoPermissionMessage : The message to fire if player don't have the permission
     */
    public void setTabCompletePermission(String tabCompletePermission, String tabCompleteNoPermissionMessage) {
        this.tabCompletePermission = tabCompletePermission;
        this.tabCompleteNoPermissionMessage = tabCompleteNoPermissionMessage;
    }

    /**
     * Check if the argument is registered or not
     *
     * @param arg : The argument class
     * @return true if registered, false otherwise
     */
    public boolean isArgumentRegistered(String arg) {
        for (CommandArg ar : args) {
            if (ar.getTrigger().equalsIgnoreCase(arg)) return true;
        }
        return false;
    }

    /**
     * Get the command argument by name
     *
     * @param arg : The command argument name or trigger
     * @return the CommandArg class if found, null otherwise
     */
    public CommandArg getArgument(String arg) {
        if (!isArgumentRegistered(arg)) return null;
        for (CommandArg ar : args) {
            if (ar.getTrigger().equalsIgnoreCase(arg)) return ar;
        }
        return null;
    }

    /**
     * Send message to player with auto coloring
     *
     * @param sender : The target
     * @param message : The message
     */
    public void sendMessage(CommandSender sender, String message) {
        if (sender.equals(Bukkit.getConsoleSender())) {
            Debug.log(message);
            return;
        }
        sender.sendMessage(StringUtils.color(message));
    }

    /**
     * Executed when /command is called
     *
     * @param sender : The command sender, could be player or console. Always check if first
     * @param args   : The args. Starting from 0, to modify the call from the first command. Use executeAbout
     */
    public abstract void execute(CommandSender sender, String[] args);

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
    public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

    /**
     * Create a return list with the specified list. This code are taken from Slimefun 4
     *
     * @param list : The list that you want to add
     * @param string : The latest argument on the command
     *
     * @return a set of string where everything has been built inside
     */
    public List<String> createReturnList(List<String> list, String string) {
        if (string.equals("")) return list;

        List<String> returnList = new ArrayList<>();
        for (String item : list) {
            if (item.toLowerCase().startsWith(string.toLowerCase())) {
                returnList.add(item);
            }
        }
        return returnList;
    }
}
