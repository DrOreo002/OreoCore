package me.droreo002.oreocore.utils.strings;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    @Language("RegExp")
    public static final String SURROUNDED_WITH_BRACKETS = "\\(.*?\\)";
    @Language("RegExp")
    public static final String SURROUNDED_WITH_SQUARE_BRACKETS = "\\[.*?]";
    @Language("RegExp")
    public static final String SURROUNDED_WITH_DIAMOND = "<(.*?)>";

    /**
     * Match all regex in that string
     *
     * @param str The string to match on
     * @param regex The regex pattern
     * @return List of string
     */
    @NotNull
    public static List<String> matchAll(String str, @Language("RegExp") String regex) {
        List<String> matches = new ArrayList<>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        while (matcher.find()) matches.add(matcher.group());
        return matches;
    }

    /**
     * Match regex and store the groups as list
     *
     * @param str The string to match
     * @param regex The regex
     * @return List of captured groups
     */
    public static Map<Integer, List<String>> matchGroups(String str, @Language("RegExp") String regex) {
        Map<Integer, List<String>> matches = new HashMap<>();
        Matcher matcher = Pattern.compile(regex).matcher(str);
        int matchSize = 0;
        while (matcher.find()) {
            List<String> groups = new ArrayList<>();
            for (int i = 1; i <= matcher.groupCount(); i++) { // Start at 1 to remove the Full Match
                groups.add(matcher.group(i));
            }
            matches.put(matchSize, groups);
            matchSize++;
        }
        return matches;
    }

    /**
     * Check any match string using regex
     * this will clean your shit up by 3 lines of code
     *
     * @param str The string to check
     * @param regex The regex str
     * @return Boolean duh
     */
    public static boolean anyMatch(String str, @Language("RegExp") String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(str);
        return matcher.matches();
    }
}
