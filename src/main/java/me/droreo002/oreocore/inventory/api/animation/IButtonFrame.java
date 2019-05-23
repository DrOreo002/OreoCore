package me.droreo002.oreocore.inventory.api.animation;

import me.droreo002.oreocore.utils.item.helper.ItemMetaType;

import java.util.List;

public interface IButtonFrame {

    /**
     * Get the next display name
     *
     * @param prevDisplayName : The previous display name
     *
     * @return next display name
     */
    String nextDisplayName(String prevDisplayName);

    /**
     * Get the next lore
     *
     * @param prevLore : The previous lore
     *
     * @return next lore
     */
    List<String> nextLore(List<String> prevLore);

    /**
     * The next frame speed, basically will change the animation speed
     * default will be -1L (Disabled)
     *
     * @return the next frame speed
     */
    default long getNextFrameUpdateSpeed() {
        return -1L;
    }

    /**
     * Get that kind of ItemMeta to update
     *
     * @return the item meta to update
     */
    ItemMetaType toUpdate();
}
