package me.droreo002.oreocore;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import lombok.Getter;
import me.droreo002.oreocore.commands.object.base.ExampleCommand;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.debug.FlatFileDebug;
import me.droreo002.oreocore.database.debug.MySqlDebug;
import me.droreo002.oreocore.database.debug.SqlDebug;
import me.droreo002.oreocore.database.object.DatabaseFlatFile;
import me.droreo002.oreocore.database.object.DatabaseMySQL;
import me.droreo002.oreocore.database.object.DatabaseSQL;
import me.droreo002.oreocore.inventory.api.paginated.PaginatedInventory;
import me.droreo002.oreocore.inventory.listener.CustomInventoryListener;
import me.droreo002.oreocore.inventory.listener.PaginatedInventoryListener;
import me.droreo002.oreocore.listeners.PlayerListener;
import me.droreo002.oreocore.utils.logging.Debug;
import me.droreo002.oreocore.utils.modules.HookUtils;
import me.droreo002.oreocore.utils.strings.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class OreoCore extends JavaPlugin {

    @Getter
    private static OreoCore instance;
    @Getter
    private final List<JavaPlugin> hookedPlugin = new ArrayList<>();
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

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        prefix = StringUtil.color("&7[ &bOreoCore &7]&f ");
        essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        protocolManager = ProtocolLibrary.getProtocolManager();
        HookUtils.getInstance(); // Initialize

        // Registering
        Bukkit.getPluginManager().registerEvents(new CustomInventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PaginatedInventoryListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);

        Bukkit.getPluginCommand("oreocore").setExecutor(new CoreCommand(this));

        Debug.log("OreoCore has been enabled successfully!", true);

        // Run after few seconds because depend plugin will get ran first
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (!hookedPlugin.isEmpty()) {
                Debug.log("&fAPI is currently handling &7(&c" + hookedPlugin.size() + "&7) &fplugins", true);
                for (JavaPlugin pl : hookedPlugin) {
                    Debug.log("     &f> &e" + pl.getName() + " version " + pl.getDescription().getVersion() + "&f access type is &cFULL_ACCESS &f| " + ((pl.isEnabled()) ? "&aACTIVE" : "&cDISABLED"));
                }
            } else {
                Debug.log("&fAPI is currently handling no plugin. You can uninstall this from your server if you want, because it will not doing anything", true);
            }
        }, 20L * 15L); // 15 Seconds
        //new ExampleCommand();
        //flatFileData = new FlatFileDebug();
        //sqlData = new SqlDebug()
        // mysqlData = new MySqlDebug();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (UUID uuid : opening.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, player::closeInventory, 1L);
        }
    }

    /**
     * Add the specified plugin as a plugin that depend on this api
     *
     * @param plugin : The specified plugin
     */
    public void dependPlugin(JavaPlugin plugin) {
        hookedPlugin.add(plugin);
    }
}
