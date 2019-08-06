package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.CustomInventory;
import me.droreo002.oreocore.inventory.animation.ButtonAnimation;
import me.droreo002.oreocore.inventory.animation.InventoryAnimation;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.animation.IButtonFrame;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;

/**
 * Animation test for CustomInventory
 *
 * Status [Success]
 */
public class CInventoryAnimationTest extends CustomInventory {

    public CInventoryAnimationTest() {
        super(9, "Animation ODebug");
        GUIButton button = new GUIButton(new CustomItem(UMaterial.OAK_DOOR.getItemStack(), "Hello "));
        button.setListener(GUIButton.CLOSE_LISTENER);
        button.setAnimated(true);

        ButtonAnimation animation = button.getButtonAnimation();
        for (char c : "World".toCharArray()) {
            animation.addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String prev) {
                    return prev + c;
                }
            }, true);
        }
        setInventoryAnimation(InventoryAnimation.builder().build()); // Default value
        animation.setRepeatingAnimation(true);
        addButton(button, true);
    }
}
