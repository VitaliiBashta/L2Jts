package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.object.Player;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("other.json")
public class OtherConfig {
    @Setting(name = "UseDeepBlueDropRules")
    public static boolean DEEPBLUE_DROP_RULES;

    @Setting(name = "DeepBlueDropMaxDiff")
    public static int DEEPBLUE_DROP_MAXDIFF;

    @Setting(name = "DeepBlueDropRaidMaxDiff")
    public static int DEEPBLUE_DROP_RAID_MAXDIFF;

    @Setting(name = "BaseSlotsForNoDwarf")
    public static int INVENTORY_BASE_NO_DWARF;

    @Setting(name = "BaseSlotsForDwarf")
    public static int INVENTORY_BASE_DWARF;

    @Setting(name = "BaseSlotsForGMPlayer")
    public static int INVENTORY_BASE_GM;

    @Setting(name = "BaseSlotsForQuests")
    public static int INVENTORY_BASE_QUEST;

    @Setting(name = "MaxSlotsForNoDwarf")
    public static int INVENTORY_MAX_NO_DWARF;

    @Setting(name = "MaxSlotsForDwarf")
    public static int INVENTORY_MAX_FOR_DWARF;

    @Setting(name = "BaseWarehouseSlotsForNoDwarf")
    public static int WAREHOUSE_SLOTS_NO_DWARF;

    @Setting(name = "BaseWarehouseSlotsForDwarf")
    public static int WAREHOUSE_SLOTS_DWARF;

    @Setting(name = "MaxWarehouseSlotsForNoDwarf")
    public static int MAX_WAREHOUSE_SLOTS_NO_DWARF;

    @Setting(name = "MaxWarehouseSlotsForDwarf")
    public static int MAX_WAREHOUSE_SLOTS_DWARF;

    @Setting(name = "MaximumWarehouseSlotsForClan")
    public static int WAREHOUSE_SLOTS_CLAN;

    @Setting(name = "MaximumFreightSlots")
    public static int FREIGHT_SLOTS;

    @Setting(name = "ShowEnchantEffectResult")
    public static boolean SHOW_ENCHANT_EFFECT_RESULT;

    @Setting(name = "RegenSitWait")
    public static boolean REGEN_SIT_WAIT;

    @Setting(name = "UnstuckSkill")
    public static boolean UNSTUCK_SKILL;

    @Setting(name = "RespawnRestoreCP")
    public static double RESPAWN_RESTORE_CP;

    @Setting(name = "RespawnRestoreHP")
    public static double RESPAWN_RESTORE_HP;

    @Setting(name = "RespawnRestoreMP")
    public static double RESPAWN_RESTORE_MP;

    @Setting(name = "BasePvtStoreSlotsDwarf")
    public static int BASE_PVTSTORE_SLOTS_DWARF;

    @Setting(name = "BasePvtStoreSlotsOther")
    public static int BASE_PVTSTORE_SLOTS_OTHER;

    @Setting(name = "MaxPvtStoreSlotsDwarf")
    public static int MAX_PVTSTORE_SLOTS_DWARF;

    @Setting(name = "MaxPvtStoreSlotsOther")
    public static int MAX_PVTSTORE_SLOTS_OTHER;

    @Setting(name = "MaxPvtManufactureSlots")
    public static int MAX_PVTCRAFT_SLOTS;

    @Setting(name = "SendStatusTradeJustOffline")
    public static boolean SENDSTATUS_TRADE_JUST_OFFLINE;

    @Setting(name = "SendStatusTradeMod")
    public static double SENDSTATUS_TRADE_MOD;

    @Setting(name = "GMNameColour", method = "gmNameColour")
    public static int GM_NAME_COLOUR;

    @Setting(name = "NormalNameColour", method = "normalNameColour")
    public static int NORMAL_NAME_COLOUR;

    @Setting(name = "ClanleaderNameColour", method = "clanleaderNameColour")
    public static int CLANLEADER_NAME_COLOUR;

    @Setting(name = "GMHeroAura")
    public static boolean GM_HERO_AURA;

    @Setting(name = "MultisellPageSize")
    public static int MULTISELL_SIZE;

	/*
	@Setting(name = "AnnounceMammonSpawn") //TODO : сделать?
	public static boolean ANNOUNCE_MAMMON_SPAWN;
	*/

    @Setting(name = "Summon_Price")
    public static int SUMMON_PRICE;

    @Setting(name = "Summon_Price_Cl")
    public static int SUMMON_PRICE_CL;

    @Setting(name = "Summon_Price_Id")
    public static int SUMMON_PRICE_ID;

    @Setting(name = "CancelPvPBuffTime")
    public static boolean CANCEL_PVP_BUFF_TIME;

    @Setting(ignore = true)
    public static boolean DEBUG;

    @Setting(name = "CustomFakeDeath")
    public static boolean CUSTOM_FAKE_DEATH;

    @Setting(name = "SkillIgnoreRaidBoss", canNull = true)
    public static int[] skillIgnore = new int[0];

    @Setting(name = "WarPvP")
    public static boolean warPvp;

    @Setting(increase = 1000)
    public static int holdShoutTime;

    @Setting(increase = 1000)
    public static int holdTradeTime;

    @Setting(increase = 1000)
    public static int holdHeroTime;

    @Setting(canNull = true)
    private static ZoneType[] tradeZone = new ZoneType[0];

    public static boolean checkTradeZone(final Player player) {
        if (tradeZone.length == 0) {
            return true;
        }

        for (final ZoneType zoneType : tradeZone) {
            if (player.isInZone(zoneType))
                return true;
        }
        return false;
    }

    private void gmNameColour(final String value) {
        GM_NAME_COLOUR = Integer.decode("0x" + value);
    }

    private void normalNameColour(final String value) {
        NORMAL_NAME_COLOUR = Integer.decode("0x" + value);
    }

    private void clanleaderNameColour(final String value) {
        CLANLEADER_NAME_COLOUR = Integer.decode("0x" + value);
    }
}

