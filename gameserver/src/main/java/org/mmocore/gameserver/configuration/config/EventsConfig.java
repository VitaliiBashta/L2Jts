package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;
import org.mmocore.gameserver.utils.Util;

import java.util.Map;

@Configuration("events.json")
public class EventsConfig {
    @Setting(name = "UndergroundColiseum")
    public static boolean EVENT_UNDERGROUND_COLISEUM;

    @Setting(name = "UndergroundColiseumMemberCount")
    public static int EVENT_UNDERGROUND_COLISEUM_MEMBER_COUNT;

    @Setting(name = "LH_StartTime")
    public static final String[] EVENT_LH_StartTime = new String[]{"20", "00"};

    @Setting(name = "LastHero_bonus_id")
    public static int EVENT_LastHeroItemID;

    @Setting(name = "LastHero_bonus_count")
    public static double EVENT_LastHeroItemCOUNT;

    @Setting(name = "LastHero_rate")
    public static boolean EVENT_LastHeroRate;

    @Setting(name = "LastHero_bonus_count_final")
    public static double EVENT_LastHeroItemCOUNTFinal;

    @Setting(name = "LastHero_rate_final")
    public static boolean EVENT_LastHeroRateFinal;

    @Setting(name = "LastHero_time")
    public static int EVENT_LastHeroTime;

    @Setting(name = "LastHero_Hwid_protect") //TODO: не работает
    public static boolean EVENT_LastHeroHwidProtect;

    @Setting(minValue = 0, maxValue = 100)
    public static int dropChance;

    @Setting(minValue = 0, maxValue = 100)
    public static int explosionChance;

    @Setting(canNull = true)
    public static final int[] disallowedSkills = new int[0];

    @Setting(canNull = true)
    public static final int[] allowedItems = new int[0];

    @Setting(canNull = true)
    public static final int[] allowedItemsOnPVP = new int[0];

    @Setting(name = "ItemOnLevelUpActive")
    public static boolean EVENT_ItemOnLevelUpActive;

    @Setting(name = "lvlsForReward", canNull = true)
    public static final String[] EVENT_IOLU_LvlsForReward = new String[0];

    @Setting(name = "lvlsForRewardItems", canNull = true)
    public static final int[] EVENT_IOLU_LvlsForRewardItems = new int[0];

    @Setting(name = "lvlsForRewardCount", canNull = true)
    public static final int[] EVENT_IOLU_LvlsForRewardCount = new int[0];

    @Setting(name = "lvlsForTeleport", canNull = true)
    public static final String[] EVENT_IOLU_LvlsForTeleport = new String[0];

    @Setting(name = "locForTeleport", canNull = true)
    public static final String[] EVENT_IOLU_LocForTeleport = new String[0];

    @Setting(name = "lvlsForRewardHtm", canNull = true)
    public static final String[] EVENT_IOLU_LvlsForRewardHtm = new String[0];

    @Setting(name = "locNameForTeleport", canNull = true)
    public static final String[] EVENT_IOLU_LocNameForTeleport = new String[0];

    public static boolean LhIsActive;
    public static int LhMaxParticipants;
    public static int LhMinParticipants;
    public static boolean LhAllowSummonTeleport;
    public static boolean LhRemoveEffects;
    public static boolean LhAllowCustomBuffs;
    @Setting(method = "parseFighterBuffs")
    public static Map<Integer, Integer> LhFighterBuffs;
    @Setting(method = "parseMageBuffs")
    public static Map<Integer, Integer> LhMageBuffs;
    public static int LhEventDurationMin;
    public static boolean LhAllowHeroChatForWinner;
    public static final String LhCustomName = "";
    public static final String LhCustomTitle = "";
    public static boolean LhHideClanCrests;
    public static int LhHeroStatusDurationMin;
    public static boolean LhForbidTransformations;
    @Setting(method = "parseVisualItems")
    public static Map<Integer, Integer> LhVisualItems;
    public static boolean TVTAllowSummonTeleport;
    public static boolean TVTRemoveEffects;
    public static boolean TVTAllowCustomBuffs;

    // ========================================================
    // TVT
    // ========================================================
    public static boolean TVTCantAttackOurTeam;
    public static boolean TVTAllowJoinParty;
    @Setting(method = "parseTVTFighterBuffs")
    public static Map<Integer, Integer> TVTFighterBuffs;
    @Setting(method = "parseTVTMageBuffs")
    public static Map<Integer, Integer> TVTMageBuffs;
    public static boolean TVTForbidTransformations;
    public static boolean CFTAllowSummonTeleport;
    public static boolean CFTRemoveEffects;
    public static boolean CFTAllowCustomBuffs;
    public static boolean CFTCantAttackOurTeam;
    public static boolean CFTAllowJoinParty;

    // ========================================================
    // CFT
    // ========================================================
    @Setting(method = "parseCFTFighterBuffs")
    public static Map<Integer, Integer> CFTFighterBuffs;
    @Setting(method = "parseCFTMageBuffs")
    public static Map<Integer, Integer> CFTMageBuffs;
    public static boolean CFTForbidTransformations;
    public static boolean isTvtActive;
    public static boolean isCtfActive;

    private void parseFighterBuffs(String value) {
        LhFighterBuffs = Util.parseConfigMap(value, Integer.class, Integer.class);
    }

    private void parseMageBuffs(String value) {
        LhMageBuffs = Util.parseConfigMap(value, Integer.class, Integer.class);
    }

    private void parseVisualItems(String value) {
        LhVisualItems = Util.parseConfigMap(value, Integer.class, Integer.class);
    }

    private void parseTVTFighterBuffs(String value) {
        TVTFighterBuffs = Util.parseConfigMap(value, Integer.class, Integer.class);
    }

    private void parseTVTMageBuffs(String value) {
        TVTMageBuffs = Util.parseConfigMap(value, Integer.class, Integer.class);
    }

    private void parseCFTFighterBuffs(String value) {
        CFTFighterBuffs = Util.parseConfigMap(value, Integer.class, Integer.class);
    }

    private void parseCFTMageBuffs(String value) {
        CFTMageBuffs = Util.parseConfigMap(value, Integer.class, Integer.class);
    }
}
