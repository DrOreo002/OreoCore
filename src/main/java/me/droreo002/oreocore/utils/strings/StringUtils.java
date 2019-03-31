package me.droreo002.oreocore.utils.strings;

import me.droreo002.oreocore.utils.misc.MathUtils;
import org.bukkit.ChatColor;

import java.util.UUID;

public final class StringUtils {

    private static final char[] HEX_MAP = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean isUUID(String toCheck) {
        try {
            UUID lel = UUID.fromString(toCheck); // Declare to remove that stupid yellow warning (IntelliJ)
        } catch (IllegalStateException e) {
            return false;
        }
        return true;
    }

    public static String stripColor(String text) {
        return ChatColor.stripColor(color(text)); // Try to color first
    }

    public static String upperCaseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String hexStr(char data) {
        StringBuilder builder = new StringBuilder("  ");
        builder.setCharAt(0, HEX_MAP[(data & 0xF0) >> 4]);
        builder.setCharAt(1, HEX_MAP[(data & 0x0F)]);
        return builder.toString();
    }

    public static String generateRid() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            String rand = Integer.toString(MathUtils.random(0, 9, false));
            builder.append(hexStr(rand.charAt(0)));
        }
        return builder.toString().toUpperCase();
    }
}
