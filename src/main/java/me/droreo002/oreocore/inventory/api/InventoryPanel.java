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
    private Map<Integer, GUIButton> buttons;
    @Getter @Setter
    private boolean shouldOverrideOtherButton;

    public InventoryPanel(String panelId, HashSet<Integer> slots, boolean shouldOverrideOtherButton) {
        this.panelId = panelId;
        this.slots = slots;
        this.buttons = new HashMap<>();
        this.shouldOverrideOtherButton = shouldOverrideOtherButton;
    }

    /**
     * Add item into the panel
     *
     * @param slot : The slot of the item
     * @param button : The button
     * @param replace : Should w replace if exists?
     */
    public void setButton(int slot, GUIButton button, boolean replace) {
        if (!slots.contains(slot)) throw new IndexOutOfBoundsException("Please don't add item outside of the panel!");
        if (replace) {
            buttons.put(slot, button);
            return;
        }
        if (buttons.containsKey(slot)) return;
        buttons.put(slot, button);
    }

    /**
     * Add a button into the available slot
     *
     * @param button : The button
     */
    public void addButton(GUIButton button) {
        boolean contains = false;
        for (int i : slots) {
            if (buttons.containsKey(i)) continue;
            setButton(i, button, true); // Found empty slot
            contains = true;
            break;
        }
        if (!contains) throw new IllegalStateException("Cannot place anymore button into the pane!");
    }
}
