package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FinishRotating;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.StartRotating;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectBluff extends Effect {
    public EffectBluff(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (getEffected().isNpc() && !getEffected().isMonster()) {
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        getEffected().broadcastPacket(new StartRotating(getEffected(), getEffected().getHeading(), 1, 65535));
        getEffected().broadcastPacket(new FinishRotating(getEffected(), getEffector().getHeading(), 65535));
        getEffected().setHeading(getEffector().getHeading());
        super.onStart();
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