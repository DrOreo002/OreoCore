package me.droreo002.oreocore.utils.list;

import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {

    public static List<String> color(List<String> list) {
        return list.stream().map(StringUtils::color).collect(Collectors.toList());
    }

    public static List<String> strip(List<String> list) {
        return list.stream().map(ChatColor::stripColor).collect(Collectors.toList());
    }
}
