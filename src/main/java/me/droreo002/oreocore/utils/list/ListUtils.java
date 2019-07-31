package me.droreo002.oreocore.utils.list;

import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {

    public static final String DEFAULT_SPLIT_MARK = "<end>";

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
        return toString(list, DEFAULT_SPLIT_MARK);
    }

    /**
     * Convert a string into list
     *
     * @param s The string to convert (Must have marker)
     * @return The converted string as list
     */
    public static List<String> toList(String s) {
        return toList(s, DEFAULT_SPLIT_MARK);
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
        for (String s : list) {
            builder.append(s).append(marker);
        }
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
        String[] sp = s.split(marker);
        return new ArrayList<>(Arrays.asList(sp));
    }
}
