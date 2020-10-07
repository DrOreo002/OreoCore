package me.droreo002.oreocore.inventory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.animation.InventoryAnimationManager;
import me.droreo002.oreocore.inventory.animation.open.DiagonalFill;
import me.droreo002.oreocore.inventory.animation.open.ItemFill;
import me.droreo002.oreocore.inventory.animation.open.ItemWave;
import me.droreo002.oreocore.inventory.animation.open.OpenAnimation;
import me.droreo002.oreocore.inventory.animation.open.OpenAnimationType;
import me.droreo002.oreocore.inventory.button.ButtonClickEvent;
import me.droreo002.oreocore.inventory.button.ButtonListener;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.bridge.XSound;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.complex.XMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public abstract class OreoInventory implements InventoryHolder {

    @Getter
    private final int size;
    private Inventory inventory;
    @Getter
    private String title;
    @Getter
    @Setter
    private InventoryType inventoryType;
    @Getter
    @Setter
    private InventoryTemplate inventoryTemplate;
    @Getter
    @Setter
    private List<GUIButton> buttons;
    @Getter
    @Setter
    private List<Integer> disabledClickListeners;
    @Getter
    @Setter
    private InventoryAnimationManager inventoryAnimationManager;
    @Getter
    @Setter
    private SoundObject soundOnClick, soundOnOpen, soundOnClose;
    @Getter
    @Setter
    private boolean shouldProcessButtonClickEvent;

    public OreoInventory(int size, String title) {
        this.size = size;
        this.title = color(title);
        this.inventoryTemplate = null;
        this.inventoryType = InventoryType.CHEST;
        setupDefault();
    }

    public OreoInventory(InventoryTemplate template) {
        this.inventoryTemplate = template;
        this.size = template.getSize();
        this.title = color(template.getTitle());
        this.inventoryType = template.getInventoryType();

        if (!template.getOpenAnimationName().equals("none")) {
            if (this.inventoryAnimationManager == null)
                this.inventoryAnimationManager = InventoryAnimationManager.getDefault();
            switch (OpenAnimationType.valueOf(template.getOpenAnimationName())) {
                case DIAGONAL_FILL_ANIMATION:
                    inventoryAnimationManager.setOpenAnimation(new DiagonalFill());
                    break;
                case ITEM_FILL_ANIMATION:
                    inventoryAnimationManager.setOpenAnimation(new ItemFill(new SoundObject(XSound.ENTITY_ITEM_PICKUP), new SoundObject(XSound.BLOCK_ANVIL_FALL)));
                    break;
                case ITEM_WAVE_ANIMATION:
                    inventoryAnimationManager.setOpenAnimation(new ItemWave(XMaterial.DIAMOND_PICKAXE.getItemStack(), XMaterial.STONE.getItemStack(), new SoundObject(XSound.BLOCK_STONE_BREAK)));
                    break;
            }
        }
        setupDefault();
    }

    public OreoInventory(int size) {
        this.size = size;
        this.title = "Inventory";
        this.inventoryTemplate = null;
        this.inventoryType = InventoryType.CHEST;

        setupDefault();
    }

    /**
     * Process button listener by default
     *
     * @param button Clicked button
     * @param e      Click event
     */
    public static void processButtonListener(GUIButton button, InventoryClickEvent e) {
        ButtonClickEvent event = new ButtonClickEvent(e.getView(), e.getSlotType(), e.getSlot(), e.getClick(), e.getAction());
        List<ButtonListener> loadedListeners = button.getButtonListeners().stream().filter(listener ->
                Arrays.asList(listener.getAllowedClickType()).contains(e.getClick())).collect(Collectors.toList());
        for (ButtonListener listener : loadedListeners) {
            if (event.isButtonClickEventCancelled()) break;
            listener.onClick(event);
        }
    }

    private void setupDefault() {
        this.buttons = new ArrayList<>();
        this.disabledClickListeners = new ArrayList<>();
        this.inventory = Bukkit.createInventory(this, size, title);
        this.soundOnClick = new SoundObject(XSound.UI_BUTTON_CLICK);
        this.soundOnClose = new SoundObject(XSound.BLOCK_CHEST_CLOSE);
        this.soundOnOpen = new SoundObject(XSound.BLOCK_CHEST_OPEN);
        this.shouldProcessButtonClickEvent = true;
    }

    /**
     * Set the inventory title (Will also update the inventory)
     *
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = color(title);
        if (inventoryType != InventoryType.CHEST) {
            this.inventory = Bukkit.createInventory(this, inventoryType, title);
        } else {
            this.inventory = Bukkit.createInventory(this, this.size, this.title);
        }
    }

    /**
     * The click handler for the inventory (Universal)
     *
     * @param e : The click event object
     */
    public void onClickHandler(InventoryClickEvent e) {
        InventoryCacheManager cacheManager = OreoCore.getInstance().getInventoryCacheManager();
        InventoryView view = e.getView();
        Inventory inventory = view.getTopInventory();
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();

        OreoInventory oreoInventory = cacheManager.getInventory(player);
        if (oreoInventory == null) {
            oreoInventory = (OreoInventory) inventory.getHolder();
            if (oreoInventory == null) return;
        }

        e.setCancelled(!oreoInventory.getDisabledClickListeners().contains(slot));
        GUIButton button = oreoInventory.getButton(slot);
        if (button != null) {
            if (oreoInventory.isShouldProcessButtonClickEvent()) {
                processButtonListener(button, e);
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
        final InventoryCacheManager cacheManager = OreoCore.getInstance().getInventoryCacheManager();
        final Player player = (Player) e.getPlayer();
        final Inventory inventory = e.getInventory();

        /*
        Simple anti cheat or duplication bug here
        might work, not sure.
         */
        if (!ServerUtils.isLegacyVersion()) {
            if (!player.getItemOnCursor().getType().equals(XMaterial.AIR.getMaterial()) && player.isSneaking()) {
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
        } else {
            if (player.getItemOnCursor() != null && player.isSneaking()) { // Anti cheat
                player.setItemOnCursor(new ItemStack(Material.AIR));
            }
        }

        OreoInventory oreoInventory = cacheManager.getInventory(player);
        if (oreoInventory == null) {
            oreoInventory = (OreoInventory) inventory.getHolder();
            if (oreoInventory == null) return;
        }

        oreoInventory.onClose(e);
        if (oreoInventory.getSoundOnClose() != null) oreoInventory.getSoundOnClose().send(player);
        final InventoryAnimationManager inventoryAnimationManager = oreoInventory.getInventoryAnimationManager();

        if (inventoryAnimationManager != null) {
            inventoryAnimationManager.stopAnimation();
            if (inventoryAnimationManager.isOpenAnimationRunning())
                inventoryAnimationManager.getOpenAnimation().stop(false);
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
        InventoryAnimationManager inventoryAnimationManager = oreoInventory.getInventoryAnimationManager();
        if (inventoryAnimationManager != null) {
            if (inventoryAnimationManager.getOpenAnimation() != null) {
                inventoryAnimationManager.getOpenAnimation().start(aVoid -> inventoryAnimationManager.startAnimation(oreoInventory));
            } else {
                inventoryAnimationManager.startAnimation(oreoInventory);
            }
        }
    }

    /**
     * Called when drag event is called
     *
     * @param e The event
     */
    public void onDrag(InventoryDragEvent e) {
    }

    /**
     * Called when click event is called
     *
     * @param e The click event object
     */
    public void onClick(InventoryClickEvent e) {
    }

    /**
     * Called when close event is called
     *
     * @param e The event
     */
    public void onClose(InventoryCloseEvent e) {
    }

    /**
     * Called when the open event is called
     *
     * @param e The event
     */
    public void onOpen(InventoryOpenEvent e) {
    }

    /**
     * Called when move item event is called
     * this will only call if {@link InventoryMoveItemEvent#getDestination()} is this instance
     *
     * @param e The event
     */
    public void onMove(InventoryMoveItemEvent e) {
    }

    /**
     * Called before {@link OreoInventory#setup()} get called
     *
     * @return true to continue setup, false otherwise
     */
    public boolean onPreSetup() {
        return true;
    }

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
     * Close player's inventory with sound
     *
     * @param player     The target player
     * @param closeSound The sound to play
     */
    public void closeInventory(Player player, SoundObject closeSound) {
        PlayerUtils.closeInventory(player);
        if (closeSound != null) closeSound.send(player);
    }

    /**
     * Open the inventory
     *
     * @param player    The target player
     * @param inventory The inventory to open
     */
    public void openInventory(Player player, Inventory inventory) {
        if (ServerUtils.isOldAsFuckVersion()) {
            player.openInventory(inventory);
        } else {
            PlayerUtils.openInventory(player, inventory);
        }
    }

    /**
     * Open the inventory with sound
     *
     * @param player    The target player
     * @param inventory The inventory to open
     * @param openSound The sound to play
     */
    public void openInventory(Player player, Inventory inventory, SoundObject openSound) {
        PlayerUtils.openInventory(player, inventory);
        if (openSound != null) openSound.send(player);
    }

    /**
     * Refresh this inventory
     * will affect the button only
     */
    public void refresh() {
        setup();
        InventoryUtils.updateInventoryViewer(getInventory());
    }

    /**
     * Open the custom inventory
     *
     * @param player The target player
     */
    public void open(Player player) {
        if (onPreSetup()) {
            setup();
            // Some basic setup with animations
            if (getInventoryAnimationManager() != null) {
                OpenAnimation openAnimation = getInventoryAnimationManager().getOpenAnimation();
                if (openAnimation != null) {
                    openAnimation.setInventory(getInventory());
                }
            }
            /*
             * Apparently custom inventory holder
             * in mc version greater than 1.12 will no longer work
             * so we use Inventory caching manager to make this work
             */
            if (inventoryType != InventoryType.CHEST && !ServerUtils.isLegacyVersion()) {
                OreoCore.getInstance().getInventoryCacheManager().add(player, this);
            }
            openInventory(player, getInventory());
        }
    }

    /**
     * Add a button into the inventory (Will be updated when inventory is opened)
     *
     * @param guiButton The button to add
     * @param replace   Should we replace if it exists already?
     */
    public void addButton(GUIButton guiButton, boolean replace) {
        addButton(guiButton, replace, false);
    }

    /**
     * Add a button into the inventory
     *
     * @param guiButton    The button to add
     * @param replace      Should we replace if it exists already?
     * @param directUpdate Should we directly set this button to inventory?
     */
    public void addButton(GUIButton guiButton, boolean replace, boolean directUpdate) {
        Validate.notNull(guiButton, "Button cannot be null!");
        if (replace) {
            if (isHasButton(guiButton.getInventorySlot())) {
                removeButton(guiButton.getInventorySlot());
            }
        } else {
            if (isHasButton(guiButton.getInventorySlot())) return;
        }
        getButtons().add(guiButton);
        if (directUpdate) getInventory().setItem(guiButton.getInventorySlot(), guiButton.getItem());
    }

    /**
     * Force set button, will force inventory to update
     *
     * @param guiButton The button to add
     */
    public void forceAddButton(GUIButton guiButton) {
        Validate.notNull(guiButton, "Button cannot be null!");
        addButton(guiButton, true);
        inventory.setItem(guiButton.getInventorySlot(), guiButton.getItem());
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
     * @param row     The row
     * @param border  The border item
     * @param replace Should we replace item on the border line?
     */
    public void addBorder(ItemStack border, boolean replace, int row) {
        addBorder(border, replace, new int[]{row});
    }

    /**
     * Add a border
     *
     * @param rows    The rows to add
     * @param border  the border item
     * @param replace Should we replace item on the border line?
     */
    public void addBorder(ItemStack border, boolean replace, int... rows) {
        for (int row : rows) {
            if (row < 0) throw new IllegalStateException("Row cannot be less than 0!");
            for (int i = row * 9; i < (row * 9) + 9; i++) {
                addButton(new GUIButton(border, i), replace);
            }
        }
    }

    /**
     * Re setup the inventory
     */
    public void clear() {
        if (this instanceof Linkable) {
            Linkable linkable = (Linkable) this;
            if (linkable.getLinkedButtons() != null) linkable.getLinkedButtons().clear();
        }
        for (int i = 0; i < size; i++) {
            getInventory().setItem(i, XMaterial.AIR.getItemStack());
        }
        this.buttons.clear();
    }

    /**
     * Setup the inventory
     */
    public void setup() {
        final InventoryTemplate template = getInventoryTemplate();

        if (template != null) {
            this.inventoryType = template.getInventoryType();
            // There's a changes
            if (!this.title.equals(color(template.getTitle()))) {
                // Other than size is always a non customize able size inventory!
                if (inventoryType != InventoryType.CHEST) {
                    this.inventory = Bukkit.createInventory(this, inventoryType, template.getTitle());
                } else {
                    this.inventory = Bukkit.createInventory(this, size, template.getTitle());
                }
            }

            // We ignore the already added button
            template.getAllGUIButtons().forEach(b -> addButton(b, false));

            if (template.getOpenSound() != null) setSoundOnOpen(template.getOpenSound());
            if (template.getCloseSound() != null) setSoundOnClose(template.getCloseSound());
            if (template.getClickSound() != null) setSoundOnClick(template.getClickSound());
        }

        if (inventoryType != InventoryType.CHEST) {
            this.inventory = Bukkit.createInventory(this, inventoryType, title);
        }

        getButtons().forEach(guiButton -> {
            try {
                getInventory().setItem(guiButton.getInventorySlot(), guiButton.getItem());
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
        });
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
     * Update the player's inventory (non packet)
     *
     * @param player The target player
     */
    public void updateInventory(Player player) {
        PlayerUtils.updateInventory(player);
    }
}
