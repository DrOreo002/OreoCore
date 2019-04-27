package me.droreo002.oreocore.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    /**
     * Check if string is an Integer
     *
     * @param sender : The command sender
     * @param toCheck : The message to check
     * @param notIntegerMessage : The message to send if its not an integer
     * @return true if its an integer, false otherwise
     */
    public boolean isInteger(CommandSender sender, String toCheck, String notIntegerMessage) {
        try {
            Integer.valueOf(toCheck);
        } catch (NumberFormatException e) {
            sendMessage(sender, notIntegerMessage);
            error(sender);
            return false;
        }
        return true;
    }

    /**
     * Check if the item is valid
     *
     * @param player : The player
     * @param expected : The expected item
     * @param notValidMessage : Message to send if the item is not valid
     * @return true if valid, false otherwise
     */
    public boolean isHandItemValid(Player player, ItemStack expected, String notValidMessage) {
        boolean b = player.getInventory().getItemInMainHand().isSimilar(expected);
        if (!b) {
            sendMessage(player, notValidMessage);
            error(player);
            return false;
        }
        return b;
    }

    /**
     * Check if the item is null or not
     *
     * @param player : The player
     * @param nullMessage : Null message
     * @return true or false
     */
    public boolean isHandItemNotNull(Player player, String nullMessage) {
        final ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().equals(Material.AIR)) {
            sendMessage(player, nullMessage);
            error(player);
            return false;
        }
        return true;
    }
}
