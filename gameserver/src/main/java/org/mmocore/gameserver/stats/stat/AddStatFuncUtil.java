package org.mmocore.gameserver.stats.stat;

import javafx.util.Pair;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.base.BaseStats;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.StatFunctions;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.stats.funcs.Func;

import java.util.stream.Stream;

/**
 * @author Mangol
 * @since 24.09.2016
 */
public final class AddStatFuncUtil {

    public static void addStatCreature(final Creature creature) {
        if (creature == null) {
            return;
        }
        if (ServerConfig.ADD_STAT_CREATURE.isEmpty()) {
            return;
        }
        loop:
        for (Pair<AddStatCreature, AddStatType[]> type : ServerConfig.ADD_STAT_CREATURE) {
            switch (type.getKey()) {
                case SUMMON: {
                    if (creature.isSummon()) {
                        addStatType(creature, type.getValue());
                        break loop;
                    }
                    break;
                }
                case PET: {
                    if (creature.isPet()) {
                        addStatType(creature, type.getValue());
                        break loop;
                    }
                    break;
                }
/*				case NPC: {
					if(creature.isNpc() && !creature.isPet() && !creature.isSummon() && !creature.isMonster() && !creature.isRaid() && !creature.isBoss()) {
						addStatType(creature, type.getValue());
						break loop;
					}
					break;
				}*/
                case MONSTER: {
                    if (creature.isMonster() && !creature.isRaid() && !creature.isBoss()) {
                        addStatType(creature, type.getValue());
                        break loop;
                    }
                    break;
                }
                case RAID_BOSS: {
                    if (creature.isRaid()) {
                        addStatType(creature, type.getValue());
                        break loop;
                    }
                    break;
                }
                case ALL: {
                    if (creature.isPet() || creature.isSummon() || creature.isNpc() || creature.isMonster() || creature.isRaid() || creature.isBoss()) {
                        addStatType(creature, type.getValue());
                        break loop;
                    }
                    break;
                }
            }
        }
    }

    private static void addStatType(final Creature creature, final AddStatType[] addStatsTypes) {
        if (creature == null) {
            return;
        }
        Stream.of(addStatsTypes).forEach(stat -> {
            switch (stat) {
                case MAX_HP: {
                    if (creature.isSummon() || creature.isNpc() || creature.isMonster() || creature.isRaid()) {
                        creature.addStatFunc(FuncMaxHpNpcRateMul.func);
                    } else {
                        creature.addStatFunc(StatFunctions.FuncMaxHpMul.func);
                    }
                    break;
                }
                case MAX_MP: {
                    creature.addStatFunc(StatFunctions.FuncMaxMpMul.func);
                    break;
                }
                case P_ATK: {
                    creature.addStatFunc(StatFunctions.FuncPAtkMul.func);
                    break;
                }
                case M_ATK: {
                    creature.addStatFunc(StatFunctions.FuncMAtkMul.func);
                    break;
                }
                case P_DEF: {
                    creature.addStatFunc(StatFunctions.FuncPDefMul.func);
                    break;
                }
                case M_DEF: {
                    creature.addStatFunc(StatFunctions.FuncMDefMul.func);
                    break;
                }
            }
        });
    }

    public static class FuncMaxHpNpcRateMul extends Func {
        public static final FuncMaxHpNpcRateMul func = new FuncMaxHpNpcRateMul();

        private FuncMaxHpNpcRateMul() {
            super(Stats.MAX_HP, 0x20, null);
        }

        @Override
        public double calc(final Creature creature, final Creature target, final SkillEntry skill, final double initialValue) {
            return (initialValue * BaseStats.CON.calcBonus(creature)) * creature.getBaseTemplate().getBaseHpRate();
        }
    }
}
