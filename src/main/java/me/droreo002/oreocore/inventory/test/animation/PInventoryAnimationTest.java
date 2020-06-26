package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.animation.button.ButtonAnimationManager;
import me.droreo002.oreocore.inventory.animation.InventoryAnimationManager;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.animation.button.IButtonFrame;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.XMaterial;

/**
 * Animation test for PaginatedInventory
 */
public class PInventoryAnimationTest extends PaginatedInventory {

    public PInventoryAnimationTest() {
        super(27, "Hello World");

        setItemRow(0, 1);
        setSearchRow(2, false, null);

        GUIButton button = new GUIButton(ItemStackBuilder.of(XMaterial.OAK_DOOR.getItemStack()).setDisplayName("Hello ").getItemStack());
        button.addListener(GUIButton.CLOSE_LISTENER);
        ButtonAnimationManager animationManager = new ButtonAnimationManager(button);
        animationManager.addFirstState();
        animationManager.getButtonAnimation().setRepeating(true);
        for (char c : "World".toCharArray()) {
            animationManager.getButtonAnimation().addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String previousDisplayName) {
                    return previousDisplayName + c;
                }
            });
        }
        button.setButtonAnimationManager(animationManager);

        setInventoryAnimationManager(InventoryAnimationManager.getDefault()); // Default value
        for (int i = 0; i < 50; i++) {
            if (i == 10) {
                addPaginatedButton(button);
            } else {
                addPaginatedButton(new GUIButton(XMaterial.CARROT.getItemStack()));
            }
        }
    }
}
