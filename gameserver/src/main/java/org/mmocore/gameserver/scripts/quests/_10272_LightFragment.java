package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10272_LightFragment extends Quest {
    // npc
    private static final int wharf_soldier_orbiu = 32560;
    private static final int warmage_artius = 32559;
    private static final int soldier_jinbi = 32566;
    private static final int silen_priest_relrikia = 32567;
    private static final int engineer_recon = 32557;

    // mobs
    private static final int draconian_eliteguard = 22536;
    private static final int draconian_archmage = 22537;
    private static final int draconian_legatus = 22538;
    private static final int draconian_primus_plius = 22539;
    private static final int draconian_centurion = 22540;
    private static final int draconian_legionarii = 22541;
    private static final int draconian_officer_leader = 22542;
    private static final int draconian_officer = 22543;
    private static final int draconian_mage = 22544;
    private static final int draconian_berserker = 22546;
    private static final int draconian_healer = 22547;
    private static final int draconian_peltast = 22548;
    private static final int draconian_peltast_fix = 22549;
    private static final int draconian_rider_ret = 22569;
    private static final int draconian_archmage_ret = 22571;
    private static final int draconian_eliteguard_ret = 22570;
    private static final int draconian_legatus_ret = 22572;
    private static final int draconian_primus_p_ret = 22573;
    private static final int draconian_centurion_ret = 22574;
    private static final int draconian_healer_ret = 22580;
    private static final int draconian_legionarii_ret = 22575;
    private static final int draconian_mage_ret_fix = 22585;
    private static final int draconian_officer_l_ret = 22576;
    private static final int draconian_legatus_r_fix = 22586;
    private static final int draconian_officer_ret = 22577;
    private static final int draconian_officer_fix = 22592;
    private static final int draconian_mage_ret = 22578;
    private static final int draconian_berserker_ret = 22579;
    private static final int draconian_peltast_ret = 22581;
    private static final int draconian_peltast_r_fix = 22582;
    private static final int draconian_offi_l_r_fix = 22583;
    private static final int draconian_offi_r_fix = 22584;
    private static final int draconian_primus_p_r_fix = 22587;
    private static final int draconian_cen_r_fix = 22588;
    private static final int draconian_leg_r_fix = 22589;
    private static final int draconian_officer_l_fix = 22591;
    private static final int draconian_mage_fix = 22593;
    private static final int draconian_legatus_fix = 22594;
    private static final int draconian_primus_p_fix = 22595;
    private static final int draconian_centurion_fix = 22596;
    private static final int draconian_legionarii_fix = 22597;
    private static final int remnant_draco_mage = 18789;
    private static final int remnant_draco_prim = 18784;
    private static final int remnant_draco_cent = 18785;
    private static final int remnant_draco_legi = 18786;
    private static final int remnant_draco_offl = 18787;
    private static final int remnant_draco_offi = 18788;
    private static final int remnant_draco_heal = 18790;
    private static final int remnant_draco_pelt = 18791;
    private static final int remnant_draco_pelt_fix = 18792;

    // questitem
    private static final int q_doc_of_investigator = 13852;
    private static final int q_powder_unholy_darkness = 13853;
    private static final int q_powder_holy_light = 13854;
    private static final int q_piece_holy_light = 13855;

    // zone_controller
    final int inzone_id = 118;

    public _10272_LightFragment() {
        super(false);
        addStartNpc(wharf_soldier_orbiu);
        addTalkId(warmage_artius, soldier_jinbi, silen_priest_relrikia, engineer_recon);
        addKillId(draconian_eliteguard, draconian_archmage, draconian_legatus, draconian_primus_plius, draconian_centurion, draconian_legionarii, draconian_officer_leader, draconian_officer, draconian_mage, draconian_berserker, draconian_healer, draconian_peltast, draconian_peltast_fix, draconian_rider_ret, draconian_archmage_ret, draconian_eliteguard_ret, draconian_legatus_ret, draconian_primus_p_ret, draconian_centurion_ret, draconian_healer_ret, draconian_legionarii_ret, draconian_mage_ret_fix, draconian_officer_l_ret, draconian_legatus_r_fix, draconian_officer_ret, draconian_officer_fix, draconian_mage_ret, draconian_berserker_ret, draconian_peltast_ret, draconian_peltast_r_fix, draconian_offi_l_r_fix, draconian_offi_r_fix, draconian_primus_p_r_fix, draconian_cen_r_fix, draconian_leg_r_fix, draconian_officer_l_fix, draconian_mage_fix, draconian_legatus_fix, draconian_primus_p_fix, draconian_centurion_fix, draconian_legionarii_fix, remnant_draco_mage, remnant_draco_prim, remnant_draco_cent, remnant_draco_legi, remnant_draco_offl, remnant_draco_offi, remnant_draco_heal, remnant_draco_pelt, remnant_draco_pelt_fix);
        addQuestItem(q_powder_unholy_darkness, q_powder_holy_light, q_piece_holy_light);
        addLevelCheck(75);
        addQuestCompletedCheck(10271);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("broken_piece_of_light");
        int npcId = npc.getNpcId();

        if (npcId == wharf_soldier_orbiu) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(q_doc_of_investigator) == 0)
                    st.giveItems(q_doc_of_investigator, 1);
                st.setCond(1);
                st.setMemoState("broken_piece_of_light", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "wharf_soldier_orbiu_q10272_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "wharf_soldier_orbiu_q10272_05.htm";
        } else if (npcId == warmage_artius) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1 && st.ownItemCount(q_doc_of_investigator) >= 1)
                    htmltext = "warmage_artius_q10272_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1 && st.ownItemCount(q_doc_of_investigator) >= 1) {
                    st.setCond(2);
                    st.setMemoState("broken_piece_of_light", String.valueOf(2), true);
                    st.takeItems(q_doc_of_investigator, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10272_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 2 && (st.getPlayer().getQuestState(10270).isCompleted() || st.get("pregnancy_of_seed") != null && st.getInt("pregnancy_of_seed") >= 10))
                    htmltext = "warmage_artius_q10272_05.htm";
                else if (GetMemoState == 2 && (st.get("pregnancy_of_seed") == null && !st.getPlayer().getQuestState(10270).isCompleted() || st.get("pregnancy_of_seed") != null && st.getInt("pregnancy_of_seed") < 10))
                    htmltext = "warmage_artius_q10272_06.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 2 && (st.getPlayer().getQuestState(10270).isCompleted() || st.get("pregnancy_of_seed") != null && st.getInt("pregnancy_of_seed") >= 10)) {
                    st.setCond(3);
                    st.setMemoState("broken_piece_of_light", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10272_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 2 && (st.get("pregnancy_of_seed") == null && !st.getPlayer().getQuestState(10270).isCompleted() || st.get("pregnancy_of_seed") != null && st.getInt("pregnancy_of_seed") < 10)) {
                    st.setCond(3);
                    st.setMemoState("broken_piece_of_light", String.valueOf(5), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10272_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (GetMemoState == 20)
                    htmltext = "warmage_artius_q10272_11.htm";

            } else if (event.equalsIgnoreCase("reply_22"))
                if (GetMemoState == 20) {
                    st.setCond(5);
                    st.setMemoState("broken_piece_of_light", String.valueOf(21), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warmage_artius_q10272_12.htm";
                }
        } else if (npcId == soldier_jinbi) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 5 && (st.getPlayer().getQuestState(10270).isCompleted() || st.get("pregnancy_of_seed") != null && st.getInt("pregnancy_of_seed") >= 10))
                    htmltext = "soldier_jinbi_q10272_03.htm";
                else if (GetMemoState == 5 && (st.get("pregnancy_of_seed") == null && !st.getPlayer().getQuestState(10270).isCompleted() || st.get("pregnancy_of_seed ") != null && st.getInt("pregnancy_of_seed") < 10))
                    htmltext = "soldier_jinbi_q10272_04.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(ADENA_ID) < 10000) {
                    if (GetMemoState == 5)
                        htmltext = "soldier_jinbi_q10272_04a.htm";
                } else if (GetMemoState == 5) {
                    st.takeItems(ADENA_ID, 10000);
                    st.setMemoState("broken_piece_of_light", String.valueOf(6), true);
                    htmltext = "soldier_jinbi_q10272_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 6)
                    htmltext = "soldier_jinbi_q10272_06.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState >= 6 && GetMemoState < 20) {
                    st.setMemoState("broken_piece_of_light", String.valueOf(10), true);
                    InstantZone_Enter(st.getPlayer(), inzone_id);
                    htmltext = "soldier_jinbi_q10272_07.htm";
                }
        } else if (npcId == silen_priest_relrikia) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 10)
                    htmltext = "silen_priest_relrikia_q10272_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 10)
                    htmltext = "silen_priest_relrikia_q10272_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 10) {
                    st.setCond(4);
                    st.setMemoState("broken_piece_of_light", String.valueOf(11), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "silen_priest_relrikia_q10272_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4"))
                if (GetMemoState == 11) {
                    st.setMemoState("broken_piece_of_light", String.valueOf(20), true);
                    st.getPlayer().getReflection().collapse();
                    htmltext = "silen_priest_relrikia_q10272_06.htm";
                }
        } else if (npcId == engineer_recon)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 24)
                    htmltext = "engineer_recon_q10272_02.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 24 && st.ownItemCount(q_powder_holy_light) >= 100) {
                    st.takeItems(q_powder_holy_light, 100);
                    st.setMemoState("broken_piece_of_light", String.valueOf(25), true);
                    htmltext = "engineer_recon_q10272_03.htm";
                } else if (GetMemoState == 24 && st.ownItemCount(q_powder_holy_light) < 100)
                    htmltext = "engineer_recon_q10272_04.htm";

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("broken_piece_of_light");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == wharf_soldier_orbiu) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "wharf_soldier_orbiu_q10272_03.htm";
                            break;
                        case QUEST:
                            htmltext = "wharf_soldier_orbiu_q10272_02.htm";
                            break;
                        default:
                            htmltext = "wharf_soldier_orbiu_q10272_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == wharf_soldier_orbiu) {
                    if (GetMemoState == 1)
                        htmltext = "wharf_soldier_orbiu_q10272_07.htm";
                } else if (npcId == warmage_artius) {
                    if (GetMemoState == 1 && st.ownItemCount(q_doc_of_investigator) >= 1)
                        htmltext = "warmage_artius_q10272_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "warmage_artius_q10272_04.htm";
                    else if (GetMemoState == 5)
                        htmltext = "warmage_artius_q10272_09.htm";
                    else if (GetMemoState == 20)
                        htmltext = "warmage_artius_q10272_10.htm";
                    else if (GetMemoState == 21 && st.ownItemCount(q_powder_unholy_darkness) < 1)
                        htmltext = "warmage_artius_q10272_13.htm";
                    else if (GetMemoState == 21 && st.ownItemCount(q_powder_unholy_darkness) >= 1 && st.ownItemCount(q_powder_unholy_darkness) < 100)
                        htmltext = "warmage_artius_q10272_14.htm";
                    else if (GetMemoState == 21 && st.ownItemCount(q_powder_unholy_darkness) >= 100) {
                        st.setCond(6);
                        st.setMemoState("broken_piece_of_light", String.valueOf(22), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "warmage_artius_q10272_15.htm";
                    } else if (GetMemoState == 22 && st.ownItemCount(q_powder_holy_light) < 100)
                        htmltext = "warmage_artius_q10272_16.htm";
                    else if (GetMemoState == 22 && st.ownItemCount(q_powder_holy_light) >= 100) {
                        st.setCond(7);
                        st.setMemoState("broken_piece_of_light", String.valueOf(24), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "warmage_artius_q10272_17.htm";
                    } else if (GetMemoState == 26) {
                        st.giveItems(ADENA_ID, 556980);
                        st.addExpAndSp(1009016, 91363);
                        st.removeMemo("broken_piece_of_light");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "warmage_artius_q10272_18.htm";
                    }
                } else if (npcId == soldier_jinbi) {
                    if (GetMemoState == 5)
                        htmltext = "soldier_jinbi_q10272_01.htm";
                    else if (GetMemoState < 5)
                        htmltext = "soldier_jinbi_q10272_02.htm";
                    else if (GetMemoState == 6)
                        htmltext = "soldier_jinbi_q10272_06a.htm";
                    else if (GetMemoState >= 10 && GetMemoState < 20)
                        htmltext = "soldier_jinbi_q10272_09.htm";
                    else if (GetMemoState == 20)
                        htmltext = "soldier_jinbi_q10272_11.htm";
                } else if (npcId == silen_priest_relrikia) {
                    if (GetMemoState == 10)
                        htmltext = "silen_priest_relrikia_q10272_01.htm";
                    else if (GetMemoState == 11)
                        htmltext = "silen_priest_relrikia_q10272_05.htm";
                } else if (npcId == engineer_recon)
                    if (GetMemoState == 24)
                        htmltext = "engineer_recon_q10272_01.htm";
                    else if (GetMemoState == 25) {
                        st.setCond(8);
                        st.setMemoState("broken_piece_of_light", String.valueOf(26), true);
                        st.giveItems(q_piece_holy_light, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "engineer_recon_q10272_05.htm";
                    } else if (GetMemoState == 26)
                        htmltext = "engineer_recon_q10272_06.htm";
                break;
            case COMPLETED:
                if (npcId == wharf_soldier_orbiu)
                    htmltext = "wharf_soldier_orbiu_q10272_04.htm";
                else if (npcId == warmage_artius)
                    htmltext = "warmage_artius_q10272_19.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("broken_piece_of_light");
        int npcId = npc.getNpcId();

        if (GetMemoState == 21)
            if (npcId == draconian_eliteguard) {
                int i0 = Rnd.get(1000);
                if (i0 < 348) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_archmage) {
                int i0 = Rnd.get(1000);
                if (i0 < 133) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_legatus) {
                int i0 = Rnd.get(1000);
                if (i0 < 548) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_primus_plius) {
                int i0 = Rnd.get(1000);
                if (i0 < 600) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_centurion) {
                int i0 = Rnd.get(1000);
                if (i0 < 250) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_legionarii) {
                int i0 = Rnd.get(1000);
                if (i0 < 293) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_officer_leader) {
                int i0 = Rnd.get(1000);
                if (i0 < 323) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_officer) {
                int i0 = Rnd.get(1000);
                if (i0 < 355) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_mage) {
                int i0 = Rnd.get(1000);
                if (i0 < 476) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_berserker) {
                int i0 = Rnd.get(1000);
                if (i0 < 402) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_healer) {
                int i0 = Rnd.get(1000);
                if (i0 < 7) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_peltast || npcId == draconian_peltast_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 610) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_rider_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 504) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_archmage_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 970) {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_eliteguard_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 545) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 5);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_legatus_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 428) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_primus_p_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 987) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_centurion_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 409) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_healer_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 686) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_legionarii_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 563) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_mage_ret_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 463) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_officer_l_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 446) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 5);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_legatus_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 439) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_officer_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 164) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_officer_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 355) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_mage_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 467) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_berserker_ret || npcId == draconian_peltast_ret) {
                int i0 = Rnd.get(1000);
                if (i0 < 585) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_peltast_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 804) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_offi_l_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 987) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 5);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_offi_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 274) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_primus_p_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 873) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_cen_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 409) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_leg_r_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 563) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_officer_l_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 323) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_mage_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 476) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_legatus_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 880) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_primus_p_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 987) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 5);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == draconian_centurion_fix || npcId == draconian_legionarii_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 423) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_mage) {
                int i0 = Rnd.get(1000);
                if (i0 < 401) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_prim) {
                int i0 = Rnd.get(1000);
                if (i0 < 689) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_cent) {
                int i0 = Rnd.get(1000);
                if (i0 < 428) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_legi) {
                int i0 = Rnd.get(1000);
                if (i0 < 589) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 3);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_offl) {
                int i0 = Rnd.get(1000);
                if (i0 < 474) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(5) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 4);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_offi) {
                int i0 = Rnd.get(1000);
                if (i0 < 173) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_heal) {
                int i0 = Rnd.get(1000);
                if (i0 < 996) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_pelt) {
                int i0 = Rnd.get(1000);
                if (i0 < 804) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == remnant_draco_pelt_fix) {
                int i0 = Rnd.get(1000);
                if (i0 < 987) {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_powder_unholy_darkness, Rnd.get(3) + 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }

        return null;
    }

    private void InstantZone_Enter(Player player, int inzone_id) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }
}
