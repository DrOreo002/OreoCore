package me.droreo002.oreocore.inventory.api.helper;

import lombok.Data;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.animation.IAnimationRunnable;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface OreoInventory {

    /**
     * Called when click event is called, will only be called if its a valid custom inventory
     *
     * @param e : The click event object
     */
    void onClick(InventoryClickEvent e);

    /**
     * Called when close event is called, will only be called if its a valid custom inventory
     *
     * @param e : The close event object
     */
    void onClose(InventoryCloseEvent e);

    /**
     * Called when the open event is called, will only be called if its a valid custom inventory
     *
     * @param e : The open event object
     */
    void onOpen(InventoryOpenEvent e);

    /**
     * Close player's inventory
     *
     * @param player The target player
     */
    void closeInventory(Player player);

    /**
     * Close player's inventoru with sound
     *
     * @param player The target player
     * @param closeSound The sound to play
     */
    void closeInventory(Player player, SoundObject closeSound);

    /**
     * Open the inventory
     *
     * @param player The target player
     * @param inventory The inventory to open
     */
    void openInventory(Player player, Inventory inventory);

    /**
     * Open the inventory with sound
     *
     * @param player The target player
     * @param inventory The inventory to open
     * @param openSound The sound to play
     */
    void openInventory(Player player, Inventory inventory, SoundObject openSound);

    /**
     * Open the custom inventory
     *
     * @param player The target player
     */
    void open(Player player);

    /**
     * Add a button into the inventory
     *
     * @param guiButton The button to add
     * @param replace Should we replace if it exists already?
     */
    void addButton(GUIButton guiButton, boolean replace);

    /**
     * Get the GUIButton on that slot
     *
     * @param slot The inventory slot
     * @return the GUIButton
     */
    GUIButton getButton(int slot);

    /**
     * Check if the slot has a button
     *
     * @param slot The slot to check
     * @return true if it has, false otherwise
     */
    boolean isHasButton(int slot);

    /**
     * Remove the button on that slot
     *
     * @param slot The slot
     */
    void removeButton(int slot);

    /**
     * Add a border
     *
     * @param row The row
     * @param border The border item
     * @param replace Should we replace item on the border line?
     */
    void addBorder(ItemStack border, boolean replace, int row);

    /**
     * Add a border
     *
     * @param rows The rows to add
     * @param border the border item
     * @param replace Should we replace item on the border line?
     */
    void addBorder(ItemStack border, boolean replace, int... rows);

    /**
     * Setup the inventory
     */
    void setup();

    /**
     * Get the animation task id
     *
     * @return the task id
     */
    int getAnimationTaskId();

    /**
     * Set the animation task id
     *
     * @param newId The new id
     */
    void setAnimationTaskId(int newId);

    /**
     * Refresh the inventory a.k.a re setup
     */
    void refreshInventory();

    /**
     * Get the animation runnable for the inventory
     *
     * @return The animation runnable
     */
    IAnimationRunnable getAnimationRunnable();
}
