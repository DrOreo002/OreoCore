package me.droreo002.oreocore.inventory.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InventoryPanel {

    @Getter
    private final String panelId;
    @Getter @Setter
    private HashSet<Integer> slots;
    @Getter @Setter
    private Set<GUIButton> buttons;
    @Getter @Setter
    private boolean shouldOverrideOtherButton;

    public InventoryPanel(String panelId, HashSet<Integer> slots, boolean shouldOverrideOtherButton) {
        this.panelId = panelId;
        this.slots = slots;
        this.buttons = new HashSet<>();
        this.shouldOverrideOtherButton = shouldOverrideOtherButton;
    }

    /**
     * Add item into the panel
     *
     * @param button : The button
     * @param replace : Should w replace if exists?
     */
    public void setButton(GUIButton button, boolean replace) {
        int slot = button.getInventorySlot();
        if (!slots.contains(slot)) throw new IndexOutOfBoundsException("Please don't add item outside of the panel!");
        if (replace) {
            removeButton(slot);
            buttons.add(button);
            return;
        }
        if (isHasButton(slot)) return;
        buttons.add(button);
    }

    /**
     * Add a button into the available slot
     *
     * @param button : The button
     */
    public void addButton(GUIButton button) {
        boolean contains = false;
        for (int i : slots) {
            if (isHasButton(i)) continue;
            setButton(button, true); // Found empty slot
            contains = true;
            break;
        }
        if (!contains) throw new IllegalStateException("Cannot place anymore button into the pane!");
    }

    /**
     * Check if that slot contains button
     *
     * @param slot : The slot to check
     * @return true if contains, false otherwise
     */
    public boolean isHasButton(int slot) {
        return buttons.stream().anyMatch(button -> button.getInventorySlot() == slot);
    }

    /**
     * Remove the button
     *
     * @param slot : The button slot to remove
     */
    public void removeButton(int slot) {
        buttons.removeIf(button -> button.getInventorySlot() == slot);
    }
}
