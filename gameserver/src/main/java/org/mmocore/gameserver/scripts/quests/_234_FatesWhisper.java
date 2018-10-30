package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 04/03/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _234_FatesWhisper extends Quest {
    // npc
    private static final int maestro_leorin = 31002;
    private static final int cliff = 30182;
    private static final int head_blacksmith_ferris = 30847;
    private static final int zenkin = 30178;
    private static final int master_kaspar = 30833;
    private static final int coffer_of_the_dead = 31027;
    private static final int chest_of_kernon = 31028;
    private static final int chest_of_golkonda = 31029;
    private static final int chest_of_hallate = 31030;
    // mobs
    private static final int domb_death_cabrio = 25035;
    private static final int hallate_the_death_lord = 25220;
    private static final int kernon = 25054;
    private static final int golkonda_longhorn = 25126;
    private static final int baium = 29020;
    private static final int platinum_tribe_grunt = 20823;
    private static final int platinum_tribe_archer = 20826;
    private static final int platinum_tribe_warrior = 20827;
    private static final int platinum_tribe_shaman = 20828;
    private static final int platinum_tribe_lord = 20829;
    private static final int guardian_angel = 20830;
    private static final int seal_angel = 20831;
    private static final int seal_angel_r = 20860;
    // questitem
    private static final int q_pipette_knife = 4665;
    private static final int q_reirias_soulorb = 4666;
    private static final int q_infernium_scepter_1 = 4667;
    private static final int q_infernium_scepter_2 = 4668;
    private static final int q_infernium_scepter_3 = 4669;
    private static final int q_maestro_reorins_hammer = 4670;
    private static final int q_maestro_reorins_mold = 4671;
    private static final int q_infernium_varnish = 4672;
    private static final int q_red_pipette_knife = 4673;
    // etcitem
    private static final int q_star_of_destiny = 5011;
    private static final int crystal_b = 1460;
    private static final int q_bloody_fabric_q0234 = 14361;
    private static final int q_white_fabric_q0234 = 14362;
    // weapon b-grade
    private static final int sword_of_damascus = 79;
    private static final int sword_of_damascus_focus = 4717;
    private static final int sword_of_damascus_crtdamage = 4718;
    private static final int sword_of_damascus_haste = 4719;
    private static final int hazard_bow_guidence = 4828;
    private static final int hazard_bow_quickrecovery = 4829;
    private static final int hazard_bow_cheapshot = 4830;
    private static final int hazard_bow = 287;
    private static final int lancia_anger = 4858;
    private static final int lancia_crtstun = 4859;
    private static final int lancia_longblow = 4860;
    private static final int lancia = 97;
    private static final int art_of_battle_axe_health = 4753;
    private static final int art_of_battle_axe_rskfocus = 4754;
    private static final int art_of_battle_axe_haste = 4755;
    private static final int art_of_battle_axe = 175;
    private static final int staff_of_evil_sprit_magicfocus = 4900;
    private static final int staff_of_evil_sprit_magicblessthebody = 4901;
    private static final int staff_of_evil_sprit_magicpoison = 4902;
    private static final int staff_of_evil_sprit = 210;
    private static final int demons_sword_crtbleed = 4780;
    private static final int demons_sword_crtpoison = 4781;
    private static final int demons_sword_mightmotal = 4782;
    private static final int demons_sword = 234;
    private static final int demons_sword_crtdamage = 6359;
    private static final int bellion_cestus_crtdrain = 4804;
    private static final int bellion_cestus_crtpoison = 4805;
    private static final int bellion_cestus_rskhaste = 4806;
    private static final int bellion_cestus = 268;
    private static final int deadmans_glory_anger = 4750;
    private static final int deadmans_glory_health = 4751;
    private static final int deadmans_glory_haste = 4752;
    private static final int deadmans_glory = 171;
    private static final int samurai_longsword_samurai_longsword = 2626;
    private static final int guardians_sword = 7883;
    private static final int guardians_sword_crtdrain = 8105;
    private static final int guardians_sword_health = 8106;
    private static final int guardians_sword_crtbleed = 8107;
    private static final int tears_of_wizard = 7889;
    private static final int tears_of_wizard_acumen = 8117;
    private static final int tears_of_wizard_magicpower = 8118;
    private static final int tears_of_wizard_updown = 8119;
    private static final int star_buster = 7901;
    private static final int star_buster_health = 8132;
    private static final int star_buster_haste = 8133;
    private static final int star_buster_rskfocus = 8134;
    private static final int bone_of_kaim_vanul = 7893;
    private static final int bone_of_kaim_vanul_manaup = 8144;
    private static final int bone_of_kaim_vanul_magicsilence = 8145;
    private static final int bone_of_kaim_vanul_updown = 8146;
    // weapon a-grade
    private static final int tallum_blade = 80;
    private static final int inferno_master = 7884;
    private static final int carnium_bow = 288;
    private static final int halbard = 98;
    private static final int elemental_sword = 150;
    private static final int dasparions_staff = 212;
    private static final int eye_of_soul = 7894;
    private static final int bloody_orchid = 235;
    private static final int blood_tornado = 269;
    private static final int meteor_shower = 2504;
    private static final int hammer_of_destroyer = 7899;
    private static final int kshanberk_kshanberk = 5233;

    public _234_FatesWhisper() {
        super(true);
        addStartNpc(maestro_leorin);
        addTalkId(cliff, head_blacksmith_ferris, zenkin, master_kaspar, coffer_of_the_dead, chest_of_kernon, chest_of_golkonda, chest_of_hallate);
        addKillId(domb_death_cabrio, hallate_the_death_lord, kernon, golkonda_longhorn, platinum_tribe_grunt, platinum_tribe_archer, platinum_tribe_warrior, platinum_tribe_shaman, platinum_tribe_lord, guardian_angel, seal_angel, seal_angel_r);
        addAttackId(baium);
        addQuestItem(q_reirias_soulorb, q_infernium_scepter_3, q_infernium_scepter_1, q_infernium_scepter_2, q_infernium_varnish, q_maestro_reorins_hammer, q_maestro_reorins_mold, q_pipette_knife, q_red_pipette_knife);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("whispers_of_destiny");
        int npcId = npc.getNpcId();
        if (npcId == maestro_leorin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("whispers_of_destiny", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "maestro_leorin_q0234_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=1"))
                htmltext = "maestro_leorin_q0234_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=234&reply=2"))
                htmltext = "maestro_leorin_q0234_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=234&reply=3"))
                htmltext = "maestro_leorin_q0234_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=234&reply=4"))
                htmltext = "maestro_leorin_q0234_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=234&reply=5")) {
                if (GetMemoState == 1 && st.ownItemCount(q_reirias_soulorb) == 1) {
                    st.setCond(2);
                    st.setMemoState("whispers_of_destiny", String.valueOf(2), true);
                    st.takeItems(q_reirias_soulorb, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "maestro_leorin_q0234_11.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=6")) {
                if (GetMemoState == 2 && st.ownItemCount(q_infernium_scepter_1) >= 1 && st.ownItemCount(q_infernium_scepter_2) >= 1 && st.ownItemCount(q_infernium_scepter_3) >= 1) {
                    st.setCond(3);
                    st.setMemoState("whispers_of_destiny", String.valueOf(4), true);
                    st.takeItems(q_infernium_scepter_1, -1);
                    st.takeItems(q_infernium_scepter_2, -1);
                    st.takeItems(q_infernium_scepter_3, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "maestro_leorin_q0234_14.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=7")) {
                if (GetMemoState == 4 && st.ownItemCount(q_infernium_varnish) > 0) {
                    st.setCond(4);
                    st.setMemoState("whispers_of_destiny", String.valueOf(5), true);
                    st.takeItems(q_infernium_varnish, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "maestro_leorin_q0234_17.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=8")) {
                if (GetMemoState == 5 && st.ownItemCount(q_maestro_reorins_hammer) > 0) {
                    st.setCond(5);
                    st.setMemoState("whispers_of_destiny", String.valueOf(6), true);
                    st.takeItems(q_maestro_reorins_hammer, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "maestro_leorin_q0234_20.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=9")) {
                if (GetMemoState == 9 && st.ownItemCount(q_maestro_reorins_mold) > 0) {
                    st.setCond(11);
                    st.setMemoState("whispers_of_destiny", String.valueOf(10), true);
                    st.takeItems(q_maestro_reorins_mold, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "maestro_leorin_q0234_23.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=10")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(11), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_26.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=11")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(19), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_26a.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=12")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(12), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_27.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=13")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(13), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_28.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=14")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(14), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_29.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=15")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(15), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_30.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=16")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(16), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_31.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=17")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(17), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_32.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=18")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(18), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_33.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=41")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(41), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_33a.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=42")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(42), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_33b.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=43")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(43), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_33c.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=44")) {
                if (GetMemoState == 10) {
                    if (st.ownItemCount(crystal_b) >= 984) {
                        st.setCond(12);
                        st.setMemoState("whispers_of_destiny", String.valueOf(44), true);
                        st.takeItems(crystal_b, 984);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "maestro_leorin_q0234_33d.htm";
                    } else
                        htmltext = "maestro_leorin_q0234_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=21")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(tallum_blade, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_44.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=22")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(carnium_bow, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_45.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=23")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(halbard, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_46.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=24")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(elemental_sword, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_47.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=25")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(dasparions_staff, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_48.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=26")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(bloody_orchid, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_49.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=27")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(blood_tornado, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_50.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=28")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(meteor_shower, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_51.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=29")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(kshanberk_kshanberk, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_52.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=30")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(inferno_master, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_53.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=31")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(eye_of_soul, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_54.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=32")) {
                if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(sword_of_damascus) > 0) {
                        st.takeItems(sword_of_damascus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_focus) > 0) {
                        st.takeItems(sword_of_damascus_focus, 1);
                    } else if (st.ownItemCount(sword_of_damascus_crtdamage) > 0) {
                        st.takeItems(sword_of_damascus_crtdamage, 1);
                    } else if (st.ownItemCount(sword_of_damascus_haste) > 0) {
                        st.takeItems(sword_of_damascus_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(hazard_bow) > 0) {
                        st.takeItems(hazard_bow, 1);
                    } else if (st.ownItemCount(hazard_bow_guidence) > 0) {
                        st.takeItems(hazard_bow_guidence, 1);
                    } else if (st.ownItemCount(hazard_bow_quickrecovery) > 0) {
                        st.takeItems(hazard_bow_quickrecovery, 1);
                    } else if (st.ownItemCount(hazard_bow_cheapshot) > 0) {
                        st.takeItems(hazard_bow_cheapshot, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(lancia) > 0) {
                        st.takeItems(lancia, 1);
                    } else if (st.ownItemCount(lancia_anger) > 0) {
                        st.takeItems(lancia_anger, 1);
                    } else if (st.ownItemCount(lancia_crtstun) > 0) {
                        st.takeItems(lancia_crtstun, 1);
                    } else if (st.ownItemCount(lancia_longblow) > 0) {
                        st.takeItems(lancia_longblow, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(art_of_battle_axe) > 0) {
                        st.takeItems(art_of_battle_axe, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_health) > 0) {
                        st.takeItems(art_of_battle_axe_health, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_rskfocus) > 0) {
                        st.takeItems(art_of_battle_axe_rskfocus, 1);
                    } else if (st.ownItemCount(art_of_battle_axe_haste) > 0) {
                        st.takeItems(art_of_battle_axe_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(staff_of_evil_sprit) > 0) {
                        st.takeItems(staff_of_evil_sprit, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicfocus, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicblessthebody, 1);
                    } else if (st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0) {
                        st.takeItems(staff_of_evil_sprit_magicpoison, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(demons_sword) > 0) {
                        st.takeItems(demons_sword, 1);
                    } else if (st.ownItemCount(demons_sword_crtbleed) > 0) {
                        st.takeItems(demons_sword_crtbleed, 1);
                    } else if (st.ownItemCount(demons_sword_crtpoison) > 0) {
                        st.takeItems(demons_sword_crtpoison, 1);
                    } else if (st.ownItemCount(demons_sword_mightmotal) > 0) {
                        st.takeItems(demons_sword_mightmotal, 1);
                    } else if (st.ownItemCount(demons_sword_crtdamage) > 0) {
                        st.takeItems(demons_sword_crtdamage, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bellion_cestus) > 0) {
                        st.takeItems(bellion_cestus, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtdrain) > 0) {
                        st.takeItems(bellion_cestus_crtdrain, 1);
                    } else if (st.ownItemCount(bellion_cestus_crtpoison) > 0) {
                        st.takeItems(bellion_cestus_crtpoison, 1);
                    } else if (st.ownItemCount(bellion_cestus_rskhaste) > 0) {
                        st.takeItems(bellion_cestus_rskhaste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(deadmans_glory) > 0) {
                        st.takeItems(deadmans_glory, 1);
                    } else if (st.ownItemCount(deadmans_glory_anger) > 0) {
                        st.takeItems(deadmans_glory_anger, 1);
                    } else if (st.ownItemCount(deadmans_glory_health) > 0) {
                        st.takeItems(deadmans_glory_health, 1);
                    } else if (st.ownItemCount(deadmans_glory_haste) > 0) {
                        st.takeItems(deadmans_glory_haste, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    st.takeItems(samurai_longsword_samurai_longsword, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(guardians_sword) > 0) {
                        st.takeItems(guardians_sword, 1);
                    } else if (st.ownItemCount(guardians_sword_crtdrain) > 0) {
                        st.takeItems(guardians_sword_crtdrain, 1);
                    } else if (st.ownItemCount(guardians_sword_health) > 0) {
                        st.takeItems(guardians_sword_health, 1);
                    } else if (st.ownItemCount(guardians_sword_crtbleed) > 0) {
                        st.takeItems(guardians_sword_crtbleed, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(tears_of_wizard) > 0) {
                        st.takeItems(tears_of_wizard, 1);
                    } else if (st.ownItemCount(tears_of_wizard_acumen) > 0) {
                        st.takeItems(tears_of_wizard_acumen, 1);
                    } else if (st.ownItemCount(tears_of_wizard_magicpower) > 0) {
                        st.takeItems(tears_of_wizard_magicpower, 1);
                    } else if (st.ownItemCount(tears_of_wizard_updown) > 0) {
                        st.takeItems(tears_of_wizard_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(star_buster) > 0) {
                        st.takeItems(star_buster, 1);
                    } else if (st.ownItemCount(star_buster_health) > 0) {
                        st.takeItems(star_buster_health, 1);
                    } else if (st.ownItemCount(star_buster_haste) > 0) {
                        st.takeItems(star_buster_haste, 1);
                    } else if (st.ownItemCount(star_buster_rskfocus) > 0) {
                        st.takeItems(star_buster_rskfocus, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
                if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0)) {
                    st.giveItems(hammer_of_destroyer, 1);
                    st.giveItems(q_star_of_destiny, 1);
                    if (st.ownItemCount(bone_of_kaim_vanul) > 0) {
                        st.takeItems(bone_of_kaim_vanul, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_manaup) > 0) {
                        st.takeItems(bone_of_kaim_vanul_manaup, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0) {
                        st.takeItems(bone_of_kaim_vanul_magicsilence, 1);
                    } else if (st.ownItemCount(bone_of_kaim_vanul_updown) > 0) {
                        st.takeItems(bone_of_kaim_vanul_updown, 1);
                    }
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "maestro_leorin_q0234_55.htm";
                    st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                }
            }
        } else if (npcId == cliff) {
            if (event.equalsIgnoreCase("menu_select?ask=234&reply=1"))
                htmltext = "cliff_q0234_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=234&reply=2"))
                htmltext = "cliff_q0234_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=234&reply=3")) {
                if (GetMemoState == 4 && st.ownItemCount(q_infernium_varnish) == 0) {
                    st.giveItems(q_infernium_varnish, 1);
                    htmltext = "cliff_q0234_04.htm";
                }
            }
        } else if (npcId == zenkin) {
            if (event.equalsIgnoreCase("menu_select?ask=234&reply=1")) {
                st.setCond(6);
                st.setMemoState("whispers_of_destiny", String.valueOf(7), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "zenkin_q0234_02.htm";
            }
        } else if (npcId == master_kaspar) {
            if (event.equalsIgnoreCase("menu_select?ask=234&reply=1")) {
                if (GetMemoState == 7)
                    htmltext = "master_kaspar_q0234_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=2")) {
                if (GetMemoState == 7) {
                    st.setCond(7);
                    st.setMemoState("whispers_of_destiny", String.valueOf(8), true);
                    st.giveItems(q_pipette_knife, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_kaspar_q0234_03a.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=234&reply=3")) {
                if (GetMemoState == 7) {
                    st.setCond(8);
                    st.setMemoState("whispers_of_destiny", String.valueOf(8), true);
                    st.giveItems(q_white_fabric_q0234, 30);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_kaspar_q0234_03b.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("whispers_of_destiny");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == maestro_leorin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "maestro_leorin_q0234_01a.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "maestro_leorin_q0234_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == maestro_leorin) {
                    if (GetMemoState == 1 && st.ownItemCount(q_reirias_soulorb) == 0)
                        htmltext = "maestro_leorin_q0234_09.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_reirias_soulorb) == 1)
                        htmltext = "maestro_leorin_q0234_10.htm";
                    else if (GetMemoState == 2 && (st.ownItemCount(q_infernium_scepter_1) < 1 || st.ownItemCount(q_infernium_scepter_2) < 1 || st.ownItemCount(q_infernium_scepter_3) < 1))
                        htmltext = "maestro_leorin_q0234_12.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_infernium_scepter_1) >= 1 && st.ownItemCount(q_infernium_scepter_2) >= 1 && st.ownItemCount(q_infernium_scepter_3) >= 1)
                        htmltext = "maestro_leorin_q0234_13.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_infernium_varnish) == 0)
                        htmltext = "maestro_leorin_q0234_15.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_infernium_varnish) == 1)
                        htmltext = "maestro_leorin_q0234_16.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_maestro_reorins_hammer) == 0)
                        htmltext = "maestro_leorin_q0234_18.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_maestro_reorins_hammer) == 1)
                        htmltext = "maestro_leorin_q0234_19.htm";
                    else if (GetMemoState < 9 && GetMemoState >= 6)
                        htmltext = "maestro_leorin_q0234_21.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_maestro_reorins_mold) == 1)
                        htmltext = "maestro_leorin_q0234_22.htm";
                    else if (GetMemoState == 10 && st.ownItemCount(crystal_b) < 984)
                        htmltext = "maestro_leorin_q0234_24.htm";
                    else if (GetMemoState == 10 && st.ownItemCount(crystal_b) >= 984)
                        htmltext = "maestro_leorin_q0234_25.htm";
                    else if (GetMemoState == 11 && (st.ownItemCount(sword_of_damascus) > 0 || st.ownItemCount(sword_of_damascus_focus) > 0 || st.ownItemCount(sword_of_damascus_crtdamage) > 0 || st.ownItemCount(sword_of_damascus_haste) > 0))
                        htmltext = "maestro_leorin_q0234_35.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(sword_of_damascus) == 0 && st.ownItemCount(sword_of_damascus_focus) == 0 && st.ownItemCount(sword_of_damascus_crtdamage) == 0 && st.ownItemCount(sword_of_damascus_haste) == 0)
                        htmltext = "maestro_leorin_q0234_35a.htm";
                    else if (GetMemoState == 12 && (st.ownItemCount(hazard_bow_guidence) > 0 || st.ownItemCount(hazard_bow_quickrecovery) > 0 || st.ownItemCount(hazard_bow_cheapshot) > 0 || st.ownItemCount(hazard_bow) > 0))
                        htmltext = "maestro_leorin_q0234_36.htm";
                    else if (GetMemoState == 12 && st.ownItemCount(hazard_bow_guidence) == 0 && st.ownItemCount(hazard_bow_quickrecovery) == 0 && st.ownItemCount(hazard_bow_cheapshot) == 0 && st.ownItemCount(hazard_bow) == 0)
                        htmltext = "maestro_leorin_q0234_36a.htm";
                    else if (GetMemoState == 13 && (st.ownItemCount(lancia_anger) > 0 || st.ownItemCount(lancia_crtstun) > 0 || st.ownItemCount(lancia_longblow) > 0 || st.ownItemCount(lancia) > 0))
                        htmltext = "maestro_leorin_q0234_37.htm";
                    else if (GetMemoState == 13 && st.ownItemCount(lancia_anger) == 0 && st.ownItemCount(lancia_crtstun) == 0 && st.ownItemCount(lancia_longblow) == 0 && st.ownItemCount(lancia) == 0)
                        htmltext = "maestro_leorin_q0234_37a.htm";
                    else if (GetMemoState == 14 && (st.ownItemCount(art_of_battle_axe_health) > 0 || st.ownItemCount(art_of_battle_axe_rskfocus) > 0 || st.ownItemCount(art_of_battle_axe_haste) > 0 || st.ownItemCount(art_of_battle_axe) > 0))
                        htmltext = "maestro_leorin_q0234_38.htm";
                    else if (GetMemoState == 14 && st.ownItemCount(art_of_battle_axe) == 0 && st.ownItemCount(art_of_battle_axe_health) == 0 && st.ownItemCount(art_of_battle_axe_rskfocus) == 0 && st.ownItemCount(art_of_battle_axe_haste) == 0)
                        htmltext = "maestro_leorin_q0234_38a.htm";
                    else if (GetMemoState == 15 && (st.ownItemCount(staff_of_evil_sprit_magicfocus) > 0 || st.ownItemCount(staff_of_evil_sprit_magicblessthebody) > 0 || st.ownItemCount(staff_of_evil_sprit_magicpoison) > 0 || st.ownItemCount(staff_of_evil_sprit) > 0))
                        htmltext = "maestro_leorin_q0234_39.htm";
                    else if (GetMemoState == 15 && st.ownItemCount(staff_of_evil_sprit) == 0 && st.ownItemCount(staff_of_evil_sprit_magicfocus) == 0 && st.ownItemCount(staff_of_evil_sprit_magicblessthebody) == 0 && st.ownItemCount(staff_of_evil_sprit_magicpoison) == 0)
                        htmltext = "maestro_leorin_q0234_39a.htm";
                    else if (GetMemoState == 16 && (st.ownItemCount(demons_sword_crtbleed) > 0 || st.ownItemCount(demons_sword_crtpoison) > 0 || st.ownItemCount(demons_sword_mightmotal) > 0 || st.ownItemCount(demons_sword) > 0 || st.ownItemCount(demons_sword_crtdamage) > 0))
                        htmltext = "maestro_leorin_q0234_40.htm";
                    else if (GetMemoState == 16 && st.ownItemCount(demons_sword) == 0 && st.ownItemCount(demons_sword_crtbleed) == 0 && st.ownItemCount(demons_sword_crtpoison) == 0 && st.ownItemCount(demons_sword_mightmotal) == 0 && st.ownItemCount(demons_sword_crtdamage) == 0)
                        htmltext = "maestro_leorin_q0234_40a.htm";
                    else if (GetMemoState == 17 && (st.ownItemCount(bellion_cestus_crtdrain) > 0 || st.ownItemCount(bellion_cestus_crtpoison) > 0 || st.ownItemCount(bellion_cestus_rskhaste) > 0 || st.ownItemCount(bellion_cestus) > 0))
                        htmltext = "maestro_leorin_q0234_41.htm";
                    else if (GetMemoState == 17 && st.ownItemCount(bellion_cestus) == 0 && st.ownItemCount(bellion_cestus_crtdrain) == 0 && st.ownItemCount(bellion_cestus_crtpoison) == 0 && st.ownItemCount(bellion_cestus_rskhaste) == 0)
                        htmltext = "maestro_leorin_q0234_41a.htm";
                    else if (GetMemoState == 18 && (st.ownItemCount(deadmans_glory_anger) > 0 || st.ownItemCount(deadmans_glory_health) > 0 || st.ownItemCount(deadmans_glory_haste) > 0 || st.ownItemCount(deadmans_glory) > 0))
                        htmltext = "maestro_leorin_q0234_42.htm";
                    else if (GetMemoState == 18 && st.ownItemCount(deadmans_glory) == 0 && st.ownItemCount(deadmans_glory_anger) == 0 && st.ownItemCount(deadmans_glory_health) == 0 && st.ownItemCount(deadmans_glory_haste) == 0)
                        htmltext = "maestro_leorin_q0234_42a.htm";
                    else if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) > 0)
                        htmltext = "maestro_leorin_q0234_43.htm";
                    else if (GetMemoState == 19 && st.ownItemCount(samurai_longsword_samurai_longsword) == 0)
                        htmltext = "maestro_leorin_q0234_43a.htm";
                    else if (GetMemoState == 41 && (st.ownItemCount(guardians_sword) > 0 || st.ownItemCount(guardians_sword_crtdrain) > 0 || st.ownItemCount(guardians_sword_health) > 0 || st.ownItemCount(guardians_sword_crtbleed) > 0))
                        htmltext = "maestro_leorin_q0234_43b.htm";
                    else if (GetMemoState == 41 && st.ownItemCount(guardians_sword) == 0 && st.ownItemCount(guardians_sword_crtdrain) == 0 && st.ownItemCount(guardians_sword_health) == 0 && st.ownItemCount(guardians_sword_crtbleed) == 0)
                        htmltext = "maestro_leorin_q0234_43c.htm";
                    else if (GetMemoState == 42 && (st.ownItemCount(tears_of_wizard) > 0 || st.ownItemCount(tears_of_wizard_acumen) > 0 || st.ownItemCount(tears_of_wizard_magicpower) > 0 || st.ownItemCount(tears_of_wizard_updown) > 0))
                        htmltext = "maestro_leorin_q0234_43d.htm";
                    else if (GetMemoState == 42 && st.ownItemCount(tears_of_wizard) == 0 && st.ownItemCount(tears_of_wizard_acumen) == 0 && st.ownItemCount(tears_of_wizard_magicpower) == 0 && st.ownItemCount(tears_of_wizard_updown) == 0)
                        htmltext = "maestro_leorin_q0234_43e.htm";
                    else if (GetMemoState == 43 && (st.ownItemCount(star_buster) > 0 || st.ownItemCount(star_buster_health) > 0 || st.ownItemCount(star_buster_haste) > 0 || st.ownItemCount(star_buster_rskfocus) > 0))
                        htmltext = "maestro_leorin_q0234_43f.htm";
                    else if (GetMemoState == 43 && st.ownItemCount(star_buster) == 0 && st.ownItemCount(star_buster_health) == 0 && st.ownItemCount(star_buster_haste) == 0 && st.ownItemCount(star_buster_rskfocus) == 0)
                        htmltext = "maestro_leorin_q0234_43g.htm";
                    else if (GetMemoState == 44 && (st.ownItemCount(bone_of_kaim_vanul) > 0 || st.ownItemCount(bone_of_kaim_vanul_manaup) > 0 || st.ownItemCount(bone_of_kaim_vanul_magicsilence) > 0 || st.ownItemCount(bone_of_kaim_vanul_updown) > 0))
                        htmltext = "maestro_leorin_q0234_43h.htm";
                    else if (GetMemoState == 44 && st.ownItemCount(bone_of_kaim_vanul) == 0 && st.ownItemCount(bone_of_kaim_vanul_manaup) == 0 && st.ownItemCount(bone_of_kaim_vanul_magicsilence) == 0 && st.ownItemCount(bone_of_kaim_vanul_updown) == 0)
                        htmltext = "maestro_leorin_q0234_43i.htm";
                } else if (npcId == cliff) {
                    if (GetMemoState == 4 && st.ownItemCount(q_infernium_varnish) == 0)
                        htmltext = "cliff_q0234_01.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_infernium_varnish) == 1)
                        htmltext = "cliff_q0234_05.htm";
                    else if (GetMemoState >= 5)
                        htmltext = "cliff_q0234_06.htm";
                } else if (npcId == head_blacksmith_ferris) {
                    if (GetMemoState == 5 && st.ownItemCount(q_maestro_reorins_hammer) == 0) {
                        st.giveItems(q_maestro_reorins_hammer, 1);
                        htmltext = "head_blacksmith_ferris_q0234_01.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_maestro_reorins_hammer) == 1)
                        htmltext = "head_blacksmith_ferris_q0234_02.htm";
                    else if (GetMemoState >= 6)
                        htmltext = "head_blacksmith_ferris_q0234_03.htm";
                } else if (npcId == zenkin) {
                    if (GetMemoState == 6)
                        htmltext = "zenkin_q0234_01.htm";
                    else if (GetMemoState == 7)
                        htmltext = "zenkin_q0234_03.htm";
                    else if (GetMemoState >= 8)
                        htmltext = "zenkin_q0234_04.htm";
                } else if (npcId == master_kaspar) {
                    if (GetMemoState == 7)
                        htmltext = "master_kaspar_q0234_01.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q_red_pipette_knife) == 0 && st.ownItemCount(q_bloody_fabric_q0234) + st.ownItemCount(q_white_fabric_q0234) == 0)
                        htmltext = "master_kaspar_q0234_03.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q_red_pipette_knife) == 1 && st.ownItemCount(q_bloody_fabric_q0234) + st.ownItemCount(q_white_fabric_q0234) == 0) {
                        st.setCond(10);
                        st.setMemoState("whispers_of_destiny", String.valueOf(9), true);
                        st.giveItems(q_maestro_reorins_mold, 1);
                        st.takeItems(q_red_pipette_knife, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_kaspar_q0234_04.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_red_pipette_knife) == 0 && st.ownItemCount(q_bloody_fabric_q0234) < 30 && st.ownItemCount(q_bloody_fabric_q0234) + st.ownItemCount(q_white_fabric_q0234) >= 30)
                        htmltext = "master_kaspar_q0234_03c.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q_red_pipette_knife) == 0 && st.ownItemCount(q_bloody_fabric_q0234) >= 30 && st.ownItemCount(q_bloody_fabric_q0234) + st.ownItemCount(q_white_fabric_q0234) >= 30) {
                        st.setCond(10);
                        st.setMemoState("whispers_of_destiny", String.valueOf(9), true);
                        st.giveItems(q_maestro_reorins_mold, 1);
                        st.takeItems(q_bloody_fabric_q0234, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_kaspar_q0234_03d.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_red_pipette_knife) == 0 && st.ownItemCount(q_bloody_fabric_q0234) + st.ownItemCount(q_white_fabric_q0234) < 30 && st.ownItemCount(q_bloody_fabric_q0234) + st.ownItemCount(q_white_fabric_q0234) > 0) {
                        st.giveItems(q_white_fabric_q0234, 30 - st.ownItemCount(q_white_fabric_q0234));
                        st.takeItems(q_bloody_fabric_q0234, -1);
                        htmltext = "master_kaspar_q0234_03e.htm";
                    } else if (GetMemoState >= 9)
                        htmltext = "master_kaspar_q0234_05.htm";
                } else if (npcId == coffer_of_the_dead) {
                    if (GetMemoState == 1 && st.ownItemCount(q_reirias_soulorb) == 0) {
                        st.giveItems(q_reirias_soulorb, 1);
                        st.soundEffect(SOUND_ITEMGET);
                        htmltext = "coffer_of_the_dead_q0234_01.htm";
                    } else if (GetMemoState > 1 || st.ownItemCount(q_reirias_soulorb) == 1)
                        htmltext = "coffer_of_the_dead_q0234_02.htm";
                } else if (npcId == chest_of_kernon) {
                    if (GetMemoState == 2 && st.ownItemCount(q_infernium_scepter_1) == 0) {
                        st.giveItems(q_infernium_scepter_1, 1);
                        st.soundEffect(SOUND_ITEMGET);
                        htmltext = "chest_of_kernon_q0234_01.htm";
                    } else if (GetMemoState != 2 || st.ownItemCount(q_infernium_scepter_1) == 1)
                        htmltext = "chest_of_kernon_q0234_02.htm";
                } else if (npcId == chest_of_golkonda) {
                    if (GetMemoState == 2 && st.ownItemCount(q_infernium_scepter_2) == 0) {
                        st.giveItems(q_infernium_scepter_2, 1);
                        st.soundEffect(SOUND_ITEMGET);
                        htmltext = "chest_of_golkonda_q0234_01.htm";
                    } else if (GetMemoState != 2 || st.ownItemCount(q_infernium_scepter_2) == 1)
                        htmltext = "chest_of_golkonda_q0234_02.htm";
                } else if (npcId == chest_of_hallate) {
                    if (GetMemoState == 2 && st.ownItemCount(q_infernium_scepter_3) == 0) {
                        st.giveItems(q_infernium_scepter_3, 1);
                        st.soundEffect(SOUND_ITEMGET);
                        htmltext = "chest_of_hallate_q0234_01.htm";
                    } else if (GetMemoState != 2 || st.ownItemCount(q_infernium_scepter_3) == 1)
                        htmltext = "chest_of_hallate_q0234_02.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("whispers_of_destiny");
        int npcId = npc.getNpcId();
        if (npcId == domb_death_cabrio)
            st.addSpawn(coffer_of_the_dead, npc.getX(), npc.getY(), npc.getZ(), 120000);
        else if (npcId == hallate_the_death_lord)
            st.addSpawn(chest_of_hallate, npc.getX(), npc.getY(), npc.getZ(), 120000);
        else if (npcId == kernon)
            st.addSpawn(chest_of_kernon, npc.getX(), npc.getY(), npc.getZ(), 120000);
        else if (npcId == golkonda_longhorn)
            st.addSpawn(chest_of_golkonda, npc.getX(), npc.getY(), npc.getZ(), 120000);
        else if (npcId == seal_angel_r || npcId == platinum_tribe_grunt || npcId == platinum_tribe_archer || npcId == platinum_tribe_warrior || npcId == platinum_tribe_shaman || npcId == platinum_tribe_lord || npcId == guardian_angel || npcId == seal_angel) {
            if (GetMemoState == 8 && st.ownItemCount(q_white_fabric_q0234) > 0) {
                st.giveItems(q_bloody_fabric_q0234, 1);
                st.takeItems(q_white_fabric_q0234, 1);
                if (st.ownItemCount(q_bloody_fabric_q0234) >= 30) {
                    st.setCond(9);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() == baium && st.ownItemCount(q_pipette_knife) >= 1 && st.ownItemCount(q_red_pipette_knife) == 0 && st.getItemEquipped(Inventory.PAPERDOLL_RHAND) == q_pipette_knife) {
            st.takeItems(q_pipette_knife, 1);
            st.giveItems(q_red_pipette_knife, 1);
            st.soundEffect(SOUND_ITEMGET);
            Functions.npcSay(npc, NpcString.WHO_DARES_TO_AND_STEAL_MY_NOBLE_BLOOD);
        }
        return null;
    }
}