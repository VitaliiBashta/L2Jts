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
 * @version 1.2
 * @date 05/09/2015
 * @LastEdit 10/09/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _213_TrialOfSeeker extends Quest {
    // npc
    private static final int dufner = 30106;
    private static final int terry = 30064;
    private static final int trader_viktor = 30684;
    private static final int magister_marina = 30715;
    private static final int blacksmith_bronp = 30526;
    // mobs
    private static final int ant_captain = 20080;
    private static final int ant_warrior_captain = 20088;
    private static final int medusa = 20158;
    private static final int neer_crawler_frak = 20198;
    private static final int ol_mahum_chief_leader = 20211;
    private static final int marsh_stakato_drone = 20234;
    private static final int turak_bugbear_warrior = 20249;
    private static final int breka_orc_overlord = 20270;
    private static final int turek_orc_warlord = 20495;
    private static final int leto_lizardman_warrior = 20580;
    // questitem
    private static final int dufners_letter = 2647;
    private static final int terys_order1 = 2648;
    private static final int terys_order2 = 2649;
    private static final int terys_letter = 2650;
    private static final int viktors_letter = 2651;
    private static final int hawkeyes_letter = 2652;
    private static final int mysterious_runestone = 2653;
    private static final int ol_mahum_runestone = 2654;
    private static final int turek_runestone = 2655;
    private static final int ant_runestone = 2656;
    private static final int turak_bugbear_runestone = 2657;
    private static final int terys_box = 2658;
    private static final int viktors_request = 2659;
    private static final int medusas_scales = 2660;
    private static final int silens_runestone = 2661;
    private static final int analysis_request = 2662;
    private static final int marinas_letter = 2663;
    private static final int experiment_tools = 2664;
    private static final int analysis_result = 2665;
    private static final int terys_order3 = 2666;
    private static final int list_of_host = 2667;
    private static final int abyss_runestone1 = 2668;
    private static final int abyss_runestone2 = 2669;
    private static final int abyss_runestone3 = 2670;
    private static final int abyss_runestone4 = 2671;
    private static final int terys_report = 2672;
    private static final int mark_of_seeker = 2673;
    private static final int q_dimension_diamond = 7562;

    public _213_TrialOfSeeker() {
        super(false);
        addStartNpc(dufner);
        addTalkId(terry, trader_viktor, magister_marina, blacksmith_bronp);
        addKillId(ant_captain, ant_warrior_captain, medusa, neer_crawler_frak, ol_mahum_chief_leader, marsh_stakato_drone, turak_bugbear_warrior, breka_orc_overlord, turek_orc_warlord, leto_lizardman_warrior);
        addQuestItem(dufners_letter, terys_order1, terys_order2, terys_letter, terys_box, viktors_letter, viktors_request, hawkeyes_letter, silens_runestone, analysis_request, marinas_letter, experiment_tools, analysis_result, list_of_host, terys_order3, terys_report, mysterious_runestone, ol_mahum_runestone, turek_runestone, ant_runestone, turak_bugbear_runestone, medusas_scales, abyss_runestone1, abyss_runestone2, abyss_runestone3, abyss_runestone4);
        addLevelCheck(35);
        addClassIdCheck(ClassId.rogue, ClassId.elven_scout, ClassId.assassin);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == dufner) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                if (st.ownItemCount(dufners_letter) == 0)
                    st.giveItems(dufners_letter, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 128);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "dufner_q0213_05a.htm";
                } else
                    htmltext = "dufner_q0213_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=1"))
                htmltext = "dufner_q0213_04.htm";
        } else if (npcId == terry) {
            if (event.equalsIgnoreCase("menu_select?ask=213&reply=1"))
                htmltext = "terry_q0213_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=2")) {
                if (st.ownItemCount(dufners_letter) >= 1) {
                    st.setCond(2);
                    st.takeItems(dufners_letter, 1);
                    st.giveItems(terys_order1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "terry_q0213_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=3")) {
                if (st.ownItemCount(terys_order1) >= 1) {
                    st.setCond(4);
                    st.takeItems(mysterious_runestone, -1);
                    st.takeItems(terys_order1, 1);
                    st.giveItems(terys_order2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "terry_q0213_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=4")) {
                st.setCond(6);
                st.takeItems(ol_mahum_runestone, -1);
                st.takeItems(turek_runestone, -1);
                st.takeItems(ant_runestone, -1);
                st.takeItems(turak_bugbear_runestone, -1);
                st.takeItems(terys_order2, -1);
                st.giveItems(terys_letter, 1);
                st.giveItems(terys_box, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "terry_q0213_10.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=5"))
                htmltext = "terry_q0213_16.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=6")) {
                if (st.ownItemCount(analysis_result) >= 1) {
                    st.setCond(15);
                    st.takeItems(analysis_result, 1);
                    st.giveItems(list_of_host, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "terry_q0213_18.htm";
                }
            }
        } else if (npcId == trader_viktor) {
            if (event.equalsIgnoreCase("menu_select?ask=213&reply=1"))
                htmltext = "trader_viktor_q0213_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=2"))
                htmltext = "trader_viktor_q0213_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=3"))
                htmltext = "trader_viktor_q0213_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=4")) {
                if (st.ownItemCount(terys_letter) >= 1) {
                    st.setCond(7);
                    st.takeItems(terys_letter, 1);
                    st.giveItems(viktors_letter, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "trader_viktor_q0213_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=5"))
                htmltext = "trader_viktor_q0213_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=6"))
                htmltext = "trader_viktor_q0213_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=7"))
                htmltext = "trader_viktor_q0213_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=8"))
                htmltext = "trader_viktor_q0213_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=9"))
                htmltext = "trader_viktor_q0213_10.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=213&reply=10")) {
                st.setCond(9);
                st.giveItems(viktors_request, 1);
                st.takeItems(terys_letter, -1);
                st.takeItems(terys_box, -1);
                st.takeItems(hawkeyes_letter, -1);
                st.takeItems(viktors_letter, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_viktor_q0213_11.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=11")) {
                st.setCond(11);
                st.takeItems(viktors_request, -1);
                st.takeItems(medusas_scales, -1);
                st.giveItems(silens_runestone, 1);
                st.giveItems(analysis_request, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_viktor_q0213_15.htm";
            }
        } else if (npcId == magister_marina) {
            if (event.equalsIgnoreCase("menu_select?ask=213&reply=1")) {
                st.setCond(12);
                st.takeItems(silens_runestone, -1);
                st.takeItems(analysis_request, -1);
                st.giveItems(marinas_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_marina_q0213_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=213&reply=2")) {
                st.setCond(14);
                st.takeItems(experiment_tools, -1);
                st.giveItems(analysis_result, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "magister_marina_q0213_05.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == dufner) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "dufner_q0213_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "dufner_q0213_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "dufner_q0213_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == dufner) {
                    if (st.ownItemCount(dufners_letter) == 1 && st.ownItemCount(terys_report) == 0)
                        htmltext = "dufner_q0213_06.htm";
                    else if (st.ownItemCount(dufners_letter) == 0 && st.ownItemCount(terys_report) == 0)
                        htmltext = "dufner_q0213_07.htm";
                    else if (st.ownItemCount(dufners_letter) == 0 && st.ownItemCount(terys_report) == 1) {
                        st.giveItems(mark_of_seeker, 1);
                        st.addExpAndSp(1029478, 66768);
                        st.giveItems(ADENA_ID, 187606);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.removeMemo("trial_of_seeker");
                        htmltext = "dufner_q0213_08.htm";
                    }
                } else if (npcId == terry) {
                    if (st.ownItemCount(dufners_letter) == 1)
                        htmltext = "terry_q0213_01.htm";
                    else if (st.ownItemCount(terys_order1) == 1) {
                        if (st.ownItemCount(mysterious_runestone) == 0)
                            htmltext = "terry_q0213_04.htm";
                        else
                            htmltext = "terry_q0213_05.htm";
                    } else if (st.ownItemCount(terys_order2) == 1) {
                        if (st.ownItemCount(terys_order2) == 1) {
                            if (st.ownItemCount(ol_mahum_runestone) + st.ownItemCount(turek_runestone) + st.ownItemCount(ant_runestone) + st.ownItemCount(turak_bugbear_runestone) < 4)
                                htmltext = "terry_q0213_08.htm";
                            else
                                htmltext = "terry_q0213_09.htm";
                        }
                    } else if (st.ownItemCount(terys_letter) == 1)
                        htmltext = "terry_q0213_11.htm";
                    else if (st.ownItemCount(viktors_letter) == 1) {
                        st.setCond(8);
                        st.takeItems(viktors_letter, -1);
                        st.giveItems(hawkeyes_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "terry_q0213_12.htm";
                    } else if (st.ownItemCount(hawkeyes_letter) == 1)
                        htmltext = "terry_q0213_13.htm";
                    else if (st.ownItemCount(viktors_request) == 1 || st.ownItemCount(analysis_request) == 1 || st.ownItemCount(marinas_letter) == 1 || st.ownItemCount(experiment_tools) == 1)
                        htmltext = "terry_q0213_14.htm";
                    else if (st.ownItemCount(analysis_result) == 1)
                        htmltext = "terry_q0213_15.htm";
                    else if (st.ownItemCount(terys_order3) == 1) {
                        if (st.getPlayer().getLevel() < 36)
                            htmltext = "terry_q0213_20.htm";
                        else {
                            st.setCond(15);
                            st.takeItems(terys_order3, 1);
                            st.giveItems(list_of_host, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "terry_q0213_21.htm";
                        }
                    } else if (st.ownItemCount(list_of_host) == 1) {
                        if (st.ownItemCount(abyss_runestone1) + st.ownItemCount(abyss_runestone2) + st.ownItemCount(abyss_runestone3) + st.ownItemCount(abyss_runestone4) < 4)
                            htmltext = "terry_q0213_22.htm";
                        else {
                            st.setCond(17);
                            st.giveItems(terys_report, 1);
                            st.takeItems(list_of_host, -1);
                            st.takeItems(abyss_runestone1, -1);
                            st.takeItems(abyss_runestone2, -1);
                            st.takeItems(abyss_runestone3, -1);
                            st.takeItems(abyss_runestone4, -1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "terry_q0213_23.htm";
                        }
                    } else if (st.ownItemCount(terys_report) == 1)
                        htmltext = "terry_q0213_24.htm";
                } else if (npcId == trader_viktor) {
                    if (st.ownItemCount(terys_letter) == 1)
                        htmltext = "trader_viktor_q0213_01.htm";
                    else if (st.ownItemCount(hawkeyes_letter) == 1)
                        htmltext = "trader_viktor_q0213_12.htm";
                    else if (st.ownItemCount(viktors_request) == 1) {
                        if (st.ownItemCount(medusas_scales) < 10)
                            htmltext = "trader_viktor_q0213_13.htm";
                        else
                            htmltext = "trader_viktor_q0213_14.htm";
                    } else if (st.ownItemCount(silens_runestone) == 1 && st.ownItemCount(analysis_request) == 1)
                        htmltext = "trader_viktor_q0213_16.htm";
                    else if (st.ownItemCount(marinas_letter) == 1 && st.ownItemCount(experiment_tools) == 1 && st.ownItemCount(analysis_result) == 1 && st.ownItemCount(terys_report) == 1)
                        htmltext = "trader_viktor_q0213_17.htm";
                    else if (st.ownItemCount(viktors_letter) == 1)
                        htmltext = "trader_viktor_q0213_05.htm";
                } else if (npcId == magister_marina) {
                    if (st.ownItemCount(silens_runestone) == 1 && st.ownItemCount(analysis_request) == 1)
                        htmltext = "magister_marina_q0213_01.htm";
                    else if (st.ownItemCount(marinas_letter) == 1)
                        htmltext = "magister_marina_q0213_03.htm";
                    else if (st.ownItemCount(experiment_tools) == 1)
                        htmltext = "magister_marina_q0213_04.htm";
                    else if (st.ownItemCount(analysis_result) == 1)
                        htmltext = "magister_marina_q0213_06.htm";
                } else if (npcId == blacksmith_bronp) {
                    if (st.ownItemCount(marinas_letter) == 1) {
                        st.setCond(13);
                        st.takeItems(marinas_letter, -1);
                        st.giveItems(experiment_tools, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_bronp_q0213_01.htm";
                    } else if (st.ownItemCount(experiment_tools) == 1)
                        htmltext = "blacksmith_bronp_q0213_02.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == ant_captain) {
            if (st.ownItemCount(terys_order2) == 1 && st.ownItemCount(ant_runestone) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(ant_runestone, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(ol_mahum_runestone) >= 1 && st.ownItemCount(turek_runestone) >= 1 && st.ownItemCount(turak_bugbear_runestone) >= 1)
                        st.setCond(5);
                }
            }
        } else if (npcId == ant_warrior_captain) {
            if (st.ownItemCount(list_of_host) == 1 && st.ownItemCount(abyss_runestone3) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(abyss_runestone3, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(abyss_runestone1) >= 1 && st.ownItemCount(abyss_runestone2) >= 1 && st.ownItemCount(abyss_runestone4) >= 1)
                        st.setCond(16);
                }
            }
        } else if (npcId == medusa) {
            if (st.ownItemCount(viktors_request) == 1 && st.ownItemCount(medusas_scales) < 10) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(medusas_scales, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(medusas_scales) >= 10)
                        st.setCond(10);
                }
            }
        } else if (npcId == neer_crawler_frak) {
            if (st.ownItemCount(terys_order1) == 1 && st.ownItemCount(mysterious_runestone) == 0) {
                if (Rnd.get(100) < 50) {
                    st.setCond(3);
                    st.giveItems(mysterious_runestone, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == ol_mahum_chief_leader) {
            if (st.ownItemCount(terys_order2) == 1 && st.ownItemCount(ol_mahum_runestone) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(ol_mahum_runestone, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(turek_runestone) >= 1 && st.ownItemCount(ant_runestone) >= 1 && st.ownItemCount(turak_bugbear_runestone) >= 1)
                        st.setCond(5);
                }
            }
        } else if (npcId == marsh_stakato_drone) {
            if (st.ownItemCount(list_of_host) == 1 && st.ownItemCount(abyss_runestone1) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(abyss_runestone1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(abyss_runestone2) >= 1 && st.ownItemCount(abyss_runestone3) >= 1 && st.ownItemCount(abyss_runestone4) >= 1)
                        st.setCond(16);
                }
            }
        } else if (npcId == turak_bugbear_warrior) {
            if (st.ownItemCount(terys_order2) == 1 && st.ownItemCount(turak_bugbear_runestone) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(turak_bugbear_runestone, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(ol_mahum_runestone) >= 1 && st.ownItemCount(turek_runestone) >= 1 && st.ownItemCount(ant_runestone) >= 1)
                        st.setCond(5);
                }
            }
        } else if (npcId == breka_orc_overlord) {
            if (st.ownItemCount(list_of_host) == 1 && st.ownItemCount(abyss_runestone2) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(abyss_runestone2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(abyss_runestone1) >= 1 && st.ownItemCount(abyss_runestone3) >= 1 && st.ownItemCount(abyss_runestone4) >= 1)
                        st.setCond(16);
                }
            }
        } else if (npcId == turek_orc_warlord) {
            if (st.ownItemCount(terys_order2) == 1 && st.ownItemCount(turek_runestone) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(turek_runestone, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(ol_mahum_runestone) >= 1 && st.ownItemCount(ant_runestone) >= 1 && st.ownItemCount(turak_bugbear_runestone) >= 1)
                        st.setCond(5);
                }
            }
        } else if (npcId == leto_lizardman_warrior) {
            if (st.ownItemCount(list_of_host) == 1 && st.ownItemCount(abyss_runestone4) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(abyss_runestone4, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    if (st.ownItemCount(abyss_runestone1) >= 1 && st.ownItemCount(abyss_runestone2) >= 1 && st.ownItemCount(abyss_runestone3) >= 1)
                        st.setCond(16);
                }
            }
        }
        return null;
    }
}