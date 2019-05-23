package me.droreo002.oreocore.inventory.api.animation;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.helper.OreoInventory;
import me.droreo002.oreocore.utils.item.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IAnimationRunnable implements Runnable {

    @Getter
    private final Set<GUIButton> buttons;
    @Getter
    private final Inventory inventory;
    @Getter
    private final OreoInventory oreoInventory;
    @Getter
    private final List<Integer> singleButtonRunnable;

    public IAnimationRunnable(Set<GUIButton> buttons, Inventory inventory, OreoInventory oreoInventory) {
        this.buttons = buttons;
        this.oreoInventory = oreoInventory;
        this.inventory = inventory;
        this.singleButtonRunnable = new ArrayList<>();
    }

    @Override
    public void run() {
        for (GUIButton button : buttons) {
            final int slot = button.getInventorySlot();
            if (singleButtonRunnable.contains(slot)) continue; // Skip this button

            if (button.isAnimated()) {
                final IButtonFrame frm = button.getNextFrame();
                if (frm == null) continue;
                final boolean hasMeta = button.getItem().hasItemMeta();
                final ItemMeta meta = button.getItem().getItemMeta();
                update(frm, hasMeta, meta, button);

                if (frm.getNextFrameUpdateSpeed() != -1L) {
                    // Add to single runnable
                    singleButtonRunnable.add(slot);
                    new SingleButtonRunnable(button, inventory).runTaskLater(OreoCore.getInstance(), frm.getNextFrameUpdateSpeed());
                    continue;
                }
            }

            inventory.setItem(slot, button.getItem()); // Update the item
        }
    }

    private class SingleButtonRunnable extends BukkitRunnable {

        private GUIButton button;
        private Inventory inventory;

        SingleButtonRunnable(GUIButton button, Inventory inventory) {
            this.button = button;
            this.inventory = inventory;
        }

        @Override
        public void run() {
            final int slot = button.getInventorySlot();
            if (!button.isAnimated()) {
                singleButtonRunnable.remove(slot);
                return; // Return will basically cancel the task
            }
            final IButtonFrame frm = button.getNextFrame();
            if (frm == null) {
                singleButtonRunnable.remove(slot);
                return; // Return will basically cancel the task
            }
            final boolean hasMeta = button.getItem().hasItemMeta();
            final ItemMeta meta = button.getItem().getItemMeta();
            update(frm, hasMeta, meta, button);

            inventory.setItem(slot, button.getItem()); // Update the item

            if (frm.getNextFrameUpdateSpeed() == -1L) {
                singleButtonRunnable.remove(slot);
                return; // Return will basically cancel the task
            }

            // Run it again
            new SingleButtonRunnable(button, inventory).runTaskLater(OreoCore.getInstance(), frm.getNextFrameUpdateSpeed());
        }
    }

    private void update(IButtonFrame frm, boolean hasMeta, ItemMeta meta, GUIButton button) {
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
}
