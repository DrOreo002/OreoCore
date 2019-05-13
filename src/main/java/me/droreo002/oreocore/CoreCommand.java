package me.droreo002.oreocore;

import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.multisupport.BukkitReflectionUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                if (args[0].equalsIgnoreCase("plugin-list")) {
                   sendMessage(player, "&fThis server is currently running on &bOreoCore &fversion &b" + plugin.getDescription().getVersion() + "&f. This plugin is also currently handling &7(&c" + plugin.getHookedPlugin().size() + "&7)");
                   sound(player);
                   return true;
                }
                if (args[0].equalsIgnoreCase("config-memory")) {
                    sendMessage(player,"Debug value : " + plugin.getPluginConfig().getMemory().getWorking());
                    sound(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("title-memory")) {
                    sendMessage(player, "This is the title!");
                    sound(player);
                    plugin.getPluginConfig().getMemory().getTitleObject().send(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("enum-memory")) {
                    sendMessage(player, "Debug Value : " + plugin.getPluginConfig().getMemory().getBody().toString());
                    sound(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-get-head")) {
                    sendMessage(player, "Testing get-head on server version " + ServerUtils.getServerVersion());
                    sound(player);
                    ItemStack item = CustomSkull.getSkullUrl("http://textures.minecraft.net/texture/3ab0263bdd76f3e418dba5bf481b921ced397d8b8a34a5561fb7beaa46ece1");
                    player.getInventory().addItem(item);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-get-texture")) {
                    sendMessage(player, "Testing get-texture on server version " + ServerUtils.getServerVersion());
                    sound(player);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item == null || !item.getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                        sound(player);
                        sendMessage(commandSender, "Please put the head on your main hand");
                        return true;
                    }
                    sound(player);
                    sendMessage(player, "Texture are " + CustomSkull.getTexture(item));
                    return true;
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
        sender.sendMessage(plugin.getPrefix() + StringUtils.color(text));
    }

    private void sound(Player player) {
        new SoundObject(Sounds.CLICK).send(player);
    }
}
