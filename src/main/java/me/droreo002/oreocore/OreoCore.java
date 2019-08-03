package me.droreo002.oreocore;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.droreo002.oreocore.bstats.Metrics;
import me.droreo002.oreocore.configuration.ConfigUpdater;
import me.droreo002.oreocore.configuration.dummy.PluginConfig;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.utils.PlayerInformationDatabase;
import me.droreo002.oreocore.debugging.ODebug;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.listeners.inventory.MainInventoryListener;
import me.droreo002.oreocore.listeners.player.PlayerListener;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.modules.HookUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class OreoCore extends JavaPlugin {

    @Getter
    private static OreoCore instance;
    @Getter
    private TaskChainFactory taskChainFactory;
    @Getter
    private final Map<JavaPlugin, Boolean> hookedPlugin = new HashMap<>();
    @Getter
    private final Map<UUID, PaginatedInventory> opening = new HashMap<>();
    @Getter
    private String prefix;
    @Getter
    private ProtocolManager protocolManager;
    @Getter
    private PlayerInformationDatabase playerInformationDatabase;
    @Getter
    private PluginConfig pluginConfig;
    @Getter
    private Metrics metrics;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        taskChainFactory = BukkitTaskChainFactory.create(this);
        metrics = new Metrics(this);
        prefix = StringUtils.color("&7[ &bOreoCore &7]&f ");
        protocolManager = ProtocolLibrary.getProtocolManager();
        HookUtils.getInstance(); // Initialize
        ConfigurationSerialization.registerClass(CustomItem.class);

        // Registering
        Bukkit.getPluginManager().registerEvents(new MainInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getPluginCommand("oreocore").setExecutor(new CoreCommand(this));
        pluginConfig = new PluginConfig(this);
        playerInformationDatabase = new PlayerInformationDatabase(this);

        // For config updating
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            ODebug.log("&fChecking for config update...", true);
            try {
                ConfigUpdater.update(new File(getDataFolder(), "config.yml"), this, "config.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }

            pluginConfig.reloadConfig();
        }, 40L);

        // Run after few seconds because depend plugin will get ran first
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (!hookedPlugin.isEmpty()) {
                ODebug.log("&fI'm currently handling &7(&c" + hookedPlugin.size() + "&7) &fplugins", true);
                for (Map.Entry ent : hookedPlugin.entrySet()) {
                    JavaPlugin pl = (JavaPlugin) ent.getKey();
                    boolean isPremium = (boolean) ent.getValue();
                    ODebug.log("     &f> &e" + pl.getName() + " version &b" + pl.getDescription().getVersion() + "&f access type is &cFULL_ACCESS &f| " + ((pl.isEnabled()) ? "&aACTIVE &f| " : "&cDISABLED &f| ") + ((isPremium) ? "&cPREMIUM" : "&aFREE"));
                }
                metrics.addCustomChart(new Metrics.AdvancedPie("handled_plugin", () -> {
                    final Map<String, Integer> res = new HashMap<>();
                    for (Map.Entry ent : hookedPlugin.entrySet()) {
                        JavaPlugin pl = (JavaPlugin) ent.getKey();
                        res.put(pl.getName(), 1);
                    }
                    return res;
                }));
            } else {
                ODebug.log("&fI'm currently handling &7(&c0&7) plugin", true);
            }
        }, 20L * 15L); // 15 Seconds
        ODebug.log("OreoCore has been enabled successfully!", true);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (UUID uuid : opening.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            player.closeInventory();
        }
        // Disable
        DatabaseManager.getDatabases().forEach(Database::onDisable);
    }

    /**
     * Add the specified plugin as a plugin that depend on this api
     *
     * @param plugin : The specified plugin
     * @param premium : Determine if this plugin is a premium plugin or not
     */
    public void dependPlugin(JavaPlugin plugin, boolean premium) {
        hookedPlugin.put(plugin, premium);
    }
}
