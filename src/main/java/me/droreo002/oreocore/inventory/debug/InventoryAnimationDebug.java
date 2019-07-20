package me.droreo002.oreocore.inventory.debug;

import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.animation.IButtonFrame;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;

import java.util.List;

public class InventoryAnimationDebug extends CustomInventory {

    public InventoryAnimationDebug() {
        super(9, "Animation Debug");
        GUIButton button = new GUIButton(new CustomItem(UMaterial.OAK_DOOR.getItemStack(), "Hello "));
        button.setListener(GUIButton.CLOSE_LISTENER);
        for (char c : "World".toCharArray()) {
            button.addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String prev) {
                    return prev + c;
                }
            });
        }
        setAnimationUpdateTime(5L);
        button.setRepeatingAnimation(true);
        addButton(button, true);
    }
}
