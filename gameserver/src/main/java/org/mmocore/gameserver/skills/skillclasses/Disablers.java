package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Disablers extends Skill {
    private final boolean _skillInterrupt;

    public Disablers(final StatsSet set) {
        super(set);
        _skillInterrupt = set.getBool("skillInterrupt", false);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        Creature realTarget;
        boolean reflected;

        for (final Creature target : targets) {
            if (target != null) {
                reflected = target.checkReflectSkill(activeChar, skillEntry);
                realTarget = reflected ? activeChar : target;

                if (_skillInterrupt) {
                    if (realTarget.getCastingSkill() != null && !realTarget.getCastingSkill().getTemplate().isMagic() && !realTarget.isRaid()) {
                        realTarget.abortCast(false, true);
                    }
                    if (!realTarget.isRaid()) {
                        realTarget.abortAttack(true, true);
                    }
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false, reflected);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}