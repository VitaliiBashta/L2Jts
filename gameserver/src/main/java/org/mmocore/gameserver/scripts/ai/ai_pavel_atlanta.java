package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.quests._114_ResurrectionOfAnOldManager;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.world.World;

/**
 * @author Magister
 */
public class ai_pavel_atlanta extends DefaultAI {
    public ai_pavel_atlanta(NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
    }

    @Override
    protected boolean thinkActive() {
        //TODO: SEE_CREATURE смотреть АИ с оффа.
        final Quest q = QuestManager.getQuest(_114_ResurrectionOfAnOldManager.class);
        if (q != null) {
            for (final Player player : World.getAroundPlayers(getActor(), 400, 300)) {
                final QuestState questState = player.getQuestState(114);
                if (questState != null && questState.getInt("return_of_old_susceptor") == 8 && player.getInventory().getCountOf(8090) >= 1) {
                    player.sendPacket(new ExShowScreenMessage(NpcString.THE_RADIO_SIGNAL_DETECTOR_IS_RESPONDING_A_SUSPICIOUS_PILE_OF_STONES_CATCHES_YOUR_EYE, 4500, ScreenMessageAlign.TOP_CENTER));
                    player.getInventory().destroyItemByItemId(8090, 1);
                    player.getInventory().addItem(8091, 1);
                    questState.setCond(18);
                    questState.soundEffect("ItemSound.quest_middle");
                }
            }
        }
        return super.thinkActive();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }
}