package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.CustomInventory;
import me.droreo002.oreocore.inventory.animation.button.ButtonAnimation;
import me.droreo002.oreocore.inventory.animation.InventoryAnimation;
import me.droreo002.oreocore.inventory.animation.open.DiagonalFill;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.animation.button.IButtonFrame;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;

/**
 * Animation test for CustomInventory
 *
 * Status [Success]
 */
public class CInventoryAnimationTest extends CustomInventory {

    public CInventoryAnimationTest() {
        super(54, "Animation ODebug");
        setInventoryAnimation(InventoryAnimation.builder().openAnimation(new DiagonalFill()).build()); // Default value
    }
}
