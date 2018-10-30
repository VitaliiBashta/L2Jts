package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _297_GateKeepersFavor extends Quest {


    private static final int STARSTONE = 1573;
    private static final int GATEKEEPER_TOKEN = 1659;

    public _297_GateKeepersFavor() {
        super(false);
        addStartNpc(30540);
        addTalkId(30540);
        addKillId(20521);
        addQuestItem(STARSTONE);
        addLevelCheck(15, 21);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("gatekeeper_wirphy_q0297_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30540) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "gatekeeper_wirphy_q0297_01.htm";
                        break;
                    default:
                        htmltext = "gatekeeper_wirphy_q0297_02.htm";
                        break;
                }
            } else if (cond == 1 && st.ownItemCount(STARSTONE) < 20) {
                htmltext = "gatekeeper_wirphy_q0297_04.htm";
            } else if (cond == 2 && st.ownItemCount(STARSTONE) < 20) {
                htmltext = "gatekeeper_wirphy_q0297_04.htm";
            } else if (cond == 2 && st.ownItemCount(STARSTONE) >= 20) {
                htmltext = "gatekeeper_wirphy_q0297_05.htm";
                st.takeItems(STARSTONE, -1);
                st.giveItems(GATEKEEPER_TOKEN, 2);
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(STARSTONE, 1, 1, 20, 33);
        if (st.ownItemCount(STARSTONE) >= 20) {
            st.setCond(2);
        }
        return null;
    }
}