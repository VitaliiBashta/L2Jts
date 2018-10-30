package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
public class _136_MoreThanMeetsTheEye extends Quest {
    // npc
    private static final int hardin = 30832;
    private static final int magister_errickin = 30701;
    private static final int magister_clayton = 30464;
    // mobs
    private static final int glass_jaguar = 20250;
    private static final int mirrorforest_ghost_b = 20636;
    private static final int mirrorforest_ghost_c = 20637;
    private static final int mirrorforest_ghost_d = 20638;
    private static final int mirror = 20639;
    // questitem
    private static final int q_stable_ectoprasm = 9786;
    private static final int q_ectoprasm = 9787;
    private static final int q_empty_shape_change_book_order = 9788;
    private static final int q_mordeo_crystal = 9789;
    private static final int q_empty_shape_change_book = 9790;
    // Item
    private static final int sb_transform_onyx_beast = 9648;

    public _136_MoreThanMeetsTheEye() {
        super(false);
        addStartNpc(hardin);
        addTalkId(magister_errickin, magister_clayton);
        addQuestItem(q_stable_ectoprasm, q_ectoprasm, q_empty_shape_change_book_order, q_mordeo_crystal, q_empty_shape_change_book);
        addKillId(mirrorforest_ghost_b, mirrorforest_ghost_c, mirrorforest_ghost_d, mirror, glass_jaguar);
        addLevelCheck(50);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("other_soul_other_shape");
        int npcId = npc.getNpcId();
        if (npcId == hardin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("other_soul_other_shape", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "hardin_q0136_04.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetMemoState == 1)
                htmltext = "hardin_q0136_06.htm";
            else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 1)
                htmltext = "hardin_q0136_07.htm";
            else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("other_soul_other_shape", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "hardin_q0136_08.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 6) {
                st.setMemoState("other_soul_other_shape", String.valueOf(7), true);
                htmltext = "hardin_q0136_12.htm";
            } else if (event.equalsIgnoreCase("reply_5") && GetMemoState == 7)
                htmltext = "hardin_q0136_13.htm";
            else if (event.equalsIgnoreCase("reply_6") && GetMemoState == 7)
                htmltext = "hardin_q0136_15.htm";
            else if (event.equalsIgnoreCase("reply_7") && GetMemoState == 7) {
                st.setCond(6);
                st.setMemoState("other_soul_other_shape", String.valueOf(8), true);
                st.giveItems(q_empty_shape_change_book_order, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "hardin_q0136_16.htm";
            } else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 12) {
                st.setMemoState("other_soul_other_shape", String.valueOf(13), true);
                htmltext = "hardin_q0136_19.htm";
            } else if (event.equalsIgnoreCase("reply_9") && GetMemoState == 13)
                htmltext = "hardin_q0136_21.htm";
            else if (event.equalsIgnoreCase("reply_10") && GetMemoState == 13) {
                st.giveItems(sb_transform_onyx_beast, 1);
                st.giveItems(ADENA_ID, 67550);
                st.removeMemo("other_soul_other_shape");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "hardin_q0136_23.htm";
            }
        } else if (npcId == magister_errickin) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 2) {
                st.setCond(3);
                st.setMemoState("other_soul_other_shape", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_errickin_q0136_03.htm";
            }
        } else if (npcId == magister_clayton) {
            if (event.equalsIgnoreCase("reply_1") && GetMemoState == 8) {
                st.setCond(7);
                st.setMemoState("other_soul_other_shape", String.valueOf(10), true);
                st.takeItems(q_empty_shape_change_book_order, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_clayton_q0136_10.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("other_soul_other_shape");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == hardin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "hardin_q0136_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "hardin_q0136_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == hardin) {
                    if (GetMemoState == 1)
                        htmltext = "hardin_q0136_05.htm";
                    else if (GetMemoState >= 2 && GetMemoState <= 4)
                        htmltext = "hardin_q0136_09.htm";
                    else if (GetMemoState == 5) {
                        if (st.ownItemCount(q_stable_ectoprasm) >= 1) {
                            st.takeItems(q_stable_ectoprasm, -1);
                            st.setMemoState("other_soul_other_shape", String.valueOf(6), true);
                            htmltext = "hardin_q0136_10.htm";
                        } else
                            htmltext = "hardin_q0136_09.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "hardin_q0136_11.htm";
                    else if (GetMemoState == 7)
                        htmltext = "hardin_q0136_14.htm";
                    else if (GetMemoState >= 8 && GetMemoState <= 10)
                        htmltext = "hardin_q0136_17.htm";
                    else if (GetMemoState == 11) {
                        st.takeItems(q_empty_shape_change_book, -1);
                        st.setMemoState("other_soul_other_shape", String.valueOf(12), true);
                        htmltext = "hardin_q0136_18.htm";
                    } else if (GetMemoState == 12) {
                        st.setMemoState("other_soul_other_shape", String.valueOf(13), true);
                        htmltext = "hardin_q0136_20.htm";
                    } else if (GetMemoState == 13)
                        htmltext = "hardin_q0136_22.htm";
                } else if (npcId == magister_errickin) {
                    if (GetMemoState < 2)
                        htmltext = "magister_errickin_q0136_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "magister_errickin_q0136_02.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_ectoprasm) < 35)
                        htmltext = "magister_errickin_q0136_04.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_ectoprasm) >= 35) {
                        st.takeItems(q_ectoprasm, -1);
                        st.setMemoState("other_soul_other_shape", String.valueOf(4), true);
                        htmltext = "magister_errickin_q0136_05.htm";
                    } else if (GetMemoState == 4) {
                        st.setCond(5);
                        st.setMemoState("other_soul_other_shape", String.valueOf(5), true);
                        st.giveItems(q_stable_ectoprasm, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_errickin_q0136_06.htm";
                    } else if (GetMemoState > 4)
                        htmltext = "magister_errickin_q0136_07.htm";
                } else if (npcId == magister_clayton) {
                    if (GetMemoState < 8)
                        htmltext = "magister_clayton_q0136_08.htm";
                    else if (GetMemoState == 8)
                        htmltext = "magister_clayton_q0136_09.htm";
                    else if (GetMemoState == 10 && st.ownItemCount(q_mordeo_crystal) < 5)
                        htmltext = "magister_clayton_q0136_12.htm";
                    else if (GetMemoState == 10 && st.ownItemCount(q_mordeo_crystal) >= 5) {
                        st.setCond(9);
                        st.setMemoState("other_soul_other_shape", String.valueOf(11), true);
                        st.giveItems(q_empty_shape_change_book, 1);
                        st.takeItems(q_mordeo_crystal, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_clayton_q0136_13.htm";
                    } else if (GetMemoState >= 11)
                        htmltext = "magister_clayton_q0136_14.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("other_soul_other_shape");
        int npcId = npc.getNpcId();
        if (npcId == mirrorforest_ghost_b) {
            if (GetMemoState == 3 && st.ownItemCount(q_ectoprasm) < 35) {
                if (st.ownItemCount(q_ectoprasm) < 35) {
                    if (st.ownItemCount(q_ectoprasm) >= 34) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_ectoprasm, 1);
                }
            }
        } else if (npcId == mirrorforest_ghost_c) {
            if (GetMemoState == 3 && st.ownItemCount(q_ectoprasm) < 35) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_ectoprasm) < 35) {
                    if (st.ownItemCount(q_ectoprasm) >= 34) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_ectoprasm, 1);
                    if (i4 < 40 && st.ownItemCount(q_ectoprasm) < 34)
                        st.giveItems(q_ectoprasm, 1);
                }
            }
        } else if (npcId == mirrorforest_ghost_d) {
            if (GetMemoState == 3 && st.ownItemCount(q_ectoprasm) < 35) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_ectoprasm) < 35) {
                    if (st.ownItemCount(q_ectoprasm) >= 34) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_ectoprasm, 1);
                    if (i4 < 90 && st.ownItemCount(q_ectoprasm) < 34)
                        st.giveItems(q_ectoprasm, 1);
                }
            }
        } else if (npcId == mirror) {
            int i4 = Rnd.get(1000);
            if (st.ownItemCount(q_ectoprasm) < 34) {
                if (st.ownItemCount(q_ectoprasm) >= 33) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
                st.giveItems(q_ectoprasm, 2);
                if (i4 < 290 && st.ownItemCount(q_ectoprasm) < 33)
                    st.giveItems(q_ectoprasm, 1);
            } else if (st.ownItemCount(q_ectoprasm) == 34) {
                st.setCond(4);
                st.soundEffect(SOUND_MIDDLE);
                st.giveItems(q_ectoprasm, 1);
            }
        } else if (npcId == glass_jaguar) {
            if (GetMemoState == 10 && st.ownItemCount(q_mordeo_crystal) < 5) {
                int i4 = Rnd.get(1000);
                if (i4 < 986 && st.ownItemCount(q_mordeo_crystal) < 5) {
                    if (st.ownItemCount(q_mordeo_crystal) >= 4) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_mordeo_crystal, 1);
                }
            }
        }
        return null;
    }
}