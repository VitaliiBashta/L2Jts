package org.mmocore.commons.utils;

import org.quartz.CronExpression;

import java.text.ParseException;

/**
 * @author Java-man
 */
public final class QuartzUtils {
    private QuartzUtils() {
    }

    public static CronExpression createCronExpression(final String cronExpression) {
        if (cronExpression == null || cronExpression.length() < 2)
            return null;

        try {
            return new CronExpression(cronExpression);
        } catch (ParseException e) {
            throw new IllegalArgumentException(cronExpression + " cannot be parsed into a valid CronExpression", e);
        }
    }
}
