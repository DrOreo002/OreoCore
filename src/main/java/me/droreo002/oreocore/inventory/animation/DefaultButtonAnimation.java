package me.droreo002.oreocore.inventory.animation;

import lombok.Getter;

/**
 * The default animations
 */
public enum DefaultButtonAnimation {
    FILL_ANIMATION("fill_animation"),
    WAVE_ANIMATION("wave_animation");

    @Getter
    private String asString;

    DefaultButtonAnimation(String asString) {
        this.asString = asString;
    }

    /**
     * Get default button animation from string
     *
     * @param str The string to check
     * @return The default button animation if matched, null otherwise
     */
    public static DefaultButtonAnimation fromString(String str) {
        for (DefaultButtonAnimation b : DefaultButtonAnimation.values()) {
            if (b.getAsString().equals(str)) {
                return b;
            }
        }
        return null;
    }
}
