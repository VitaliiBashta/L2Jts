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
public class _373_SupplierOfReagents extends Quest {
    // npc
    private static final int bandor = 30166;
    private static final int alchemical_mixing_jar = 31149;

    // mobs
    private static final int credion = 20813;
    private static final int hallates_maiden = 20822;
    private static final int platinum_tribe_shaman = 20828;
    private static final int hallates_guardian = 21061;
    private static final int plat_protect_shaman = 21066;
    private static final int lava_wyrm = 21111;
    private static final int hames_orc_shaman = 21115;

    // etcitem
    private static final int mixing_manual = 6317;
    private static final int dracoplasm = 6021;
    private static final int swift_attack_potion = 735;
    private static final int magma_dust = 6022;
    private static final int scroll_of_resurrection = 737;
    private static final int moon_dust = 6023;
    private static final int cursed_bone = 2508;
    private static final int necroplasm = 6024;
    private static final int enria = 4042;
    private static final int asofe = 4043;
    private static final int thons = 4044;
    private static final int demonplasm = 6025;
    private static final int rp_avadon_gloves_i = 4953;
    private static final int rp_avadon_boots_i = 4959;
    private static final int rp_shrnoens_gauntlet_i = 4960;
    private static final int rp_shrnoens_boots_i = 4958;
    private static final int inferno_dust = 6026;
    private static final int rp_blue_wolves_gloves_i = 4998;
    private static final int rp_blue_wolves_boots_i = 4992;
    private static final int draconic_essence = 6027;
    private static final int fire_essence = 6028;
    private static final int rp_doom_gloves_i = 4993;
    private static final int rp_doom_boots_i = 4999;
    private static final int lunargent = 6029;
    private static final int sealed_dark_crystal_leather_mail_pattern = 5478;
    private static final int sealed_tallum_leather_mail_pattern = 5479;
    private static final int sealed_leather_mail_of_nightmare_fabric = 5480;
    private static final int sealed_majestic_leather_mail_fabric = 5481;
    private static final int sealed_legging_of_dark_crystal_design = 5482;
    private static final int midnight_oil = 6030;
    private static final int sealed_dark_crystal_breastplate_pattern = 5520;
    private static final int sealed_tallum_plate_armor_pattern = 5521;
    private static final int sealed_armor_of_nightmare_pattern = 5522;
    private static final int sealed_majestic_platte_armor_pattern = 5523;
    private static final int sealed_dark_crystal_gaiters_pattern = 5524;
    private static final int demonic_essence = 6031;
    private static final int abyss_oil = 6032;
    private static final int hellfire_oil = 6033;
    private static final int nightmare_oil = 6034;
    private static final int wyrms_blood = 6011;
    private static final int lava_stone = 6012;
    private static final int moonstone_shard = 6013;
    private static final int decaying_bone = 6014;
    private static final int demons_blood = 6015;
    private static final int infernium_ore = 6016;
    private static final int blood_root = 6017;
    private static final int volcanic_ash = 6018;
    private static final int quicksilver = 6019;
    private static final int sulfur = 6020;
    private static final int ingredient_pouch1 = 6007;
    private static final int ingredient_pouch2 = 6008;
    private static final int ingredient_pouch3 = 6009;
    private static final int ingredient_box = 6010;

    // armor
    private static final int tower_shield = 103;
    private static final int drake_leather_boots = 2437;
    private static final int square_shield = 630;
    private static final int shrnoens_gauntlet = 612;
    private static final int avadon_gloves = 2464;
    private static final int shrnoens_boots = 554;
    private static final int avadon_boots = 600;
    private static final int blue_wolves_boots = 2439;
    private static final int doom_boots = 601;
    private static final int blue_wolves_gloves = 2487;
    private static final int doom_gloves = 2475;

    // questitem
    private static final int wesleys_mixing_stone = 5904;
    private static final int pure_silver = 6320;

    public _373_SupplierOfReagents() {
        super(true);
        addStartNpc(bandor);
        addTalkId(alchemical_mixing_jar);
        addKillId(credion, hallates_maiden, platinum_tribe_shaman, hallates_guardian, plat_protect_shaman, lava_wyrm, hames_orc_shaman);
        addLevelCheck(57, 75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("reagent_supplier");
        int GetMemoStateEx = st.getInt("reagent_supplier_ex");

        if (npcId == bandor) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.giveItems(wesleys_mixing_stone, 1);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "bandor_q0373_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "bandor_q0373_03.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "bandor_q0373_06.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "bandor_q0373_07.htm";
            else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "bandor_q0373_08.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                st.takeItems(wesleys_mixing_stone, -1);
                st.takeItems(mixing_manual, -1);
                st.removeMemo("reagent_supplier");
                st.removeMemo("reagent_supplier_ex");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "bandor_q0373_09.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (st.ownItemCount(dracoplasm) >= 1) {
                    st.giveItems(swift_attack_potion, 1);
                    st.takeItems(dracoplasm, 1);
                    htmltext = "bandor_q0373_10.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (st.ownItemCount(magma_dust) >= 1) {

                    st.giveItems(scroll_of_resurrection, 2);
                    st.takeItems(magma_dust, 1);
                    htmltext = "bandor_q0373_11.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (st.ownItemCount(moon_dust) >= 1) {

                    st.giveItems(cursed_bone, 25);
                    st.takeItems(moon_dust, 1);
                    htmltext = "bandor_q0373_12.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_14")) {
                if (st.ownItemCount(necroplasm) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 32)
                        st.giveItems(enria, 1);
                    else if (i0 < 66)
                        st.giveItems(asofe, 1);
                    else
                        st.giveItems(thons, 1);
                    st.giveItems(ADENA_ID, 500);
                    st.takeItems(necroplasm, 1);
                    htmltext = "bandor_q0373_13.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_15")) {
                if (st.ownItemCount(demonplasm) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 28)
                        st.giveItems(swift_attack_potion, 20);
                    else if (i0 < 68)
                        st.giveItems(rp_avadon_gloves_i, 1);
                    else
                        st.giveItems(rp_avadon_boots_i, 1);
                    st.takeItems(demonplasm, 1);
                    htmltext = "bandor_q0373_14.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_16")) {
                if (st.ownItemCount(inferno_dust) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 40)
                        st.giveItems(cursed_bone, 200);
                    else if (i0 < 70)
                        st.giveItems(rp_shrnoens_gauntlet_i, 1);
                    else
                        st.giveItems(rp_shrnoens_boots_i, 1);
                    st.takeItems(inferno_dust, 1);
                    htmltext = "bandor_q0373_15.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_17")) {
                if (st.ownItemCount(draconic_essence) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 40)
                        st.giveItems(rp_blue_wolves_gloves_i, 1);
                    else if (i0 < 80)
                        st.giveItems(rp_blue_wolves_boots_i, 1);
                    else
                        st.giveItems(scroll_of_resurrection, 20);
                    st.takeItems(draconic_essence, 1);
                    htmltext = "bandor_q0373_16.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_18")) {
                if (st.ownItemCount(fire_essence) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 90) {
                        st.giveItems(rp_doom_gloves_i, 1);
                        st.giveItems(rp_doom_boots_i, 1);
                    } else
                        st.giveItems(enria, 2);
                    st.takeItems(fire_essence, 1);
                    htmltext = "bandor_q0373_17.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_19")) {
                if (st.ownItemCount(lunargent) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 20)
                        st.giveItems(sealed_dark_crystal_leather_mail_pattern, 2);
                    else if (i0 < 40)
                        st.giveItems(sealed_tallum_leather_mail_pattern, 2);
                    else if (i0 < 60)
                        st.giveItems(sealed_leather_mail_of_nightmare_fabric, 2);
                    else if (i0 < 80)
                        st.giveItems(sealed_majestic_leather_mail_fabric, 2);
                    else
                        st.giveItems(sealed_legging_of_dark_crystal_design, 2);
                    st.giveItems(ADENA_ID, 8000);
                    st.takeItems(lunargent, 1);
                    htmltext = "bandor_q0373_18.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (st.ownItemCount(midnight_oil) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 20)
                        st.giveItems(sealed_dark_crystal_breastplate_pattern, 3);
                    else if (i0 < 40)
                        st.giveItems(sealed_tallum_plate_armor_pattern, 3);
                    else if (i0 < 60)
                        st.giveItems(sealed_armor_of_nightmare_pattern, 3);
                    else if (i0 < 80)
                        st.giveItems(sealed_majestic_platte_armor_pattern, 3);
                    else
                        st.giveItems(sealed_dark_crystal_gaiters_pattern, 3);
                    st.giveItems(ADENA_ID, 8000);
                    st.takeItems(midnight_oil, 1);
                    htmltext = "bandor_q0373_19.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (st.ownItemCount(demonic_essence) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 40)
                        st.giveItems(ADENA_ID, 320000);
                    else if (i0 < 80)
                        st.giveItems(ADENA_ID, 245000);
                    else
                        st.giveItems(ADENA_ID, 160000);
                    st.takeItems(demonic_essence, 1);
                    htmltext = "bandor_q0373_20.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_22")) {
                if (st.ownItemCount(abyss_oil) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 30)
                        st.giveItems(tower_shield, 1);
                    else if (i0 < 60)
                        st.giveItems(drake_leather_boots, 1);
                    else
                        st.giveItems(square_shield, 1);
                    st.giveItems(ADENA_ID, 5000);
                    st.takeItems(abyss_oil, 1);
                    htmltext = "bandor_q0373_21.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_23")) {
                if (st.ownItemCount(hellfire_oil) >= 1) {

                    int i0 = Rnd.get(100);
                    if (i0 < 25)
                        st.giveItems(shrnoens_gauntlet, 1);
                    else if (i0 < 50)
                        st.giveItems(avadon_gloves, 1);
                    else if (i0 < 75)
                        st.giveItems(shrnoens_boots, 1);
                    else
                        st.giveItems(avadon_boots, 1);
                    st.takeItems(hellfire_oil, 1);
                    htmltext = "bandor_q0373_22.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
            } else if (event.equalsIgnoreCase("reply_24"))
                if (st.ownItemCount(nightmare_oil) >= 1) {
                    int i0 = Rnd.get(100);
                    if (i0 < 17)
                        st.giveItems(blue_wolves_boots, 1);
                    else if (i0 < 34)
                        st.giveItems(doom_boots, 1);
                    else if (i0 < 51)
                        st.giveItems(blue_wolves_gloves, 1);
                    else if (i0 < 68)
                        st.giveItems(doom_gloves, 1);
                    else {
                        st.giveItems(rp_blue_wolves_boots_i, 1);
                        st.giveItems(rp_blue_wolves_gloves_i, 1);
                    }
                    st.giveItems(ADENA_ID, 19000);
                    st.takeItems(nightmare_oil, 1);
                    htmltext = "bandor_q0373_23.htm";
                } else
                    htmltext = "bandor_q0373_24.htm";
        } else if (npcId == alchemical_mixing_jar)
            if (event.equalsIgnoreCase("reply_1")) {
                st.setMemoState("reagent_supplier", String.valueOf(0), true);
                st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                htmltext = "alchemical_mixing_jar_q0373_02.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (st.ownItemCount(wyrms_blood) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(11), true);
                    htmltext = "alchemical_mixing_jar_q0373_03.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (st.ownItemCount(lava_stone) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(12), true);
                    htmltext = "alchemical_mixing_jar_q0373_04.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (st.ownItemCount(moonstone_shard) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(13), true);
                    htmltext = "alchemical_mixing_jar_q0373_05.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_14")) {
                if (st.ownItemCount(decaying_bone) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(14), true);
                    htmltext = "alchemical_mixing_jar_q0373_06.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_15")) {
                if (st.ownItemCount(demons_blood) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(15), true);
                    htmltext = "alchemical_mixing_jar_q0373_07.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_16")) {
                if (st.ownItemCount(infernium_ore) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(16), true);
                    htmltext = "alchemical_mixing_jar_q0373_08.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_17")) {
                if (st.ownItemCount(dracoplasm) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(17), true);
                    htmltext = "alchemical_mixing_jar_q0373_09.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_18")) {
                if (st.ownItemCount(magma_dust) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(18), true);
                    htmltext = "alchemical_mixing_jar_q0373_10.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_19")) {
                if (st.ownItemCount(moon_dust) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(19), true);
                    htmltext = "alchemical_mixing_jar_q0373_11.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_20")) {
                if (st.ownItemCount(necroplasm) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(20), true);
                    htmltext = "alchemical_mixing_jar_q0373_12.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (st.ownItemCount(demonplasm) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(21), true);
                    htmltext = "alchemical_mixing_jar_q0373_13.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_22")) {
                if (st.ownItemCount(inferno_dust) >= 10) {
                    st.setMemoState("reagent_supplier", String.valueOf(22), true);
                    htmltext = "alchemical_mixing_jar_q0373_14.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_23")) {
                if (st.ownItemCount(fire_essence) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(23), true);
                    htmltext = "alchemical_mixing_jar_q0373_15.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_24")) {
                if (st.ownItemCount(lunargent) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(24), true);
                    htmltext = "alchemical_mixing_jar_q0373_16.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "alchemical_mixing_jar_q0373_18.htm";
            else if (event.equalsIgnoreCase("reply_31")) {
                if (st.ownItemCount(blood_root) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(GetMemoState + 1100), true);
                    htmltext = "alchemical_mixing_jar_q0373_19.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else {
                    if (GetMemoState == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (GetMemoState == 12)
                        st.takeItems(lava_stone, 10);
                    else if (GetMemoState == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (GetMemoState == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (GetMemoState == 15)
                        st.takeItems(demons_blood, 10);
                    else if (GetMemoState == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (GetMemoState == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (GetMemoState == 18)
                        st.takeItems(magma_dust, 10);
                    else if (GetMemoState == 19)
                        st.takeItems(moon_dust, 10);
                    else if (GetMemoState == 20)
                        st.takeItems(necroplasm, 10);
                    else if (GetMemoState == 21)
                        st.takeItems(demonplasm, 10);
                    else if (GetMemoState == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (GetMemoState == 23)
                        st.takeItems(fire_essence, 1);
                    else if (GetMemoState == 24)
                        st.takeItems(lunargent, 1);
                    htmltext = "alchemical_mixing_jar_q0373_25.htm";
                }
            } else if (event.equalsIgnoreCase("reply_32")) {
                if (st.ownItemCount(volcanic_ash) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(GetMemoState + 1200), true);
                    htmltext = "alchemical_mixing_jar_q0373_20.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else {
                    if (GetMemoState == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (GetMemoState == 12)
                        st.takeItems(lava_stone, 10);
                    else if (GetMemoState == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (GetMemoState == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (GetMemoState == 15)
                        st.takeItems(demons_blood, 10);
                    else if (GetMemoState == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (GetMemoState == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (GetMemoState == 18)
                        st.takeItems(magma_dust, 10);
                    else if (GetMemoState == 19)
                        st.takeItems(moon_dust, 10);
                    else if (GetMemoState == 20)
                        st.takeItems(necroplasm, 10);
                    else if (GetMemoState == 21)
                        st.takeItems(demonplasm, 10);
                    else if (GetMemoState == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (GetMemoState == 23)
                        st.takeItems(fire_essence, 1);
                    else if (GetMemoState == 24)
                        st.takeItems(lunargent, 1);
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_33")) {
                if (st.ownItemCount(quicksilver) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(GetMemoState + 1300), true);
                    htmltext = "alchemical_mixing_jar_q0373_21.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else {
                    if (GetMemoState == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (GetMemoState == 12)
                        st.takeItems(lava_stone, 10);
                    else if (GetMemoState == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (GetMemoState == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (GetMemoState == 15)
                        st.takeItems(demons_blood, 10);
                    else if (GetMemoState == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (GetMemoState == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (GetMemoState == 18)
                        st.takeItems(magma_dust, 10);
                    else if (GetMemoState == 19)
                        st.takeItems(moon_dust, 10);
                    else if (GetMemoState == 20)
                        st.takeItems(necroplasm, 10);
                    else if (GetMemoState == 21)
                        st.takeItems(demonplasm, 10);
                    else if (GetMemoState == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (GetMemoState == 23)
                        st.takeItems(fire_essence, 1);
                    else if (GetMemoState == 24)
                        st.takeItems(lunargent, 1);
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_34")) {
                if (st.ownItemCount(sulfur) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(GetMemoState + 1400), true);
                    htmltext = "alchemical_mixing_jar_q0373_22.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else {
                    if (GetMemoState == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (GetMemoState == 12)
                        st.takeItems(lava_stone, 10);
                    else if (GetMemoState == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (GetMemoState == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (GetMemoState == 15)
                        st.takeItems(demons_blood, 10);
                    else if (GetMemoState == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (GetMemoState == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (GetMemoState == 18)
                        st.takeItems(magma_dust, 10);
                    else if (GetMemoState == 19)
                        st.takeItems(moon_dust, 10);
                    else if (GetMemoState == 20)
                        st.takeItems(necroplasm, 10);
                    else if (GetMemoState == 21)
                        st.takeItems(demonplasm, 10);
                    else if (GetMemoState == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (GetMemoState == 23)
                        st.takeItems(fire_essence, 1);
                    else if (GetMemoState == 24)
                        st.takeItems(lunargent, 1);
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_35")) {
                if (st.ownItemCount(demonic_essence) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(GetMemoState + 1500), true);
                    htmltext = "alchemical_mixing_jar_q0373_23.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else {
                    if (GetMemoState == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (GetMemoState == 12)
                        st.takeItems(lava_stone, 10);
                    else if (GetMemoState == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (GetMemoState == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (GetMemoState == 15)
                        st.takeItems(demons_blood, 10);
                    else if (GetMemoState == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (GetMemoState == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (GetMemoState == 18)
                        st.takeItems(magma_dust, 10);
                    else if (GetMemoState == 19)
                        st.takeItems(moon_dust, 10);
                    else if (GetMemoState == 20)
                        st.takeItems(necroplasm, 10);
                    else if (GetMemoState == 21)
                        st.takeItems(demonplasm, 10);
                    else if (GetMemoState == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (GetMemoState == 23)
                        st.takeItems(fire_essence, 1);
                    else if (GetMemoState == 24)
                        st.takeItems(lunargent, 1);
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_36")) {
                if (st.ownItemCount(midnight_oil) >= 1) {
                    st.setMemoState("reagent_supplier", String.valueOf(GetMemoState + 1600), true);
                    htmltext = "alchemical_mixing_jar_q0373_24.htm";
                    st.soundEffect(SOUND_LIQUID_MIX_01);
                } else {
                    if (GetMemoState == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (GetMemoState == 12)
                        st.takeItems(lava_stone, 10);
                    else if (GetMemoState == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (GetMemoState == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (GetMemoState == 15)
                        st.takeItems(demons_blood, 10);
                    else if (GetMemoState == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (GetMemoState == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (GetMemoState == 18)
                        st.takeItems(magma_dust, 10);
                    else if (GetMemoState == 19)
                        st.takeItems(moon_dust, 10);
                    else if (GetMemoState == 20)
                        st.takeItems(necroplasm, 10);
                    else if (GetMemoState == 21)
                        st.takeItems(demonplasm, 10);
                    else if (GetMemoState == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (GetMemoState == 23)
                        st.takeItems(fire_essence, 1);
                    else if (GetMemoState == 24)
                        st.takeItems(lunargent, 1);
                    htmltext = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1324)
                    htmltext = "alchemical_mixing_jar_q0373_26a.htm";
                else
                    htmltext = "alchemical_mixing_jar_q0373_26.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                st.setMemoState("reagent_supplier_ex", String.valueOf(1), true);
                htmltext = "alchemical_mixing_jar_q0373_27.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (Rnd.get(100) < 33) {
                    st.setMemoState("reagent_supplier_ex", String.valueOf(3), true);
                    htmltext = "alchemical_mixing_jar_q0373_28a.htm";
                } else {
                    st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                    htmltext = "alchemical_mixing_jar_q0373_28a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (Rnd.get(100) < 20) {
                    st.setMemoState("reagent_supplier_ex", String.valueOf(5), true);
                    htmltext = "alchemical_mixing_jar_q0373_29a.htm";
                } else {
                    st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                    htmltext = "alchemical_mixing_jar_q0373_29a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7") && GetMemoStateEx != 0) {
                if (GetMemoState == 1111) {
                    if (st.ownItemCount(wyrms_blood) >= 10 && st.ownItemCount(blood_root) >= 1) {
                        st.takeItems(wyrms_blood, 10);
                        st.takeItems(blood_root, 1);
                        st.giveItems(dracoplasm, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_30.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1212) {
                    if (st.ownItemCount(lava_stone) >= 10 && st.ownItemCount(volcanic_ash) >= 1) {
                        st.takeItems(lava_stone, 10);
                        st.takeItems(volcanic_ash, 1);
                        st.giveItems(magma_dust, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_31.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1213) {
                    if (st.ownItemCount(moonstone_shard) >= 10 && st.ownItemCount(volcanic_ash) >= 1) {

                        st.takeItems(moonstone_shard, 10);
                        st.takeItems(volcanic_ash, 1);
                        st.giveItems(moon_dust, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_32.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1114) {
                    if (st.ownItemCount(decaying_bone) >= 10 && st.ownItemCount(blood_root) >= 1) {
                        st.takeItems(decaying_bone, 10);
                        st.takeItems(blood_root, 1);
                        st.giveItems(necroplasm, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_33.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1115) {
                    if (st.ownItemCount(demons_blood) >= 10 && st.ownItemCount(blood_root) >= 1) {

                        st.takeItems(demons_blood, 10);
                        st.takeItems(blood_root, 1);
                        st.giveItems(demonplasm, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_34.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1216) {
                    if (st.ownItemCount(infernium_ore) >= 10 && st.ownItemCount(volcanic_ash) >= 1) {

                        st.takeItems(infernium_ore, 10);
                        st.takeItems(volcanic_ash, 1);
                        st.giveItems(inferno_dust, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_35.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1317) {
                    if (st.ownItemCount(dracoplasm) >= 10 && st.ownItemCount(quicksilver) >= 1) {
                        st.takeItems(dracoplasm, 10);
                        st.takeItems(quicksilver, 1);
                        st.giveItems(draconic_essence, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_36.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1418) {
                    if (st.ownItemCount(magma_dust) >= 10 && st.ownItemCount(sulfur) >= 1) {
                        st.takeItems(magma_dust, 10);
                        st.takeItems(sulfur, 1);
                        st.giveItems(fire_essence, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_37.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1319) {
                    if (st.ownItemCount(moon_dust) >= 10 && st.ownItemCount(quicksilver) >= 1) {

                        st.takeItems(moon_dust, 10);
                        st.takeItems(quicksilver, 1);
                        st.giveItems(lunargent, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_38.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1320) {
                    if (st.ownItemCount(necroplasm) >= 10 && st.ownItemCount(quicksilver) >= 1) {
                        st.takeItems(necroplasm, 10);
                        st.takeItems(quicksilver, 1);
                        st.giveItems(midnight_oil, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_39.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1421) {
                    if (st.ownItemCount(demonplasm) >= 10 && st.ownItemCount(sulfur) >= 1) {

                        st.takeItems(demonplasm, 10);
                        st.takeItems(sulfur, 1);
                        st.giveItems(demonic_essence, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_40.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1422) {
                    if (st.ownItemCount(inferno_dust) >= 10 && st.ownItemCount(sulfur) >= 1) {

                        st.takeItems(inferno_dust, 10);
                        st.takeItems(sulfur, 1);
                        st.giveItems(abyss_oil, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_41.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1523) {
                    if (st.ownItemCount(fire_essence) >= 1 && st.ownItemCount(demonic_essence) >= 1) {

                        st.takeItems(fire_essence, 1);
                        st.takeItems(demonic_essence, 1);
                        st.giveItems(hellfire_oil, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_42.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1624) {
                    if (st.ownItemCount(lunargent) >= 1 && st.ownItemCount(midnight_oil) >= 1) {
                        st.takeItems(lunargent, 1);
                        st.takeItems(midnight_oil, 1);
                        st.giveItems(nightmare_oil, GetMemoStateEx);
                        st.setMemoState("reagent_supplier", String.valueOf(0), true);
                        st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                        htmltext = "alchemical_mixing_jar_q0373_43.htm";
                        st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                    } else {
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                        st.soundEffect(SOUND_LIQUID_FAIL_01);
                    }
                } else if (GetMemoState == 1324) {
                    if (st.ownItemCount(pure_silver) == 0) {
                        if (st.ownItemCount(lunargent) >= 1 && st.ownItemCount(quicksilver) >= 1) {

                            st.takeItems(lunargent, 1);
                            st.takeItems(quicksilver, 1);
                            st.giveItems(pure_silver, 1);
                            st.setMemoState("reagent_supplier", String.valueOf(0), true);
                            st.setMemoState("reagent_supplier_ex", String.valueOf(0), true);
                            htmltext = "alchemical_mixing_jar_q0373_46.htm";
                            st.soundEffect(SOUND_LIQUID_SUCCESS_01);
                        } else {
                            htmltext = "alchemical_mixing_jar_q0373_44.htm";
                            st.soundEffect(SOUND_LIQUID_FAIL_01);
                        }
                    } else
                        htmltext = "alchemical_mixing_jar_q0373_44.htm";
                } else if (GetMemoState == 1324)
                    htmltext = "alchemical_mixing_jar_q0373_44.htm";
                else {
                    int i1 = GetMemoState / 100;
                    int i2 = GetMemoState % 100;
                    if (i2 == 11)
                        st.takeItems(wyrms_blood, 10);
                    else if (i2 == 12)
                        st.takeItems(lava_stone, 10);
                    else if (i2 == 13)
                        st.takeItems(moonstone_shard, 10);
                    else if (i2 == 14)
                        st.takeItems(decaying_bone, 10);
                    else if (i2 == 15)
                        st.takeItems(demons_blood, 10);
                    else if (i2 == 16)
                        st.takeItems(infernium_ore, 10);
                    else if (i2 == 17)
                        st.takeItems(dracoplasm, 10);
                    else if (i2 == 18)
                        st.takeItems(magma_dust, 10);
                    else if (i2 == 19)
                        st.takeItems(moon_dust, 10);
                    else if (i2 == 20)
                        st.takeItems(necroplasm, 10);
                    else if (i2 == 21)
                        st.takeItems(demonplasm, 10);
                    else if (i2 == 22)
                        st.takeItems(inferno_dust, 10);
                    else if (i2 == 23)
                        st.takeItems(fire_essence, 1);
                    else if (i2 == 24)
                        st.takeItems(lunargent, 1);
                    if (i1 == 11)
                        st.takeItems(blood_root, 1);
                    else if (i1 == 12)
                        st.takeItems(volcanic_ash, 1);
                    else if (i1 == 13)
                        st.takeItems(quicksilver, 1);
                    else if (i1 == 14)
                        st.takeItems(sulfur, 1);
                    else if (i1 == 15)
                        st.takeItems(demonic_essence, 1);
                    else if (i1 == 16)
                        st.takeItems(midnight_oil, 1);
                    htmltext = "alchemical_mixing_jar_q0373_44.htm";
                    st.soundEffect(SOUND_LIQUID_FAIL_01);
                }
            } else if (event.equalsIgnoreCase("reply_7") && GetMemoStateEx == 0) {
                int i1 = GetMemoState / 100;
                int i2 = GetMemoState % 100;
                if (i2 == 11)
                    st.takeItems(wyrms_blood, 10);
                else if (i2 == 12)
                    st.takeItems(lava_stone, 10);
                else if (i2 == 13)
                    st.takeItems(moonstone_shard, 10);
                else if (i2 == 14)
                    st.takeItems(decaying_bone, 10);
                else if (i2 == 15)
                    st.takeItems(demons_blood, 10);
                else if (i2 == 16)
                    st.takeItems(infernium_ore, 10);
                else if (i2 == 17)
                    st.takeItems(dracoplasm, 10);
                else if (i2 == 18)
                    st.takeItems(magma_dust, 10);
                else if (i2 == 19)
                    st.takeItems(moon_dust, 10);
                else if (i2 == 20)
                    st.takeItems(necroplasm, 10);
                else if (i2 == 21)
                    st.takeItems(demonplasm, 10);
                else if (i2 == 22)
                    st.takeItems(inferno_dust, 10);
                else if (i2 == 23)
                    st.takeItems(fire_essence, 1);
                else if (i2 == 24)
                    st.takeItems(lunargent, 1);
                if (i1 == 11)
                    st.takeItems(blood_root, 1);
                else if (i1 == 12)
                    st.takeItems(volcanic_ash, 1);
                else if (i1 == 13)
                    st.takeItems(quicksilver, 1);
                else if (i1 == 14)
                    st.takeItems(sulfur, 1);
                else if (i1 == 15)
                    st.takeItems(demonic_essence, 1);
                else if (i1 == 16)
                    st.takeItems(midnight_oil, 1);
                htmltext = "alchemical_mixing_jar_q0373_45.htm";
                st.soundEffect(SOUND_LIQUID_FAIL_01);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("reagent_supplier");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == bandor) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "bandor_q0373_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "bandor_q0373_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == bandor) {
                    if (GetMemoState == 0)
                        htmltext = "bandor_q0373_05.htm";
                } else if (npcId == alchemical_mixing_jar)
                    if (GetMemoState >= 0)
                        htmltext = "alchemical_mixing_jar_q0373_01.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();

        if (npcId == credion) {
            int i4 = Rnd.get(1000);
            if (i4 < 618) {
                st.giveItems(decaying_bone, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 1000) {
                st.giveItems(quicksilver, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hallates_maiden) {
            int i4 = Rnd.get(100);
            if (i4 < 45) {
                st.giveItems(ingredient_pouch1, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 65) {
                st.giveItems(volcanic_ash, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == platinum_tribe_shaman) {
            int i4 = Rnd.get(1000);
            if (i4 < 658) {
                st.giveItems(ingredient_pouch2, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 100) {
                st.giveItems(quicksilver, 2);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hallates_guardian) {
            int i4 = Rnd.get(1000);
            if (i4 < 766) {
                st.giveItems(demons_blood, 3);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 876) {
                st.giveItems(moonstone_shard, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == plat_protect_shaman) {
            int i4 = Rnd.get(1000);
            if (i4 < 444) {
                st.giveItems(ingredient_box, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == lava_wyrm) {
            int i4 = Rnd.get(1000);
            if (i4 < 666) {
                st.giveItems(wyrms_blood, 1);
                st.soundEffect(SOUND_ITEMGET);
            } else if (i4 < 989) {
                st.giveItems(lava_stone, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hames_orc_shaman) {
            int i4 = Rnd.get(1000);
            if (i4 < 616) {
                st.giveItems(ingredient_pouch3, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}