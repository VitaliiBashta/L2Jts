package org.mmocore.gameserver.configuration.config.protection;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;
import org.mmocore.gameserver.ProtectType;

/**
 * @author Mangol
 * @since 21.08.2016
 */
@Configuration("protection/baseSetting.json")
public class BaseProtectSetting {
    @Setting(name = "ProtectType")
    public static ProtectType protectType;
}
