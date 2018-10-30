package org.mmocore.gameserver.skills.effects;

import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ChangeWaitType;
import org.mmocore.gameserver.network.lineage.serverpackets.Revive;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

public final class EffectFakeDeath extends Effect {
    public EffectFakeDeath(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        final Player player = (Player) getEffected();
        player.setFakeDeath(true);
        player.getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH, null, null);
        player.broadcastPacket(new ChangeWaitType(player, ChangeWaitType.WT_START_FAKEDEATH));
        player.broadcastCharInfo();
    }

    @Override
    public void onExit() {
        super.onExit();
        // 5 секунд после FakeDeath на персонажа не агрятся мобы
        final Player player = (Player) getEffected();
        player.setNonAggroTime(System.currentTimeMillis() + 5000L);
        player.setFakeDeath(false);
        player.broadcastPacket(new ChangeWaitType(player, ChangeWaitType.WT_STOP_FAKEDEATH));
        player.broadcastPacket(new Revive(player));
        player.broadcastCharInfo();
    }

    @Override
    public boolean onActionTime() {
        if (getEffected().isDead()) {
            return false;
        }

        final double manaDam = calc();

        if (manaDam > getEffected().getCurrentMp()) {
            if (getSkill().getTemplate().isToggle()) {
                getEffected().sendPacket(SystemMsg.NOT_ENOUGH_MP);
                getEffected().sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(getSkill().getId(),
                        getSkill().getDisplayLevel()));
                return false;
            }
        }

        getEffected().reduceCurrentMp(manaDam, null);
        return true;
    }
}
