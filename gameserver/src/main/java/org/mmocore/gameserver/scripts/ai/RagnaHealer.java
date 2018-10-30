package org.mmocore.gameserver.scripts.ai;


import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Priest;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;

/**
 * @author Diamond
 */
public class RagnaHealer extends Priest {
    private long lastFactionNotifyTime;

    public RagnaHealer(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker == null) {
            return;
        }

        if (System.currentTimeMillis() - lastFactionNotifyTime > 10000) {
            lastFactionNotifyTime = System.currentTimeMillis();
            List<NpcInstance> around = actor.getAroundNpc(500, 300);
            if (around != null && !around.isEmpty()) {
                for (NpcInstance npc : around) {
                    if (npc.isMonster() && npc.getNpcId() >= 22691 && npc.getNpcId() <= 22702) {
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
                    }
                }
            }
        }

        super.onEvtAttacked(attacker, skill, damage);
    }
}