package me.droreo002.oreocore.utils.entity;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainTasks;
import com.comphenix.packetwrapper.WrapperPlayServerSetSlot;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Charsets;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.bridge.CrackedServerUtils;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.misc.SoundObject;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

@SuppressWarnings("deprecation")
public final class PlayerUtils {

    public static final UUID INVALID_USER_UUID = UUID.nameUUIDFromBytes("InvalidUsername".getBytes(Charsets.UTF_8));

    /**
     * Generate a new invalid OfflinePlayer by skipping
     * the original Bukkit's lookup. The UUID will be generated using
     * {@link PlayerUtils#INVALID_USER_UUID}
     *
     * @param name The user name
     * @return Invalid OfflinePlayer with name specified
     */
    public static OfflinePlayer getOfflinePlayerSkipLookup(String name) {
        Class<?> gameProfileClass;
        Constructor<?> gameProfileConstructor;
        Constructor<?> craftOfflinePlayerConstructor;
        try {
            try { // 1.7
                gameProfileClass = Class.forName("net.minecraft.util.com.mojang.authlib.GameProfile");
            } catch (ClassNotFoundException e) { // 1.8
                gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
            }
            gameProfileConstructor = gameProfileClass.getDeclaredConstructor(UUID.class, String.class);
            gameProfileConstructor.setAccessible(true);
            Class<?> serverClass = Bukkit.getServer().getClass();
            Class<?> craftOfflinePlayerClass = Class.forName(serverClass.getName()
                    .replace("CraftServer", "CraftOfflinePlayer"));
            craftOfflinePlayerConstructor = craftOfflinePlayerClass.getDeclaredConstructor(
                    serverClass, gameProfileClass
            );
            craftOfflinePlayerConstructor.setAccessible(true);
            Object gameProfile = gameProfileConstructor.newInstance(INVALID_USER_UUID, name);
            Object craftOfflinePlayer = craftOfflinePlayerConstructor.newInstance(Bukkit.getServer(), gameProfile);
            return (OfflinePlayer) craftOfflinePlayer;
        } catch (Throwable t) { // Fallback if fail
            return Bukkit.getOfflinePlayer(name);
        }
    }

    /**
     * Get block's location that the player's looking at
     *
     * @param player The player to check
     * @param distance The distance
     * @return the Location of block the player's looking at
     */
    public static Location getPlayerLooking(Player player, int distance) {
        List<Block> sightBlock = player.getLineOfSight(null, distance);
        List<Location> sight = new ArrayList<Location>();
        for (Block block : sightBlock) {
            sight.add(block.getLocation());
        }
        // Get the last
        return sight.get(sight.size() - 1);
    }

    /**
     * Send a message to player
     *
     * @param message The message to send
     * @param target The target
     * @param clearChat Should we clear chat?
     * @param soundObject The sound to play, nullable
     */
    public static void sendMessage(Player target, String message, boolean clearChat, SoundObject soundObject) {
        if (clearChat) PlayerUtils.clearChat(target);
        if (soundObject != null) soundObject.send(target);
        sendMessage(target, message);
    }

    /**
     * Get the player ping (Via reflection)
     *
     * @param player The target player
     * @return the player ping
     */
    public static int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
            return -1;
        }
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
     * Update the player's inventory via packet way
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
     * Update the player's inventory (non packet)
     *
     * @param player The target player
     */
    public static void updateInventory(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::updateInventory, 1L);
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
            if (ItemStackBuilder.isSimilar(i, item)) {
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
