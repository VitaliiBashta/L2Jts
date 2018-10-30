package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Created by Mangol on 18.10.2015.
 * TODO: Временная затычка пока не сделаю систему снятия скила со слота.
 */
public class EffectCancelTransform extends Effect {
    public EffectCancelTransform(Creature creature, Creature target, SkillEntry skill, EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        if (getEffected().isPlayer()) {
            final Player player = (Player) getEffected();
            player.stopTransformation(true);
        }
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
