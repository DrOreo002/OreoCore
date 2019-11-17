package me.droreo002.oreocore.utils.time;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DateParser {
    private static final Pattern TIME_PATTERN = Pattern.compile(Stream.of("y", "mo", "w", "d", "h", "m").map((i) -> "(?:([0-9]+)\\s*" + i + "[a-z]*[,\\s]*)?").collect(Collectors.joining()) + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
    private static final int MAX_YEARS = 100000;

    private DateParser() { }

    public static long parseDate(String time, boolean future) throws IllegalArgumentException {
        Matcher matcher = TIME_PATTERN.matcher(time);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        boolean found = false;

        while(matcher.find()) {
            if (matcher.group() != null && !matcher.group().isEmpty()) {
                for(int i = 0; i < matcher.groupCount(); ++i) {
                    if (matcher.group(i) != null && !matcher.group(i).isEmpty()) {
                        found = true;
                        break;
                    }
                }

                if (found) {
                    if (matcher.group(1) != null && !matcher.group(1).isEmpty()) {
                        years = Integer.parseInt(matcher.group(1));
                    }

                    if (matcher.group(2) != null && !matcher.group(2).isEmpty()) {
                        months = Integer.parseInt(matcher.group(2));
                    }

                    if (matcher.group(3) != null && !matcher.group(3).isEmpty()) {
                        weeks = Integer.parseInt(matcher.group(3));
                    }

                    if (matcher.group(4) != null && !matcher.group(4).isEmpty()) {
                        days = Integer.parseInt(matcher.group(4));
                    }

                    if (matcher.group(5) != null && !matcher.group(5).isEmpty()) {
                        hours = Integer.parseInt(matcher.group(5));
                    }

                    if (matcher.group(6) != null && !matcher.group(6).isEmpty()) {
                        minutes = Integer.parseInt(matcher.group(6));
                    }

                    if (matcher.group(7) != null && !matcher.group(7).isEmpty()) {
                        seconds = Integer.parseInt(matcher.group(7));
                    }
                    break;
                }
            }
        }

        if (!found) {
            throw new IllegalArgumentException();
        } else {
            Calendar c = new GregorianCalendar();
            if (years > 0) {
                if (years > 100000) {
                    years = 100000;
                }

                c.add(1, years * (future ? 1 : -1));
            }

            if (months > 0) {
                c.add(2, months * (future ? 1 : -1));
            }

            if (weeks > 0) {
                c.add(3, weeks * (future ? 1 : -1));
            }

            if (days > 0) {
                c.add(5, days * (future ? 1 : -1));
            }

            if (hours > 0) {
                c.add(11, hours * (future ? 1 : -1));
            }

            if (minutes > 0) {
                c.add(12, minutes * (future ? 1 : -1));
            }

            if (seconds > 0) {
                c.add(13, seconds * (future ? 1 : -1));
            }

            Calendar max = new GregorianCalendar();
            max.add(1, 10);
            return c.after(max) ? max.getTimeInMillis() / 1000L + 1L : c.getTimeInMillis() / 1000L + 1L;
        }
    }
}
