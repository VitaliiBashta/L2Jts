package org.mmocore.gameserver.scripts.ai;


import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.List;

/**
 * Квестовый NPC, атакующий мобов. Игнорирует игроков.
 *
 * @author Diamond
 */
public class AttackMobNotPlayerFighter extends Fighter {
    public AttackMobNotPlayerFighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker == null) {
            return;
        }

        Player player = attacker.getPlayer();
        if (player != null) {
            List<QuestState> quests = player.getQuestsForEvent(actor, QuestEventType.ATTACKED_WITH_QUEST);
            if (quests != null) {
                for (QuestState qs : quests) {
                    qs.getQuest().notifyAttack(actor, qs);
                }
            }
        }

        onEvtAggression(attacker, damage);
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
        NpcInstance actor = getActor();
        if (attacker == null) {
            return;
        }

        if (!actor.isRunning()) {
            startRunningTask(AI_TASK_ATTACK_DELAY);
        }

        if (getIntention() != CtrlIntention.AI_INTENTION_ATTACK) {
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, attacker);
        }
    }
}