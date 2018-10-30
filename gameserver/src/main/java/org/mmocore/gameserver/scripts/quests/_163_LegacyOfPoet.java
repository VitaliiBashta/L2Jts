package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _163_LegacyOfPoet extends Quest {
    final int RUMIELS_POEM_1_ID = 1038;
    final int RUMIELS_POEM_3_ID = 1039;
    final int RUMIELS_POEM_4_ID = 1040;
    final int RUMIELS_POEM_5_ID = 1041;


    public _163_LegacyOfPoet() {
        super(false);

        addStartNpc(30220);

        addTalkId(30220);

        addTalkId(30220);

        addKillId(20372);
        addKillId(20373);

        addQuestItem(new int[]{
                RUMIELS_POEM_1_ID,
                RUMIELS_POEM_3_ID,
                RUMIELS_POEM_4_ID,
                RUMIELS_POEM_5_ID
        });
        addLevelCheck(11, 15);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.setMemoState("id", "0");
            htmltext = "30220-07.htm";
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
        if (npcId == 30220 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "30220-02.htm";
                        st.exitQuest(true);
                        break;
                    case RACE:
                        htmltext = "30220-03.htm";
                        return htmltext;
                    default:
                        htmltext = "30220-00.htm";
                        break;
                }
            } else {
                htmltext = "30220-02.htm";
                st.exitQuest(true);
            }
        } else if (npcId == 30220 && st.getCond() == 0) {
            htmltext = "completed";
        } else if (npcId == 30220 && st.getCond() > 0) {
            if (st.ownItemCount(RUMIELS_POEM_1_ID) == 1 && st.ownItemCount(RUMIELS_POEM_3_ID) == 1 && st.ownItemCount(RUMIELS_POEM_4_ID) == 1 && st.ownItemCount(RUMIELS_POEM_5_ID) == 1) {
                if (st.getInt("id") != 163) {
                    st.setMemoState("id", "163");
                    htmltext = "30220-09.htm";
                    st.takeItems(RUMIELS_POEM_1_ID, 1);
                    st.takeItems(RUMIELS_POEM_3_ID, 1);
                    st.takeItems(RUMIELS_POEM_4_ID, 1);
                    st.takeItems(RUMIELS_POEM_5_ID, 1);
                    st.giveItems(ADENA_ID, 13890);
                    st.addExpAndSp(21643, 943);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                }
            } else {
                htmltext = "30220-08.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 20372 || npcId == 20373) {
            st.setMemoState("id", "0");
            if (st.getCond() == 1) {
                if (Rnd.chance(10) && st.ownItemCount(RUMIELS_POEM_1_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_1_ID, 1);
                    if (st.ownItemCount(RUMIELS_POEM_1_ID) + st.ownItemCount(RUMIELS_POEM_3_ID) + st.ownItemCount(RUMIELS_POEM_4_ID) + st.ownItemCount(RUMIELS_POEM_5_ID) == 4) {
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                if (Rnd.chance(70) && st.ownItemCount(RUMIELS_POEM_3_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_3_ID, 1);
                    if (st.ownItemCount(RUMIELS_POEM_1_ID) + st.ownItemCount(RUMIELS_POEM_3_ID) + st.ownItemCount(RUMIELS_POEM_4_ID) + st.ownItemCount(RUMIELS_POEM_5_ID) == 4) {
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                if (Rnd.chance(70) && st.ownItemCount(RUMIELS_POEM_4_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_4_ID, 1);
                    if (st.ownItemCount(RUMIELS_POEM_1_ID) + st.ownItemCount(RUMIELS_POEM_3_ID) + st.ownItemCount(RUMIELS_POEM_4_ID) + st.ownItemCount(RUMIELS_POEM_5_ID) == 4) {
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                //if(st.getRandom(10)>5 && st.ownItemCount(RUMIELS_POEM_5_ID) == 0)
                if (Rnd.chance(50) && st.ownItemCount(RUMIELS_POEM_5_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_5_ID, 1);
                    if (st.ownItemCount(RUMIELS_POEM_1_ID) + st.ownItemCount(RUMIELS_POEM_3_ID) + st.ownItemCount(RUMIELS_POEM_4_ID) + st.ownItemCount(RUMIELS_POEM_5_ID) == 4) {
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}