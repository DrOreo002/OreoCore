package me.droreo002.oreocore.inventory.button;

import lombok.Getter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Represents mostly {@link InventoryClickEvent}
 * but created for {@link ButtonListener} usage
 */
public class ButtonClickEvent implements Cancellable {

    private boolean cancelled;

    @Getter
    private final ClickType click;
    @Getter
    private final InventoryAction action;
    @Getter
    private InventoryType.SlotType slotType;
    @Getter
    private int slot;
    @Getter
    private int rawSlot;
    @Getter
    private ItemStack currentItem;
    @Getter
    private int hotbarButton;
    @Getter
    private Inventory clickedInventory;
    @Getter
    private Inventory inventory;
    @Getter
    private InventoryView view;
    @Getter
    private List<HumanEntity> viewers;
    @Getter
    private HumanEntity whoClicked;

    public ButtonClickEvent(InventoryClickEvent event) {
        this.click = event.getClick();
        this.action = event.getAction();
        this.slotType = event.getSlotType();
        this.slot = event.getSlot();
        this.rawSlot = event.getRawSlot();
        this.currentItem = event.getCurrentItem();
        this.hotbarButton = event.getHotbarButton();
        this.clickedInventory = event.getClickedInventory();
        this.inventory = event.getInventory();
        this.view = event.getView();
        this.viewers = event.getViewers();
        this.whoClicked = event.getWhoClicked();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
