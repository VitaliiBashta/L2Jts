package org.mmocore.gameserver.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author Java-man
 */
public class TimeUtils {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String timeFormat(final TemporalAccessor time) {
        return TIME_FORMATTER.format(time);
    }

    public static String dateFormat(final TemporalAccessor dateTime) {
        return DATE_FORMATTER.format(dateTime);
    }

    public static String dateFormat(final Instant instant) {
        final ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateFormat(dateTime);
    }

    public static String dateTimeFormat(final TemporalAccessor dateTime) {
        return DATE_TIME_FORMATTER.format(dateTime);
    }

    public static String dateTimeFormat(final Instant instant) {
        final ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTimeFormat(dateTime);
    }
}
