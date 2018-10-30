package org.mmocore.gameserver.configuration.config.clientCustoms;

import org.mmocore.commons.configuration.annotations.Configuration;

/**
 * Created by Hack
 * Date: 17.08.2016 5:23
 */

@Configuration("LostDreamCustom.json")
public class LostDreamCustom {
    public static boolean AllowDressService;

    public static boolean BBS_COMMISSION_ALLOW;
    public static boolean INDIVIDUAL_COMMISSION_PRICE;
    public static boolean BBS_COMMISSION_ALLOW_EQUIPPED;
    public static boolean BBS_COMMISSION_ALLOW_UNDERWEAR;
    public static boolean BBS_COMMISSION_ALLOW_CLOAK;
    public static boolean BBS_COMMISSION_ALLOW_BRACELET;
    public static boolean BBS_COMMISSION_ALLOW_AUGMENTED;
    public static boolean BBS_COMMISSION_ALLOW_PVP;
    public static int[] INDIVIDUAL_ID_ITEM;
    public static int[] COMMISSION_INDIVIDUAL_PRICE;
    public static int[] BBS_COMMISSION_ARMOR_PRICE;
    public static int[] BBS_COMMISSION_WEAPON_PRICE;
    public static int[] BBS_COMMISSION_JEWERLY_PRICE;
    public static int[] BBS_COMMISSION_OTHER_PRICE;
    public static int[] BBS_COMMISSION_MONETS;
    public static int[] BBS_COMMISSION_ALLOW_ITEMS;
    public static int[] BBS_COMMISSION_NOT_ALLOW_ITEMS;
    public static int BBS_COMMISSION_SAVE_DAYS;
    public static int BBS_COMMISSION_MAX_ENCHANT;
    public static int BBS_COMMISSION_COUNT_TO_PAGE;
    public static int BBS_COMMISSION_INTERVAL;
    public static String NAME_SERVER;

    public static boolean customOlyEnchant;
    public static int enchantOlyArmor;
    public static int enchantOlyWeapon;
    public static int enchantOlyJewelry;
    public static boolean enchantOlyExceptLowerItems;

    public static String additionalOlyWinnerRewardItems;
    public static int additionalOlyWinnerRewardCrp;
    public static int additionalOlyWinnerRewardFame;
    public static String additionalOlyLoserRewardItems;
    public static int additionalOlyLoserRewardCrp;
    public static int additionalOlyLoserRewardFame;

    public static boolean allowTradePvp;
    public static boolean allowTransformPvp;

    public static boolean addNoblesseBlessing;

    public static String crpRewardMonsters;
    public static double crpRewardMinHatePercent;

    public static int pvpHolderSize;
    public static boolean allowPvpRewards;
    public static String pvpRewards;
    public static int pvpRewardFame;

    public static String vipBuffs;
    public static boolean vipAccessByItemExists;
    public static boolean vipAccessForPremium;
    public static int vipAccessItemId;

    public static boolean allowAdditionalCastleRewards;
    public static String gludioCastleRewards;
    public static String dionCastleRewards;
    public static String giranCastleRewards;
    public static String orenCastleRewards;
    public static String adenCastleRewards;
    public static String innadrilCastleRewards;
    public static String goddardCastleRewards;
    public static String runeCastleRewards;
    public static String shuttgartCastleRewards;

    public static String gludioLeaderCastleRewards;
    public static String dionLeaderCastleRewards;
    public static String giranLeaderCastleRewards;
    public static String orenLeaderCastleRewards;
    public static String adenLeaderCastleRewards;
    public static String innadrilLeaderCastleRewards;
    public static String goddardLeaderCastleRewards;
    public static String runeLeaderCastleRewards;
    public static String shuttgartLeaderCastleRewards;

    public static int[] ccNonLockableBosses;
    public static int ccMinLockableSize;

    public static boolean customDestructionEnchant;

    public static int refineWeaponItem;
    public static int refineWeaponPrice;
    public static int refineJewelryItem;
    public static int refineJewelryPrice;
    public static String heroRewards;
}
