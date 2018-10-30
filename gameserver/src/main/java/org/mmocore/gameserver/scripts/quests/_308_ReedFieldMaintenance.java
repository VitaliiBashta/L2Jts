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
public class _308_ReedFieldMaintenance extends Quest {
    // npc
    private static final int contract_worker = 32646;

    // mobs
    private static final int murcrokian_fanatic = 22650;
    private static final int murcrokian_ascetic = 22651;
    private static final int murcrokian_savior = 22652;
    private static final int murcrokian_foreseer = 22653;
    private static final int murcrokian_corrupted = 22654;
    private static final int murcrokian_awakened = 22655;

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

    // questitem
    private static final int q308_leather_of_murcrokian1 = 14871;
    private static final int q308_leather_of_murcrokian2 = 14872;

    public _308_ReedFieldMaintenance() {
        super(false);
        addStartNpc(contract_worker);
        addQuestItem(q308_leather_of_murcrokian1, q308_leather_of_murcrokian2);
        addKillId(murcrokian_fanatic, murcrokian_ascetic, murcrokian_savior, murcrokian_foreseer, murcrokian_corrupted, murcrokian_awakened);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        QuestState qs_238 = st.getPlayer().getQuestState(238);
        int GetMemoState = st.getInt("improving_field_of_reeds");
        int npcId = npc.getNpcId();

        if (npcId == contract_worker)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("improving_field_of_reeds", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "contract_worker_q0308_08.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "contract_worker_q0308_03.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "contract_worker_q0308_06.htm";
            else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) >= 1)
                    if (qs_238 != null && qs_238.isCompleted())
                        htmltext = "contract_worker_q0308_11.htm";
                    else
                        htmltext = "contract_worker_q0308_12.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) >= 1)
                    htmltext = "contract_worker_q0308_11a.htm";
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (GetMemoState == 1)
                    htmltext = "contract_worker_q0308_13.htm";
            } else if (event.equalsIgnoreCase("reply_30")) {
                if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) >= 1)
                    htmltext = "contract_worker_q0308_14.htm";
                else if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) < 1 && st.ownItemCount(q308_leather_of_murcrokian2) < 1) {
                    st.removeMemo("improving_field_of_reeds");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "contract_worker_q0308_15.htm";
                }
            } else if (event.equalsIgnoreCase("reply_40")) {
                if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) >= 1)
                    htmltext = "contract_worker_q0308_14.htm";
                else if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) < 1 && st.ownItemCount(q308_leather_of_murcrokian2) < 1) {
                    st.removeMemo("improving_field_of_reeds");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "contract_worker_q0308_15.htm";
                }
            } else if (event.equalsIgnoreCase("reply_50")) {
                if (GetMemoState == 1) {
                    st.removeMemo("improving_field_of_reeds");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "contract_worker_q0308_16.htm";
                }
            } else if (event.equalsIgnoreCase("reply_101")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 192) {
                    st.giveItems(rp_sealed_dynasty_earring_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 96)
                        st.takeItems(q308_leather_of_murcrokian2, 96);
                    else {
                        long i0 = 192 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_102")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 256) {
                    st.giveItems(rp_sealed_dynasty_neckalce_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 128)
                        st.takeItems(q308_leather_of_murcrokian2, 128);
                    else {
                        long i0 = 256 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_103")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 128) {

                    st.giveItems(rp_sealed_dynasty_ring_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 64)
                        st.takeItems(q308_leather_of_murcrokian2, 64);
                    else {
                        long i0 = 128 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, st.ownItemCount(q308_leather_of_murcrokian2));
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_104")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 206) {
                    st.giveItems(rp_sealed_dynasty_sigil_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 103)
                        st.takeItems(q308_leather_of_murcrokian2, 103);
                    else {
                        long i0 = 206 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_105")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 180) {
                    int i0 = Rnd.get(9);
                    switch (i0) {
                        case 0: {
                            st.giveItems(rp_sealed_destino_circlet_i, 1);
                            break;
                        }
                        case 1: {
                            st.giveItems(rp_sealed_destino_jaket_i, 1);
                            break;
                        }
                        case 2: {
                            st.giveItems(rp_sealed_destino_hose_i, 1);
                            break;
                        }
                        case 3: {
                            st.giveItems(rp_sealed_destino_gloves_i, 1);
                            break;
                        }
                        case 4: {
                            st.giveItems(rp_sealed_destino_shoes_i, 1);
                            break;
                        }
                        case 5: {
                            st.giveItems(rp_sealed_destino_sigil_i, 1);
                            break;
                        }
                        case 6: {
                            st.giveItems(rp_sealed_destino_ring_i, 1);
                            break;
                        }
                        case 7: {
                            st.giveItems(rp_sealed_destino_earring_i, 1);
                            break;
                        }
                        case 8: {
                            st.giveItems(rp_sealed_destino_necklace_i, 1);
                            break;
                        }
                    }
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 90)
                        st.takeItems(q308_leather_of_murcrokian2, 90);
                    else {
                        long c0 = 180 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, c0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_106")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 100) {
                    for (int i1 = 0; i1 < 5; i1 = i1 + 1) {
                        int i0 = Rnd.get(9);
                        switch (i0) {
                            case 0: {
                                st.giveItems(sealed_destino_circlet_piece, 1);
                                break;
                            }
                            case 1: {
                                st.giveItems(sealed_destino_jaket_piece, 1);
                                break;
                            }
                            case 2: {
                                st.giveItems(sealed_destino_hose_piece, 1);
                                break;
                            }
                            case 3: {
                                st.giveItems(sealed_destino_gloves_piece, 1);
                                break;
                            }
                            case 4: {
                                st.giveItems(sealed_destino_shoes_piece, 1);
                                break;
                            }
                            case 5: {
                                st.giveItems(sealed_destino_sigil_piece, 1);
                                break;
                            }
                            case 6: {
                                st.giveItems(sealed_destino_ring_gem, 1);
                                break;
                            }
                            case 7: {
                                st.giveItems(sealed_destino_earring_gem, 1);
                                break;
                            }
                            case 8: {
                                st.giveItems(sealed_destino_necklace_gem, 1);
                                break;
                            }
                        }
                    }
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 50)
                        st.takeItems(q308_leather_of_murcrokian2, 50);
                    else {
                        long i0 = 100 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_201")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 230) {
                    st.giveItems(rp_sealed_dynasty_earring_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 105)
                        st.takeItems(q308_leather_of_murcrokian2, 105);
                    else {
                        long i0 = 230 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_202")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 308) {
                    st.giveItems(rp_sealed_dynasty_neckalce_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 154)
                        st.takeItems(q308_leather_of_murcrokian2, 154);
                    else {
                        long i0 = 308 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_203")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 154) {
                    st.giveItems(rp_sealed_dynasty_ring_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 77)
                        st.takeItems(q308_leather_of_murcrokian2, 77);
                    else {
                        long i0 = 154 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_204")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 248) {
                    st.giveItems(rp_sealed_dynasty_sigil_i, 1);
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 124)
                        st.takeItems(q308_leather_of_murcrokian2, 124);
                    else {
                        long i0 = 248 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, st.ownItemCount(q308_leather_of_murcrokian2));
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_205")) {
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 216) {
                    int i0 = Rnd.get(9);
                    switch (i0) {
                        case 0: {
                            st.giveItems(rp_sealed_destino_circlet_i, 1);
                            break;
                        }
                        case 1: {
                            st.giveItems(rp_sealed_destino_jaket_i, 1);
                            break;
                        }
                        case 2: {
                            st.giveItems(rp_sealed_destino_hose_i, 1);
                            break;
                        }
                        case 3: {
                            st.giveItems(rp_sealed_destino_gloves_i, 1);
                            break;
                        }
                        case 4: {
                            st.giveItems(rp_sealed_destino_shoes_i, 1);
                            break;
                        }
                        case 5: {
                            st.giveItems(rp_sealed_destino_sigil_i, 1);
                            break;
                        }
                        case 6: {
                            st.giveItems(rp_sealed_destino_ring_i, 1);
                            break;
                        }
                        case 7: {
                            st.giveItems(rp_sealed_destino_earring_i, 1);
                            break;
                        }
                        case 8: {
                            st.giveItems(rp_sealed_destino_necklace_i, 1);
                            break;
                        }
                    }
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 108)
                        st.takeItems(q308_leather_of_murcrokian2, 108);
                    else {
                        long c0 = 216 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, -1);
                        st.takeItems(q308_leather_of_murcrokian1, c0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
            } else if (event.equalsIgnoreCase("reply_206"))
                if (st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) * 2 >= 120) {
                    for (int i1 = 0; i1 < 5; i1 = i1 + 1) {
                        int i0 = Rnd.get(9);
                        switch (i0) {
                            case 0: {
                                st.giveItems(sealed_destino_circlet_piece, 1);
                                break;
                            }
                            case 1: {
                                st.giveItems(sealed_destino_jaket_piece, 1);
                                break;
                            }
                            case 2: {
                                st.giveItems(sealed_destino_hose_piece, 1);
                                break;
                            }
                            case 3: {
                                st.giveItems(sealed_destino_gloves_piece, 1);
                                break;
                            }
                            case 4: {
                                st.giveItems(sealed_destino_shoes_piece, 1);
                                break;
                            }
                            case 5: {
                                st.giveItems(sealed_destino_sigil_piece, 1);
                                break;
                            }
                            case 6: {
                                st.giveItems(sealed_destino_ring_gem, 1);
                                break;
                            }
                            case 7: {
                                st.giveItems(sealed_destino_earring_gem, 1);
                                break;
                            }
                            case 8: {
                                st.giveItems(sealed_destino_necklace_gem, 1);
                                break;
                            }
                        }
                    }
                    if (st.ownItemCount(q308_leather_of_murcrokian2) >= 60)
                        st.takeItems(q308_leather_of_murcrokian2, 60);
                    else {
                        long i0 = 120 - st.ownItemCount(q308_leather_of_murcrokian2) * 2;
                        st.takeItems(q308_leather_of_murcrokian2, st.ownItemCount(q308_leather_of_murcrokian2));
                        st.takeItems(q308_leather_of_murcrokian1, i0);
                    }
                    htmltext = "contract_worker_q0308_20.htm";
                } else
                    htmltext = "contract_worker_q0308_21.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;

        int GetMemoState = st.getInt("improving_field_of_reeds");
        int npcId = npc.getNpcId();
        int id = st.getState();
        QuestState qs_309 = st.getPlayer().getQuestState(309);
        switch (id) {
            case CREATED:
                if (npcId == contract_worker) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "contract_worker_q0308_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (qs_309 != null && qs_309.isStarted()) {
                                st.exitQuest(true);
                                htmltext = "contract_worker_q0308_02.htm";
                            } else
                                htmltext = "contract_worker_q0308_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == contract_worker)
                    if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) < 1 && st.ownItemCount(q308_leather_of_murcrokian2) < 1)
                        htmltext = "contract_worker_q0308_09.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q308_leather_of_murcrokian1) + st.ownItemCount(q308_leather_of_murcrokian2) >= 1)
                        htmltext = "contract_worker_q0308_10.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("improving_field_of_reeds");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == murcrokian_fanatic) {
                int i0 = Rnd.get(1000);
                if (i0 < 218) {
                    st.giveItems(q308_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_ascetic) {
                int i0 = Rnd.get(1000);
                if (i0 < 258) {
                    st.giveItems(q308_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_savior) {
                int i0 = Rnd.get(1000);
                if (i0 < 248) {
                    st.giveItems(q308_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_foreseer) {
                int i0 = Rnd.get(1000);
                if (i0 < 290) {
                    st.giveItems(q308_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_corrupted) {
                int i0 = Rnd.get(1000);
                if (i0 < 220) {
                    st.giveItems(q308_leather_of_murcrokian1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == murcrokian_awakened) {
                int i0 = Rnd.get(1000);
                if (i0 < 124) {
                    st.giveItems(q308_leather_of_murcrokian2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        return null;
    }
}