package me.droreo002.oreocore.inventory.api.helper;

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

     void closeInventory(Player player);

     void closeInventory(Player player, SoundObject closeSound);

     void openInventory(Player player, Inventory inventory);

     void openInventory(Player player, Inventory inventory, SoundObject openSound);

     void open(Player player);

     void openAsync(Player player);

     void openAsync(Player player, int delayInSecond);

     void addButton(GUIButton guiButton, boolean replace);

     GUIButton getButton(int slot);

     boolean isHasButton(int slot);

     void removeButton(int slot);

     void addBorder(int row, ItemStack border, boolean replace);

     void addBorder(int[] rows, ItemStack border, boolean replace);

     void setup();

     int getAnimationTaskId();

     void setAnimationTaskId(int newId);

     IAnimationRunnable getAnimationRunnable();
}
