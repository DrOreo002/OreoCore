package me.droreo002.oreocore.utils.strings;

import me.droreo002.oreocore.enums.Currency;
import me.droreo002.oreocore.utils.misc.MathUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final char[] HEX_MAP = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static final Map<Currency, String> DEFAULT_CURRENCY = new HashMap<>();

    static {
        DEFAULT_CURRENCY.put(Currency.THOUSANDS, "k");
        DEFAULT_CURRENCY.put(Currency.MILLIONS, "M");
        DEFAULT_CURRENCY.put(Currency.BILLIONS, "B");
        DEFAULT_CURRENCY.put(Currency.TRILLIONS, "T");
    }

    /**
     * Colorize the string
     *
     * @param text : The string to colorize
     * @return the colorized string
     */
    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Check if the string is an UUID
     *
     * @param toCheck : The string to check
     * @return true if its UUID false otherwise
     */
    public static boolean isUUID(String toCheck) {
        try {
            UUID.fromString(toCheck); // Declare to remove that stupid yellow warning (IntelliJ)
        } catch (IllegalStateException e) {
            return false;
        }
        return true;
    }

    /**
     * Strip the string's color
     *
     * @param text : The string to strip
     * @return the stripped string
     */
    public static String stripColor(String text) {
        return ChatColor.stripColor(color(text)); // Try to color first
    }

    /**
     * Uppercase the first letter of the string
     *
     * @param str : The string to modify
     * @return the upper cased string (First letter)
     */
    public static String upperCaseFirstLetter(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Get the hex string from char
     *
     * @param data : The char data
     * @return result string
     */
    public static String hexStr(char data) {
        StringBuilder builder = new StringBuilder("  ");
        builder.setCharAt(0, HEX_MAP[(data & 0xF0) >> 4]);
        builder.setCharAt(1, HEX_MAP[(data & 0x0F)]);
        return builder.toString();
    }

    /**
     * Generate a new RID
     *
     * @return The RID
     */
    public static String generateRid() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            String rand = Integer.toString(MathUtils.random(0, 9, false));
            builder.append(hexStr(rand.charAt(0)));
        }
        return builder.toString().toUpperCase();
    }

    /**
     * Join the string together
     *
     * @param elements : The string elements
     * @param separator : The string separator
     * @param startIndex : Start index of elements
     * @param endIndex : End index of elements
     *
     * @return the joined string
     */
    public static String join(String[] elements, String separator, int startIndex, int endIndex) {
        Validate.isTrue(startIndex >= 0 && startIndex < elements.length, "startIndex out of bounds");
        Validate.isTrue(endIndex >= 0 && endIndex <= elements.length, "endIndex out of bounds");
        Validate.isTrue(startIndex <= endIndex, "startIndex lower than endIndex");

        StringBuilder result = new StringBuilder();

        while (startIndex < endIndex) {
            if (result.length() != 0) {
                result.append(separator);
            }

            if (elements[startIndex] != null) {
                result.append(elements[startIndex]);
            }
            startIndex++;
        }

        return result.toString();
    }

    /**
     * Check if the string has a special character inside
     *
     * @param text : The string to check
     * @return true if it has a special character, false otherwise
     */
    public static boolean hasSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.find();
    }

    /**
     * Format the long value to readAble
     * @param value : The long value
     * @return readable format. Example : 1000 to 1k
     */
    public static String formatToReadable(long value, Map<Currency, String> format) {
        if (format.size() < 3) throw new IllegalStateException("Please add all currency format to the map!"); // Not all of Currency is set
        final NavigableMap<Long, String> suffixes = new TreeMap<>();
        suffixes.put(1_000L, format.get(Currency.THOUSANDS));
        suffixes.put(1_000_000L, format.get(Currency.MILLIONS));
        suffixes.put(1_000_000_000L, format.get(Currency.BILLIONS));
        suffixes.put(1_000_000_000_000L, format.get(Currency.TRILLIONS));
        
        if (value == Long.MIN_VALUE) return formatToReadable(Long.MIN_VALUE + 1, format);
        if (value < 0) return "-" + formatToReadable(-value, format);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}
