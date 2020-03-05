package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.animation.button.ButtonAnimation;
import me.droreo002.oreocore.inventory.animation.InventoryAnimation;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.animation.button.IButtonFrame;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.UMaterial;

/**
 * Animation test for PaginatedInventory
 */
public class PInventoryAnimationTest extends PaginatedInventory {

    public PInventoryAnimationTest() {
        super(27, "Hello World");

        setItemRow(0, 1);
        setSearchRow(2, false, null);

        GUIButton button = new GUIButton(ItemStackBuilder.of(UMaterial.OAK_DOOR.getItemStack()).setDisplayName("Hello ").getItemStack());
        button.addListener(GUIButton.CLOSE_LISTENER);

        ButtonAnimation animation = button.getButtonAnimation();
        for (char c : "World".toCharArray()) {
            animation.addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String prev) {
                    return prev + c;
                }
            }, true);
        }
        animation.setRepeatingAnimation(true);
        button.setAnimated(true);
        button.setButtonAnimation(animation);

        setInventoryAnimation(InventoryAnimation.builder().build()); // Default value
        for (int i = 0; i < 50; i++) {
            if (i == 10) {
                addPaginatedButton(button);
            } else {
                addPaginatedButton(new GUIButton(UMaterial.CARROT_ITEM.getItemStack()));
            }
        }
    }
}
