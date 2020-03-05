package me.droreo002.oreocore.utils.list;

import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {

    public static final String DEFAULT_LIST_MARK = "<l>";
    public static final String SPLIT_MARK = "<br>";

    /**
     * Colorize the list
     *
     * @param list The list of string
     * @return Colorized list
     */
    public static List<String> color(List<String> list) {
        return list.stream().map(StringUtils::color).collect(Collectors.toList());
    }

    /**
     * Strip the list
     *
     * @param list The list of string
     * @return Stripped color list
     */
    public static List<String> strip(List<String> list) {
        return list.stream().map(ChatColor::stripColor).collect(Collectors.toList());
    }

    /**
     * Convert list to a string
     *
     * @param list The list
     * @return The converted list as string
     */
    public static String toString(List<String> list) {
        return toString(list, DEFAULT_LIST_MARK);
    }

    /**
     * Convert a string into list
     *
     * @param s The string to convert (Must have marker)
     * @return The converted string as list
     */
    public static List<String> toList(String s) {
        return toList(s, DEFAULT_LIST_MARK);
    }

    /**
     * Check if the string is a serialized list
     * will only work if string has DEFAULT_SPLIT_MARK
     *
     * @param s The string to check
     * @return true if serialized, false otherwise
     */
    public static boolean isSerializedList(String s) {
        return s.contains(DEFAULT_LIST_MARK);
    }

    /**
     * Get the serialized list inside the string
     *
     * @param s The source string
     * @return the serialized string
     */
    public static String getSerializedString(String s)  {
        return getSerializedString(s, DEFAULT_LIST_MARK);
    }

    /**
     * Get the serialized list inside the string
     *
     * @param s The source string
     * @param marker The list mark
     * @return the serialized string
     */
    public static String getSerializedString(String s, String marker) {
        if (!s.contains(marker)) return "";

        int lastIndex = s.lastIndexOf(marker);
        int firstIndex = s.indexOf(marker);
        return s.substring(
                (firstIndex == -1) ? 0 : firstIndex,
                (lastIndex == -1) ? s.length() : lastIndex) + marker;
    }

    /**
     * Convert list to a string
     *
     * @param list The list
     * @param marker The marker or string separator
     * @return The converted list as string
     */
    public static String toString(List<String> list, String marker) {
        final StringBuilder builder = new StringBuilder();
        builder.append(marker); // Add marker at the first line of string. To mark it as a list
        for (String s : list) {
            builder.append(s).append(SPLIT_MARK);
        }
        builder.append(marker); // Also at the end of the line
        return builder.toString();
    }

    /**
     * Convert a string into list
     *
     * @param s The string to convert (Must have marker)
     * @param marker The marker or string separator
     * @return The converted string as list
     */
    public static List<String> toList(String s, String marker) {
        if (!s.contains(marker)) return new ArrayList<>();
        s = getSerializedString(s, marker).replace(marker, "");

        String[] sp = s.split(SPLIT_MARK);
        return new ArrayList<>(Arrays.asList(sp));
    }

    /**
     * Convert the array into an ArrayList
     *
     * @param array The array to convert
     * @param <T> Object type
     * @return List
     */
    @NotNull
    public static <T> List<T> convertArrayToList(@NotNull T[] array) {
        // create a list from the Array
        return Arrays
                .stream(array)
                .collect(Collectors.toList());
    }
}
