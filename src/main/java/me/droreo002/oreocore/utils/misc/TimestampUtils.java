package me.droreo002.oreocore.utils.misc;

import me.droreo002.oreocore.utils.strings.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class TimestampUtils {

    /**
     * Get the difference of the dates
     *
     * @param startDate : Start date
     * @param endDate : End date
     * @param addColor : Should we add color to diff format message?
     * @param diffFormat : The diff format message. This also contains placeholder, which is
     *                   %elapsedDays
     *                   %elapsedHours
     *                   %elapsedMinutes
     *                   %elapsedSeconds
     * @return The difference format string
     */
    public static String getDifference(Date startDate, Date endDate, boolean addColor, String diffFormat) {
        //milliseconds
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
                .replace("%elapsedDays", String.valueOf(elapsedDays))
                .replace("%elapsedHours", String.valueOf(elapsedHours))
                .replace("%elapsedMinutes", String.valueOf(elapsedMinutes))
                .replace("%elapsedSeconds", String.valueOf(elapsedSeconds));

        if (addColor) diffFormat = StringUtils.color(diffFormat);
        return diffFormat;
    }

    /**
     * Check if the Clock time has passed
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
                data = addSecond(value, data);
                break;
            case MINUTE:
                data = addMinute(value, data);
                break;
            case HOUR:
                data = addHour(value, data);
                break;
            case DAY:
                data = addDay(value, data);
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
}
