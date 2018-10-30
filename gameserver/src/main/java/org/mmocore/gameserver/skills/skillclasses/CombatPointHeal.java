package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class CombatPointHeal extends Skill {
    private final boolean _ignoreCpEff;

    public CombatPointHeal(final StatsSet set) {
        super(set);
        _ignoreCpEff = set.getBool("ignoreCpEff", false);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                if (target.isDead() || target.isHealBlocked()) {
                    continue;
                }
                final double maxNewCp = _power * (!_ignoreCpEff ? target.calcStat(Stats.CPHEAL_EFFECTIVNESS, 100., activeChar, skillEntry) : 100.) / 100.;
                final double addToCp = Math.max(0, Math.min(maxNewCp, target.calcStat(Stats.CP_LIMIT, null, null) * target.getMaxCp() / 100. - target.getCurrentCp()));
                if (addToCp > 0) {
                    target.setCurrentCp(addToCp + target.getCurrentCp());
                }
                if (activeChar.equals(target)) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addNumber(Math.round(addToCp)));
                } else {
                    target.sendPacket(new SystemMessage(SystemMsg.S2_CP_HAS_BEEN_RESTORED_BY_C1).addName(activeChar).addNumber(Math.round(addToCp)));
                }
                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }
        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
