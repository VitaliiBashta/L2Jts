package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 04.12.2015.
 */
@Configuration("spoil.json")
public class SpoilConfig {
    @Setting(name = "BasePercentChanceOfSpoilSuccess")
    public static double BASE_SPOIL_RATE;

    @Setting(name = "MinimumPercentChanceOfSpoilSuccess")
    public static double MINIMUM_SPOIL_RATE;

    @Setting(name = "AltFormula")
    public static boolean ALT_SPOIL_FORMULA;

    @Setting(name = "AllowManor")
    public static boolean ALLOW_MANOR;

    @Setting(name = "AltManorRefreshTime")
    public static int MANOR_REFRESH_TIME;

    @Setting(name = "AltManorRefreshMin")
    public static int MANOR_REFRESH_MIN;

    @Setting(name = "AltManorApproveTime")
    public static int MANOR_APPROVE_TIME;

    @Setting(name = "AltManorApproveMin")
    public static int MANOR_APPROVE_MIN;

    @Setting(name = "AltManorMaintenancePeriod")
    public static int MANOR_MAINTENANCE_PERIOD;

    @Setting(name = "BasePercentChanceOfSowingSuccess")
    public static double MANOR_SOWING_BASIC_SUCCESS;

    @Setting(name = "BasePercentChanceOfSowingAltSuccess")
    public static double MANOR_SOWING_ALT_BASIC_SUCCESS;

    @Setting(name = "BasePercentChanceOfHarvestingSuccess")
    public static double MANOR_HARVESTING_BASIC_SUCCESS;

    @Setting(name = "MinDiffPlayerMob")
    public static int MANOR_DIFF_PLAYER_TARGET;

    @Setting(name = "DiffPlayerMobPenalty")
    public static double MANOR_DIFF_PLAYER_TARGET_PENALTY;

    @Setting(name = "MinDiffSeedMob")
    public static int MANOR_DIFF_SEED_TARGET;

    @Setting(name = "DiffSeedMobPenalty")
    public static double MANOR_DIFF_SEED_TARGET_PENALTY;
}
