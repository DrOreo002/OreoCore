package me.droreo002.oreocore.utils.inventory;

import org.bukkit.inventory.Inventory;

public final class InventoryUtils {

    public static boolean isInventoryFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }
}
