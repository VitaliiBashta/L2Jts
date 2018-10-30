package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class HealPercent extends Skill {
    private final boolean _ignoreHpEff;

    public HealPercent(final StatsSet set) {
        super(set);
        _ignoreHpEff = set.getBool("ignoreHpEff", true);
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
        for (final Creature target : targets) {
            if (target != null) {
                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);

                if (target.isHealBlocked()) {
                    continue;
                }

                final double hp = _power * target.getMaxHp() / 100.;
                final double newHp = hp * (!_ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100., activeChar, skillEntry) : 100.) / 100.;
                final double addToHp = Math.max(0, Math.min(newHp, target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp() / 100. - target.getCurrentHp()));

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
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}