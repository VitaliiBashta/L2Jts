package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.quests._021_HiddenTruth;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

public class GhostOfVonHellmannsPage extends DefaultAI {
    static final Location[] points = {
            new Location(51446, -54514, -3136),
            new Location(51870, -54398, -3176),
            new Location(52164, -53964, -3176),
            new Location(52390, -53282, -3176),
            new Location(52058, -52071, -3104),
            new Location(52237, -51483, -3112),
            new Location(52024, -51262, -3096)};
    private int current_point = -1;
    private long wait_timeout = 0;
    private boolean wait = false;

    public GhostOfVonHellmannsPage(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    protected boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead()) {
            return true;
        }
        if (_def_think) {
            doTask();
            return true;
        }
        if (actor.isMoving) {
            return true;
        }
        if (System.currentTimeMillis() > wait_timeout && (current_point > -1 || Rnd.chance(5))) {
            if (!wait) {
                switch (current_point) {
                    case 6:
                        wait_timeout = System.currentTimeMillis() + 15000;
                        Quest q = QuestManager.getQuest(_021_HiddenTruth.class);
                        if (q != null) {
                            for (Player player : World.getAroundPlayers(actor, 100, 100)) {
                                QuestState questState = player.getQuestState(21);
                                if (questState != null && questState.getCond() == 3) {
                                    Functions.npcSay(actor, NpcString.PLEASE_CHECK_THIS_BOOKCASE_S1, player.getName());
                                    questState.setCond(4);
                                    questState.soundEffect("ItemSound.quest_middle");
                                }
                            }
                        }
                        wait = true;
                        return true;
                }
            }
            wait_timeout = 0;
            wait = false;
            current_point++;
            if (current_point >= points.length) {
                actor.deleteMe();
                return false;
            }
            addTaskMove(points[current_point], true);
            doTask();
            return true;
        }
        if (randomAnimation()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature target, int aggro) {
    }
}