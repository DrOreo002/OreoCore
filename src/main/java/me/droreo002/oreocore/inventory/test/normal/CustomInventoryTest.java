package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.CustomInventory;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.complex.UMaterial;

/**
 * Simple api test for CustomInventory
 *
 * Status [Success]
 */
public class CustomInventoryTest extends CustomInventory {

    public CustomInventoryTest() {
        super(27, "CustomInventory");
        addButton(new GUIButton(UMaterial.DIRT.getItemStack(), 5).setListener(e -> System.out.println("Ouch!")), true);
    }
}
