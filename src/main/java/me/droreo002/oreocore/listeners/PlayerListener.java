package me.droreo002.oreocore.listeners;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.strings.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private final OreoCore plugin = OreoCore.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.sendMessage(StringUtil.color("&7[ &aOreoCore &7] &fThis server is currently running on &bOreoCore &fversion &b" + plugin.getDescription().getVersion() + "&f. This plugin is also currently handling &7(&c" + plugin.getHookedPlugin().size() + "&7)"));
    }
}
