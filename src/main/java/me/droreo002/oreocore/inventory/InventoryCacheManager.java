package me.droreo002.oreocore.inventory;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryCacheManager {

    @Getter
    private final Map<UUID, OreoInventory> cache;

    public InventoryCacheManager() {
        this.cache = new HashMap<>();
    }

    /**
     * Check if player is opening a custom
     * inventory
     *
     * @param player The target player
     * @return true or false
     */
    public boolean isOpeningInventory(Player player) {
        return cache.containsKey(player.getUniqueId());
    }

    /**
     * Get OreoInventory that the player opens
     *
     * @param player Target player
     * @return OreoInventory if there's any, null otherwise
     */
    public OreoInventory getInventory(Player player) {
        return cache.get(player.getUniqueId());
    }

    /**
     * Remove from cache
     *
     * @param player Target player
     */
    public void remove(Player player) {
        cache.remove(player.getUniqueId());
    }

    /**
     * Add player to cache
     *
     * @param player The player
     * @param inventory The inventory to add
     */
    public void add(Player player, OreoInventory inventory) {
        if (isOpeningInventory(player)) throw new IllegalStateException("Player is already opening a inventory!");
        cache.put(player.getUniqueId(), inventory);
    }
}
