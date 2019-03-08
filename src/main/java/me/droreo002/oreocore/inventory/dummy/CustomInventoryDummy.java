package me.droreo002.oreocore.inventory.dummy;

import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class CustomInventoryDummy extends CustomInventory {

    public CustomInventoryDummy() {
        super(27, "Hello World");
        addButton(5, new GUIButton(new ItemStack(Material.DIRT)).setListener(e -> {
            e.getWhoClicked().closeInventory();
        }), true);
    }

    @Override
    public void onClick(InventoryClickEvent e) {

    }

    @Override
    public void onClose(InventoryCloseEvent e) {

    }

    @Override
    public void onOpen(InventoryOpenEvent e) {

    }
}
