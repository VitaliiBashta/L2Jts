package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _154_SacrificeToSea extends Quest {
    private static final int FOX_FUR_ID = 1032;
    private static final int FOX_FUR_YARN_ID = 1033;
    private static final int MAIDEN_DOLL_ID = 1034;
    private static final int MYSTICS_EARRING_ID = 113;


    public _154_SacrificeToSea() {
        super(false);

        addStartNpc(30312);

        addTalkId(30051);
        addTalkId(30055);

        addKillId(20481);
        addKillId(20544);
        addKillId(20545);

        addQuestItem(new int[]{
                FOX_FUR_ID,
                FOX_FUR_YARN_ID,
                MAIDEN_DOLL_ID
        });
        addLevelCheck(2, 7);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.setMemoState("id", "0");
            htmltext = "30312-04.htm";
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
        int id = st.getState();
        if (id == CREATED) {
            st.setState(STARTED);
            st.setCond(0);
            st.setMemoState("id", "0");
        }
        if (npcId == 30312 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30312-02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "30312-03.htm";
                        return htmltext;
                }
            } else {
                htmltext = "30312-02.htm";
                st.exitQuest(true);
            }
        } else if (npcId == 30312 && st.getCond() == 0) {
            htmltext = "completed";
        } else if (npcId == 30312 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_YARN_ID) == 0 && st.ownItemCount(MAIDEN_DOLL_ID) == 0 && st.ownItemCount(FOX_FUR_ID) < 10) {
            htmltext = "30312-05.htm";
        } else if (npcId == 30312 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_ID) >= 10) {
            htmltext = "30312-08.htm";
        } else if (npcId == 30051 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_ID) < 10 && st.ownItemCount(FOX_FUR_ID) > 0) {
            htmltext = "30051-01.htm";
        } else if (npcId == 30051 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_ID) >= 10 && st.ownItemCount(FOX_FUR_YARN_ID) == 0 && st.ownItemCount(MAIDEN_DOLL_ID) == 0 && st.ownItemCount(MAIDEN_DOLL_ID) < 10) {
            htmltext = "30051-02.htm";
            st.giveItems(FOX_FUR_YARN_ID, 1);
            st.takeItems(FOX_FUR_ID, st.ownItemCount(FOX_FUR_ID));
        } else if (npcId == 30051 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_YARN_ID) >= 1) {
            htmltext = "30051-03.htm";
        } else if (npcId == 30051 && st.getCond() == 1 && st.ownItemCount(MAIDEN_DOLL_ID) == 1) {
            htmltext = "30051-04.htm";
        } else if (npcId == 30312 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_YARN_ID) >= 1) {
            htmltext = "30312-06.htm";
        } else if (npcId == 30055 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_YARN_ID) >= 1) {
            htmltext = "30055-01.htm";
            st.giveItems(MAIDEN_DOLL_ID, 1);
            st.takeItems(FOX_FUR_YARN_ID, st.ownItemCount(FOX_FUR_YARN_ID));
        } else if (npcId == 30055 && st.getCond() == 1 && st.ownItemCount(MAIDEN_DOLL_ID) >= 1) {
            htmltext = "30055-02.htm";
        } else if (npcId == 30055 && st.getCond() == 1 && st.ownItemCount(FOX_FUR_YARN_ID) == 0 && st.ownItemCount(MAIDEN_DOLL_ID) == 0) {
            htmltext = "30055-03.htm";
        } else if (npcId == 30312 && st.getCond() == 1 && st.ownItemCount(MAIDEN_DOLL_ID) >= 1) {
            if (st.getInt("id") != 154) {
                st.setMemoState("id", "154");
                htmltext = "30312-07.htm";
                st.takeItems(MAIDEN_DOLL_ID, st.ownItemCount(MAIDEN_DOLL_ID));
                st.giveItems(MYSTICS_EARRING_ID, 1);
                st.addExpAndSp(1000, 0);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.ownItemCount(FOX_FUR_YARN_ID) == 0) {
            st.rollAndGive(FOX_FUR_ID, 1, 1, 10, 14);
        }
        return null;
    }
}