package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import me.droreo002.oreocore.utils.strings.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeStampUtils {

    @Getter
    private String timeStampFormat;
    @Getter
    private final DateFormat dateFormat;

    public TimeStampUtils(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
        this.dateFormat = new SimpleDateFormat(timeStampFormat);
    }

    public TimeStampUtils() {
        this.timeStampFormat = "dd/M/yyyy hh:mm:ss";
        this.dateFormat = new SimpleDateFormat(timeStampFormat);
    }

    /**
     * Add value manually via Clock into the TimeStamp
     *
     * @param data : The TimeStamp that will get edited
     * @param clock : The clock value {@link TimeStampUtils.Clock}
     * @param value : How much to add
     * @return an edited TimeStamp
     */
    public Timestamp add(Timestamp data, Clock clock, int value) {
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
                data = addDays(value, data);
                break;
            default:
                data = addMinute(value, data);
                break;
        }
        return data;
    }

    /**
     * Check if the Clock time has passed
     *
     * @param data : The TimeStamp to check
     * @param clock : The clock time to check {@link TimeStampUtils.Clock}
     * @param value : The value
     * @return true if it passed, false otherwise
     */
    public boolean hasPassed(Timestamp data, Clock clock, int value) {
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
                data = addDays(value, data);
                break;
            default:
                data = addMinute(value, data);
                break;
        }
        return data.before(getCurrentTimestamp());
    }

    /**
     * Get the current TimeStamp, format will be the same as {@see timeStampFormat}
     *
     * @return a new TimeStamp object
     */
    public Timestamp getCurrentTimestamp() {
        return convertStringToTimestamp(new SimpleDateFormat(timeStampFormat).format(new Date()));
    }

    /**
     * Get the current TimeStamp as string
     *
     * @return The Result as a string
     */
    public String getCurrentTimestampString() {
        return new SimpleDateFormat(timeStampFormat).format(new Date());
    }

    /**
     * Convert TimeStamp into string
     *
     * @param time : The TimeStamp object
     * @return The string result
     */
    public String convertTimestampToString(Timestamp time) {
        return new SimpleDateFormat(timeStampFormat).format(time);
    }

    /**
     * Convert that string into TimeStamp, keep in mind that the string must be the same exact format as {@see timeStampdFormat} variable
     * in order to successfully convert it
     *
     * @param str_date : The value format
     * @return TimeStamp if succeeded, null otherwise
     */
    public Timestamp convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat(timeStampFormat);
            // you can change format of date
            Date date = formatter.parse(str_date);
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
     * @param t1 : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public Timestamp addDays(int days, Timestamp t1) {
        Long miliseconds = TimeUnit.DAYS.toMillis(days);
        return new Timestamp(t1.getTime() + miliseconds);
    }

    /**
     * Add second into the TimeStamp
     *
     * @param second : How much to add?
     * @param timestamp : The TimeStamp object that will get edited
     * @return The edited TimeStamp
     */
    public Timestamp addSecond(int second, Timestamp timestamp) {
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
    public Timestamp addMinute(int minute, Timestamp timestamp) {
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
    public Timestamp addHour(int hours, Timestamp timestamp) {
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
    public Timestamp decreaseHour(int hours, Timestamp timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp.getTime());

        cal.add(Calendar.HOUR, -hours);
        return new Timestamp(cal.getTime().getTime());
    }

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
    public String getDifference(Date startDate, Date endDate, boolean addColor, String diffFormat) {
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

        if (addColor) {
            diffFormat = diffFormat
                    .replace("%elapsedDays", String.valueOf(elapsedDays))
                    .replace("%elapsedHours", String.valueOf(elapsedHours))
                    .replace("%elapsedMinutes", String.valueOf(elapsedMinutes))
                    .replace("%elapsedSeconds", String.valueOf(elapsedSeconds));
            return StringUtils.color(diffFormat);
        } else {
            diffFormat = diffFormat
                    .replace("%elapsedDays", String.valueOf(elapsedDays))
                    .replace("%elapsedHours", String.valueOf(elapsedHours))
                    .replace("%elapsedMinutes", String.valueOf(elapsedMinutes))
                    .replace("%elapsedSeconds", String.valueOf(elapsedSeconds));
            return diffFormat;
        }
    }

    public enum Clock {
        SECOND,
        MINUTE,
        HOUR,
        DAY
    }
}
