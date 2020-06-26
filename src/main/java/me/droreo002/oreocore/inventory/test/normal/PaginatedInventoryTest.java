package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.XMaterial;

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
        setInformationButton(new GUIButton(ItemStackBuilder.of(XMaterial.BOOK.getMaterial())));
        setInformationButtonSlot(24);

        for (int i = 0; i <= 50; i++) addPaginatedButton(new GUIButton(XMaterial.DIRT.getItemStack()));
    }
}
