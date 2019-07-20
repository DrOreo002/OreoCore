package me.droreo002.oreocore.utils.entity;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainTasks;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.bridge.CrackedServerUtils;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

public final class PlayerUtils {

    /**
     * Get block's location that the player's looking at
     *
     * @param player The player to check
     * @param distance The distance
     * @return the Location of block the player's looking at
     */
    public static Location getPlayerLooking(Player player, int distance) {
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight(null, distance);
        ArrayList<Location> sight = new ArrayList<Location>();
        for (Block block : sightBlock) {
            sight.add(block.getLocation());
        }
        // Get the last
        return sight.get(sight.size() - 1);
    }

    /**
     * Get player name by UUID
     *
     * @param uuid The UUID
     * @return the player name if succeeded, empty string otherwise
     */
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
     * Get player's uuid by name (Not recommended for server that had 1k+ offline player data tho)
     *
     * @param playerName Player name
     * @return Player's uuid
     */
    @SuppressWarnings("deprecation")
    public static Future<UUID> getPlayerUuid(String playerName) {
        return ThreadingUtils.makeFuture(() -> {
            UUID uuid = Bukkit.getOfflinePlayer(playerName).getUniqueId();
            if (!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
                return CrackedServerUtils.getPlayerUuid(playerName);
            }
            return uuid;
        });
    }

    /**
     * Clear player's chat
     *
     * @param player Target player
     */
    public static void clearChat(Player player) {
        for (int i = 0; i < 40; i++) player.sendMessage(" ");
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

    /**
     * Send a ResourcePack to player
     *
     * @param player The target player
     * @param link The ResourcePack direct download link
     * @param sha The ResourcePack's sha
     *
     * @throws InvocationTargetException If something bad happens
     */
    public static void sendResourcepack(Player player, String link, String sha) throws InvocationTargetException {
        PacketContainer pac = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.RESOURCE_PACK_SEND);
        pac.getStrings().write(0, link);
        pac.getStrings().write(1, sha.toLowerCase());

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, pac);
    }

    /**
     * Send message to the player
     *
     * @param player The target player
     * @param msg The message to send (color code supported)
     */
    public static void sendMessage(Player player, String msg) {
        player.sendMessage(StringUtils.color(msg));
    }
}
