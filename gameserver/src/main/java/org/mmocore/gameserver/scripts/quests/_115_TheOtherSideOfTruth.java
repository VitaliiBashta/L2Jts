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
public class _115_TheOtherSideOfTruth extends Quest {
    // npc
    private static final int repre = 32020;
    private static final int misa = 32018;
    private static final int keier = 32022;
    private static final int ice_sculpture = 32021;
    private static final int ice_sculpture2 = 32077;
    private static final int ice_sculpture3 = 32078;
    private static final int ice_sculpture4 = 32079;

    // questitem
    private static final int q_letter_of_misa = 8079;
    private static final int q_letter_of_repre = 8080;
    private static final int q_part_of_tablet = 8081;
    private static final int q_part_of_report_keier = 8082;

    public _115_TheOtherSideOfTruth() {
        super(false);
        addStartNpc(repre);
        addTalkId(misa, keier, ice_sculpture, ice_sculpture2, ice_sculpture3, ice_sculpture4);
        addQuestItem(q_letter_of_misa, q_letter_of_repre, q_part_of_tablet, q_part_of_report_keier);
        addLevelCheck(53, 63);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("beyond_the_turth");
        int GetMemoStateEx = st.getInt("beyond_the_turth_ex");
        int npcId = npc.getNpcId();

        if (npcId == repre) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("beyond_the_turth", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "repre_q0115_03.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 2) {
                st.setCond(3);
                st.setMemoState("beyond_the_turth", String.valueOf(3), true);
                st.takeItems(q_letter_of_misa, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "repre_q0115_07.htm";
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 2) {
                st.takeItems(q_letter_of_misa, -1);
                st.removeMemo("beyond_the_turth");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "repre_q0115_08.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 3) {
                st.setCond(4);
                st.setMemoState("beyond_the_turth", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "repre_q0115_11.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 3) {
                st.setCond(4);
                st.setMemoState("beyond_the_turth", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "repre_q0115_12.htm";
            } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 3) {
                st.removeMemo("beyond_the_turth");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "repre_q0115_13.htm";
            } else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 4) {
                st.setCond(5);
                st.setMemoState("beyond_the_turth", String.valueOf(5), true);
                st.soundEffect(SOUND_T_WINGFLAP_04);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "repre_q0115_17.htm";
            } else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 9 && st.ownItemCount(q_part_of_report_keier) >= 1) {
                st.setCond(10);
                st.setMemoState("beyond_the_turth", String.valueOf(10), true);
                st.takeItems(q_part_of_report_keier, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "repre_q0115_23.htm";
            } else if (event.equalsIgnoreCase("reply_9") && GetMemoState == 10) {
                if (st.ownItemCount(q_part_of_tablet) >= 1) {
                    st.takeItems(q_part_of_tablet, -1);
                    st.giveItems(ADENA_ID, 115673);
                    st.addExpAndSp(493595, 40442);
                    st.removeMemo("beyond_the_turth");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "repre_q0115_25.htm";
                } else {
                    st.setCond(11);
                    st.setMemoState("beyond_the_turth", String.valueOf(11), true);
                    st.soundEffect(THUNDER_02);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "repre_q0115_27.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10") && GetMemoState == 10)
                if (st.ownItemCount(q_part_of_tablet) >= 1) {
                    st.takeItems(q_part_of_tablet, -1);
                    st.giveItems(ADENA_ID, 115673);
                    st.addExpAndSp(493595, 40442);
                    st.removeMemo("beyond_the_turth");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "repre_q0115_26.htm";
                } else {
                    st.setCond(11);
                    st.setMemoState("beyond_the_turth", String.valueOf(11), true);
                    st.soundEffect(THUNDER_02);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "repre_q0115_28.htm";
                }
        } else if (npcId == misa) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 6 && st.ownItemCount(q_letter_of_repre) > 0) {
                st.setCond(7);
                st.setMemoState("beyond_the_turth", String.valueOf(7), true);
                st.takeItems(q_letter_of_repre, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "misa_q0115_05.htm";
            }
        } else if (npcId == keier) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 8) {
                st.setCond(9);
                st.setMemoState("beyond_the_turth", String.valueOf(9), true);
                st.giveItems(q_part_of_report_keier, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "keier_q0115_02.htm";
            }
        } else if (npcId == ice_sculpture) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 7 && GetMemoStateEx % 2 < 1) {
                int i0 = GetMemoStateEx;
                if (i0 == 6 || i0 == 10 || i0 == 12) {

                    int i1 = i0 + 1;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    st.giveItems(q_part_of_tablet, 1);
                    htmltext = "ice_sculpture_q0115_03.htm";
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 7 && GetMemoStateEx % 2 < 1) {
                int i0 = GetMemoStateEx;
                if (i0 == 6 || i0 == 10 || i0 == 12) {
                    int i1 = i0 + 1;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    htmltext = "ice_sculpture_q0115_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 7 && GetMemoStateEx == 14) {
                st.setCond(8);
                st.setMemoState("beyond_the_turth", String.valueOf(8), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ice_sculpture_q0115_06.htm";
            }
        } else if (npcId == ice_sculpture2) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 7 && GetMemoStateEx % 4 <= 1) {
                int i0 = GetMemoStateEx;
                if (i0 == 5 || i0 == 9 || i0 == 12) {

                    int i1 = i0 + 2;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    st.giveItems(q_part_of_tablet, 1);
                    htmltext = "ice_sculpture_q0115_03.htm";
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 7 && GetMemoStateEx % 4 <= 1) {
                int i0 = GetMemoStateEx;
                if (i0 == 5 || i0 == 9 || i0 == 12) {
                    int i1 = i0 + 2;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    htmltext = "ice_sculpture_q0115_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 7 && GetMemoStateEx == 13) {
                st.setCond(8);
                st.setMemoState("beyond_the_turth", String.valueOf(8), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ice_sculpture_q0115_06.htm";
            }
        } else if (npcId == ice_sculpture3) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 7 && GetMemoStateEx % 8 <= 3) {
                int i0 = GetMemoStateEx;
                if (i0 == 3 || i0 == 9 || i0 == 10) {

                    int i1 = i0 + 4;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    st.giveItems(q_part_of_tablet, 1);
                    htmltext = "ice_sculpture_q0115_03.htm";
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 7 && GetMemoStateEx % 8 <= 3) {
                int i0 = GetMemoStateEx;
                if (i0 == 3 || i0 == 9 || i0 == 10) {
                    int i1 = i0 + 4;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    htmltext = "ice_sculpture_q0115_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 7 && GetMemoStateEx == 11) {
                st.setCond(8);
                st.setMemoState("beyond_the_turth", String.valueOf(8), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ice_sculpture_q0115_06.htm";
            }
        } else if (npcId == ice_sculpture4)
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 7 && GetMemoStateEx <= 7) {
                int i0 = GetMemoStateEx;
                if (i0 == 3 || i0 == 5 || i0 == 6) {

                    int i1 = i0 + 8;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    st.giveItems(q_part_of_tablet, 1);
                    htmltext = "ice_sculpture_q0115_03.htm";
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 7 && GetMemoStateEx <= 7) {
                int i0 = GetMemoStateEx;
                if (i0 == 3 || i0 == 5 || i0 == 6) {
                    int i1 = i0 + 8;
                    st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                    htmltext = "ice_sculpture_q0115_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 7 && GetMemoStateEx == 7) {
                st.setCond(8);
                st.setMemoState("beyond_the_turth", String.valueOf(8), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ice_sculpture_q0115_06.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("beyond_the_turth");
        int GetMemoStateEx = st.getInt("beyond_the_turth_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == repre) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "repre_q0115_02.htm";
                            break;
                        default:
                            htmltext = "repre_q0115_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == repre) {
                    if (GetMemoState == 1)
                        htmltext = "repre_q0115_04.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_letter_of_misa) == 0) {
                        st.removeMemo("beyond_the_turth");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "repre_q0115_05.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_letter_of_misa) > 0)
                        htmltext = "repre_q0115_06.htm";
                    else if (GetMemoState == 3)
                        htmltext = "repre_q0115_09.htm";
                    else if (GetMemoState == 4)
                        htmltext = "repre_q0115_16.htm";
                    else if (GetMemoState == 5) {
                        st.setCond(6);
                        st.setMemoState("beyond_the_turth", String.valueOf(6), true);
                        st.giveItems(q_letter_of_repre, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "repre_q0115_18.htm";
                    } else if (GetMemoState == 6 && st.ownItemCount(q_letter_of_repre) != 0)
                        htmltext = "repre_q0115_19.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_letter_of_repre) == 0) {
                        st.giveItems(q_letter_of_repre, 1);
                        htmltext = "repre_q0115_20.htm";
                    } else if (GetMemoState >= 7 && GetMemoState < 9)
                        htmltext = "repre_q0115_21.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_part_of_report_keier) >= 1)
                        htmltext = "repre_q0115_22.htm";
                    else if (GetMemoState == 10)
                        htmltext = "repre_q0115_24.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 0)
                        htmltext = "repre_q0115_29.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) >= 1) {
                        st.takeItems(q_part_of_tablet, -1);
                        st.giveItems(ADENA_ID, 115673);
                        st.addExpAndSp(493595, 40442);
                        st.removeMemo("beyond_the_turth");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "repre_q0115_30.htm";
                    }
                } else if (npcId == misa) {
                    if (GetMemoState > 2 && GetMemoState < 5)
                        htmltext = "misa_q0115_01.htm";
                    else if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("beyond_the_turth", String.valueOf(2), true);
                        st.giveItems(q_letter_of_misa, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "misa_q0115_02.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "misa_q0115_03.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_letter_of_repre) > 0)
                        htmltext = "misa_q0115_04.htm";
                    else if (GetMemoState == 7)
                        htmltext = "misa_q0115_06.htm";
                } else if (npcId == keier) {
                    if (GetMemoState == 8)
                        htmltext = "keier_q0115_01.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_part_of_report_keier) >= 1)
                        htmltext = "keier_q0115_03.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_part_of_report_keier) == 0) {
                        st.giveItems(q_part_of_report_keier, 1);
                        htmltext = "keier_q0115_04.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_part_of_report_keier) == 0)
                        htmltext = "keier_q0115_05.htm";
                } else if (npcId == ice_sculpture) {
                    if (GetMemoState == 7 && GetMemoStateEx % 2 < 1) {
                        int i0 = GetMemoStateEx;
                        if (i0 == 6 || i0 == 10 || i0 == 12)
                            htmltext = "ice_sculpture_q0115_02.htm";
                        else if (i0 == 14)
                            htmltext = "ice_sculpture_q0115_05.htm";
                        else {
                            int i1 = i0 + 1;
                            st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                            htmltext = "ice_sculpture_q0115_01.htm";
                        }
                    } else if (GetMemoState == 7 && GetMemoStateEx % 2 >= 1)
                        htmltext = "ice_sculpture_q0115_01a.htm";
                    else if (GetMemoState == 8)
                        htmltext = "ice_sculpture_q0115_07.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 0) {
                        st.setCond(12);
                        st.giveItems(q_part_of_tablet, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ice_sculpture_q0115_08.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 1)
                        htmltext = "ice_sculpture_q0115_09.htm";
                } else if (npcId == ice_sculpture2) {
                    if (GetMemoState == 7 && GetMemoStateEx % 4 <= 1) {
                        int i0 = GetMemoStateEx;
                        if (i0 == 5 || i0 == 9 || i0 == 12)
                            htmltext = "ice_sculpture_q0115_02.htm";
                        else if (i0 == 13)
                            htmltext = "ice_sculpture_q0115_05.htm";
                        else {
                            int i1 = i0 + 2;
                            st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                            htmltext = "ice_sculpture_q0115_01.htm";
                        }
                    } else if (GetMemoState == 7 && GetMemoStateEx % 4 > 1)
                        htmltext = "ice_sculpture_q0115_01a.htm";
                    else if (GetMemoState == 8)
                        htmltext = "ice_sculpture_q0115_07.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 0) {
                        st.setCond(12);
                        st.giveItems(q_part_of_tablet, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ice_sculpture_q0115_08.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 1)
                        htmltext = "ice_sculpture_q0115_09.htm";
                } else if (npcId == ice_sculpture3) {
                    if (GetMemoState == 7 && GetMemoStateEx % 8 <= 3) {
                        int i0 = GetMemoStateEx;
                        if (i0 == 3 || i0 == 9 || i0 == 10)
                            htmltext = "ice_sculpture_q0115_02.htm";
                        else if (i0 == 11)
                            htmltext = "ice_sculpture_q0115_05.htm";
                        else {
                            int i1 = i0 + 4;
                            st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                            htmltext = "ice_sculpture_q0115_01.htm";
                        }
                    } else if (GetMemoState == 7 && GetMemoStateEx % 8 > 3)
                        htmltext = "ice_sculpture_q0115_01a.htm";
                    else if (GetMemoState == 8)
                        htmltext = "ice_sculpture_q0115_07.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 0) {
                        st.setCond(12);
                        st.giveItems(q_part_of_tablet, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ice_sculpture_q0115_08.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 1)
                        htmltext = "ice_sculpture_q0115_09.htm";
                } else if (npcId == ice_sculpture4)
                    if (GetMemoState == 7 && GetMemoStateEx <= 7) {
                        int i0 = GetMemoStateEx;
                        if (i0 == 3 || i0 == 5 || i0 == 6)
                            htmltext = "ice_sculpture_q0115_02.htm";
                        else if (i0 == 7)
                            htmltext = "ice_sculpture_q0115_05.htm";
                        else {
                            int i1 = i0 + 8;
                            st.setMemoState("beyond_the_turth_ex", String.valueOf(i1), true);
                            htmltext = "ice_sculpture_q0115_01.htm";
                        }
                    } else if (GetMemoState == 7 && GetMemoStateEx > 7)
                        htmltext = "ice_sculpture_q0115_01a.htm";
                    else if (GetMemoState == 8)
                        htmltext = "ice_sculpture_q0115_07.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 0) {
                        st.setCond(12);
                        st.giveItems(q_part_of_tablet, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "ice_sculpture_q0115_08.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_part_of_tablet) == 1)
                        htmltext = "ice_sculpture_q0115_09.htm";
                break;
        }
        return htmltext;
    }

}