package me.droreo002.oreocore.utils.entity;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainTasks;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.google.common.base.Stopwatch;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import net.minecraft.server.v1_14_R1.PacketPlayOutSetSlot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class PlayerUtils {

    public static Location getPlayerLooking(Player player, int distance) {
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight(null, distance);
        ArrayList<Location> sight = new ArrayList<Location>();
        for (Block block : sightBlock) {
            sight.add(block.getLocation());
        }
        // Get the last
        return sight.get(sight.size() - 1);
    }

    public static String getPlayerName(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            OfflinePlayer off = Bukkit.getOfflinePlayer(uuid);
            if (!off.hasPlayedBefore()) return "";
            return off.getName();
        } else {
            return player.getName();
        }
    }

    /**
     * Check if player is vanished. Will prob only work on SuperVanish
     *
     * @param player : The target player
     * @return true if vanished, false otherwise
     */
    public static boolean isVanished(Player player) {
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }
        return false;
    }

    /**
     * Update the player's inventory
     *
     * @param player : Target player
     */
    public static void updateInventory(Player player, Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            int index = i;
            if (i < 9) {
                index = i + 36;
            }

            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            WrapperPlayServerSetSlot packet = new WrapperPlayServerSetSlot();
            packet.setSlot(index);
            packet.setSlotData(item);
            packet.setWindowId(0);

            packet.sendPacket(player);
        }
    }

    /**
     * Get a OfflinePlayer, this will always try it via async way
     *
     * @param name : The player name
     */
    @SuppressWarnings("deprecation")
    public static void getOfflinePlayer(String name, TaskChainTasks.LastTask<OfflinePlayer> callback) {
        TaskChain<OfflinePlayer> chain = ThreadingUtils.makeChain();
        chain.asyncFirst(() -> Bukkit.getOfflinePlayer(name)).asyncLast(callback).execute();
    }

    /**
     * Check if the player's inventory is full
     *
     * @param player The player to check
     * @return true if full, false otherwise
     */
    public static boolean isInventoryFull(Player player) {
        return InventoryUtils.isInventoryFull(player.getInventory());
    }

    /**
     * Close player's inventory with duplication bug fix
     *
     * @param player The target player
     */
    public static void closeInventory(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    /**
     * Open inventory with duplication bug fix
     *
     * @param player The target player
     * @param inventory The inventory to open
     */
    public static void openInventory(Player player, Inventory inventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }

    /**
     * Check if player has the expected amount of the target item
     *
     * @param expected The expected amount
     * @param playerInventory Player's inventory
     * @param item The item to check
     * @return true if amount is greater than expected, false otherwise
     */
    public static boolean has(int expected, Inventory playerInventory, ItemStack item) {
        int amount = 0;
        final List<ItemStack> items = new ArrayList<>(Arrays.asList(playerInventory.getContents()));
        for (ItemStack i : items) {
            if (CustomItem.isSimilar(i, item)) {
                amount += i.getAmount();
            }
        }

        return amount > expected;
    }
}
