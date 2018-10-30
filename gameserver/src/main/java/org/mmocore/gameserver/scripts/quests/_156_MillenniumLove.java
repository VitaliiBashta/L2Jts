package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _156_MillenniumLove extends Quest {
    int LILITHS_LETTER = 1022;
    int THEONS_DIARY = 1023;
    int GR_COMP_PACKAGE_SS = 5250;
    int GR_COMP_PACKAGE_SPS = 5256;


    public _156_MillenniumLove() {
        super(false);

        addStartNpc(30368);

        addTalkId(30368);
        addTalkId(30368);
        addTalkId(30368);
        addTalkId(30369);

        addQuestItem(new int[]{
                LILITHS_LETTER,
                THEONS_DIARY
        });
        addLevelCheck(15, 19);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("30368-06.htm")) {
            st.giveItems(LILITHS_LETTER, 1);
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("156_1")) {
            st.takeItems(LILITHS_LETTER, -1);
            if (st.ownItemCount(THEONS_DIARY) == 0) {
                st.giveItems(THEONS_DIARY, 1);
                st.setCond(2);
            }
            htmltext = "30369-03.htm";
        } else if (event.equals("156_2")) {
            st.takeItems(LILITHS_LETTER, -1);
            st.soundEffect(SOUND_FINISH);
            htmltext = "30369-04.htm";
            st.exitQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30368) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30368-05.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30368-02.htm";
                        break;
                }
            } else if (cond == 1 && st.ownItemCount(LILITHS_LETTER) == 1) {
                htmltext = "30368-07.htm";
            } else if (cond == 2 && st.ownItemCount(THEONS_DIARY) == 1) {
                st.takeItems(THEONS_DIARY, -1);
                if (st.getPlayer().getPlayerClassComponent().getClassId().isMage()) {
                    st.giveItems(GR_COMP_PACKAGE_SPS, 1);
                } else {
                    st.giveItems(GR_COMP_PACKAGE_SS, 1);
                }
                st.addExpAndSp(3000, 0);
                st.soundEffect(SOUND_FINISH);
                htmltext = "30368-08.htm";
                st.exitQuest(false);
            }
        } else if (npcId == 30369) {
            if (cond == 1 && st.ownItemCount(LILITHS_LETTER) == 1) {
                htmltext = "30369-02.htm";
            } else if (cond == 2 && st.ownItemCount(THEONS_DIARY) == 1) {
                htmltext = "30369-05.htm";
            }
        }
        return htmltext;
    }
}