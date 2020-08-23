package me.droreo002.oreocore.listeners.player;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.dummy.PluginConfig;
import me.droreo002.oreocore.debugging.Process;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final OreoCore plugin = OreoCore.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        final PluginConfig memory = plugin.getPluginConfig();

        if (plugin.getPlayerInformationDatabase() != null) {
            plugin.getPlayerInformationDatabase().loadPlayer(player);
        }
        if (memory.isCachePlayerHead()) {
            ThreadingUtils.makeChain().asyncFirst(() -> CustomSkull.fromUniqueId(player.getUniqueId())).execute();
        }

        if (!player.hasPermission("oreocore.admin")) return;
        if (plugin.getPluginConfig().isDisableNotif()) return;
        player.sendMessage(plugin.getPrefix() + StringUtils.color("&fThis server is currently running on &eOreoCore &7(&c" + plugin.getDescription().getVersion() + "&7)&f. This plugin is also currently handling &7(&c" + plugin.getHookedPlugin().size() + "&7) &fplugin"));
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Firework)) return;
        Firework firework = (Firework) e.getDamager();

        if (firework.getCustomName() != null) {
            if (firework.getCustomName().contains("OreoCore")) e.setCancelled(true);
        }
    }
}
