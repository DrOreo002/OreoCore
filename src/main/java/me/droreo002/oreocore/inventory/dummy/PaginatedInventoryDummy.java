package me.droreo002.oreocore.inventory.dummy;

import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.CustomItem;

public class PaginatedInventoryDummy extends PaginatedInventory {

    public PaginatedInventoryDummy() {
        super(27, "Hello");
        setItemSlot(new Integer[] { 1, 2, 3, 4, 5, 6, 7 });
        setSearchRow(1, false, null);

        for (int i = 0; i < 10; i++) {
            final int finalI = i;
            addPaginatedButton(new GUIButton(new CustomItem(XMaterial.DIRT.parseItem(), "&fHello World " + i)).setListener(e -> {
                e.getWhoClicked().sendMessage("hello " + finalI);
                e.getWhoClicked().closeInventory();
            }));
        }
    }
}
