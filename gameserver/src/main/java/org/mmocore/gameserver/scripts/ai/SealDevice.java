package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

/**
 * AI Emperor's Seal Device.
 *
 * @author pchayka
 */
public class SealDevice extends Fighter {
    private boolean _firstAttack = false;

    public SealDevice(NpcInstance actor) {
        super(actor);
        actor.block();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (!_firstAttack) {
            actor.broadcastPacket(new MagicSkillUse(actor, actor, 5980, 1, 0, 0));
            _firstAttack = true;
        }
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}