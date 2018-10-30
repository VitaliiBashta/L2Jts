package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

public class EffectCallSkills extends Effect {
    public EffectCallSkills(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        final int[] skillIds = getTemplate().getParam().getIntegerArray("skillIds");
        final int[] skillLevels = getTemplate().getParam().getIntegerArray("skillLevels");

        for (int i = 0; i < skillIds.length; i++) {
            final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skillIds[i], skillLevels[i]);
            for (final Creature cha : skill.getTemplate().getTargets(getEffector(), getEffected(), false)) {
                getEffector().broadcastPacket(new MagicSkillUse(getEffector(), cha, skillIds[i], skillLevels[i], 0, 0));
            }
            getEffector().callSkill(skill, skill.getTemplate().getTargets(getEffector(), getEffected(), false), false);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}