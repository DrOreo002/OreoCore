package me.droreo002.oreocore.inventory.linked;

import me.droreo002.oreocore.inventory.OreoInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public interface Linkable {

    /**
     * Request a data from this inventory, called before closing the inventory
     *
     * @return The data
     */
    default Map<String, Object> onLinkRequestData() {
        return new HashMap<>();
    }

    /**
     * Called when data is sent from previous inventory
     *
     * @param data The data from previous inventory
     * @param previousInventory The previous inventory
     */
    default void onLinkAcceptData(Map<String, Object> data, Linkable previousInventory) {}

    /**
     * Get the Inventory owner
     *
     * @return The OreoInventory instance
     */
    default OreoInventory getInventoryOwner() {
        return (OreoInventory) this;
    }


    /**
     * Get the next inventory button, nulls are allowed
     *
     * @return The next inventory button
     */
    default LinkedButton getNextInventoryButton() {
        return null;
    }

    /**
     * Get the previous inventory button, nulls are allowed
     *
     * @return The previous inventory button
     */
    default LinkedButton getPreviousInventoryButton() {
        return null;
    }

    /**
     * Get the inventory name
     *
     * @return The inventory name
     */
    String getInventoryName();
}
