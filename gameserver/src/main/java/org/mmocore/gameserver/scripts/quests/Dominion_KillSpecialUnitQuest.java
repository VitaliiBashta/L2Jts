package org.mmocore.gameserver.scripts.quests;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 15:51/12.04.2011
 */
public abstract class Dominion_KillSpecialUnitQuest extends Quest {
    private final ClassId[] _classIds;

    public Dominion_KillSpecialUnitQuest() {
        super(PARTY_ALL);

        _classIds = getTargetClassIds();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        for (ClassId c : _classIds) {
            runnerEvent.addClassQuest(c, this);
        }
    }

    protected abstract NpcString startNpcString();

    protected abstract NpcString progressNpcString();

    protected abstract NpcString doneNpcString();

    protected abstract int getRandomMin();

    protected abstract int getRandomMax();

    protected abstract ClassId[] getTargetClassIds();

    @Override
    public String onKill(Player killed, QuestState qs) {
        Player player = qs.getPlayer();
        if (player == null) {
            return null;
        }

        DominionSiegeEvent event1 = player.getEvent(DominionSiegeEvent.class);
        if (event1 == null) {
            return null;
        }
        DominionSiegeEvent event2 = killed.getEvent(DominionSiegeEvent.class);
        if (event2 == null || event2 == event1 || !event2.isInProgress()) {
            return null;
        }

        if (!ArrayUtils.contains(_classIds, killed.getPlayerClassComponent().getClassId())) {
            return null;
        }

        int max_kills = qs.getInt("max_kills");
        if (max_kills == 0) {
            qs.setState(STARTED);
            qs.setCond(1);

            max_kills = Rnd.get(getRandomMin(), getRandomMax());
            qs.setMemoState("max_kills", max_kills);
            qs.setMemoState("current_kills", 0);

            player.sendPacket(new ExShowScreenMessage(startNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String.valueOf(max_kills)));
        } else {
            int current_kills = qs.getInt("current_kills") + 1;
            if (current_kills >= max_kills) {
                event1.addReward(player, DominionSiegeEvent.STATIC_BADGES, 10);

                qs.setState(COMPLETED);
                qs.addExpAndSp(534000, 51000);
                qs.exitQuest(false);

                qs.setMemoState(DominionSiegeEvent.DATE, String.valueOf(event1.getResidence().getSiegeDate().toEpochSecond()));

                player.sendPacket(new ExShowScreenMessage(doneNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false));
            } else {
                qs.setMemoState("current_kills", current_kills);
                player.sendPacket(new ExShowScreenMessage(progressNpcString(), 2000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, false, String
                        .valueOf(max_kills), String.valueOf(current_kills)));
            }
        }

        return null;
    }

    @Override
    public boolean canAbortByPacket() {
        return false;
    }

    @Override
    public boolean isUnderLimit() {
        return true;
    }

    @Override
    public void onCreate(QuestState qs) {
        super.onCreate(qs);

        if (!qs.isCompleted()) {
            qs.addPlayerOnKillListener();
        }
    }

    @Override
    public void onAbort(QuestState qs) {
        qs.removePlayerOnKillListener();
        super.onAbort(qs);
    }
}
