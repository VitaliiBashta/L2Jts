package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.FinishRotating;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.StartRotating;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.PositionUtils;

/**
 * @author Java-man
 */
public class i_align_direction extends Effect {
    public i_align_direction(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        if (_effected.isInvul())
            return false;
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();

        int heading = PositionUtils.calculateHeadingFrom(_effected, _effector);

        _effected.broadcastPacket(new StartRotating(_effected, _effected.getHeading(), 1, 65535));
        _effected.broadcastPacket(new FinishRotating(_effected, heading, 65535));
        _effected.setHeading(_effector.getHeading());

        _effected.sendPacket(new SystemMessage(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));

    }

    @Override
    public boolean onActionTime() {
        return false;
    }
}