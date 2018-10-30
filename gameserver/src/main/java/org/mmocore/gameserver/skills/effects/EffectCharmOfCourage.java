package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectCharmOfCourage extends Effect {
    public EffectCharmOfCourage(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isPlayer()) {
            _effected.getPlayer().setCharmOfCourage(true);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.getPlayer().setCharmOfCourage(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}