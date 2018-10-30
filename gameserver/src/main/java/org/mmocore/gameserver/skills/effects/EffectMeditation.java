package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * Иммобилизует и парализует на время действия.
 *
 * @author Diamond
 * @date 24.07.2007
 * @time 5:32:46
 */
public final class EffectMeditation extends Effect {
    public EffectMeditation(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _effected.startParalyzed();
        _effected.setMeditated(true);
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.tryToStopParalyzation(getSkill());
        _effected.setMeditated(false);
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}