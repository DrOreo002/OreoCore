package me.droreo002.oreocore.inventory.api.linked;

import me.droreo002.oreocore.inventory.api.CustomInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class LinkedInventory extends CustomInventory {

    public LinkedInventory(int size, String title) {
        super(size, title);
    }

    /**
     * Open the inventory with linked data
     *
     * @param player The target player
     * @param linkedData The linked data from previous inventory
     */
    public void onOpen(Player player, Map<String, Object> linkedData) {}

    /**
     * Request a data from this inventory, called before closing the inventory
     *
     * @return The data
     */
    public Map<String, Object> requestData() {
        return new HashMap<>();
    }
}
