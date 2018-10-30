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
public class _631_DeliciousTopChoiceMeat extends Quest {
    // npc
    private final static int beast_herder_tunatun = 31537;

    // mobs
    private final static int alpine_kukaburo_4i = 18878;
    private final static int alpine_kukaburo_4a = 18879;
    private final static int alpine_cougar_4i = 18885;
    private final static int alpine_cougar_4a = 18886;
    private final static int alpine_buffalo_4i = 18892;
    private final static int alpine_buffalo_4a = 18893;
    private final static int alpine_grendel_4i = 18899;
    private final static int alpine_grendel_4a = 18900;

    // questitem
    private final static int q_goodmeat_of_beast_h_re = 15534;

    // etcitem
    private final static int rp_icarus_sowsword_i = 10373;
    private final static int rp_icarus_disperser_i = 10374;
    private final static int rp_icarus_spirits_i = 10375;
    private final static int rp_icarus_heavy_arms_i = 10376;
    private final static int rp_icarus_trident_i = 10377;
    private final static int rp_icarus_chopper_i = 10378;
    private final static int rp_icarus_knuckle_i = 10379;
    private final static int rp_icarus_wand_i = 10380;
    private final static int rp_icarus_accipiter_i = 10381;
    private final static int icarus_sowsword_piece = 10397;
    private final static int icarus_disperser_piece = 10398;
    private final static int icarus_spirits_piece = 10399;
    private final static int icarus_heavy_arms_piece = 10400;
    private final static int icarus_trident_piece = 10401;
    private final static int icarus_chopper_piece = 10402;
    private final static int icarus_knuckle_piece = 10403;
    private final static int icarus_wand_piece = 10404;
    private final static int icarus_accipiter_piece = 10405;
    private final static int golden_spice_comp = 15482;
    private final static int crystal_spice_comp = 15483;

    public _631_DeliciousTopChoiceMeat() {
        super(true);
        addStartNpc(beast_herder_tunatun);
        addTalkId(beast_herder_tunatun);
        addKillId(alpine_kukaburo_4i, alpine_kukaburo_4a, alpine_cougar_4i, alpine_cougar_4a, alpine_buffalo_4i, alpine_buffalo_4a, alpine_grendel_4i, alpine_grendel_4a);
        addQuestItem(q_goodmeat_of_beast_h_re);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("good_taste_meat");
        int npcId = npc.getNpcId();

        if (npcId == beast_herder_tunatun)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("good_taste_meat", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "beast_herder_tunatun_q0631_104n.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                if (GetMemoState == 2 && st.ownItemCount(q_goodmeat_of_beast_h_re) >= 120) {
                    int i0 = Rnd.get(10);
                    if (i0 == 0) {
                        int i1 = Rnd.get(9);
                        switch (i1) {
                            case 0: {
                                st.giveItems(rp_icarus_sowsword_i, 1);
                                break;
                            }
                            case 1: {
                                st.giveItems(rp_icarus_disperser_i, 1);
                                break;
                            }
                            case 2: {
                                st.giveItems(rp_icarus_spirits_i, 1);
                                break;
                            }
                            case 3: {
                                st.giveItems(rp_icarus_heavy_arms_i, 1);
                                break;
                            }
                            case 4: {
                                st.giveItems(rp_icarus_trident_i, 1);
                                break;
                            }
                            case 5: {
                                st.giveItems(rp_icarus_chopper_i, 1);
                                break;
                            }
                            case 6: {
                                st.giveItems(rp_icarus_knuckle_i, 1);
                                break;
                            }
                            case 7: {
                                st.giveItems(rp_icarus_wand_i, 1);
                                break;
                            }
                            case 8: {
                                st.giveItems(rp_icarus_accipiter_i, 1);
                                break;
                            }
                        }
                    } else if (i0 == 1) {
                        int i1 = Rnd.get(9);
                        switch (i1) {
                            case 0: {
                                st.giveItems(icarus_sowsword_piece, 1);
                                break;
                            }
                            case 1: {
                                st.giveItems(icarus_disperser_piece, 1);
                                break;
                            }
                            case 2: {
                                st.giveItems(icarus_spirits_piece, 1);
                                break;
                            }
                            case 3: {
                                st.giveItems(icarus_heavy_arms_piece, 1);
                                break;
                            }
                            case 4: {
                                st.giveItems(icarus_trident_piece, 1);
                                break;
                            }
                            case 5: {
                                st.giveItems(icarus_chopper_piece, 1);
                                break;
                            }
                            case 6: {
                                st.giveItems(icarus_knuckle_piece, 1);
                                break;
                            }
                            case 7: {
                                st.giveItems(icarus_wand_piece, 1);
                                break;
                            }
                            case 8: {
                                st.giveItems(icarus_accipiter_piece, 1);
                                break;
                            }
                        }
                    } else if (i0 == 2) {
                        int i1 = Rnd.get(9);
                        switch (i1) {
                            case 0: {
                                st.giveItems(icarus_sowsword_piece, 2);
                                break;
                            }
                            case 1: {
                                st.giveItems(icarus_disperser_piece, 2);
                                break;
                            }
                            case 2: {
                                st.giveItems(icarus_spirits_piece, 2);
                                break;
                            }
                            case 3: {
                                st.giveItems(icarus_heavy_arms_piece, 2);
                                break;
                            }
                            case 4: {
                                st.giveItems(icarus_trident_piece, 2);
                                break;
                            }
                            case 5: {
                                st.giveItems(icarus_chopper_piece, 2);
                                break;
                            }
                            case 6: {
                                st.giveItems(icarus_knuckle_piece, 2);
                                break;
                            }
                            case 7: {
                                st.giveItems(icarus_wand_piece, 2);
                                break;
                            }
                            case 8: {
                                st.giveItems(icarus_accipiter_piece, 2);
                                break;
                            }
                        }
                    } else if (i0 == 3) {
                        int i1 = Rnd.get(9);
                        switch (i1) {
                            case 0: {
                                st.giveItems(icarus_sowsword_piece, 3);
                                break;
                            }
                            case 1: {
                                st.giveItems(icarus_disperser_piece, 3);
                                break;
                            }
                            case 2: {
                                st.giveItems(icarus_spirits_piece, 3);
                                break;
                            }
                            case 3: {
                                st.giveItems(icarus_heavy_arms_piece, 3);
                                break;
                            }
                            case 4: {
                                st.giveItems(icarus_trident_piece, 3);
                                break;
                            }
                            case 5: {
                                st.giveItems(icarus_chopper_piece, 3);
                                break;
                            }
                            case 6: {
                                st.giveItems(icarus_knuckle_piece, 3);
                                break;
                            }
                            case 7: {
                                st.giveItems(icarus_wand_piece, 3);
                                break;
                            }
                            case 8: {
                                st.giveItems(icarus_accipiter_piece, 3);
                                break;
                            }
                        }
                    } else if (i0 == 4) {
                        int i1 = Rnd.get(9);
                        switch (i1) {
                            case 0: {
                                st.giveItems(icarus_sowsword_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 1: {
                                st.giveItems(icarus_disperser_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 2: {
                                st.giveItems(icarus_spirits_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 3: {
                                st.giveItems(icarus_heavy_arms_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 4: {
                                st.giveItems(icarus_trident_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 5: {
                                st.giveItems(icarus_chopper_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 6: {
                                st.giveItems(icarus_knuckle_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 7: {
                                st.giveItems(icarus_wand_piece, Rnd.get(5) + 2);
                                break;
                            }
                            case 8: {
                                st.giveItems(icarus_accipiter_piece, Rnd.get(5) + 2);
                                break;
                            }
                        }
                    } else if (i0 == 5) {
                        int i1 = Rnd.get(9);
                        switch (i1) {
                            case 0: {
                                st.giveItems(icarus_sowsword_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 1: {
                                st.giveItems(icarus_disperser_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 2: {
                                st.giveItems(icarus_spirits_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 3: {
                                st.giveItems(icarus_heavy_arms_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 4: {
                                st.giveItems(icarus_trident_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 5: {
                                st.giveItems(icarus_chopper_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 6: {
                                st.giveItems(icarus_knuckle_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 7: {
                                st.giveItems(icarus_wand_piece, Rnd.get(7) + 2);
                                break;
                            }
                            case 8: {
                                st.giveItems(icarus_accipiter_piece, Rnd.get(7) + 2);
                                break;
                            }
                        }
                    } else if (i0 == 6)
                        st.giveItems(golden_spice_comp, 1);
                    else if (i0 == 7)
                        st.giveItems(golden_spice_comp, 2);
                    else if (i0 == 8)
                        st.giveItems(crystal_spice_comp, 1);
                    else if (i0 == 9)
                        st.giveItems(crystal_spice_comp, 2);
                    st.takeItems(q_goodmeat_of_beast_h_re, -1);
                    st.removeMemo("good_taste_meat");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "beast_herder_tunatun_q0631_202n.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("good_taste_meat");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == beast_herder_tunatun) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "beast_herder_tunatun_q0631_103n.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "beast_herder_tunatun_q0631_101n.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == beast_herder_tunatun)
                    if (GetMemoState == 2 && st.ownItemCount(q_goodmeat_of_beast_h_re) >= 120)
                        htmltext = "beast_herder_tunatun_q0631_105n.htm";
                    else if (st.ownItemCount(q_goodmeat_of_beast_h_re) < 120)
                        htmltext = "beast_herder_tunatun_q0631_106n.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("good_taste_meat");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == alpine_kukaburo_4i || npcId == alpine_cougar_4i) {
                int i0 = Rnd.get(1000);
                if (i0 < 172)
                    if (st.ownItemCount(q_goodmeat_of_beast_h_re) >= 119) {
                        st.setCond(2);
                        st.setMemoState("good_taste_meat", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                    } else {
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == alpine_kukaburo_4a || npcId == alpine_cougar_4a) {
                int i0 = Rnd.get(1000);
                if (i0 < 334)
                    if (st.ownItemCount(q_goodmeat_of_beast_h_re) >= 119) {
                        st.setCond(2);
                        st.setMemoState("good_taste_meat", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                    } else {
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == alpine_buffalo_4i || npcId == alpine_grendel_4i) {
                int i0 = Rnd.get(1000);
                if (i0 < 182)
                    if (st.ownItemCount(q_goodmeat_of_beast_h_re) >= 119) {
                        st.setCond(2);
                        st.setMemoState("good_taste_meat", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                    } else {
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == alpine_buffalo_4a || npcId == alpine_grendel_4a) {
                int i0 = Rnd.get(1000);
                if (i0 < 349)
                    if (st.ownItemCount(q_goodmeat_of_beast_h_re) >= 119) {
                        st.setCond(2);
                        st.setMemoState("good_taste_meat", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                    } else {
                        st.giveItems(q_goodmeat_of_beast_h_re, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}