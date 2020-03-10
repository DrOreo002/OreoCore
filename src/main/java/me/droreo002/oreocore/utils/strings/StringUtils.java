package me.droreo002.oreocore.utils.strings;

import javafx.beans.binding.StringBinding;
import me.droreo002.oreocore.enums.Currency;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.MathUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {

    private static final char[] HEX_MAP = { '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    public static final Map<Currency, String> DEFAULT_CURRENCY = new HashMap<>();
    private static AtomicLong idCounter = new AtomicLong();

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
        } catch (IllegalStateException | IllegalArgumentException e) {
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
     * (Special char is currently other than a to z / A to Z / 0 - 9)
     *
     * @param text : The string to check
     * @return true if it has a special character, false otherwise
     */
    public static boolean hasSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-zA-Z0-9_-]", Pattern.CASE_INSENSITIVE);
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

    /**
     * Create a Human readable ID. This is also Thread save
     *
     * @return : The ID as String
     */
    public static String createID() {
        return String.valueOf(idCounter.getAndIncrement());
    }

    /**
     * Check if the string is a valid url
     *
     * @param string The string to check
     * @return true if url, false otherwise
     */
    public static boolean isUrl(String string) {
        try {
            URL url = new URL(string);
            url.toURI();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * Generate a horizontal loading bar (from left to right)
     *
     * @param max The max value of the bar
     * @param current The current value of the bar
     * @param baseLoadingColor The loading bar base color
     * @param loadingColor The loading bar progress color
     * @param loadedChar The character to use when loaded
     * @param defaultChar The character to use when not loaded
     * @param loadingBarFormat The loading bar format, also has a placeholder, check code
     * @return The loading bar
     */
    public static String generateLoadingBar(double current, double max, ChatColor baseLoadingColor,
                                            ChatColor loadingColor, char loadedChar, char defaultChar, String loadingBarFormat) {
        if (!loadingBarFormat.contains("%loadingBar%")) throw new IllegalStateException("Invalid loading bar format!");
        TextPlaceholder placeholder = TextPlaceholder
                .of("%percentage%", new DecimalFormat("0.#").format(MathUtils.getPercentage(current, max)) + "%")
                .add("%current%", (int) current)
                .add("%max%", (int) max);

        String loadedCharIcon = loadingColor.toString() + loadedChar;
        int maxBarSize = 20; // Default

        int loading = (int) Math.floor(Math.abs(Math.round(((100 * current) / max) / 10)) * 2); // Round and absolute it. We don't want negative value
        String barString = new String(new char[maxBarSize]).replace('\0', defaultChar);
        StringBuilder barDone = new StringBuilder();
        for (int i = 0; i < loading; i++) {
            barDone.append(loadedCharIcon);
        }
        String resultBar = color(barDone + baseLoadingColor.toString() + barString.substring(loading));
        return placeholder.add("%loadingBar%", resultBar + ChatColor.RESET).format(StringUtils.color(loadingBarFormat));
    }

    /**
     * Beautify a straight line string
     *
     * @param oneLineStr The string
     * @param wordSpace The word max spacing before newline
     * @return The beautified string
     */
    public static List<String> beautify(String oneLineStr, int wordSpace) {
        final List<String> ret = new ArrayList<>();
        StringBuilder b = new StringBuilder();

        int spaceFound = 0;
        for (char c : oneLineStr.toCharArray()) {
            if (c == ' ') spaceFound++;
            if (spaceFound == wordSpace) {
                ret.add(b.toString());
                b = new StringBuilder();
                spaceFound = 0;
            }
            b.append(c);
        }

        // Add the remaining or the whole string because wordSpace is not reached
        ret.add(b.toString());
        return ret;
    }

    /**
     * Convert integer to roman number
     *
     * @param num The number
     * @return The roman
     */
    public static String toRoman(int num) {
        StringBuilder sb = new StringBuilder();
        int times;
        String[] romans = new String[] { "I", "IV", "V", "IX", "X", "XL", "L",
                "XC", "C", "CD", "D", "CM", "M" };
        int[] ints = new int[] { 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500,
                900, 1000 };
        for (int i = ints.length - 1; i >= 0; i--) {
            times = num / ints[i];
            num %= ints[i];
            while (times > 0) {
                sb.append(romans[i]);
                times--;
            }
        }
        return sb.toString();
    }

    /**
     * Remove the quotes from string
     *
     * @param val The val
     * @return list of removed quotes
     */
    public static List<String> removeQuotes(String val) {
        List<String> result = new ArrayList<>();
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(val);

        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }
}
