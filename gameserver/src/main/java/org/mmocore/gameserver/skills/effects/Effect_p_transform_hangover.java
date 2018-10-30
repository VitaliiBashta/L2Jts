package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Create by Mangol on 15.10.2015.
 */
public class Effect_p_transform_hangover extends Effect {
    public Effect_p_transform_hangover(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        final Player player = (Player) getEffected();
        player.setBlockTransform(true);
        super.onStart();
    }

    @Override
    public void onExit() {
        final Player player = (Player) getEffected();
        player.setBlockTransform(false);
        super.onExit();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
