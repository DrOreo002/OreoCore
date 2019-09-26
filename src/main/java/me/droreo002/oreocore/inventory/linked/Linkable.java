package me.droreo002.oreocore.inventory.linked;

import me.droreo002.oreocore.inventory.OreoInventory;

import java.util.ArrayList;
import java.util.List;

public interface Linkable {

    /**
     * Called when data is sent from previous inventory. This will also get
     * called before {@link OreoInventory#open}
     *
     * @param data The data from previous inventory
     * @param previousInventory The previous inventory
     */
    default void acceptData(LinkedDatas data, Linkable previousInventory) {}

    /**
     * Called when linked inventory is opened
     *
     * @param previousInventory The previous inventory
     */
    default void onLinkOpen(Linkable previousInventory) {}

    /**
     * Get the Inventory owner
     *
     * @return The OreoInventory instance
     */
    default OreoInventory getInventoryOwner() {
        return (OreoInventory) this;
    }

    /**
     * Get the inventory data
     *
     * @return The data
     */
    default List<LinkedData> getInventoryData() {
        return new ArrayList<>();
    }

    /**
     * Get the linked buttons this will only setup the buttons and will
     * add it into the inventory
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
