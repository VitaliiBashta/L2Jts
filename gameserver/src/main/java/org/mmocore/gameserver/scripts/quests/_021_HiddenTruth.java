package org.mmocore.gameserver.scripts.quests;

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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _021_HiddenTruth extends Quest {
    // npc
    private static final int shadow_hardin = 31522;
    private static final int q_forest_stone1 = 31523;
    private static final int rune_ghost1 = 31524;
    private static final int rune_ghost1b = 31525;
    private static final int broken_desk1 = 31526;
    private static final int falsepriest_agripel = 31348;
    private static final int falsepriest_benedict = 31349;
    private static final int falsepriest_dominic = 31350;
    private static final int highpriest_innocentin = 31328;
    // questitem
    private static final int q_cross_of_einhasad1 = 7140;
    private static final int q_cross_of_einhasad2 = 7141;
    // value spawn mob
    private int count_rune_ghost1b = 0;

    public _021_HiddenTruth() {
        super(false);
        addStartNpc(shadow_hardin);
        addTalkId(q_forest_stone1, rune_ghost1, rune_ghost1b, broken_desk1, falsepriest_agripel, falsepriest_benedict, falsepriest_dominic, highpriest_innocentin);
        addLevelCheck(63);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetMemoState = st.getInt("secret_truth");
        final int GetMemoStateEx = st.getInt("secret_truth_ex");
        final int spawned_rune_ghost1 = st.getInt("spawned_rune_ghost1");
        final int spawned_rune_ghost1b = st.getInt("spawned_rune_ghost1b");
        final int npcId = npc.getNpcId();
        if (npcId == shadow_hardin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("secret_truth", String.valueOf(1), true);
                st.setMemoState("spawned_rune_ghost1", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "shadow_hardin_q0021_02.htm";
            }
        } else if (npcId == q_forest_stone1) {
            if (event.equalsIgnoreCase("reply_1")) {
                htmltext = "q_forest_stone1_q0021_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1 || GetMemoState == 3) {
                    if (spawned_rune_ghost1 == 0) {
                        st.setCond(2);
                        st.soundEffect(SOUND_HORROR2);
                        st.setMemoState("spawned_rune_ghost1", String.valueOf(1), true);
                        htmltext = "q_forest_stone1_q0021_03.htm";
                        NpcInstance ghost1 = st.addSpawn(rune_ghost1, 51432, -54570, -3136);
                        Functions.npcSay(ghost1, NpcString.WHO_AWOKE_ME);
                        st.startQuestTimer("2101", 300000, ghost1);
                    } else {
                        st.soundEffect(SOUND_HORROR2);
                        htmltext = "q_forest_stone1_q0021_04.htm";
                    }
                }
            }
        } else if (npcId == rune_ghost1) {
            if (event.equalsIgnoreCase("reply_3")) {
                if (count_rune_ghost1b <= 5) {
                    st.setCond(3);
                    st.setMemoState("secret_truth", String.valueOf(3), true);
                    st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 1), true);
                    st.setMemoState("spawned_rune_ghost1b", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    NpcInstance ghost1b = st.addSpawn(rune_ghost1b, 51446, -54514, -3136);
                    count_rune_ghost1b++;
                    st.startQuestTimer("2102", 500, ghost1b);
                    st.startQuestTimer("2103", 120000, ghost1b);
                    return null;
                } else {
                    htmltext = "rune_ghost1_q0021_06a.htm";
                }
            } else if (event.equalsIgnoreCase("2101")) {
                st.setMemoState("spawned_rune_ghost1", String.valueOf(0), true);
                if (spawned_rune_ghost1 == 1) {
                    npc.deleteMe();
                }
                return null;
            }
        } else if (npcId == rune_ghost1b) {
            if (event.equalsIgnoreCase("2102")) {
                if (spawned_rune_ghost1b == 0) {
                    Functions.npcSay(npc, NpcString.MY_MASTER_HAS_INSTRUCTED_ME_TO_BE_YOUR_GUIDE_S1, st.getPlayer().getName());
                }
                return null;
            } else if (event.equalsIgnoreCase("2103")) {
                count_rune_ghost1b--;
                if (count_rune_ghost1b < 0) {
                    count_rune_ghost1b = 0;
                }
                if (npc != null) {
                    npc.deleteMe();
                }
                return null;
            } else if (event.equalsIgnoreCase("2105")) {
                count_rune_ghost1b--;
                if (count_rune_ghost1b < 0)
                    count_rune_ghost1b = 0;
                if (npc != null) {
                    npc.deleteMe();
                }
                return null;
            }
        } else if (npcId == broken_desk1) {
            if (event.equalsIgnoreCase("reply_4")) {
                st.soundEffect(SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH);
                htmltext = "broken_desk1_q0021_03.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                htmltext = "broken_desk1_q0021_04.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                htmltext = "broken_desk1_q0021_05.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                st.setMemoState("secret_truth", String.valueOf(5), true);
                htmltext = "broken_desk1_q0021_07.htm";
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 5) {
                    st.setCond(5);
                    st.setMemoState("secret_truth", String.valueOf(6), true);
                    st.soundEffect(SOUND_ED_CHIMES05);
                    return null;
                } else if (GetMemoState == 6) {
                    htmltext = "broken_desk1_q0021_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9")) {
                htmltext = "broken_desk1_q0021_12.htm";
            } else if (event.equalsIgnoreCase("reply_10")) {
                st.setCond(5);
                st.setMemoState("secret_truth", String.valueOf(7), true);
                st.setMemoState("secret_truth_ex", String.valueOf(0), true);
                st.giveItems(q_cross_of_einhasad1, 1);
                htmltext = "broken_desk1_q0021_14.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                htmltext = "broken_desk1_q0021_13.htm";
            }
        } else if (npcId == highpriest_innocentin) {
            if (event.equalsIgnoreCase("reply_11")) {
                htmltext = "highpriest_innocentin_q0021_02.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                htmltext = "highpriest_innocentin_q0021_03.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                htmltext = "highpriest_innocentin_q0021_04.htm";
            } else if (event.equalsIgnoreCase("reply_14")) {
                st.takeItems(q_cross_of_einhasad1, 1);
                st.giveItems(q_cross_of_einhasad2, 1);
                st.addExpAndSp(131228, 11978);
                st.soundEffect(SOUND_FINISH);
                st.removeMemo("secret_truth");
                st.removeMemo("secret_truth_ex");
                st.removeMemo("spawned_rune_ghost1");
                st.removeMemo("spawned_rune_ghost1b");
                st.exitQuest(false);
                htmltext = "highpriest_innocentin_q0021_05.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("secret_truth");
        int GetMemoStateEx = st.getInt("secret_truth_ex");
        int spawned_rune_ghost1b = st.getInt("spawned_rune_ghost1b");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == shadow_hardin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "shadow_hardin_q0021_03.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "shadow_hardin_q0021_01.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == shadow_hardin) {
                    if (GetMemoState == 1) {
                        htmltext = "shadow_hardin_q0021_05.htm";
                    }
                } else if (npcId == q_forest_stone1) {
                    if (GetMemoState == 1 || GetMemoState == 3) {
                        htmltext = "q_forest_stone1_q0021_01.htm";
                    }
                } else if (npcId == rune_ghost1) {
                    if (GetMemoState == 1) {
                        htmltext = "rune_ghost1_q0021_01.htm";
                    } else if (GetMemoState == 3 && GetMemoStateEx <= 20) {
                        if (count_rune_ghost1b <= 5) {
                            st.setCond(3);
                            st.soundEffect(SOUND_MIDDLE);
                            st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 1), true);
                            st.setMemoState("spawned_rune_ghost1b", String.valueOf(0), true);
                            htmltext = "rune_ghost1_q0021_07.htm";
                            NpcInstance ghost1b = st.addSpawn(rune_ghost1b, 51446, -54514, -3136);
                            count_rune_ghost1b++;
                            st.startQuestTimer("2102", 500, ghost1b);
                            st.startQuestTimer("2103", 120000, ghost1b);
                            return null;
                        } else {
                            htmltext = "rune_ghost1_q0021_07a.htm";
                        }
                    } else if (GetMemoState == 3 && GetMemoStateEx > 20) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rune_ghost1_q0021_07b.htm";
                    } else if (GetMemoState >= 4) {
                        htmltext = "rune_ghost1_q0021_07c.htm";
                    }
                } else if (npcId == rune_ghost1b) {
                    if (GetMemoState == 3) {
                        if (spawned_rune_ghost1b == 0) {
                            htmltext = "rune_ghost1b_q0021_01.htm";
                        } else {
                            htmltext = "rune_ghost1b_q0021_02.htm";
                            st.startQuestTimer("2105", 3000);
                        }
                    }
                } else if (npcId == broken_desk1) {
                    if (GetMemoState == 3) {
                        htmltext = "broken_desk1_q0021_01.htm";
                    } else if (GetMemoState == 5) {
                        st.setCond(5);
                        st.setMemoState("secret_truth", String.valueOf(6), true);
                        st.soundEffect(SOUND_ED_CHIMES05);
                        htmltext = "broken_desk1_q0021_10.htm";
                    } else if (GetMemoState == 6) {
                        htmltext = "broken_desk1_q0021_11.htm";
                    } else if (GetMemoState == 7) {
                        htmltext = "broken_desk1_q0021_15.htm";
                    }
                } else if (npcId == falsepriest_agripel) {
                    if (GetMemoState == 7 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        st.setMemoState("secret_truth", String.valueOf(GetMemoState + 1), true);
                        st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 1), true);
                        htmltext = "falsepriest_agripel_q0021_01.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && (GetMemoStateEx == 2 || GetMemoStateEx == 4)) {
                        st.setMemoState("secret_truth", String.valueOf(GetMemoState + 1), true);
                        st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 1), true);
                        htmltext = "falsepriest_agripel_q0021_02.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx == 1) {
                        htmltext = "falsepriest_agripel_q0021_01.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx == 6) {
                        st.setCond(7);
                        st.setMemoState("secret_truth", String.valueOf(10), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "falsepriest_agripel_q0021_03.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx != 6) {
                        htmltext = "falsepriest_agripel_q0021_02.htm";
                    } else if (GetMemoState == 10 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        htmltext = "falsepriest_agripel_q0021_03.htm";
                    }
                } else if (npcId == falsepriest_benedict) {
                    if (GetMemoState == 7 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        st.setMemoState("secret_truth", String.valueOf(GetMemoState + 1), true);
                        st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 2), true);
                        htmltext = "falsepriest_benedict_q0021_01.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && (GetMemoStateEx == 1 || GetMemoStateEx == 4)) {
                        st.setMemoState("secret_truth", String.valueOf(GetMemoState + 1), true);
                        st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 2), true);
                        htmltext = "falsepriest_benedict_q0021_02.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx == 2) {
                        htmltext = "falsepriest_benedict_q0021_01.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx == 5) {
                        st.setCond(7);
                        st.setMemoState("secret_truth", String.valueOf(10), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "falsepriest_benedict_q0021_03.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx != 5) {
                        htmltext = "falsepriest_benedict_q0021_02.htm";
                    } else if (GetMemoState == 10 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        htmltext = "falsepriest_benedict_q0021_03.htm";
                    }
                } else if (npcId == falsepriest_dominic) {
                    if (GetMemoState == 7 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        st.setMemoState("secret_truth", String.valueOf(GetMemoState + 1), true);
                        st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 4), true);
                        htmltext = "falsepriest_dominic_q0021_01.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && (GetMemoStateEx == 1 || GetMemoStateEx == 2)) {
                        st.setMemoState("secret_truth", String.valueOf(GetMemoState + 1), true);
                        st.setMemoState("secret_truth_ex", String.valueOf(GetMemoStateEx + 4), true);
                        htmltext = "falsepriest_dominic_q0021_02.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx == 4) {
                        htmltext = "falsepriest_dominic_q0021_01.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx == 3) {
                        st.setCond(7);
                        st.setMemoState("secret_truth", String.valueOf(10), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "falsepriest_dominic_q0021_03.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_cross_of_einhasad1) >= 1 && GetMemoStateEx != 3) {
                        htmltext = "falsepriest_dominic_q0021_02.htm";
                    } else if (GetMemoState == 10 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        htmltext = "falsepriest_dominic_q0021_03.htm";
                    }
                } else if (npcId == highpriest_innocentin) {
                    if (GetMemoState == 10 && st.ownItemCount(q_cross_of_einhasad1) >= 1) {
                        htmltext = "highpriest_innocentin_q0021_01.htm";
                    }
                }
                break;
            }
            case COMPLETED: {
                if (!st.getPlayer().getQuestState(22).isCreated()) {
                    htmltext = "highpriest_innocentin_q0021_06.htm";
                }
                break;
            }
        }
        return htmltext;
    }
}