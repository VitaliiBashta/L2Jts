package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author : Mangol
 */
public class Effect_i_sp extends Effect {
    private final int _power;

    public Effect_i_sp(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _power = template.getParam().getInteger("argument", 0);
    }

    @Override
    public void onStart() {
        Player player = (Player) getEffector();
        player.addExpAndSp(0, (long) _power);
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
