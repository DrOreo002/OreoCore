package me.droreo002.oreocore.inventory.animation;

import org.bukkit.Material;

import java.util.List;

public interface IButtonFrame {

    /**
     * Get the next display name
     *
     * @param prevDisplayName : The previous display name
     *
     * @return next display name
     */
    default String nextDisplayName(String prevDisplayName) {
        return null;
    }

    /**
     * Get the next lore
     *
     * @param prevLore : The previous lore
     *
     * @return next lore
     */
    default List<String> nextLore(List<String> prevLore) {
        return null;
    }

    /**
     * Get the next Material for the button
     *
     * @return the Material, default is null (disabled)
     */
    default Material nextMaterial() {
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
