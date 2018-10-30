package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _158_SeedOfEvil extends Quest {
    int CLAY_TABLET_ID = 1025;
    int ENCHANT_ARMOR_D = 956;


    public _158_SeedOfEvil() {
        super(false);

        addStartNpc(30031);

        addKillId(27016);

        addQuestItem(CLAY_TABLET_ID);
        addLevelCheck(21, 26);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.setMemoState("id", "0");
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "30031-04.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.setState(STARTED);
            st.setMemoState("id", "0");
        }
        if (npcId == 30031 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30031-02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30031-03.htm";
                        return htmltext;
                }
            } else {
                htmltext = "30031-02.htm";
                st.exitQuest(true);
            }
        } else if (npcId == 30031 && st.getCond() == 0) {
            htmltext = "completed";
        } else if (npcId == 30031 && st.getCond() != 0 && st.ownItemCount(CLAY_TABLET_ID) == 0) {
            htmltext = "30031-05.htm";
        } else if (npcId == 30031 && st.getCond() != 0 && st.ownItemCount(CLAY_TABLET_ID) != 0) {
            st.takeItems(CLAY_TABLET_ID, st.ownItemCount(CLAY_TABLET_ID));
            st.soundEffect(SOUND_FINISH);
            st.giveItems(ADENA_ID, 1495);
            st.addExpAndSp(17818, 927);
            st.giveItems(ENCHANT_ARMOR_D, 1);
            htmltext = "30031-06.htm";
            st.exitQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.ownItemCount(CLAY_TABLET_ID) == 0) {
            st.giveItems(CLAY_TABLET_ID, 1);
            st.soundEffect(SOUND_MIDDLE);
            st.setCond(2);
        }
        return null;
    }
}