package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 18/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _334_TheWishingPotion extends Quest {
    // npc
    private static final int alchemist_matild = 30738;
    private static final int fairy_rupina = 30742;
    private static final int torai = 30557;
    private static final int wisdom_chest = 30743;
    // mobs
    private static final int whispering_wind = 20078;
    private static final int ant_soldier = 20087;
    private static final int ant_warrior_captain = 20088;
    private static final int silenos = 20168;
    private static final int tyrant = 20192;
    private static final int tyrant_kingpin = 20193;
    private static final int amber_basilisk = 20199;
    private static final int mist_horror_ripper = 20227;
    private static final int turak_bugbear = 20248;
    private static final int turak_bugbear_warrior = 20249;
    private static final int glass_jaguar = 20250;
    private static final int grima = 27135;
    private static final int succubus_of_seduction = 27136;
    private static final int great_demon_king = 27138;
    private static final int secret_keeper_tree = 27139;
    private static final int dlord_alexandrosanches = 27153;
    private static final int abyssking_bonaparterius = 27154;
    private static final int eviloverlord_ramsebalius = 27155;
    // questitem
    private static final int q_amber_scale = 3684;
    private static final int q_wind_soulstone = 3685;
    private static final int q_glass_eye = 3686;
    private static final int q_horror_ectoplasm = 3687;
    private static final int q_silenos_horn = 3688;
    private static final int q_ant_soldier_aphid = 3689;
    private static final int q_tyrants_chitin = 3690;
    private static final int q_bugbear_blood = 3691;
    // etcitem
    private static final int demons_tunic = 441;
    private static final int demons_hose = 472;
    private static final int necklace_of_grace = 931;
    private static final int demons_boots = 2435;
    private static final int demons_gloves = 2459;
    private static final int q_wish_potion = 3467;
    private static final int q_ancient_crown = 3468;
    private static final int q_certificate_of_royalty = 3469;
    private static final int q_alchemy_text = 3678;
    private static final int q_secret_book_of_potion = 3679;
    private static final int q_potion_recipe_1 = 3680;
    private static final int q_potion_recipe_2 = 3681;
    private static final int q_matilds_orb = 3682;
    private static final int q_fobbiden_love_scroll = 3683;
    private static final int demons_tunic_fabric = 1979;
    private static final int demons_hose_pattern = 1980;
    private static final int demons_boots_fabric = 2952;
    private static final int demons_gloves_fabric = 2953;
    private static final int q_musicnote_love = 4408;
    private static final int q_musicnote_battle = 4409;
    private static final int q_gold_circlet = 12766;
    private static final int q_silver_circlet = 12767;
    // spawn
    private int myself_av_quest0 = 0;
    // memo
    private int talker_flag = 0;
    private int myself_i_quest0 = 0;
    private int succubus = 0;
    private int grima_count = 0;

    public _334_TheWishingPotion() {
        super(false);
        addStartNpc(alchemist_matild);
        addTalkId(fairy_rupina, torai, wisdom_chest);
        addKillId(whispering_wind, ant_soldier, ant_warrior_captain, silenos, tyrant, tyrant_kingpin, amber_basilisk, mist_horror_ripper, turak_bugbear, turak_bugbear_warrior, glass_jaguar, grima, succubus_of_seduction, great_demon_king, secret_keeper_tree, dlord_alexandrosanches, abyssking_bonaparterius, eviloverlord_ramsebalius);
        addQuestItem(q_amber_scale, q_wind_soulstone, q_glass_eye, q_horror_ectoplasm, q_silenos_horn, q_ant_soldier_aphid, q_tyrants_chitin, q_bugbear_blood);
        addLevelCheck(30);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        NpcInstance matild = GameObjectsStorage.getByNpcId(alchemist_matild);
        int npcId = npc.getNpcId();
        if (npcId == alchemist_matild) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("wish_potion", String.valueOf(1), true);
                if (st.ownItemCount(q_alchemy_text) == 0)
                    st.giveItems(q_alchemy_text, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "alchemist_matild_q0334_04.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "alchemist_matild_q0334_03.htm";
            else if (event.equalsIgnoreCase("reply=2")) {
                st.setCond(3);
                st.setMemoState("wish_potion", String.valueOf(2), true);
                st.takeItems(q_secret_book_of_potion, -1);
                st.takeItems(q_alchemy_text, -1);
                st.giveItems(q_potion_recipe_1, 1);
                st.giveItems(q_potion_recipe_2, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "alchemist_matild_q0334_07.htm";
            } else if (event.equalsIgnoreCase("reply=3"))
                htmltext = "alchemist_matild_q0334_10.htm";
            else if (event.equalsIgnoreCase("reply=4") && st.ownItemCount(q_amber_scale) > 0 && st.ownItemCount(q_glass_eye) > 0 && st.ownItemCount(q_horror_ectoplasm) > 0 && st.ownItemCount(q_silenos_horn) > 0 && st.ownItemCount(q_ant_soldier_aphid) > 0 && st.ownItemCount(q_tyrants_chitin) > 0 && st.ownItemCount(q_bugbear_blood) > 0 && st.ownItemCount(q_wind_soulstone) > 0 && st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0) {
                st.setCond(5);
                st.setMemoState("wish_potion", String.valueOf(2), true);
                st.giveItems(q_wish_potion, 1);
                if (st.ownItemCount(q_matilds_orb) == 0)
                    st.giveItems(q_matilds_orb, 1);
                st.takeItems(q_amber_scale, 1);
                st.takeItems(q_glass_eye, 1);
                st.takeItems(q_horror_ectoplasm, 1);
                st.takeItems(q_silenos_horn, 1);
                st.takeItems(q_ant_soldier_aphid, 1);
                st.takeItems(q_tyrants_chitin, 1);
                st.takeItems(q_bugbear_blood, 1);
                st.takeItems(q_wind_soulstone, 1);
                st.takeItems(q_potion_recipe_1, -1);
                st.takeItems(q_potion_recipe_2, -1);
                st.soundEffect(SOUND_ITEMGET);
                htmltext = "alchemist_matild_q0334_11.htm";
            } else if (event.equalsIgnoreCase("reply=5")) {
                if (st.ownItemCount(q_wish_potion) > 0)
                    htmltext = "alchemist_matild_q0334_13.htm";
                else
                    htmltext = "alchemist_matild_q0334_14.htm";
            } else if (event.equalsIgnoreCase("reply=6")) {
                if (st.ownItemCount(q_wish_potion) > 0)
                    htmltext = "alchemist_matild_q0334_15a.htm";
                else {
                    htmltext = "alchemist_matild_q0334_15.htm";
                    st.giveItems(q_potion_recipe_1, 1);
                    st.giveItems(q_potion_recipe_2, 1);
                }
            } else if (event.equalsIgnoreCase("reply=7")) {
                if (st.ownItemCount(q_wish_potion) > 0) {
                    if (myself_av_quest0 == 0) {
                        htmltext = "alchemist_matild_q0334_16.htm";
                        st.takeItems(q_wish_potion, 1);
                        myself_i_quest0 = 1;
                        talker_flag = 1;
                        st.startQuestTimer("2336008", 3 * 1000, matild);
                    } else
                        htmltext = "alchemist_matild_q0334_20.htm";
                } else
                    htmltext = "alchemist_matild_q0334_14.htm";
            } else if (event.equalsIgnoreCase("reply=8")) {
                if (st.ownItemCount(q_wish_potion) > 0) {
                    if (myself_av_quest0 == 0) {
                        htmltext = "alchemist_matild_q0334_17.htm";
                        st.takeItems(q_wish_potion, 1);
                        myself_i_quest0 = 2;
                        talker_flag = 2;
                        st.startQuestTimer("2336008", 3 * 1000, matild);
                    } else
                        htmltext = "alchemist_matild_q0334_20.htm";
                } else
                    htmltext = "alchemist_matild_q0334_14.htm";
            } else if (event.equalsIgnoreCase("reply=9")) {
                if (st.ownItemCount(q_wish_potion) > 0) {
                    if (myself_av_quest0 == 0) {
                        htmltext = "alchemist_matild_q0334_18.htm";
                        st.takeItems(q_wish_potion, 1);
                        myself_i_quest0 = 3;
                        talker_flag = 3;
                        st.startQuestTimer("2336008", 3 * 1000, matild);
                    } else
                        htmltext = "alchemist_matild_q0334_20.htm";
                } else
                    htmltext = "alchemist_matild_q0334_14.htm";
            } else if (event.equalsIgnoreCase("reply=10")) {
                if (st.ownItemCount(q_wish_potion) > 0) {
                    if (myself_av_quest0 == 0) {
                        htmltext = "alchemist_matild_q0334_18.htm";
                        st.takeItems(q_wish_potion, 1);
                        myself_i_quest0 = 4;
                        talker_flag = 4;
                        st.startQuestTimer("2336008", 3 * 1000, matild);
                    } else
                        htmltext = "alchemist_matild_q0334_20.htm";
                } else
                    htmltext = "alchemist_matild_q0334_14.htm";
            } else if (event.equalsIgnoreCase("2336008")) {
                Functions.npcSay(matild, NpcString.OK_EVERYBODY_PRAY_FERVENTLY);
                st.startQuestTimer("2336009", 4 * 1000, matild);
                return null;
            } else if (event.equalsIgnoreCase("2336009")) {
                Functions.npcSay(matild, NpcString.BOTH_HANDS_TO_HEAVEN_EVERYBODY_YELL_TOGETHER);
                st.startQuestTimer("2336010", 4 * 1000, matild);
                return null;
            } else if (event.equalsIgnoreCase("2336010")) {
                int i0 = 0;
                Functions.npcSay(matild, NpcString.ONE_TWO_MAY_YOUR_DREAMS_COME_TRUE);
                if (myself_i_quest0 == 1)
                    i0 = Rnd.get(2);
                else if (myself_i_quest0 == 3 || myself_i_quest0 == 4 || myself_i_quest0 == 2)
                    i0 = Rnd.get(3);
                switch (i0) {
                    case 0:
                        if (myself_i_quest0 == 1) {
                            NpcInstance rupina = st.addSpawn(fairy_rupina);
                            Functions.npcSay(rupina, NpcString.I_WILL_MAKE_YOUR_LOVE_COME_TRUE_LOVE_LOVE_LOVE);
                            st.startQuestTimer("2336001", 120 * 1000, rupina);
                            myself_av_quest0 = 1;
                        } else if (myself_i_quest0 == 2) {
                            NpcInstance grima0 = st.addSpawn(grima, 120000);
                            NpcInstance grima1 = st.addSpawn(grima, 120000);
                            NpcInstance grima2 = st.addSpawn(grima, 120000);
                            Functions.npcSay(grima0, NpcString.OH_OH_OH);
                            Functions.npcSay(grima1, NpcString.OH_OH_OH);
                            Functions.npcSay(grima2, NpcString.OH_OH_OH);
                            st.startQuestTimer("2336002", 120 * 1000, matild);
                            myself_av_quest0 = 1;
                        } else if (myself_i_quest0 == 3) {
                            st.soundEffect(SOUND_ITEMGET);
                            st.giveItems(q_certificate_of_royalty, 1);
                        } else if (myself_i_quest0 == 4) {
                            NpcInstance chest = st.addSpawn(wisdom_chest);
                            Functions.npcSay(chest, NpcString.I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM);
                            st.startQuestTimer("2336007", 120 * 1000, chest);
                            myself_av_quest0 = 1;
                        }
                        break;
                    case 1:
                        if (myself_i_quest0 == 1) {
                            NpcInstance succubus0 = st.addSpawn(succubus_of_seduction, 200000);
                            NpcInstance succubus1 = st.addSpawn(succubus_of_seduction, 200000);
                            NpcInstance succubus2 = st.addSpawn(succubus_of_seduction, 200000);
                            Functions.npcSay(succubus0, NpcString.DO_YOU_WANT_US_TO_LOVE_YOU_OH);
                            Functions.npcSay(succubus1, NpcString.DO_YOU_WANT_US_TO_LOVE_YOU_OH);
                            Functions.npcSay(succubus2, NpcString.DO_YOU_WANT_US_TO_LOVE_YOU_OH);
                            st.startQuestTimer("2336003", 1000 * 200, matild);
                            myself_av_quest0 = 1;
                        } else if (myself_i_quest0 == 2) {
                            st.soundEffect(SOUND_ITEMGET);
                            st.giveItems(ADENA_ID, 10000);
                        } else if (myself_i_quest0 == 3) {
                            NpcInstance dlord = st.addSpawn(dlord_alexandrosanches);
                            Functions.npcSay(dlord, NpcString.WHO_IS_CALLING_THE_LORD_OF_DARKNESS);
                            st.startQuestTimer("2336004", 1000 * 200, dlord);
                            myself_av_quest0 = 1;
                        } else if (myself_i_quest0 == 4) {
                            NpcInstance chest = st.addSpawn(wisdom_chest);
                            Functions.npcSay(chest, NpcString.I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM);
                            st.startQuestTimer("2336007", 120 * 1000, chest);
                            myself_av_quest0 = 1;
                        }
                        break;
                    case 2:
                        if (myself_i_quest0 == 2) {
                            st.soundEffect(SOUND_ITEMGET);
                            st.giveItems(ADENA_ID, 10000);
                        } else if (myself_i_quest0 == 3) {
                            st.soundEffect(SOUND_ITEMGET);
                            st.giveItems(q_ancient_crown, 1);
                        } else if (myself_i_quest0 == 4) {
                            NpcInstance chest = st.addSpawn(wisdom_chest);
                            Functions.npcSay(chest, NpcString.I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM);
                            st.startQuestTimer("2336007", 120 * 1000, chest);
                            myself_av_quest0 = 1;
                        }
                        break;
                }
                return null;
            } else if (event.equalsIgnoreCase("2336003")) {
                myself_av_quest0 = 0;
                return null;
            } else if (event.equalsIgnoreCase("2336002")) {
                myself_av_quest0 = 0;
                return null;
            }
        } else if (event.equalsIgnoreCase("2336001")) {
            if (npc != null)
                myself_av_quest0 = 0;
            npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("2336007")) {
            if (npc != null)
                myself_av_quest0 = 0;
            npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("2336004")) {
            if (npc != null)
                myself_av_quest0 = 0;
            npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("2336107")) {
            if (npc != null) {
                Functions.npcSay(npc, NpcString.DONT_INTERRUPT_MY_REST_AGAIN);
                Functions.npcSay(npc, NpcString.YOURE_A_GREAT_DEVIL_NOW);
            }
            myself_av_quest0 = 0;
            npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("2336005")) {
            if (npc != null)
                Functions.npcSay(npc, NpcString.OH_ITS_NOT_AN_OPPONENT_OF_MINE_HA_HA_HA);
            myself_av_quest0 = 0;
            npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("2336006")) {
            if (npc != null)
                Functions.npcSay(npc, NpcString.OH_ITS_NOT_AN_OPPONENT_OF_MINE_HA_HA_HA);
            myself_av_quest0 = 0;
            npc.deleteMe();
            return null;
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
                if (npcId == alchemist_matild) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "alchemist_matild_q0334_01.htm";
                            st.exitQuest(true);
                        default:
                            htmltext = "alchemist_matild_q0334_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == alchemist_matild) {
                    if (st.ownItemCount(q_secret_book_of_potion) == 0 && st.ownItemCount(q_alchemy_text) == 1)
                        htmltext = "alchemist_matild_q0334_05.htm";
                    else if (st.ownItemCount(q_secret_book_of_potion) == 1 && st.ownItemCount(q_alchemy_text) == 1)
                        htmltext = "alchemist_matild_q0334_06.htm";
                    else if (st.ownItemCount(q_potion_recipe_1) == 1 && st.ownItemCount(q_potion_recipe_2) == 1 && (st.ownItemCount(q_amber_scale) == 0 || (st.ownItemCount(q_wind_soulstone) > 0 && st.ownItemCount(q_glass_eye) == 0) || st.ownItemCount(q_horror_ectoplasm) == 0 || st.ownItemCount(q_silenos_horn) == 0 || st.ownItemCount(q_ant_soldier_aphid) == 0 || st.ownItemCount(q_tyrants_chitin) == 0 || st.ownItemCount(q_bugbear_blood) == 0))
                        htmltext = "alchemist_matild_q0334_08.htm";
                    else if (st.ownItemCount(q_potion_recipe_1) == 1 && st.ownItemCount(q_potion_recipe_2) == 1 && st.ownItemCount(q_amber_scale) > 0 && st.ownItemCount(q_wind_soulstone) > 0 && st.ownItemCount(q_glass_eye) > 0 && st.ownItemCount(q_horror_ectoplasm) > 0 && st.ownItemCount(q_silenos_horn) > 0 && st.ownItemCount(q_ant_soldier_aphid) > 0 && st.ownItemCount(q_tyrants_chitin) > 0 && st.ownItemCount(q_bugbear_blood) > 0)
                        htmltext = "alchemist_matild_q0334_09.htm";
                    else if (st.ownItemCount(q_matilds_orb) == 1 && st.ownItemCount(q_potion_recipe_1) == 0 && st.ownItemCount(q_potion_recipe_2) == 0 && (st.ownItemCount(q_amber_scale) == 0 || (st.ownItemCount(q_wind_soulstone) > 0 && st.ownItemCount(q_glass_eye) == 0) || st.ownItemCount(q_horror_ectoplasm) == 0 || st.ownItemCount(q_silenos_horn) == 0 || st.ownItemCount(q_ant_soldier_aphid) == 0 || st.ownItemCount(q_tyrants_chitin) == 0 || st.ownItemCount(q_bugbear_blood) == 0))
                        htmltext = "alchemist_matild_q0334_12.htm";
                } else if (npcId == fairy_rupina) {
                    if (talker_flag == 1) {
                        if (Rnd.get(100) < 4) {
                            htmltext = "fairy_rupina_q0334_01.htm";
                            st.giveItems(necklace_of_grace, 1);
                            talker_flag = 0;
                            if (st.isRunningQuestTimer("2336001"))
                                st.cancelQuestTimer("2336001");
                            myself_av_quest0 = 0;
                            npc.deleteMe();
                        } else {
                            htmltext = "fairy_rupina_q0334_02.htm";
                            int i0 = Rnd.get(4);
                            if (i0 == 0)
                                st.giveItems(demons_tunic_fabric, 1);
                            else if (i0 == 1)
                                st.giveItems(demons_hose_pattern, 1);
                            else if (i0 == 2)
                                st.giveItems(demons_boots_fabric, 1);
                            else if (i0 == 3)
                                st.giveItems(demons_gloves_fabric, 1);
                            talker_flag = 0;
                            if (st.isRunningQuestTimer("2336001"))
                                st.cancelQuestTimer("2336001");
                            myself_av_quest0 = 0;
                            npc.deleteMe();
                        }
                    }
                } else if (npcId == torai) {
                    if (st.ownItemCount(q_fobbiden_love_scroll) >= 1) {
                        st.giveItems(ADENA_ID, 500000);
                        st.takeItems(q_fobbiden_love_scroll, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "torai_q0334_01.htm";
                    }
                } else if (npcId == wisdom_chest) {
                    if (talker_flag == 4) {
                        int i0 = Rnd.get(100);
                        if (i0 < 10) {
                            st.giveItems(q_fobbiden_love_scroll, 1);
                            htmltext = "wisdom_chest_q0334_02.htm";
                            talker_flag = 0;
                            myself_av_quest0 = 0;
                            if (st.isRunningQuestTimer("2336007"))
                                st.cancelQuestTimer("2336007");
                            npc.deleteMe();
                        } else if (i0 >= 10 && i0 < 50) {
                            htmltext = "wisdom_chest_q0334_03.htm";
                            int i1 = Rnd.get(4);
                            if (i1 == 0)
                                st.giveItems(demons_tunic_fabric, 1);
                            else if (i1 == 1)
                                st.giveItems(demons_hose_pattern, 1);
                            else if (i1 == 2)
                                st.giveItems(demons_boots_fabric, 1);
                            else
                                st.giveItems(demons_gloves_fabric, 1);
                            talker_flag = 0;
                            myself_av_quest0 = 0;
                            if (st.isRunningQuestTimer("2336007"))
                                st.cancelQuestTimer("2336007");
                            npc.deleteMe();
                        } else if (i0 >= 50 && i0 < 85) {
                            htmltext = "wisdom_chest_q0334_04.htm";
                            int i1 = Rnd.get(2);
                            if (i1 == 0)
                                st.giveItems(q_musicnote_love, 1);
                            else
                                st.giveItems(q_musicnote_battle, 1);
                            talker_flag = 0;
                            myself_av_quest0 = 0;
                            if (st.isRunningQuestTimer("2336007"))
                                st.cancelQuestTimer("2336007");
                            npc.deleteMe();
                        } else if (i0 >= 85 && i0 < 95) {
                            htmltext = "wisdom_chest_q0334_05.htm";
                            int i1 = Rnd.get(4);
                            if (i1 == 0)
                                st.giveItems(demons_tunic, 1);
                            else if (i1 == 1)
                                st.giveItems(demons_hose, 1);
                            else if (i1 == 2)
                                st.giveItems(demons_boots, 1);
                            else
                                st.giveItems(demons_gloves, 1);
                            talker_flag = 0;
                            myself_av_quest0 = 0;
                            if (st.isRunningQuestTimer("2336007"))
                                st.cancelQuestTimer("2336007");
                            npc.deleteMe();
                        } else if (i0 >= 95) {
                            htmltext = "wisdom_chest_q0334_06.htm";
                            int i1 = Rnd.get(2);
                            if (i1 == 0)
                                st.giveItems(q_gold_circlet, 1);
                            else
                                st.giveItems(q_silver_circlet, 1);
                            talker_flag = 0;
                            myself_av_quest0 = 0;
                            if (st.isRunningQuestTimer("2336007"))
                                st.cancelQuestTimer("2336007");
                            npc.deleteMe();
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("wish_potion");
        int npcId = npc.getNpcId();
        if (npcId == whispering_wind) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_wind_soulstone) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_wind_soulstone, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ant_soldier || npcId == ant_warrior_captain) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_ant_soldier_aphid) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_ant_soldier_aphid, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == silenos) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_silenos_horn) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_silenos_horn, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == tyrant || npcId == tyrant_kingpin) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_tyrants_chitin) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_tyrants_chitin, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == amber_basilisk) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_amber_scale) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_amber_scale, 1);
                    if (st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == mist_horror_ripper) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_horror_ectoplasm) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_horror_ectoplasm, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == turak_bugbear || npcId == turak_bugbear_warrior) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_bugbear_blood) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_bugbear_blood, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_glass_eye) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == glass_jaguar) {
            if (st.ownItemCount(q_potion_recipe_1) > 0 && st.ownItemCount(q_potion_recipe_2) > 0 && st.ownItemCount(q_glass_eye) == 0) {
                if (Rnd.get(10) == 0) {
                    st.giveItems(q_glass_eye, 1);
                    if (st.ownItemCount(q_amber_scale) >= 1 && st.ownItemCount(q_wind_soulstone) >= 1 && st.ownItemCount(q_horror_ectoplasm) >= 1 && st.ownItemCount(q_silenos_horn) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_ant_soldier_aphid) >= 1 && st.ownItemCount(q_tyrants_chitin) >= 1 && st.ownItemCount(q_bugbear_blood) >= 1) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == grima) {
            if (GetMemoState == 2 && talker_flag == 2) {
                if (Rnd.get(1000) < 33) {
                    int i0 = Rnd.get(1000);
                    if (i0 == 0)
                        st.giveItems(ADENA_ID, 100000000);
                    else
                        st.giveItems(ADENA_ID, 900000);
                    st.soundEffect(SOUND_ITEMGET);
                }
                grima_count++;
                if (grima_count == 3) {
                    talker_flag = 0;
                    myself_av_quest0 = 0;
                    grima_count = 0;
                    if (st.isRunningQuestTimer("2336002"))
                        st.cancelQuestTimer("2336002");
                }
            }
        } else if (npcId == succubus_of_seduction) {
            if (GetMemoState == 2 && talker_flag == 1 && st.ownItemCount(q_fobbiden_love_scroll) == 0) {
                if (Rnd.get(1000) < 28) {
                    st.giveItems(q_fobbiden_love_scroll, 1);
                    st.soundEffect(SOUND_ITEMGET);
                    talker_flag = 0;
                    myself_av_quest0 = 0;
                    succubus = 0;
                    if (st.isRunningQuestTimer("2336003"))
                        st.cancelQuestTimer("2336003");
                }
                succubus++;
                if (succubus == 3) {
                    talker_flag = 0;
                    myself_av_quest0 = 0;
                    succubus = 0;
                    if (st.isRunningQuestTimer("2336003"))
                        st.cancelQuestTimer("2336003");
                }
            }
        } else if (npcId == great_demon_king) {
            if (GetMemoState == 2 && talker_flag == 3) {
                st.giveItems(ADENA_ID, 1406956);
                st.soundEffect(SOUND_ITEMGET);
                talker_flag = 0;
                myself_av_quest0 = 0;
                if (st.isRunningQuestTimer("2336107"))
                    st.cancelQuestTimer("2336107");
            }
        } else if (npcId == secret_keeper_tree) {
            if (GetMemoState == 1 && st.ownItemCount(q_secret_book_of_potion) == 0) {
                st.setCond(2);
                st.giveItems(q_secret_book_of_potion, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == dlord_alexandrosanches) {
            if (GetMemoState == 2 && talker_flag == 3) {
                Functions.npcSay(npc, NpcString.BONAPARTERIUS_ABYSS_KING_WILL_PUNISH_YOU);
                if (Rnd.get(2) == 0) {
                    NpcInstance abyssking = st.addSpawn(abyssking_bonaparterius);
                    Functions.npcSay(abyssking, NpcString.I_AM_A_GREAT_EMPIRE_BONAPARTERIUS);
                    st.startQuestTimer("2336005", 1000 * 200, abyssking);
                } else {
                    int i1 = Rnd.get(4);
                    if (i1 == 0)
                        st.giveItems(demons_tunic_fabric, 1);
                    else if (i1 == 1)
                        st.giveItems(demons_hose_pattern, 1);
                    else if (i1 == 2)
                        st.giveItems(demons_boots_fabric, 1);
                    else if (i1 == 3)
                        st.giveItems(demons_gloves_fabric, 1);
                    myself_av_quest0 = 0;
                    talker_flag = 0;
                    if (st.isRunningQuestTimer("2336004"))
                        st.cancelQuestTimer("2336004");
                }
            }
        } else if (npcId == abyssking_bonaparterius) {
            if (GetMemoState == 2 && talker_flag == 3) {
                Functions.npcSay(npc, NpcString.REVENGE_IS_OVERLORD_RAMSEBALIUS_OF_THE_EVIL_WORLD);
                if (Rnd.get(2) == 0) {
                    NpcInstance eviloverlord = st.addSpawn(eviloverlord_ramsebalius);
                    Functions.npcSay(eviloverlord, NpcString.LET_YOUR_HEAD_DOWN_BEFORE_THE_LORD);
                    st.startQuestTimer("2336006", 1000 * 200, eviloverlord);
                } else {
                    int i1 = Rnd.get(4);
                    if (i1 == 0)
                        st.giveItems(demons_tunic_fabric, 1);
                    else if (i1 == 1)
                        st.giveItems(demons_hose_pattern, 1);
                    else if (i1 == 2)
                        st.giveItems(demons_boots_fabric, 1);
                    else if (i1 == 3)
                        st.giveItems(demons_gloves_fabric, 1);
                    myself_av_quest0 = 0;
                    talker_flag = 0;
                    if (st.isRunningQuestTimer("2336005"))
                        st.cancelQuestTimer("2336005");
                }
            }
        } else if (npcId == eviloverlord_ramsebalius) {
            if (GetMemoState == 2 && talker_flag == 3) {
                Functions.npcSay(npc, NpcString.OH_GREAT_DEMON_KING);
                if (Rnd.get(2) == 0) {
                    NpcInstance demon_king = st.addSpawn(great_demon_king);
                    Functions.npcSay(demon_king, NpcString.WHO_KILLED_MY_UNDERLING_DEVIL);
                    st.startQuestTimer("2336107", 1000 * 600, demon_king);
                } else {
                    int i1 = Rnd.get(4);
                    if (i1 == 0)
                        st.giveItems(demons_tunic_fabric, 1);
                    else if (i1 == 1)
                        st.giveItems(demons_hose_pattern, 1);
                    else if (i1 == 2)
                        st.giveItems(demons_boots_fabric, 1);
                    else if (i1 == 3)
                        st.giveItems(demons_gloves_fabric, 1);
                    myself_av_quest0 = 0;
                    talker_flag = 0;
                    if (st.isRunningQuestTimer("2336006"))
                        st.cancelQuestTimer("2336006");
                }
            }
        }
        return null;
    }
}