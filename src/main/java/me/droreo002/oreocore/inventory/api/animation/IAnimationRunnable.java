package me.droreo002.oreocore.inventory.api.animation;

import lombok.Getter;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Set;

public class IAnimationRunnable implements Runnable {

    @Getter
    private final Set<GUIButton> buttons;
    @Getter
    private final Inventory inventory;

    public IAnimationRunnable(Set<GUIButton> buttons, Inventory inventory) {
        this.buttons = buttons;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        for (GUIButton button : buttons) {
            final int slot = button.getInventorySlot();

            if (button.isAnimated()) {
                final IButtonFrame frm = button.nextFrame();
                if (frm == null) continue;
                final boolean hasMeta = button.getItem().hasItemMeta();
                final ItemMeta meta = button.getItem().getItemMeta();

                switch (frm.toUpdate()) {
                    case DISPLAY_NAME:
                        if (hasMeta && meta.hasDisplayName()) {
                            button.setItem(new CustomItem(button.getItem(), frm.nextDisplayName(meta.getDisplayName())));
                        } else {
                            button.setItem(new CustomItem(button.getItem(), frm.nextDisplayName("")));
                        }
                        break;
                    case LORE:
                        if (hasMeta && meta.hasLore()) {
                            button.setItem(new CustomItem(button.getItem(), frm.nextLore(meta.getLore())));
                        } else {
                            button.setItem(new CustomItem(button.getItem(), frm.nextLore(new ArrayList<>())));
                        }
                        break;
                    case DISPLAY_AND_LORE:
                        if (hasMeta && meta.hasDisplayName()) {
                            button.setItem(new CustomItem(button.getItem(), frm.nextDisplayName(meta.getDisplayName())));
                        } else {
                            button.setItem(new CustomItem(button.getItem(), frm.nextDisplayName("")));
                        }

                        if (hasMeta && meta.hasLore()) {
                            button.setItem(new CustomItem(button.getItem(), frm.nextLore(meta.getLore())));
                        } else {
                            button.setItem(new CustomItem(button.getItem(), frm.nextLore(new ArrayList<>())));
                        }

                        if (hasMeta && meta.hasDisplayName() && meta.hasLore()) {
                            button.setItem(new CustomItem(button.getItem(), frm.nextDisplayName(meta.getDisplayName()), frm.nextLore(meta.getLore())));
                        }
                        break;
                }
            }

            inventory.setItem(slot, button.getItem()); // Update the item
        }
        inventory.getViewers().forEach(humanEntity -> ((Player) humanEntity).updateInventory());
    }
}
