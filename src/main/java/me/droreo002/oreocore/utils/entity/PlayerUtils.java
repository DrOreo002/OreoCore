package me.droreo002.oreocore.utils.entity;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.item.CustomSkull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public final class PlayerUtils {

    public static Location getPlayerLooking(Player player, int distance) {
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight( (Set<Material>) null, distance);
        ArrayList<Location> sight = new ArrayList<Location>();
        for (int i = 0; i < sightBlock.size(); i++) {
            sight.add(sightBlock.get(i).getLocation());
        }
        // Get the last
        return sight.get(sight.size() - 1);
    }

    public static String getPlayerName(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
            if (off == null) return "";
            return off.getName();
        } else {
            return player.getName();
        }
    }

    public static ItemStack getSkull(Player player) {
        return CustomSkull.getHead(player);
    }

    public static void closeInventory(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    public static void openInventory(Player player, Inventory inventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }
}
