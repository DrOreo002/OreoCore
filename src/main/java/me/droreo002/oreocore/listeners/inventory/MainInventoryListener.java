package me.droreo002.oreocore.listeners.inventory;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class MainInventoryListener implements Listener {

    private OreoCore plugin;

    public MainInventoryListener(OreoCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory inventory = view.getTopInventory();
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();

        if (inventory == null) return; // For < 1.13 version ofc
        if (e.getClickedInventory() == null) return;
        if (item == null || item.getType().equals(UMaterial.AIR.getMaterial())) return;

        if (inventory.getHolder() instanceof OreoInventory) {
            System.out.println("[Super] On click check");
            ((OreoInventory) inventory.getHolder()).onClickHandler(e);
        } else {
            // Check on cache
            OreoInventory opening = plugin.getInventoryCacheManager().getInventory(player);
            if (opening != null) opening.onClickHandler(e);
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
        final Player player = (Player) e.getPlayer();
        if (inventory.getHolder() instanceof OreoInventory) {
            ((OreoInventory) inventory.getHolder()).onCloseHandler(e);
        } else {
            // Check on cache
            OreoInventory opening = plugin.getInventoryCacheManager().getInventory(player);
            if (opening != null) opening.onCloseHandler(e);
            plugin.getInventoryCacheManager().remove(player);
        }
    }
}
