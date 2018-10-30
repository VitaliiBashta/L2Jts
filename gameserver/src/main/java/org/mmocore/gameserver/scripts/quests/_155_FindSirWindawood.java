package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _155_FindSirWindawood extends Quest {
    final int OFFICIAL_LETTER = 1019;
    final int HASTE_POTION = 734;


    public _155_FindSirWindawood() {
        super(false);

        addStartNpc(30042);

        addTalkId(30042);
        addTalkId(30311);

        addQuestItem(OFFICIAL_LETTER);
        addLevelCheck(3, 6);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("30042-04.htm")) {
            st.giveItems(OFFICIAL_LETTER, 1);
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30042) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30042-02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30042-03.htm";
                        return htmltext;
                }
            } else if (cond == 1 && st.ownItemCount(OFFICIAL_LETTER) == 1) {
                htmltext = "30042-05.htm";
            }
        } else if (npcId == 30311 && cond == 1 && st.ownItemCount(OFFICIAL_LETTER) == 1) {
            htmltext = "30311-01.htm";
            st.takeItems(OFFICIAL_LETTER, -1);
            st.giveItems(HASTE_POTION, 1);
            st.setCond(0);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        }
        return htmltext;
    }
}