package me.droreo002.oreocore.inventory.animation.button;

import java.util.ArrayList;
import java.util.List;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

public class ButtonAnimationUtils {

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
}
