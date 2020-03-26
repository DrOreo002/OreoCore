package me.droreo002.oreocore.inventory.animation.button;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface IButtonFrame {

    /**
     * Get the next display name
     *
     * @param previousDisplayName : The previous display name
     *
     * @return next display name
     */
    default String nextDisplayName(String previousDisplayName) {
        return null;
    }

    /**
     * Get the next lore
     *
     * @param previousLore : The previous lore
     * @return next lore
     */
    default List<String> nextLore(List<String> previousLore) {
        return null;
    }

    /**
     * Get the next item of this button
     * useful if there's a lot of changes
     *
     * @param previousItem The previous item
     * @return the next item
     */
    default ItemStack nextItem(ItemStack previousItem) {
        return null;
    }

    /**
     * Get the next Material for the button
     *
     * @param previousMaterial The previous material
     * @return the Material, default is null (disabled)
     */
    default Material nextMaterial(Material previousMaterial) {
        return null;
    }

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
     * This will get called when the animation frame
     * is executed
     */
    default void run() {

    }
}
