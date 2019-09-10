package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static me.droreo002.oreocore.utils.misc.TimestampUtils.*;

public class TimestampBuilder {

    public static final String DEFAULT_FORMAT = "dd/MM/yyyy hh:mm:ss";

    @Getter @Setter
    private Timestamp timestamp;
    @Getter
    private String timeStampFormat;
    @Getter
    private DateFormat dateFormat;

    /**
     * Start the Timestamp builder
     *
     * @param format The date format
     * @return the TimestampBuilder
     */
    public static TimestampBuilder builder(String format) {
        return new TimestampBuilder(format);
    }

    /**
     * Build the Timestamp
     *
     * @return The time stamp
     */
    public Timestamp build() {
        return timestamp;
    }

    /**
     * Build the Timestamp builder as string
     *
     * @return The string formatted
     */
    public String buildAsString() {
        return dateFormat.format(timestamp);
    }

    /**
     * Construct Timestamp builder
     *
     * @param timeStampFormat The format
     */
    private TimestampBuilder(String timeStampFormat) {
        this.timeStampFormat = timeStampFormat;
        this.dateFormat = new SimpleDateFormat(timeStampFormat);
        this.timestamp = convertStringToTimestamp(dateFormat.format(new Date()), dateFormat);
    }

    /**
     * Add value manually via Clock into the TimeStamp
     *
     * @param data : The TimeStamp that will get edited
     * @param clock : The clock value {@link TimestampBuilder.Clock}
     * @param value : How much to add
     * @return an edited TimeStamp
     */
    public TimestampBuilder add(Timestamp data, Clock clock, int value) {
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
        setTimestamp(data);
        return this;
    }

    /**
     * Add DAY, MINUTE, SECOND, etc to the current Timestamp
     *
     * @param value The value
     * @param clock The current clock
     * @return TimestampBuilder
     */
    public TimestampBuilder addTime(int value, Clock clock) {
        if (value <= 0) throw new IllegalStateException("Value cannot be less than 0!");
        switch (clock) {
            case SECOND:
                this.timestamp = addSecond(value, timestamp);
                break;
            case MINUTE:
                this.timestamp = addMinute(value, timestamp);
                break;
            case HOUR:
                this.timestamp = addHour(value, timestamp);
                break;
            case DAY:
                this.timestamp = addDay(value, timestamp);
                break;
        }
        return this;
    }

    /**
     * Decrease DAY, MINUTE, SECOND, etc to the current Timestamp
     *
     * @param value The value
     * @param clock The current clock
     * @return TimestampBuilder
     */
    public TimestampBuilder decreaseTime(int value, Clock clock) {
        if (value <= 0) throw new IllegalStateException("Value cannot be less than 0!");
        value = -value; // Set to negative.
        switch (clock) {
            case SECOND:
                this.timestamp = addSecond(value, timestamp);
                break;
            case MINUTE:
                this.timestamp = addMinute(value, timestamp);
                break;
            case HOUR:
                this.timestamp = addHour(value, timestamp);
                break;
            case DAY:
                this.timestamp = addDay(value, timestamp);
                break;
        }
        return this;
    }

    /**
     * Represent a "Clock"
     */
    public enum Clock {
        SECOND,
        MINUTE,
        HOUR,
        DAY
    }
}
