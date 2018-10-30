package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

/**
 * @author pchayka
 */
public class _176_StepsForHonor extends Quest {

    public _176_StepsForHonor() {
        super(PARTY_ALL);
        int RAPIDUS = 36479;
        addStartNpc(RAPIDUS);
        addLevelCheck(80);
    }

    private static int calculatePlayersToKill(int cond) {
        switch (cond) {
            case 1:
                return 9;
            case 3:
                return 9 + 18;
            case 5:
                return 9 + 18 + 27;
            case 7:
                return 9 + 18 + 27 + 36;
            default:
                return 0;
        }
    }

    @Override
    public String onKill(Player killed, QuestState st) {
        int cond = st.getCond();
        if (!isValidKill(killed, st.getPlayer())) {
            return null;
        }
        if (cond == 1 || cond == 3 || cond == 5 || cond == 7) {
            st.setMemoState("kill", st.getInt("kill") + 1);
            if (st.getInt("kill") >= calculatePlayersToKill(cond)) {
                st.setCond(cond + 1);
            }
        }
        return null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("rapidus_q176_04.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        if (runnerEvent.isInProgress()) {
            htmltext = "rapidus_q176_05.htm";
        } else {
            switch (cond) {
                default:
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "rapidus_q176_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "rapidus_q176_03.htm";
                            break;
                    }
                    break;
                case 1:
                    htmltext = "rapidus_q176_06.htm";
                    break;
                case 2:
                    htmltext = "rapidus_q176_07.htm";
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                    break;
                case 3:
                    htmltext = "rapidus_q176_08.htm";
                    break;
                case 4:
                    htmltext = "rapidus_q176_09.htm";
                    st.setCond(5);
                    st.soundEffect(SOUND_MIDDLE);
                    break;
                case 5:
                    htmltext = "rapidus_q176_10.htm";
                    break;
                case 6:
                    htmltext = "rapidus_q176_11.htm";
                    st.setCond(7);
                    st.soundEffect(SOUND_MIDDLE);
                    break;
                case 7:
                    htmltext = "rapidus_q176_12.htm";
                    break;
                case 8:
                    htmltext = "rapidus_q176_13.htm";
                    st.giveItems(14603, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    break;
            }
        }
        return htmltext;
    }

    private boolean isValidKill(Player killed, Player killer) {
        DominionSiegeEvent killedSiegeEvent = killed.getEvent(DominionSiegeEvent.class);
        DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

        if (killedSiegeEvent == null || killerSiegeEvent == null) {
            return false;
        }
        if (killedSiegeEvent == killerSiegeEvent) {
            return false;
        }
        if (killed.getLevel() < 61) {
            return false;
        }
        return true;
    }

    @Override
    public void onCreate(QuestState qs) {
        super.onCreate(qs);
        qs.addPlayerOnKillListener();
    }

    @Override
    public void onAbort(QuestState qs) {
        qs.removePlayerOnKillListener();
        super.onAbort(qs);
    }


}