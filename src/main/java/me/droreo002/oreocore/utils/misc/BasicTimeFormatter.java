package me.droreo002.oreocore.utils.misc;

public class BasicTimeFormatter {

    /**
     * Format time in seconds with default values
     *
     * @param seconds The time in seconds
     * @return Formatted string
     */
    public static String formatSeconds(int seconds) {
        return formatSeconds(seconds, "s", "m", "h", "d");
    }

    /**
     * Format time in seconds
     *
     * @param seconds The time in seconds
     * @param secondSymbol Second symbol
     * @param minuteSymbol Minute symbol
     * @param hourSymbol Hour symbol
     * @param daySymbol Day symbol
     * @return Formatted String
     */
    public static String formatSeconds(int seconds, String secondSymbol, String minuteSymbol, String hourSymbol, String daySymbol) {
        if (seconds < 60) {
            return seconds + secondSymbol;
        }
        int minutes = seconds / 60;
        int s = 60 * minutes;
        int secondsLeft = seconds - s;
        if (minutes < 60) {
            if (secondsLeft > 0) {
                return minutes + minuteSymbol + " " + secondsLeft + secondSymbol;
            }
            return minutes + minuteSymbol;
        }
        if (minutes < 1440) {
            String time = "";
            int hours = minutes / 60;
            time = hours + hourSymbol;
            int inMins = 60 * hours;
            int leftOver = minutes - inMins;
            if (leftOver >= 1) {
                time = time + " " + leftOver + minuteSymbol;
            }
            if (secondsLeft > 0) {
                time = time + " " + secondsLeft + secondSymbol;
            }
            return time;
        }
        String time = "";
        int days = minutes / 1440;
        time = days + daySymbol;
        int inMins = 1440 * days;
        int leftOver = minutes - inMins;
        if (leftOver >= 1) {
            if (leftOver < 60) {
                time = time + " " + leftOver + minuteSymbol;
            } else {
                int hours = leftOver / 60;
                time = time + " " + hours + hourSymbol;
                int hoursInMins = 60 * hours;
                int minsLeft = leftOver - hoursInMins;
                time = time + " " + minsLeft + minuteSymbol;
            }
        }
        if (secondsLeft > 0) {
            time = time + " " + secondsLeft + secondSymbol;
        }
        return time;
    }
}
