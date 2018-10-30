package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectHourglass extends Effect {
    public EffectHourglass(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isPlayer()) {
            _effected.getPlayer().getRecommendationComponent().startHourglassEffect();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected.isPlayer()) {
            _effected.getPlayer().getRecommendationComponent().stopHourglassEffect();
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}