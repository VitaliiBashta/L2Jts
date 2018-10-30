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
public class _023_LidiasHeart extends Quest {
    // npc
    private final static int highpriest_innocentin = 31328;
    private final static int broken_desk1 = 31526;
    private final static int rune_ghost1 = 31524;
    private final static int q_forest_stone1 = 31523;
    private final static int day_violet = 31386;
    private final static int rust_box1 = 31530;
    // questitem
    private final static int q_lost_map = 7063;
    private final static int q_silversp_key1 = 7149;
    private final static int q_ridia_hairpin = 7148;
    private final static int q_ridia_diary = 7064;
    private final static int q_silver_spear = 7150;
    // spawn memo
    private int myself_i_quest0 = 0;

    public _023_LidiasHeart() {
        super(false);
        addStartNpc(highpriest_innocentin);
        addTalkId(highpriest_innocentin, broken_desk1, rune_ghost1, q_forest_stone1, day_violet, rust_box1);
        addQuestCompletedCheck(22);
        addLevelCheck(64);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetMemoState = st.getInt("truth_of_ridia");
        if (npc == null) {
            LOGGER.error("Player: " + st.getPlayer() + " maybe used not valid bypass or PH. NPC nulled in quest: " + getClass().getSimpleName());
            st.getPlayer().sendDebugMessage("Attention, quest not returned HTML. Please, say this ID to GM: " + 23);
            return htmltext;
        }
        final int npcId = npc.getNpcId();
        if (npcId == highpriest_innocentin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.getPlayer().getLevel() < 64) {
                    htmltext = "highpriest_innocentin_q0023_02.htm";
                } else {
                    st.setCond(1);
                    st.setMemoState("truth_of_ridia", String.valueOf(1), true);
                    st.setMemoState("spawned_rune_ghost1", String.valueOf(0), true);
                    st.giveItems(q_lost_map, 1);
                    st.giveItems(q_silversp_key1, 1);
                    st.setState(STARTED);
                    st.soundEffect(SOUND_ACCEPT);
                    htmltext = "highpriest_innocentin_q0023_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                htmltext = "highpriest_innocentin_q0023_05.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                htmltext = "highpriest_innocentin_q0023_06.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("truth_of_ridia", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "highpriest_innocentin_q0023_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_13")) {
                htmltext = "highpriest_innocentin_q0023_11.htm";
            } else if (event.equalsIgnoreCase("reply_15")) {
                if (GetMemoState == 5 || GetMemoState == 6) {
                    st.setCond(5);
                    st.setMemoState("truth_of_ridia", String.valueOf(6), true);
                    htmltext = "highpriest_innocentin_q0023_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_14")) {
                if (GetMemoState == 5 || GetMemoState == 6) {
                    st.setMemoState("truth_of_ridia", String.valueOf(7), true);
                    htmltext = "highpriest_innocentin_q0023_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply_17")) {
                htmltext = "highpriest_innocentin_q0023_16.htm";
            } else if (event.equalsIgnoreCase("reply_18")) {
                htmltext = "highpriest_innocentin_q0023_17.htm";
            } else if (event.equalsIgnoreCase("reply_19")) {
                htmltext = "highpriest_innocentin_q0023_18.htm";
            } else if (event.equalsIgnoreCase("reply_20")) {
                htmltext = "highpriest_innocentin_q0023_19.htm";
                st.soundEffect(SOUND_MT_CREAK01);
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (GetMemoState == 7) {
                    st.setCond(6);
                    st.setMemoState("truth_of_ridia", String.valueOf(8), true);
                    htmltext = "highpriest_innocentin_q0023_20.htm";
                }
            } else if (event.equalsIgnoreCase("reply_16")) {
                st.setCond(5);
                htmltext = "highpriest_innocentin_q0023_21.htm";
            }
        } else if (npcId == broken_desk1) {
            if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 2 && st.ownItemCount(q_silversp_key1) >= 1) {
                    st.setMemoState("truth_of_ridia", String.valueOf(3), true);
                    st.takeItems(q_silversp_key1, -1);
                    htmltext = "broken_desk1_q0023_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                htmltext = "broken_desk1_q0023_04.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                htmltext = "broken_desk1_q0023_05.htm";
            } else if (event.equalsIgnoreCase("reply_8")) {
                st.setMemoState("truth_of_ridia", String.valueOf(GetMemoState + 1), true);
                st.giveItems(q_ridia_hairpin, 1);
                htmltext = "broken_desk1_q0023_06.htm";
                if (st.ownItemCount(q_ridia_diary) >= 1) {
                    st.setCond(4);
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                htmltext = "broken_desk1_q0023_07a.htm";
            } else if (event.equalsIgnoreCase("reply_9")) {
                htmltext = "broken_desk1_q0023_08.htm";
                st.soundEffect(SOUND_ITEMDROP_ARMOR_LEATHER);
            } else if (event.equalsIgnoreCase("reply_10")) {
                htmltext = "broken_desk1_q0023_09.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                htmltext = "broken_desk1_q0023_10.htm";
                st.soundEffect(SOUND_EG_DRON_02);
            } else if (event.equalsIgnoreCase("reply_12")) {
                st.setMemoState("truth_of_ridia", String.valueOf(GetMemoState + 1), true);
                st.giveItems(q_ridia_diary, 1);
                htmltext = "broken_desk1_q0023_11.htm";
                if (st.ownItemCount(q_ridia_hairpin) >= 1) {
                    st.setCond(4);
                }
            } else if (event.equals("read_book")) {
                htmltext = "q_ridia_diary001.htm";
            }
        } else if (npcId == rune_ghost1) {
            if (event.equalsIgnoreCase("reply_23")) {
                htmltext = "rune_ghost1_q0023_02.htm";
                st.soundEffect(SOUND_MHFIGHTER_CRY);
            } else if (event.equalsIgnoreCase("reply_24")) {
                htmltext = "rune_ghost1_q0023_03.htm";
            } else if (event.equalsIgnoreCase("reply_25")) {
                if (GetMemoState == 8) {
                    st.setCond(7);
                    st.setMemoState("truth_of_ridia", String.valueOf(9), true);
                    st.takeItems(q_ridia_diary, -1);
                    htmltext = "rune_ghost1_q0023_04.htm";
                }
            }
        } else if (npcId == q_forest_stone1) {
            if (event.equalsIgnoreCase("reply_22")) {
                if (GetMemoState == 8 || GetMemoState == 9) {
                    if (myself_i_quest0 == 0) {
                        myself_i_quest0 = 1;
                        st.soundEffect(SOUND_HORROR2);
                        NpcInstance ghost1 = st.addSpawn(rune_ghost1, 51432, -54570, -3136);
                        Functions.npcSay(ghost1, NpcString.WHO_AWOKE_ME);
                        st.startQuestTimer("2101", 300000, ghost1);
                        htmltext = "q_forest_stone1_q0023_02.htm";
                    } else {
                        st.soundEffect(SOUND_HORROR2);
                        htmltext = "q_forest_stone1_q0023_03.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("reply_26")) {
                if (GetMemoState == 9) {
                    st.setCond(8);
                    st.setMemoState("truth_of_ridia", String.valueOf(10), true);
                    st.giveItems(q_silversp_key1, 1);
                    htmltext = "q_forest_stone1_q0023_06.htm";
                }
            }
        } else if (npcId == rust_box1) {
            if (event.equalsIgnoreCase("reply_27")) {
                if (GetMemoState == 11 && st.ownItemCount(q_silversp_key1) >= 1) {
                    st.setCond(10);
                    st.giveItems(q_silver_spear, 1);
                    st.takeItems(q_silversp_key1, -1);
                    st.soundEffect(SOUND_ITEMDROP_WEAPON_SPEAR);
                    htmltext = "rust_box1_q0023_02.htm";
                }
            }
        } else if (event.equalsIgnoreCase("2101")) {
            if (npc != null) {
                myself_i_quest0 = 0;
                npc.deleteMe();
            }
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("truth_of_ridia");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == highpriest_innocentin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL: {
                            htmltext = "highpriest_innocentin_q0023_01a.htm";
                            break;
                        }
                        default: {
                            htmltext = "highpriest_innocentin_q0023_01.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == highpriest_innocentin) {
                    if (GetMemoState == 1) {
                        htmltext = "highpriest_innocentin_q0023_04.htm";
                    } else if (GetMemoState == 2) {
                        htmltext = "highpriest_innocentin_q0023_08.htm";
                    } else if (GetMemoState == 5) {
                        htmltext = "highpriest_innocentin_q0023_09.htm";
                    } else if (GetMemoState == 6) {
                        htmltext = "highpriest_innocentin_q0023_14.htm";
                    } else if (GetMemoState == 7) {
                        htmltext = "highpriest_innocentin_q0023_15.htm";
                    } else if (GetMemoState == 8) {
                        st.setCond(6);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "highpriest_innocentin_q0023_22.htm";
                    }
                } else if (npcId == broken_desk1) {
                    if (GetMemoState == 2 && st.ownItemCount(q_silversp_key1) >= 1) {
                        st.setCond(3);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "broken_desk1_q0023_01.htm";
                    } else if (GetMemoState == 3) {
                        htmltext = "broken_desk1_q0023_03.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_ridia_hairpin) >= 1) {
                        htmltext = "broken_desk1_q0023_07.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_ridia_diary) >= 1) {
                        htmltext = "broken_desk1_q0023_12.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_ridia_hairpin) >= 1 && st.ownItemCount(q_ridia_diary) >= 1) {
                        st.startQuestTimer("read_book", 120000);
                        htmltext = "broken_desk1_q0023_13.htm";
                    }
                } else if (npcId == rune_ghost1) {
                    if (GetMemoState == 8) {
                        htmltext = "rune_ghost1_q0023_01.htm";
                    } else if (GetMemoState == 9 && st.ownItemCount(q_silversp_key1) == 0) {
                        htmltext = "rune_ghost1_q0023_05.htm";
                    } else if ((GetMemoState == 9 || GetMemoState == 10) && st.ownItemCount(q_silversp_key1) >= 1) {
                        st.setMemoState("truth_of_ridia", String.valueOf(10), true);
                        htmltext = "rune_ghost1_q0023_06.htm";
                    }
                } else if (npcId == q_forest_stone1) {
                    if (GetMemoState == 8) {
                        htmltext = "q_forest_stone1_q0023_01.htm";
                    } else if (GetMemoState == 9) {
                        htmltext = "q_forest_stone1_q0023_04.htm";
                    } else if (GetMemoState == 10) {
                        htmltext = "q_forest_stone1_q0023_05.htm";
                    }
                } else if (npcId == day_violet) {
                    if (GetMemoState == 10 && st.ownItemCount(q_silversp_key1) >= 1) {
                        st.setCond(9);
                        st.setMemoState("truth_of_ridia", String.valueOf(11), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "day_violet_q0023_01.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_silver_spear) == 0) {
                        htmltext = "day_violet_q0023_02.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_silver_spear) >= 1) {
                        st.giveItems(ADENA_ID, 350000);
                        st.addExpAndSp(456893, 42112);
                        st.takeItems(q_silver_spear, -1);
                        st.removeMemo("truth_of_ridia");
                        st.removeMemo("spawned_rune_ghost1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "day_violet_q0023_03.htm";
                    }
                } else if (npcId == rust_box1) {
                    if (GetMemoState == 11 && st.ownItemCount(q_silversp_key1) >= 1) {
                        htmltext = "rust_box1_q0023_01.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_silver_spear) >= 1) {
                        htmltext = "rust_box1_q0023_03.htm";
                    }
                }
                break;
            }
            case COMPLETED: {
                if (npcId == day_violet) {
                    htmltext = "day_violet_q0023_04.htm";
                }
                break;
            }
        }
        return htmltext;
    }
}