package me.droreo002.oreocore.inventory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.droreo002.oreocore.inventory.animation.InventoryAnimation;
import me.droreo002.oreocore.inventory.animation.open.FillAnimation;
import me.droreo002.oreocore.inventory.animation.open.OpenAnimation;
import me.droreo002.oreocore.inventory.animation.open.WaveAnimation;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.button.GroupedButton;
import me.droreo002.oreocore.inventory.linked.LinkedButton;
import me.droreo002.oreocore.utils.bridge.OSound;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
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

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public abstract class OreoInventory implements InventoryHolder {

    private Inventory inventory;

    @Getter
    private final int size;
    @Getter
    private InventoryTemplate inventoryTemplate;
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
        this.title = color(title);
        this.inventoryTemplate = null;
        setupDefault();
    }

    public OreoInventory(InventoryTemplate template) {
        this.size = template.getSize();
        this.title = color(template.getTitle());
        this.inventoryTemplate = template;

        if (!template.getOpenAnimationName().equals("none")) {
            InventoryAnimation.InventoryAnimationBuilder animation = InventoryAnimation.builder();
            switch (template.getOpenAnimationName().toLowerCase()) {
                case "fillanimation":
                    animation.openAnimation(new FillAnimation(new SoundObject(OSound.ENTITY_ITEM_PICKUP), new SoundObject(OSound.BLOCK_ANVIL_FALL)));
                    break;
                case "waveanimation":
                    animation.openAnimation(new WaveAnimation(UMaterial.DIAMOND_PICKAXE.getItemStack(), UMaterial.STONE.getItemStack(), new SoundObject(OSound.BLOCK_STONE_BREAK)));
                    break;
            }
            setInventoryAnimation(animation.build());
        }
        setupDefault();
    }

    public OreoInventory(int size) {
        this.size = size;
        this.title = "Inventory";
        this.inventoryTemplate = null;
        setupDefault();
    }

    private void setupDefault() {
        this.buttons = new ArrayList<>();
        this.disabledClickListeners = new ArrayList<>();
        this.groupedButtons = new ArrayList<>();
        this.inventory = Bukkit.createInventory(this, size, title);
        this.soundOnClick = new SoundObject(OSound.UI_BUTTON_CLICK);
        this.soundOnClose = new SoundObject(OSound.BLOCK_CHEST_CLOSE);
        this.soundOnOpen = new SoundObject(OSound.BLOCK_CHEST_OPEN);
        this.shouldProcessButtonClickEvent = true;
    }

    /**
     * Set the inventory title (Will also update the inventory)
     *
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = color(title);
        this.inventory = Bukkit.createInventory(this, this.size, this.title);
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

        e.setCancelled(!oreoInventory.getDisabledClickListeners().contains(slot));
        GUIButton button = oreoInventory.getButton(slot);
        if (button != null) {
            if (oreoInventory.isShouldProcessButtonClickEvent()) {
                if (button instanceof LinkedButton) {
                    LinkedButton linkedButton = (LinkedButton) button;
                    List<GUIButton.ButtonListener> loadedListeners = linkedButton.getButtonListeners().get(e.getClick());
                    if (loadedListeners != null) loadedListeners.forEach(buttonListener -> buttonListener.onClick(e));
                    return;
                }
                if (button.getListener() != null) button.getListener().onClick(e);
            }
        }
        if (oreoInventory.getSoundOnClick() != null) oreoInventory.getSoundOnClick().send(player);
        oreoInventory.onClick(e); // After process we call
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
            } else {
                inventoryAnimation.startAnimation(oreoInventory);
            }
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
        // Some basic setup with animations
        if (getInventoryAnimation() != null) {
            OpenAnimation openAnimation = getInventoryAnimation().getOpenAnimation();
            if (openAnimation != null) {
                openAnimation.setInventory(getInventory());
            }
        }
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
            if (isHasButton(guiButton.getInventorySlot())) return;
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
        final InventoryTemplate template = getInventoryTemplate();
        if (template != null) {
            buttons.addAll(template.getAllGUIButtons());
        }

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
     * Update the template
     *
     * @param newTemplate The new / updated template
     */
    public void updateTemplate(InventoryTemplate newTemplate) {
        this.inventoryTemplate = newTemplate;
        buttons.clear();
        if (inventoryTemplate != null) buttons.addAll(inventoryTemplate.getAllGUIButtons());
        getButtons().forEach(guiButton -> getInventory().setItem(guiButton.getInventorySlot(), guiButton.getItem()));
        InventoryUtils.updateInventoryViewer(getInventory());
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
