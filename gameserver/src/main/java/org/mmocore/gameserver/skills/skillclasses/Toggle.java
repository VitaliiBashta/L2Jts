package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Toggle extends Skill {
    public Toggle(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (activeChar.getEffectList().getEffectsBySkillId(_id) != null) {
            activeChar.getEffectList().stopEffect(_id);
            activeChar.sendActionFailed();
            return;
        }

        getEffects(skillEntry, activeChar, activeChar, getActivateRate() > 0, false);
    }
}
