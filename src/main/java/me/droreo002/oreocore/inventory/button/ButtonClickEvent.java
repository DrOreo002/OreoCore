package me.droreo002.oreocore.inventory.button;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

/**
 * Same event as {@link InventoryClickEvent}
 * we extend it because we want to have other variable
 * to handle cancelling the event or not. Because we want
 * to always cancel the base event {@link InventoryClickEvent#isCancelled()}
 */
public class ButtonClickEvent extends InventoryClickEvent {

    @Getter @Setter
    private boolean buttonClickEventCancelled;

    public ButtonClickEvent(@NotNull InventoryView view, InventoryType.@NotNull SlotType type, int slot, @NotNull ClickType click, @NotNull InventoryAction action) {
        super(view, type, slot, click, action);
        this.buttonClickEventCancelled = false;
    }
}
