package me.droreo002.oreocore;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChainFactory;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import lombok.Getter;
import me.droreo002.oreocore.bstats.Metrics;
import me.droreo002.oreocore.configuration.dummy.PluginConfig;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import me.droreo002.oreocore.database.object.DatabaseMySQL;
import me.droreo002.oreocore.database.object.DatabaseSQL;
import me.droreo002.oreocore.inventory.api.paginated.PaginatedInventory;
import me.droreo002.oreocore.inventory.listener.CustomInventoryListener;
import me.droreo002.oreocore.inventory.listener.PaginatedInventoryListener;
import me.droreo002.oreocore.listeners.PlayerListener;
import me.droreo002.oreocore.debugging.Debug;
import me.droreo002.oreocore.utils.modules.HookUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
    private Essentials essentials;
    @Getter
    private ProtocolManager protocolManager;
    @Getter
    private DatabaseFlatFile flatFileData;
    @Getter
    private DatabaseMySQL mysqlData;
    @Getter
    private DatabaseSQL sqlData;
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
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        protocolManager = ProtocolLibrary.getProtocolManager();
        HookUtils.getInstance(); // Initialize

        // Registering
        Bukkit.getPluginManager().registerEvents(new CustomInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PaginatedInventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getPluginCommand("oreocore").setExecutor(new CoreCommand(this));

        //new ExampleCommand();
        //flatFileData = new FlatFileDebug();
        //sqlData = new SqlDebug()
        //mysqlData = new MySqlDebug();
        pluginConfig = new PluginConfig(this);

        // Run after few seconds because depend plugin will get ran first
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (!hookedPlugin.isEmpty()) {
                Debug.log("&fAPI is currently handling &7(&c" + hookedPlugin.size() + "&7) &fplugins", true);
                for (Map.Entry ent : hookedPlugin.entrySet()) {
                    JavaPlugin pl = (JavaPlugin) ent.getKey();
                    boolean isPremium = (boolean) ent.getValue();
                    Debug.log("     &f> &e" + pl.getName() + " version " + pl.getDescription().getVersion() + "&f access type is &cFULL_ACCESS &f| " + ((pl.isEnabled()) ? "&aACTIVE &f| " : "&cDISABLED &f| ") + ((isPremium) ? "&cPREMIUM" : "&aFREE"));
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
                Debug.log("&fAPI is currently handling 0 plugin. Its recommended to uninstall this from your server", true);
            }
        }, 20L * 15L); // 15 Seconds
        Debug.log("OreoCore has been enabled successfully!", true);
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
