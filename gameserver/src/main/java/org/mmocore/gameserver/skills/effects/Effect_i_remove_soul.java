package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * @author : Mangol
 * @date : 26.04.14  08:59
 */
public class Effect_i_remove_soul extends Effect {
    private int _power;

    public Effect_i_remove_soul(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _power = template.getParam().getInteger("argument", 0);
    }

    @Override
    public void onStart() {
        Player player = (Player) getEffector();
        player.setConsumedSouls(player.getConsumedSouls() - _power, null);
    }

    @Override
    public void onExit() {
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
