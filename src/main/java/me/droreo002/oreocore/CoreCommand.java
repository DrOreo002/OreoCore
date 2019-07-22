package me.droreo002.oreocore;

import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.linked.LinkedInventory;
import me.droreo002.oreocore.inventory.api.linked.LinkedInventoryHandler;
import me.droreo002.oreocore.inventory.debug.InventoryAnimationDebug;
import me.droreo002.oreocore.inventory.debug.PInventoryAnimationDebug;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

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
                if (args[0].equalsIgnoreCase("test-config-comment")) {
                    sendMessage(player, "Testing. Please check file after this!");
                    plugin.getPluginConfig().reloadConfig();
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
                if (args[0].equalsIgnoreCase("test-animated-inventory")) {
                    sendMessage(player, "Please select the type (CustomInventory, PaginatedInventory)");
                    sound(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-linked-inventory")) {
                    final LinkedInventoryHandler linkedInventoryHandler = new LinkedInventoryHandler();

                    // First inventory
                    linkedInventoryHandler.addInventory(null, GUIButton.DEFAULT_NEXT_BUTTON, new LinkedInventory(27, "First") {
                        @Override
                        public Map<String, Object> requestData() {
                            HashMap<String, Object> o = new HashMap<>();
                            o.put("Hello", "Data from other inventory!");
                            return o;
                        }
                    });

                    linkedInventoryHandler.addInventory(GUIButton.DEFAULT_BACK_BUTTON, null, new LinkedInventory(45, "Second") {
                        @Override
                        public void onOpen(Player player, Map<String, Object> linkedData) {
                            System.out.println("Opened last inventory. Prev data are " + linkedData.get("Hello"));
                        }
                    });

                    linkedInventoryHandler.open(player);
                    // Second inventory
                    return true;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("test-animated-inventory")) {
                    String type = args[1];

                    switch (type.toLowerCase()) {
                        case "custominventory":
                            sendMessage(player, "Testing animated-inventory on server version " + ServerUtils.getServerVersion());
                            sound(player);
                            new InventoryAnimationDebug().open(player);
                            return true;
                        case "paginatedinventory":
                            sendMessage(player, "Testing animated-inventory on server version " + ServerUtils.getServerVersion());
                            sound(player);
                            new PInventoryAnimationDebug().open(player);
                            return true;
                    }
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
