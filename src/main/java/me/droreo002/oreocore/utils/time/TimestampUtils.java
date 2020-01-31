package me.droreo002.oreocore.utils.time;

import me.droreo002.oreocore.utils.strings.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class TimestampUtils {

    /**
     * Get the difference of the dates
     *
     * @param startDate Start date
     * @param endDate End date
     * @param diffFormat The diff format message
     * @return The difference format string
     */
    public static String getDifference(Date startDate, Date endDate, String diffFormat) {
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        diffFormat = diffFormat
                .replace("%dd%", String.valueOf(elapsedDays))
                .replace("%hh%", String.valueOf(elapsedHours))
                .replace("%mm%", String.valueOf(elapsedMinutes))
                .replace("%ss%", String.valueOf(elapsedSeconds))
                // Or
                .replace("%d%", String.valueOf(elapsedDays))
                .replace("%h%", String.valueOf(elapsedHours))
                .replace("%m%", String.valueOf(elapsedMinutes))
                .replace("%s%", String.valueOf(elapsedSeconds));

        return StringUtils.color(diffFormat);
    }

    /**
     * Check if the {@param data}'s specified clock time has passed {@param second}
     *
     * @param data The TimeStamp to check
     * @param clock The clock time to check {@link TimestampBuilder.Clock}
     * @param value The value
     * @param second The second Timestamp to compare
     * @return true if it passed, false otherwise
     */
    public static boolean hasPassed(Timestamp data, Timestamp second, TimestampBuilder.Clock clock, int value) {
        switch (clock) {
            case SECOND:
                second = addSecond(value, second);
                break;
            case MINUTE:
                second = addMinute(value, second);
                break;
            case HOUR:
                second = addHour(value, second);
                break;
            case DAY:
                second = addDay(value, second);
                break;
        }
        return data.after(second);
    }

    /**
     * Convert TimeStamp into string
     *
     * @param time : The TimeStamp object
     * @return The string result
     */
    public String convertTimestampToString(Timestamp time, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(time);
    }

    /**
     * Convert that string into TimeStamp
     *
     * @param dateFormat The date format
     * @param timestampString The Timestamp string
     * @return TimeStamp if succeeded, null otherwise
     */
    public static Timestamp convertStringToTimestamp(String timestampString, DateFormat dateFormat) {
        try {
            // you can change format of date
            Date date = dateFormat.parse(timestampString);
            return new Timestamp(date.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Add days into the TimeStamp
     *
     * @param days : How much to add?
     * @param timestamp : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public static Timestamp addDay(int days, Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        cal.add(Calendar.DAY_OF_WEEK, days);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Add second into the TimeStamp
     *
     * @param second : How much to add?
     * @param timestamp : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public static Timestamp addSecond(int second, Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        cal.add(Calendar.SECOND, second);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Add minute into the TimeStamp
     *
     * @param minute : How much to add?
     * @param timestamp : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public static Timestamp addMinute(int minute, Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        cal.add(Calendar.MINUTE, minute);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Add hour into the TimeStamp
     *
     * @param hours : How much to add?
     * @param timestamp : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public static Timestamp addHour(int hours, Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        cal.add(Calendar.HOUR, hours);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Decrease hour from the TimeStamp
     *
     * @param hours : How much to decrease?
     * @param timestamp : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public static Timestamp decreaseHour(int hours, Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        cal.add(Calendar.HOUR, -hours);
        return new Timestamp(cal.getTime().getTime());
    }

    /**
     * Get the time
     *
     * @param clock The time to get
     * @param timestamp The target Timestamp
     * @return the time value
     */
    public static int getTime(TimestampBuilder.Clock clock, Timestamp timestamp) {
        Date date = new Date(timestamp.getTime());
        SimpleDateFormat format = null;
        switch (clock) {
            case SECOND:
                format = new SimpleDateFormat("ss");
                break;
            case MINUTE:
                format = new SimpleDateFormat("mm");
                break;
            case HOUR:
                format = new SimpleDateFormat("HH");
                break;
            case DAY:
                format = new SimpleDateFormat("dd");
                break;
        }
        return Integer.parseInt(format.format(date));
    }

    /**
     * Make a new TimestampBuilder from seconds
     *
     * @param timeFormat The time format
     * @param seconds The seconds
     * @return the TimestampBuilder
     */
    public static TimestampBuilder fromSeconds(String timeFormat, int seconds) {
        TimestampBuilder builder = TimestampBuilder.builder(timeFormat);
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        int hour = Math.toIntExact(TimeUnit.SECONDS.toHours(seconds) - (day * 24));
        int minute = Math.toIntExact(TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60));
        int second = Math.toIntExact(TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60));

        return builder
                .addTime(day, TimestampBuilder.Clock.DAY)
                .addTime(hour, TimestampBuilder.Clock.HOUR)
                .addTime(minute, TimestampBuilder.Clock.MINUTE)
                .addTime(second, TimestampBuilder.Clock.SECOND);
    }
}
