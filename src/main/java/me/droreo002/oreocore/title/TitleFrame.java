package me.droreo002.oreocore.title;

import me.droreo002.oreocore.utils.misc.SoundObject;

public interface TitleFrame {

    /**
     * Called when the frame is
     * executed
     */
    default void run() {}

    /**
     * Get the current frame sound
     *
     * @return The sound
     */
    default SoundObject getSound() {
        return null;
    }

    /**
     * Get the next title frame
     *
     * @param prevTitle The previous title
     * @return The next title
     */
    default String getNextTitle(String prevTitle) {
        return prevTitle;
    }

    /**
     * Get the next sub title
     *
     * @param prevSubTitle The previous sub title
     * @return The next sub title
     */
    default String getNextSubTitle(String prevSubTitle) {
        return prevSubTitle;
    }
}
