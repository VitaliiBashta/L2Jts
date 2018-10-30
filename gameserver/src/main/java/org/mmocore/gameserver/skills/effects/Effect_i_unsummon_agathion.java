package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Create by Mangol on 25.09.2015.
 */
public class Effect_i_unsummon_agathion extends Effect {
    public Effect_i_unsummon_agathion(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        if (getEffected().isPlayer()) {
            final Player player = (Player) getEffected();
            if (player.getAgathion() != null) {
                player.deleteAgathion();
            }
        }
    }

    @Override
    public void onExit() {
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
