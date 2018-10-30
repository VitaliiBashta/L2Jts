package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _124_MeetingTheElroki extends Quest {
    // npc
    private final static int marquez = 32113;
    private final static int mushika = 32114;
    private final static int asama = 32115;
    private final static int shaman_caracawe = 32117;
    private final static int egg_of_mantarasa = 32118;

    // questitem
    private final static int q_egg_of_mantarasa = 8778;

    public _124_MeetingTheElroki() {
        super(false);
        addStartNpc(marquez);
        addTalkId(mushika, asama, shaman_caracawe, egg_of_mantarasa);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("encounter_with_crokian");

        if (event.equals("quest_accept")) {
            st.setCond(1);
            st.setMemoState("encounter_with_crokian", String.valueOf(0), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "marquez_q0124_04.htm";
        }
        if (event.equals("reply_2") && GetMemoState == 0) {
            st.setCond(2);
            st.setMemoState("encounter_with_crokian", String.valueOf(1), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "marquez_q0124_06.htm";
        }
        if (event.equals("reply_2a") && GetMemoState == 1) {
            st.setCond(3);
            st.setMemoState("encounter_with_crokian", String.valueOf(2), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "mushika_q0124_03.htm";
        }
        if (event.equals("reply_6") && GetMemoState == 2) {
            st.setCond(4);
            st.setMemoState("encounter_with_crokian", String.valueOf(3), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "asama_q0124_06.htm";
        }
        if (event.equals("reply_9") && GetMemoState == 3) {
            st.setCond(5);
            st.setMemoState("encounter_with_crokian", String.valueOf(4), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "shaman_caracawe_q0124_05.htm";
        }
        if (event.equals("reply_10") && GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) < 1) {
            st.setCond(6);
            st.giveItems(q_egg_of_mantarasa, 1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "egg_of_mantarasa_q0124_02.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("encounter_with_crokian");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == marquez) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "marquez_q0124_02.htm";
                            break;
                        default:
                            htmltext = "marquez_q0124_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == marquez) {
                    if (GetMemoState == 0)
                        htmltext = "marquez_q0124_05.htm";
                    else if (GetMemoState == 1)
                        htmltext = "marquez_q0124_07.htm";
                    else if (GetMemoState >= 2 && GetMemoState <= 5)
                        htmltext = "marquez_q0124_08.htm";
                } else if (npcId == mushika) {
                    if (GetMemoState == 1)
                        htmltext = "mushika_q0124_01.htm";
                    else if (GetMemoState < 1)
                        htmltext = "mushika_q0124_02.htm";
                    else if (GetMemoState >= 2)
                        htmltext = "mushika_q0124_04.htm";
                } else if (npcId == asama) {
                    if (GetMemoState == 2)
                        htmltext = "asama_q0124_01.htm";
                    else if (GetMemoState < 2)
                        htmltext = "asama_q0124_02.htm";
                    else if (GetMemoState == 3)
                        htmltext = "asama_q0124_07.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) >= 1) {
                        st.giveItems(ADENA_ID, 100013);
                        st.addExpAndSp(301922, 30294);
                        st.takeItems(q_egg_of_mantarasa, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("encounter_with_crokian");
                        htmltext = "asama_q0124_08.htm";
                        st.exitQuest(false);
                    } else if (GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) < 1)
                        htmltext = "asama_q0124_09.htm";
                } else if (npcId == shaman_caracawe) {
                    if (GetMemoState == 3)
                        htmltext = "shaman_caracawe_q0124_01.htm";
                    else if (GetMemoState < 3)
                        htmltext = "shaman_caracawe_q0124_02.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) < 1)
                        htmltext = "shaman_caracawe_q0124_06.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) >= 1)
                        htmltext = "shaman_caracawe_q0124_07.htm";
                } else if (npcId == egg_of_mantarasa)
                    if (GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) < 1)
                        htmltext = "egg_of_mantarasa_q0124_01.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_egg_of_mantarasa) >= 1)
                        htmltext = "egg_of_mantarasa_q0124_03.htm";
                    else if (GetMemoState < 4)
                        htmltext = "egg_of_mantarasa_q0124_04.htm";
                break;
        }
        return htmltext;
    }
}