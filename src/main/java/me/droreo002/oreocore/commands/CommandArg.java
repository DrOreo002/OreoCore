package me.droreo002.oreocore.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandArg {

    @Getter
    private String trigger;
    @Getter
    private CustomCommand parent;
    @Getter
    @Setter
    private boolean hasPermission;
    @Getter
    @Setter
    private String permission;
    @Getter
    @Setter
    private String noPermissionMessage;
    @Getter
    @Setter
    private boolean consoleOnly;
    @Getter
    @Setter
    private String consoleOnlyMessage;

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
}
