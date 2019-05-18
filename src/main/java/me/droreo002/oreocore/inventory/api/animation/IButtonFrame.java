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
     * Get that kind of ItemMeta to update
     *
     * @return the item meta to update
     */
    ItemMetaType toUpdate();
}
