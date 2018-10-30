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
 * @date 11/02/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _111_ElrokianHuntersProof extends Quest {
    // npc
    private static final int marquez = 32113;
    private static final int mushika = 32114;
    private static final int asama = 32115;
    private static final int kirikachin = 32116;
    // mobs
    private static final int velociraptor_leader = 22196;
    private static final int velociraptor = 22197;
    private static final int velociraptor_s = 22198;
    private static final int ornithomimus_leader = 22200;
    private static final int ornithomimus = 22201;
    private static final int ornithomimus_s = 22202;
    private static final int deinonychus_leader = 22203;
    private static final int deinonychus = 22204;
    private static final int deinonychus_s = 22205;
    private static final int pachycephalosaurus_ldr = 22208;
    private static final int pachycephalosaurus = 22209;
    private static final int pachycephalosaurus_s = 22210;
    private static final int velociraptor_n = 22218;
    private static final int ornithomimus_n = 22219;
    private static final int deinonychus_n = 22220;
    private static final int pachycephalosaurus_n = 22221;
    private static final int velociraptor_leader2 = 22223;
    private static final int ornithomimus_leader2 = 22224;
    private static final int deinonychus_leader2 = 22225;
    private static final int pachycephalosaurus_ldr2 = 22226;
    // questitem
    private static final int q_dyno_dairy = 8768;
    private static final int q_letter_to_kirikachin = 8769;
    private static final int q_capture_claw = 8770;
    private static final int q_capture_bone = 8771;
    private static final int q_capture_skin = 8772;
    private static final int q_capture_train_weapon = 8773;
    // etcitem
    private static final int capture_of_elcrok = 8763;
    private static final int capture_stone = 8764;

    public _111_ElrokianHuntersProof() {
        super(true);
        addStartNpc(marquez);
        addTalkId(mushika, asama, kirikachin);
        addKillId(velociraptor_leader, velociraptor, velociraptor_s, ornithomimus_leader, ornithomimus, ornithomimus_s, deinonychus_leader, deinonychus, deinonychus_s, pachycephalosaurus_ldr, pachycephalosaurus, pachycephalosaurus_s, velociraptor_n, ornithomimus_n, deinonychus_n, pachycephalosaurus_n, velociraptor_leader2, ornithomimus_leader2, deinonychus_leader2, pachycephalosaurus_ldr2);
        addQuestItem(q_dyno_dairy, q_capture_claw, q_capture_bone, q_capture_skin, q_letter_to_kirikachin, q_capture_train_weapon);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_proop_of_elcroky_hunter");
        int npcId = npc.getNpcId();
        if (npcId == marquez) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "marquez_q0111_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=111&reply=101"))
                htmltext = "marquez_q0111_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=102"))
                htmltext = "marquez_q0111_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=103"))
                htmltext = "marquez_q0111_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=104"))
                htmltext = "marquez_q0111_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=105"))
                htmltext = "marquez_q0111_10.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=106"))
                htmltext = "marquez_q0111_11.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=107"))
                htmltext = "marquez_q0111_12.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=108")) {
                if (GetMemoState == 3) {
                    st.setCond(4);
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "marquez_q0111_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=111&reply=109"))
                htmltext = "marquez_q0111_16.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=110"))
                htmltext = "marquez_q0111_17.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=111"))
                htmltext = "marquez_q0111_18.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=112"))
                htmltext = "marquez_q0111_19.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=113"))
                htmltext = "marquez_q0111_20.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=114"))
                htmltext = "marquez_q0111_21.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=115"))
                htmltext = "marquez_q0111_22.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=116")) {
                if (GetMemoState == 5) {
                    st.setCond(6);
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(6), true);
                    st.giveItems(q_letter_to_kirikachin, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "marquez_q0111_23.htm";
                }
            }
        } else if (npcId == asama) {
            if (event.equalsIgnoreCase("menu_select?ask=111&reply=301")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "asama_q0111_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=111&reply=302")) {
                if (GetMemoState == 9) {
                    st.setCond(9);
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(10), true);
                    st.soundEffect(SOUND_ELCROKI_SONG_FULL);
                    htmltext = "asama_q0111_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=111&reply=303"))
                htmltext = "asama_q0111_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=304")) {
                if (GetMemoState == 10) {
                    st.setCond(10);
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(11), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "asama_q0111_09.htm";
                }
            }
        } else if (npcId == kirikachin) {
            if (event.equalsIgnoreCase("menu_select?ask=111&reply=401"))
                htmltext = "kirikachin_q0111_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=111&reply=402")) {
                if (GetMemoState == 7) {
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(8), true);
                    st.soundEffect(SOUND_ELCROKI_SONG_FULL);
                    htmltext = "kirikachin_q0111_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=111&reply=403")) {
                if (GetMemoState == 8) {
                    st.setCond(8);
                    st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(9), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "kirikachin_q0111_07.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=111&reply=404")) {
                if (GetMemoState == 12 && st.ownItemCount(q_capture_train_weapon) >= 1) {
                    st.takeItems(q_capture_train_weapon, -1);
                    st.giveItems(capture_of_elcrok, 1);
                    st.giveItems(capture_stone, 100);
                    st.giveItems(ADENA_ID, 1071691);
                    st.addExpAndSp(553524, 55538);
                    st.removeMemo("the_proop_of_elcroky_hunter");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "kirikachin_q0111_10.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("the_proop_of_elcroky_hunter");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == marquez) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "marquez_q0111_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "marquez_q0111_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == marquez) {
                    if (GetMemoState == 1)
                        htmltext = "marquez_q0111_06.htm";
                    else if (GetMemoState == 2)
                        htmltext = "marquez_q0111_06a.htm";
                    else if (GetMemoState == 3)
                        htmltext = "marquez_q0111_07.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_dyno_dairy) < 50)
                        htmltext = "marquez_q0111_14.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_dyno_dairy) >= 50) {
                        st.takeItems(q_dyno_dairy, -1);
                        st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(5), true);
                        htmltext = "marquez_q0111_15.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "marquez_q0111_15a.htm";
                    else if (GetMemoState == 6)
                        htmltext = "marquez_q0111_24.htm";
                    else if (GetMemoState >= 7 && GetMemoState <= 8)
                        htmltext = "marquez_q0111_25.htm";
                    else if (GetMemoState == 9)
                        htmltext = "marquez_q0111_26.htm";
                    else if (GetMemoState >= 10)
                        htmltext = "marquez_q0111_27.htm";
                } else if (npcId == mushika) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "mushika_q0111_01.htm";
                    } else if (GetMemoState > 1 && GetMemoState < 10)
                        htmltext = "mushika_q0111_02 .htm";
                    else if (GetMemoState >= 10)
                        htmltext = "mushika_q0111_03.htm";
                } else if (npcId == asama) {
                    if (GetMemoState == 1)
                        htmltext = "asama_q0111_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "asama_q0111_02.htm";
                    else if (GetMemoState >= 3 && GetMemoState < 9)
                        htmltext = "asama_q0111_04.htm";
                    else if (GetMemoState == 9)
                        htmltext = "asama_q0111_05.htm";
                    else if (GetMemoState == 10)
                        htmltext = "asama_q0111_07.htm";
                    else if (GetMemoState == 11 && (st.ownItemCount(q_capture_claw) < 10 || st.ownItemCount(q_capture_bone) < 10 || st.ownItemCount(q_capture_skin) < 10))
                        htmltext = "asama_q0111_10.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_capture_claw) >= 10 && st.ownItemCount(q_capture_bone) >= 10 && st.ownItemCount(q_capture_skin) >= 10) {
                        st.setCond(12);
                        st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(12), true);
                        st.giveItems(q_capture_train_weapon, 1);
                        st.takeItems(q_capture_claw, -1);
                        st.takeItems(q_capture_bone, -1);
                        st.takeItems(q_capture_skin, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "asama_q0111_11.htm";
                    } else if (GetMemoState >= 12)
                        htmltext = "asama_q0111_12.htm";
                } else if (npcId == kirikachin) {
                    if (GetMemoState < 6)
                        htmltext = "kirikachin_q0111_01.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_letter_to_kirikachin) >= 1) {
                        st.setCond(7);
                        st.setMemoState("the_proop_of_elcroky_hunter", String.valueOf(7), true);
                        st.takeItems(q_letter_to_kirikachin, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "kirikachin_q0111_02.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "kirikachin_q0111_04.htm";
                    else if (GetMemoState == 8)
                        htmltext = "kirikachin_q0111_06.htm";
                    else if (GetMemoState >= 9 && GetMemoState < 12)
                        htmltext = "kirikachin_q0111_08.htm";
                    else if (GetMemoState == 12)
                        htmltext = "kirikachin_q0111_09.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("the_proop_of_elcroky_hunter");
        int npcId = npc.getNpcId();
        if (npcId == velociraptor_leader || npcId == velociraptor || npcId == velociraptor_s) {
            if (GetMemoState == 4) {
                int i4 = Rnd.get(1000);
                if (i4 < 510 && st.ownItemCount(q_dyno_dairy) <= 49) {
                    if (st.ownItemCount(q_dyno_dairy) < 49) {
                        st.giveItems(q_dyno_dairy, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_dyno_dairy) >= 49) {
                        st.setCond(5);
                        st.giveItems(q_dyno_dairy, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            }
        } else if (npcId == ornithomimus_leader || npcId == ornithomimus_s) {
            if (GetMemoState == 11) {
                int i4 = Rnd.get(1000);
                if (i4 < 660 && st.ownItemCount(q_capture_claw) <= 9) {
                    if (st.ownItemCount(q_capture_claw) < 9) {
                        st.giveItems(q_capture_claw, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_capture_claw) == 9) {
                        st.giveItems(q_capture_claw, 1);
                        if (st.ownItemCount(q_capture_skin) >= 10 && st.ownItemCount(q_capture_bone) >= 10) {
                            st.setCond(11);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
                }
            }
        } else if (npcId == ornithomimus || npcId == ornithomimus_n || npcId == ornithomimus_leader2) {
            if (GetMemoState == 11) {
                int i4 = Rnd.get(1000);
                if (i4 < 330 && st.ownItemCount(q_capture_claw) <= 9) {
                    if (st.ownItemCount(q_capture_claw) < 9) {
                        st.giveItems(q_capture_claw, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_capture_claw) == 9) {
                        st.giveItems(q_capture_claw, 1);
                        if (st.ownItemCount(q_capture_skin) >= 10 && st.ownItemCount(q_capture_bone) >= 10) {
                            st.setCond(11);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
                }
            }
        } else if (npcId == deinonychus_leader || npcId == deinonychus_s) {
            if (GetMemoState == 11) {
                int i4 = Rnd.get(1000);
                if (i4 < 650 && st.ownItemCount(q_capture_bone) <= 9) {
                    if (st.ownItemCount(q_capture_bone) < 9) {
                        st.giveItems(q_capture_bone, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_capture_bone) == 9) {
                        st.giveItems(q_capture_bone, 1);
                        if (st.ownItemCount(q_capture_claw) >= 10 && st.ownItemCount(q_capture_skin) >= 10) {
                            st.setCond(11);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
                }
            }
        } else if (npcId == deinonychus || npcId == deinonychus_n || npcId == deinonychus_leader2) {
            if (GetMemoState == 11) {
                int i4 = Rnd.get(1000);
                if (i4 < 320 && st.ownItemCount(q_capture_bone) <= 9) {
                    if (st.ownItemCount(q_capture_bone) < 9) {
                        st.giveItems(q_capture_bone, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_capture_bone) == 9) {
                        st.giveItems(q_capture_bone, 1);
                        if (st.ownItemCount(q_capture_claw) >= 10 && st.ownItemCount(q_capture_skin) >= 10) {
                            st.setCond(11);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
                }
            }
        } else if (npcId == pachycephalosaurus_ldr || npcId == pachycephalosaurus || npcId == pachycephalosaurus_s || npcId == pachycephalosaurus_n || npcId == pachycephalosaurus_ldr2) {
            if (GetMemoState == 11) {
                int i4 = Rnd.get(1000);
                if (i4 < 500 && st.ownItemCount(q_capture_skin) <= 9) {
                    if (st.ownItemCount(q_capture_skin) < 9) {
                        st.giveItems(q_capture_skin, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_capture_skin) == 9) {
                        st.giveItems(q_capture_skin, 1);
                        if (st.ownItemCount(q_capture_claw) >= 10 && st.ownItemCount(q_capture_bone) >= 10) {
                            st.setCond(11);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    }
                }
            }
        } else if (npcId == velociraptor_n || npcId == velociraptor_leader2) {
            if (GetMemoState == 4) {
                int i4 = Rnd.get(1000);
                if (i4 < 250 && st.ownItemCount(q_dyno_dairy) <= 49) {
                    if (st.ownItemCount(q_dyno_dairy) < 49) {
                        st.giveItems(q_dyno_dairy, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(q_dyno_dairy) >= 49) {
                        st.setCond(5);
                        st.giveItems(q_dyno_dairy, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
            }
        }
        return null;
    }
}