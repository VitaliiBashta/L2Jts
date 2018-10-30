package org.mmocore.gameserver.scripts.ai.pts.forest_of_the_dead;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.quests._024_InhabitantsOfTheForestOfTheDead;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.World;

/**
 * @author VISTALL
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_raid_fighter_night_dorian extends Fighter {
    public ai_raid_fighter_night_dorian(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        final Quest q = QuestManager.getQuest(_024_InhabitantsOfTheForestOfTheDead.class);
        if (q != null) {
            for (final Player player : World.getAroundPlayers(getActor(), 400, 300)) {
                final QuestState questState = player.getQuestState(24);
                if (questState != null && questState.getCond() == 3) {
                    q.notifyEvent("see_creature", questState, getActor());
                }
            }
        }

        return super.thinkActive();
    }
}