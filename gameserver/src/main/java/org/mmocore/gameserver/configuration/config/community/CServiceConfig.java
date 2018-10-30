package org.mmocore.gameserver.configuration.config.community;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;

/**
 * @author Mangol
 * @since 08.02.2016
 */
@Configuration("community/communityService.json")
public class CServiceConfig {
    public static boolean allowLeaseTransform;
    public static boolean leaseTransformUseOlympiad;
    public static boolean leaseTransformUseEvent;
    public static boolean leaseTransformUseDuel;
    public static boolean leaseTransformUseSiege;
    public static boolean leaseTransformUseCombat;
    public static boolean leaseTransformUseInstance;
    public static boolean leaseTransformUseFlag;

    public static boolean nickRenameAsk;
    public static boolean allowNickRename;
    public static int nickRenameItemId;
    public static long nickRenameItemCount;
    public static String namePattern;

    public static boolean allowClearPK;
    public static boolean clearPKAsk;
    public static int clearPKItemId;
    public static long clearPKItemCount;
    public static int clearPKMax;

    public static boolean allowClearKarma;
    public static boolean clearKarmaAsk;
    public static int clearKarmaItemId;
    public static long clearKarmaItemCount;
    public static int clearKarmaMax;
    public static int clearKarmaFullItemId;
    public static long clearKarmaFullPrice;

    public static boolean allowNobless;
    public static boolean noblessAsk;
    public static int[] noblessItemId;
    public static long[] noblessItemCount;

    public static boolean allowChangeSex;
    public static boolean changeSexAsk;
    public static int changeSexItemId;
    public static long changeSexItemCount;

    public static boolean allowChangeRace;
    public static boolean changeRaceAsk;
    public static int changeRaceItemId;
    public static long changeRaceItemCount;

    public static boolean allowChangeHairStyle;
    public static boolean changeHairStyleAsk;
    public static int changeHairStyleWearItemId;
    public static long changeHairStyleWearItemCount;
    public static int changeHairStyleItemId;
    public static long changeHairStyleItemCount;

    public static boolean allowChangeHairColor;
    public static boolean changeHairColorAsk;
    public static int changeHairColorWearItemId;
    public static long changeHairColorWearItemCount;
    public static int changeHairColorItemId;
    public static long changeHairColorItemCount;

    public static boolean allowChangeFace;
    public static boolean changeFaceAsk;
    public static int changeFaceItemId;
    public static long changeFaceItemCount;
    public static int changeFaceWearItemId;
    public static long changeFaceWearItemCount;

    public static boolean allowBuyFame;
    public static boolean buyFameAsk;
    public static int[] fameCounts;
    public static int[] fameItemIds;
    public static long[] famePriceCounts;

    public static boolean allowBuyRec;
    public static boolean buyRecAsk;
    public static int buyRecItemId;
    public static long buyRecItemCount;

    public static boolean allowBuyLevel;
    public static boolean buyLevelAsk;
    public static int buyLevelItemId;
    public static long buyLevelItemCount;

    public static boolean allowNameColor;
    public static boolean nameColorAsk;
    public static int nameColorItemId;
    public static long nameColorItemCount;

    public static boolean allowTitleColor;
    public static boolean titleColorAsk;
    public static int titleColorItemId;
    public static long titleColorItemCount;

    public static boolean allowUnBanChat;
    public static boolean unBanChatAsk;
    public static int unBanChatItemId;
    public static long unBanChatItemCount;

    public static boolean allowExpandInventory;
    public static boolean expandInventoryAsk;
    public static int expandInventoryItemId;
    public static long expandInventoryItemCount;

    public static boolean allowExpandWarehouse;
    public static boolean expandWarehouseAsk;
    public static int expandWarehouseItemId;
    public static long expandWarehouseItemCount;

    public static boolean allowExpandPrivateStore;
    public static boolean expandPrivateStoreAsk;
    public static int expandPrivateStoreItemId;
    public static long expandPrivateStoreItemCount;

    public static boolean allowNoDropPK;
    public static boolean noDropPKAsk;
    public static int noDropMaxDays;
    public static int noDropPKItemId;
    public static long noDropPKItemCount;

    public static boolean allowRestorePetName;
    public static boolean restorePetNameAsk;
    public static int restorePetNameItemId;
    public static int restorePetNameItemCount;

    public static boolean allowTemporalHero;
    public static boolean temporalHeroAsk;
    public static int[] temporalHeroDays;
    public static int[] temporalHeroItemIds;
    public static long[] temporalHeroItemCounts;

    public static boolean allowClanRename;
    public static boolean clanRenameAsk;
    public static int clanRenameItemId;
    public static long clanRenameItemCount;

    public static boolean allowClanReputation;
    public static boolean clanReputationAsk;
    public static int[] clanReputationCounts;
    public static int[] clanReputationItemIds;
    public static long[] clanReputationItemCounts;

    public static boolean allowBuyClanLevel;
    public static boolean buyClanLevelAsk;
    @Setting(minValue = 2, maxValue = 11)
    public static int[] buyClanLevels;
    public static int[] buyClanLevelItemIds;
    public static long[] buyClanLevelItemCounts;

    public static boolean allowClanSkill;
    public static boolean clanSkillAsk;

    public static boolean paAsk;
    @Setting(minValue = 0, maxValue = 2)
    public static final int rateBonusType = PremiumBonus.NO_BONUS;
    @Setting(canNull = true)
    public static final int[] paBossRatedItems = new int[0];

    public static boolean allowSubscription;
    public static boolean subscriptionAsk;
    public static int subscriptionMaxBuyHour;
    public static int subscriptionItemId;
    public static long subscriptionItemCount;

    public static boolean allowClanPenaltyRemove;
    public static boolean clanPenaltyRemoveAsk;
    public static long clanPenaltyPrice;
    public static int clanPenaltyPriceItem;

    public static boolean allowPlayerPenaltyRemove;
    public static boolean playerPenaltyRemoveAsk;
    public static int playerPenaltyPrice;
    public static int playerPenaltyPriceItem;

    public static boolean allowChangePassword;
    public static boolean changePasswordAsk;

    public static boolean allowOlyReset;
    public static boolean olyResetAsk;
    public static int olyResetItem;
    public static int olyResetItemCount;

    public static boolean allowBuySP;
    public static boolean buySPAsk;
    public static long[] spCounts;
    public static int[] spItemIds;
    public static long[] spPriceCounts;

    @Setting(canNull = true)
    public static String[] ratingIgnoreNicks = new String[0];
}
