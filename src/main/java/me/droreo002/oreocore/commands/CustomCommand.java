package me.droreo002.oreocore.commands;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.debugging.ODebug;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public abstract class CustomCommand {

    @Getter
    private final JavaPlugin owner;
    @Getter @Setter
    private SoundObject successSound, errorSound;
    @Getter @Setter
    private String argumentNotFoundMessage, commandBase;
    @Getter
    private String tabCompletePermission, tabCompleteNoPermissionMessage, permission, noPermissionMessage, consoleOnlyMessage, playerOnlyMessage;;
    @Getter
    private boolean consoleOnly, playerOnly;
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
     * Add a argument into the command
     *
     * @param arg : The argument class
     */
    public void addArgument(CommandArg arg) {
        if (getArgument(arg.getTrigger()) != null) return;
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
     * Set the required permission for this argument
     *
     * @param permission : The permission
     * @param noPermissionMessage : The message that will throw if player doesn't have that permission
     */
    public void setPermission(String permission, String noPermissionMessage) {
        this.permission = permission;
        this.noPermissionMessage = noPermissionMessage;
    }

    /**
     * Set if this argument is player only or not
     *
     * @param playerOnly : Player only?
     * @param playerOnlyMessage : The message that will throw if non player tried to execute a player only command
     */
    public void setPlayerOnly(boolean playerOnly, String playerOnlyMessage) {
        this.playerOnly = playerOnly;
        this.playerOnlyMessage = playerOnlyMessage;
    }

    /**
     * Set if this argument is console only or not
     *
     * @param consoleOnly : Console only?
     * @param consoleOnlyMessage : The message that will throw if non console tried to execute a console only command
     */
    public void setConsoleOnly(boolean consoleOnly, String consoleOnlyMessage) {
        this.consoleOnly = consoleOnly;
        this.consoleOnlyMessage = consoleOnlyMessage;
    }

    /**
     * Get the command argument by name
     *
     * @param arg : The command argument name or trigger
     * @return the CommandArg class if found, null otherwise
     */
    public CommandArg getArgument(String arg) {
        return args.stream().filter(cmd -> cmd.getTrigger().equalsIgnoreCase(arg)).findAny().orElse(null);
    }

    /**
     * Send message to player with auto coloring
     *
     * @param sender : The target
     * @param message : The message
     */
    public void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(StringUtils.color(message));
        } else {
            ODebug.log(message); // So color code will work
        }
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
