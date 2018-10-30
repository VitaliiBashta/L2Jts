package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.AgathionComponent;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Create by Mangol on 26.09.2015.
 */
public class Effect_i_event_agathion_reuse_delay extends Effect {
    public Effect_i_event_agathion_reuse_delay(Creature creature, Creature target, SkillEntry skill, EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        final Player player = (Player) getEffector();
        final AgathionComponent comp = player.getAgathion();
        if (comp != null) {
            comp.sendMessage(0);
        }
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
