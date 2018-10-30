package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;

public class FieldMachine extends DefaultAI {
    private long _lastAction;

    public FieldMachine(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker == null || attacker.getPlayer() == null) {
            return;
        }

        // Ругаемся не чаще, чем раз в 15 секунд
        if (System.currentTimeMillis() - _lastAction > 15000) {
            _lastAction = System.currentTimeMillis();
            Functions.npcSay(actor, Rnd.chance(50) ? NpcString.ALERT_ALERT_DAMAGE_DETECTION_RECOGNIZED : NpcString.THE_PURIFICATION_FIELD_IS_BEING_ATTACKED);
            List<NpcInstance> around = actor.getAroundNpc(1500, 300);
            if (around != null && !around.isEmpty()) {
                for (NpcInstance npc : around) {
                    if (npc.isMonster() && npc.getNpcId() >= 22656 && npc.getNpcId() <= 22659) {
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000);
                    }
                }
            }
        }
    }
}