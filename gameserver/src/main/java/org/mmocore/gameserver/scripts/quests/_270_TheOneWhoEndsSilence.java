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
 * @version 1.0
 * @date 25/04/2016
 * @lastedit 25/04/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _270_TheOneWhoEndsSilence extends Quest {
    // npc
    private static final int new_falsepriest_gremory = 32757;
    // mobs
    private static final int n_solina_saver = 22790;
    private static final int n_solina_learner = 22791;
    private static final int n_solina_student = 22793;
    private static final int n_divine_judge = 22794;
    private static final int n_divine_wise = 22795;
    private static final int n_divine_manager = 22796;
    private static final int n_divine_worshipper = 22797;
    private static final int n_divine_protector = 22798;
    private static final int n_divine_fighter = 22799;
    private static final int n_divine_magician = 22800;
    // questitem
    private static final int q_tatters_of_monk = 15526;
    // etcitem
    private static final int rp_icarus_sowsword_i = 10373;
    private static final int rp_icarus_disperser_i = 10374;
    private static final int rp_icarus_spirits_i = 10375;
    private static final int rp_icarus_heavy_arms_i = 10376;
    private static final int rp_icarus_trident_i = 10377;
    private static final int rp_icarus_chopper_i = 10378;
    private static final int rp_icarus_knuckle_i = 10379;
    private static final int rp_icarus_wand_i = 10380;
    private static final int rp_icarus_accipiter_i = 10381;
    private static final int icarus_sowsword_piece = 10397;
    private static final int icarus_disperser_piece = 10398;
    private static final int icarus_spirits_piece = 10399;
    private static final int icarus_heavy_arms_piece = 10400;
    private static final int icarus_trident_piece = 10401;
    private static final int icarus_chopper_piece = 10402;
    private static final int icarus_knuckle_piece = 10403;
    private static final int icarus_wand_piece = 10404;
    private static final int icarus_accipiter_piece = 10405;
    private static final int sp_scroll1 = 5593;
    private static final int sp_scroll2 = 5594;
    private static final int sp_scroll3 = 5595;
    private static final int scroll_of_high_sp = 9898;

    public _270_TheOneWhoEndsSilence() {
        super(false);
        addStartNpc(new_falsepriest_gremory);
        addKillId(n_solina_saver, n_solina_learner, n_solina_student, n_divine_judge, n_divine_wise, n_divine_manager, n_divine_worshipper, n_divine_protector, n_divine_fighter, n_divine_magician);
        addLevelCheck(82);
        addQuestCompletedCheck(10288);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("terminator_of_the_silence");
        int npcId = npc.getNpcId();
        if (npcId == new_falsepriest_gremory) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("terminator_of_the_silence", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "new_falsepriest_gremory_q0270_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=1")) {
                htmltext = "new_falsepriest_gremory_q0270_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=2")) {
                if (GetMemoState == 1 && st.ownItemCount(q_tatters_of_monk) < 1) {
                    htmltext = "new_falsepriest_gremory_q0270_06.htm";
                } else if (GetMemoState == 1 && st.ownItemCount(q_tatters_of_monk) < 100) {
                    htmltext = "new_falsepriest_gremory_q0270_07.htm";
                } else if (GetMemoState == 1 && st.ownItemCount(q_tatters_of_monk) >= 100) {
                    htmltext = "new_falsepriest_gremory_q0270_08.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=11")) {
                if (st.ownItemCount(q_tatters_of_monk) >= 100) {

                    int i0 = Rnd.get(10);
                    if (i0 < 5) {
                        int i1 = Rnd.get(1000);
                        if (i1 < 438) {
                            int i2 = Rnd.get(9);
                            if (i2 == 0) {
                                st.giveItems(rp_icarus_sowsword_i, 1);
                            } else if (i2 == 1) {
                                st.giveItems(rp_icarus_disperser_i, 1);
                            } else if (i2 == 2) {
                                st.giveItems(rp_icarus_spirits_i, 1);
                            } else if (i2 == 3) {
                                st.giveItems(rp_icarus_heavy_arms_i, 1);
                            } else if (i2 == 4) {
                                st.giveItems(rp_icarus_trident_i, 1);
                            } else if (i2 == 5) {
                                st.giveItems(rp_icarus_chopper_i, 1);
                            } else if (i2 == 6) {
                                st.giveItems(rp_icarus_knuckle_i, 1);
                            } else if (i2 == 7) {
                                st.giveItems(rp_icarus_wand_i, 1);
                            } else if (i2 == 8) {
                                st.giveItems(rp_icarus_accipiter_i, 1);
                            }
                        } else {
                            int i2 = Rnd.get(9);
                            if (i2 == 0) {
                                st.giveItems(icarus_sowsword_piece, 1);
                            } else if (i2 == 1) {
                                st.giveItems(icarus_disperser_piece, 1);
                            } else if (i2 == 2) {
                                st.giveItems(icarus_spirits_piece, 1);
                            } else if (i2 == 3) {
                                st.giveItems(icarus_heavy_arms_piece, 1);
                            } else if (i2 == 4) {
                                st.giveItems(icarus_trident_piece, 1);
                            } else if (i2 == 5) {
                                st.giveItems(icarus_chopper_piece, 1);
                            } else if (i2 == 6) {
                                st.giveItems(icarus_knuckle_piece, 1);
                            } else if (i2 == 7) {
                                st.giveItems(icarus_wand_piece, 1);
                            } else if (i2 == 8) {
                                st.giveItems(icarus_accipiter_piece, 1);
                            }
                        }
                    } else {
                        int i1 = Rnd.get(100);
                        if (i1 < 1) {
                            st.giveItems(sp_scroll1, 1);
                        } else if (i1 < 28) {
                            st.giveItems(sp_scroll2, 1);
                        } else if (i1 < 61) {
                            st.giveItems(sp_scroll3, 1);
                        } else {
                            st.giveItems(scroll_of_high_sp, 1);
                        }
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    st.takeItems(q_tatters_of_monk, 100);
                    htmltext = "new_falsepriest_gremory_q0270_09.htm";
                } else {
                    htmltext = "new_falsepriest_gremory_q0270_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=12")) {
                if (st.ownItemCount(q_tatters_of_monk) >= 200) {

                    int i1 = Rnd.get(1000);
                    if (i1 < 549) {
                        int i2 = Rnd.get(9);
                        if (i2 == 0) {
                            st.giveItems(rp_icarus_sowsword_i, 1);
                        } else if (i2 == 1) {
                            st.giveItems(rp_icarus_disperser_i, 1);
                        } else if (i2 == 2) {
                            st.giveItems(rp_icarus_spirits_i, 1);
                        } else if (i2 == 3) {
                            st.giveItems(rp_icarus_heavy_arms_i, 1);
                        } else if (i2 == 4) {
                            st.giveItems(rp_icarus_trident_i, 1);
                        } else if (i2 == 5) {
                            st.giveItems(rp_icarus_chopper_i, 1);
                        } else if (i2 == 6) {
                            st.giveItems(rp_icarus_knuckle_i, 1);
                        } else if (i2 == 7) {
                            st.giveItems(rp_icarus_wand_i, 1);
                        } else if (i2 == 8) {
                            st.giveItems(rp_icarus_accipiter_i, 1);
                        }
                    } else {
                        int i2 = Rnd.get(9);
                        if (i2 == 0) {
                            st.giveItems(icarus_sowsword_piece, 1);
                        } else if (i2 == 1) {
                            st.giveItems(icarus_disperser_piece, 1);
                        } else if (i2 == 2) {
                            st.giveItems(icarus_spirits_piece, 1);
                        } else if (i2 == 3) {
                            st.giveItems(icarus_heavy_arms_piece, 1);
                        } else if (i2 == 4) {
                            st.giveItems(icarus_trident_piece, 1);
                        } else if (i2 == 5) {
                            st.giveItems(icarus_chopper_piece, 1);
                        } else if (i2 == 6) {
                            st.giveItems(icarus_knuckle_piece, 1);
                        } else if (i2 == 7) {
                            st.giveItems(icarus_wand_piece, 1);
                        } else if (i2 == 8) {
                            st.giveItems(icarus_accipiter_piece, 1);
                        }
                    }
                    int i11 = Rnd.get(100);
                    if (i11 < 20) {
                        st.giveItems(sp_scroll1, 1);
                    } else if (i11 < 40) {
                        st.giveItems(sp_scroll2, 1);
                    } else if (i11 < 70) {
                        st.giveItems(sp_scroll3, 1);
                    } else {
                        st.giveItems(scroll_of_high_sp, 1);
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    st.takeItems(q_tatters_of_monk, 200);
                    htmltext = "new_falsepriest_gremory_q0270_09.htm";
                } else {
                    htmltext = "new_falsepriest_gremory_q0270_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=13")) {
                if (st.ownItemCount(q_tatters_of_monk) >= 300) {

                    int i0 = Rnd.get(9);
                    if (i0 == 0) {
                        st.giveItems(rp_icarus_sowsword_i, 1);
                    } else if (i0 == 1) {
                        st.giveItems(rp_icarus_disperser_i, 1);
                    } else if (i0 == 2) {
                        st.giveItems(rp_icarus_spirits_i, 1);
                    } else if (i0 == 3) {
                        st.giveItems(rp_icarus_heavy_arms_i, 1);
                    } else if (i0 == 4) {
                        st.giveItems(rp_icarus_trident_i, 1);
                    } else if (i0 == 5) {
                        st.giveItems(rp_icarus_chopper_i, 1);
                    } else if (i0 == 6) {
                        st.giveItems(rp_icarus_knuckle_i, 1);
                    } else if (i0 == 7) {
                        st.giveItems(rp_icarus_wand_i, 1);
                    } else if (i0 == 8) {
                        st.giveItems(rp_icarus_accipiter_i, 1);
                    }
                    int i1 = Rnd.get(9);
                    if (i1 == 0) {
                        st.giveItems(icarus_sowsword_piece, 1);
                    } else if (i1 == 1) {
                        st.giveItems(icarus_disperser_piece, 1);
                    } else if (i1 == 2) {
                        st.giveItems(icarus_spirits_piece, 1);
                    } else if (i1 == 3) {
                        st.giveItems(icarus_heavy_arms_piece, 1);
                    } else if (i1 == 4) {
                        st.giveItems(icarus_trident_piece, 1);
                    } else if (i1 == 5) {
                        st.giveItems(icarus_chopper_piece, 1);
                    } else if (i1 == 6) {
                        st.giveItems(icarus_knuckle_piece, 1);
                    } else if (i1 == 7) {
                        st.giveItems(icarus_wand_piece, 1);
                    } else if (i1 == 8) {
                        st.giveItems(icarus_accipiter_piece, 1);
                    }
                    int i2 = Rnd.get(1000);
                    if (i2 < 242) {
                        st.giveItems(sp_scroll1, 1);
                    } else if (i2 < 486) {
                        st.giveItems(sp_scroll2, 1);
                    } else if (i2 < 742) {
                        st.giveItems(sp_scroll3, 1);
                    } else {
                        st.giveItems(scroll_of_high_sp, 1);
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    st.takeItems(q_tatters_of_monk, 300);
                    htmltext = "new_falsepriest_gremory_q0270_09.htm";
                } else {
                    htmltext = "new_falsepriest_gremory_q0270_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=14")) {
                if (st.ownItemCount(q_tatters_of_monk) >= 400) {

                    int i0 = Rnd.get(9);
                    if (i0 == 0) {
                        st.giveItems(rp_icarus_sowsword_i, 1);
                    } else if (i0 == 1) {
                        st.giveItems(rp_icarus_disperser_i, 1);
                    } else if (i0 == 2) {
                        st.giveItems(rp_icarus_spirits_i, 1);
                    } else if (i0 == 3) {
                        st.giveItems(rp_icarus_heavy_arms_i, 1);
                    } else if (i0 == 4) {
                        st.giveItems(rp_icarus_trident_i, 1);
                    } else if (i0 == 5) {
                        st.giveItems(rp_icarus_chopper_i, 1);
                    } else if (i0 == 6) {
                        st.giveItems(rp_icarus_knuckle_i, 1);
                    } else if (i0 == 7) {
                        st.giveItems(rp_icarus_wand_i, 1);
                    } else if (i0 == 8) {
                        st.giveItems(rp_icarus_accipiter_i, 1);
                    }
                    int i1 = Rnd.get(9);
                    if (i1 == 0) {
                        st.giveItems(icarus_sowsword_piece, 1);
                    } else if (i1 == 1) {
                        st.giveItems(icarus_disperser_piece, 1);
                    } else if (i1 == 2) {
                        st.giveItems(icarus_spirits_piece, 1);
                    } else if (i1 == 3) {
                        st.giveItems(icarus_heavy_arms_piece, 1);
                    } else if (i1 == 4) {
                        st.giveItems(icarus_trident_piece, 1);
                    } else if (i1 == 5) {
                        st.giveItems(icarus_chopper_piece, 1);
                    } else if (i1 == 6) {
                        st.giveItems(icarus_knuckle_piece, 1);
                    } else if (i1 == 7) {
                        st.giveItems(icarus_wand_piece, 1);
                    } else if (i1 == 8) {
                        st.giveItems(icarus_accipiter_piece, 1);
                    }
                    int i2 = Rnd.get(1000);
                    if (i2 < 242) {
                        st.giveItems(sp_scroll1, 1);
                    } else if (i2 < 486) {
                        st.giveItems(sp_scroll2, 1);
                    } else if (i2 < 742) {
                        st.giveItems(sp_scroll3, 1);
                    } else {
                        st.giveItems(scroll_of_high_sp, 1);
                    }
                    int i00 = Rnd.get(10);
                    if (i00 < 5) {
                        int i11 = Rnd.get(1000);
                        if (i11 < 438) {
                            int i22 = Rnd.get(9);
                            if (i22 == 0) {
                                st.giveItems(rp_icarus_sowsword_i, 1);
                            } else if (i22 == 1) {
                                st.giveItems(rp_icarus_disperser_i, 1);
                            } else if (i22 == 2) {
                                st.giveItems(rp_icarus_spirits_i, 1);
                            } else if (i22 == 3) {
                                st.giveItems(rp_icarus_heavy_arms_i, 1);
                            } else if (i22 == 4) {
                                st.giveItems(rp_icarus_trident_i, 1);
                            } else if (i22 == 5) {
                                st.giveItems(rp_icarus_chopper_i, 1);
                            } else if (i22 == 6) {
                                st.giveItems(rp_icarus_knuckle_i, 1);
                            } else if (i22 == 7) {
                                st.giveItems(rp_icarus_wand_i, 1);
                            } else if (i22 == 8) {
                                st.giveItems(rp_icarus_accipiter_i, 1);
                            }
                        } else {
                            int i222 = Rnd.get(9);
                            if (i222 == 0) {
                                st.giveItems(icarus_sowsword_piece, 1);
                            } else if (i222 == 1) {
                                st.giveItems(icarus_disperser_piece, 1);
                            } else if (i222 == 2) {
                                st.giveItems(icarus_spirits_piece, 1);
                            } else if (i222 == 3) {
                                st.giveItems(icarus_heavy_arms_piece, 1);
                            } else if (i222 == 4) {
                                st.giveItems(icarus_trident_piece, 1);
                            } else if (i222 == 5) {
                                st.giveItems(icarus_chopper_piece, 1);
                            } else if (i222 == 6) {
                                st.giveItems(icarus_knuckle_piece, 1);
                            } else if (i222 == 7) {
                                st.giveItems(icarus_wand_piece, 1);
                            } else if (i222 == 8) {
                                st.giveItems(icarus_accipiter_piece, 1);
                            }
                        }
                    } else {
                        int i111 = Rnd.get(100);
                        if (i111 < 1) {
                            st.giveItems(sp_scroll1, 1);
                        } else if (i111 < 28) {
                            st.giveItems(sp_scroll2, 1);
                        } else if (i111 < 61) {
                            st.giveItems(sp_scroll3, 1);
                        } else {
                            st.giveItems(scroll_of_high_sp, 1);
                        }
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    st.takeItems(q_tatters_of_monk, 400);
                    htmltext = "new_falsepriest_gremory_q0270_09.htm";
                } else {
                    htmltext = "new_falsepriest_gremory_q0270_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=15")) {
                if (st.ownItemCount(q_tatters_of_monk) >= 500) {

                    int i0 = Rnd.get(9);
                    if (i0 == 0) {
                        st.giveItems(rp_icarus_sowsword_i, 1);
                    } else if (i0 == 1) {
                        st.giveItems(rp_icarus_disperser_i, 1);
                    } else if (i0 == 2) {
                        st.giveItems(rp_icarus_spirits_i, 1);
                    } else if (i0 == 3) {
                        st.giveItems(rp_icarus_heavy_arms_i, 1);
                    } else if (i0 == 4) {
                        st.giveItems(rp_icarus_trident_i, 1);
                    } else if (i0 == 5) {
                        st.giveItems(rp_icarus_chopper_i, 1);
                    } else if (i0 == 6) {
                        st.giveItems(rp_icarus_knuckle_i, 1);
                    } else if (i0 == 7) {
                        st.giveItems(rp_icarus_wand_i, 1);
                    } else if (i0 == 8) {
                        st.giveItems(rp_icarus_accipiter_i, 1);
                    }
                    int i1 = Rnd.get(9);
                    if (i1 == 0) {
                        st.giveItems(icarus_sowsword_piece, 1);
                    } else if (i1 == 1) {
                        st.giveItems(icarus_disperser_piece, 1);
                    } else if (i1 == 2) {
                        st.giveItems(icarus_spirits_piece, 1);
                    } else if (i1 == 3) {
                        st.giveItems(icarus_heavy_arms_piece, 1);
                    } else if (i1 == 4) {
                        st.giveItems(icarus_trident_piece, 1);
                    } else if (i1 == 5) {
                        st.giveItems(icarus_chopper_piece, 1);
                    } else if (i1 == 6) {
                        st.giveItems(icarus_knuckle_piece, 1);
                    } else if (i1 == 7) {
                        st.giveItems(icarus_wand_piece, 1);
                    } else if (i1 == 8) {
                        st.giveItems(icarus_accipiter_piece, 1);
                    }
                    int i2 = Rnd.get(1000);
                    if (i2 < 242) {
                        st.giveItems(sp_scroll1, 1);
                    } else if (i2 < 486) {
                        st.giveItems(sp_scroll2, 1);
                    } else if (i2 < 742) {
                        st.giveItems(sp_scroll3, 1);
                    } else {
                        st.giveItems(scroll_of_high_sp, 1);
                    }
                    int i11 = Rnd.get(1000);
                    if (i11 < 549) {
                        int i22 = Rnd.get(9);
                        if (i22 == 0) {
                            st.giveItems(rp_icarus_sowsword_i, 1);
                        } else if (i22 == 1) {
                            st.giveItems(rp_icarus_disperser_i, 1);
                        } else if (i22 == 2) {
                            st.giveItems(rp_icarus_spirits_i, 1);
                        } else if (i22 == 3) {
                            st.giveItems(rp_icarus_heavy_arms_i, 1);
                        } else if (i22 == 4) {
                            st.giveItems(rp_icarus_trident_i, 1);
                        } else if (i22 == 5) {
                            st.giveItems(rp_icarus_chopper_i, 1);
                        } else if (i22 == 6) {
                            st.giveItems(rp_icarus_knuckle_i, 1);
                        } else if (i22 == 7) {
                            st.giveItems(rp_icarus_wand_i, 1);
                        } else if (i22 == 8) {
                            st.giveItems(rp_icarus_accipiter_i, 1);
                        }
                    } else {
                        int i222 = Rnd.get(9);
                        if (i222 == 0) {
                            st.giveItems(icarus_sowsword_piece, 1);
                        } else if (i222 == 1) {
                            st.giveItems(icarus_disperser_piece, 1);
                        } else if (i222 == 2) {
                            st.giveItems(icarus_spirits_piece, 1);
                        } else if (i222 == 3) {
                            st.giveItems(icarus_heavy_arms_piece, 1);
                        } else if (i222 == 4) {
                            st.giveItems(icarus_trident_piece, 1);
                        } else if (i222 == 5) {
                            st.giveItems(icarus_chopper_piece, 1);
                        } else if (i222 == 6) {
                            st.giveItems(icarus_knuckle_piece, 1);
                        } else if (i222 == 7) {
                            st.giveItems(icarus_wand_piece, 1);
                        } else if (i222 == 8) {
                            st.giveItems(icarus_accipiter_piece, 1);
                        }
                    }
                    int i111 = Rnd.get(100);
                    if (i111 < 20) {
                        st.giveItems(sp_scroll1, 1);
                    } else if (i111 < 40) {
                        st.giveItems(sp_scroll2, 1);
                    } else if (i111 < 70) {
                        st.giveItems(sp_scroll3, 1);
                    } else {
                        st.giveItems(scroll_of_high_sp, 1);
                    }
                    st.soundEffect(SOUND_MIDDLE);
                    st.takeItems(q_tatters_of_monk, 500);
                    htmltext = "new_falsepriest_gremory_q0270_09.htm";
                } else {
                    htmltext = "new_falsepriest_gremory_q0270_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=6")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_tatters_of_monk) >= 1) {
                        htmltext = "new_falsepriest_gremory_q0270_12.htm";
                    } else {
                        st.takeItems(q_tatters_of_monk, -1);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "new_falsepriest_gremory_q0270_13.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=270&reply=7")) {
                if (GetMemoState == 1) {
                    st.takeItems(q_tatters_of_monk, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "new_falsepriest_gremory_q0270_13.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("terminator_of_the_silence");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == new_falsepriest_gremory) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "new_falsepriest_gremory_q0270_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "new_falsepriest_gremory_q0270_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == new_falsepriest_gremory) {
                    if (GetMemoState == 1) {
                        htmltext = "new_falsepriest_gremory_q0270_05.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("terminator_of_the_silence");
        int npcId = npc.getNpcId();
        if (npcId == n_solina_saver) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 57) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_solina_learner) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 55) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_solina_student) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 59) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_judge) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 698) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_wise) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 735) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_manager) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 903) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_worshipper) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 811) {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_protector) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 884) {
                    st.giveItems(q_tatters_of_monk, 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_fighter) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 893) {
                    st.giveItems(q_tatters_of_monk, 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == n_divine_magician) {
            if (GetMemoState == 1) {
                int i0 = Rnd.get(1000);
                if (i0 < 953) {
                    st.giveItems(q_tatters_of_monk, 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_tatters_of_monk, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}