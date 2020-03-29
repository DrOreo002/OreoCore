package me.droreo002.oreocore;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import lombok.Getter;
import me.droreo002.oreocore.bstats.Metrics;
import me.droreo002.oreocore.configuration.dummy.PluginConfig;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.debug.SqlDebug;
import me.droreo002.oreocore.database.utils.PlayerInformationDatabase;
import me.droreo002.oreocore.debugging.ODebug;
import me.droreo002.oreocore.debugging.Process;
import me.droreo002.oreocore.dependencies.DependencyManager;
import me.droreo002.oreocore.dependencies.OCoreDependency;
import me.droreo002.oreocore.dependencies.classloader.PluginClassLoader;
import me.droreo002.oreocore.dependencies.classloader.ReflectionClassLoader;
import me.droreo002.oreocore.inventory.InventoryCacheManager;
import me.droreo002.oreocore.listeners.inventory.MainInventoryListener;
import me.droreo002.oreocore.listeners.player.PlayerListener;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.modules.HookUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class OreoCore extends JavaPlugin {

    @Getter
    private static OreoCore instance;
    @Getter
    private TaskChainFactory taskChainFactory;
    @Getter
    private final Map<String, DependedPluginProperties> hookedPlugin = new HashMap<>();
    @Getter
    private InventoryCacheManager inventoryCacheManager;
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
    @Getter
    private PluginClassLoader pluginClassLoader;
    @Getter
    private DependencyManager dependencyManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        taskChainFactory = BukkitTaskChainFactory.create(this);
        metrics = new Metrics(this);
        prefix = StringUtils.color("&7[ &bOreoCore &7]&f ");
        protocolManager = ProtocolLibrary.getProtocolManager();
        pluginClassLoader = new ReflectionClassLoader(this);
        dependencyManager = new DependencyManager(this, pluginClassLoader);
        inventoryCacheManager = new InventoryCacheManager();

        HookUtils.getInstance(); // Initialize

        if (ServerUtils.isOldAsFuckVersion()) {
            ODebug.log(this, "Old minecraft version found!. Beginning loading external sql driver! This might take a while please wait...", true);

            Process process = new Process();
            /*
            We load custom driver manually, since in 1.8 sql(s) driver are limited
             */
            for (OCoreDependency dependency : OCoreDependency.values()) {
                dependencyManager.loadDependencies(dependency.getDependency());
            }

            try {
                Class.forName("org.sqlite.jdbc4.JDBC4Connection");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ODebug.log(this, process.stop("Dependencies successfully loaded in &e%totalTime ms!"), true);
        }

        // Registering
        Bukkit.getPluginManager().registerEvents(new MainInventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getPluginCommand("oreocore").setExecutor(new CoreCommand(this));
        pluginConfig = new PluginConfig(this);
        if (pluginConfig.isCachePlayerInformation()) {
            playerInformationDatabase = new PlayerInformationDatabase(this);
        }

        // Debug
        new SqlDebug();

        // Run after few seconds because depend plugin will get ran first
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            final ConfigurationSection dData = getPluginConfig().getDebuggingData();
            if (!hookedPlugin.isEmpty()) {
                ODebug.log(this, "&fI'm currently handling &7(&c" + hookedPlugin.size() + "&7) &fplugins", true);
                for (Map.Entry<String, DependedPluginProperties> ent : hookedPlugin.entrySet()) {
                    JavaPlugin pl = ServerUtils.getPlugin(ent.getKey());
                    DependedPluginProperties properties = ent.getValue();
                    if (properties == null) continue;
                    if (dData == null) return;

                    boolean isPremium = properties.isPremiumPlugin();
                    ODebug.log(this, "     &f> &e" + pl.getName() + " version &b" + pl.getDescription().getVersion() + "&f access type is &cFULL_ACCESS &f| " + ((pl.isEnabled()) ? "&aACTIVE &f| " : "&cDISABLED &f| ") + ((isPremium) ? "&cPREMIUM" : "&aFREE"), false);

                    // Update config data
                    dData.set(pl.getName(), properties.isEnableLogging());
                }
                getPluginConfig().setDebuggingData(dData);
                getPluginConfig().saveConfig(true);

                metrics.addCustomChart(new Metrics.AdvancedPie("handled_plugin", () -> {
                    final Map<String, Integer> res = new HashMap<>();
                    for (Map.Entry<String, DependedPluginProperties> ent : hookedPlugin.entrySet()) {
                        JavaPlugin pl = ServerUtils.getPlugin(ent.getKey());
                        res.put(pl.getName(), 1);
                    }
                    return res;
                }));
            } else {
                ODebug.log(this, "&fI'm currently handling &7(&c0&7) plugin", true);
            }
        }, 20L * 120L); // 2 Minute after initialization
        ODebug.log(this, "OreoCore has been enabled successfully!", true);
    }

    @Override
    public void onDisable() {
        for (UUID uuid : inventoryCacheManager.getCache().keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            PlayerUtils.closeInventory(player);
        }
        inventoryCacheManager.getCache().clear();

        // Disable
        DatabaseManager.getDatabases().forEach(Database::onDisable);
    }

    /**
     * Add the specified plugin as a plugin that depend on this api
     *
     * @param plugin The specified plugin
     * @param properties The plugin properties
     */
    public void dependPlugin(JavaPlugin plugin, DependedPluginProperties properties) {
        hookedPlugin.put(plugin.getName(), properties);
    }
}
