package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 03.12.2015.
 */
@Configuration("services.json")
public class ServicesConfig {
    @Setting(name = "EnableMailActivation")
    public static boolean SERVICE_ENABLED_MAIL_ACTIVATION_SERVICE;

    @Setting(name = "NickChangeEnabled")
    public static boolean SERVICES_CHANGE_NICK_ENABLED; //TODO сделать

    @Setting(name = "NickChangePrice")
    public static int SERVICES_CHANGE_NICK_PRICE;

    @Setting(name = "NickChangeItem")
    public static int SERVICES_CHANGE_NICK_ITEM;

    @Setting(name = "PetNameChangeEnabled")
    public static boolean SERVICES_CHANGE_PET_NAME_ENABLED; //TODO сделать

    @Setting(name = "PetNameChangePrice")
    public static int SERVICES_CHANGE_PET_NAME_PRICE; //TODO сделать

    @Setting(name = "PetNameChangeItem")
    public static int SERVICES_CHANGE_PET_NAME_ITEM; //TODO сделать

    @Setting(name = "BabyPetExchangeEnabled")
    public static boolean SERVICES_EXCHANGE_BABY_PET_ENABLED; //TODO сделать

    @Setting(name = "BabyPetExchangePrice")
    public static int SERVICES_EXCHANGE_BABY_PET_PRICE; //TODO сделать

    @Setting(name = "BabyPetExchangeItem")
    public static int SERVICES_EXCHANGE_BABY_PET_ITEM; //TODO сделать

    @Setting(name = "SexChangeEnabled")
    public static boolean SERVICES_CHANGE_SEX_ENABLED; //TODO сделать

    @Setting(name = "SexChangePrice")
    public static int SERVICES_CHANGE_SEX_PRICE;

    @Setting(name = "SexChangeItem")
    public static int SERVICES_CHANGE_SEX_ITEM;

    @Setting(name = "BaseChangeEnabled")
    public static boolean SERVICES_CHANGE_BASE_ENABLED; //TODO сделать

    @Setting(name = "BaseChangePrice")
    public static int SERVICES_CHANGE_BASE_PRICE;

    @Setting(name = "BaseChangeItem")
    public static int SERVICES_CHANGE_BASE_ITEM;

    @Setting(name = "SeparateSubEnabled")
    public static boolean SERVICES_SEPARATE_SUB_ENABLED; //TODO сделать

    @Setting(name = "SeparateSubPrice")
    public static int SERVICES_SEPARATE_SUB_PRICE;

    @Setting(name = "SeparateSubItem")
    public static int SERVICES_SEPARATE_SUB_ITEM;

    @Setting(name = "NickColorChangeEnabled")
    public static boolean SERVICES_CHANGE_NICK_COLOR_ENABLED; //TODO сделать

    @Setting(name = "NickColorChangeList")
    public static String[] SERVICES_CHANGE_NICK_COLOR_LIST;

    @Setting(name = "NickColorChangePrice")
    public static int SERVICES_CHANGE_NICK_COLOR_PRICE;

    @Setting(name = "NickColorChangeItem")
    public static int SERVICES_CHANGE_NICK_COLOR_ITEM;

    @Setting(name = "NoblessSellEnabled")
    public static boolean SERVICES_NOBLESS_SELL_ENABLED; //TODO сделать

    @Setting(name = "NoblessSellPrice")
    public static int SERVICES_NOBLESS_SELL_PRICE;

    @Setting(name = "NoblessSellItem")
    public static int SERVICES_NOBLESS_SELL_ITEM;

    @Setting(name = "ExpandInventoryEnabled")
    public static boolean SERVICES_EXPAND_INVENTORY_ENABLED;

    @Setting(name = "ExpandInventoryPrice")
    public static int SERVICES_EXPAND_INVENTORY_PRICE;

    @Setting(name = "ExpandInventoryItem")
    public static int SERVICES_EXPAND_INVENTORY_ITEM;

    @Setting(name = "ExpandWarehouseEnabled")
    public static boolean SERVICES_EXPAND_WAREHOUSE_ENABLED;

    @Setting(name = "ExpandWarehousePrice")
    public static int SERVICES_EXPAND_WAREHOUSE_PRICE;

    @Setting(name = "ExpandWarehouseItem")
    public static int SERVICES_EXPAND_WAREHOUSE_ITEM;

    @Setting(name = "ExpandCWHEnabled")
    public static boolean SERVICES_EXPAND_CWH_ENABLED;

    @Setting(name = "ExpandCWHPrice")
    public static int SERVICES_EXPAND_CWH_PRICE;

    @Setting(name = "ExpandCWHItem")
    public static int SERVICES_EXPAND_CWH_ITEM;

    @Setting(name = "ClanNameChangeEnabled")
    public static boolean SERVICES_CHANGE_CLAN_NAME_ENABLED; //TODO сделать

    @Setting(name = "ClanNameChangePrice")
    public static int SERVICES_CHANGE_CLAN_NAME_PRICE;

    @Setting(name = "ClanNameChangeItem")
    public static int SERVICES_CHANGE_CLAN_NAME_ITEM;

    @Setting(name = "BashEnabled")
    public static boolean SERVICES_BASH_ENABLED; //TODO сделать

    @Setting(name = "BashSkipDownload")
    public static boolean SERVICES_BASH_SKIP_DOWNLOAD; //TODO сделать

    @Setting(name = "BashReloadTime")
    public static int SERVICES_BASH_RELOAD_TIME; //TODO сделать

    @Setting(name = "AllowOfflineTrade")
    public static boolean SERVICES_OFFLINE_TRADE_ALLOW;

    @Setting(name = "AllowOfflineTradeOnlyOffshore")
    public static boolean SERVICES_OFFLINE_TRADE_ALLOW_OFFSHORE;

    @Setting(name = "OfflineMinLevel")
    public static int SERVICES_OFFLINE_TRADE_MIN_LEVEL;

    @Setting(name = "OfflineTradeChangeNameColor")
    public static boolean SERVICES_OFFLINE_TRADE_CHANGE_NAME_COLOR;

    @Setting(name = "OfflineTradeNameColor", method = "offlineTradeNameColor")
    public static int SERVICES_OFFLINE_TRADE_NAME_COLOR;

    @Setting(name = "OfflineTradePrice")
    public static int SERVICES_OFFLINE_TRADE_PRICE;

    @Setting(name = "OfflineTradePriceItem")
    public static int SERVICES_OFFLINE_TRADE_PRICE_ITEM;

    @Setting(name = "OfflineTradeDaysToKick", increase = 86400L)
    public static long SERVICES_OFFLINE_TRADE_SECONDS_TO_KICK;

    @Setting(name = "OfflineRestoreAfterRestart")
    public static boolean SERVICES_OFFLINE_TRADE_RESTORE_AFTER_RESTART;

    @Setting(name = "NoTradeOnlyOffline")
    public static boolean SERVICES_NO_TRADE_ONLY_OFFLINE;

    @Setting(name = "TradeTax")
    public static final double SERVICES_TRADE_TAX = 1.6;

    @Setting(name = "OffshoreTradeTax")
    public static final double SERVICES_OFFSHORE_TRADE_TAX = 1.6;

    @Setting(name = "TradeTaxOnlyOffline")
    public static boolean SERVICES_TRADE_TAX_ONLY_OFFLINE;

    @Setting(name = "TradeOnlyFar")
    public static boolean SERVICES_TRADE_ONLY_FAR;

    @Setting(name = "TradeRadius")
    public static int SERVICES_TRADE_RADIUS;

    @Setting(name = "GiranHarborZone")
    public static boolean SERVICES_GIRAN_HARBOR_ENABLED;

    @Setting(name = "ParnassusZone")
    public static boolean SERVICES_PARNASSUS_ENABLED;

    @Setting(name = "ParnassusNoTax")
    public static boolean SERVICES_PARNASSUS_NOTAX;

    @Setting(name = "ParnassusPrice")
    public static long SERVICES_PARNASSUS_PRICE;

    @Setting(name = "NoCastleTaxInOffshore")
    public static boolean SERVICES_OFFSHORE_NO_CASTLE_TAX;

    @Setting(name = "MinLevelForTrade")
    public static int SERVICES_TRADE_MIN_LEVEL;

    @Setting(name = "AllowLottery")
    public static boolean SERVICES_ALLOW_LOTTERY;

    @Setting(name = "LotteryPrize")
    public static int SERVICES_LOTTERY_PRIZE;

    @Setting(name = "AltLotteryPrice")
    public static int SERVICES_ALT_LOTTERY_PRICE;

    @Setting(name = "LotteryTicketPrice")
    public static int SERVICES_LOTTERY_TICKET_PRICE;

    @Setting(name = "Lottery5NumberRate")
    public static double SERVICES_LOTTERY_5_NUMBER_RATE;

    @Setting(name = "Lottery4NumberRate")
    public static double SERVICES_LOTTERY_4_NUMBER_RATE;

    @Setting(name = "Lottery3NumberRate")
    public static double SERVICES_LOTTERY_3_NUMBER_RATE;

    @Setting(name = "Lottery2and1NumberPrize")
    public static int SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;

    @Setting(name = "AllowRoulette")
    public static boolean SERVICES_ALLOW_ROULETTE;

    @Setting(name = "RouletteMinBet")
    public static long SERVICES_ROULETTE_MIN_BET = 1L;

    @Setting(name = "RouletteMaxBet")
    public static long SERVICES_ROULETTE_MAX_BET = Long.MAX_VALUE;

    @Setting(name = "UseItemBrokerItemSearch")
    public static boolean ITEM_BROKER_ITEM_SEARCH; //TODO: сделать

    @Setting(name = "BestProductCount")
    public static int SERVICES_PREMIUMSHOP_BEST_PRODUCT_COUNT;

    @Setting(name = "PlayerProductCount")
    public static int SERVICES_PREMIUMSHOP_PLAYER_PRODUCT_COUNT;

    @Setting(name = "PrimeShopFilename")
    public static String SERVICES_PREMIUMSHOP_PREMIUMSHOP_FILENAME;

    // Auto-enchant service
    @Setting(name = "AllowEnchantService")
    public static boolean ALLOW_ENCHANT_SERVICE;

    @Setting(name = "EnchantServiceOnlyForPremium")
    public static boolean ENCHANT_SERVICE_ONLY_FOR_PREMIUM;

    @Setting(name = "EnchantConsumeItem")
    public static int ENCHANT_CONSUME_ITEM;

    @Setting(name = "EnchantConsumeItemCount")
    public static int ENCHANT_CONSUME_ITEM_COUNT;

    @Setting(name = "EnchantMaxItemLimit")
    public static int ENCHANT_MAX_ITEM_LIMIT;

    @Setting(name = "EnchantAllowScrolls")
    public static boolean ENCHANT_ALLOW_SCROLLS;

    @Setting(name = "EnchantAllowAttribute")
    public static boolean ENCHANT_ALLOW_ATTRIBUTE;

    @Setting(name = "EnchantScrollChanceCorrect")
    public static int ENCHANT_SCROLL_CHANCE_CORRECT;

    @Setting(name = "EnchantAttributeChanceCorrect")
    public static int ENCHANT_ATTRIBUTE_CHANCE_CORRECT;

    public static int enchantServiceDefaultLimit;

    public static int enchantServiceDefaultEnchant;

    public static int enchantServiceDefaultAttribute;

    @Setting(name = "EnchantAllowBelts")
    public static boolean ENCHANT_ALLOW_BELTS;

    @Setting(name = "EnchantMaxWeapon")
    public static int ENCHANT_MAX_WEAPON;

    @Setting(name = "EnchantMaxArmor")
    public static int ENCHANT_MAX_ARMOR;

    @Setting(name = "EnchantMaxJewelry")
    public static int ENCHANT_MAX_JEWELRY;

    @Setting(name = "SafeEnchantCommon")
    public static int SAFE_ENCHANT_COMMON;

    public static int blessedFailEnchantLvl;
    public static int olfBlessedFailEnchantLvl;

    public static boolean allowHwidLock;
    public static long minReuseEnchantMillis;
    public static int rouletteItemId;
    public static int[] VISUAL_DISALLOWED_ITEMS;
    public static int[] VISUAL_COSTUMES;
    public static int VISUAL_ARMOR_PRICE_ID;
    public static int VISUAL_ARMOR_PRICE_COUNT;
    public static boolean VISUAL_RETURN_VISUAL_ITEM;
    public static int VISUAL_WEAPON_PRICE_ID;
    public static int VISUAL_WEAPON_PRICE_COUNT;
    public static int VISUAL_COSTUME_PRICE_ID;
    public static int VISUAL_COSTUME_PRICE_COUNT;
    public static int[] VISUAL_DISALLOWED_FOR_VISUAL_ITEMS;

    private void offlineTradeNameColor(final String value) {
        SERVICES_OFFLINE_TRADE_NAME_COLOR = Integer.decode("0x" + value);
    }

}
