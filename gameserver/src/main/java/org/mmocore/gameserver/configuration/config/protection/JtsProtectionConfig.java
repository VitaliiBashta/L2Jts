package org.mmocore.gameserver.configuration.config.protection;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 29.11.2015.
 */
@Configuration("protection/jtsProtection.json")
public class JtsProtectionConfig {
    @Setting(name = "ProtectionEnabled")
    public static boolean PROTECTION_ENABLED;

    @Setting(name = "ProtectionEnabledHWIDRequest")
    public static boolean PROTECTION_ENABLED_HWID_REQUEST;

    @Setting(name = "ProtectionKickEmptyHWID")
    public static boolean PROTECTION_KICK_EMPTY_HWID;

    @Setting(name = "ProtectionBanHWID")
    public static boolean PROTECTION_BAN_HWID;

    @Setting(name = "ProtectionRevisionNumber")
    public static int PROTECTION_REVISION_NUMBER;
}
