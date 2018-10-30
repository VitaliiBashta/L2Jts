package org.mmocore.gameserver.scripts.ai;


import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;

/**
 * AI Kanadis Guide и минионов для Rim Pailaka
 *
 * @author pchayka
 */

public class KanadisGuide extends Fighter {

    public KanadisGuide(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();

        NpcInstance actor = getActor();
        List<NpcInstance> around = actor.getAroundNpc(5000, 300);
        if (around != null && !around.isEmpty()) {
            for (NpcInstance npc : around) {
                if (npc.getNpcId() == 36562) {
                    actor.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, npc, null, 5000);
                }
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker.getNpcId() == 36562) {
            actor.getAggroList().addDamageHate(attacker, 0, 1);
            startRunningTask(2000);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected boolean maybeMoveToHome() {
        return false;
    }
}