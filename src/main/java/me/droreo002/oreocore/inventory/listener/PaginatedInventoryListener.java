package me.droreo002.oreocore.inventory.listener;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.paginated.PaginatedInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaginatedInventoryListener implements Listener {

    private OreoCore main;

    public PaginatedInventoryListener(OreoCore main) {
        this.main = main;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory inventory = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        ItemStack item = e.getCurrentItem();

        if (item == null || inventory == null) return;
        if (inventory.getHolder() instanceof PaginatedInventory) {
            if (inventory.getType().equals(InventoryType.PLAYER)) {
                e.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, player::closeInventory, 1L);
                return;
            }
            if (!main.getOpening().containsKey(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, player::closeInventory, 1L);
                Bukkit.getLogger().warning("Player (" + player.getName() + ") is interacting on a paginated inventory. But there's no player with that unique id on the opening list!. Inventory is now force closed!");
                return;
            }
            e.setCancelled(true);
            PaginatedInventory pagi = main.getOpening().get(player.getUniqueId());
            if (pagi.getClickSound() != null) {
                pagi.getClickSound().send(player);
            }

            pagi.onClick(e);

            // Paginated button listener
            if (!pagi.getPaginatedButton().isEmpty()) {
                // Convert (For paginated button)
                Map<Integer, GUIButton> buttons = new HashMap<>();
                List<GUIButton> list = pagi.getButtons().get(pagi.getCurrentPage());
                int currSlot = 0;
                for (int i : pagi.getItemSlot()) {
                    GUIButton b;
                    try {
                        b = list.get(currSlot);
                    } catch (IndexOutOfBoundsException e1) {
                        currSlot++;
                        continue;
                    }
                    buttons.put(i, b);
                    currSlot++;
                }

                if (buttons.containsKey(slot)) {
                    GUIButton.ButtonListener lis = buttons.get(slot).getListener();
                    if (lis != null) {
                        lis.onClick(e);
                    }
                }
            }

            // Check for normal button
            if (pagi.isHasButton(slot)) {
                GUIButton.ButtonListener lis = (pagi.getButton(slot) == null) ? null : pagi.getButton(slot).getListener();
                if (lis != null) {
                    lis.onClick(e);
                }
            }
        }
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        Inventory inventory = e.getInventory();
        if (inventory.getHolder() instanceof PaginatedInventory) {
            if (!main.getOpening().containsKey(player.getUniqueId())) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, player::closeInventory, 1L);
                Bukkit.getLogger().warning("Player (" + player.getName() + ") is opening a paginated inventory. But there's no player with that unique id on the opening list!. Inventory is now force closed!");
                return;
            }
            PaginatedInventory pagi = main.getOpening().get(player.getUniqueId());
            pagi.getOpenSound().send(player);
            pagi.onOpen(e);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inventory = e.getInventory();
        Player player = (Player) e.getPlayer();
        if (inventory.getHolder() instanceof PaginatedInventory) {
            if (!main.getOpening().containsKey(player.getUniqueId())) {
                Bukkit.getLogger().warning("Player (" + player.getName() + ") is closing a paginated inventory. But there's no player with that unique id on the opening list!. Inventory is now force closed!");
                return;
            }
            PaginatedInventory pagi = main.getOpening().get(player.getUniqueId());
            pagi.getCloseSound().send(player);
            main.getOpening().remove(player.getUniqueId());
            pagi.onClose(e);
        }
    }
}
