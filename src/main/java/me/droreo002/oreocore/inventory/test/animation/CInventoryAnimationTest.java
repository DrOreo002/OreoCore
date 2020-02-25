package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.animation.InventoryAnimation;
import me.droreo002.oreocore.inventory.animation.open.DiagonalFill;

/**
 * Animation test for CustomInventory
 *
 * Status [Success]
 */
public class CInventoryAnimationTest extends OreoInventory {

    public CInventoryAnimationTest() {
        super(54, "Animation ODebug");
        setInventoryAnimation(InventoryAnimation.builder().openAnimation(new DiagonalFill()).build()); // Default value
    }
}
