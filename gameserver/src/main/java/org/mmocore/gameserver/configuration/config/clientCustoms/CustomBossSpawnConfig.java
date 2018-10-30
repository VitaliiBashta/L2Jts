package org.mmocore.gameserver.configuration.config.clientCustoms;

import org.mmocore.commons.configuration.annotations.Configuration;

/**
 * Created by Hack
 * Date: 29.08.2016 5:46
 */

@Configuration("customBossSpawn.json")
public class CustomBossSpawnConfig {
    public static boolean activateCustomSpawn;
    public static String antharasCron;
    public static int antharasRandomMinutes;
    public static String valakasCron;
    public static int valakasRandomMinutes;
    public static String baiumCron;
    public static int baiumRandomMinutes;
    public static String belethCron;
    public static int belethRandomMinutes;
}
