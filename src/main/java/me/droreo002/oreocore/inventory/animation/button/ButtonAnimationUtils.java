package me.droreo002.oreocore.inventory.animation.button;

import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.ItemUtils;

import java.util.ArrayList;
import java.util.List;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

public class ButtonAnimationUtils {

    /**
     * Add a wave animation into the button
     *
     * @param button The button
     * @param duplicatesPerFrame The frame duplicates (This can also be used as duration)
     */
    public static void addWaveAnimation(GUIButton button, String waveColor, String waveBaseColor, int duplicatesPerFrame) {
        String displayName = stripColor(ItemUtils.getName(button.getItem(), false));
        button.setItem(new CustomItem(button.getItem(), waveBaseColor + displayName), true, false);
        String[] wave = colorWaveString(displayName,waveColor, waveBaseColor);
        addFrames(button, wave, duplicatesPerFrame, true);
    }

    /**
     * Add a fill animation into the button
     *
     * @param button The button
     * @param fillColor The fill color
     * @param fillBaseColor The fill base color
     * @param duplicatesPerFrame The frame duplicates (This can also be used as duration)
     */
    public static void addFillAnimation(GUIButton button, String fillColor, String fillBaseColor, int duplicatesPerFrame) {
        String displayName = stripColor(ItemUtils.getName(button.getItem(), false));
        button.setItem(new CustomItem(button.getItem(), fillBaseColor + displayName), true, false);
        String[] wave = colorFillString(displayName, fillColor, fillBaseColor);
        addFrames(button, wave, duplicatesPerFrame, true);
    }

    /**
     * Add frames to button
     *
     * @param button The button
     * @param frames The frames to add
     * @param duplicatesPerFrame The frame duplicates (This can also be used as duration)
     * @param repeating Is this a repeating animation?
     */
    public static void addFrames(GUIButton button, String[] frames, int duplicatesPerFrame, boolean repeating) {
        if (!button.isAnimated()) throw new IllegalStateException("Button is not an animated button!");
        ButtonAnimation animation = button.getButtonAnimation();
        animation.getFrames().clear();
        for (String s : frames) {
            if (duplicatesPerFrame > 0) {
                for (int i = 0; i < duplicatesPerFrame; i++) {
                    animation.addFrame(new IButtonFrame() {
                        @Override
                        public String nextDisplayName(String prevDisplayName) {
                            return s;
                        }
                    }, false);
                }
            } else {
                animation.addFrame(new IButtonFrame() {
                    @Override
                    public String nextDisplayName(String prevDisplayName) {
                        return s;
                    }
                }, false);
            }
        }
        animation.setRepeatingAnimation(repeating);
    }

    /**
     * Make a wave animation on string
     *
     * @param text The string to animate
     * @param waveColor The wave color
     * @param stringBaseColor The wave base color or string base color
     * @return the animation as array strings
     */
    public static String[] colorWaveString(String text, String waveColor, String stringBaseColor) {
        final List<String> s = new ArrayList<>();
        text = stripColor(text);
        // Clone the list
        for (int i = 0; i < text.toCharArray().length; i++) {
            s.add(text);
        }

        int slot = 0;
        int charAt = 0;
        while (slot < s.size()) {
            String clone = s.get(slot);
            StringBuilder builder = new StringBuilder(clone);
            builder.deleteCharAt(charAt);

            if (slot == (s.size() - 1)) {
                // Last one
                builder.insert(charAt, "&r" + waveColor + clone.charAt(charAt) + "&r");
            } else {
                builder.insert(charAt, waveColor + clone.charAt(charAt) + stringBaseColor);
            }
            s.set(slot, stringBaseColor + builder.toString());
            charAt++;
            slot++;
        }
        return s.toArray(new String[0]);
    }

    /**
     * Make a color fill string animation
     *
     * @param text The text to animate
     * @param fillColor The fill color
     * @param stringBaseColor Base color
     * @return the animation as array strings
     */
    public static String[] colorFillString(String text, String fillColor, String stringBaseColor) {
        final List<String> s = new ArrayList<>();
        text = stripColor(text);
        // Clone the list
        for (int i = 0; i < text.toCharArray().length; i++) {
            s.add(text);
        }

        // Fill with fillColor
        int slot = 0;
        int charAt = 0;
        int size = s.size();

        while (slot < size) {
            String clone = s.get(slot);
            StringBuilder builder = new StringBuilder(clone);
            builder.deleteCharAt(charAt);
            builder.insert(charAt,  clone.charAt(charAt) + stringBaseColor);
            s.set(slot, fillColor + builder.toString());
            charAt++;
            slot++;
        }

        // Fill with stringBaseColor (will add that little effect into the animation)
        slot = 0;
        charAt = 0;
        while (slot < size) {
            String clone = stripColor(s.get(slot));
            StringBuilder builder = new StringBuilder(clone);
            builder.deleteCharAt(charAt);
            builder.insert(charAt,  clone.charAt(charAt) + fillColor);
            s.add(stringBaseColor + builder.toString());
            charAt++;
            slot++;
        }
        return s.toArray(new String[0]);
    }

    /**
     * Add animation via default value
     *
     * @param button The button
     * @param animation The animation
     */
    public static void addAnimation(GUIButton button, DefaultButtonAnimation animation) {
        switch (animation) {
            case FILL_ANIMATION:
                addFillAnimation(button, "&b&l", "&f&l", 0);
                break;
            case WAVE_ANIMATION:
                addWaveAnimation(button, "&b&l", "&f&l", 0);
                break;
        }
    }
}
