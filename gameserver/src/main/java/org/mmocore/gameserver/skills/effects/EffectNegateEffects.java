package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectNegateEffects extends Effect {
    public EffectNegateEffects(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onExit() {
        super.onExit();
    }

    @Override
    public boolean onActionTime() {
        for (final Effect e : _effected.getEffectList().getAllEffects()) {
            if (!e.getStackType().equals(EffectTemplate.NO_STACK) && (e.getStackType().equals(getStackType()) || e.getStackType().equals(getStackType2())) || !e.getStackType2()
                    .equals(EffectTemplate.NO_STACK) && (e.getStackType2().equals(getStackType()) || e.getStackType2().equals(getStackType2()))) {
                if (e.getStackOrder() <= getStackOrder()) {
                    e.exit();
                }
            }
        }
        return false;
    }
}