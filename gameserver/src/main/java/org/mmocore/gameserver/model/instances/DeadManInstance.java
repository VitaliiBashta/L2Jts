package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.network.lineage.serverpackets.Die;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class DeadManInstance extends NpcInstance {
    public DeadManInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
        setAI(new CharacterAI(this));
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        setCurrentHp(0, false);
        broadcastPacket(new Die(this));
        setWalking();
    }

    @Override
    public void reduceCurrentHp(final double damage, final Creature attacker, final SkillEntry skill, final boolean awake, final boolean standUp,
                                final boolean directHp, final boolean canReflect, final boolean transferDamage, final boolean isDot,
                                final boolean sendMessage, final boolean lethal) {
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isBlocked() {
        return true;
    }
}