package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;

import static org.mmocore.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public class EffectBetray extends Effect {
    public EffectBetray(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected != null && _effected.isSummon()) {
            final Servitor summon = (Servitor) _effected;
            summon.setDepressed(true);
            summon.getAI().Attack(summon.getPlayer(), true, false);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected != null && _effected.isSummon()) {
            final Servitor summon = (Servitor) _effected;
            summon.setDepressed(false);
            summon.getAI().setIntention(AI_INTENTION_ACTIVE);
        }
    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}