package me.droreo002.oreocore.utils.time;

import lombok.Getter;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import static me.droreo002.oreocore.utils.time.TimestampBuilder.Clock;
import static me.droreo002.oreocore.utils.time.TimestampUtils.*;

public class SimplifiedTime {

    public static final String DEFAULT_FORMAT = "%d% %h% %m% %s%";

    @Getter
    private final String timeFormat;
    @Getter
    private int day, hour, minute, second;
    @Getter
    private String simplifiedResult;

    public SimplifiedTime(String timeFormat) {
        this.timeFormat = timeFormat;
        update();
    }

    public SimplifiedTime(String timeFormat, Timestamp timestamp) {
        this.timeFormat = timeFormat;
        this.day = getTime(Clock.DAY, timestamp);
        this.hour = getTime(Clock.HOUR, timestamp);
        this.minute = getTime(Clock.MINUTE, timestamp);
        this.second = getTime(Clock.SECOND, timestamp);
        update();
    }

    /**
     * Add a time
     *
     * @param val The value to add
     * @param clock The clock
     */
    public void addTime(int val, Clock clock) {
        switch (clock) {
            case SECOND:
                second += val;
                break;
            case MINUTE:
                minute += val;
                break;
            case HOUR:
                hour += val;
                break;
            case DAY:
                day += val;
                break;
        }
        update();
    }

    /**
     * Decrease time
     *
     * @param val The value to decrease
     * @param clock The clock
     */
    public void decreaseTime(int val, Clock clock) {
        switch (clock) {
            case SECOND:
                if (second <= 0) return;
                this.second -= val;
                break;
            case MINUTE:
                if (minute <= 0) return;
                this.minute -= val;
                break;
            case HOUR:
                if (hour <= 0) return;
                this.hour -= val;
                break;
            case DAY:
                if (day <= 0) return;
                this.day -= val;
                break;
        }
        update();
    }

    private void update() {
        // Validate time
        if (second > 60) {
            second = 0;
            minute++;
        }
        if (minute > 60) {
            minute = 0;
            hour++;
        }
        if (hour > 24) {
            hour = 0;
            day++;
        }


        // Format the time
        this.simplifiedResult = timeFormat
                .replace("%h%", String.valueOf(hour))
                .replace("%s%", String.valueOf(second))
                .replace("%m%", String.valueOf(minute))
                .replace("%d%", String.valueOf(day));
    }

    /**
     * Get the custom date, as a long
     *
     * @param future if the date is in the future, as opposed to the past
     * @return Long date
     */
    public long getDate(boolean future) {
        return DateParser.parseDate(simplifiedResult, future);
    }
}
