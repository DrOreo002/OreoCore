package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.InventoryTemplate;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;

public class InventoryTemplateTest extends PaginatedInventory {

    public InventoryTemplateTest(InventoryTemplate template) {
        super(template);

        // INFO: Test case for paginated buttons
//        for (int i = 0; i < 30; i++) {
//            addPaginatedButton(new GUIButton(new CustomItem(UMaterial.DIRT.getItemStack(), "&f" + i)));
//        }
    }
}
