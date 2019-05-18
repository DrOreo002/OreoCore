package me.droreo002.oreocore.inventory.debug;

import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.inventory.api.animation.IButtonFrame;
import me.droreo002.oreocore.inventory.api.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;

import java.util.List;

public class PInventoryAnimationDebug extends PaginatedInventory {

    public PInventoryAnimationDebug() {
        super(27, "Hello World");

        setItemRow(0, 1);
        setSearchRow(2, true, UMaterial.BLACK_STAINED_GLASS_PANE.getItemStack());

        GUIButton button = new GUIButton(new CustomItem(UMaterial.OAK_DOOR.getItemStack(), "Hello "));
        button.setListener(GUIButton.CLOSE_LISTENER);
        for (char c : "World".toCharArray()) {
            button.addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String prev) {
                    return prev + c;
                }

                @Override
                public List<String> nextLore(List<String> prev) {
                    return null;
                }

                @Override
                public ItemMetaType toUpdate() {
                    return ItemMetaType.DISPLAY_NAME;
                }
            });
        }
        button.setRepeatingAnimation(true);

        for (int i = 0; i < 50; i++) {
            if (i == 10) {
                addPaginatedButton(button);
            } else {
                addPaginatedButton(new GUIButton(UMaterial.CARROT_ITEM.getItemStack()));
            }
        }
    }
}
