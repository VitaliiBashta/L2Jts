package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _329_CuriosityOfDwarf extends Quest {
    private int GOLEM_HEARTSTONE = 1346;
    private int BROKEN_HEARTSTONE = 1365;


    public _329_CuriosityOfDwarf() {
        super(false);

        addStartNpc(30437);
        addKillId(20083);
        addKillId(20085);

        addQuestItem(BROKEN_HEARTSTONE);
        addQuestItem(GOLEM_HEARTSTONE);
        addLevelCheck(33, 38);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("trader_rolento_q0329_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("trader_rolento_q0329_06.htm")) {
            st.exitQuest(true);
            st.soundEffect(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();
        long heart;
        long broken;
        if (id == CREATED) {
            st.setCond(0);
        }
        if (st.getCond() == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "trader_rolento_q0329_01.htm";
                    st.exitQuest(true);
                default:
                    htmltext = "trader_rolento_q0329_02.htm";
                    break;
            }
        } else {
            heart = st.ownItemCount(GOLEM_HEARTSTONE);
            broken = st.ownItemCount(BROKEN_HEARTSTONE);
            if (broken + heart > 0) {
                st.giveItems(ADENA_ID, 50 * broken + 1000 * heart);
                st.takeItems(BROKEN_HEARTSTONE, -1);
                st.takeItems(GOLEM_HEARTSTONE, -1);
                htmltext = "trader_rolento_q0329_05.htm";
            } else {
                htmltext = "trader_rolento_q0329_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int n = Rnd.get(1, 100);
        if (npcId == 20085) {
            if (n < 5) {
                st.giveItems(GOLEM_HEARTSTONE, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (n < 58) {
                st.giveItems(BROKEN_HEARTSTONE, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == 20083) {
            if (n < 6) {
                st.giveItems(GOLEM_HEARTSTONE, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (n < 56) {
                st.giveItems(BROKEN_HEARTSTONE, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}