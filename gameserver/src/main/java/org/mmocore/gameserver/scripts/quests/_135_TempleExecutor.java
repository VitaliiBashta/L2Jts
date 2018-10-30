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
 * @version 1.1
 * @date 10/02/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _135_TempleExecutor extends Quest {
    // npc
    private final static int shegfield = 30068;
    private final static int pano = 30078;
    private final static int alankell = 30291;
    private final static int warehouse_keeper_sonin = 31773;
    // mobs
    private final static int delu_lizardman_shaman = 20781;
    private final static int delu_lizardman_q_master = 21104;
    private final static int delu_lizardman_agent = 21105;
    private final static int cursed_observer = 21106;
    private final static int delu_lizardman_commander = 21107;
    // questitem
    private final static int q_robbed_goods = 10328;
    private final static int q_crystal_of_hate = 10329;
    private final static int q_old_tresure_map = 10330;
    private final static int q_recommand_sonin = 10331;
    private final static int q_recommand_pano = 10332;
    private final static int q_recommand_alankel = 10333;
    private final static int q_bedge_hand_of_order = 10334;

    public _135_TempleExecutor() {
        super(false);
        addStartNpc(shegfield);
        addTalkId(alankell, warehouse_keeper_sonin, pano);
        addKillId(delu_lizardman_shaman, delu_lizardman_q_master, delu_lizardman_agent, cursed_observer, delu_lizardman_commander);
        addQuestItem(q_robbed_goods, q_crystal_of_hate, q_old_tresure_map, q_recommand_sonin, q_recommand_pano, q_recommand_alankel);
        addLevelCheck(35);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("hand_of_order");
        int npcId = npc.getNpcId();
        if (npcId == shegfield) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("hand_of_order", String.valueOf(1), true);
                st.setMemoState("hand_of_order_ex", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "shegfield_q0135_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("hand_of_order", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "shegfield_q0135_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 7)
                    htmltext = "shegfield_q0135_10.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 7)
                    htmltext = "shegfield_q0135_11.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 7)
                    htmltext = "shegfield_q0135_12.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                if (GetMemoState == 7) {
                    st.giveItems(q_bedge_hand_of_order, 1);
                    if (st.getPlayer().getLevel() < 41)
                        st.addExpAndSp(30000, 2000);
                    st.giveItems(ADENA_ID, 10000 + 3234 + 3690);
                    st.removeMemo("hand_of_order");
                    st.removeMemo("hand_of_order_ex");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "shegfield_q0135_13.htm";
                }
        } else if (npcId == alankell)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3)
                    htmltext = "alankell_q0135_02a.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 3)
                    htmltext = "alankell_q0135_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 3)
                    htmltext = "alankell_q0135_05.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 3)
                    htmltext = "alankell_q0135_06.htm";
            } else if (event.equalsIgnoreCase("reply_5"))
                if (GetMemoState == 3) {
                    st.setCond(3);
                    st.setMemoState("hand_of_order", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "alankell_q0135_07.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("hand_of_order");
        int GetMemoStateEx = st.getInt("hand_of_order_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == shegfield) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "shegfield_q0135_02.htm";
                            break;
                        default:
                            htmltext = "shegfield_q0135_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == shegfield) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("hand_of_order", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "shegfield_q0135_04.htm";
                    } else if (GetMemoState >= 2 && GetMemoState < 5 && (st.ownItemCount(q_recommand_sonin) < 1 || st.ownItemCount(q_recommand_pano) < 1 || st.ownItemCount(q_recommand_alankel) < 1))
                        htmltext = "shegfield_q0135_06.htm";
                    else if (GetMemoState >= 5 && (st.ownItemCount(q_recommand_sonin) < 1 || st.ownItemCount(q_recommand_pano) < 1 || st.ownItemCount(q_recommand_alankel) < 1))
                        htmltext = "shegfield_q0135_07.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_recommand_sonin) >= 1 && st.ownItemCount(q_recommand_pano) >= 1 && st.ownItemCount(q_recommand_alankel) >= 1) {
                        st.takeItems(q_recommand_sonin, 1);
                        st.takeItems(q_recommand_pano, 1);
                        st.takeItems(q_recommand_alankel, 1);
                        st.setMemoState("hand_of_order", String.valueOf(7), true);
                        htmltext = "shegfield_q0135_08.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "shegfield_q0135_09.htm";
                } else if (npcId == alankell) {
                    if (GetMemoState < 2)
                        htmltext = "alankell_q0135_01.htm";
                    else if (GetMemoState == 2) {
                        st.setMemoState("hand_of_order", String.valueOf(3), true);
                        htmltext = "alankell_q0135_02.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "alankell_q0135_03.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_old_tresure_map) < 10)
                        htmltext = "alankell_q0135_08.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_old_tresure_map) >= 10 && (st.ownItemCount(q_recommand_sonin) < 1 || st.ownItemCount(q_recommand_pano) < 1))
                        htmltext = "alankell_q0135_09.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_old_tresure_map) >= 10 && st.ownItemCount(q_recommand_sonin) >= 1 && st.ownItemCount(q_recommand_pano) >= 1 && st.ownItemCount(q_recommand_alankel) < 1) {
                        st.setCond(5);
                        st.setMemoState("hand_of_order", String.valueOf(6), true);
                        st.giveItems(q_recommand_alankel, 1);
                        st.takeItems(q_old_tresure_map, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "alankell_q0135_10.htm";
                    } else if (GetMemoState >= 6) {
                        if (st.ownItemCount(q_recommand_alankel) == 0) {
                            st.giveItems(q_recommand_alankel, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "alankell_q0135_11.htm";
                    }
                } else if (npcId == warehouse_keeper_sonin) {
                    if (GetMemoState < 2)
                        htmltext = "warehouse_keeper_sonin_q0135_01.htm";
                    else if (GetMemoState == 2 || GetMemoState == 3)
                        htmltext = "warehouse_keeper_sonin_q0135_02.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_robbed_goods) < 10 && st.ownItemCount(q_recommand_sonin) < 1)
                        htmltext = "warehouse_keeper_sonin_q0135_02a.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_robbed_goods) >= 10 && st.ownItemCount(q_recommand_sonin) < 1 && GetMemoStateEx / 10 == 0) {
                        st.giveItems(q_recommand_sonin, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        st.setMemoState("hand_of_order_ex", String.valueOf(GetMemoStateEx + 10), true);
                        st.takeItems(q_robbed_goods, -1);
                        htmltext = "warehouse_keeper_sonin_q0135_03.htm";
                    } else if (GetMemoState >= 5 && GetMemoStateEx / 10 >= 1) {
                        if (st.ownItemCount(q_recommand_sonin) == 0) {
                            st.giveItems(q_recommand_sonin, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "warehouse_keeper_sonin_q0135_04.htm";
                    }
                } else if (npcId == pano)
                    if (GetMemoState < 2)
                        htmltext = "pano_q0135_01.htm";
                    else if (GetMemoState == 2 || GetMemoState == 3)
                        htmltext = "pano_q0135_02.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_crystal_of_hate) < 10 && st.ownItemCount(q_recommand_pano) < 1)
                        htmltext = "pano_q0135_02a.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_crystal_of_hate) >= 10 && st.ownItemCount(q_recommand_pano) < 1 && GetMemoStateEx % 10 == 0) {
                        st.giveItems(q_recommand_pano, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        st.setMemoState("hand_of_order_ex", String.valueOf(GetMemoStateEx + 1), true);
                        st.takeItems(q_crystal_of_hate, -1);
                        htmltext = "pano_q0135_03.htm";
                    } else if (GetMemoState >= 5 && GetMemoStateEx % 10 >= 1) {
                        if (st.ownItemCount(q_recommand_pano) == 0) {
                            st.giveItems(q_recommand_pano, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        htmltext = "pano_q0135_04.htm";
                    }
                break;
        }
        return htmltext;
    }

    public void checkItems(QuestState st) {
        if (st.ownItemCount(q_robbed_goods) + st.ownItemCount(q_crystal_of_hate) + st.ownItemCount(q_old_tresure_map) == 30) {
            st.setCond(4);
            st.setMemoState("hand_of_order", String.valueOf(5), true);
            st.soundEffect(SOUND_MIDDLE);
        }
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("hand_of_order");
        int npcId = npc.getNpcId();
        if (GetMemoState == 4) {
            if (npcId == delu_lizardman_shaman || npcId == delu_lizardman_q_master) {
                int i4 = Rnd.get(1000);
                if (i4 < 439) {
                    if (st.ownItemCount(q_robbed_goods) < 9) {
                        st.giveItems(q_robbed_goods, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_robbed_goods) == 9) {
                        st.giveItems(q_robbed_goods, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) < 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) == 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) < 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) == 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            } else if (npcId == delu_lizardman_agent) {
                int i4 = Rnd.get(1000);
                if (i4 < 504) {
                    if (st.ownItemCount(q_robbed_goods) < 9) {
                        st.giveItems(q_robbed_goods, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_robbed_goods) == 9) {
                        st.giveItems(q_robbed_goods, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) < 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) == 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) < 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) == 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            } else if (npcId == cursed_observer) {
                int i4 = Rnd.get(1000);
                if (i4 < 423) {
                    if (st.ownItemCount(q_robbed_goods) < 9) {
                        st.giveItems(q_robbed_goods, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_robbed_goods) == 9) {
                        st.giveItems(q_robbed_goods, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) < 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) == 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) < 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) == 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            } else if (npcId == delu_lizardman_commander) {
                int i4 = Rnd.get(1000);
                if (i4 < 902) {
                    if (st.ownItemCount(q_robbed_goods) < 9) {
                        st.giveItems(q_robbed_goods, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_robbed_goods) == 9) {
                        st.giveItems(q_robbed_goods, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) < 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_crystal_of_hate) == 9) {
                        st.giveItems(q_crystal_of_hate, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) < 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_old_tresure_map) == 9) {
                        st.giveItems(q_old_tresure_map, 1);
                        checkItems(st);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}