package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("olympiad.json")
public class OlympiadConfig {
    @Setting(name = "EnableOlympiad")
    public static boolean ENABLE_OLYMPIAD;

    @Setting(name = "EnableOlympiadSpectating")
    public static boolean ENABLE_OLYMPIAD_SPECTATING;

    @Setting(name = "OlympiadStadiasCount")
    public static int OLYMPIAD_STADIAS_COUNT;

    @Setting(name = "AltOlyStartTime")
    public static int ALT_OLY_START_TIME;

    @Setting(name = "AltOlyMin")
    public static int ALT_OLY_MIN;

    @Setting(name = "AltOlyCPeriod")
    public static long ALT_OLY_CPERIOD;

    @Setting(name = "AltOlyWPeriod")
    public static long ALT_OLY_WPERIOD;

    @Setting(name = "AltOlyVPeriod")
    public static long ALT_OLY_VPERIOD;

    @Setting(name = "ClassGameMin")
    public static int CLASS_GAME_MIN;

    @Setting(name = "NonClassGameMin")
    public static int NONCLASS_GAME_MIN;

    @Setting(name = "TeamGameMin")
    public static int TEAM_GAME_MIN;

    @Setting(name = "EnableOlympiadClass")
    public static boolean OLYMPIAD_CLASS_ENABLE;

    @Setting(name = "AltOlyBattleRewItem")
    public static int ALT_OLY_BATTLE_REWARD_ITEM;

    @Setting(name = "AltOlyClassedRewItemCount")
    public static int ALT_OLY_CLASSED_RITEM_C;

    @Setting(name = "AltOlyNonClassedRewItemCount")
    public static int ALT_OLY_NONCLASSED_RITEM_C;

    @Setting(name = "AltOlyTeamRewItemCount")
    public static int ALT_OLY_TEAM_RITEM_C;

    @Setting(name = "AltOlyCompRewItem")
    public static int ALT_OLY_COMP_RITEM;

    @Setting(name = "AltOlyGPPerPoint")
    public static int ALT_OLY_GP_PER_POINT;

    @Setting(name = "AltOlyHeroPoints")
    public static int ALT_OLY_HERO_POINTS;

    @Setting(name = "AltOlyRank1Points")
    public static int ALT_OLY_RANK1_POINTS;

    @Setting(name = "AltOlyRank2Points")
    public static int ALT_OLY_RANK2_POINTS;

    @Setting(name = "AltOlyRank3Points")
    public static int ALT_OLY_RANK3_POINTS;

    @Setting(name = "AltOlyRank4Points")
    public static int ALT_OLY_RANK4_POINTS;

    @Setting(name = "AltOlyRank5Points")
    public static int ALT_OLY_RANK5_POINTS;

    @Setting(name = "GameMaxLimit")
    public static int GAME_MAX_LIMIT;

    @Setting(name = "GameClassesCountLimit")
    public static int GAME_CLASSES_COUNT_LIMIT;

    @Setting(name = "GameNoClassesCountLimit")
    public static int GAME_NOCLASSES_COUNT_LIMIT;

    @Setting(name = "GameTeamCountLimit")
    public static int GAME_TEAM_COUNT_LIMIT;

    @Setting(name = "OlympiadBattlesForReward")
    public static int OLYMPIAD_BATTLES_FOR_REWARD;

    @Setting(name = "OlympiadPointsDefault")
    public static int OLYMPIAD_POINTS_DEFAULT;

    @Setting(name = "OlympiadPointsWeekly")
    public static int OLYMPIAD_POINTS_WEEKLY;

    @Setting(name = "OlympiadOldStyleStat")
    public static boolean OLYMPIAD_OLDSTYLE_STAT;

    @Setting(name = "OlympiadUseWeeklyPeriod")
    public static boolean OLYMPIAD_USE_WEEKLY_PERIOD;

    @Setting(name = "OlympiadWeekCount")
    public static int OLYMPIAD_WEEK_COUNT;

    @Setting(minValue = 1, maxValue = 7, name = "OlympiadDayOfWeek")
    public static int OLYMPIAD_DAY_OF_WEEK;

    @Setting(name = "OlympiadCheckIp")
    public static boolean OLYMPIAD_CHECK_IP;

    @Setting(name = "OlympiadCheckHwid")
    public static boolean OLYMPIAD_CHECK_HWID;

    @Setting(name = "OlympiadDeleteCubics")
    public static boolean OLYMPIAD_DELETE_CUBICS;

    public static int olympiadTeleTime;

    @Setting(name = "OlympiadPointTransfer")
    public static boolean OLYMPIAD_POINT_TRANSFER;

    @Setting(name = "OlympiadPointTransferTime", canNull = true)
    public static int[] OLYMPIAD_POINT_TRANSFER_TIMES = new int[0];

    public static boolean addCustomEffect;
}
