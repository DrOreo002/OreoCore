package me.droreo002.oreocore.inventory;

import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.button.GroupedButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.inventory.ItemStack;

public class CustomInventory extends OreoInventory {

    public CustomInventory(int size, String title) {
        super(size, title);
    }

    @Override
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
