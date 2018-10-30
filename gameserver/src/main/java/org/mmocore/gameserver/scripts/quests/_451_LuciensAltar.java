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
public class _451_LuciensAltar extends Quest {
    // npc
    private final static int daichir_priest_of_earth = 30537;
    private final static int altar_of_lucien1 = 32706;
    private final static int altar_of_lucien2 = 32707;
    private final static int altar_of_lucien3 = 32708;
    private final static int altar_of_lucien4 = 32709;
    private final static int altar_of_lucien5 = 32710;

    // questitem
    private final static int q_barrier_bead_charged = 14877;
    private final static int q_barrier_bead_discharged = 14878;

    public _451_LuciensAltar() {
        super(false);

        addStartNpc(daichir_priest_of_earth);
        addTalkId(altar_of_lucien1, altar_of_lucien2, altar_of_lucien3, altar_of_lucien4, altar_of_lucien5);
        addQuestItem(q_barrier_bead_charged, q_barrier_bead_discharged);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("altar_of_lucien", String.valueOf(1), true);
            st.setMemoState("altar_of_lucien_ex", String.valueOf(1), true);
            st.giveItems(q_barrier_bead_charged, 5);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "daichir_priest_of_earth_q0451_07.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("altar_of_lucien");
        int GetMemoStateEx = st.getInt("altar_of_lucien_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == daichir_priest_of_earth) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "daichir_priest_of_earth_q0451_02.htm";
                            st.exitQuest(this);
                            break;
                        default:
                            if (st.isNowAvailable())
                                htmltext = "daichir_priest_of_earth_q0451_01.htm";
                            else {
                                htmltext = "daichir_priest_of_earth_q0451_03.htm";
                                st.exitQuest(this);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == daichir_priest_of_earth) {
                    if (GetMemoState == 1 && GetMemoStateEx == 1)
                        htmltext = "daichir_priest_of_earth_q0451_10.htm";
                    else if (GetMemoState == 1 && GetMemoStateEx >= 2)
                        htmltext = "daichir_priest_of_earth_q0451_11.htm";
                    else if (GetMemoState == 2) {
                        st.giveItems(ADENA_ID, 255380);
                        st.takeItems(q_barrier_bead_discharged, -1);
                        st.removeMemo("altar_of_lucien");
                        st.removeMemo("altar_of_lucien_ex");
                        st.soundEffect(SOUND_FINISH);
                        htmltext = "daichir_priest_of_earth_q0451_12.htm";
                        st.exitQuest(this);
                    }
                } else if (npcId == altar_of_lucien1) {
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_barrier_bead_charged) >= 1)
                            if (GetMemoStateEx % 2 == 0)
                                htmltext = "altar_of_lucien_q0451_02.htm";
                            else if (GetMemoStateEx * 2 == 2310) {
                                st.setCond(2);
                                st.setMemoState("altar_of_lucien", String.valueOf(2), true);
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            } else {
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.setMemoState("altar_of_lucien_ex", String.valueOf(GetMemoStateEx * 2), true);
                                st.soundEffect(SOUND_ITEMGET);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            }
                } else if (npcId == altar_of_lucien2) {
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_barrier_bead_charged) >= 1)
                            if (GetMemoStateEx % 3 == 0)
                                htmltext = "altar_of_lucien_q0451_02.htm";
                            else if (GetMemoStateEx * 3 == 2310) {
                                st.setCond(2);
                                st.setMemoState("altar_of_lucien", String.valueOf(2), true);
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            } else {
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.setMemoState("altar_of_lucien_ex", String.valueOf(GetMemoStateEx * 3), true);
                                st.soundEffect(SOUND_ITEMGET);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            }
                } else if (npcId == altar_of_lucien3) {
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_barrier_bead_charged) >= 1)
                            if (GetMemoStateEx % 5 == 0)
                                htmltext = "altar_of_lucien_q0451_02.htm";
                            else if (GetMemoStateEx * 5 == 2310) {
                                st.setCond(2);
                                st.setMemoState("altar_of_lucien", String.valueOf(2), true);
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            } else {
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.setMemoState("altar_of_lucien_ex", String.valueOf(GetMemoStateEx * 5), true);
                                st.soundEffect(SOUND_ITEMGET);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            }
                } else if (npcId == altar_of_lucien4) {
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_barrier_bead_charged) >= 1)
                            if (GetMemoStateEx % 7 == 0)
                                htmltext = "altar_of_lucien_q0451_02.htm";
                            else if (GetMemoStateEx * 7 == 2310) {
                                st.setCond(2);
                                st.setMemoState("altar_of_lucien", String.valueOf(2), true);
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            } else {
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.setMemoState("altar_of_lucien_ex", String.valueOf(GetMemoStateEx * 7), true);
                                st.soundEffect(SOUND_ITEMGET);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            }
                } else if (npcId == altar_of_lucien5)
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_barrier_bead_charged) >= 1)
                            if (GetMemoStateEx % 11 == 0)
                                htmltext = "altar_of_lucien_q0451_02.htm";
                            else if (GetMemoStateEx * 11 == 2310) {
                                st.setCond(2);
                                st.setMemoState("altar_of_lucien", String.valueOf(2), true);
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.soundEffect(SOUND_MIDDLE);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            } else {
                                st.giveItems(q_barrier_bead_discharged, 1);
                                st.takeItems(q_barrier_bead_charged, 1);
                                st.setMemoState("altar_of_lucien_ex", String.valueOf(GetMemoStateEx * 11), true);
                                st.soundEffect(SOUND_ITEMGET);
                                htmltext = "altar_of_lucien_q0451_01.htm";
                            }
                break;
        }
        return htmltext;
    }
}