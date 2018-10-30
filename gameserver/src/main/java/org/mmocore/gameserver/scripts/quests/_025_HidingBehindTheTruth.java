package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 28/01/2015
 * @LastEdit 31/03/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _025_HidingBehindTheTruth extends Quest {
    // npc
    private final static int falsepriest_agripel = 31348;
    private final static int falsepriest_benedict = 31349;
    private final static int broken_desk2 = 31533;
    private final static int broken_desk3 = 31534;
    private final static int broken_desk4 = 31535;
    private final static int q_forest_box1 = 31536;
    private final static int maid_of_ridia = 31532;
    private final static int shadow_hardin = 31522;
    private final static int q_forest_stone2 = 31531;
    // etcitem
    private final static int q_lost_map = 7063;
    private final static int q_lost_contract = 7066;
    private final static int earing_of_blessing = 874;
    private final static int ring_of_blessing = 905;
    private final static int necklace_of_blessing = 936;
    // questitem
    private final static int q_lost_jewel_key = 7157;
    private final static int q_ridias_dress = 7155;
    private final static int q_triols_totem2 = 7156;
    private final static int q_triols_totem3 = 7158;
    // Quest Monster
    private final static int triyol_zzolda = 27218;
    // spawn memo
    private int myself_i_quest0 = 0;
    // player objectId
    private int myself_i_quest1 = 0;

    public _025_HidingBehindTheTruth() {
        super(false);
        addStartNpc(falsepriest_benedict);
        addTalkId(falsepriest_agripel, shadow_hardin, broken_desk2, broken_desk3, broken_desk4, maid_of_ridia, q_forest_stone2, q_forest_box1);
        addAttackId(triyol_zzolda);
        addKillId(triyol_zzolda);
        addQuestItem(q_triols_totem3);
        addQuestCompletedCheck(24);
        addLevelCheck(66);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("man_behind_the_truth");
        int GetMemoStateEx = st.getInt("man_behind_the_truth_ex");
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("man_behind_the_truth", String.valueOf(1), true);
            st.setMemoState("spawned_triyol_zzolda", String.valueOf(0), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "falsepriest_benedict_q0025_03.htm";
        } else if (event.equalsIgnoreCase("reply_1")) {
            if (GetMemoState == 1 && st.ownItemCount(q_triols_totem2) >= 1)
                htmltext = "falsepriest_benedict_q0025_04.htm";
            else if (GetMemoState == 1 && st.ownItemCount(q_triols_totem2) == 0) {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "falsepriest_benedict_q0025_05.htm";
            }
        } else if (event.equalsIgnoreCase("reply_3")) {
            if (GetMemoState == 1 && st.ownItemCount(q_triols_totem2) >= 1) {
                st.setCond(4);
                st.setMemoState("man_behind_the_truth", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "falsepriest_benedict_q0025_10.htm";
            }
        } else if (event.equalsIgnoreCase("reply_4")) {
            if (GetMemoState == 2) {
                st.setMemoState("man_behind_the_truth", String.valueOf(3), true);
                st.takeItems(q_triols_totem2, -1);
                htmltext = "falsepriest_agripel_q0025_02.htm";
            }
        } else if (event.equalsIgnoreCase("reply_6")) {
            if (GetMemoState == 3) {
                st.setCond(5);
                st.setMemoState("man_behind_the_truth", String.valueOf(6), true);
                st.giveItems(q_lost_jewel_key, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "falsepriest_agripel_q0025_08.htm";
            }
        } else if (event.equalsIgnoreCase("reply_25")) {
            if (GetMemoState == 20 && st.ownItemCount(q_triols_totem3) >= 1) {
                st.setMemoState("man_behind_the_truth", String.valueOf(21), true);
                st.takeItems(q_triols_totem3, -1);
                htmltext = "falsepriest_agripel_q0025_10.htm";
            }
        } else if (event.equalsIgnoreCase("reply_21")) {
            if (GetMemoState == 21) {
                st.setMemoState("man_behind_the_truth", String.valueOf(22), true);
                htmltext = "falsepriest_agripel_q0025_13.htm";
            }
        } else if (event.equalsIgnoreCase("reply_23")) {
            if (GetMemoState == 22) {
                st.setCond(17);
                st.setMemoState("man_behind_the_truth", String.valueOf(23), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "falsepriest_agripel_q0025_16.htm";
            }
        } else if (event.equalsIgnoreCase("reply_24")) {
            if (GetMemoState == 22) {
                st.setCond(18);
                st.setMemoState("man_behind_the_truth", String.valueOf(24), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "falsepriest_agripel_q0025_17.htm";
            }
        } else if (event.equalsIgnoreCase("reply_7")) {
            if (GetMemoState == 6 && st.ownItemCount(q_lost_jewel_key) >= 1) {
                st.setCond(6);
                st.setMemoState("man_behind_the_truth", String.valueOf(7), true);
                st.setMemoState("man_behind_the_truth_ex", String.valueOf(20), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "shadow_hardin_q0025_04.htm";
            }
        } else if (event.equalsIgnoreCase("reply_17")) {
            if (GetMemoState == 16) {
                st.setMemoState("man_behind_the_truth", String.valueOf(19), true);
                htmltext = "shadow_hardin_q0025_10.htm";
            }
        } else if (event.equalsIgnoreCase("reply_19")) {
            if (GetMemoState == 19) {
                st.setCond(16);
                st.setMemoState("man_behind_the_truth", String.valueOf(20), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "shadow_hardin_q0025_13.htm";
            }
        } else if (event.equalsIgnoreCase("reply_23a")) {
            if (GetMemoState == 24) {
                st.giveItems(earing_of_blessing, 1);
                st.giveItems(necklace_of_blessing, 1);
                st.takeItems(q_lost_map, -1);
                st.addExpAndSp(572277, 53750);
                st.removeMemo("man_behind_the_truth");
                st.removeMemo("man_behind_the_truth_ex");
                st.exitQuest(false);
                st.soundEffect(SOUND_FINISH);
                htmltext = "shadow_hardin_q0025_16.htm";
            }
        } else if (event.equalsIgnoreCase("reply_8")) {
            int i0 = GetMemoState;
            i0 = i0 % 1000;
            if (i0 >= 100)
                htmltext = "broken_desk2_q0025_03.htm";
            else if (Rnd.get(60) > GetMemoStateEx) {
                st.setMemoState("man_behind_the_truth_ex", String.valueOf(GetMemoStateEx + 20), true);
                st.setMemoState("man_behind_the_truth", String.valueOf(GetMemoState + 100), true);
                htmltext = "broken_desk2_q0025_04.htm";
            } else {
                st.setMemoState("man_behind_the_truth", String.valueOf(8), true);
                htmltext = "broken_desk2_q0025_05.htm";
                st.soundEffect(SOUND_DD_HORROR_02);
            }
        } else if (event.equalsIgnoreCase("reply_9")) {
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) == 0) {
                if (myself_i_quest0 == 0) {
                    st.setCond(7);
                    myself_i_quest0 = 1;
                    myself_i_quest1 = st.getPlayer().getObjectId();
                    NpcInstance zzolda = st.addSpawn(triyol_zzolda, 47142, -35941, -1623);
                    if (zzolda != null)
                        zzolda.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 20000);
                    st.startQuestTimer("2501", 500, zzolda);
                    st.startQuestTimer("2502", 120000, zzolda);
                    htmltext = "broken_desk2_q0025_07.htm";
                } else if (myself_i_quest1 == st.getPlayer().getObjectId())
                    htmltext = "broken_desk2_q0025_08.htm";
                else
                    htmltext = "broken_desk2_q0025_09.htm";
            }
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) >= 1)
                htmltext = "broken_desk2_q0025_10.htm";
        } else if (event.equalsIgnoreCase("2501")) {
            Functions.npcSay(npc, NpcString.THAT_BOX_WAS_SEALED_BY_MY_MASTER_S1_DONT_TOUCH_IT, st.getPlayer().getName());
            return null;
        } else if (event.equalsIgnoreCase("2502")) {
            if (npc != null) {
                myself_i_quest0 = 0;
                myself_i_quest1 = 0;
                npc.deleteMe();
            }
            return null;
        } else if (event.equalsIgnoreCase("reply_10")) {
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) >= 1 && st.ownItemCount(q_lost_jewel_key) >= 1) {
                st.setCond(9);
                st.setMemoState("man_behind_the_truth", String.valueOf(9), true);
                st.giveItems(q_lost_contract, 1);
                st.takeItems(q_lost_jewel_key, -1);
                htmltext = "broken_desk2_q0025_11.htm";
            }
        } else if (event.equalsIgnoreCase("reply_8a")) {
            int i0 = GetMemoState;
            i0 = i0 % 10000;
            if (i0 >= 1000)
                htmltext = "broken_desk3_q0025_03.htm";
            else if (Rnd.get(60) > GetMemoStateEx) {
                st.setMemoState("man_behind_the_truth_ex", String.valueOf(GetMemoStateEx + 20), true);
                st.setMemoState("man_behind_the_truth", String.valueOf(GetMemoState + 1000), true);
                htmltext = "broken_desk3_q0025_04.htm";
            } else {
                st.setMemoState("man_behind_the_truth", String.valueOf(8), true);
                htmltext = "broken_desk3_q0025_05.htm";
                st.soundEffect(SOUND_DD_HORROR_02);
            }
        } else if (event.equalsIgnoreCase("reply_9a")) {
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) == 0) {
                if (myself_i_quest0 == 0) {
                    st.setCond(7);
                    myself_i_quest0 = 1;
                    myself_i_quest1 = st.getPlayer().getObjectId();
                    NpcInstance zzolda = st.addSpawn(triyol_zzolda, 50055, -47020, -3396);
                    if (zzolda != null)
                        zzolda.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 20000);
                    st.startQuestTimer("2501", 500, zzolda);
                    st.startQuestTimer("2502", 120000, zzolda);
                    htmltext = "broken_desk3_q0025_07.htm";
                } else if (myself_i_quest1 == st.getPlayer().getObjectId())
                    htmltext = "broken_desk3_q0025_08.htm";
                else
                    htmltext = "broken_desk3_q0025_09.htm";
            }
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) >= 1)
                htmltext = "broken_desk3_q0025_10.htm";
        } else if (event.equalsIgnoreCase("reply_10a")) {
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) >= 1 && st.ownItemCount(q_lost_jewel_key) >= 1) {
                st.setCond(9);
                st.setMemoState("man_behind_the_truth", String.valueOf(9), true);
                st.giveItems(q_lost_contract, 1);
                st.takeItems(q_lost_jewel_key, -1);
                htmltext = "broken_desk3_q0025_11.htm";
            }
        } else if (event.equalsIgnoreCase("reply_8b")) {
            if (GetMemoState >= 10000)
                htmltext = "broken_desk4_q0025_03.htm";
            else if (Rnd.get(60) > GetMemoStateEx) {
                st.setMemoState("man_behind_the_truth_ex", String.valueOf(GetMemoStateEx + 20), true);
                st.setMemoState("man_behind_the_truth", String.valueOf(GetMemoState + 10000), true);
                htmltext = "broken_desk4_q0025_04.htm";
            } else {
                st.setMemoState("man_behind_the_truth", String.valueOf(8), true);
                htmltext = "broken_desk4_q0025_05.htm";
                st.soundEffect(SOUND_DD_HORROR_02);
            }
        } else if (event.equalsIgnoreCase("reply_9b")) {
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) == 0) {
                if (myself_i_quest0 == 0) {
                    st.setCond(7);
                    myself_i_quest0 = 1;
                    myself_i_quest1 = st.getPlayer().getObjectId();
                    NpcInstance zzolda = st.addSpawn(triyol_zzolda, 59712, -47568, -2720);
                    if (zzolda != null)
                        zzolda.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 20000);
                    st.startQuestTimer("2501", 500, zzolda);
                    st.startQuestTimer("2502", 120000, zzolda);
                    htmltext = "broken_desk4_q0025_07.htm";
                } else if (myself_i_quest1 == st.getPlayer().getObjectId())
                    htmltext = "broken_desk4_q0025_08.htm";
                else
                    htmltext = "broken_desk4_q0025_09.htm";
            }
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) >= 1)
                htmltext = "broken_desk4_q0025_10.htm";
        } else if (event.equalsIgnoreCase("reply_10b")) {
            if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) >= 1 && st.ownItemCount(q_lost_jewel_key) >= 1) {
                st.setCond(9);
                st.setMemoState("man_behind_the_truth", String.valueOf(9), true);
                st.giveItems(q_lost_contract, 1);
                st.takeItems(q_lost_jewel_key, -1);
                htmltext = "broken_desk4_q0025_11.htm";
            }
        } else if (event.equalsIgnoreCase("reply_11")) {
            if (GetMemoState == 9 && st.ownItemCount(q_lost_contract) >= 1) {
                st.takeItems(q_lost_contract, -1);
                st.setMemoState("man_behind_the_truth", String.valueOf(10), true);
                htmltext = "maid_of_ridia_q0025_02.htm";
            }
        } else if (event.equalsIgnoreCase("reply_13")) {
            if (GetMemoState == 10) {
                st.setCond(11);
                st.setMemoState("man_behind_the_truth", String.valueOf(11), true);
                st.soundEffect(SOUND_HORROR1);
                htmltext = "maid_of_ridia_q0025_07.htm";
            }
        } else if (event.equalsIgnoreCase("reply_14")) {
            if (GetMemoState == 13) {
                if (GetMemoStateEx <= 3) {
                    st.setMemoState("man_behind_the_truth_ex", String.valueOf(GetMemoStateEx + 1), true);
                    st.soundEffect(SOUND_FDELF_CRY);
                    htmltext = "maid_of_ridia_q0025_11.htm";
                } else {
                    st.setMemoState("man_behind_the_truth", String.valueOf(14), true);
                    htmltext = "maid_of_ridia_q0025_12.htm";
                }
            }
        } else if (event.equalsIgnoreCase("reply_15")) {
            if (GetMemoState == 14) {
                st.setMemoState("man_behind_the_truth", String.valueOf(15), true);
                htmltext = "maid_of_ridia_q0025_17.htm";
            }
        } else if (event.equalsIgnoreCase("reply_16")) {
            if (GetMemoState == 15) {
                st.setCond(15);
                st.setMemoState("man_behind_the_truth", String.valueOf(16), true);
                htmltext = "maid_of_ridia_q0025_21.htm";
            }
        } else if (event.equalsIgnoreCase("reply_22")) {
            if (GetMemoState == 23) {
                st.giveItems(earing_of_blessing, 1);
                st.giveItems(ring_of_blessing, 2);
                st.takeItems(q_lost_map, -1);
                st.addExpAndSp(572277, 53750);
                st.soundEffect(SOUND_FINISH);
                st.removeMemo("man_behind_the_truth");
                st.removeMemo("man_behind_the_truth_ex");
                htmltext = "maid_of_ridia_q0025_25.htm";
                st.exitQuest(false);
            }
        } else if (event.equalsIgnoreCase("reply_1a")) {
            if (GetMemoState == 11) {
                st.setCond(12);
                st.addSpawn(q_forest_box1, 60104, -35820, -681, 20000);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "q_forest_stone2_q0025_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("man_behind_the_truth");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == falsepriest_benedict) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "falsepriest_benedict_q0025_02.htm";
                            break;
                        default:
                            htmltext = "falsepriest_benedict_q0025_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == falsepriest_benedict) {
                    if (GetMemoState == 1)
                        htmltext = "falsepriest_benedict_q0025_03a.htm";
                    else if (GetMemoState == 2)
                        htmltext = "falsepriest_benedict_q0025_11.htm";
                } else if (npcId == falsepriest_agripel) {
                    if (GetMemoState == 2)
                        htmltext = "falsepriest_agripel_q0025_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "falsepriest_agripel_q0025_03.htm";
                    else if (GetMemoState == 6)
                        htmltext = "falsepriest_agripel_q0025_08a.htm";
                    else if (GetMemoState == 20 && st.ownItemCount(q_triols_totem3) >= 1)
                        htmltext = "falsepriest_agripel_q0025_09.htm";
                    else if (GetMemoState == 21)
                        htmltext = "falsepriest_agripel_q0025_10a.htm";
                    else if (GetMemoState == 22)
                        htmltext = "falsepriest_agripel_q0025_15.htm";
                    else if (GetMemoState == 23)
                        htmltext = "falsepriest_agripel_q0025_18.htm";
                    else if (GetMemoState == 24)
                        htmltext = "falsepriest_agripel_q0025_19.htm";
                } else if (npcId == shadow_hardin) {
                    if (GetMemoState == 1 && st.ownItemCount(q_triols_totem2) == 0) {
                        st.setCond(3);
                        st.giveItems(q_triols_totem2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "shadow_hardin_q0025_01.htm";
                    } else if (GetMemoState == 1 && st.ownItemCount(q_triols_totem2) >= 1)
                        htmltext = "shadow_hardin_q0025_02.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_lost_jewel_key) >= 1)
                        htmltext = "shadow_hardin_q0025_03.htm";
                    else if (GetMemoState % 100 == 7)
                        htmltext = "shadow_hardin_q0025_05.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_lost_contract) >= 1) {
                        st.setCond(10);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "shadow_hardin_q0025_06.htm";
                    } else if (GetMemoState == 16)
                        htmltext = "shadow_hardin_q0025_06a.htm";
                    else if (GetMemoState == 19)
                        htmltext = "shadow_hardin_q0025_12.htm";
                    else if (GetMemoState == 20)
                        htmltext = "shadow_hardin_q0025_14.htm";
                    else if (GetMemoState == 24)
                        htmltext = "shadow_hardin_q0025_15.htm";
                    else if (GetMemoState == 23)
                        htmltext = "shadow_hardin_q0025_15a.htm";
                } else if (npcId == broken_desk2) {
                    if (GetMemoState % 100 == 7)
                        htmltext = "broken_desk2_q0025_01.htm";
                    else if (GetMemoState % 100 >= 9)
                        htmltext = "broken_desk2_q0025_02.htm";
                    else if (GetMemoState == 8)
                        htmltext = "broken_desk2_q0025_06.htm";
                } else if (npcId == broken_desk3) {
                    if (GetMemoState % 100 == 7)
                        htmltext = "broken_desk3_q0025_01.htm";
                    else if (GetMemoState % 100 >= 9)
                        htmltext = "broken_desk3_q0025_02.htm";
                    else if (GetMemoState == 8)
                        htmltext = "broken_desk3_q0025_06.htm";
                } else if (npcId == broken_desk4) {
                    if (GetMemoState % 100 == 7)
                        htmltext = "broken_desk4_q0025_01.htm";
                    else if (GetMemoState % 100 >= 9)
                        htmltext = "broken_desk4_q0025_02.htm";
                    else if (GetMemoState == 8)
                        htmltext = "broken_desk4_q0025_06.htm";
                } else if (npcId == maid_of_ridia) {
                    if (GetMemoState == 9 && st.ownItemCount(q_lost_contract) >= 1)
                        htmltext = "maid_of_ridia_q0025_01.htm";
                    else if (GetMemoState == 10)
                        htmltext = "maid_of_ridia_q0025_03.htm";
                    else if (GetMemoState == 11) {
                        st.soundEffect(SOUND_HORROR1);
                        htmltext = "maid_of_ridia_q0025_08.htm";
                    } else if (GetMemoState == 12 && st.ownItemCount(q_ridias_dress) >= 1) {
                        st.setCond(14);
                        st.takeItems(q_ridias_dress, -1);
                        st.setMemoState("man_behind_the_truth", String.valueOf(13), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maid_of_ridia_q0025_09.htm";
                    } else if (GetMemoState == 13) {
                        st.setMemoState("man_behind_the_truth_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_FDELF_CRY);
                        htmltext = "maid_of_ridia_q0025_10.htm";
                    } else if (GetMemoState == 14)
                        htmltext = "maid_of_ridia_q0025_13.htm";
                    else if (GetMemoState == 15)
                        htmltext = "maid_of_ridia_q0025_18.htm";
                    else if (GetMemoState == 16)
                        htmltext = "maid_of_ridia_q0025_22.htm";
                    else if (GetMemoState == 23)
                        htmltext = "maid_of_ridia_q0025_23.htm";
                    else if (GetMemoState == 24)
                        htmltext = "maid_of_ridia_q0025_24.htm";
                } else if (npcId == q_forest_stone2) {
                    if (GetMemoState == 11)
                        htmltext = "q_forest_stone2_q0025_01.htm";
                    else if (GetMemoState == 12)
                        htmltext = "q_forest_stone2_q0025_03.htm";
                } else if (npcId == q_forest_box1) {
                    if (GetMemoState == 11) {
                        st.setCond(13);
                        st.setMemoState("man_behind_the_truth", String.valueOf(12), true);
                        st.giveItems(q_ridias_dress, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "q_forest_box1_q0025_01.htm";
                        if (npc != null)
                            npc.deleteMe();
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("man_behind_the_truth");
        if (npcId == triyol_zzolda) {
            if (npc.getCurrentHpPercents() <= 30) {
                if (GetMemoState == 8 && st.ownItemCount(q_triols_totem3) == 0 && myself_i_quest1 == st.getPlayer().getObjectId()) {
                    st.setCond(8);
                    st.giveItems(q_triols_totem3, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    Functions.npcSay(npc, NpcString.YOUVE_ENDED_MY_IMMORTAL_LIFE_YOURE_PROTECTED_BY_THE_FEUDAL_LORD_ARENT_YOU);
                    if (st.isRunningQuestTimer("2502"))
                        st.cancelQuestTimer("2502");
                    if (npc != null) {
                        myself_i_quest0 = 0;
                        myself_i_quest1 = 0;
                        npc.deleteMe();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == triyol_zzolda) {
            myself_i_quest0 = 0;
            myself_i_quest1 = 0;
        }
        return null;
    }
}