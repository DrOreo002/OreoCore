package me.droreo002.oreocore.commands;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.logging.Debug;
import me.droreo002.oreocore.utils.strings.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomCommand {

    @Getter
    private JavaPlugin owner;
    @Getter
    @Setter
    private SoundObject successSound, errorSound;
    @Getter
    @Setter
    private String argumentNotFoundMessage;
    @Getter
    private String commandBase;
    @Getter
    private String[] aliases;
    @Getter
    private List<CommandArg> args;

    public CustomCommand(JavaPlugin owner, String commandBase, String... aliases) {
        this.owner = owner;
        this.commandBase = commandBase;
        this.args = new ArrayList<>();
        this.aliases = aliases;
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

    public void addArgument(CommandArg arg) {
        if (isArgumentRegistered(arg.getTrigger())) return;
        args.add(arg);
    }

    public void success(Player player) {
        if (successSound == null) return;
        successSound.send(player);
    }

    public void error(Player player) {
        if (errorSound == null) return;
        errorSound.send(player);
    }

    public boolean isArgumentRegistered(String arg) {
        for (CommandArg ar : args) {
            if (ar.getTrigger().equalsIgnoreCase(arg)) return true;
        }
        return false;
    }

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
        sender.sendMessage(StringUtil.color(message));
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
