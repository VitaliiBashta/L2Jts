package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class ManaHealPercent extends Skill {
    private final boolean _ignoreMpEff;

    public ManaHealPercent(final StatsSet set) {
        super(set);
        _ignoreMpEff = set.getBool("ignoreMpEff", true);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                if (target.isDead() || target.isHealBlocked()) {
                    continue;
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);

                final double mp = _power * target.getMaxMp() / 100.;
                final double newMp = mp * (!_ignoreMpEff ? target.calcStat(Stats.MANAHEAL_EFFECTIVNESS, 100., activeChar, skillEntry) : 100.) / 100.;
                final double addToMp = Math.max(0, Math.min(newMp, target.calcStat(Stats.MP_LIMIT, null, null) * target.getMaxMp() / 100. - target.getCurrentMp()));

                if (addToMp > 0) {
                    target.setCurrentMp(target.getCurrentMp() + addToMp);
                }
                if (target.isPlayer()) {
                    if (!activeChar.equals(target)) {
                        target.sendPacket(new SystemMessage(SystemMsg.S2_MP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addNumber(Math.round(addToMp)));
                    } else {
                        activeChar.sendPacket(new SystemMessage(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addNumber(Math.round(addToMp)));
                    }
                }
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}