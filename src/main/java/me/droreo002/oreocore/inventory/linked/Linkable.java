package me.droreo002.oreocore.inventory.linked;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * Called when data is sent from previous inventory. This will also get
     * called before {@link OreoInventory#open}
     *
     * @param data The data from previous inventory
     * @param previousInventory The previous inventory
     */
    default void onLinkAcceptData(Map<String, Object> data, Linkable previousInventory) {}

    /**
     * Called when pre opening other inventory
     *
     * @param event The inventory click event
     */
    default void onPreOpenOtherInventory(InventoryClickEvent event, Linkable targetInventory) {}

    /**
     * Get the default listener's ClickType
     * default are LEFT. This will change what ClickType is processed
     * when clicking LinkedButton
     *
     * @return the ClickType
     */
    default ClickType getDefaultListenerClickType() {
        return ClickType.LEFT;
    }

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
    default List<LinkedButton> getLinkedButtons() {
        return new ArrayList<>();
    }

    /**
     * Get the inventory name
     *
     * @return The inventory name
     */
    String getInventoryName();
}
