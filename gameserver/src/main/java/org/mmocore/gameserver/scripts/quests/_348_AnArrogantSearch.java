package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 03/12/2014
 * @lastedit 17/04/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _348_AnArrogantSearch extends Quest {
    // npc
    private final static int sir_gustaf_athebaldt = 30760;
    private final static int hardin = 30832;
    private final static int magister_hanellin = 30864;
    private final static int iason_haine = 30969;
    private final static int holy_ark_1 = 30977;
    private final static int holy_ark_2 = 30978;
    private final static int holy_ark_3 = 30979;
    private final static int ark_guardians_corpse = 30980;
    private final static int harne = 30144;
    private final static int claudia_a = 31001;
    private final static int martian = 30645;
    // mobs
    private final static int ark_guardian_elberoth = 27182;
    private final static int ark_guardian_shadowfang = 27183;
    private final static int angel_killer = 27184;
    private final static int platinum_tribe_shaman = 20828;
    private final static int platinum_tribe_lord = 20829;
    private final static int ynglzu = 20647;
    private final static int paliote = 20648;
    private final static int guardian_angel = 20830;
    private final static int seal_angel = 20831;
    private final static int seal_angel_r = 20860;
    // questitem
    private final static int q_shell_of_evil_re = 14857;
    private final static int q0348_engine_of_lgiant = 4287;
    private final static int q0348_hanellins_letter1 = 4288;
    private final static int q0348_hanellins_letter2 = 4289;
    private final static int q0348_hanellins_letter3 = 4290;
    private final static int q0348_key_of_ark1 = 4291;
    private final static int q0348_key_of_ark2 = 4292;
    private final static int q0348_key_of_ark3 = 4293;
    private final static int q0348_white_fabric = 4294;
    private final static int q0348_blooded_fabric = 4295;
    private final static int q0348_white_fabric_a = 5232;
    // etcitem
    private final static int hanellins_white_flower = 4394;
    private final static int hanellins_red_flower = 4395;
    private final static int hanellins_yellow_flower = 4396;
    private final static int book_of_saint = 4397;
    private final static int blood_of_saint = 4398;
    private final static int bough_of_saint = 4399;
    private final static int white_fabric = 4400;
    private final static int antidote = 1831;
    private final static int healing_potion = 1061;
    private final static int kris_edge = 4109;
    private final static int synthesis_cokes = 1888;
    private final static int demons_sword_edge = 4119;
    private final static int cokes = 1879;
    private final static int arthro_nail_blade = 4111;
    private final static int bellion_cestus_edge = 4120;
    private final static int oriharukon_ore = 1874;
    private final static int kshanberk_blade = 4107;
    private final static int sword_of_damascus_blade = 4114;
    private final static int dark_elven_long_bow_shaft = 4112;
    private final static int sprites_staff_head = 4106;
    private final static int hazard_bow_shaft = 4121;
    private final static int coarse_bone_powder = 1881;
    private final static int heavy_war_axe_head = 4105;
    private final static int art_of_battle_axe_blade = 4117;
    private final static int great_axe_head = 4113;
    private final static int enria = 4042;
    private final static int lancia_blade = 4115;
    private final static int staff_of_evil_sprit_head = 4118;
    private final static int animal_bone = 1872;
    private final static int hell_knife_edge = 4110;
    private final static int great_sword_blade = 4104;
    private final static int varnish_of_purity = 1887;
    private final static int sword_of_valhalla_blade = 4108;
    // talker occupation
    private final static int treasure_hunter = 0x08;
    private final static int plains_walker = 0x17;
    private final static int abyss_walker = 0x24;
    private final static int adventurer = 0x5D;
    private final static int wind_rider = 0x65;
    private final static int ghost_hunter = 0x6C;
    private final static int m_soul_breaker = 0x80;
    private final static int f_soul_breaker = 0x81;
    private final static int m_soul_hound = 0x84;
    private final static int f_soul_hound = 0x85;
    private final static int inspector = 0x87;
    private final static int judicator = 0x88;
    private final static int tyrant = 0x30;
    private final static int grand_khavatari = 0x72;
    private final static int paladin = 0x05;
    private final static int dark_avenger = 0x06;
    private final static int prophet = 0x11;
    private final static int temple_knight = 0x14;
    private final static int swordsinger = 0x15;
    private final static int shillien_knight = 0x21;
    private final static int bladedancer = 0x22;
    private final static int shillien_elder = 0x2B;
    private final static int phoenix_knight = 0x5A;
    private final static int hell_knight = 0x5B;
    private final static int hierophant = 0x62;
    private final static int evas_templar = 0x63;
    private final static int sword_muse = 0x64;
    private final static int shillien_templar = 0x6A;
    private final static int spectral_dancer = 0x6B;
    private final static int shillien_saint = 0x70;
    private final static int hawkeye = 0x09;
    private final static int silver_ranger = 0x18;
    private final static int phantom_ranger = 0x25;
    private final static int sagittarius = 0x5C;
    private final static int moonlight_sentinel = 0x66;
    private final static int ghost_sentinel = 0x6D;
    private final static int arbalester = 0x82;
    private final static int trickster = 0x86;
    private final static int gladiator = 0x02;
    private final static int bishop = 0x10;
    private final static int elder = 0x1E;
    private final static int duelist = 0x58;
    private final static int cardinal = 0x61;
    private final static int evas_saint = 0x69;
    private final static int warlord = 0x03;
    private final static int bounty_hunter = 0x37;
    private final static int warsmith = 0x39;
    private final static int dreadnought = 0x59;
    private final static int fortune_seeker = 0x75;
    private final static int maestro = 0x76;
    private final static int sorcerer = 0x0C;
    private final static int spellsinger = 0x1B;
    private final static int overlord = 0x33;
    private final static int archmage = 0x5E;
    private final static int mystic_muse = 0x67;
    private final static int dominator = 0x73;
    private final static int necromancer = 0x0D;
    private final static int spellhowler = 0x28;
    private final static int soultaker = 0x5F;
    private final static int storm_screamer = 0x6E;
    private final static int destroyer = 0x2E;
    private final static int titan = 0x71;
    private final static int berserker = 0x7F;
    private final static int doombringer = 0x83;
    private final static int elemental_summoner = 0x1C;
    private final static int phantom_summoner = 0x29;
    private final static int elemental_master = 0x68;
    private final static int spectral_master = 0x6F;
    private final static int warcryer = 0x34;
    private final static int doomcryer = 0x74;
    private final static int warlock = 0x0E;
    private final static int arcana_lord = 0x60;

    public _348_AnArrogantSearch() {
        super(true);
        addStartNpc(magister_hanellin);
        addTalkId(claudia_a, holy_ark_2, martian, holy_ark_3, harne, ark_guardians_corpse, holy_ark_1, iason_haine, hardin, sir_gustaf_athebaldt);
        addKillId(ark_guardian_elberoth, ark_guardian_shadowfang, angel_killer, platinum_tribe_shaman, platinum_tribe_lord, ynglzu, paliote, guardian_angel, seal_angel, seal_angel_r);
        addAttackId(angel_killer, ark_guardian_elberoth, ark_guardian_shadowfang, platinum_tribe_shaman, platinum_tribe_lord);
        addQuestItem(q0348_hanellins_letter1, q0348_hanellins_letter2, q0348_hanellins_letter3, hanellins_white_flower, hanellins_red_flower, hanellins_yellow_flower, book_of_saint, q0348_white_fabric, blood_of_saint, bough_of_saint, white_fabric, q0348_white_fabric_a, q0348_key_of_ark1, q0348_key_of_ark2, q0348_key_of_ark3);
        addLevelCheck(60, 75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("arrogant_quest");
        int GetMemoStateEx_1 = st.getInt("arrogant_quest_ex_1");
        int npcId = npc.getNpcId();
        if (npcId == magister_hanellin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(hanellins_white_flower) == 1 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0) {
                    st.takeItems(hanellins_white_flower, 1);
                    if (st.ownItemCount(hanellins_red_flower) > 0)
                        st.takeItems(hanellins_red_flower, 1);
                    if (st.ownItemCount(hanellins_yellow_flower) > 0)
                        st.takeItems(hanellins_yellow_flower, 1);
                    st.setCond(1);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(1), true);
                    st.soundEffect(SOUND_ACCEPT);
                    st.setState(STARTED);
                    htmltext = "magister_hanellin_q0348_15.htm";
                } else if (st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 1 && st.ownItemCount(hanellins_yellow_flower) == 0) {
                    st.takeItems(hanellins_red_flower, 1);
                    if (st.ownItemCount(hanellins_white_flower) > 0)
                        st.takeItems(hanellins_white_flower, 1);
                    if (st.ownItemCount(hanellins_yellow_flower) > 0)
                        st.takeItems(hanellins_yellow_flower, 1);
                    st.setCond(1);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(2), true);
                    st.soundEffect(SOUND_ACCEPT);
                    st.setState(STARTED);
                    htmltext = "magister_hanellin_q0348_15.htm";
                } else if (st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 1) {
                    st.takeItems(hanellins_yellow_flower, 1);
                    if (st.ownItemCount(hanellins_white_flower) > 0)
                        st.takeItems(hanellins_white_flower, 1);
                    if (st.ownItemCount(hanellins_red_flower) > 0)
                        st.takeItems(hanellins_red_flower, 1);
                    st.setCond(1);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(3), true);
                    st.soundEffect(SOUND_ACCEPT);
                    st.setState(STARTED);
                    htmltext = "magister_hanellin_q0348_15.htm";
                } else {
                    st.setCond(2);
                    st.setMemoState("arrogant_quest", String.valueOf(1), true);
                    st.soundEffect(SOUND_ACCEPT);
                    st.setState(STARTED);
                    htmltext = "magister_hanellin_q0348_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (((GetMemoState == 1 && (st.ownItemCount(q0348_engine_of_lgiant) > 0 || st.ownItemCount(q_shell_of_evil_re) > 0)) || GetMemoState == 2)) {
                    st.setCond(3);
                    st.setMemoState("arrogant_quest", String.valueOf(3), true);
                    st.giveItems(hanellins_white_flower, 1);
                    st.giveItems(hanellins_red_flower, 1);
                    st.giveItems(hanellins_yellow_flower, 1);
                    htmltext = "magister_hanellin_q0348_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (((GetMemoState == 1 && (st.ownItemCount(q0348_engine_of_lgiant) > 0 || st.ownItemCount(q_shell_of_evil_re) > 0)) || GetMemoState == 2)) {
                    st.setCond(4);
                    st.setMemoState("arrogant_quest", String.valueOf(4), true); // по логике должно быть!
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(0), true);
                    htmltext = "magister_hanellin_q0348_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 4 && GetMemoStateEx_1 == 0) {
                    st.setCond(5);
                    st.setMemoState("arrogant_quest", String.valueOf(5), true);
                    st.giveItems(q0348_hanellins_letter1, 1);
                    st.giveItems(q0348_hanellins_letter2, 1);
                    st.giveItems(q0348_hanellins_letter3, 1);
                    htmltext = "magister_hanellin_q0348_17.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 4) {
                    if (GetMemoStateEx_1 == 2 && st.ownItemCount(q0348_hanellins_letter2) == 0) {
                        st.setCond(7);
                        st.setMemoState("arrogant_quest", String.valueOf(5), true);
                        st.giveItems(q0348_hanellins_letter2, 1);
                        htmltext = "magister_hanellin_q0348_19.htm";
                    } else if (GetMemoStateEx_1 == 1 && st.ownItemCount(q0348_hanellins_letter1) == 0) {
                        st.setCond(6);
                        st.setMemoState("arrogant_quest", String.valueOf(5), true);
                        st.giveItems(q0348_hanellins_letter1, 1);
                        htmltext = "magister_hanellin_q0348_18.htm";
                    } else if (GetMemoStateEx_1 == 3 && st.ownItemCount(q0348_hanellins_letter3) == 0) {
                        st.setCond(8);
                        st.setMemoState("arrogant_quest", String.valueOf(5), true);
                        st.giveItems(q0348_hanellins_letter3, 1);
                        htmltext = "magister_hanellin_q0348_20.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("reply_5"))
                htmltext = "magister_hanellin_q0348_21.htm";
            else if (event.equalsIgnoreCase("reply_6"))
                htmltext = "magister_hanellin_q0348_22.htm";
            else if (event.equalsIgnoreCase("reply_7"))
                htmltext = "magister_hanellin_q0348_23.htm";
            else if (event.equalsIgnoreCase("reply_8"))
                htmltext = "magister_hanellin_q0348_32.htm";
            else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 10 && st.ownItemCount(q0348_white_fabric) == 1) {
                    st.setMemoState("arrogant_quest", String.valueOf(11), true);
                    htmltext = "magister_hanellin_q0348_33.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 11 && st.ownItemCount(q0348_white_fabric) == 1 && GetMemoStateEx_1 > 0) {
                    int i0 = GetMemoStateEx_1;
                    if (i0 == 1)
                        st.giveItems(ADENA_ID, 43000);
                    else if (i0 == 2)
                        st.giveItems(ADENA_ID, 4000);
                    else if (i0 == 3)
                        st.giveItems(ADENA_ID, 13000);
                    st.setCond(24);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(12), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(100), true);
                    htmltext = "magister_hanellin_q0348_34.htm";
                } else
                    htmltext = "magister_hanellin_q0348_35.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 11 && GetMemoStateEx_1 == 0 && st.ownItemCount(q0348_white_fabric) == 1) {
                    st.setCond(24);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(12), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(20000), true);
                    st.giveItems(ADENA_ID, 49000);
                    htmltext = "magister_hanellin_q0348_36.htm";
                }
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 11 && GetMemoStateEx_1 == 0 && st.ownItemCount(q0348_white_fabric) == 1) {
                    st.setCond(25);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(13), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(20000), true);
                    htmltext = "magister_hanellin_q0348_37.htm";
                }
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (GetMemoState == 15) {
                    st.setMemoState("arrogant_quest", String.valueOf(16), true);
                    htmltext = "magister_hanellin_q0348_50.htm";
                }
            } else if (event.equalsIgnoreCase("reply_14")) {
                if (GetMemoState == 15 || GetMemoState == 16) {
                    if (st.ownItemCount(q0348_blooded_fabric) > 0 && st.ownItemCount(q0348_white_fabric) == 0)
                        st.giveItems(q0348_white_fabric, 9);
                    else
                        st.giveItems(q0348_white_fabric, 10);
                    st.setCond(26);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(17), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(0), true);
                    htmltext = "magister_hanellin_q0348_51.htm";
                }
            } else if (event.equalsIgnoreCase("reply_15")) {
                if (GetMemoState == 19) {
                    st.setCond(29);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(17), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(0), true);
                    st.giveItems(q0348_white_fabric, 10);
                    htmltext = "magister_hanellin_q0348_56.htm";
                }
            } else if (event.equalsIgnoreCase("reply_16")) {
                st.removeMemo("arrogant_quest");
                st.removeMemo("arrogant_quest_ex_0");
                st.removeMemo("arrogant_quest_ex_1");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "magister_hanellin_q0348_57.htm";
            } else if (event.equalsIgnoreCase("reply_17") && st.ownItemCount(q0348_blooded_fabric) >= 10 && st.ownItemCount(q0348_white_fabric) == 0) {
                if (GetMemoState == 17) {
                    st.setCond(27);
                    st.setMemoState("arrogant_quest_ex_0", String.valueOf(18), true);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(0), true);
                    htmltext = "magister_hanellin_q0348_58.htm";
                }
            } else if (event.equalsIgnoreCase("reply_18"))
                htmltext = "magister_hanellin_q0348_03.htm";
            else if (event.equalsIgnoreCase("reply_19"))
                htmltext = "magister_hanellin_q0348_04.htm";
        } else if (npcId == claudia_a) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "claudia_a_q0348_02.htm";
        } else if (npcId == martian) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "martian_q0348_02.htm";
        } else if (npcId == harne) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "harne_q0348_02.htm";
        } else if (event.equalsIgnoreCase("34803")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("34804")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("34801")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int GetMemoState = st.getInt("arrogant_quest");
        int GetMemoStateEx_1 = st.getInt("arrogant_quest_ex_1");
        int GetMemoStateEx_0 = st.getInt("arrogant_quest_ex_0");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == magister_hanellin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            if (st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0 && st.ownItemCount(hanellins_white_flower) == 0) {
                                htmltext = "magister_hanellin_q0348_01.htm";
                                st.exitQuest(true);
                            } else if ((st.ownItemCount(hanellins_white_flower) == 1 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0) || (st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 1 && st.ownItemCount(hanellins_yellow_flower) == 0) || (st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 1)) {
                                htmltext = "magister_hanellin_q0348_13.htm";
                                st.exitQuest(true);
                            }
                            break;
                        default:
                            if (st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0 && st.ownItemCount(hanellins_white_flower) == 0)
                                htmltext = "magister_hanellin_q0348_02.htm";
                            else if (((st.ownItemCount(hanellins_white_flower) == 1 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0) || (st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 1 && st.ownItemCount(hanellins_yellow_flower) == 0) || (st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 1)))
                                htmltext = "magister_hanellin_q0348_14.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == magister_hanellin) {
                    if (GetMemoState == 1000) {
                        st.takeItems(hanellins_yellow_flower, 1);
                        st.takeItems(hanellins_red_flower, 1);
                        st.takeItems(hanellins_white_flower, 1);
                    } else if (GetMemoState == 1 && st.ownItemCount(q0348_engine_of_lgiant) == 0 && st.ownItemCount(q_shell_of_evil_re) == 0)
                        htmltext = "magister_hanellin_q0348_06.htm";
                    else if ((GetMemoState == 1 && st.ownItemCount(q0348_engine_of_lgiant) > 0) || st.ownItemCount(q_shell_of_evil_re) > 0 || GetMemoState == 2) {
                        if (st.ownItemCount(q_shell_of_evil_re) > 0)
                            st.takeItems(q_shell_of_evil_re, 1);
                        if (st.ownItemCount(q0348_engine_of_lgiant) > 0)
                            st.takeItems(q0348_engine_of_lgiant, 1);
                        st.setMemoState("arrogant_quest", String.valueOf(2), true);
                        htmltext = "magister_hanellin_q0348_07.htm";
                    } else if ((st.ownItemCount(hanellins_white_flower) == 1 && st.ownItemCount(hanellins_red_flower) == 1) || (st.ownItemCount(hanellins_white_flower) == 1 && st.ownItemCount(hanellins_yellow_flower) == 1) || (st.ownItemCount(hanellins_red_flower) == 1 && st.ownItemCount(hanellins_yellow_flower) == 1))
                        htmltext = "magister_hanellin_q0348_10.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0)
                        htmltext = "magister_hanellin_q0348_11.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(hanellins_white_flower) == 1 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 0) {
                        st.takeItems(hanellins_white_flower, 1);
                        // st.setMemoState("arrogant_quest", String.valueOf(4), true);
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(1), true);
                        htmltext = "magister_hanellin_q0348_12.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 1 && st.ownItemCount(hanellins_yellow_flower) == 0) {
                        st.takeItems(hanellins_red_flower, 1);
                        // st.setMemoState("arrogant_quest", String.valueOf(4), true);
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(2), true);
                        htmltext = "magister_hanellin_q0348_12.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(hanellins_white_flower) == 0 && st.ownItemCount(hanellins_red_flower) == 0 && st.ownItemCount(hanellins_yellow_flower) == 1) {
                        st.takeItems(hanellins_yellow_flower, 1);
                        // st.setMemoState("arrogant_quest", String.valueOf(4), true);
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(4), true);
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(3), true);
                        htmltext = "magister_hanellin_q0348_12.htm";
                    }
                    /*
                     * else if(GetMemoState == 4 && GetMemoStateEx_1 == 0) {
                     * st.setCond(5); st.setMemoState("arrogant_quest",
                     * String.valueOf(5), true);
                     * st.giveItems(q0348_hanellins_letter1, 1);
                     * st.giveItems(q0348_hanellins_letter2, 1);
                     * st.giveItems(q0348_hanellins_letter3, 1);
                     * st.soundEffect(SOUND_MIDDLE); htmltext =
                     * "magister_hanellin_q0348_17.htm"; }
                     */
                    else if (GetMemoState == 4 && GetMemoStateEx_1 == 1) {
                        st.setCond(6);
                        st.setMemoState("arrogant_quest", String.valueOf(5), true);
                        st.giveItems(q0348_hanellins_letter1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_18.htm";
                    } else if (GetMemoState == 4 && GetMemoStateEx_1 == 2) {
                        st.setCond(7);
                        st.setMemoState("arrogant_quest", String.valueOf(5), true);
                        st.giveItems(q0348_hanellins_letter2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_19.htm";
                    } else if (GetMemoState == 4 && GetMemoStateEx_1 == 3) {
                        st.setCond(8);
                        st.setMemoState("arrogant_quest", String.valueOf(5), true);
                        st.giveItems(q0348_hanellins_letter3, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_20.htm";
                    } else if (GetMemoState == 5 && GetMemoStateEx_1 % 10 == 0)
                        htmltext = "magister_hanellin_q0348_24.htm";
                    else if (GetMemoState == 5 && GetMemoStateEx_1 == 1)
                        htmltext = "magister_hanellin_q0348_25.htm";
                    else if (GetMemoState == 5 && GetMemoStateEx_1 == 2)
                        htmltext = "magister_hanellin_q0348_26.htm";
                    else if (GetMemoState == 5 && GetMemoStateEx_1 == 3)
                        htmltext = "magister_hanellin_q0348_27.htm";
                    else if (GetMemoState == 6 || GetMemoState == 7)
                        htmltext = "magister_hanellin_q0348_28.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(book_of_saint) == 1 && st.ownItemCount(blood_of_saint) == 1 && st.ownItemCount(bough_of_saint) == 1) {
                        st.setCond(22);
                        st.setMemoState("arrogant_quest", String.valueOf(9), true);
                        st.takeItems(book_of_saint, 1);
                        st.takeItems(blood_of_saint, 1);
                        st.takeItems(bough_of_saint, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_29.htm";
                    } else if ((GetMemoState == 8 && GetMemoStateEx_1 == 0 && (st.ownItemCount(book_of_saint) == 0 || st.ownItemCount(blood_of_saint) == 0 || st.ownItemCount(bough_of_saint) == 0)))
                        htmltext = "magister_hanellin_q0348_29t.htm";
                    else if (GetMemoState == 9 && (st.ownItemCount(antidote) < 5 || st.ownItemCount(healing_potion) == 0))
                        htmltext = "magister_hanellin_q0348_30.htm";
                    else if (GetMemoState == 9 && GetMemoStateEx_1 == 0 && st.ownItemCount(antidote) >= 5 && st.ownItemCount(healing_potion) > 0) {
                        st.setMemoState("arrogant_quest", String.valueOf(10), true);
                        st.takeItems(antidote, 5);
                        st.takeItems(healing_potion, 1);
                        st.giveItems(q0348_white_fabric, 1);
                        htmltext = "magister_hanellin_q0348_31.htm";
                    } else if (GetMemoState == 10 && st.ownItemCount(q0348_white_fabric) == 1)
                        htmltext = "magister_hanellin_q0348_32.htm";
                    else if (GetMemoState == 11 && GetMemoStateEx_1 > 0 && st.ownItemCount(q0348_white_fabric) == 1) {
                        int i0 = GetMemoStateEx_1;
                        if (i0 == 1)
                            st.giveItems(ADENA_ID, 43000);
                        else if (i0 == 2)
                            st.giveItems(ADENA_ID, 4000);
                        else if (i0 == 3)
                            st.giveItems(ADENA_ID, 13000);
                        st.setCond(24);
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(12), true);
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(100), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_34.htm";
                    } else if (GetMemoState == 11 && GetMemoStateEx_1 == 0 && st.ownItemCount(q0348_white_fabric) == 1)
                        htmltext = "magister_hanellin_q0348_35.htm";
                    else if (GetMemoState == 12 && st.ownItemCount(q0348_white_fabric) == 1)
                        htmltext = "magister_hanellin_q0348_38.htm";
                    else if (GetMemoState == 13 && st.ownItemCount(q0348_white_fabric) == 1)
                        htmltext = "magister_hanellin_q0348_39.htm";
                    else if (GetMemoState == 11 && GetMemoStateEx_1 == 1 && st.ownItemCount(blood_of_saint) == 1 && (st.ownItemCount(book_of_saint) == 0 || st.ownItemCount(bough_of_saint) == 0))
                        htmltext = "magister_hanellin_q0348_40.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx_1 == 2 && st.ownItemCount(book_of_saint) == 1 && (st.ownItemCount(blood_of_saint) == 0 || st.ownItemCount(bough_of_saint) == 0))
                        htmltext = "magister_hanellin_q0348_41.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx_1 == 3 && st.ownItemCount(book_of_saint) == 1 && (st.ownItemCount(blood_of_saint) == 0 || st.ownItemCount(bough_of_saint) == 0))
                        htmltext = "magister_hanellin_q0348_42.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx_1 == 1 && st.ownItemCount(blood_of_saint) == 0 && st.ownItemCount(white_fabric) == 0)
                        htmltext = "magister_hanellin_q0348_43.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx_1 == 2 && st.ownItemCount(blood_of_saint) == 0 && st.ownItemCount(white_fabric) == 0)
                        htmltext = "magister_hanellin_q0348_44.htm";
                    else if (GetMemoState == 8 && GetMemoStateEx_1 == 3 && st.ownItemCount(blood_of_saint) == 0 && st.ownItemCount(white_fabric) == 0)
                        htmltext = "magister_hanellin_q0348_45.htm";
                    else if (GetMemoState == 9 && GetMemoStateEx_1 != 0 && st.ownItemCount(antidote) >= 5 && st.ownItemCount(healing_potion) > 0) {
                        st.setCond(23);
                        st.setMemoState("arrogant_quest", String.valueOf(10), true);
                        st.giveItems(white_fabric, 3);
                        st.takeItems(antidote, 5);
                        st.takeItems(healing_potion, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_46.htm";
                    } else if ((GetMemoState == 10 || GetMemoState == 8) && GetMemoStateEx_1 > 0 && st.ownItemCount(white_fabric) > 1)
                        htmltext = "magister_hanellin_q0348_47.htm";
                    else if ((GetMemoState == 10 || GetMemoState == 8) && GetMemoStateEx_1 > 0 && st.ownItemCount(book_of_saint) == 0 && st.ownItemCount(blood_of_saint) == 0 && st.ownItemCount(bough_of_saint) == 0 && st.ownItemCount(white_fabric) == 1) {
                        st.setMemoState("arrogant_quest", String.valueOf(10), true);
                        st.giveItems(q0348_white_fabric, 1);
                        st.takeItems(white_fabric, 1);
                        htmltext = "magister_hanellin_q0348_48.htm";
                    } else if (GetMemoState == 14) {
                        if (talker_occupation == treasure_hunter || talker_occupation == plains_walker || talker_occupation == abyss_walker || talker_occupation == adventurer || talker_occupation == wind_rider || talker_occupation == ghost_hunter || talker_occupation == m_soul_breaker || talker_occupation == f_soul_breaker || talker_occupation == m_soul_hound || talker_occupation == f_soul_hound || talker_occupation == inspector || talker_occupation == judicator)
                            if (st.getPlayer().getLevel() < 69) {
                                st.giveItems(kris_edge, 1);
                                st.giveItems(synthesis_cokes, 2);
                            } else if (st.getPlayer().getLevel() >= 69) {
                                st.giveItems(demons_sword_edge, 1);
                                st.giveItems(cokes, 2);
                            } else if (talker_occupation == tyrant || talker_occupation == grand_khavatari) {
                                if (st.getPlayer().getLevel() < 69) {
                                    st.giveItems(arthro_nail_blade, 1);
                                    st.giveItems(synthesis_cokes, 2);
                                    st.giveItems(cokes, 1);
                                } else if (st.getPlayer().getLevel() >= 69) {
                                    st.giveItems(bellion_cestus_edge, 1);
                                    st.giveItems(oriharukon_ore, 2);
                                }
                            } else if (talker_occupation == paladin || talker_occupation == dark_avenger || talker_occupation == prophet || talker_occupation == temple_knight || talker_occupation == swordsinger || talker_occupation == shillien_knight || talker_occupation == bladedancer || talker_occupation == shillien_elder || talker_occupation == phoenix_knight || talker_occupation == hell_knight || talker_occupation == hierophant || talker_occupation == evas_templar || talker_occupation == sword_muse || talker_occupation == shillien_templar || talker_occupation == spectral_dancer || talker_occupation == shillien_saint) {
                                if (st.getPlayer().getLevel() < 69) {
                                    st.giveItems(kshanberk_blade, 1);
                                    st.giveItems(synthesis_cokes, 2);
                                } else if (st.getPlayer().getLevel() >= 69) {
                                    st.giveItems(sword_of_damascus_blade, 1);
                                    st.giveItems(oriharukon_ore, 2);
                                }
                            } else if (talker_occupation == hawkeye || talker_occupation == silver_ranger || talker_occupation == phantom_ranger || talker_occupation == sagittarius || talker_occupation == moonlight_sentinel || talker_occupation == ghost_sentinel || talker_occupation == arbalester || talker_occupation == trickster) {
                                if (st.getPlayer().getLevel() < 69) {
                                    st.giveItems(dark_elven_long_bow_shaft, 1);
                                    st.giveItems(synthesis_cokes, 2);
                                } else if (st.getPlayer().getLevel() >= 69) {
                                    st.giveItems(hazard_bow_shaft, 1);
                                    st.giveItems(coarse_bone_powder, 9);
                                }
                            } else if (talker_occupation == gladiator || talker_occupation == bishop || talker_occupation == elder || talker_occupation == duelist || talker_occupation == cardinal || talker_occupation == evas_saint) {
                                if (st.getPlayer().getLevel() < 69) {
                                    st.giveItems(heavy_war_axe_head, 1);
                                    st.giveItems(synthesis_cokes, 2);
                                    st.giveItems(cokes, 1);
                                } else if (st.getPlayer().getLevel() >= 69) {
                                    st.giveItems(art_of_battle_axe_blade, 1);
                                    st.giveItems(oriharukon_ore, 2);
                                }
                            } else if (talker_occupation == warlord || talker_occupation == bounty_hunter || talker_occupation == warsmith || talker_occupation == dreadnought || talker_occupation == fortune_seeker || talker_occupation == maestro) {
                                if (st.getPlayer().getLevel() < 63) {
                                    st.giveItems(great_axe_head, 1);
                                    st.giveItems(enria, 1);
                                    st.giveItems(cokes, 1);
                                } else if (st.getPlayer().getLevel() >= 63) {
                                    st.giveItems(lancia_blade, 1);
                                    st.giveItems(oriharukon_ore, 2);
                                }
                            } else if (talker_occupation == sorcerer || talker_occupation == spellsinger || talker_occupation == overlord || talker_occupation == archmage || talker_occupation == mystic_muse || talker_occupation == dominator) {
                                if (st.getPlayer().getLevel() < 63) {
                                    st.giveItems(sprites_staff_head, 1);
                                    st.giveItems(oriharukon_ore, 4);
                                    st.giveItems(coarse_bone_powder, 1);
                                } else if (st.getPlayer().getLevel() >= 63) {
                                    st.giveItems(staff_of_evil_sprit_head, 1);
                                    st.giveItems(animal_bone, 5);
                                }
                            } else if (talker_occupation == necromancer || talker_occupation == spellhowler || talker_occupation == soultaker || talker_occupation == storm_screamer) {
                                st.giveItems(hell_knife_edge, 1);
                                st.giveItems(synthesis_cokes, 2);
                                st.giveItems(animal_bone, 2);
                            } else if (talker_occupation == necromancer || talker_occupation == spellhowler || talker_occupation == soultaker || talker_occupation == storm_screamer) {
                                st.giveItems(hell_knife_edge, 1);
                                st.giveItems(synthesis_cokes, 2);
                                st.giveItems(animal_bone, 2);
                            } else if (talker_occupation == destroyer || talker_occupation == titan || talker_occupation == berserker || talker_occupation == doombringer) {
                                st.giveItems(great_sword_blade, 1);
                                st.giveItems(varnish_of_purity, 2);
                                st.giveItems(synthesis_cokes, 2);
                            } else if (talker_occupation == elemental_summoner || talker_occupation == phantom_summoner || talker_occupation == elemental_master || talker_occupation == spectral_master) {
                                st.giveItems(sword_of_damascus_blade, 1);
                                st.giveItems(enria, 1);
                            } else if (talker_occupation == warcryer || talker_occupation == doomcryer) {
                                st.giveItems(sword_of_valhalla_blade, 1);
                                st.giveItems(oriharukon_ore, 1);
                                st.giveItems(varnish_of_purity, 1);
                            } else if (talker_occupation == warlock || talker_occupation == arcana_lord) {
                                st.giveItems(art_of_battle_axe_blade, 1);
                                st.giveItems(enria, 1);
                            } else
                                st.giveItems(ADENA_ID, 49000);
                        st.setMemoState("arrogant_quest", String.valueOf(15), true);
                        htmltext = "magister_hanellin_q0348_49.htm";
                    } else if (GetMemoState == 15)
                        htmltext = "magister_hanellin_q0348_50.htm";
                    else if (GetMemoState == 16) {
                        if (st.ownItemCount(q0348_blooded_fabric) > 0 && st.ownItemCount(q0348_white_fabric) == 0)
                            st.giveItems(q0348_white_fabric, 9);
                        else
                            st.giveItems(q0348_white_fabric, 10);
                        st.setCond(26);
                        st.setMemoState("arrogant_quest", String.valueOf(17), true); // по логике должно здесь быть.
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(17), true);
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_hanellin_q0348_51.htm";
                    } else if (GetMemoState == 17 && st.ownItemCount(q0348_white_fabric) > 0)
                        htmltext = "magister_hanellin_q0348_52.htm";
                    else if (GetMemoState == 17 && st.ownItemCount(q0348_blooded_fabric) >= 10 && st.ownItemCount(q0348_white_fabric) == 0)
                        htmltext = "magister_hanellin_q0348_53.htm";
                    else if (GetMemoState == 17 && st.ownItemCount(q0348_blooded_fabric) < 10 && st.ownItemCount(q0348_white_fabric) == 0) {
                        st.giveItems(ADENA_ID, st.ownItemCount(q0348_blooded_fabric) * 1000 + 4000);
                        st.takeItems(q0348_blooded_fabric, -1);
                        st.removeMemo("arrogant_quest_ex_0");
                        st.removeMemo("arrogant_quest_ex_1");
                        st.removeMemo("arrogant_quest");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "magister_hanellin_q0348_54.htm";
                    } else if (GetMemoState == 19)
                        htmltext = "magister_hanellin_q0348_55.htm";
                    else if (GetMemoState == 18 && GetMemoStateEx_1 % 10 < 7) {
                        int i1 = 0;
                        int i2 = 0;
                        int i0 = GetMemoStateEx_1 % 10;
                        if (i0 >= 4) {
                            i1 = i1 + 6;
                            i0 = i0 - 4;
                            i2 = i2 + 1;
                        }
                        if (i0 >= 2) {
                            i0 = i0 - 2;
                            i1 = i1 + 1;
                            i2 = i2 + 1;
                        }
                        if (i0 >= 1) {
                            i1 = i1 + 3;
                            i2 = i2 + 1;
                            i0 = i0 - 1;
                        }
                        if (i0 == 0 && st.ownItemCount(q0348_blooded_fabric) + i1 >= 10)
                            htmltext = "magister_hanellin_q0348_59.htm";
                        else if (i0 == 0 && st.ownItemCount(q0348_blooded_fabric) + i1 < 10) {
                            htmltext = "magister_hanellin_q0348_61.htm";
                            if (i2 == 2)
                                st.giveItems(ADENA_ID, 24000);
                            else if (i2 == 1)
                                st.giveItems(ADENA_ID, 12000);
                            st.removeMemo("arrogant_quest_ex_0");
                            st.removeMemo("arrogant_quest_ex_1");
                            st.removeMemo("arrogant_quest");
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(true);
                        }
                    } else if (GetMemoState == 18 && GetMemoStateEx_1 % 10 == 7) {
                        st.setCond(28);
                        htmltext = "magister_hanellin_q0348_60.htm";
                        st.soundEffect(SOUND_MIDDLE);
                        if (talker_occupation == treasure_hunter || talker_occupation == plains_walker || talker_occupation == abyss_walker || talker_occupation == adventurer || talker_occupation == wind_rider || talker_occupation == ghost_hunter || talker_occupation == m_soul_breaker || talker_occupation == f_soul_breaker || talker_occupation == m_soul_hound || talker_occupation == f_soul_hound || talker_occupation == inspector || talker_occupation == judicator) {
                            if (st.getPlayer().getLevel() < 69) {
                                st.giveItems(kris_edge, 1);
                                st.giveItems(synthesis_cokes, 2);
                            } else if (st.getPlayer().getLevel() >= 69) {
                                st.giveItems(demons_sword_edge, 1);
                                st.giveItems(cokes, 2);
                            }
                        } else if (talker_occupation == tyrant || talker_occupation == grand_khavatari) {
                            if (st.getPlayer().getLevel() < 69) {
                                st.giveItems(arthro_nail_blade, 1);
                                st.giveItems(synthesis_cokes, 2);
                                st.giveItems(cokes, 1);
                            } else if (st.getPlayer().getLevel() >= 69) {
                                st.giveItems(bellion_cestus_edge, 1);
                                st.giveItems(oriharukon_ore, 2);
                            }
                        } else if (talker_occupation == paladin || talker_occupation == dark_avenger || talker_occupation == prophet || talker_occupation == temple_knight || talker_occupation == swordsinger || talker_occupation == shillien_knight || talker_occupation == bladedancer || talker_occupation == shillien_elder || talker_occupation == phoenix_knight || talker_occupation == hell_knight || talker_occupation == hierophant || talker_occupation == evas_templar || talker_occupation == sword_muse || talker_occupation == shillien_templar || talker_occupation == spectral_dancer || talker_occupation == shillien_saint) {
                            if (st.getPlayer().getLevel() < 69) {
                                st.giveItems(kshanberk_blade, 1);
                                st.giveItems(synthesis_cokes, 2);
                            } else if (st.getPlayer().getLevel() >= 69) {
                                st.giveItems(sword_of_damascus_blade, 1);
                                st.giveItems(oriharukon_ore, 2);
                            }
                        } else if (talker_occupation == hawkeye || talker_occupation == silver_ranger || talker_occupation == phantom_ranger || talker_occupation == sagittarius || talker_occupation == moonlight_sentinel || talker_occupation == ghost_sentinel || talker_occupation == arbalester || talker_occupation == trickster) {
                            if (st.getPlayer().getLevel() < 69) {
                                st.giveItems(dark_elven_long_bow_shaft, 1);
                                st.giveItems(synthesis_cokes, 2);
                            } else if (st.getPlayer().getLevel() >= 69) {
                                st.giveItems(hazard_bow_shaft, 1);
                                st.giveItems(coarse_bone_powder, 9);
                            }
                        } else if (talker_occupation == gladiator || talker_occupation == bishop || talker_occupation == elder || talker_occupation == duelist || talker_occupation == cardinal || talker_occupation == evas_saint) {
                            if (st.getPlayer().getLevel() < 69) {
                                st.giveItems(heavy_war_axe_head, 1);
                                st.giveItems(synthesis_cokes, 2);
                                st.giveItems(cokes, 1);
                            } else if (st.getPlayer().getLevel() >= 69) {
                                st.giveItems(art_of_battle_axe_blade, 1);
                                st.giveItems(oriharukon_ore, 2);
                            }
                        } else if (talker_occupation == warlord || talker_occupation == bounty_hunter || talker_occupation == warsmith || talker_occupation == dreadnought || talker_occupation == fortune_seeker || talker_occupation == maestro) {
                            if (st.getPlayer().getLevel() < 63) {
                                st.giveItems(great_axe_head, 1);
                                st.giveItems(enria, 1);
                                st.giveItems(cokes, 1);
                            } else if (st.getPlayer().getLevel() >= 63) {
                                st.giveItems(lancia_blade, 1);
                                st.giveItems(oriharukon_ore, 2);
                            }
                        } else if (talker_occupation == sorcerer || talker_occupation == spellsinger || talker_occupation == overlord || talker_occupation == archmage || talker_occupation == mystic_muse || talker_occupation == dominator) {
                            if (st.getPlayer().getLevel() < 63) {
                                st.giveItems(sprites_staff_head, 1);
                                st.giveItems(oriharukon_ore, 4);
                                st.giveItems(coarse_bone_powder, 1);
                            } else if (st.getPlayer().getLevel() >= 63) {
                                st.giveItems(staff_of_evil_sprit_head, 1);
                                st.giveItems(animal_bone, 5);
                            }
                        } else if (talker_occupation == necromancer || talker_occupation == spellhowler || talker_occupation == soultaker || talker_occupation == storm_screamer) {
                            st.giveItems(hell_knife_edge, 1);
                            st.giveItems(synthesis_cokes, 2);
                            st.giveItems(animal_bone, 2);
                        } else if (talker_occupation == necromancer || talker_occupation == spellhowler || talker_occupation == soultaker || talker_occupation == storm_screamer) {
                            st.giveItems(hell_knife_edge, 1);
                            st.giveItems(synthesis_cokes, 2);
                            st.giveItems(animal_bone, 2);
                        } else if (talker_occupation == destroyer || talker_occupation == titan || talker_occupation == berserker || talker_occupation == doombringer) {
                            st.giveItems(great_sword_blade, 1);
                            st.giveItems(varnish_of_purity, 2);
                            st.giveItems(synthesis_cokes, 2);
                        } else if (talker_occupation == elemental_summoner || talker_occupation == phantom_summoner || talker_occupation == elemental_master || talker_occupation == spectral_master) {
                            st.giveItems(sword_of_damascus_blade, 1);
                            st.giveItems(enria, 1);
                        } else if (talker_occupation == warcryer || talker_occupation == doomcryer) {
                            st.giveItems(sword_of_valhalla_blade, 1);
                            st.giveItems(oriharukon_ore, 1);
                            st.giveItems(varnish_of_purity, 1);
                        } else if (talker_occupation == warlock || talker_occupation == arcana_lord) {
                            st.giveItems(art_of_battle_axe_blade, 1);
                            st.giveItems(enria, 1);
                        } else
                            st.giveItems(ADENA_ID, 49000);
                        st.setMemoState("arrogant_quest", String.valueOf(19), true);
                    }
                } else if (npcId == claudia_a) {
                    if (st.ownItemCount(q0348_hanellins_letter2) > 0) {
                        int i0 = GetMemoStateEx_1;
                        i0 = i0 + 100;
                        if (i0 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 181472, 7158, -2725));
                        else {
                            st.setCond(9);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                        st.takeItems(q0348_hanellins_letter2, 1);
                        htmltext = "claudia_a_q0348_01.htm";
                    } else if (GetMemoState < 8 && GetMemoStateEx_1 % 1000 / 100 == 1 && st.ownItemCount(q0348_key_of_ark2) == 0) {
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 181472, 7158, -2725));
                        htmltext = "claudia_a_q0348_03.htm";
                    } else if (st.ownItemCount(q0348_key_of_ark2) > 0) {
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 181472, 7158, -2725));
                        htmltext = "claudia_a_q0348_04.htm";
                    } else if (st.ownItemCount(book_of_saint) > 0)
                        htmltext = "claudia_a_q0348_05.htm";
                } else if (npcId == holy_ark_2) {
                    if (GetMemoState < 8 && GetMemoStateEx_1 % 1000 / 100 == 1 && st.ownItemCount(q0348_key_of_ark2) == 0) {
                        htmltext = "holy_ark_2_q0348_01.htm";
                        int i0 = GetMemoStateEx_1;
                        if (i0 % 10 != 0) {
                            st.setCond(10);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.soundEffect(SOUND_MIDDLE);
                        NpcInstance ark_guardian_e = st.addSpawn(ark_guardian_elberoth, st.getPlayer().getX(), st.getPlayer().getY(), st.getPlayer().getZ());
                        Functions.npcSay(ark_guardian_e, NpcString.THAT_DOESNT_BELONG_TO_YOU);
                        st.startQuestTimer("34804", 1000 * 600, ark_guardian_e);
                    } else if (st.ownItemCount(q0348_key_of_ark2) > 0) {
                        st.giveItems(book_of_saint, 1);
                        st.takeItems(q0348_key_of_ark2, 1);
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.DELETE_ARROW, RadarControl.RadarType.ARROW, 181472, 7158, -2725));
                        if (GetMemoStateEx_1 % 10 == 0 && st.ownItemCount(blood_of_saint) > 0 && st.ownItemCount(bough_of_saint) > 0) {
                            st.setCond(21);
                            st.soundEffect(SOUND_MIDDLE);
                        } else if (GetMemoStateEx_1 % 10 != 0) {
                            st.setCond(12);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(GetMemoStateEx_1 - 200), true);
                        if ((GetMemoStateEx_1 - 200) % 1000 / 100 == 0)
                            st.setMemoState("arrogant_quest_ex_0", String.valueOf(GetMemoStateEx_0 + 1), true);
                        if ((GetMemoStateEx_1 - 200) % 10 == 2)
                            st.setMemoState("arrogant_quest_ex_0", String.valueOf(8), true);
                        htmltext = "holy_ark_2_q0348_02.htm";
                    } else if (GetMemoState <= 8 && GetMemoStateEx_1 % 1000 / 100 == 0 && st.ownItemCount(q0348_key_of_ark2) == 0 && st.ownItemCount(book_of_saint) > 0)
                        htmltext = "holy_ark_2_q0348_03.htm";
                } else if (npcId == martian) {
                    if (st.ownItemCount(q0348_hanellins_letter3) > 0) {
                        int i0 = GetMemoStateEx_1;
                        i0 = i0 + 1000;
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 50693, 158674, 376));
                        else {
                            st.setCond(13);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                        st.takeItems(q0348_hanellins_letter3, 1);
                        htmltext = "martian_q0348_01.htm";
                    } else if (GetMemoState < 8 && GetMemoStateEx_1 % 10000 / 1000 == 1 && st.ownItemCount(q0348_key_of_ark3) == 0) {
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 50693, 158674, 376));
                        htmltext = "martian_q0348_03.htm";
                    } else if (st.ownItemCount(q0348_key_of_ark3) > 0) {
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 50693, 158674, 376));
                        htmltext = "martian_q0348_04.htm";
                    } else if (st.ownItemCount(bough_of_saint) > 0)
                        htmltext = "martian_q0348_05.htm";
                } else if (npcId == holy_ark_3) {
                    if (GetMemoState < 8 && GetMemoStateEx_1 % 10000 / 1000 == 1 && st.ownItemCount(q0348_key_of_ark3) == 0) {
                        htmltext = "holy_ark_3_q0348_01.htm";
                        int i0 = GetMemoStateEx_1;
                        if (i0 % 10 != 0) {
                            st.setCond(14);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.soundEffect(SOUND_MIDDLE);
                        NpcInstance ark_guardian_s = st.addSpawn(ark_guardian_shadowfang, st.getPlayer().getX(), st.getPlayer().getY(), st.getPlayer().getZ());
                        Functions.npcSay(ark_guardian_s, NpcString.GET_OUT_OF_MY_SIGHT_YOU_INFIDELS);
                        st.startQuestTimer("34803", 1000 * 600, ark_guardian_s);
                    } else if (st.ownItemCount(q0348_key_of_ark3) > 0) {
                        st.giveItems(bough_of_saint, 1);
                        st.takeItems(q0348_key_of_ark3, 1);
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.DELETE_ARROW, RadarControl.RadarType.ARROW, 50693, 158674, 376));
                        if (GetMemoStateEx_1 % 10 == 0 && st.ownItemCount(blood_of_saint) > 0 && st.ownItemCount(book_of_saint) > 0) {
                            st.setCond(21);
                            st.soundEffect(SOUND_MIDDLE);
                        } else if (GetMemoStateEx_1 % 10 != 0) {
                            st.setCond(16);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(GetMemoStateEx_1 - 2000), true);
                        if ((GetMemoStateEx_1 - 2000) % 10000 / 1000 == 0)
                            st.setMemoState("arrogant_quest_ex_0", String.valueOf(GetMemoStateEx_0 + 1), true);
                        if ((GetMemoStateEx_1 - 2000) % 10 == 3)
                            st.setMemoState("arrogant_quest_ex_0", String.valueOf(8), true);
                        htmltext = "holy_ark_3_q0348_02.htm";
                    } else if (GetMemoState <= 8 && GetMemoStateEx_1 % 10000 / 1000 == 0 && st.ownItemCount(q0348_key_of_ark3) == 0 && st.ownItemCount(bough_of_saint) > 0)
                        htmltext = "holy_ark_3_q0348_03.htm";
                } else if (npcId == harne) {
                    if (st.ownItemCount(q0348_hanellins_letter1) > 0) {
                        int i0 = GetMemoStateEx_1;
                        i0 = i0 + 10;
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 2908, 44128, -2712));
                        else {
                            st.setCond(17);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                        st.takeItems(q0348_hanellins_letter1, 1);
                        htmltext = "harne_q0348_01.htm";
                    } else if (GetMemoState < 8 && GetMemoStateEx_1 % 100 / 10 == 1 && st.ownItemCount(q0348_key_of_ark1) == 0) {
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 2908, 44128, -2712));
                        htmltext = "harne_q0348_03.htm";
                    } else if (st.ownItemCount(q0348_key_of_ark1) > 0) {
                        if (GetMemoStateEx_1 % 10 == 0)
                            st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, 2908, 44128, -2712));
                        htmltext = "harne_q0348_04.htm";
                    } else if (st.ownItemCount(blood_of_saint) > 0)
                        htmltext = "harne_q0348_05.htm";
                } else if (npcId == ark_guardians_corpse) {
                    if (GetMemoState < 8 && GetMemoStateEx_1 % 100 / 10 == 1 && st.ownItemCount(q0348_key_of_ark1) == 0 && st.ownItemCount(blood_of_saint) == 0) {
                        htmltext = "ark_guardians_corpse_q0348_01.htm";
                        int i0 = GetMemoStateEx_1;
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.DELETE_ARROW, RadarControl.RadarType.ARROW, 2908, 44128, -2712));
                        if (i0 % 10 != 0) {
                            st.setCond(18);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.soundEffect(SOUND_MIDDLE);
                        NpcInstance angel_k = st.addSpawn(angel_killer, st.getPlayer().getX(), st.getPlayer().getY(), st.getPlayer().getZ());
                        Functions.npcSay(angel_k, NpcString.I_HAVE_THE_KEY);
                        st.startQuestTimer("34801", 1000 * 600, angel_k);
                    } else if (GetMemoState < 8 && GetMemoStateEx_1 % 100 / 10 == 2 && st.ownItemCount(q0348_key_of_ark1) == 0 && st.ownItemCount(blood_of_saint) == 0) {
                        st.giveItems(q0348_key_of_ark1, 1);
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, -418, 44174, -3568));
                        htmltext = "ark_guardians_corpse_q0348_02.htm";
                    } else if (st.ownItemCount(q0348_key_of_ark1) > 0 || st.ownItemCount(blood_of_saint) > 0)
                        htmltext = "ark_guardians_corpse_q0348_03.htm";
                } else if (npcId == holy_ark_1) {
                    if (st.ownItemCount(q0348_key_of_ark1) == 1) {
                        st.giveItems(blood_of_saint, 1);
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.DELETE_ARROW, RadarControl.RadarType.ARROW, -418, 44174, -3568));
                        if (GetMemoStateEx_1 % 10 == 0 && st.ownItemCount(book_of_saint) > 0 && st.ownItemCount(bough_of_saint) > 0) {
                            st.setCond(21);
                            st.soundEffect(SOUND_MIDDLE);
                        } else if (GetMemoStateEx_1 % 10 != 0) {
                            st.setCond(20);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.takeItems(q0348_key_of_ark1, 1);
                        st.setMemoState("arrogant_quest", String.valueOf(8), true);
                        st.setMemoState("arrogant_quest_ex_1", String.valueOf(GetMemoStateEx_1 - 20), true);
                        if ((GetMemoStateEx_1 - 20) % 100 / 10 == 0)
                            st.setMemoState("arrogant_quest_ex_0", String.valueOf(GetMemoStateEx_0 + 1), true);
                        if ((GetMemoStateEx_1 - 20) % 10 == 1)
                            st.setMemoState("arrogant_quest_ex_0", String.valueOf(8), true);
                        htmltext = "holy_ark_1_q0348_02.htm";
                    } else if (GetMemoState <= 8 && GetMemoStateEx_1 % 100 / 10 == 0 && st.ownItemCount(q0348_key_of_ark1) == 0 && st.ownItemCount(blood_of_saint) > 0)
                        htmltext = "holy_ark_1_q0348_03.htm";
                    else if (GetMemoState < 8 && GetMemoStateEx_1 % 100 / 10 == 1 && st.ownItemCount(q0348_key_of_ark1) == 0 && st.ownItemCount(blood_of_saint) == 0)
                        htmltext = "holy_ark_1_q0348_04.htm";
                } else if (npcId == iason_haine) {
                    if (GetMemoStateEx_0 == 18 && GetMemoStateEx_1 % 8 < 4) {
                        if (st.ownItemCount(q0348_blooded_fabric) >= 6) {
                            st.takeItems(q0348_blooded_fabric, 6);
                            int i0 = GetMemoStateEx_1 + 4;
                            st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                            htmltext = "iason_haine_q0348_01.htm";
                        } else if (st.ownItemCount(q0348_blooded_fabric) < 6)
                            htmltext = "iason_haine_q0348_03.htm";
                    } else if (GetMemoStateEx_0 == 18 && GetMemoStateEx_1 % 8 >= 4)
                        htmltext = "iason_haine_q0348_02.htm";
                } else if (npcId == hardin) {
                    if (GetMemoStateEx_0 == 18 && GetMemoStateEx_1 % 4 < 2) {
                        if (st.ownItemCount(q0348_blooded_fabric) >= 1) {
                            st.takeItems(q0348_blooded_fabric, 1);
                            int i0 = GetMemoStateEx_1 + 2;
                            st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                            htmltext = "hardin_q0348_01.htm";
                        } else if (st.ownItemCount(q0348_blooded_fabric) < 3)
                            htmltext = "hardin_q0348_03.htm";
                    } else if (GetMemoStateEx_0 == 18 && GetMemoStateEx_1 % 4 >= 2)
                        htmltext = "hardin_q0348_02.htm";
                } else if (npcId == sir_gustaf_athebaldt)
                    if (GetMemoStateEx_0 == 18 && GetMemoStateEx_1 % 2 == 0) {
                        if (st.ownItemCount(q0348_blooded_fabric) >= 3) {
                            st.takeItems(q0348_blooded_fabric, 3);
                            int i0 = GetMemoStateEx_1 + 1;
                            st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                            htmltext = "sir_gustaf_athebaldt_q0348_01.htm";
                        } else if (st.ownItemCount(q0348_blooded_fabric) < 3)
                            htmltext = "sir_gustaf_athebaldt_q0348_03.htm";
                    } else if (GetMemoStateEx_0 == 18 && GetMemoStateEx_1 % 2 == 1)
                        htmltext = "sir_gustaf_athebaldt_q0348_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onAttack(NpcInstance npc, QuestState st) {
        int GetMemoStateEx_1 = st.getInt("arrogant_quest_ex_1");
        int GetMemoStateEx_0 = st.getInt("arrogant_quest_ex_0");
        int npcId = npc.getNpcId();
        if (npcId == ark_guardian_elberoth && npc != null)
            Functions.npcSay(npc, NpcString.SORRY_ABOUT_THIS_BUT_I_MUST_KILL_YOU_NOW);
        else if (npcId == ark_guardian_shadowfang && npc != null)
            Functions.npcSay(npc, NpcString.I_SHALL_DRENCH_THIS_MOUNTAIN_WITH_YOUR_BLOOD);
        else if (npcId == angel_killer && npc != null) {
            if (GetMemoStateEx_0 < 8 && GetMemoStateEx_1 % 100 / 10 == 1 && st.ownItemCount(q0348_key_of_ark1) == 0 && st.ownItemCount(blood_of_saint) == 0) {
                if (npc.getCurrentHp() < npc.getMaxHp() * 0.300000) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 10;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (i0 % 10 == 0) {
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.DELETE_ARROW, RadarControl.RadarType.ARROW, -2908, 44128, -2712));
                        st.getPlayer().sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, -2908, 44128, -2712));
                    } else {
                        st.setCond(19);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                    Functions.npcSay(npc, NpcString.HA_THAT_WAS_FUN_IF_YOU_WISH_TO_FIND_THE_KEY_SEARCH_THE_CORPSE);
                    if (npc != null)
                        npc.deleteMe();
                }
            } else if (GetMemoStateEx_0 < 8 && GetMemoStateEx_1 % 100 / 10 == 2 && st.ownItemCount(q0348_key_of_ark1) == 0 && st.ownItemCount(blood_of_saint) == 0) {
                Functions.npcSay(npc, NpcString.WE_DONT_HAVE_ANY_FURTHER_BUSINESS_TO_DISCUSS);
                if (npc != null)
                    npc.deleteMe();
            } else if (st.ownItemCount(q0348_key_of_ark1) >= 1 || st.ownItemCount(blood_of_saint) >= 1) {
                Functions.npcSay(npc, NpcString.WE_DONT_HAVE_ANY_FURTHER_BUSINESS_TO_DISCUSS);
                if (npc != null)
                    npc.deleteMe();
            }
        } else if (npcId == platinum_tribe_shaman) {
            if (npc.getCurrentHp() < npc.getMaxHp() * 0.500000)
                if (GetMemoStateEx_0 == 12 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 60;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 60 > 80000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.removeMemo("arrogant_quest");
                        st.removeMemo("arrogant_quest_ex_0");
                        st.removeMemo("arrogant_quest_ex_1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                } else if (GetMemoStateEx_0 == 13 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 60;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 60 > 100000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.setMemoState("arrogant_quest", String.valueOf(14), true); // по логике должно быть!
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(14), true);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
        } else if (npcId == platinum_tribe_lord)
            if (npc.getCurrentHp() < npc.getMaxHp() * 0.500000)
                if (GetMemoStateEx_0 == 12 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 70;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 70 > 80000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.removeMemo("arrogant_quest");
                        st.removeMemo("arrogant_quest_ex_0");
                        st.removeMemo("arrogant_quest_ex_1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                } else if (GetMemoStateEx_0 == 13 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 70;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 70 > 100000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.setMemoState("arrogant_quest", String.valueOf(14), true); // по логике должно быть!
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(14), true);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
        return null;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("arrogant_quest");
        int GetMemoStateEx_1 = st.getInt("arrogant_quest_ex_1");
        int GetMemoStateEx_0 = st.getInt("arrogant_quest_ex_0");
        int npcId = npc.getNpcId();
        if (npcId == ark_guardian_elberoth) {
            if (GetMemoStateEx_0 < 8 && GetMemoStateEx_1 % 1000 / 100 == 1 && st.ownItemCount(q0348_key_of_ark2) == 0 && st.ownItemCount(book_of_saint) == 0) {
                int i0 = GetMemoStateEx_1;
                i0 = i0 + 100;
                st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                if (i0 % 10 != 0)
                    st.setCond(11);
                st.giveItems(q0348_key_of_ark2, 1);
                st.soundEffect(SOUND_ITEMGET);
                Functions.npcSay(npc, NpcString.YOU_FOOLS_WILL_GET_WHATS_COMING_TO_YOU);
            }
        } else if (npcId == ark_guardian_shadowfang) {
            if (GetMemoStateEx_0 < 8 && GetMemoStateEx_1 % 10000 / 1000 == 1 && st.ownItemCount(q0348_key_of_ark3) == 0 && st.ownItemCount(bough_of_saint) == 0) {
                int i0 = GetMemoStateEx_1;
                i0 = i0 + 1000;
                if (i0 % 10 != 0)
                    st.setCond(15);
                st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                st.giveItems(q0348_key_of_ark3, 1);
                st.soundEffect(SOUND_ITEMGET);
                Functions.npcSay(npc, NpcString.YOU_GUYS_WOULDNT_KNOW);
            }
        } else if (npcId == platinum_tribe_shaman) {
            if ((GetMemoStateEx_0 == 12 || GetMemoStateEx_0 == 13) && st.ownItemCount(q0348_white_fabric) > 0)
                if (GetMemoStateEx_0 == 12 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 600;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 600 > 80000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.removeMemo("arrogant_quest");
                        st.removeMemo("arrogant_quest_ex_0");
                        st.removeMemo("arrogant_quest_ex_1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                } else if (GetMemoStateEx_0 == 13 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 600;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 600 > 100000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.setMemoState("arrogant_quest", String.valueOf(14), true); // по логике должно быть!
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(14), true);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
        } else if (npcId == platinum_tribe_lord) {
            if ((GetMemoStateEx_0 == 12 || GetMemoStateEx_0 == 13) && st.ownItemCount(q0348_white_fabric) > 0)
                if (GetMemoStateEx_0 == 12 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 700;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 700 > 80000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.removeMemo("arrogant_quest");
                        st.removeMemo("arrogant_quest_ex_0");
                        st.removeMemo("arrogant_quest_ex_1");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                    }
                } else if (GetMemoStateEx_0 == 13 && st.ownItemCount(q0348_white_fabric) > 0) {
                    int i0 = GetMemoStateEx_1;
                    i0 = i0 + 700;
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                    if (GetMemoStateEx_1 + 700 > 100000) {
                        st.giveItems(q0348_blooded_fabric, 1);
                        st.takeItems(q0348_white_fabric, 1);
                        st.setMemoState("arrogant_quest", String.valueOf(14), true); // по логике должно быть!
                        st.setMemoState("arrogant_quest_ex_0", String.valueOf(14), true);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                }
        } else if (npcId == ynglzu || npcId == paliote) {
            if (GetMemoState == 1 && st.ownItemCount(q_shell_of_evil_re) == 0) {
                st.giveItems(q_shell_of_evil_re, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == guardian_angel || npcId == seal_angel || npcId == seal_angel_r) {
            if (GetMemoStateEx_0 == 17 && st.ownItemCount(q0348_white_fabric) > 0) {
                int i0 = GetMemoStateEx_1;
                i0 = i0 + Rnd.get(100) + 100;
                st.setMemoState("arrogant_quest_ex_1", String.valueOf(i0), true);
                if (GetMemoStateEx_1 + i0 > 750) {
                    st.giveItems(q0348_blooded_fabric, 1);
                    st.takeItems(q0348_white_fabric, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    st.setMemoState("arrogant_quest_ex_1", String.valueOf(0), true);
                }
            }
        }
        return null;
    }
}