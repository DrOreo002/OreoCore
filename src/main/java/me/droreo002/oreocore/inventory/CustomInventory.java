package me.droreo002.oreocore.inventory;

import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.utils.item.CustomItem;
import org.bukkit.inventory.ItemStack;

public class CustomInventory extends OreoInventory implements Linkable {

    public CustomInventory(int size, String title) {
        super(size, title);
    }

    public CustomInventory(InventoryTemplate template) {
        super(template);
    }

    /**
     * Find the item slot
     *
     * @param itemStack : The item to find
     * @return the item slot if found, -1 otherwise
     */
    public int findItemSlot(ItemStack itemStack) {
        for (int i = 0; i < getSize(); i++) {
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
        getButtons().add(button);
    }
}
