package me.droreo002.oreocore.inventory.animation.button;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A button animation registry
 */
public final class ButtonAnimationRegistry {

    private static final Map<String, Class<? extends ButtonAnimation>> REGISTERED = new HashMap<>();

    static {
        // Initialize default
        registerAnimation(ButtonAnimationType.FILL_ANIMATION.name(), DisplayNameFill.class);
        registerAnimation(ButtonAnimationType.WAVE_ANIMATION.name(), DisplayNameWave.class);
    }

    /**
     * Register a new animation
     *
     * @param animationName The animation name
     * @param animationClazz The button animation class
     */
    public static void registerAnimation(String animationName, Class<? extends ButtonAnimation> animationClazz) {
        if (animationName.equalsIgnoreCase(ButtonAnimation.UNSPECIFIC)) throw new NullPointerException("Cannot register an Unspecific animation!");
        REGISTERED.put(animationName, animationClazz);
    }

    /**
     * Get a ButtonAnimation by that name
     *
     * @param animationName The animation name
     * @return ButtonAnimation if there's any
     */
    @Nullable
    @SneakyThrows
    public static ButtonAnimation getAnimation(String animationName) {
        Class<? extends ButtonAnimation> clazz = REGISTERED.get(animationName);
        if (clazz == null) return null;
        return (ButtonAnimation) clazz.getDeclaredMethod("build").invoke(null);
    }
}
