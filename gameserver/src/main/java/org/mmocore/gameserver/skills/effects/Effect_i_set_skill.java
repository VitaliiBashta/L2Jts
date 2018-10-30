package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author KilRoy
 */
public class Effect_i_set_skill extends Effect {
    private final int skillId;
    private final int skillLevel;

    public Effect_i_set_skill(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        skillId = template.getParam().getInteger("skill_id");
        skillLevel = template.getParam().getInteger("skill_level");
    }

    @Override
    public boolean checkCondition() {
        if (!getEffected().isPlayer()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        final Player player = (Player) getEffected();
        final SkillEntry entry = SkillTable.getInstance().getSkillEntry(skillId, skillLevel);
        player.addSkill(entry, true);
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}