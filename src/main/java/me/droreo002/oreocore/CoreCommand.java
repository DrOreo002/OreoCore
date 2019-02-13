package me.droreo002.oreocore;

import me.droreo002.oreocore.inventory.dummy.CustomInventoryDummy;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtil;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoreCommand implements CommandExecutor {

    private OreoCore plugin;

    public CoreCommand(OreoCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            sendMessage(commandSender, "A CorePlugin specified for plugins from author &c@DrOreo002");
            return true;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("oreocore.admin")) {
            sendMessage(player, "No permission!");
            sound(player);
            return true;
        }
        if (args.length > 0) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("inventory")) {
                    new CustomInventoryDummy().open(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("plugin-list")) {
                   sendMessage(player, "&fThis server is currently running on &bOreoCore &fversion &b" + plugin.getDescription().getVersion() + "&f. This plugin is also currently handling &7(&c" + plugin.getHookedPlugin().size() + "&7)");
                }
            }
        } else {
            sendMessage(commandSender, "A CorePlugin specified for plugins from author &c@DrOreo002");
            sound(player);
            return true;
        }
        return false;
    }

    private void sendMessage(CommandSender sender, String text) {
        sender.sendMessage(plugin.getPrefix() + StringUtil.color(text));
    }

    private void sound(Player player) {
        new SoundObject(Sound.UI_BUTTON_CLICK).send(player);
    }
}
