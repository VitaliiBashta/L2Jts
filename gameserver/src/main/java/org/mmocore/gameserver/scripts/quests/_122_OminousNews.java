package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _122_OminousNews extends Quest {
    int MOIRA = 31979;
    int KARUDA = 32017;


    public _122_OminousNews() {
        super(false);
        addStartNpc(MOIRA);
        addTalkId(KARUDA);
        addLevelCheck(20, 36);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        htmltext = event;
        if (htmltext.equalsIgnoreCase("seer_moirase_q0122_0104.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (htmltext.equalsIgnoreCase("karuda_q0122_0201.htm")) {
            if (cond == 1) {
                st.giveItems(ADENA_ID, 8923);
                st.addExpAndSp(45151, 2310); // награда соответствует Т2
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            } else {
                htmltext = "noquest";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == MOIRA) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "seer_moirase_q0122_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "seer_moirase_q0122_0101.htm";
                        break;
                }
            } else {
                htmltext = "seer_moirase_q0122_0104.htm";
            }
        } else if (npcId == KARUDA && cond == 1) {
            htmltext = "karuda_q0122_0101.htm";
        }
        return htmltext;
    }
}