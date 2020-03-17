package me.droreo002.oreocore;

import me.droreo002.oreocore.actionbar.ProgressActionBar;
import me.droreo002.oreocore.bossbar.ProgressBossBar;
import me.droreo002.oreocore.conversation.OreoConversation;
import me.droreo002.oreocore.conversation.OreoPrompt;
import me.droreo002.oreocore.enums.ParticleEffect;
import me.droreo002.oreocore.inventory.linked.LinkedInventoryBuilder;
import me.droreo002.oreocore.inventory.test.animation.CInventoryAnimationTest;
import me.droreo002.oreocore.inventory.test.animation.PInventoryAnimationTest;
import me.droreo002.oreocore.inventory.test.normal.InventoryTemplateTest;
import me.droreo002.oreocore.inventory.test.normal.CustomInventoryTest;
import me.droreo002.oreocore.inventory.test.normal.FirstLinkedInventory;
import me.droreo002.oreocore.inventory.test.normal.LagInventoryTest;
import me.droreo002.oreocore.inventory.test.normal.PaginatedInventoryTest;
import me.droreo002.oreocore.inventory.test.normal.SecondLinkedInventory;
import me.droreo002.oreocore.netty.NettyDebug;
import me.droreo002.oreocore.scoreboard.OreoScoreboard;
import me.droreo002.oreocore.utils.bridge.OSound;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.Base64ItemStack;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import me.droreo002.oreocore.utils.strings.TextBuilder;
import me.droreo002.oreocore.utils.time.TimestampBuilder;
import me.droreo002.oreocore.utils.time.TimestampUtils;
import me.droreo002.oreocore.utils.world.WorldUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
                    sendMessage(player,"ODebug value : " + plugin.getPluginConfig().getWorking());
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
                    plugin.getPluginConfig().getOreoTitle().send(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-timestamp")) {
                    TimestampBuilder builder = TimestampBuilder.builder(TimestampBuilder.SIMPLIFIED_FORMAT);
                    sendMessage(player, "Your time (+1 Hour): " + builder.addTime(1, TimestampBuilder.Clock.HOUR).buildAsString());
                    sendMessage(player, "Your time (-2 Hour): " + builder.decreaseTime(2, TimestampBuilder.Clock.HOUR).buildAsString());

                    TimestampBuilder fromSecond = TimestampUtils.fromSeconds(TimestampBuilder.DEFAULT_FORMAT, 300);
                    sendMessage(player, "Difference between (Now) with (+5 Minute from now): " +
                            TimestampUtils.getDifference(new Date(),
                                    new Date(fromSecond.getTimestamp().getTime()), TimestampBuilder.TICKING_TIME_FORMAT)
                    );
                    return true;
                }
                if (args[0].equalsIgnoreCase("enum-memory")) {
                    sendMessage(player, "ODebug Value : " + plugin.getPluginConfig().getBody().toString());
                    sound(player);
                    return true;
                }
                if (args[0].equals("test-config-update")) {
                    sendMessage(player, "Testing...");
                    plugin.getPluginConfig().getOreoTitle().setTitle("Hello World!");
                    plugin.getPluginConfig().saveConfig(true);
                    sound(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-get-head")) {
                    sendMessage(player, "Testing get-head on server version " + ServerUtils.getServerVersion());
                    sound(player);
                    ItemStack item = CustomSkull.fromUrl("badc048a7ce78f7dad72a07da27d85c0916881e5522eeed1e3daf217a38c1a");
                    player.getInventory().addItem(item);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-netty")) {
                    sendMessage(player, "Testing netty");
                    NettyDebug.startDebug();
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-item-condition")) {
                    sendMessage(player,"Testing item condition. Is op: " + player.isOp());
                    ItemStackBuilder builder = plugin.getPluginConfig().getItemStackBuilderTest().clone().applyBuilderCondition("is-op", player.isOp(), null);
                    player.getInventory().addItem(builder.getItemStack());
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-get-texture")) {
                    sendMessage(player, "Testing get-texture on server version " + ServerUtils.getServerVersion());
                    sound(player);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (!item.getType().equals(UMaterial.PLAYER_HEAD.getItemStack())) {
                        sound(player);
                        sendMessage(commandSender, "Please put the head on your main hand");
                        return true;
                    }
                    sound(player);
                    sendMessage(player, "Texture are " + CustomSkull.getTexture(item));
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-particle")) {
                    sendMessage(player, "Testing particle..");
                    WorldUtils.playParticles(ParticleEffect.WATER_SPLASH, 0, 0, 0, 3, 5, player.getLocation(), 5);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-conversation")) {
                    sendMessage(player, "Testing conversation...");
                    new OreoConversation<String>("not player", "exit", plugin)
                            .first(new OreoPrompt<String>("first", "FIRST_DATA") {
                                @Override
                                public String onInput(ConversationContext conversationContext, String s) {
                                    return s;
                                }

                                @NotNull
                                @Override
                                public String getPromptText(@NotNull ConversationContext conversationContext) {
                                    return "Type first data";
                                }
                            })
                            .lastly((s1, conversationContext) -> sendMessage(player, "Your data is " + s1))
                            .send(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-boss-bar")) {
                    ProgressBossBar bossBar = new ProgressBossBar("Loading.. (%currentProgress%/%maxProgress%)", BarColor.RED, BarStyle.SOLID, 0, 100, true);
                    bossBar.setHalfwayColor(BarColor.GREEN);
                    bossBar.setAutomated(true);
                    bossBar.setIncrementPerTick(1);
                    bossBar.setOnDone(progressBossBar -> {
                        progressBossBar.remove();
                        player.sendMessage("We're done!");
                    });

                    bossBar.start(player, 10L);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-action-bar")) {
                    ProgressActionBar actionBar = new ProgressActionBar(0, 500, ChatColor.GRAY, ChatColor.GREEN, '∎', '∎', "%loadingBar% &7(&a%percentage%&7)");
                    actionBar.addPlayer(player);
                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {
                            actionBar.addProgress(1D);
                        }
                    }.runTaskTimer(plugin, 0L, 10L);
                    actionBar.setOnDone(aVoid -> {
                        task.cancel();
                        player.sendMessage("Done!");
                    });
                    actionBar.send();
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-animated-inventory")) {
                    sendMessage(player, "Please select the type (CustomInventory, PaginatedInventory)");
                    sound(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-placeholder-inventory")) {
                    sendMessage(player, "Testing placeholder inventory...");
                    new InventoryTemplateTest(plugin.getPluginConfig().getTestTemplate().clone()).open(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-scoreboard")) {
                    OreoScoreboard scoreboard = new OreoScoreboard("Hello World");
                    scoreboard.add("        :", 0);
                    scoreboard.add("        :", 1);
                    scoreboard.add("        :", 2);
                    scoreboard.add("      &b➡ &a∎", 3);
                    scoreboard.add("        :", 4);
                    scoreboard.add("        :", 5);
                    scoreboard.add("        :", 6);
                    scoreboard.send(player);
                    return true;
                }
                // To encrypt
                if (args[0].equalsIgnoreCase("test-en-base64")) {
                    ItemStack item = player.getInventory().getItemInMainHand();
                    System.out.println(Base64ItemStack.asBase64(item));
                    sendMessage(player, "Check console!");
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-linked-inventory")) {
                    final LinkedInventoryBuilder manager = new LinkedInventoryBuilder()
                            .add(new FirstLinkedInventory())
                            .add(new SecondLinkedInventory());
                    manager.build(player, null);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-text-builder")) {
                    List<BaseComponent> components = TextBuilder.of("&7[Test]").setHoverEvent(HoverEvent.Action.SHOW_TEXT, Collections.singletonList("&7- Ini list ya")).getList();
                    TextBuilder.of("Hello World %lol%").replace("%lol%", components).send(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-template-inventory")) {
                    new InventoryTemplateTest(plugin.getPluginConfig().getTestTemplate()).open(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-lag-inventory")) {
                    new LagInventoryTest().open(player);
                    return true;
                }
                if (args[0].equalsIgnoreCase("test-paginated-inventory")) {
                    new PaginatedInventoryTest().open(player);
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
