package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

public class EffectRelax extends Effect {
    private boolean _isWereSitting;

    public EffectRelax(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public boolean checkCondition() {
        final Player player = _effected.getPlayer();
        if (player == null) {
            return false;
        }
        if (player.isMounted()) {
            player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(_skill.getId(),
                    _skill.getLevel()));
            return false;
        }
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Player player = _effected.getPlayer();
        if (player.isMoving) {
            player.stopMove();
        }
        _isWereSitting = player.isSitting();
        player.sitDown(null);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (!_isWereSitting) {
            _effected.getPlayer().standUp();
        }
    }

    @Override
    public boolean onActionTime() {
        final Player player = _effected.getPlayer();
        if (player.isAlikeDead()) {
            return false;
        }

        if (!player.isSitting()) {
            return false;
        }

        if (player.isCurrentHpFull() && getSkill().getTemplate().isToggle()) {
            getEffected().sendPacket(SystemMsg.HP_WAS_FULLY_RECOVERED_AND_SKILL_WAS_REMOVED);
            return false;
        }

        final double manaDam = calc();
        if (manaDam > _effected.getCurrentMp()) {
            if (getSkill().getTemplate().isToggle()) {
                player.sendPacket(SystemMsg.NOT_ENOUGH_MP, new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(
                        getSkill().getId(), getSkill().getDisplayLevel()));
                return false;
            }
        }

        _effected.reduceCurrentMp(manaDam, null);

        return true;
    }
}
