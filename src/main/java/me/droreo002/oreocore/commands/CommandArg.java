package me.droreo002.oreocore.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandArg {

    @Getter
    private String trigger, permission, noPermissionMessage, consoleOnlyMessage, playerOnlyMessage;
    @Getter
    private CustomCommand parent;
    @Getter
    private boolean consoleOnly, playerOnly;

    public CommandArg(String trigger, CustomCommand parent) {
        this.trigger = trigger;
        this.parent = parent;
    }

    public void success(CommandSender sender) {
        parent.successSound(sender);
    }

    public void error(CommandSender sender) {
        parent.errorSound(sender);
    }

    /**
     * Execute the command
     *
     * @param sender : The command sender, could be player or console. Always check if first
     * @param args : The args. Starting from 0, to modify the call from the first command. Use executeAbout
     */
    public abstract void execute(CommandSender sender, String[] args);

    /**
     * Send message to player with auto coloring
     *
     * @param sender : The target
     * @param message : The message
     */
    public void sendMessage(CommandSender sender, String message) {
        parent.sendMessage(sender, message);
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
}
