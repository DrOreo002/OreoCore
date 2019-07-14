package me.droreo002.oreocore.inventory.api;

import co.aikar.taskchain.TaskChain;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.inventory.api.animation.IAnimatedInventory;
import me.droreo002.oreocore.inventory.api.animation.IAnimationRunnable;
import me.droreo002.oreocore.inventory.api.animation.open.OpenAnimation;
import me.droreo002.oreocore.inventory.api.helper.OreoInventory;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class CustomInventory implements InventoryHolder, IAnimatedInventory, OreoInventory {

    private Inventory inventory;

    @Getter
    private final Set<GUIButton> buttons;
    @Getter
    private final Map<String, InventoryPanel> panels;
    @Getter
    private int size;
    @Getter
    private String title;
    @Getter
    private boolean containsButtonAnimation, containsOpenAnimation;

    @Getter @Setter
    private int animationUpdateId;
    @Getter @Setter
    private boolean shouldProcessButton, cancelPlayerInventoryClickEvent; // Cancel the click when player clicked his / her inventory?
    @Getter @Setter
    private List<Integer> noClickCancel; // Don't cancel the click event on these slots
    @Getter @Setter
    private SoundObject soundOnClick, soundOnOpen, soundOnClose;
    @Getter @Setter
    private long animationUpdateTime;
    @Getter @Setter
    private boolean keepButtonAnimation;
    @Getter @Setter
    private OpenAnimation openAnimation;

    private int animationId;
    private IAnimationRunnable animationRunnable;

    public CustomInventory(int size, String title) {
        this.buttons = new HashSet<>();
        this.panels = new HashMap<>();
        this.size = size;
        this.title = StringUtils.color(title);
        this.inventory = Bukkit.createInventory(this, size, title);
        this.cancelPlayerInventoryClickEvent = true; // Default are true
        this.shouldProcessButton = true;
        this.keepButtonAnimation = false;
        this.noClickCancel = new ArrayList<>();
        this.soundOnClick = new SoundObject(Sounds.CLICK);
        this.soundOnClose = new SoundObject(Sounds.CHEST_CLOSE);
        this.soundOnOpen = new SoundObject(Sounds.CHEST_OPEN);
        this.animationUpdateTime = 1L;
    }

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
    @Override
    public void closeInventory(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    /**
     * Open an inventory for the player, scheduled 1 tick to prevent duplication glitch
     *
     * @param player : Target player
     * @param inventory : Inventory to open
     */
    @Override
    public void openInventory(Player player, Inventory inventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }

    /**
     * Close the player's inventory, scheduled 1 tick to prevent duplication glitch. This will also play sounds
     *
     * @param player : Target player
     * @param soundWhenClose : The sound that will get played when the inventory closes
     */
    @Override
    public void closeInventory(Player player, SoundObject soundWhenClose) {
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
    @Override
    public void openInventory(Player player, Inventory inventory, SoundObject soundWhenOpen) {
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
    @Override
    public void open(Player player) {
        setup();
        openInventory(player, getInventory());
    }

    /**
     * Setup the inventory
     */
    @Override
    public void setup() {
        for (GUIButton button : buttons) {
            if (button.isAnimated()) containsButtonAnimation = true;
            inventory.setItem(button.getInventorySlot(), button.getItem());
        }

        if (!panels.isEmpty()) {
            for (Map.Entry ent : panels.entrySet()) {
                InventoryPanel panel = (InventoryPanel) ent.getValue();
                if (panel.isShouldOverrideOtherButton()) { // Remove every single thing inside
                    for (int i : panel.getSlots()) {
                        inventory.setItem(i, UMaterial.AIR.getItemStack());
                    }
                }
                for (GUIButton button : panel.getButtons()) {
                    final int slot = button.getInventorySlot();

                    if (panel.isShouldOverrideOtherButton()) {
                        inventory.setItem(slot, button.getItem());
                    } else {
                        if (inventory.getItem(slot) != null) {
                            inventory.setItem(slot, button.getItem());
                        }
                    }
                }
            }
        }

        if (openAnimation != null) containsOpenAnimation = true;
    }

    /**
     * Add a button into the inventory
     *
     * @param button : The button object
     * @param replaceIfExist : Do we need to replace the button if it already exists at that slot?
     */
    @Override
    public void addButton(GUIButton button, boolean replaceIfExist) {
        Validate.notNull(button, "Button cannot be null!");
        if (replaceIfExist) {
            if (isHasButton(button.getInventorySlot())) {
                removeButton(button.getInventorySlot());
                buttons.add(button);
            } else {
                buttons.add(button);
            }
            getInventory().setItem(button.getInventorySlot(), button.getItem());
        } else {
            if (isHasButton(button.getInventorySlot())) {
                throw new IllegalStateException("Slot " + button.getInventorySlot() + " already occupied");
            }
            buttons.add(button);
        }
    }

    /**
     * Check if that slot contains button
     *
     * @param slot : The slot to check
     * @return true if contains, false otherwise
     */
    @Override
    public boolean isHasButton(int slot) {
        return buttons.stream().anyMatch(button -> button.getInventorySlot() == slot);
    }

    /**
     * Remove the button
     *
     * @param slot : The button slot to remove
     */
    @Override
    public void removeButton(int slot) {
        buttons.removeIf(button -> button.getInventorySlot() == slot);
    }

    /**
     * Add a border, will fill the row with the specified item
     *
     * @param row : The row
     * @param border : The item
     * @param replaceIfExist : Replace if there's something on the row?
     */
    @Override
    public void addBorder(int row, ItemStack border, boolean replaceIfExist) {
        if (row < 0) throw new IllegalStateException("Row cannot be 0!");
        for (int i = row * 9; i < (row * 9) + 9; i++) {
            addButton(new GUIButton(border, i).setListener(GUIButton.CLOSE_LISTENER), replaceIfExist);
        }
    }

    @Override
    public GUIButton getButton(int slot) {
        return buttons.stream().filter(but -> but.getInventorySlot() == slot)
                .findFirst().orElse(null);
    }

    /**
     * Open the inventory for the specified player
     *
     * @param player : The targeted player
     * @param delayInSecond : The delay in second before opening the inventory
     */
    @Override
    public void openAsync(Player player, int delayInSecond) {
        Bukkit.getScheduler().runTaskLater(OreoCore.getInstance(), () -> {
            TaskChain<Inventory> chain = ThreadingUtils.makeChain();
            chain.delay(delayInSecond, TimeUnit.SECONDS).asyncFirst(() -> {
                setup();
                return getInventory();
            }).asyncLast(player::openInventory).execute((e, task) -> e.printStackTrace());
        }, 1L);
    }

    /**
     * Open the custom inventory via async way
     *
     * @param player : Target player
     */
    @Override
    public void openAsync(Player player) {
        Bukkit.getScheduler().runTaskLater(OreoCore.getInstance(), () -> {
            TaskChain<Inventory> chain = ThreadingUtils.makeChain();
            chain.asyncFirst(() -> {
                setup();
                return getInventory();
            }).asyncLast(player::openInventory).execute((e, task) -> e.printStackTrace());
        }, 1L);
    }

    /**
     * Add a border, will fill the row with the specified item
     *
     * @param rows : The rows
     * @param border : The item
     * @param replaceIfExist : Replace if there's something on the row?
     */
    @Override
    public void addBorder(int[] rows, ItemStack border, boolean replaceIfExist) {
        for (int row : rows) {
            if (row < 0) throw new IllegalStateException("Row cannot be 0!");
            for (int i = row * 9; i < (row * 9) + 9; i++) {
                addButton(new GUIButton(border, i).setListener(GUIButton.CLOSE_LISTENER), replaceIfExist);
            }
        }
    }

    /**
     * Find the item slot
     *
     * @param itemStack : The item to find
     * @return the item slot if found, -1 otherwise
     */
    public int findItemSlot(ItemStack itemStack) {
        for (int i = 0; i < size; i++) {
            ItemStack toCheck = getInventory().getItem(i);
            if (toCheck == null) continue;
            if (CustomItem.isSimilar(toCheck, itemStack)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Update the button!, also it is really recommended to call updateInventory on the player.
     *
     * @param button : The new button
     */
    public void updateButton(GUIButton button) {
        if (!isHasButton(button.getInventorySlot())) throw new NullPointerException("Cannot update button because no valid button found on slot " + button.getInventorySlot());
        getInventory().setItem(button.getInventorySlot(), button.getItem());
        removeButton(button.getInventorySlot());
        buttons.add(button);
    }

    /**
     * Add a panel into the inventory
     *
     * @param panel : The panel object
     */
    public void addPanel(InventoryPanel panel) {
        for (GUIButton but : panel.getButtons()) {
            addButton(but, panel.isShouldOverrideOtherButton());
        }
        panels.put(panel.getPanelId(), panel);
    }

    @Override
    public void startAnimation() {
        if (animationId != 0) Bukkit.getScheduler().cancelTask(animationId);
        this.animationRunnable = new IAnimationRunnable(buttons, getInventory(), this);
        this.animationId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), animationRunnable, 0L, this.animationUpdateTime).getTaskId();
        this.animationUpdateId = new BukkitRunnable() {
            @Override
            public void run() {
                inventory.getViewers().forEach(humanEntity -> ((Player) humanEntity).updateInventory());
            }
        }.runTaskTimer(OreoCore.getInstance(), 0L, 1L).getTaskId();
    }

    @Override
    public void stopAnimation() {
        if (animationRunnable == null) return;
        Bukkit.getScheduler().cancelTask(animationId);
        Bukkit.getScheduler().cancelTask(animationUpdateId);
        animationRunnable.getSingleButtonRunnable().forEach(Bukkit.getScheduler()::cancelTask);
    }

    @Override
    public IAnimationRunnable getAnimationRunnable() {
        return this.animationRunnable;
    }

    @Override
    public int getAnimationTaskId() {
        return this.animationId;
    }

    @Override
    public void setAnimationTaskId(int newId) {
        this.animationId = newId;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void refreshInventory() {
        for (int slot = 0; slot < size; slot++) {
            if (getInventory().getItem(slot) != null) getInventory().setItem(slot, UMaterial.AIR.getItemStack()); // Remove the whole shit
        }
        setup();

        stopAnimation();
        for (HumanEntity ent : inventory.getViewers()) {
            if (ent == null) continue;
            if (!(ent instanceof Player)) continue;
            Player player = (Player) ent;
            player.updateInventory();
        }
        startAnimation();
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }

    @Override
    public void onOpen(InventoryOpenEvent e) {

    }
}
