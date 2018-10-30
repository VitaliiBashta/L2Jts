package org.mmocore.gameserver.configuration.config.community;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

import java.time.ZoneId;

/**
 * Create by Mangol on 07.12.2015.
 */
@Configuration("community/communityBasic.json")
public class CBasicConfig {
    @Setting(name = "AllowCommunityBoard")
    public static boolean COMMUNITYBOARD_ENABLED;

    @Setting(name = "BBSDefault")
    public static String BBS_DEFAULT;

    @Setting(name = "Path")
    public static String BBS_PATH;

    @Setting(name = "TimeZone", method = "timeZoneId")
    public static ZoneId timeZoneId;

    @Setting(name = "IncreaseOnline", minValue = 1.)
    public static double increaseOnline;

    @Setting(name = "IncreaseOffline", minValue = 1.)
    public static double increaseOffline;

    private void timeZoneId(final String value) {
        timeZoneId = ZoneId.of(value);
    }
}
