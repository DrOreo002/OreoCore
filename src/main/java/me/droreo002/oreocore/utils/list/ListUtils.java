package me.droreo002.oreocore.utils.list;

import me.droreo002.oreocore.utils.strings.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public final class ListUtils {

    public static List<String> color(List<String> list) {
        return list.stream().map(StringUtils::color).collect(Collectors.toList());
    }
}
