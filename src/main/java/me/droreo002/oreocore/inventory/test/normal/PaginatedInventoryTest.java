package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Api test for PaginatedInventory
 *
 * Status [Success]
 */
public class PaginatedInventoryTest extends PaginatedInventory {

    public PaginatedInventoryTest() {
        super(27, "Paginated Inventory");

        setItemRow(0, 1);
        setSearchRow(2, false, null);

        for (int i = 0; i <= 50; i++) addPaginatedButton(new GUIButton(UMaterial.DIRT.getItemStack()));
    }

    @Override
    public void onOpen(InventoryOpenEvent e) {
        getPaginatedButtonResult().forEach(a -> a.forEach(b -> {
            System.out.println("Button on slot " + b.getInventorySlot());
        }));
    }
}
