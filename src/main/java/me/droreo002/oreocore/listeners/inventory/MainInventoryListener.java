package me.droreo002.oreocore.listeners.inventory;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class MainInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory inventory = view.getTopInventory();
        ItemStack item = e.getCurrentItem();

        if (inventory == null) return; // For < 1.13 version ofc
        if (e.getClickedInventory() == null) return;
        if (item == null || item.getType().equals(UMaterial.AIR.getMaterial())) return;

        if (inventory.getHolder() instanceof OreoInventory) {
            ((OreoInventory) inventory.getHolder()).onClickHandler(e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onOpen(InventoryOpenEvent e) {
        final Inventory inventory = e.getInventory();
        if (inventory.getHolder() instanceof OreoInventory) {
            ((OreoInventory) inventory.getHolder()).onOpenHandler(e);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClose(InventoryCloseEvent e) {
        final Inventory inventory = e.getInventory();
        if (inventory.getHolder() instanceof OreoInventory) {
            ((OreoInventory) inventory.getHolder()).onCloseHandler(e);
        }
    }
}
