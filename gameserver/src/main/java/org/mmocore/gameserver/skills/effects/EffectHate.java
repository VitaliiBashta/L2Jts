package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectHate extends Effect {
    public EffectHate(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isNpc() && _effected.isMonster()) {
            _effected.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, _effector, _template._value);
        }
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}