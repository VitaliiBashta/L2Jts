package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectConsumeSoulsOverTime extends Effect {
    public EffectConsumeSoulsOverTime(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isDead()) {
            return false;
        }

        if (_effected.getConsumedSouls() < 0) {
            return false;
        }

        final int damage = (int) calc();

        if (_effected.getConsumedSouls() < damage) {
            _effected.setConsumedSouls(0, null);
        } else {
            _effected.setConsumedSouls(_effected.getConsumedSouls() - damage, null);
        }

        return true;
    }
}