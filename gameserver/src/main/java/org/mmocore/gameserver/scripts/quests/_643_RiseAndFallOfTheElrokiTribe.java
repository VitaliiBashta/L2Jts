package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _643_RiseAndFallOfTheElrokiTribe extends Quest {
    private static final int BONES_OF_A_PLAINS_DINOSAUR = 8776;

    private static final int[] PLAIN_DINOSAURS = {
            22208,
            22209,
            22210,
            22211,
            22212,
            22213,
            22221,
            22222,
            22226,
            22227,
            22742,
            22743,
            22744,
            22745
    };
    private static final int[] REWARDS = {
            8712,
            8713,
            8714,
            8715,
            8716,
            8717,
            8718,
            8719,
            8720,
            8721,
            8722
    };


    public _643_RiseAndFallOfTheElrokiTribe() {
        super(true);

        addStartNpc(32106);
        addTalkId(32117);

        for (int npc : PLAIN_DINOSAURS) {
            addKillId(npc);
        }

        addQuestItem(BONES_OF_A_PLAINS_DINOSAUR);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long count = st.ownItemCount(BONES_OF_A_PLAINS_DINOSAUR);
        if (event.equalsIgnoreCase("singsing_q0643_05.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("shaman_caracawe_q0643_06.htm")) {
            if (count >= 300) {
                st.takeItems(BONES_OF_A_PLAINS_DINOSAUR, 300);
                st.giveItems(REWARDS[Rnd.get(REWARDS.length)], 5, false);
            } else {
                htmltext = "shaman_caracawe_q0643_05.htm";
            }
        } else if (event.equalsIgnoreCase("None")) {
            htmltext = null;
        } else if (event.equalsIgnoreCase("Quit")) {
            htmltext = null;
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getCond() == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "singsing_q0643_04.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "singsing_q0643_01.htm";
                    break;
            }
        } else if (st.getState() == STARTED) {
            if (npcId == 32106) {
                long count = st.ownItemCount(BONES_OF_A_PLAINS_DINOSAUR);
                if (count == 0) {
                    htmltext = "singsing_q0643_08.htm";
                } else {
                    htmltext = "singsing_q0643_08.htm";
                    st.takeItems(BONES_OF_A_PLAINS_DINOSAUR, -1);
                    st.giveItems(ADENA_ID, count * 1374, false);
                }
            } else if (npcId == 32117) {
                htmltext = "shaman_caracawe_q0643_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            int DROP_CHANCE = 75;
            st.rollAndGive(BONES_OF_A_PLAINS_DINOSAUR, 1, DROP_CHANCE);
        }
        return null;
    }
}