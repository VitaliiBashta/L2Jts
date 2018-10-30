package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 26.11.2015.
 */
@Configuration("altsettings.json")
public class AllSettingsConfig {
    @Setting(ignore = true) //TODO: Достойно вынести в конфиг ?
    public static final int[] VITALITY_LEVELS = {240, 2000, 13000, 17000, 20000};
    @Setting(name = "AltRequestCustomStartEquipment")
    public static boolean ALT_REQUEST_CUSTOM_START_EQUIPMENT;
    @Setting(name = "AltEnableMenuCommand")
    public static boolean ALT_ENABLE_MENU_COMMAND;
    @Setting(name = "AdditionalCharString")
    public static boolean ALT_ENABLE_ADDITIONAL_CHAR_STRING;
    @Setting(name = "AdditionalCharStringList", splitter = "@", canNull = true)
    public static String[] ALT_ADDITIONAL_CHAR_STRING_LIST = new String[0];
    @Setting(name = "AutoLoot")
    public static boolean AUTO_LOOT;
    @Setting(name = "AutoLootOnlyForPremium")
    public static boolean AUTO_LOOT_INDIVIDUAL_ONLY_FOR_PREMIUM;
    @Setting(name = "AutoLootHerbs")
    public static boolean AUTO_LOOT_HERBS;
    @Setting(name = "AutoLootIndividual")
    public static boolean AUTO_LOOT_INDIVIDUAL;
    @Setting(name = "AutoLootFromRaids")
    public static boolean AUTO_LOOT_FROM_RAIDS;
    @Setting(name = "AutoLootPK")
    public static boolean AUTO_LOOT_PK;
    @Setting(name = "AltKarmaPlayerCanShop")
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    @Setting(name = "Delevel")
    public static boolean ALT_GAME_DELEVEL;
    @Setting(name = "AltDisableSpellbooks")
    public static boolean ALT_DISABLE_SPELLBOOKS;
    @Setting(name = "AltSaveUnsaveable")
    public static boolean ALT_SAVE_UNSAVEABLE;
    @Setting(name = "AltSaveEffectsRemainingTime")
    public static int ALT_SAVE_EFFECTS_REMAINING_TIME;
    @Setting(name = "AltShowSkillReuseMessage")
    public static boolean ALT_SHOW_REUSE_MSG;
    @Setting(name = "AltReuseCorrection")
    public static int ALT_REUSE_CORRECTION;
    @Setting(name = "AutoLearnSkills")
    public static boolean AUTO_LEARN_SKILLS;
    @Setting(name = "AutoLearnForgottenSkills")
    public static boolean AUTO_LEARN_FORGOTTEN_SKILLS;
    @Setting(name = "CharTitle")
    public static boolean CHAR_TITLE;
    @Setting(name = "CharAddTitle")
    public static String ADD_CHAR_TITLE;
    @Setting(name = "SavingSpS")
    public static boolean SAVING_SPS;
    @Setting(name = "AltUnregisterRecipe")
    public static boolean ALT_GAME_UNREGISTER_RECIPE;
    @Setting(name = "AllowShiftClick")
    public static boolean ALLOW_NPC_SHIFTCLICK;
    @Setting(name = "AltShowDroplist")
    public static boolean ALT_GAME_SHOW_DROPLIST;
    @Setting(name = "AltFullStatsPage")
    public static boolean ALT_FULL_NPC_STATS_PAGE;
    @Setting(name = "AltAllowSubClassWithoutQuest")
    public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
    @Setting(name = "AltLevelToGetSubclass")
    public static int ALT_GAME_LEVEL_TO_GET_SUBCLASS;
    @Setting(name = "AltSubAdd")
    public static int ALT_GAME_SUB_ADD;
    @Setting(name = "AltSubAllMasters")
    public static boolean ALT_GAME_SUB_ALL_MASTERS;
    @Setting(name = "AltMaxLevel", maxValue = 99)
    public static int ALT_MAX_LEVEL;
    @Setting(name = "AltMaxSubLevel", maxValue = 99)
    public static int ALT_MAX_SUB_LEVEL;
    @Setting(name = "AltAddRecipes")
    public static int ALT_ADD_RECIPES;
    @Setting(name = "AltMaxAllySize")
    public static int ALT_MAX_ALLY_SIZE;
    @Setting(name = "AltRequireClanCastle")
    public static boolean ALT_GAME_REQUIRE_CLAN_CASTLE;
    @Setting(name = "AltRequireCastleDawn")
    public static boolean ALT_GAME_REQUIRE_CASTLE_DAWN;
    @Setting(name = "AltAllowAdenaDawn")
    public static boolean ALT_GAME_ALLOW_ADENA_DAWN;
    @Setting(name = "AltAllowOthersWithdrawFromClanWarehouse")
    public static boolean ALT_ALLOW_OTHERS_WITHDRAW_FROM_CLAN_WAREHOUSE;
    @Setting(name = "AltAllowClanCommandOnlyForClanLeader")
    public static boolean ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER;
    @Setting(name = "SSAnnouncePeriod")
    public static int SS_ANNOUNCE_PERIOD;
    @Setting(name = "EnableAltDeathPenalty")
    public static boolean ALT_DEATH_PENALTY;
    @Setting(name = "AltPKDeathRate")
    public static double ALT_PK_DEATH_RATE;
    @Setting(name = "EnableDeathPenaltyC5")
    public static boolean ALLOW_DEATH_PENALTY_C5;
    @Setting(name = "DeathPenaltyC5Chance")
    public static int ALT_DEATH_PENALTY_C5_CHANCE;
    @Setting(name = "ChaoticCanUseScrollOfRecovery")
    public static boolean ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY;
    @Setting(name = "DeathPenaltyC5RateExpPenalty")
    public static int ALT_DEATH_PENALTY_C5_EXPERIENCE_PENALTY;
    @Setting(name = "DeathPenaltyC5RateKarma")
    public static int ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
    @Setting(name = "PushkinSignsOptions")
    public static boolean ALT_SIMPLE_SIGNS;
    @Setting(name = "TeleToCatacombs")
    public static boolean ALT_TELE_TO_CATACOMBS;
    @Setting(name = "BSCrystallize")
    public static boolean ALT_BS_CRYSTALLIZE;
    @Setting(name = "AllowSellCommon")
    public static boolean ALT_ALLOW_SELL_COMMON;
    @Setting(name = "AllowShadowWeapons")
    public static boolean ALT_ALLOW_SHADOW_WEAPONS;
    @Setting(name = "DisabledMultisells", canNull = true)
    public static int[] ALT_DISABLED_MULTISELL = new int[0];
    @Setting(name = "ShopPriceLimits", canNull = true)
    public static int[] ALT_SHOP_PRICE_LIMITS = new int[0];
    @Setting(name = "ShopUnallowedItems", canNull = true)
    public static int[] ALT_SHOP_UNALLOWED_ITEMS = new int[0];
    @Setting(name = "AllowedPetPotions", canNull = true)
    public static int[] ALT_ALLOWED_PET_POTIONS = new int[0];
    @Setting(name = "BuffLimit")
    public static int ALT_BUFF_LIMIT;
    @Setting(name = "DanceSongLimit")
    public static int ALT_DANCE_SONG_LIMIT;
    @Setting(name = "NonOwnerItemPickupDelay", increase = 1000L)
    public static long NONOWNER_ITEM_PICKUP_DELAY;
    @Setting(name = "FestivalMinPartySize")
    public static int FESTIVAL_MIN_PARTY_SIZE;
    @Setting(name = "FestivalRatePrice")
    public static double FESTIVAL_RATE_PRICE;
    @Setting(name = "RiftMinPartySize")
    public static int RIFT_MIN_PARTY_SIZE;
    @Setting(name = "RiftSpawnDelay")
    public static int RIFT_SPAWN_DELAY;
    @Setting(name = "MaxRiftJumps")
    public static int RIFT_MAX_JUMPS;
    @Setting(name = "AutoJumpsDelay")
    public static int RIFT_AUTO_JUMPS_TIME;
    @Setting(name = "AutoJumpsDelayRandom")
    public static int RIFT_AUTO_JUMPS_TIME_RAND;
    @Setting(name = "RecruitFC")
    public static int RIFT_ENTER_COST_RECRUIT;
    @Setting(name = "SoldierFC")
    public static int RIFT_ENTER_COST_SOLDIER;
    @Setting(name = "OfficerFC")
    public static int RIFT_ENTER_COST_OFFICER;
    @Setting(name = "CaptainFC")
    public static int RIFT_ENTER_COST_CAPTAIN;
    @Setting(name = "CommanderFC")
    public static int RIFT_ENTER_COST_COMMANDER;
    @Setting(name = "HeroFC")
    public static int RIFT_ENTER_COST_HERO;
    @Setting(name = "AllowLearnTransSkillsWOQuest")
    public static boolean ALLOW_LEARN_TRANS_SKILLS_WO_QUEST;
    @Setting(name = "PartyLeaderOnlyCanInvite")
    public static boolean PARTY_LEADER_ONLY_CAN_INVITE;
    @Setting(name = "AllowTalkWhileSitting")
    public static boolean ALLOW_TALK_WHILE_SITTING;
    @Setting(name = "AllowNobleTPToAll")
    public static boolean ALLOW_NOBLE_TP_TO_ALL;
    @Setting(name = "AltAntharasMinionsNumber")
    public static int ALT_ANTHARAS_MINIONS_NUMBER;
    @Setting(name = "AltRaidRespawnMultiplier")
    public static double ALT_RAID_RESPAWN_MULTIPLIER;
    @Setting(name = "AlowDropAugmented")
    public static boolean ALT_ALLOW_DROP_AUGMENTED;
    @Setting(name = "BuffTimeModifier")
    public static double BUFFTIME_MODIFIER;
    @Setting(name = "ClanHallBuffTimeModifier")
    public static double CLANHALL_BUFFTIME_MODIFIER;
    @Setting(name = "SongDanceTimeModifier")
    public static double SONGDANCETIME_MODIFIER;
    @Setting(name = "MaxLoadModifier")
    public static double MAXLOAD_MODIFIER;
    @Setting(name = "GkCostMultiplier")
    public static double GATEKEEPER_MODIFIER;
    @Setting(name = "GkNobleCostMultiplier")
    public static double GATEKEEPER_NOBLE_MODIFIER;
    @Setting(name = "AltEnchantSkillAdenaMultiplier")
    public static double ALT_SKILL_ENCHANT_ADENA_MODIFIER;
    @Setting(name = "AltEnchantSkillSPMultiplier")
    public static double ALT_SKILL_ENCHANT_SP_MODIFIER;
    @Setting(name = "AltSafeEnchantSkillAdenaMultiplier")
    public static double ALT_SKILL_SAFE_ENCHANT_ADENA_MODIFIER;
    @Setting(name = "AltSafeEnchantSkillSPMultiplier")
    public static double ALT_SKILL_SAFE_ENCHANT_SP_MODIFIER;
    @Setting(name = "AltSkillRouteChangeAdenaMultiplier")
    public static double ALT_SKILL_ROUTE_CHANGE_ADENA_MODIFIER;
    @Setting(name = "AltSkillRouteChangeSPMultiplier")
    public static double ALT_SKILL_ROUTE_CHANGE_SP_MODIFIER;
    @Setting(name = "AltSkillUntrainRefundSPMultiplier")
    public static double ALT_SKILL_UNTRAIN_REFUND_SP_MODIFIER;
    @Setting(name = "AltChampionChance1")
    public static double ALT_CHAMPION_CHANCE1;
    @Setting(name = "AltChampionChance2")
    public static double ALT_CHAMPION_CHANCE2;
    @Setting(name = "AltChampionAggro")
    public static boolean ALT_CHAMPION_CAN_BE_AGGRO;
    @Setting(name = "AltChampionSocial")
    public static boolean ALT_CHAMPION_CAN_BE_SOCIAL;
    @Setting(name = "AltChampionTopLevel")
    public static int ALT_CHAMPION_TOP_LEVEL;
    @Setting(name = "NoLasthitOnRaid")
    public static boolean ALT_NO_LASTHIT;
    @Setting(name = "PetsHealOnlyInBattle")
    public static boolean ALT_PET_HEAL_BATTLE_ONLY;
    @Setting(name = "AltPartyDistributionRange")
    public static int ALT_PARTY_DISTRIBUTION_RANGE;
    @Setting(name = "AltPartyBonus")
    public static double[] ALT_PARTY_BONUS = new double[]{1.00, 1.10, 1.20, 1.30, 1.40, 1.50, 2.00, 2.10, 2.20};
    @Setting(name = "AltRemoveSkillsOnDelevel")
    public static boolean ALT_REMOVE_SKILLS_ON_DELEVEL;
    @Setting(name = "AltChAllBuffs")
    public static boolean ALT_CH_ALL_BUFFS;
    @Setting(name = "AltChAllowHourBuff")
    public static boolean ALT_CH_ALLOW_1H_BUFFS;
    @Setting(name = "AltNotAllowTWWardsInClanHalls")
    public static boolean ALT_NOT_ALLOW_TW_WARDS_IN_CLANHALLS;
    @Setting(name = "AltNoFameForDead")
    public static boolean ALT_NO_FAME_FOR_DEAD;
    @Setting(name = "GkFree")
    public static int GATEKEEPER_FREE;
    @Setting(name = "GkCruma")
    public static int CRUMA_GATEKEEPER_LVL;
    @Setting(name = "AltVitalityEnabled")
    public static boolean ALT_VITALITY_ENABLED;
    @Setting(name = "AltVitalityRate")
    public static double ALT_VITALITY_RATE;

    @Setting(name = "AltVitalityRaidBonus")
    public static int ALT_VITALITY_RAID_BONUS;

    @Setting(name = "AltVitalityConsumeRate")
    public static double ALT_VITALITY_CONSUME_RATE;

    @Setting(name = "KamalokaNightmaresPremiumOnly")
    public static boolean ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY;

    @Setting(name = "AltPetInventoryLimit")
    public static int ALT_PET_INVENTORY_LIMIT;

    @Setting(name = "FollowRange")
    public static int FOLLOW_RANGE;

    @Setting(name = "OpenCloakSlot")
    public static boolean ALT_OPEN_CLOAK_SLOT;

    @Setting(name = "ShowServerTime")
    public static boolean ALT_SHOW_SERVER_TIME;

    @Setting(name = "AltDisableFeatherOnSiegeAndEpic")
    public static boolean ALT_DISABLE_FEATHER_ON_SIEGES_AND_EPIC;

    @Setting(name = "AltItemAuctionCanRebid")
    public static boolean ALT_ITEM_AUCTION_CAN_REBID;

    @Setting(name = "AltItemAuctionAnnounce")
    public static boolean ALT_ITEM_AUCTION_START_ANNOUNCE;

    @Setting(name = "AltItemAuctionBidItemId")
    public static int ALT_ITEM_AUCTION_BID_ITEM_ID;

    @Setting(name = "AltItemAuctionMaxBid")
    public static long ALT_ITEM_AUCTION_MAX_BID;

    @Setting(name = "AltItemAuctionMaxCancelTimeInMillis")
    public static int ALT_ITEM_AUCTION_MAX_CANCEL_TIME_IN_MILLIS;

    @Setting(name = "AltFishChampionshipEnabled")
    public static boolean ALT_FISH_CHAMPIONSHIP_ENABLED;

    @Setting(name = "AltFishChampionshipRewardItemId")
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_ITEM;

    @Setting(name = "AltFishChampionshipReward1")
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_1;

    @Setting(name = "AltFishChampionshipReward2")
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_2;

    @Setting(name = "AltFishChampionshipReward3")
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_3;

    @Setting(name = "AltFishChampionshipReward4")
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_4;

    @Setting(name = "AltFishChampionshipReward5")
    public static int ALT_FISH_CHAMPIONSHIP_REWARD_5;

    @Setting(name = "EnableBlockCheckerEvent")
    public static boolean ALT_ENABLE_BLOCK_CHECKER_EVENT;

    @Setting(name = "BlockCheckerMinTeamMembers")
    public static int ALT_MIN_BLOCK_CHECKER_TEAM_MEMBERS;

    @Setting(name = "BlockCheckerRateCoinReward")
    public static double ALT_RATE_COINS_REWARD_BLOCK_CHECKER;

    @Setting(name = "HBCEFairPlay")
    public static boolean ALT_HBCE_FAIR_PLAY;

    @Setting(name = "PetitioningAllowed")
    public static boolean PETITIONING_ALLOWED;

    @Setting(name = "MaxPetitionsPerPlayer")
    public static int MAX_PETITIONS_PER_PLAYER;

    @Setting(name = "MaxPetitionsPending")
    public static int MAX_PETITIONS_PENDING;

    @Setting(name = "AltPcBangPointsEnabled")
    public static boolean ALT_PCBANG_POINTS_ENABLED;

    @Setting(name = "AltPcBangPointsBonus")
    public static int ALT_PCBANG_POINTS_BONUS;

    @Setting(name = "AltPcBangPointsDelay")
    public static int ALT_PCBANG_POINTS_DELAY;

    @Setting(name = "AltPcBangPointsMinLvl")
    public static int ALT_PCBANG_POINTS_MIN_LVL;

    @Setting(name = "AltPcBangPointsDoubleChance")
    public static double ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE;

    @Setting(name = "AltDebugEnabled")
    public static boolean ALT_DEBUG_ENABLED;

    @Setting(name = "AltDebugPvPEnabled")
    public static boolean ALT_DEBUG_PVP_ENABLED;

    @Setting(name = "AltDebugPvPDuelOnly")
    public static boolean ALT_DEBUG_PVP_DUEL_ONLY;

    @Setting(name = "AltDebugPvEEnabled")
    public static boolean ALT_DEBUG_PVE_ENABLED;

    @Setting(name = "AllowClosedHellbound")
    public static boolean ALLOW_CLOSED_HELLBOUND;

    @Setting(name = "HellboundTeleWithoutQuest")
    public static boolean HELLBOUND_TELE_WITHOUT_QUEST;

    @Setting(name = "HellboundRate")
    public static double HELLBOUND_RATE;

    @Setting(name = "AltBlueChampionDrop", canNull = true)
    public static String AltBlueChampionDrop;

    @Setting(name = "AltRedChampionDrop", canNull = true)
    public static String AltRedChampionDrop;

    @Setting(name = "AltKillAnnounce")
    public static boolean AltKillAnnounce;

    public static boolean AltAllowNoCarrier;

    public static int startLevel;
    public static int startLevelSub;
    public static boolean allItemsForOneAdena;
    public static int[] customItemDistribute;
    public static boolean unlimitedShots;
    public static boolean unlimitedArrowsBolts;
    public static boolean showPriceWarnings;
    public static boolean allowSeveralCertiBooks;
    public static int clanCreateLevel;
    public static boolean allowBossSpawnAnnounce;
    public static int[] ignoredBossSpawnAnnounceIds;
    public static boolean allowDispelDanceSong;
    public static boolean allowAugmentPvp;
    public static int augmentPvpGemCount;
    public static boolean allowAttributePvp;
    public static boolean showSkillChances;
    public static boolean allowAttackTemporalAlly;
    public static int clanLvlUpRepTo6;
    public static int clanLvlUpSizeTo6;
    public static int clanLvlUpRepTo7;
    public static int clanLvlUpSizeTo7;
    public static int clanLvlUpRepTo8;
    public static int clanLvlUpSizeTo8;
    public static int clanLvlUpRepTo9;
    public static int clanLvlUpSizeTo9;
    public static int clanLvlUpRepTo10;
    public static int clanLvlUpSizeTo10;
    public static int clanLvlUpRepTo11;
    public static int clanLvlUpSizeTo11;
    public static int clanPenaltyForClan;
    public static int clanPenaltyForPlayer;
    public static boolean regularSpiritShotCastBonus;
    public static boolean naiaTeleportWholeCc;
    public static boolean oneClickSkillEnchant;
    public static boolean oneCLickItemEnchant;
    public static boolean oneClickItemAttribute;
}
