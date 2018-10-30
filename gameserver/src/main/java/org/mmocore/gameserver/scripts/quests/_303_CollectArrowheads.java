package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _303_CollectArrowheads extends Quest {
    int ORCISH_ARROWHEAD = 963;


    public _303_CollectArrowheads() {
        super(false);

        addStartNpc(30029);

        addTalkId(30029);

        addKillId(20361);

        addQuestItem(ORCISH_ARROWHEAD);
        addLevelCheck(10, 14);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("minx_q0303_04.htm")) {
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

        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "minx_q0303_02.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "minx_q0303_03.htm";
                    break;
            }
        } else if (st.ownItemCount(ORCISH_ARROWHEAD) < 10) {
            htmltext = "minx_q0303_05.htm";
        } else {
            st.takeItems(ORCISH_ARROWHEAD, -1);
            st.giveItems(ADENA_ID, 1000);
            st.addExpAndSp(2000, 0);
            htmltext = "minx_q0303_06.htm";
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.ownItemCount(ORCISH_ARROWHEAD) < 10) {
            st.giveItems(ORCISH_ARROWHEAD, 1);
            if (st.ownItemCount(ORCISH_ARROWHEAD) == 10) {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}