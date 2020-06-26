package me.droreo002.oreocore.inventory.animation;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.animation.button.IButtonFrame;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AnimationHandler implements Runnable {

    @Getter
    private Inventory inventory;
    @Getter
    private OreoInventory oreoInventory;
    @Getter
    private List<Integer> singleButtonRunnable;

    public AnimationHandler(Inventory inventory, OreoInventory oreoInventory) {
        this.oreoInventory = oreoInventory;
        this.inventory = inventory;
        this.singleButtonRunnable = new ArrayList<>();
    }

    @Override
    public void run() {
        for (int i = 0; i < getInventory().getSize(); i++) {
            GUIButton button = getOreoInventory().getButton(i);
            if (button == null) continue;
            int slot = button.getInventorySlot();
            if (singleButtonRunnable.contains(slot)) continue; // Skip this button

            if (button.isAnimated()) {
                final IButtonFrame frm = button.getButtonAnimationManager().getNextFrame();
                if (frm == null) continue;
                update(frm, button);

                if (frm.getNextFrameUpdateSpeed() != -1L) {
                    // Add to single runnable
                    singleButtonRunnable.add(slot);
                    new SingleButtonRunnable(button, inventory).runTaskLater(OreoCore.getInstance(), frm.getNextFrameUpdateSpeed());
                    continue;
                }

                inventory.setItem(slot, button.getItem()); // Update the item
            }
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
            final IButtonFrame frm = button.getButtonAnimationManager().getNextFrame();
            if (frm == null) {
                singleButtonRunnable.remove(slot);
                return; // Return will basically cancel the task
            }
            update(frm, button);

            inventory.setItem(slot, button.getItem()); // Update the item

            if (frm.getNextFrameUpdateSpeed() == -1L) {
                singleButtonRunnable.remove(slot);
                return; // Return will basically cancel the task
            }

            // Run it again
            new SingleButtonRunnable(button, inventory).runTaskLater(OreoCore.getInstance(), frm.getNextFrameUpdateSpeed());
        }
    }

    /**
     * Update the button
     *
     * @param button The button
     */
    private void update(IButtonFrame frm, GUIButton button) {
        ItemStack item = button.getItem().clone();
        frm.run();

        if (frm.nextItem(item) == null) {
            final ItemMeta meta = item.getItemMeta();
            final Material material = frm.nextMaterial(item.getType());
            if (material != null) button.getItem().setType(material);

            String nextDisplayName = frm.nextDisplayName(meta.getDisplayName());
            List<String> nextLore = frm.nextLore(meta.getLore());

            if (nextDisplayName == null) nextDisplayName = meta.getDisplayName();
            if (nextLore == null) nextLore = meta.getLore();
            if (nextLore == null) nextLore = new ArrayList<>();

            // TODO: 26/03/2020 There's a flick problem here. Please check if other reported

            item = ItemStackBuilder.of(item).setDisplayName(nextDisplayName).setLore(nextLore).getItemStack();

            button.setItem(item, true, false);
        } else {
            button.setItem(frm.nextItem(item), true, false);
        }
    }
}
