package org.mmocore.gameserver.scripts.ai.freya;


import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;

/**
 * @author pchayka
 */

public class JiniaKnight extends Fighter {
    public JiniaKnight(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return false;
        }

        List<NpcInstance> around = actor.getAroundNpc(4000, 300);
        if (around != null && !around.isEmpty()) {
            for (NpcInstance npc : around) {
                if (npc.getNpcId() == 22767) {
                    actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, null, 300);
                }
            }
        }
        return true;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (attacker == null || attacker.isPlayable()) {
            return;
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    public boolean checkAggression(Creature target) {
        if (target.isPlayable()) {
            return false;
        }

        return super.checkAggression(target);
    }
}