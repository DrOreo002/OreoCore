package me.droreo002.oreocore.inventory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.inventory.animation.InventoryAnimation;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.button.GroupedButton;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class OreoInventory implements InventoryHolder {

    private Inventory inventory;

    @Getter
    private int size;
    @Getter
    private String title;
    @Getter @Setter
    private List<GUIButton> buttons;
    @Getter @Setter
    private List<Integer> disabledClickListeners;
    @Getter @Setter
    private InventoryAnimation inventoryAnimation;
    @Getter @Setter
    private SoundObject soundOnClick, soundOnOpen, soundOnClose;
    @Getter @Setter
    private boolean shouldProcessButtonClickEvent;
    @Getter @Setter
    private List<GroupedButton> groupedButtons;

    public OreoInventory(int size, String title) {
        this.size = size;
        this.title = StringUtils.color(title);
        this.buttons = new ArrayList<>();
        this.disabledClickListeners = new ArrayList<>();
        this.groupedButtons = new ArrayList<>();
        this.inventory = Bukkit.createInventory(this, size, title);
        this.soundOnClick = new SoundObject(Sounds.CLICK);
        this.soundOnClose = new SoundObject(Sounds.CHEST_CLOSE);
        this.soundOnOpen = new SoundObject(Sounds.CHEST_OPEN);
        this.shouldProcessButtonClickEvent = true;
    }

    /**
     * The click handler for the inventory (Universal)
     *
     * @param e : The click event object
     */
    public void onClickHandler(InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory inventory = view.getTopInventory();
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        final OreoInventory oreoInventory = (OreoInventory) inventory.getHolder();
        if (oreoInventory == null) return;

        oreoInventory.onClick(e);
        e.setCancelled(!oreoInventory.getDisabledClickListeners().contains(slot));
        GUIButton button = oreoInventory.getButton(slot);
        if (button != null) {
            if (oreoInventory.isShouldProcessButtonClickEvent()) {
                if (button.getListener() != null) button.getListener().onClick(e);
            }
        }
        if (oreoInventory.getSoundOnClick() != null) oreoInventory.getSoundOnClick().send(player);
    }

    /**
     * Called when close event is called, will only be called if its a valid custom inventory
     *
     * @param e : The close event object
     */
    public void onCloseHandler(InventoryCloseEvent e) {
        final Player player = (Player) e.getPlayer();
        final Inventory inventory = e.getInventory();

        /*
        Simple anti cheat or duplication bug here
        might work, not sure.
         */
        if (!ServerUtils.isLegacyVersion()) {
            if (!player.getItemOnCursor().getType().equals(UMaterial.AIR.getMaterial()) && player.isSneaking()) {
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
        } else {
            if (player.getItemOnCursor() != null && player.isSneaking()) { // Anti cheat
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
        }

        final OreoInventory oreoInventory = (OreoInventory) inventory.getHolder();
        if (oreoInventory == null) return;

        oreoInventory.onClose(e);
        if (oreoInventory.getSoundOnClose() != null) oreoInventory.getSoundOnClose().send(player);
        final InventoryAnimation inventoryAnimation = oreoInventory.getInventoryAnimation();

        if (inventoryAnimation != null) {
            if (inventoryAnimation.isButtonAnimationRunning()) inventoryAnimation.stopAnimation();
            if (inventoryAnimation.isOpenAnimationRunning()) inventoryAnimation.getOpenAnimation().stop(false);
        }
    }

    /**
     * Called when the open event is called, will only be called if its a valid custom inventory
     *
     * @param e : The open event object
     */
    public void onOpenHandler(InventoryOpenEvent e) {
        final Player player = (Player) e.getPlayer();
        final Inventory inventory = e.getInventory();
        final OreoInventory oreoInventory = (OreoInventory) inventory.getHolder();
        if (oreoInventory == null) return;

        oreoInventory.onOpen(e);
        if (oreoInventory.getSoundOnOpen() != null) oreoInventory.getSoundOnOpen().send(player);
        InventoryAnimation inventoryAnimation = oreoInventory.getInventoryAnimation();
        if (inventoryAnimation != null) {
            if (inventoryAnimation.getOpenAnimation() != null) {
                inventoryAnimation.getOpenAnimation().start(aVoid -> inventoryAnimation.startAnimation(oreoInventory));
            }
            inventoryAnimation.startAnimation(oreoInventory);
        }
    }

    /**
     * Called when click event is called
     *
     * @param e : The click event object
     */
    public void onClick(InventoryClickEvent e) {}

    /**
     * Called when close event is called, will only be called if its a valid custom inventory
     *
     * @param e : The close event object
     */
    public void onClose(InventoryCloseEvent e) {}

    /**
     * Called when the open event is called, will only be called if its a valid custom inventory
     *
     * @param e : The open event object
     */
    public void onOpen(InventoryOpenEvent e) {}

    /**
     * Get the inventory
     *
     * @return The inventory, can be null also
     */
    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }

    /**
     * Close player's inventory
     *
     * @param player The target player
     */
    public void closeInventory(Player player) {
        PlayerUtils.closeInventory(player);
    }

    /**
     * Close player's inventoru with sound
     *
     * @param player The target player
     * @param closeSound The sound to play
     */
    public void closeInventory(Player player, SoundObject closeSound) {
        PlayerUtils.closeInventory(player);
        if (closeSound != null) closeSound.send(player);
    }

    /**
     * Open the inventory
     *
     * @param player The target player
     * @param inventory The inventory to open
     */
    public void openInventory(Player player, Inventory inventory) {
        PlayerUtils.openInventory(player, inventory);
    }

    /**
     * Open the inventory with sound
     *
     * @param player The target player
     * @param inventory The inventory to open
     * @param openSound The sound to play
     */
    public void openInventory(Player player, Inventory inventory, SoundObject openSound) {
        PlayerUtils.openInventory(player, inventory);
        if (openSound != null) openSound.send(player);
    }

    /**
     * Open the custom inventory
     *
     * @param player The target player
     */
    public void open(Player player) {
        setup();
        openInventory(player, getInventory());
    }

    /**
     * Add a button into the inventory
     *
     * @param guiButton The button to add
     * @param replace Should we replace if it exists already?
     */
    public void addButton(GUIButton guiButton, boolean replace) {
        Validate.notNull(guiButton, "Button cannot be null!");
        if (replace) {
            if (isHasButton(guiButton.getInventorySlot())) {
                removeButton(guiButton.getInventorySlot());
                getButtons().add(guiButton);
            } else {
                getButtons().add(guiButton);
            }
        } else {
            if (isHasButton(guiButton.getInventorySlot())) {
                throw new IllegalStateException("Slot " + guiButton.getInventorySlot() + " already occupied");
            }
            getButtons().add(guiButton);
        }
    }

    /**
     * Get the GUIButton on that slot
     *
     * @param slot The inventory slot
     * @return the GUIButton
     */
    @Nullable
    public GUIButton getButton(int slot) {
        return buttons.stream().filter(but -> but.getInventorySlot() == slot)
                .findFirst().orElse(null);
    }

    /**
     * Check if the slot has a button
     *
     * @param slot The slot to check
     * @return true if it has, false otherwise
     */
    public boolean isHasButton(int slot) {
        return getButton(slot) != null;
    }

    /**
     * Remove the button on that slot
     *
     * @param slot The slot
     */
    public void removeButton(int slot) {
        buttons.removeIf(button -> button.getInventorySlot() == slot);
    }

    /**
     * Add a border
     *
     * @param row The row
     * @param border The border item
     * @param replace Should we replace item on the border line?
     */
    public void addBorder(ItemStack border, boolean replace, int row) {
        addBorder(border, replace, new int[] {row});
    }

    /**
     * Add a border
     *
     * @param rows The rows to add
     * @param border the border item
     * @param replace Should we replace item on the border line?
     */
    public void addBorder(ItemStack border, boolean replace, int... rows) {
        for (int row : rows) {
            if (row < 0) throw new IllegalStateException("Row cannot be 0!");
            for (int i = row * 9; i < (row * 9) + 9; i++) {
                addButton(new GUIButton(border, i).setListener(GUIButton.CLOSE_LISTENER), replace);
            }
        }
    }

    /**
     * Setup the inventory
     */
    public void setup() {
        getButtons().forEach(guiButton -> getInventory().setItem(guiButton.getInventorySlot(), guiButton.getItem()));
        if (!getGroupedButtons().isEmpty()) {
            for (GroupedButton groupedButton : getGroupedButtons()) {
                if (groupedButton.isShouldOverrideOtherButton()) { // Remove every single thing inside
                    for (int i : groupedButton.getSlots()) {
                        getInventory().setItem(i, UMaterial.AIR.getItemStack());
                    }
                }

                for (GUIButton button : groupedButton.getButtons()) {
                    final int slot = button.getInventorySlot();
                    if (groupedButton.isShouldOverrideOtherButton()) {
                        getInventory().setItem(slot, button.getItem());
                    } else {
                        if (getInventory().getItem(slot) != null) getInventory().setItem(slot, button.getItem());
                    }
                }
            }
        }
    }

    /**
     * Disable the click listener on that slot
     *
     * @param slot The slot
     */
    public void disableClickListenerOn(int slot) {
        if (disabledClickListeners.contains(slot)) return;
        disabledClickListeners.add(slot);
    }

    /**
     * Add a groped button into the inventory
     *
     * @param groupedButton The grouped button
     */
    public void addGroupedButton(GroupedButton groupedButton) {
        groupedButtons.add(groupedButton);
    }

    /**
     * Update the player's inventory (non packet)
     *
     * @param player The target player
     */
    public void updateInventory(Player player) {
        PlayerUtils.updateInventory(player);
    }
}
