package org.mmocore.gameserver.stats;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.base.BaseStats;
import org.mmocore.gameserver.model.base.ClassType2;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.conditions.ConditionPlayerState;
import org.mmocore.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.stat.AddStatFuncUtil;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;

/**
 * Коллекция предопределенных классов функций.
 */
public class StatFunctions {
    public static void addPredefinedFuncs(final Creature cha) {
        if (cha.isPlayer()) {
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_CP_RATE));
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_HP_RATE));
            cha.addStatFunc(FuncMultRegenResting.getFunc(Stats.REGENERATE_MP_RATE));
            cha.addStatFunc(FuncMultRegenStanding.getFunc(Stats.REGENERATE_MP_RATE));
            cha.addStatFunc(FuncMultRegenRunning.getFunc(Stats.REGENERATE_MP_RATE));

            cha.addStatFunc(FuncMaxCpMul.func);

            cha.addStatFunc(FuncAttackRange.func);

            cha.addStatFunc(FuncDyeSTR.func);
            cha.addStatFunc(FuncDyeDEX.func);
            cha.addStatFunc(FuncDyeINT.func);
            cha.addStatFunc(FuncDyeMEN.func);
            cha.addStatFunc(FuncDyeCON.func);
            cha.addStatFunc(FuncDyeWIT.func);

            cha.addStatFunc(FuncInventory.func);
            cha.addStatFunc(FuncWarehouse.func);
            cha.addStatFunc(FuncTradeLimit.func);

            cha.addStatFunc(FuncSDefPlayers.func);

            cha.addStatFunc(FuncMaxHpLimit.func);
            cha.addStatFunc(FuncMaxMpLimit.func);
            cha.addStatFunc(FuncMaxCpLimit.func);
            cha.addStatFunc(FuncRunSpdLimit.func);
            cha.addStatFunc(FuncRunSpdLimit.func);
            cha.addStatFunc(FuncPDefLimit.func);
            cha.addStatFunc(FuncMDefLimit.func);
            cha.addStatFunc(FuncPAtkLimit.func);
            cha.addStatFunc(FuncMAtkLimit.func);
        }

        if (cha.isPlayer()) {
            cha.addStatFunc(FuncMaxHpMul.func);
            cha.addStatFunc(FuncMaxMpMul.func);
            cha.addStatFunc(FuncPAtkMul.func);
            cha.addStatFunc(FuncMAtkMul.func);
            cha.addStatFunc(FuncPDefMul.func);
            cha.addStatFunc(FuncMDefMul.func);
            cha.addStatFunc(FuncMoveSpeedMul.func);
        }
        if (cha.isPet()) {
            cha.addStatFunc(FuncMoveSpeedMul.func);
        }
        if (cha.isSummon()) {
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.FIRE));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.WATER));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.EARTH));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.WIND));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.HOLY));
            cha.addStatFunc(FuncAttributeAttackSet.getFunc(Element.UNHOLY));

            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.FIRE));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.WATER));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.EARTH));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.WIND));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.HOLY));
            cha.addStatFunc(FuncAttributeDefenceSet.getFunc(Element.UNHOLY));
        }
        if (!cha.isPet() && !cha.isSummon()) {
            cha.addStatFunc(FuncSDefInit.func);
            cha.addStatFunc(FuncSDefAll.func);
        }
        AddStatFuncUtil.addStatCreature(cha);

        cha.addStatFunc(FuncPAtkSpeedMul.func);
        cha.addStatFunc(FuncMAtkSpeedMul.func);
        cha.addStatFunc(FuncAccuracyAdd.func);
        cha.addStatFunc(FuncEvasionAdd.func);
        cha.addStatFunc(FuncPAtkSpdLimit.func);
        cha.addStatFunc(FuncMAtkSpdLimit.func);
        cha.addStatFunc(FuncCAtkLimit.func);
        cha.addStatFunc(FuncEvasionLimit.func);
        cha.addStatFunc(FuncAccuracyLimit.func);
        cha.addStatFunc(FuncCritLimit.func);
        cha.addStatFunc(FuncMCritLimit.func);

        cha.addStatFunc(FuncMCriticalRateMul.func);
        cha.addStatFunc(FuncPCriticalRateMul.func);
        cha.addStatFunc(FuncPDamageResists.func);
        cha.addStatFunc(FuncMDamageResists.func);

        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.FIRE));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.WATER));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.EARTH));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.WIND));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.HOLY));
        cha.addStatFunc(FuncAttributeAttackInit.getFunc(Element.UNHOLY));

        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.FIRE));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.WATER));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.EARTH));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.WIND));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.HOLY));
        cha.addStatFunc(FuncAttributeDefenceInit.getFunc(Element.UNHOLY));
    }

    private static class FuncMultRegenResting extends Func {
        static final FuncMultRegenResting[] func = new FuncMultRegenResting[Stats.NUM_STATS];

        private FuncMultRegenResting(final Stats stat) {
            super(stat, 0x20, null);
            setCondition(new ConditionPlayerState(CheckPlayerState.RESTING, true));
        }

        static Func getFunc(final Stats stat) {
            final int pos = stat.ordinal();
            if (func[pos] == null) {
                func[pos] = new FuncMultRegenResting(stat);
            }
            return func[pos];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (creature.isPlayer() && creature.getLevel() <= 40 && ((Player) creature).getPlayerClassComponent().getClassId().getLevel().ordinal() < 3 && stat == Stats.REGENERATE_HP_RATE) {
                return initialValue * 6.; // TODO: переделать красивее
            } else {
                return initialValue * 1.5;
            }
        }
    }

    private static class FuncMultRegenStanding extends Func {
        static final FuncMultRegenStanding[] func = new FuncMultRegenStanding[Stats.NUM_STATS];

        private FuncMultRegenStanding(final Stats stat) {
            super(stat, 0x20, null);
            setCondition(new ConditionPlayerState(CheckPlayerState.STANDING, true));
        }

        static Func getFunc(final Stats stat) {
            final int pos = stat.ordinal();
            if (func[pos] == null) {
                func[pos] = new FuncMultRegenStanding(stat);
            }
            return func[pos];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * 1.1;
        }
    }

    private static class FuncMultRegenRunning extends Func {
        static final FuncMultRegenRunning[] func = new FuncMultRegenRunning[Stats.NUM_STATS];

        private FuncMultRegenRunning(final Stats stat) {
            super(stat, 0x30, null);
            setCondition(new ConditionPlayerState(CheckPlayerState.RUNNING, true));
        }

        static Func getFunc(final Stats stat) {
            final int pos = stat.ordinal();
            if (func[pos] == null) {
                func[pos] = new FuncMultRegenRunning(stat);
            }
            return func[pos];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * 0.7;
        }
    }

    public static class FuncPAtkMul extends Func {
        public static final FuncPAtkMul func = new FuncPAtkMul();

        private FuncPAtkMul() {
            super(Stats.POWER_ATTACK, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.STR.calcBonus(creature) * creature.getLevelMod();
        }
    }

    public static class FuncMAtkMul extends Func {
        public static final FuncMAtkMul func = new FuncMAtkMul();

        private FuncMAtkMul() {
            super(Stats.MAGIC_ATTACK, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            //{Wpn*(lvlbn^2)*[(1+INTbn)^2]+Msty}
            final double ib = BaseStats.INT.calcBonus(creature);
            final double lvlb = creature.getLevelMod();

            return initialValue * lvlb * lvlb * ib * ib;
        }
    }

    public static class FuncPDefMul extends Func {
        public static final FuncPDefMul func = new FuncPDefMul();

        private FuncPDefMul() {
            super(Stats.POWER_DEFENCE, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * creature.getLevelMod();
        }
    }

    public static class FuncMDefMul extends Func {
        public static final FuncMDefMul func = new FuncMDefMul();

        private FuncMDefMul() {
            super(Stats.MAGIC_DEFENCE, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.MEN.calcBonus(creature) * creature.getLevelMod();
        }
    }

    private static class FuncAttackRange extends Func {
        static final FuncAttackRange func = new FuncAttackRange();

        private FuncAttackRange() {
            super(Stats.POWER_ATTACK_RANGE, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;
            final WeaponTemplate weapon = creature.getActiveWeaponItem();

            if (weapon != null) {
                value += weapon.getAttackRange();
            }

            return value;
        }
    }

    private static class FuncAccuracyAdd extends Func {
        static final FuncAccuracyAdd func = new FuncAccuracyAdd();

        private FuncAccuracyAdd() {
            super(Stats.ACCURACY_COMBAT, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;
            final int level = creature.getLevel();
            final int dex = creature.getDEX();
            final double mod = creature.isPlayable() ? 6 : creature.getTemplate().getBaseHitModify() != 0 ? creature.getTemplate().getBaseHitModify() : 6;

            if (level < 78 || dex > 75 || level > 85) {
                value += Math.sqrt(dex) * mod + level;

                if (level > 69) {
                    value += level - 69;
                }

                if (level > 77) {
                    value += level - 77;
                }

                if (creature.isSummon() || creature.isPet()) {
                    value += 5;
                }
            } else {
                if (creature.isSummon() || creature.isPet()) {
                    value += BaseStats.SUMMON_EVASION_OR_ATTACK_ACCURACY_78_85[level - 78][dex];
                } else {
                    value += BaseStats.EVASION_OR_ATTACK_ACCURACY_78_85[level - 78][dex];
                }
            }
            return value;
        }
    }

    private static class FuncEvasionAdd extends Func {
        static final FuncEvasionAdd func = new FuncEvasionAdd();

        private FuncEvasionAdd() {
            super(Stats.EVASION_RATE, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;
            final int level = creature.getLevel();
            final int dex = creature.getDEX();
            final double mod = creature.isPlayable() ? 6 : creature.getTemplate().getBaseAvoidModify() != 0 ? creature.getTemplate().getBaseAvoidModify() : 6;

            if (level < 78 || dex > 75 || level > 85) {
                value += Math.sqrt(dex) * mod + level;

                if (level > 69) {
                    value += level - 69;
                }

                if (level > 77) {
                    value += level - 77;
                }
            } else {
                if (creature.isSummon() || creature.isPet()) {
                    value += BaseStats.SUMMON_EVASION_OR_ATTACK_ACCURACY_78_85[level - 78][dex];
                } else {
                    value += BaseStats.EVASION_OR_ATTACK_ACCURACY_78_85[level - 78][dex];
                }
            }
            return value;
        }
    }

    private static class FuncMCriticalRateMul extends Func {
        static final FuncMCriticalRateMul func = new FuncMCriticalRateMul();

        private FuncMCriticalRateMul() {
            super(Stats.MCRITICAL_RATE, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.WIT.calcBonus(creature);
        }
    }

    private static class FuncPCriticalRateMul extends Func {
        static final FuncPCriticalRateMul func = new FuncPCriticalRateMul();

        private FuncPCriticalRateMul() {
            super(Stats.CRITICAL_BASE, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final int mod = creature.isPlayer() ? 10 : 1;
            final WeaponTemplate weapon = creature.getActiveWeaponItem();
            if (weapon != null) {
                value = weapon.getCritical() * mod * BaseStats.DEX.calcBonus(creature);
            } else {
                value = initialValue * mod * BaseStats.DEX.calcBonus(creature);
            }
            value *= 0.01 * creature.calcStat(Stats.CRITICAL_RATE, target, skill);

            return value;
        }
    }

    private static class FuncMoveSpeedMul extends Func {
        static final FuncMoveSpeedMul func = new FuncMoveSpeedMul();

        private FuncMoveSpeedMul() {
            super(Stats.RUN_SPEED, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.DEX.calcBonus(creature);
        }
    }

    private static class FuncPAtkSpeedMul extends Func {
        static final FuncPAtkSpeedMul func = new FuncPAtkSpeedMul();

        private FuncPAtkSpeedMul() {
            super(Stats.POWER_ATTACK_SPEED, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.DEX.calcBonus(creature);
        }
    }

    /**
     * [K]:
     * ((100 + (WITBonus)) / 100) * 333 - Дефолт
     * ((100 + (WITBonus)) / 100) * 166(.5) - Для магов(ибо скилл + 50% кастСпида)
     */
    private static class FuncMAtkSpeedMul extends Func {
        static final FuncMAtkSpeedMul func = new FuncMAtkSpeedMul();

        private FuncMAtkSpeedMul() {
            super(Stats.MAGIC_ATTACK_SPEED, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.WIT.calcBonus(creature);
        }
    }

    private static class FuncDyeSTR extends Func {
        static final FuncDyeSTR func = new FuncDyeSTR();

        private FuncDyeSTR() {
            super(Stats.STAT_STR, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = creature.getPlayer();
            if (player != null) {
                value = Math.max(1, value + player.getDyeStatSTR());
            }

            return value;
        }
    }

    private static class FuncDyeDEX extends Func {
        static final FuncDyeDEX func = new FuncDyeDEX();

        private FuncDyeDEX() {
            super(Stats.STAT_DEX, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = creature.getPlayer();
            if (player != null) {
                value = Math.max(1, value + player.getDyeStatDEX());
            }

            return value;
        }
    }

    private static class FuncDyeINT extends Func {
        static final FuncDyeINT func = new FuncDyeINT();

        private FuncDyeINT() {
            super(Stats.STAT_INT, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = creature.getPlayer();
            if (player != null) {
                value = Math.max(1, value + player.getDyeStatINT());
            }

            return value;
        }
    }

    private static class FuncDyeMEN extends Func {
        static final FuncDyeMEN func = new FuncDyeMEN();

        private FuncDyeMEN() {
            super(Stats.STAT_MEN, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = (Player) creature;
            if (player != null) {
                value = Math.max(1, value + player.getDyeStatMEN());
            }

            return value;
        }
    }

    private static class FuncDyeCON extends Func {
        static final FuncDyeCON func = new FuncDyeCON();

        private FuncDyeCON() {
            super(Stats.STAT_CON, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = (Player) creature;
            if (player != null) {
                value = Math.max(1, value + player.getDyeStatCON());
            }

            return value;
        }
    }

    private static class FuncDyeWIT extends Func {
        static final FuncDyeWIT func = new FuncDyeWIT();

        private FuncDyeWIT() {
            super(Stats.STAT_WIT, 0x10, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = (Player) creature;
            if (player != null) {
                value = Math.max(1, value + player.getDyeStatWIT());
            }

            return value;
        }
    }

    public static class FuncMaxHpMul extends Func {
        public static final FuncMaxHpMul func = new FuncMaxHpMul();

        private FuncMaxHpMul() {
            super(Stats.MAX_HP, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.CON.calcBonus(creature);
        }
    }

    private static class FuncMaxCpMul extends Func {
        static final FuncMaxCpMul func = new FuncMaxCpMul();

        private FuncMaxCpMul() {
            super(Stats.MAX_CP, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double cpSSmod = 1;
            final int sealOwnedBy = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE);
            final int playerCabal = SevenSigns.getInstance().getPlayerCabal((Player) creature);

            if (sealOwnedBy != SevenSigns.CABAL_NULL) {
                if (playerCabal == sealOwnedBy) {
                    cpSSmod = 1.1;
                } else {
                    cpSSmod = 0.9;
                }
            }

            return initialValue * BaseStats.CON.calcBonus(creature) * cpSSmod;
        }
    }

    public static class FuncMaxMpMul extends Func {
        public static final FuncMaxMpMul func = new FuncMaxMpMul();

        private FuncMaxMpMul() {
            super(Stats.MAX_MP, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue * BaseStats.MEN.calcBonus(creature);
        }
    }

    private static class FuncPDamageResists extends Func {
        static final FuncPDamageResists func = new FuncPDamageResists();

        private FuncPDamageResists() {
            super(Stats.PHYSICAL_DAMAGE, 0x30, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (target.isRaid() && creature.getLevel() - target.getLevel() > ServerConfig.RAID_MAX_LEVEL_DIFF) {
                return 1;
            }

            double value = initialValue;

            // TODO переделать на ту же систему, что у эффектов
            final WeaponTemplate weapon = creature.getActiveWeaponItem();
            if (weapon == null) {
                value *= 0.01 * target.calcStat(Stats.FIST_WPN_VULNERABILITY, creature, skill);
            } else if (weapon.getItemType().getDefence() != null) {
                value *= 0.01 * target.calcStat(weapon.getItemType().getDefence(), creature, skill);
            }

            value = Formulas.calcDamageResists(skill, creature, target, value);

            return value;
        }
    }

    private static class FuncMDamageResists extends Func {
        static final FuncMDamageResists func = new FuncMDamageResists();

        private FuncMDamageResists() {
            super(Stats.MAGIC_DAMAGE, 0x30, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (target.isRaid() && Math.abs(creature.getLevel() - target.getLevel()) > ServerConfig.RAID_MAX_LEVEL_DIFF) {
                return 1;
            }

            return Formulas.calcDamageResists(skill, creature, target, initialValue);
        }
    }

    private static class FuncInventory extends Func {
        static final FuncInventory func = new FuncInventory();

        private FuncInventory() {
            super(Stats.INVENTORY_LIMIT, 0x01, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = creature.getPlayer();

            if (player.isGM()) {
                value = OtherConfig.INVENTORY_BASE_GM;
            } else if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf) {
                value = OtherConfig.INVENTORY_BASE_DWARF;
            } else {
                value = OtherConfig.INVENTORY_BASE_NO_DWARF;
            }

            final int maxInventory = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.INVENTORY_MAX_FOR_DWARF : OtherConfig.INVENTORY_MAX_NO_DWARF;
            value += player.getPremiumAccountComponent().getExpandInventory();
            value = Math.min(value, maxInventory);

            return value;
        }
    }

    private static class FuncWarehouse extends Func {
        static final FuncWarehouse func = new FuncWarehouse();

        private FuncWarehouse() {
            super(Stats.STORAGE_LIMIT, 0x01, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;

            final Player player = (Player) creature;

            if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf) {
                value = OtherConfig.WAREHOUSE_SLOTS_DWARF;
            } else {
                value = OtherConfig.WAREHOUSE_SLOTS_NO_DWARF;
            }
            final int maxWarehouse = player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.MAX_WAREHOUSE_SLOTS_DWARF : OtherConfig.MAX_WAREHOUSE_SLOTS_NO_DWARF;
            value += player.getPremiumAccountComponent().getExpandWarehouse();
            value = Math.min(value, maxWarehouse);
            return value;
        }
    }

    private static class FuncTradeLimit extends Func {
        static final FuncTradeLimit func = new FuncTradeLimit();

        private FuncTradeLimit() {
            super(Stats.TRADE_LIMIT, 0x01, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            double value = initialValue;
            final Player _cha = creature.getPlayer();
            if (_cha.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf) {
                value = OtherConfig.BASE_PVTSTORE_SLOTS_DWARF;
            } else {
                value = OtherConfig.BASE_PVTSTORE_SLOTS_OTHER;
            }
            final int maxTrade = _cha.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.dwarf ? OtherConfig.MAX_PVTSTORE_SLOTS_DWARF : OtherConfig.MAX_PVTSTORE_SLOTS_OTHER;
            value += _cha.getPremiumAccountComponent().getPrivateStore();
            value = Math.min(value, maxTrade);
            return value;
        }
    }

    private static class FuncSDefInit extends Func {
        static final Func func = new FuncSDefInit();

        private FuncSDefInit() {
            super(Stats.SHIELD_RATE, 0x01, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return creature.isPlayer() ? 0 : creature.getTemplate().getBaseShldRate();
        }
    }

    private static class FuncSDefAll extends Func {
        static final FuncSDefAll func = new FuncSDefAll();

        private FuncSDefAll() {
            super(Stats.SHIELD_RATE, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (initialValue == 0) {
                return initialValue;
            }

            double value = initialValue;

            if (target != null) {
                final WeaponTemplate weapon = target.getActiveWeaponItem();
                if (weapon != null) {
                    switch (weapon.getItemType()) {
                        case BOW:
                        case CROSSBOW:
                            value += 30.;
                            break;
                        case DAGGER:
                        case DUALDAGGER:
                            value += 12.;
                            break;
                    }
                }
            }

            return value;
        }
    }

    private static class FuncSDefPlayers extends Func {
        static final FuncSDefPlayers func = new FuncSDefPlayers();

        private FuncSDefPlayers() {
            super(Stats.SHIELD_RATE, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (initialValue == 0) {
                return initialValue;
            }

            final Creature cha = creature;
            final ItemInstance shld = cha.getPlayer().getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
            if (shld == null || shld.getItemType() != WeaponType.NONE) {
                return initialValue;
            }

            return initialValue * BaseStats.DEX.calcBonus(creature);
        }
    }

    private static class FuncMaxHpLimit extends Func {
        static final Func func = new FuncMaxHpLimit();

        private FuncMaxHpLimit() {
            super(Stats.MAX_HP, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_HP, initialValue);
        }
    }

    private static class FuncMaxMpLimit extends Func {
        static final Func func = new FuncMaxMpLimit();

        private FuncMaxMpLimit() {
            super(Stats.MAX_MP, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_MP, initialValue);
        }
    }

    private static class FuncMaxCpLimit extends Func {
        static final Func func = new FuncMaxCpLimit();

        private FuncMaxCpLimit() {
            super(Stats.MAX_CP, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_CP, initialValue);
        }
    }

    private static class FuncRunSpdLimit extends Func {
        static final Func func = new FuncRunSpdLimit();

        private FuncRunSpdLimit() {
            super(Stats.RUN_SPEED, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return creature.isPlayer() && creature.getPlayer().isGM() ? initialValue : Math.min(FormulasConfig.LIM_MOVE, initialValue);
        }
    }

    private static class FuncPDefLimit extends Func {
        static final Func func = new FuncPDefLimit();

        private FuncPDefLimit() {
            super(Stats.POWER_DEFENCE, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_PDEF, initialValue);
        }
    }

    private static class FuncMDefLimit extends Func {
        static final Func func = new FuncMDefLimit();

        private FuncMDefLimit() {
            super(Stats.MAGIC_DEFENCE, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_MDEF, initialValue);
        }
    }

    private static class FuncPAtkLimit extends Func {
        static final Func func = new FuncPAtkLimit();

        private FuncPAtkLimit() {
            super(Stats.POWER_ATTACK, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_PATK, initialValue);
        }
    }

    private static class FuncMAtkLimit extends Func {
        static final Func func = new FuncMAtkLimit();

        private FuncMAtkLimit() {
            super(Stats.MAGIC_ATTACK, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_MATK, initialValue);
        }
    }

    private static class FuncPAtkSpdLimit extends Func {
        static final Func func = new FuncPAtkSpdLimit();

        private FuncPAtkSpdLimit() {
            super(Stats.POWER_ATTACK_SPEED, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_PATK_SPD, initialValue);
        }
    }

    private static class FuncMAtkSpdLimit extends Func {
        static final Func func = new FuncMAtkSpdLimit();

        private FuncMAtkSpdLimit() {
            super(Stats.MAGIC_ATTACK_SPEED, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_MATK_SPD, initialValue);
        }
    }

    private static class FuncCAtkLimit extends Func {
        static final Func func = new FuncCAtkLimit();

        private FuncCAtkLimit() {
            super(Stats.CRITICAL_DAMAGE, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_CRIT_DAM / 2., initialValue);
        }
    }

    private static class FuncEvasionLimit extends Func {
        static final Func func = new FuncEvasionLimit();

        private FuncEvasionLimit() {
            super(Stats.EVASION_RATE, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_EVASION, initialValue);
        }
    }

    private static class FuncAccuracyLimit extends Func {
        static final Func func = new FuncAccuracyLimit();

        private FuncAccuracyLimit() {
            super(Stats.ACCURACY_COMBAT, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_ACCURACY, initialValue);
        }
    }

    private static class FuncCritLimit extends Func {
        static final Func func = new FuncCritLimit();

        private FuncCritLimit() {
            super(Stats.CRITICAL_BASE, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_CRIT, initialValue);
        }
    }

    private static class FuncMCritLimit extends Func {
        static final Func func = new FuncMCritLimit();

        private FuncMCritLimit() {
            super(Stats.MCRITICAL_RATE, 0x100, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return Math.min(FormulasConfig.LIM_MCRIT, initialValue);
        }
    }

    private static class FuncAttributeAttackInit extends Func {
        static final Func[] func = new FuncAttributeAttackInit[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++) {
                func[i] = new FuncAttributeAttackInit(Element.VALUES[i]);
            }
        }

        private final Element element;

        private FuncAttributeAttackInit(final Element element) {
            super(element.getAttack(), 0x01, null);
            this.element = element;
        }

        static Func getFunc(final Element element) {
            return func[element.getId()];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue + (creature.isPlayer() ? 0 : creature.getTemplate().getBaseAttributeAttack()[element.getId()]);
        }
    }

    private static class FuncAttributeDefenceInit extends Func {
        static final Func[] func = new FuncAttributeDefenceInit[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++) {
                func[i] = new FuncAttributeDefenceInit(Element.VALUES[i]);
            }
        }

        private final Element element;

        private FuncAttributeDefenceInit(final Element element) {
            super(element.getDefence(), 0x01, null);
            this.element = element;
        }

        static Func getFunc(final Element element) {
            return func[element.getId()];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return initialValue + (creature.isPlayer() ? 0 : creature.getTemplate().getBaseAttributeDefence()[element.getId()]);
        }
    }

    private static class FuncAttributeAttackSet extends Func {
        static final Func[] func = new FuncAttributeAttackSet[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++) {
                func[i] = new FuncAttributeAttackSet(Element.VALUES[i].getAttack());
            }
        }

        private FuncAttributeAttackSet(final Stats stat) {
            super(stat, 0x10, null);
        }

        static Func getFunc(final Element element) {
            return func[element.getId()];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (creature.getPlayer().getPlayerClassComponent().getClassId().getType2() == ClassType2.Summoner) {
                return creature.getPlayer().calcStat(stat, 0.);
            }

            return initialValue;
        }
    }

    private static class FuncAttributeDefenceSet extends Func {
        static final Func[] func = new FuncAttributeDefenceSet[Element.VALUES.length];

        static {
            for (int i = 0; i < Element.VALUES.length; i++) {
                func[i] = new FuncAttributeDefenceSet(Element.VALUES[i].getDefence());
            }
        }

        private FuncAttributeDefenceSet(final Stats stat) {
            super(stat, 0x10, null);
        }

        static Func getFunc(final Element element) {
            return func[element.getId()];
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            if (creature.getPlayer().getPlayerClassComponent().getClassId().getType2() == ClassType2.Summoner) {
                return creature.getPlayer().calcStat(stat, 0.);
            }

            return initialValue;
        }
    }
}