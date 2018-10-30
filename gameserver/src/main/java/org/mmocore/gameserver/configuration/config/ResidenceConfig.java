package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Create by Mangol on 02.12.2015.
 */
@Configuration("residence.json")
public class ResidenceConfig {
    @Setting(name = "CastleValidationDate", method = "castleValidationTime")
    public static ZonedDateTime CASTLE_VALIDATION_DATE;

    @Setting(name = "CastleSelectHours")
    public static int[] CASTLE_SELECT_HOURS;

    @Setting(name = "CastleResetFlags")
    public static boolean CASTLE_RESET_FLAGS = true;

    @Setting(name = "CastleSiegePeriod")
    public static int CASTLE_SIEGE_PERIOD;

    private void castleValidationTime(final int[] value) {
        final int year = value[2];
        final int month = value[1];
        final int dayOfMonth = value[0];
        CASTLE_VALIDATION_DATE = ZonedDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.MIDNIGHT, ZoneId.systemDefault());
    }
}
