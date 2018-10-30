package org.mmocore.gameserver.configuration.config.community;

import org.mmocore.commons.configuration.annotations.Configuration;

/**
 * Create by Mangol on 12.12.2015.
 */
@Configuration("community/communityTeleport.json")
public class CTeleportConfig {
    public static boolean allowTeleport;
    public static int freeLevelTeleport;
    public static boolean inInstance;
    public static boolean inCombat;
    public static boolean inPvp;
    public static boolean inSiege;
    public static boolean inWater;
    public static boolean inBoat;

    public static int saveItemId;
    public static int savePrice;
    public static int premiumSaveItemId;
    public static int premiumSavePrice;
    public static int maxPointsCount;
    public static boolean pointForPremium;
    public static boolean disallowSavePointsInAnyZone;

    public static boolean inSkillOffensive;
}
