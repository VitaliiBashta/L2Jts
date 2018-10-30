package org.mmocore.gameserver.skills.skillclasses;


import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class BuffCharger extends Skill {
    private final int _target;

    public BuffCharger(final StatsSet set) {
        super(set);
        _target = set.getInteger("targetBuff", 0);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            int level = 0;
            final List<Effect> el = target.getEffectList().getEffectsBySkillId(_target);
            if (el != null) {
                level = el.get(0).getSkill().getLevel();
            }

            final SkillEntry next = SkillTable.getInstance().getSkillEntry(_target, level + 1);
            if (next != null) {
                next.getEffects(activeChar, target, false, false);
            }
        }
    }
}