package me.droreo002.oreocore.inventory.button;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ButtonListener {

    /**
     * Get the button listener's ClickType
     *
     * @return The click type
     */
    default ClickType getClickType() {
        return ClickType.LEFT;
    }

    /**
     * Called when button is clicked
     *
     * @param e The inventory click event
     */
    void onClick(InventoryClickEvent e);
}