package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Playable;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectSilentMove extends Effect {
    public EffectSilentMove(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (_effected.isPlayable()) {
            ((Playable) _effected).startSilentMoving();
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (_effected.isPlayable()) {
            ((Playable) _effected).stopSilentMoving();
        }
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isDead()) {
            return false;
        }

        if (!getSkill().getTemplate().isToggle()) {
            return false;
        }

        //Выводим из эффекта SilentMove если еффектед - имеет таск атаки
        if (_effected.isAttackingNow()) {
            return false;
        }

        final double manaDam = calc();
        if (manaDam > _effected.getCurrentMp()) {
            _effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            _effected.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(),
                    getSkill().getDisplayLevel()));
            return false;
        }

        _effected.reduceCurrentMp(manaDam, null);
        return true;
    }
}
