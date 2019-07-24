package me.droreo002.oreocore.inventory.linked;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public interface Linkable {

    /**
     * Open the inventory with linked data
     *
     * @param player The target player
     * @param linkedData The linked data from previous inventory
     */
    default void onOpen(Player player, Map<String, Object> linkedData) {}

    /**
     * Request a data from this inventory, called before closing the inventory
     *
     * @return The data
     */
    default Map<String, Object> onLinkRequestData() {
        return new HashMap<>();
    }
}
