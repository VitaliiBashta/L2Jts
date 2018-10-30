package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill.AddedSkill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectAddSkills extends Effect {
    public EffectAddSkills(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        for (final AddedSkill as : getSkill().getTemplate().getAddedSkills()) {
            getEffected().addSkill(as.getSkill());
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        for (final AddedSkill as : getSkill().getTemplate().getAddedSkills()) {
            getEffected().removeSkill(as.getSkill());
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}