package me.droreo002.oreocore;

import me.droreo002.oreocore.inventory.linked.LinkedInventoryManager;
import me.droreo002.oreocore.inventory.test.animation.CInventoryAnimationTest;
import me.droreo002.oreocore.inventory.test.animation.PInventoryAnimationTest;
import me.droreo002.oreocore.inventory.test.normal.InventoryTemplateTest;
import me.droreo002.oreocore.inventory.test.normal.CustomInventoryTest;
import me.droreo002.oreocore.inventory.test.normal.FirstLinkedInventory;
import me.droreo002.oreocore.inventory.test.normal.LagInventoryTest;
import me.droreo002.oreocore.inventory.test.normal.PaginatedInventoryTest;
import me.droreo002.oreocore.inventory.test.normal.SecondLinkedInventory;
import me.droreo002.oreocore.utils.bridge.OSound;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CoreCommand implements CommandExecutor, TabCompleter {

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
                if (args[0].equalsIgnoreCase("plugins")) {
                   sendMessage(player, "Currently handling in total of &7(&c" + plugin.getHookedPlugin().size() + "&7) &fplugins");
                   sound(player);
                   return true;
                }
                if (args[0].equalsIgnoreCase("config-memory")) {
                    sendMessage(player,"ODebug value : " + plugin.getPluginConfig().getMemory().getWorking());
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
                    sendMessage(player, "ODebug Value : " + plugin.getPluginConfig().getMemory().getBody().toString());
                    sound(player);
                    return true;
                }
                if (args[0].equals("test-config-update")) {
                    sendMessage(player, "Testing...");
                    plugin.getPluginConfig().getMemory().getTitleObject().setTitle("Hello World!");
                    plugin.getPluginConfig().saveConfig(true);
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
                    if (item == null || !item.getType().equals(UMaterial.PLAYER_HEAD.getItemStack())) {
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
                    final LinkedInventoryManager manager = new LinkedInventoryManager(
                            new FirstLinkedInventory(),
                            new SecondLinkedInventory()
                    );
                    manager.openInventory(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-template-inventory")) {
                    new InventoryTemplateTest(plugin.getPluginConfig().getMemory().getTestTemplate()).open(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-lag-inventory")) {
                    new LagInventoryTest().open(player);
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
                            new CInventoryAnimationTest().open(player);
                            return true;
                        case "paginatedinventory":
                            sendMessage(player, "Testing animated-inventory on server version " + ServerUtils.getServerVersion());
                            sound(player);
                            new PInventoryAnimationTest().open(player);
                            return true;
                    }
                    return true;
                }
            }
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("test-inventory")) {
                    String type = args[1];

                    switch (type.toLowerCase()) {
                        case "custominventory":
                            sendMessage(player, "Testing CustomInventory on server version " + ServerUtils.getServerVersion());
                            sound(player);
                            new CustomInventoryTest().open(player);
                            return true;
                        case "paginatedinventory":
                            sendMessage(player, "Testing PaginatedInventory on server version " + ServerUtils.getServerVersion());
                            sound(player);
                            new PaginatedInventoryTest().open(player);
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
        new SoundObject(OSound.UI_BUTTON_CLICK).send(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

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
