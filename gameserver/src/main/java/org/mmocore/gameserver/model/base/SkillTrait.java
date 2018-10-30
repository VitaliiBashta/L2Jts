package org.mmocore.gameserver.model.base;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;

public enum SkillTrait {
    NONE,
    BLEED {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.BLEED_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return creature.calcStat(Stats.BLEED_POWER, target, skillEntry);
        }
    },
    BOSS,
    DEATH,
    DERANGEMENT {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.MENTAL_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return Math.min(40., creature.calcStat(Stats.MENTAL_POWER, target, skillEntry) + calcEnchantMod(creature, target, skillEntry));
        }
    },
    ETC,
    GUST,
    HOLD {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.ROOT_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return creature.calcStat(Stats.ROOT_POWER, target, skillEntry);
        }
    },
    PARALYZE {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.PARALYZE_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return creature.calcStat(Stats.PARALYZE_POWER, target, skillEntry);
        }
    },
    PHYSICAL_BLOCKADE,
    POISON {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.POISON_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return creature.calcStat(Stats.POISON_POWER, target, skillEntry);
        }
    },
    SHOCK {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.STUN_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return creature.calcStat(Stats.STUN_POWER, target, skillEntry);
            //return Math.min(40., creature.calcStat(Stats.STUN_POWER, target, skillEntry) + calcEnchantMod(env));
        }
    },
    SLEEP {
        @Override
        public final double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return target.calcStat(Stats.SLEEP_RESIST, creature, skillEntry);
        }

        @Override
        public final double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
            return creature.calcStat(Stats.SLEEP_POWER, target, skillEntry);
        }
    },
    VALAKAS;

    /*
        public double calcResistMod(final Creature creature, final Creature target, final SkillEntry skillEntry)
        {
            final double vulnMod = calcVuln(creature, target, skillEntry);
            final double profMod = calcProf(creature, target, skillEntry);
            final double maxResist = 90. + Math.max(calcEnchantMod(creature, target, skillEntry), profMod * 0.85);
            return (maxResist - vulnMod) / 60.;
        }
    */
    public static double calcEnchantMod(final Creature creature, final Creature target, final SkillEntry skillEntry) {
        int enchantLevel = skillEntry.getDisplayLevel();
        if (enchantLevel <= 100) {
            return 0;
        }
        enchantLevel %= 100;
        return skillEntry.getTemplate().getEnchantLevelCount() == 15 ? enchantLevel * 2 : enchantLevel;
    }

    public double calcVuln(final Creature creature, final Creature target, final SkillEntry skillEntry) {
        return 0;
    }

    public double calcProf(final Creature creature, final Creature target, final SkillEntry skillEntry) {
        return 0;
    }
}