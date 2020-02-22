package me.droreo002.oreocore.bossbar;

import org.bukkit.boss.BarColor;

public interface BBAnimationFrame {

    /**
     * Get the progress of this animation frame
     *
     * @return The progress
     */
    double getProgress();

    /**
     * Get the title of this animation frame
     *
     * @return The title
     */
    String getTitle();

    /**
     * Get the color of this animation frame
     *
     * @return The color
     */
    BarColor getColor();
}
