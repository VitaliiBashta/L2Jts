package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChainHeal extends Skill {
    private final double[] _healPercents;
    private final int _maxTargets;

    public ChainHeal(final StatsSet set) {
        super(set);
        final String[] params = set.getString("healPercents", "").split(";");
        _maxTargets = params.length;
        _healPercents = new double[params.length];
        for (int i = 0; i < params.length; i++) {
            _healPercents[i] = (double) Integer.parseInt(params[i]) / 100.;
        }
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (activeChar.isPlayable() && target.isMonster()) {
            return false;
        }
        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        int curTarget = 0;
        for (final Creature target : targets) {
            if (target == null || target.isDead() || target.isHealBlocked()) {
                continue;
            }

            getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);

            final double hp = _healPercents[curTarget] * target.getMaxHp();
            final double addToHp = Math.max(0, Math.min(hp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));

            if (addToHp > 0) {
                target.setCurrentHp(addToHp + target.getCurrentHp(), false);
            }

            if (target.isPlayer()) {
                if (!activeChar.equals(target)) {
                    target.sendPacket(new SystemMessage(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addNumber(Math.round(addToHp)));
                } else {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addNumber(Math.round(addToHp)));
                }
            }

            curTarget++;
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    @Override
    public List<Creature> getTargets(final Creature activeChar, final Creature aimingTarget, final boolean forceUse) {
        final List<Creature> result = new ArrayList<>(_maxTargets);

        // Добавляем, если это возможно, текущую цель
        if (!aimingTarget.isDead() && !aimingTarget.isHealBlocked()) {
            result.add(aimingTarget);
        }

        final List<Creature> targets = aimingTarget.getAroundCharacters(getSkillRadius(), 128);
        if (targets == null) {
            return result;
        }

        final List<HealTarget> healList = new ArrayList<>();

        for (final Creature target : targets) {
            if (target == null || target.isDead() || target.isHealBlocked()) {
                continue;
            }
            // DS: протестировать всегда ли захватывает кастера
            if (activeChar.getObjectId() != aimingTarget.getObjectId() && target.getObjectId() == activeChar.getObjectId()) {
                continue;
            }
            if (target.isAutoAttackable(activeChar)) {
                continue;
            }

            healList.add(new HealTarget(target));
        }

        if (healList.isEmpty()) {
            return result;
        }

        Collections.sort(healList);

        final int size = Math.min(_maxTargets - result.size(), healList.size()); // возможно текущая цель уже добавлена
        for (int i = 0; i < size; i++) {
            result.add(healList.get(i).target);
        }

        healList.clear();
        return result;
    }

    private static final class HealTarget implements Comparable<HealTarget> {
        public final Creature target;
        private final double hpPercent;

        public HealTarget(final Creature target) {
            this.target = target;
            this.hpPercent = target.getCurrentHpPercents();
        }

        @Override
        public int compareTo(final HealTarget ht) {
            return Double.compare(hpPercent, ht.hpPercent);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final HealTarget that = (HealTarget) o;

            if (Double.compare(that.hpPercent, hpPercent) != 0) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            final long temp = Double.doubleToLongBits(hpPercent);
            return (int) (temp ^ (temp >>> 32));
        }
    }
}