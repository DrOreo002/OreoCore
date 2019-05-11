package me.droreo002.oreocore.utils.list;

import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {

    public static final String END_LINE = "</end>";

    public static List<String> color(List<String> list) {
        return list.stream().map(StringUtils::color).collect(Collectors.toList());
    }

    public static List<String> strip(List<String> list) {
        return list.stream().map(ChatColor::stripColor).collect(Collectors.toList());
    }

    public static String toString(List<String> list) {
        final StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append(END_LINE);
        }
        return builder.toString();
    }

    public static List<String> toList(String s) {
        if (!s.contains(END_LINE)) return new ArrayList<>();
        String[] sp = s.split(END_LINE);
        return new ArrayList<>(Arrays.asList(sp));
    }
}
