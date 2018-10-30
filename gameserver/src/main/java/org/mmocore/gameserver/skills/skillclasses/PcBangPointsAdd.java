package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class PcBangPointsAdd extends Skill {
    public PcBangPointsAdd(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final int points = (int) _power;

        for (final Creature target : targets) {
            if (target.isPlayer()) {
                final Player player = target.getPlayer();
                player.getPremiumAccountComponent().addPcBangPoints(points, false);
            }
            getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}