package me.droreo002.oreocore.inventory.api;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.api.animation.ItemAnimation;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CustomInventory implements InventoryHolder {

    private Inventory inventory;

    @Getter
    private final Map<Integer, ItemAnimation> animationButtonMap = new HashMap<>();
    @Getter
    private final Map<Integer, GUIButton> buttonMap = new HashMap<>();
    @Getter
    private final Map<Integer, ItemStack> normalItem = new HashMap<>();
    @Getter
    private int size;
    @Getter
    private String title;
    @Getter
    @Setter
    private boolean shouldProcessButton, containsAnimation, cancelPlayerInventoryClickEvent; // Cancel the click when player clicked his / her inventory?
    @Getter
    @Setter
    private List<Integer> noClickCancel; // Don't cancel the click event on these slots
    @Getter
    @Setter
    private SoundObject soundOnClick, soundOnOpen, soundOnClose;

    public CustomInventory(int size, String title) {
        this.size = size;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, size, title);
        this.cancelPlayerInventoryClickEvent = true; // Default are true
        this.shouldProcessButton = true;
        this.noClickCancel = new ArrayList<>();
    }

    /**
     * Called when click event is called, will only be called if its a valid custom inventory
     *
     * @param e : The click event object
     */
    public abstract void onClick(InventoryClickEvent e);

    /**
     * Called when close event is called, will only be called if its a valid custom inventory
     *
     * @param e : The close event object
     */
    public abstract void onClose(InventoryCloseEvent e);

    /**
     * Called when the open event is called, will only be called if its a valid custom inventory
     *
     * @param e : The open event object
     */
    public abstract void onOpen(InventoryOpenEvent e);

    /**
     * Called at the first time when the vanilla click event is called. And if the inventory is a valid
     * custom inventory
     *
     * @param e : The click event object
     * @return true if the event is cancelled, false otherwise
     */
    public boolean onPreClick(InventoryClickEvent e) {
        return false;
    }

    /**
     * Close the player's inventory, scheduled 1 tick to prevent duplication glitch
     *
     * @param player : Target player
     */
    public void close(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    /**
     * Open an inventory for the player, scheduled 1 tick to prevent duplication glitch
     *
     * @param player : Target player
     * @param inventory : Inventory to open
     */
    public void open(Player player, Inventory inventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }

    /**
     * Close the player's inventory, scheduled 1 tick to prevent duplication glitch. This will also play sounds
     *
     * @param player : Target player
     * @param soundWhenClose : The sound that will get played when the inventory closes
     */
    public void close(Player player, SoundObject soundWhenClose) {
        if (soundWhenClose != null) {
            soundWhenClose.send(player);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    /**
     * Open an inventory for the player, scheduled 1 tick to prevent duplication glitch. This will also play sounds
     *
     * @param player : Target player
     * @param inventory : The inventory
     * @param soundWhenOpen : The shounds that will get played when the inventory opens
     */
    public void open(Player player, Inventory inventory, SoundObject soundWhenOpen) {
        if (soundWhenOpen != null) {
            soundWhenOpen.send(player);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }

    /**
     * Open the custom inventory
     *
     * @param player : Target player
     */
    public void open(Player player) {
        for (Map.Entry ent : buttonMap.entrySet()) {
            int slot = (int) ent.getKey();
            GUIButton button = (GUIButton) ent.getValue();
            inventory.setItem(slot, button.getItem());
        }

        if (!animationButtonMap.isEmpty()) {
            containsAnimation = true;
            for (Map.Entry ent : animationButtonMap.entrySet()) {
                int slot = (int) ent.getKey();
                ItemAnimation button = (ItemAnimation) ent.getValue();
                inventory.setItem(slot, button.getItem());
            }
        }

        if (!normalItem.isEmpty()) {
            for (Map.Entry ent : normalItem.entrySet()) {
                int slot = (int) ent.getKey();
                ItemStack item = (ItemStack) ent.getValue();
                inventory.setItem(slot, item);
            }
        }

        open(player, getInventory());
    }

    /**
     * Reset the inventory, will set it to default
     */
    public void reset() {
        for (Map.Entry ent : buttonMap.entrySet()) {
            int slot = (int) ent.getKey();
            GUIButton button = (GUIButton) ent.getValue();
            inventory.setItem(slot, button.getItem());
        }

        if (!animationButtonMap.isEmpty()) {
            containsAnimation = true;
            for (Map.Entry ent : animationButtonMap.entrySet()) {
                int slot = (int) ent.getKey();
                ItemAnimation button = (ItemAnimation) ent.getValue();
                inventory.setItem(slot, button.getItem());
            }
        }

        for (HumanEntity ent : inventory.getViewers()) {
            if (ent == null) continue;
            if (!(ent instanceof Player)) continue;
            Player player = (Player) ent;
            player.updateInventory();
        }
    }

    /**
     * Add a button into the inventory
     *
     * @param slot : The slot to put it
     * @param button : The button object
     * @param replaceIfExist : Do we need to replace the button if it already exists at that slot?
     */
    public void addButton(int slot, GUIButton button, boolean replaceIfExist) {
        Validate.notNull(button, "Button cannot be null!");
        if (replaceIfExist) {
            if (buttonMap.containsKey(slot)) {
                buttonMap.remove(slot);
                buttonMap.put(slot, button);
            } else {
                buttonMap.put(slot, button);
            }
        } else {
            if (buttonMap.containsKey(slot)) {
                throw new IllegalStateException("Please select other empty slot!");
            }
            buttonMap.put(slot, button);
        }
    }

    /**
     * Add a AnimatedButton into the inventory
     *
     * @param slot : The slot to put it
     * @param button : The button object (Animated)
     * @param replaceIfExist : Do we need to replace the button if it already exists at that slot?
     */
    public void addAnimatedButton(int slot, ItemAnimation button, boolean replaceIfExist) {
        Validate.notNull(button, "Button cannot be null!");
        if (replaceIfExist) {
            if (animationButtonMap.containsKey(slot)) {
                animationButtonMap.remove(slot);
                animationButtonMap.put(slot, button);
            } else {
                animationButtonMap.put(slot, button);
            }
        } else {
            if (animationButtonMap.containsKey(slot)) {
                throw new IllegalStateException("Please select other empty slot!");
            }
            animationButtonMap.put(slot, button);
        }
    }

    /**
     * Add a border, will fill the row with the specified item
     *
     * @param row : The row
     * @param border : The item
     * @param replaceIfExist : Replace if there's something on the row?
     */
    public void addBorder(int row, ItemStack border, boolean replaceIfExist) {
        if (row < 0) throw new IllegalStateException("Row cannot be 0!");
        for (int i = row * 9; i < (row * 9) + 9; i++) {
            addButton(i, new GUIButton(border).setListener(e -> close((Player) e.getWhoClicked())), replaceIfExist);
        }
    }

    /**
     * Add a border, will fill the row with the specified item
     *
     * @param rows : The rows
     * @param border : The item
     * @param replaceIfExist : Replace if there's something on the row?
     */
    public void addBorder(int[] rows, ItemStack border, boolean replaceIfExist) {
        for (int row : rows) {
            if (row < 0) throw new IllegalStateException("Row cannot be 0!");
            for (int i = row * 9; i < (row * 9) + 9; i++) {
                addButton(i, new GUIButton(border).setListener(e -> close((Player) e.getWhoClicked())), replaceIfExist);
            }
        }
    }

    public void setItem(int slot, ItemStack item, boolean replaceIfExists) {
        if (replaceIfExists) {
            animationButtonMap.remove(slot);
            buttonMap.remove(slot);
        }
        normalItem.put(slot, item);
    }

    /**
     * Check if its a button
     *
     * @param e : The event, we use this instead for shorter code
     * @return true if its a button, false otherwise
     */
    public boolean isButton(InventoryClickEvent e) {
        if (buttonMap.isEmpty()) return false;
        return buttonMap.containsKey(e.getSlot());
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
