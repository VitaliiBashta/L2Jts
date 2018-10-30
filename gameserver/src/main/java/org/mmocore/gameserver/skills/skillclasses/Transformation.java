package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class Transformation extends Skill {
    public Transformation(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null && target.isPlayer()) {
                getEffects(skillEntry, activeChar, target, false, false);
            }
        }
    }
}