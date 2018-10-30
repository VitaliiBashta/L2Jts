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
 * @date 22/06/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _309_ForAGoodCause extends Quest {
    // npc
    private static final int pro_agitator = 32647;
    // mobs
    private static final int murcrokian_fanatic = 22650;
    private static final int murcrokian_ascetic = 22651;
    private static final int murcrokian_savior = 22652;
    private static final int murcrokian_foreseer = 22653;
    private static final int murcrokian_corrupted = 22654;
    private static final int murcrokian_awakened = 22655;
    // questitem
    private static final int q309_leather_of_murcrokian1 = 14873;
    private static final int q309_leather_of_murcrokian2 = 14874;
    // etcitem
    private static final int rp_sealed_dynasty_earring_i = 9985;
    private static final int rp_sealed_dynasty_neckalce_i = 9986;
    private static final int rp_sealed_dynasty_ring_i = 9987;
    private static final int rp_sealed_dynasty_sigil_i = 10115;
    private static final int rp_sealed_destino_circlet_i = 15777;
    private static final int rp_sealed_destino_jaket_i = 15780;
    private static final int rp_sealed_destino_hose_i = 15783;
    private static final int rp_sealed_destino_gloves_i = 15786;
    private static final int rp_sealed_destino_shoes_i = 15789;
    private static final int rp_sealed_destino_sigil_i = 15790;
    private static final int rp_sealed_destino_ring_i = 15812;
    private static final int rp_sealed_destino_earring_i = 15813;
    private static final int rp_sealed_destino_necklace_i = 15814;
    private static final int sealed_destino_circlet_piece = 15647;
    private static final int sealed_destino_jaket_piece = 15650;
    private static final int sealed_destino_hose_piece = 15653;
    private static final int sealed_destino_gloves_piece = 15656;
    private static final int sealed_destino_shoes_piece = 15659;
    private static final int sealed_destino_sigil_piece = 15692;
    private static final int sealed_destino_ring_gem = 15772;
    private static final int sealed_destino_earring_gem = 15773;
    private static final int sealed_destino_necklace_gem = 15774;

    public _309_ForAGoodCause() {
        super(false);
        addStartNpc(pro_agitator);
        addQuestItem(q309_leather_of_murcrokian1, q309_leather_of_murcrokian2);
        addKillId(murcrokian_fanatic, murcrokian_ascetic, murcrokian_savior, murcrokian_foreseer, murcrokian_corrupted, murcrokian_awakened);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int talker_level = st.getPlayer().getLevel();
        int GetMemoState = st.getInt("participate_in_the_movement");
        QuestState Wont_you_join_us = st.getPlayer().getQuestState(239);
        int npcId = npc.getNpcId();
        if (npcId == pro_agitator) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("participate_in_the_movement", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pro_agitator_q0309_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=1")) {
                if (talker_level >= 82)
                    htmltext = "pro_agitator_q0309_03.htm";
                else
                    htmltext = "pro_agitator_q0309_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=2"))
                htmltext = "pro_agitator_q0309_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=309&reply=3"))
                htmltext = "pro_agitator_q0309_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=309&reply=4"))
                htmltext = "pro_agitator_q0309_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=309&reply=10")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian1) >= 1) {
                    if (st.getPlayer().getQuestState(239).isCompleted())
                        htmltext = "pro_agitator_q0309_11.htm";
                    else
                        htmltext = "pro_agitator_q0309_12.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=11")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian1) >= 1 && !Wont_you_join_us.isCompleted())
                    htmltext = "pro_agitator_q0309_11a.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=20")) {
                if (GetMemoState == 1)
                    htmltext = "pro_agitator_q0309_13.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=30")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian1) >= 1)
                    htmltext = "pro_agitator_q0309_14.htm";
                else if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) < 1 && st.ownItemCount(q309_leather_of_murcrokian1) < 1) {
                    st.removeMemo("participate_in_the_movement");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "pro_agitator_q0309_15.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=40")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian1) >= 1)
                    htmltext = "pro_agitator_q0309_14.htm";
                else if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) < 1 && st.ownItemCount(q309_leather_of_murcrokian1) < 1) {
                    st.removeMemo("participate_in_the_movement");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "pro_agitator_q0309_15.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=50")) {
                if (GetMemoState == 1) {
                    st.removeMemo("participate_in_the_movement");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "pro_agitator_q0309_16.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=101")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && Wont_you_join_us != null && Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 192) {
                        st.giveItems(rp_sealed_dynasty_earring_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 96)
                            st.takeItems(q309_leather_of_murcrokian2, 96);
                        else {
                            long i0 = 192 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=102")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && Wont_you_join_us != null && Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 256) {
                        st.giveItems(rp_sealed_dynasty_neckalce_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 128)
                            st.takeItems(q309_leather_of_murcrokian2, 128);
                        else {
                            long i0 = 256 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=103")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && Wont_you_join_us != null && Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 128) {
                        st.giveItems(rp_sealed_dynasty_ring_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 64)
                            st.takeItems(q309_leather_of_murcrokian2, 64);
                        else {
                            long i0 = 128 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=104")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && Wont_you_join_us != null && Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 206) {
                        st.giveItems(rp_sealed_dynasty_sigil_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 103)
                            st.takeItems(q309_leather_of_murcrokian2, 103);
                        else {
                            long i0 = 206 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=104")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && Wont_you_join_us != null && Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 180) {
                        int i0 = Rnd.get(9);
                        switch (i0) {
                            case 0:
                                st.giveItems(rp_sealed_destino_circlet_i, 1);
                                break;
                            case 1:
                                st.giveItems(rp_sealed_destino_jaket_i, 1);
                                break;
                            case 2:
                                st.giveItems(rp_sealed_destino_hose_i, 1);
                                break;
                            case 3:
                                st.giveItems(rp_sealed_destino_gloves_i, 1);
                                break;
                            case 4:
                                st.giveItems(rp_sealed_destino_shoes_i, 1);
                                break;
                            case 5:
                                st.giveItems(rp_sealed_destino_sigil_i, 1);
                                break;
                            case 6:
                                st.giveItems(rp_sealed_destino_ring_i, 1);
                                break;
                            case 7:
                                st.giveItems(rp_sealed_destino_earring_i, 1);
                                break;
                            case 8:
                                st.giveItems(rp_sealed_destino_necklace_i, 1);
                                break;
                        }
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 90)
                            st.takeItems(q309_leather_of_murcrokian2, 90);
                        else {
                            long i10 = 180 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i10);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    }
                } else
                    htmltext = "pro_agitator_q0309_21.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=106")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && Wont_you_join_us != null && Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 100) {
                        int i0 = Rnd.get(9);
                        switch (i0) {
                            case 0:
                                st.giveItems(sealed_destino_circlet_piece, 1);
                                break;
                            case 1:
                                st.giveItems(sealed_destino_jaket_piece, 1);
                                break;
                            case 2:
                                st.giveItems(sealed_destino_hose_piece, 1);
                                break;
                            case 3:
                                st.giveItems(sealed_destino_gloves_piece, 1);
                                break;
                            case 4:
                                st.giveItems(sealed_destino_shoes_piece, 1);
                                break;
                            case 5:
                                st.giveItems(sealed_destino_sigil_piece, 1);
                                break;
                            case 6:
                                st.giveItems(sealed_destino_ring_gem, 1);
                                break;
                            case 7:
                                st.giveItems(sealed_destino_earring_gem, 1);
                                break;
                            case 8:
                                st.giveItems(sealed_destino_necklace_gem, 1);
                                break;
                        }
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 50)
                            st.takeItems(q309_leather_of_murcrokian2, 50);
                        else {
                            long i10 = 100 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i10);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=201")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && !Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 230) {
                        st.giveItems(rp_sealed_dynasty_earring_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 115)
                            st.takeItems(q309_leather_of_murcrokian2, 115);
                        else {
                            long i0 = 230 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=202")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && !Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 308) {
                        st.giveItems(rp_sealed_dynasty_neckalce_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 154)
                            st.takeItems(q309_leather_of_murcrokian2, 154);
                        else {
                            long i0 = 308 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=203")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && !Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 154) {
                        st.giveItems(rp_sealed_dynasty_ring_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 77)
                            st.takeItems(q309_leather_of_murcrokian2, 77);
                        else {
                            long i0 = 154 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=204")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && !Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 248) {
                        st.giveItems(rp_sealed_dynasty_sigil_i, 1);
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 124)
                            st.takeItems(q309_leather_of_murcrokian2, 124);
                        else {
                            long i0 = 248 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, -1);
                            st.takeItems(q309_leather_of_murcrokian1, i0);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=205")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && !Wont_you_join_us.isCompleted()) {
                    if (st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) * 2 >= 216) {
                        int i0 = Rnd.get(9);
                        switch (i0) {
                            case 0:
                                st.giveItems(rp_sealed_destino_circlet_i, 1);
                                break;
                            case 1:
                                st.giveItems(rp_sealed_destino_jaket_i, 1);
                                break;
                            case 2:
                                st.giveItems(rp_sealed_destino_hose_i, 1);
                                break;
                            case 3:
                                st.giveItems(rp_sealed_destino_gloves_i, 1);
                                break;
                            case 4:
                                st.giveItems(rp_sealed_destino_shoes_i, 1);
                                break;
                            case 5:
                                st.giveItems(rp_sealed_destino_sigil_i, 1);
                                break;
                            case 6:
                                st.giveItems(rp_sealed_destino_ring_i, 1);
                                break;
                            case 7:
                                st.giveItems(rp_sealed_destino_earring_i, 1);
                                break;
                            case 8:
                                st.giveItems(rp_sealed_destino_necklace_i, 1);
                                break;
                        }
                        if (st.ownItemCount(q309_leather_of_murcrokian2) >= 108)
                            st.takeItems(q309_leather_of_murcrokian2, 108);
                        else {
                            long i10 = 216 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                            st.takeItems(q309_leather_of_murcrokian2, st.ownItemCount(q309_leather_of_murcrokian2));
                            st.takeItems(q309_leather_of_murcrokian1, i10);
                        }
                        htmltext = "pro_agitator_q0309_20.htm";
                    } else
                        htmltext = "pro_agitator_q0309_21.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=309&reply=206")) {
                if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian2) >= 1 && !Wont_you_join_us.isCompleted()) {
                    int i0 = Rnd.get(9);
                    switch (i0) {
                        case 0:
                            st.giveItems(sealed_destino_circlet_piece, 1);
                            break;
                        case 1:
                            st.giveItems(sealed_destino_jaket_piece, 1);
                            break;
                        case 2:
                            st.giveItems(sealed_destino_hose_piece, 1);
                            break;
                        case 3:
                            st.giveItems(sealed_destino_gloves_piece, 1);
                            break;
                        case 4:
                            st.giveItems(sealed_destino_shoes_piece, 1);
                            break;
                        case 5:
                            st.giveItems(sealed_destino_sigil_piece, 1);
                            break;
                        case 6:
                            st.giveItems(sealed_destino_ring_gem, 1);
                            break;
                        case 7:
                            st.giveItems(sealed_destino_earring_gem, 1);
                            break;
                        case 8:
                            st.giveItems(sealed_destino_necklace_gem, 1);
                            break;
                    }
                    if (st.ownItemCount(q309_leather_of_murcrokian2) >= 60)
                        st.takeItems(q309_leather_of_murcrokian2, 60);
                    else {
                        long i10 = 120 - st.ownItemCount(q309_leather_of_murcrokian2) * 2;
                        st.takeItems(q309_leather_of_murcrokian2, st.ownItemCount(q309_leather_of_murcrokian2));
                        st.takeItems(q309_leather_of_murcrokian1, i10);
                    }
                    htmltext = "pro_agitator_q0309_20.htm";
                } else
                    htmltext = "pro_agitator_q0309_21.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("participate_in_the_movement");
        QuestState improving_field_of_reeds = st.getPlayer().getQuestState(308);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pro_agitator) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pro_agitator_q0309_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (improving_field_of_reeds != null && improving_field_of_reeds.isStarted())
                                htmltext = "pro_agitator_q0309_02.htm";
                            else
                                htmltext = "pro_agitator_q0309_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pro_agitator) {
                    if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) < 1 && st.ownItemCount(q309_leather_of_murcrokian1) < 1)
                        htmltext = "pro_agitator_q0309_09.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q309_leather_of_murcrokian1) + st.ownItemCount(q309_leather_of_murcrokian1) >= 1)
                        htmltext = "pro_agitator_q0309_10.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("participate_in_the_movement");
        int npcId = npc.getNpcId();
        if (GetMemoState == 1) {
            if (npcId == murcrokian_fanatic) {
                int i0 = Rnd.get(1000);
                if (i0 < 218) {
                    st.giveItems(q309_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_ascetic) {
                int i0 = Rnd.get(1000);
                if (i0 < 258) {
                    st.giveItems(q309_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_savior) {
                int i0 = Rnd.get(1000);
                if (i0 < 248) {
                    st.giveItems(q309_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_foreseer) {
                int i0 = Rnd.get(1000);
                if (i0 < 290) {
                    st.giveItems(q309_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_corrupted) {
                int i0 = Rnd.get(1000);
                if (i0 < 124) {
                    st.giveItems(q309_leather_of_murcrokian2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_awakened) {
                int i0 = Rnd.get(1000);
                if (i0 < 220) {
                    st.giveItems(q309_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}