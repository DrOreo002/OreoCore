package me.droreo002.oreocore.inventory.listener;

import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.animation.ItemAnimationManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory inven = view.getTopInventory();
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        if (inven == null) return;
        if (e.getClickedInventory() == null) return;
        if (item == null || item.getType().equals(Material.AIR)) return;

        if (inven.getHolder() instanceof CustomInventory) {
            CustomInventory custom = (CustomInventory) inven.getHolder();

            if (custom.onPreClick(e)) {
                e.setCancelled(true);
                return;
            }

            if (e.getClickedInventory().getType().equals(InventoryType.PLAYER)) {
                if (custom.isCancelPlayerInventoryClickEvent()) e.setCancelled(true);
                return;
            }

            // Cancelling
            if (custom.getNoClickCancel().contains(slot)) {
                e.setCancelled(false);
            } else {
                e.setCancelled(true);
            }

            // Running onClick method
            custom.onClick(e);

            /*
            Button Management
             */
            if (custom.getSoundOnClick() != null) custom.getSoundOnClick().send(player);
            if (custom.isShouldProcessButton()) {
                if (custom.getButtonMap().containsKey(slot)) {
                    GUIButton but = custom.getButtonMap().get(slot);
                    if (but.getListener() != null) {
                        if (but.getSoundOnClick() != null) {
                            but.getSoundOnClick().send(player);
                        }
                        but.getListener().onClick(e);
                    }
                }

                // Animation button
                if (custom.isContainsAnimation()) {
                    if (custom.getAnimationButtonMap().containsKey(slot)) {
                        if (custom.getAnimationButtonMap().get(slot).getListener() != null) {
                            custom.getAnimationButtonMap().get(slot).getListener().onClick(e);
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOpen(InventoryOpenEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory) {
            CustomInventory custom = (CustomInventory) e.getInventory().getHolder();
            custom.onOpen(e);

            if (custom.getSoundOnOpen() != null) {
                custom.getSoundOnOpen().send((Player) e.getPlayer());
            }
            if (custom.isContainsAnimation()) ItemAnimationManager.registerAnimation(custom);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent e) {
        if (e.getInventory().getHolder() instanceof CustomInventory) {
            Player player = (Player) e.getPlayer();
            if (XMaterial.isNewVersion()) {
                if (!player.getItemOnCursor().getType().equals(XMaterial.AIR.parseMaterial()) && player.isSneaking()) { // Anti cheat
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                }
            } else {
                if (player.getItemOnCursor() != null && player.isSneaking()) { // Anti cheat
                    player.setItemOnCursor(new ItemStack(Material.AIR));
                }
            }
            CustomInventory custom = (CustomInventory) e.getInventory().getHolder();
            custom.onClose(e);

            if (custom.getSoundOnClose() != null) {
                custom.getSoundOnClose().send(player);
            }
            if (custom.isContainsAnimation()) ItemAnimationManager.stopAnimation(custom);
        }
    }
}
