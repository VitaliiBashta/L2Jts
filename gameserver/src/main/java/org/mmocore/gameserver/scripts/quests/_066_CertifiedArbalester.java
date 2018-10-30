package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 12/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _066_CertifiedArbalester extends Quest {
    // npc
    private final static int master_ryndy = 32201;
    private final static int magister_clayton = 30464;
    private final static int blacksmith_poitan = 30458;
    private final static int holvas = 30058;
    private final static int grandmaster_meldina = 32214;
    private final static int master_selsia = 32220;
    private final static int az = 30171;
    private final static int magister_gauen = 30717;
    private final static int magister_kaiena = 30720;
    // mobs
    private final static int delu_lizardman_shaman = 20781;
    private final static int plain_observer = 21102;
    private final static int oddly_stone_golem = 21103;
    private final static int delu_lizardman_q_master = 21104;
    private final static int delu_lizardman_agent = 21105;
    private final static int cursed_observer = 21106;
    private final static int delu_lizardman_commander = 21107;
    private final static int granitic_golem = 20083;
    private final static int hanged_man_ripper = 20144;
    private final static int amber_basilisk = 20199;
    private final static int strain = 20200;
    private final static int ghoul = 20201;
    private final static int dead_seeker = 20202;
    private final static int grandis = 20554;
    private final static int manashen = 20563;
    private final static int timak_orc = 20583;
    private final static int timak_orc_archer = 20584;
    private final static int lady_of_crimson = 27336;
    // questitem
    private final static int q_crystal_of_rancor = 9773;
    private final static int q_crystal_cluster_of_rancor = 9774;
    private final static int q_copy_page_of_new_race_book = 9775;
    private final static int q_secret_encodings = 9776;
    private final static int q_inqusitor_tainer_badge = 9777;
    private final static int q_attack_request_to_grandis_part = 9778;
    private final static int q_attack_request_to_grandis = 9779;
    private final static int q_manasen_control_tailsman = 9780;
    private final static int q_giant_study_paper = 9781;
    private final static int q_inqusitor_badge = 9782;
    // etcitem
    private final static int q_dimension_diamond = 7562;

    public _066_CertifiedArbalester() {
        super(false);
        addStartNpc(master_ryndy);
        addTalkId(magister_clayton, blacksmith_poitan, holvas, grandmaster_meldina, master_selsia, az, magister_gauen, magister_kaiena);
        addQuestItem(q_crystal_of_rancor, q_crystal_cluster_of_rancor, q_copy_page_of_new_race_book, q_inqusitor_tainer_badge, q_attack_request_to_grandis_part, q_manasen_control_tailsman, q_giant_study_paper);
        addKillId(delu_lizardman_shaman, plain_observer, oddly_stone_golem, delu_lizardman_q_master, delu_lizardman_agent, delu_lizardman_commander, cursed_observer, granitic_golem, hanged_man_ripper, amber_basilisk, strain, ghoul, dead_seeker, grandis, manashen, timak_orc, timak_orc_archer, lady_of_crimson);
        addLevelCheck(39);
        addClassIdCheck(ClassId.warder);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_poof_of_arbalester");
        int npcId = npc.getNpcId();
        if (npcId == master_ryndy) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("the_poof_of_arbalester", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 64);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "master_ryndy_q0066_07a.htm";
                } else
                    htmltext = "master_ryndy_q0066_07.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_ryndy_q0066_08.htm";
                }
            }
        } else if (npcId == magister_clayton) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2)
                    htmltext = "magister_clayton_q0066_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2)
                    htmltext = "magister_clayton_q0066_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2)
                    htmltext = "magister_clayton_q0066_05.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "magister_clayton_q0066_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 4) {
                    st.setCond(5);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(5), true);
                    st.giveItems(q_crystal_cluster_of_rancor, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "magister_clayton_q0066_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6"))
                htmltext = "magister_clayton_q0066_11.htm";
        } else if (npcId == blacksmith_poitan) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 5) {
                    st.takeItems(q_crystal_cluster_of_rancor, -1);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(6), true);
                    htmltext = "blacksmith_poitan_q0066_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 6)
                    htmltext = "blacksmith_poitan_q0066_05.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 6)
                    htmltext = "blacksmith_poitan_q0066_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 6)
                    htmltext = "blacksmith_poitan_q0066_07.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 6)
                    htmltext = "blacksmith_poitan_q0066_08.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 6) {
                    st.setCond(6);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(7), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "blacksmith_poitan_q0066_09.htm";
                }
            }
        } else if (npcId == holvas) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 7)
                    htmltext = "holvas_q0066_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 7)
                    htmltext = "holvas_q0066_04.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 7) {
                    st.setCond(7);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(8), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "holvas_q0066_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 9) {
                    st.setCond(9);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(10), true);
                    st.giveItems(q_secret_encodings, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "holvas_q0066_08.htm";
                }
            }
        } else if (npcId == grandmaster_meldina) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 10)
                    htmltext = "grandmaster_meldina_q0066_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 10) {
                    st.setCond(10);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(11), true);
                    st.giveItems(q_inqusitor_tainer_badge, 1);
                    st.takeItems(q_secret_encodings, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "grandmaster_meldina_q0066_04.htm";
                }
            }
        } else if (npcId == master_selsia) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 11) {
                    st.takeItems(q_inqusitor_tainer_badge, -1);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(12), true);
                    htmltext = "master_selsia_q0066_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 12) {
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(13), true);
                    htmltext = "master_selsia_q0066_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 13) {
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(0), true);
                    htmltext = "master_selsia_q0066_06.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 13)
                    htmltext = "master_selsia_q0066_09.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 13)
                    htmltext = "master_selsia_q0066_10.htm";
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 13) {
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(13), true);
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(1), true);
                    htmltext = "master_selsia_q0066_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 13) {
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(13), true);
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(1), true);
                    htmltext = "master_selsia_q0066_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (GetMemoState == 13) {
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(13), true);
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(1), true);
                    htmltext = "master_selsia_q0066_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 13) {
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(20), true);
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(0), true);
                    htmltext = "master_selsia_q0066_13a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (GetMemoState == 20) {
                    st.setCond(11);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(21), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_selsia_q0066_13b.htm";
                }
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (GetMemoState == 31)
                    htmltext = "master_selsia_q0066_19.htm";
            } else if (event.equalsIgnoreCase("reply_22")) {
                if (GetMemoState == 31)
                    htmltext = "master_selsia_q0066_21.htm";
            } else if (event.equalsIgnoreCase("reply_23")) {
                if (GetMemoState == 31)
                    htmltext = "master_selsia_q0066_22.htm";
            } else if (event.equalsIgnoreCase("reply_24")) {
                if (GetMemoState == 31)
                    htmltext = "master_selsia_q0066_23.htm";
            } else if (event.equalsIgnoreCase("reply_25")) {
                if (GetMemoState == 31)
                    htmltext = "master_selsia_q0066_24.htm";
            } else if (event.equalsIgnoreCase("reply_26")) {
                if (GetMemoState == 31)
                    htmltext = "master_selsia_q0066_25.htm";
            } else if (event.equalsIgnoreCase("reply_27")) {
                if (GetMemoState == 31) {
                    st.setCond(19);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(32), true);
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(0), true);
                    htmltext = "master_selsia_q0066_26.htm";
                }
            }
        } else if (npcId == az) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 23)
                    htmltext = "az_q0066_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 23) {
                    st.setCond(19);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(24), true);
                    st.takeItems(q_attack_request_to_grandis, -1);
                    htmltext = "az_q0066_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 24)
                    htmltext = "az_q0066_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 24)
                    htmltext = "az_q0066_07.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 24) {
                    st.setCond(14);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(25), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "az_q0066_08.htm";
                }
            }
        } else if (npcId == magister_gauen) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 28)
                    htmltext = "magister_gauen_q0066_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 28)
                    htmltext = "magister_gauen_q0066_05.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 28)
                    htmltext = "magister_gauen_q0066_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 28)
                    htmltext = "magister_gauen_q0066_07.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 28)
                    htmltext = "magister_gauen_q0066_08.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 28) {
                    st.setCond(17);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(29), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "magister_gauen_q0066_09.htm";
                }
            }
        } else if (npcId == magister_kaiena) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 29)
                    htmltext = "magister_kaiena_q0066_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 29) {
                    st.setCond(18);
                    st.setMemoState("the_poof_of_arbalester", String.valueOf(30), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "magister_kaiena_q0066_04.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int GetMemoState = st.getInt("the_poof_of_arbalester");
        int GetMemoStateEx = st.getInt("the_poof_of_arbalester_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        int warder = 0x7e;
        int arbalester = 0x82;
        switch (id) {
            case CREATED:
                if (npcId == master_ryndy) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "master_ryndy_q0066_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            if (talker_occupation != warder && talker_occupation != arbalester)
                                htmltext = "master_ryndy_q0066_03.htm";
                            if (talker_occupation == arbalester)
                                htmltext = "master_ryndy_q0066_05.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "master_ryndy_q0066_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == master_ryndy) {
                    if (st.ownItemCount(q_inqusitor_badge) > 0 && talker_occupation != arbalester)
                        htmltext = "master_ryndy_q0066_06.htm";
                    else if (GetMemoState == 2)
                        htmltext = "master_ryndy_q0066_10.htm";
                    else if (GetMemoState > 2 && GetMemoState < 11)
                        htmltext = "master_ryndy_q0066_11.htm";
                    else if (GetMemoState >= 11)
                        htmltext = "master_ryndy_q0066_12.htm";
                } else if (npcId == magister_clayton) {
                    if (GetMemoState < 2)
                        htmltext = "magister_clayton_q0066_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "magister_clayton_q0066_02.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30)
                        htmltext = "magister_clayton_q0066_07.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) >= 30) {
                        st.takeItems(q_crystal_of_rancor, -1);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(4), true);
                        htmltext = "magister_clayton_q0066_08.htm";
                    } else if (GetMemoState == 4) {
                        st.setCond(5);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(5), true);
                        st.giveItems(q_crystal_cluster_of_rancor, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_clayton_q0066_10.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "magister_clayton_q0066_12.htm";
                    else if (GetMemoState > 5)
                        htmltext = "magister_clayton_q0066_13.htm";
                } else if (npcId == blacksmith_poitan) {
                    if (GetMemoState < 5)
                        htmltext = "blacksmith_poitan_q0066_01.htm";
                    else if (GetMemoState == 5)
                        htmltext = "blacksmith_poitan_q0066_02.htm";
                    else if (GetMemoState == 6)
                        htmltext = "blacksmith_poitan_q0066_04.htm";
                    else if (GetMemoState == 7)
                        htmltext = "blacksmith_poitan_q0066_10.htm";
                } else if (npcId == holvas) {
                    if (GetMemoState < 7)
                        htmltext = "holvas_q0066_01.htm";
                    else if (GetMemoState == 7)
                        htmltext = "holvas_q0066_02.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q_copy_page_of_new_race_book) < 30)
                        htmltext = "holvas_q0066_06.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q_copy_page_of_new_race_book) >= 30) {
                        st.takeItems(q_copy_page_of_new_race_book, -1);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(9), true);
                        htmltext = "holvas_q0066_07.htm";
                    } else if (GetMemoState == 9) {
                        st.setCond(9);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(10), true);
                        st.giveItems(q_secret_encodings, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "holvas_q0066_09.htm";
                    } else if (GetMemoState > 9)
                        htmltext = "holvas_q0066_10.htm";
                } else if (npcId == grandmaster_meldina) {
                    if (GetMemoState < 10)
                        htmltext = "grandmaster_meldina_q0066_01.htm";
                    else if (GetMemoState == 10)
                        htmltext = "grandmaster_meldina_q0066_02.htm";
                    else if (GetMemoState == 11)
                        htmltext = "grandmaster_meldina_q0066_05.htm";
                    else if (GetMemoState > 11)
                        htmltext = "grandmaster_meldina_q0066_06.htm";
                } else if (npcId == master_selsia) {
                    if (GetMemoState < 11)
                        htmltext = "master_selsia_q0066_01.htm";
                    else if (GetMemoState == 11)
                        htmltext = "master_selsia_q0066_02.htm";
                    else if (GetMemoState == 12)
                        htmltext = "master_selsia_q0066_04.htm";
                    else if (GetMemoState == 13 && GetMemoStateEx == 0) {
                        st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(0), true);
                        htmltext = "master_selsia_q0066_07.htm";
                    } else if (GetMemoState == 13 && GetMemoStateEx == 1) {
                        st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(0), true);
                        htmltext = "master_selsia_q0066_08.htm";
                    } else if (GetMemoState == 20) {
                        st.setCond(11);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(21), true);
                        st.giveItems(q_secret_encodings, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_selsia_q0066_14.htm";
                    } else if (GetMemoState == 21)
                        htmltext = "master_selsia_q0066_15.htm";
                    else if (GetMemoState == 22)
                        htmltext = "master_selsia_q0066_16.htm";
                    else if (GetMemoState >= 23 && GetMemoState < 30)
                        htmltext = "master_selsia_q0066_17.htm";
                    else if (GetMemoState == 30) {
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(31), true);
                        htmltext = "master_selsia_q0066_18.htm";
                    } else if (GetMemoState == 31)
                        htmltext = "master_selsia_q0066_20.htm";
                    else if (GetMemoState == 32 && st.ownItemCount(q_giant_study_paper) < 1)
                        htmltext = "master_selsia_q0066_27.htm";
                    else if (GetMemoState == 32 && st.ownItemCount(q_giant_study_paper) >= 1) {
                        st.giveItems(q_inqusitor_badge, 1);
                        st.takeItems(q_giant_study_paper, -1);
                        if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.PROF2_1)) {
                            st.addExpAndSp(429546, 29476);
                            st.giveItems(ADENA_ID, 77666);
                        }
                        st.removeMemo("the_poof_of_arbalester");
                        st.removeMemo("the_poof_of_arbalester_ex");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "master_selsia_q0066_28.htm";
                    }
                } else if (npcId == az) {
                    if (GetMemoState < 23)
                        htmltext = "az_q0066_01.htm";
                    else if (GetMemoState == 23)
                        htmltext = "az_q0066_02.htm";
                    else if (GetMemoState == 24)
                        htmltext = "az_q0066_06.htm";
                    else if (GetMemoState == 25)
                        htmltext = "az_q0066_09.htm";
                    else if (GetMemoState == 26)
                        htmltext = "az_q0066_10.htm";
                    else if (GetMemoState == 27)
                        htmltext = "az_q0066_11.htm";
                    else if (GetMemoState == 28)
                        htmltext = "az_q0066_12.htm";
                    else if (GetMemoState == 29)
                        htmltext = "az_q0066_13.htm";
                } else if (npcId == magister_gauen) {
                    if (GetMemoState < 27)
                        htmltext = "magister_gauen_q0066_01.htm";
                    else if (GetMemoState == 27) {
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(28), true);
                        st.takeItems(q_manasen_control_tailsman, -1);
                        htmltext = "magister_gauen_q0066_02.htm";
                    } else if (GetMemoState == 28)
                        htmltext = "magister_gauen_q0066_04.htm";
                    else if (GetMemoState >= 29)
                        htmltext = "magister_gauen_q0066_10.htm";
                } else if (npcId == magister_kaiena) {
                    if (GetMemoState < 29)
                        htmltext = "magister_kaiena_q0066_01.htm";
                    else if (GetMemoState == 29)
                        htmltext = "magister_kaiena_q0066_02.htm";
                    else if (GetMemoState >= 30)
                        htmltext = "magister_kaiena_q0066_05.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("the_poof_of_arbalester");
        int GetMemoStateEx = st.getInt("the_poof_of_arbalester_ex");
        int npcId = npc.getNpcId();
        if (npcId == delu_lizardman_shaman || npcId == delu_lizardman_q_master) {
            if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_crystal_of_rancor) < 30) {
                    if (st.ownItemCount(q_crystal_of_rancor) >= 29) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_crystal_of_rancor, 1);
                    if (i4 < 80 && st.ownItemCount(q_crystal_of_rancor) < 29)
                        st.giveItems(q_crystal_of_rancor, 1);
                }
            }
        } else if (npcId == plain_observer) {
            if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                int i4 = Rnd.get(1000);
                if (i4 < 840 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                    if (st.ownItemCount(q_crystal_of_rancor) >= 29) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_crystal_of_rancor, 1);
                }
            }
        } else if (npcId == oddly_stone_golem) {
            if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                int i4 = Rnd.get(1000);
                if (i4 < 860 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                    if (st.ownItemCount(q_crystal_of_rancor) >= 29) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_crystal_of_rancor, 1);
                }
            }
        } else if (npcId == delu_lizardman_agent) {
            if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_crystal_of_rancor) < 30) {
                    if (st.ownItemCount(q_crystal_of_rancor) >= 29) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_crystal_of_rancor, 1);
                    if (i4 < 240 && st.ownItemCount(q_crystal_of_rancor) < 29)
                        st.giveItems(q_crystal_of_rancor, 1);
                }
            }
        } else if (npcId == cursed_observer) {
            if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_crystal_of_rancor) < 30) {
                    if (st.ownItemCount(q_crystal_of_rancor) >= 29) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_crystal_of_rancor, 1);
                    if (i4 < 40 && st.ownItemCount(q_crystal_of_rancor) < 29)
                        st.giveItems(q_crystal_of_rancor, 1);
                }
            }
        } else if (npcId == delu_lizardman_commander) {
            if (GetMemoState == 3 && st.ownItemCount(q_crystal_of_rancor) < 30) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_crystal_of_rancor) < 30) {
                    if (st.ownItemCount(q_crystal_of_rancor) >= 29) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_crystal_of_rancor, 1);
                    if (i4 < 220 && st.ownItemCount(q_crystal_of_rancor) < 29)
                        st.giveItems(q_crystal_of_rancor, 2);
                }
            }
        } else if (npcId == granitic_golem || npcId == hanged_man_ripper) {
            if (GetMemoState == 8 && st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                    if (st.ownItemCount(q_copy_page_of_new_race_book) >= 29) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_copy_page_of_new_race_book, 1);
                    if (i4 < 100 && st.ownItemCount(q_copy_page_of_new_race_book) < 29)
                        st.giveItems(q_copy_page_of_new_race_book, 1);
                }
            }
        } else if (npcId == amber_basilisk) {
            if (GetMemoState == 8 && st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                int i4 = Rnd.get(1000);
                if (i4 < 980 && st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                    if (st.ownItemCount(q_copy_page_of_new_race_book) >= 29) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_copy_page_of_new_race_book, 1);
                }
            }
        } else if (npcId == strain) {
            if (GetMemoState == 8 && st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                int i4 = Rnd.get(1000);
                if (i4 < 860 && st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                    if (st.ownItemCount(q_copy_page_of_new_race_book) >= 29) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_copy_page_of_new_race_book, 1);
                }
            }
        } else if (npcId == ghoul || npcId == dead_seeker) {
            if (GetMemoState == 8 && st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                int i4 = Rnd.get(1000);
                if (st.ownItemCount(q_copy_page_of_new_race_book) < 30) {
                    if (st.ownItemCount(q_copy_page_of_new_race_book) >= 29) {
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_copy_page_of_new_race_book, 1);
                    if (i4 < 20 && st.ownItemCount(q_copy_page_of_new_race_book) < 29)
                        st.giveItems(q_copy_page_of_new_race_book, 1);
                }
            }
        } else if (npcId == grandis) {
            if ((GetMemoState == 21 || GetMemoState == 22) && st.ownItemCount(q_attack_request_to_grandis_part) < 10) {
                int i4 = Rnd.get(1000);
                if (i4 < 780 && st.ownItemCount(q_attack_request_to_grandis_part) < 10) {
                    if (GetMemoState == 21 && st.ownItemCount(q_attack_request_to_grandis_part) < 1) {
                        st.setCond(12);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(22), true);
                        st.giveItems(q_attack_request_to_grandis_part, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (GetMemoState == 22 && st.ownItemCount(q_attack_request_to_grandis_part) >= 9) {
                        st.setCond(13);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(23), true);
                        st.giveItems(q_attack_request_to_grandis, 1);
                        st.takeItems(q_attack_request_to_grandis_part, -1);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                        st.giveItems(q_attack_request_to_grandis_part, 1);
                    }
                }
            }
        } else if (npcId == manashen) {
            if ((GetMemoState == 25 || GetMemoState == 26) && st.ownItemCount(q_manasen_control_tailsman) < 10) {
                int i4 = Rnd.get(1000);
                if (i4 < 840 && st.ownItemCount(q_manasen_control_tailsman) < 10) {
                    if (GetMemoState == 25 && st.ownItemCount(q_manasen_control_tailsman) < 1) {
                        st.setCond(15);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(26), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (GetMemoState == 26 && st.ownItemCount(q_manasen_control_tailsman) >= 9) {
                        st.setCond(16);
                        st.setMemoState("the_poof_of_arbalester", String.valueOf(27), true);
                        st.soundEffect(SOUND_MIDDLE);
                    } else {
                        st.soundEffect(SOUND_ITEMGET);
                        st.giveItems(q_manasen_control_tailsman, 1);
                    }
                }
            }
        } else if (npcId == timak_orc || npcId == timak_orc_archer) {
            if (GetMemoState == 32) {
                if (GetMemoStateEx < 5)
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(GetMemoStateEx + 1), true);
                else if (GetMemoStateEx >= 4) {
                    st.setMemoState("the_poof_of_arbalester_ex", String.valueOf(0), true);
                    st.addSpawn(lady_of_crimson, npc.getX(), npc.getY(), npc.getZ());
                }
            }
        } else if (npcId == lady_of_crimson) {
            if (GetMemoState == 32) {
                st.setCond(20);
                st.setMemoState("the_poof_of_arbalester", String.valueOf(32), true);
                st.giveItems(q_giant_study_paper, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}