package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 29.11.2015.
 */
@Configuration("pts_data.json")
public class PtsDataConfig {
    @Setting(name = "EnabledMakerSpawn")
    public static boolean PTS_DATA_ENABLED_MAKER_SPAWN;
}
