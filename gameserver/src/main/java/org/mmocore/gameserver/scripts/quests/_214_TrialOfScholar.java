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
 * @date 01/11/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _214_TrialOfScholar extends Quest {
    // npc
    private static final int sylvain = 30070;
    private static final int lucas = 30071;
    private static final int valkon = 30103;
    private static final int dieter = 30111;
    private static final int jurek = 30115;
    private static final int trader_edroc = 30230;
    private static final int warehouse_keeper_raut = 30316;
    private static final int blacksmith_poitan = 30458;
    private static final int magister_mirien = 30461;
    private static final int marya = 30608;
    private static final int astrologer_creta = 30609;
    private static final int sage_cronos = 30610;
    private static final int drunkard_treaf = 30611;
    private static final int sage_kasian = 30612;
    // mobs
    private static final int beamer = 20068;
    private static final int medusa = 20158;
    private static final int ghoul = 20201;
    private static final int shackle = 20235;
    private static final int breka_orc_shaman = 20269;
    private static final int shackle_hold = 20279;
    private static final int crimson_bind = 20552;
    private static final int grandis = 20554;
    private static final int enchanted_gargoyle = 20567;
    private static final int leto_lizardman_warrior = 20580;
    // questitem
    private static final int mark_of_scholar = 2674;
    private static final int miriens_sigil1 = 2675;
    private static final int miriens_sigil2 = 2676;
    private static final int miriens_sigil3 = 2677;
    private static final int miriens_instruction = 2678;
    private static final int maryas_letter1 = 2679;
    private static final int maryas_letter2 = 2680;
    private static final int lukas_letter = 2681;
    private static final int lucillas_handbag = 2682;
    private static final int cretas_letter1 = 2683;
    private static final int cretas_painting1 = 2684;
    private static final int cretas_painting2 = 2685;
    private static final int cretas_painting3 = 2686;
    private static final int brown_scroll_scrap = 2687;
    private static final int crystal_of_purity1 = 2688;
    private static final int highpriests_sigil = 2689;
    private static final int gmagisters_sigil = 2690;
    private static final int cronos_sigil = 2691;
    private static final int sylvains_letter = 2692;
    private static final int symbol_of_sylvain = 2693;
    private static final int jureks_list = 2694;
    private static final int beamers_skin = 2695;
    private static final int shamans_necklace = 2696;
    private static final int shackles_scalp = 2697;
    private static final int symbol_of_jurek = 2698;
    private static final int cronos_letter = 2699;
    private static final int dieters_key = 2700;
    private static final int cretas_letter2 = 2701;
    private static final int dieters_letter = 2702;
    private static final int dieters_diary = 2703;
    private static final int rauts_letter_envelope = 2704;
    private static final int treafs_ring = 2705;
    private static final int scripture_chapter_1 = 2706;
    private static final int scripture_chapter_2 = 2707;
    private static final int scripture_chapter_3 = 2708;
    private static final int scripture_chapter_4 = 2709;
    private static final int valkons_request = 2710;
    private static final int poitans_notes = 2711;
    private static final int strong_liquor = 2713;
    private static final int crystal_of_purity2 = 2714;
    private static final int kasians_list = 2715;
    private static final int ghouls_skin = 2716;
    private static final int medusas_blood = 2717;
    private static final int crimsonbinds_ichor = 2718;
    private static final int encht_gargoyles_nail = 2719;
    private static final int symbol_of_cronos = 2720;
    // etcitem
    private static final int q_dimension_diamond = 7562;

    public _214_TrialOfScholar() {
        super(false);
        addStartNpc(magister_mirien);
        addTalkId(sylvain, marya, lucas, astrologer_creta, jurek, sage_cronos, dieter, trader_edroc, warehouse_keeper_raut, drunkard_treaf, valkon, blacksmith_poitan, sage_kasian);
        addKillId(beamer, medusa, ghoul, shackle, breka_orc_shaman, shackle_hold, crimson_bind, grandis, enchanted_gargoyle, leto_lizardman_warrior);
        addQuestItem(scripture_chapter_3, brown_scroll_scrap, beamers_skin, shamans_necklace, shackles_scalp, ghouls_skin, medusas_blood, crimsonbinds_ichor, encht_gargoyles_nail);
        addLevelCheck(35);
        addClassIdCheck(ClassId.wizard, ClassId.elven_wizard, ClassId.dark_wizard);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == magister_mirien) {
            if (event.equalsIgnoreCase("quest_accept")) {
                if (st.ownItemCount(miriens_sigil1) == 0) {
                    st.giveItems(miriens_sigil1, 1);
                }
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.DD1)) {
                    st.giveItems(q_dimension_diamond, 168);
                    st.getPlayer().getPlayerVariables().set(PlayerVariables.DD1, "1", -1);
                    htmltext = "magister_mirien_q0214_04a.htm";
                } else
                    htmltext = "magister_mirien_q0214_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=1")) {
                if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(symbol_of_jurek) == 1) {
                    if (st.ownItemCount(miriens_sigil2) >= 1) {
                        st.setCond(19);
                        st.takeItems(symbol_of_jurek, -1);
                        st.takeItems(miriens_sigil2, 1);
                        st.giveItems(miriens_sigil3, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_mirien_q0214_10.htm";
                    }
                }
            }
        } else if (npcId == sylvain) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1")) {
                st.setCond(2);
                st.giveItems(highpriests_sigil, 1);
                st.giveItems(sylvains_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sylvain_q0214_02.htm";
            }
        } else if (npcId == marya) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1")) {
                if (st.ownItemCount(sylvains_letter) >= 1) {
                    st.setCond(3);
                    st.takeItems(sylvains_letter, 1);
                    st.giveItems(maryas_letter1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "marya_q0214_02.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2"))
                htmltext = "marya_q0214_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3")) {
                if (st.ownItemCount(cretas_letter1) >= 1) {
                    st.setCond(7);
                    st.takeItems(cretas_letter1, 1);
                    st.giveItems(lucillas_handbag, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "marya_q0214_08.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=4")) {
                if (st.ownItemCount(cretas_painting3) >= 1) {
                    st.setCond(13);
                    st.takeItems(brown_scroll_scrap, -1);
                    st.takeItems(cretas_painting3, 1);
                    st.giveItems(crystal_of_purity1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "marya_q0214_14.htm";
                }
            }
        } else if (npcId == lucas) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1")) {
                if (st.ownItemCount(cretas_painting2) >= 1) {
                    st.setCond(10);
                    st.takeItems(cretas_painting2, 1);
                    st.giveItems(cretas_painting3, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "lucas_q0214_04.htm";
                }
            }
        } else if (npcId == astrologer_creta) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "astrologer_creta_q0214_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2"))
                htmltext = "astrologer_creta_q0214_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3"))
                htmltext = "astrologer_creta_q0214_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=4")) {
                if (st.ownItemCount(maryas_letter2) >= 1) {
                    st.setCond(6);
                    st.takeItems(maryas_letter2, 1);
                    st.giveItems(cretas_letter1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "astrologer_creta_q0214_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=5"))
                htmltext = "astrologer_creta_q0214_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=6")) {
                if (st.ownItemCount(lucillas_handbag) >= 1) {
                    st.setCond(8);
                    st.takeItems(lucillas_handbag, 1);
                    st.giveItems(cretas_painting1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "astrologer_creta_q0214_09.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=7"))
                htmltext = "astrologer_creta_q0214_13.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=8")) {
                if (st.ownItemCount(dieters_key) >= 1) {
                    st.setCond(22);
                    st.takeItems(dieters_key, 1);
                    st.giveItems(cretas_letter2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "astrologer_creta_q0214_14.htm";
                }
            }
        } else if (npcId == jurek) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "jurek_q0214_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2")) {
                st.setCond(16);
                st.giveItems(jureks_list, 1);
                st.giveItems(gmagisters_sigil, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "jurek_q0214_03.htm";
            }
        } else if (npcId == sage_cronos) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "sage_cronos_q0214_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2"))
                htmltext = "sage_cronos_q0214_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3"))
                htmltext = "sage_cronos_q0214_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=4"))
                htmltext = "sage_cronos_q0214_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=5"))
                htmltext = "sage_cronos_q0214_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=6"))
                htmltext = "sage_cronos_q0214_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=7"))
                htmltext = "sage_cronos_q0214_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=8"))
                htmltext = "sage_cronos_q0214_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=9")) {
                st.setCond(20);
                st.giveItems(cronos_sigil, 1);
                st.giveItems(cronos_letter, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_cronos_q0214_10.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=10"))
                htmltext = "sage_cronos_q0214_13.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=11")) {
                if (st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(scripture_chapter_2) >= 1 && st.ownItemCount(scripture_chapter_3) >= 1 && st.ownItemCount(scripture_chapter_4) >= 1) {
                    st.setCond(31);
                    st.takeItems(scripture_chapter_1, -1);
                    st.takeItems(scripture_chapter_2, -1);
                    st.takeItems(scripture_chapter_3, -1);
                    st.takeItems(scripture_chapter_4, -1);
                    st.takeItems(cronos_sigil, -1);
                    st.takeItems(treafs_ring, -1);
                    st.takeItems(dieters_diary, -1);
                    st.giveItems(symbol_of_cronos, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sage_cronos_q0214_14.htm";
                }
            }
        } else if (npcId == dieter) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "dieter_q0214_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2"))
                htmltext = "dieter_q0214_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3"))
                htmltext = "dieter_q0214_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=4")) {
                if (st.ownItemCount(cronos_letter) >= 1) {
                    st.setCond(21);
                    st.takeItems(cronos_letter, 1);
                    st.giveItems(dieters_key, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "dieter_q0214_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=5"))
                htmltext = "dieter_q0214_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=6")) {
                if (st.ownItemCount(cronos_letter) >= 1) {
                    st.setCond(23);
                    st.takeItems(cretas_letter2, 1);
                    st.giveItems(dieters_letter, 1);
                    st.giveItems(dieters_diary, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "dieter_q0214_09.htm";
                }
            }
        } else if (npcId == trader_edroc) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1")) {
                if (st.ownItemCount(dieters_letter) >= 1) {
                    st.setCond(24);
                    st.takeItems(dieters_letter, 1);
                    st.giveItems(rauts_letter_envelope, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "trader_edroc_q0214_02.htm";
                }
            }
        } else if (npcId == warehouse_keeper_raut) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1")) {
                if (st.ownItemCount(rauts_letter_envelope) >= 1) {
                    st.setCond(25);
                    st.takeItems(rauts_letter_envelope, 1);
                    st.giveItems(scripture_chapter_1, 1);
                    st.giveItems(strong_liquor, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "warehouse_keeper_raut_q0214_02.htm";
                }
            }
        } else if (npcId == drunkard_treaf) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "drunkard_treaf_q0214_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2"))
                htmltext = "drunkard_treaf_q0214_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3")) {
                if (st.ownItemCount(strong_liquor) >= 1) {
                    st.setCond(26);
                    st.takeItems(strong_liquor, 1);
                    st.giveItems(treafs_ring, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "drunkard_treaf_q0214_04.htm";
                }
            }
        } else if (npcId == valkon) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "valkon_q0214_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2"))
                htmltext = "valkon_q0214_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3")) {
                st.giveItems(valkons_request, 1);
                htmltext = "valkon_q0214_04.htm";
            }
        } else if (npcId == sage_kasian) {
            if (event.equalsIgnoreCase("menu_select?ask=214&reply=1"))
                htmltext = "sage_kasian_q0214_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=214&reply=2")) {
                st.setCond(28);
                st.giveItems(kasians_list, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_kasian_q0214_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=214&reply=3")) {
                st.setCond(30);
                st.takeItems(kasians_list, -1);
                st.takeItems(ghouls_skin, -1);
                st.takeItems(medusas_blood, -1);
                st.takeItems(crimsonbinds_ichor, -1);
                st.takeItems(encht_gargoyles_nail, -1);
                st.takeItems(poitans_notes, -1);
                st.giveItems(scripture_chapter_4, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_kasian_q0214_07.htm";
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
                if (npcId == magister_mirien) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "magister_mirien_q0214_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "magister_mirien_q0214_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "magister_mirien_q0214_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == magister_mirien) {
                    if (st.ownItemCount(miriens_sigil1) == 1 && st.ownItemCount(symbol_of_sylvain) == 0)
                        htmltext = "magister_mirien_q0214_05.htm";
                    else if (st.ownItemCount(miriens_sigil1) == 1 && st.ownItemCount(symbol_of_sylvain) == 1) {
                        st.setCond(15);
                        st.takeItems(symbol_of_sylvain, -1);
                        st.takeItems(miriens_sigil1, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "magister_mirien_q0214_06.htm";
                    } else if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(symbol_of_jurek) == 0)
                        htmltext = "magister_mirien_q0214_07.htm";
                    else if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(symbol_of_jurek) == 1)
                        htmltext = "magister_mirien_q0214_08.htm";
                    else if (st.ownItemCount(miriens_instruction) == 1) {
                        if (st.getPlayer().getLevel() < 36)
                            htmltext = "magister_mirien_q0214_11.htm";
                        else {
                            st.setCond(19);
                            st.takeItems(miriens_instruction, 1);
                            st.giveItems(miriens_sigil3, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "magister_mirien_q0214_12.htm";
                        }
                    } else if (st.ownItemCount(miriens_sigil3) == 1) {
                        if (st.ownItemCount(symbol_of_cronos) == 0)
                            htmltext = "magister_mirien_q0214_13.htm";
                        else {
                            st.addExpAndSp(1753926, 113754);
                            st.giveItems(ADENA_ID, 319628);
                            st.takeItems(symbol_of_cronos, -1);
                            st.takeItems(miriens_sigil3, 1);
                            st.giveItems(mark_of_scholar, 1);
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                            st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        }
                    }
                } else if (npcId == sylvain) {
                    if (st.ownItemCount(miriens_sigil1) == 1 && st.ownItemCount(highpriests_sigil) == 0 && st.ownItemCount(symbol_of_sylvain) == 0)
                        htmltext = "sylvain_q0214_01.htm";
                    else if (st.ownItemCount(crystal_of_purity1) == 0 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(miriens_sigil1) >= 1)
                        htmltext = "sylvain_q0214_03.htm";
                    else if (st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(crystal_of_purity1) >= 1) {
                        st.setCond(14);
                        st.takeItems(highpriests_sigil, -1);
                        st.takeItems(crystal_of_purity1, -1);
                        st.giveItems(symbol_of_sylvain, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sylvain_q0214_04.htm";
                    } else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(symbol_of_sylvain) >= 1 && st.ownItemCount(highpriests_sigil) == 0)
                        htmltext = "sylvain_q0214_05.htm";
                    else if (st.ownItemCount(miriens_sigil2) >= 1 || st.ownItemCount(miriens_sigil3) >= 1)
                        htmltext = "sylvain_q0214_06.htm";
                } else if (npcId == marya) {
                    if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(sylvains_letter) >= 1)
                        htmltext = "marya_q0214_01.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(maryas_letter1) >= 1)
                        htmltext = "marya_q0214_03.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(lukas_letter) >= 1) {
                        st.setCond(5);
                        st.takeItems(lukas_letter, 1);
                        st.giveItems(maryas_letter2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "marya_q0214_04.htm";
                    } else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(maryas_letter2) >= 1)
                        htmltext = "marya_q0214_05.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_letter1) >= 1)
                        htmltext = "marya_q0214_06.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(lucillas_handbag) >= 1)
                        htmltext = "marya_q0214_09.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_painting1) >= 1) {
                        st.setCond(9);
                        st.takeItems(cretas_painting1, 1);
                        st.giveItems(cretas_painting2, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "marya_q0214_10.htm";
                    } else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_painting2) >= 1)
                        htmltext = "marya_q0214_11.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_painting3) >= 1 && st.ownItemCount(brown_scroll_scrap) < 5) {
                        st.setCond(11);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "marya_q0214_12.htm";
                    } else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_painting3) >= 1 && st.ownItemCount(brown_scroll_scrap) >= 5)
                        htmltext = "marya_q0214_13.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(crystal_of_purity1) >= 1)
                        htmltext = "marya_q0214_15.htm";
                    else if (st.ownItemCount(symbol_of_sylvain) >= 1 || st.ownItemCount(miriens_sigil2) >= 1)
                        htmltext = "marya_q0214_16.htm";
                    else if (st.ownItemCount(miriens_sigil3) == 1 && st.ownItemCount(valkons_request) == 0)
                        htmltext = "marya_q0214_17.htm";
                    else if (st.ownItemCount(miriens_sigil3) == 1 && st.ownItemCount(valkons_request) == 1) {
                        st.takeItems(valkons_request, 1);
                        st.giveItems(crystal_of_purity2, 1);
                        htmltext = "marya_q0214_18.htm";
                    }
                } else if (npcId == lucas) {
                    if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(maryas_letter1) >= 1) {
                        st.setCond(4);
                        st.takeItems(maryas_letter1, 1);
                        st.giveItems(lukas_letter, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "lucas_q0214_01.htm";
                    } else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && (st.ownItemCount(maryas_letter2) >= 1 || st.ownItemCount(cretas_letter1) >= 1 || st.ownItemCount(lucillas_handbag) >= 1 || st.ownItemCount(cretas_painting1) >= 1 || st.ownItemCount(lukas_letter) >= 1))
                        htmltext = "lucas_q0214_02.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_painting2) >= 1)
                        htmltext = "lucas_q0214_03.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_painting3) >= 1) {
                        if (st.ownItemCount(brown_scroll_scrap) < 5)
                            htmltext = "lucas_q0214_05.htm";
                        else
                            htmltext = "lucas_q0214_06.htm";
                    } else if (st.ownItemCount(symbol_of_sylvain) >= 1 || st.ownItemCount(miriens_sigil2) >= 1 || st.ownItemCount(miriens_sigil3) >= 1 || st.ownItemCount(crystal_of_purity1) >= 1)
                        htmltext = "lucas_q0214_07.htm";
                } else if (npcId == astrologer_creta) {
                    if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(maryas_letter2) >= 1)
                        htmltext = "astrologer_creta_q0214_01.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(cretas_letter1) >= 1)
                        htmltext = "astrologer_creta_q0214_06.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && st.ownItemCount(lucillas_handbag) >= 1)
                        htmltext = "astrologer_creta_q0214_07.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 && st.ownItemCount(highpriests_sigil) >= 1 && (st.ownItemCount(cretas_painting1) >= 1 || st.ownItemCount(cretas_painting2) >= 1 || st.ownItemCount(cretas_painting3) >= 1))
                        htmltext = "astrologer_creta_q0214_10.htm";
                    else if (st.ownItemCount(crystal_of_purity1) >= 1 || st.ownItemCount(symbol_of_sylvain) >= 1 || st.ownItemCount(miriens_sigil2) >= 1)
                        htmltext = "astrologer_creta_q0214_11.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(dieters_key) >= 1)
                        htmltext = "astrologer_creta_q0214_12.htm";
                    else if (st.ownItemCount(miriens_sigil3) == 1 && st.ownItemCount(dieters_key) == 0)
                        htmltext = "astrologer_creta_q0214_15.htm";
                } else if (npcId == jurek) {
                    if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(gmagisters_sigil) == 0 && st.ownItemCount(symbol_of_jurek) == 0)
                        htmltext = "jurek_q0214_01.htm";
                    else if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(jureks_list) == 1) {
                        if (st.ownItemCount(beamers_skin) + st.ownItemCount(shamans_necklace) + st.ownItemCount(shackles_scalp) < 12)
                            htmltext = "jurek_q0214_04.htm";
                        else {
                            st.setCond(18);
                            st.takeItems(jureks_list, -1);
                            st.takeItems(beamers_skin, -1);
                            st.takeItems(shamans_necklace, -1);
                            st.takeItems(shackles_scalp, -1);
                            st.takeItems(gmagisters_sigil, 1);
                            st.giveItems(symbol_of_jurek, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "jurek_q0214_05.htm";
                        }
                    } else if (st.ownItemCount(miriens_sigil2) >= 1 && st.ownItemCount(symbol_of_jurek) >= 1 && st.ownItemCount(gmagisters_sigil) == 0)
                        htmltext = "jurek_q0214_06.htm";
                    else if (st.ownItemCount(miriens_sigil1) >= 1 || st.ownItemCount(miriens_sigil3) >= 1)
                        htmltext = "jurek_q0214_07.htm";
                } else if (npcId == sage_cronos) {
                    if (st.ownItemCount(miriens_sigil3) == 1 && st.ownItemCount(cronos_sigil) == 0 && st.ownItemCount(symbol_of_cronos) == 0)
                        htmltext = "sage_cronos_q0214_01.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1) {
                        if (st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(scripture_chapter_2) >= 1 && st.ownItemCount(scripture_chapter_3) >= 1 && st.ownItemCount(scripture_chapter_4) >= 1)
                            htmltext = "sage_cronos_q0214_12.htm";
                        else
                            htmltext = "sage_cronos_q0214_11.htm";
                    } else if (st.ownItemCount(miriens_sigil3) == 1 && st.ownItemCount(symbol_of_cronos) == 1 && st.ownItemCount(cronos_sigil) == 0)
                        htmltext = "sage_cronos_q0214_15.htm";
                } else if (npcId == dieter) {
                    if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1 && st.ownItemCount(cronos_letter) >= 1)
                        htmltext = "dieter_q0214_01.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1 && st.ownItemCount(dieters_key) >= 1)
                        htmltext = "dieter_q0214_06.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1 && st.ownItemCount(cretas_letter2) >= 1)
                        htmltext = "dieter_q0214_07.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1 && st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(dieters_letter) >= 1)
                        htmltext = "dieter_q0214_10.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1 && st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(rauts_letter_envelope) >= 1)
                        htmltext = "dieter_q0214_11.htm";
                    else if (st.ownItemCount(miriens_sigil3) >= 1 && st.ownItemCount(cronos_sigil) >= 1 && st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(dieters_letter) == 0 && st.ownItemCount(rauts_letter_envelope) == 0) {
                        if (st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(scripture_chapter_2) >= 1 && st.ownItemCount(scripture_chapter_3) >= 1 && st.ownItemCount(scripture_chapter_4) >= 1)
                            htmltext = "dieter_q0214_13.htm";
                        else
                            htmltext = "dieter_q0214_12.htm";
                    } else if (st.ownItemCount(symbol_of_cronos) == 1)
                        htmltext = "dieter_q0214_15.htm";
                } else if (npcId == trader_edroc) {
                    if (st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(dieters_letter) >= 1)
                        htmltext = "trader_edroc_q0214_01.htm";
                    else if (st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(rauts_letter_envelope) >= 1)
                        htmltext = "trader_edroc_q0214_03.htm";
                    else if (st.ownItemCount(dieters_diary) == 1 && (st.ownItemCount(strong_liquor) >= 1 || st.ownItemCount(treafs_ring) >= 1))
                        htmltext = "trader_edroc_q0214_04.htm";
                } else if (npcId == warehouse_keeper_raut) {
                    if (st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(rauts_letter_envelope) >= 1)
                        htmltext = "warehouse_keeper_raut_q0214_01.htm";
                    else if (st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(strong_liquor) >= 1)
                        htmltext = "warehouse_keeper_raut_q0214_04.htm";
                    else if (st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(treafs_ring) >= 1)
                        htmltext = "warehouse_keeper_raut_q0214_05.htm";
                } else if (npcId == drunkard_treaf) {
                    if (st.ownItemCount(dieters_diary) >= 1 && st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(strong_liquor) >= 1)
                        htmltext = "drunkard_treaf_q0214_01.htm";
                    else if (st.ownItemCount(treafs_ring) >= 1 || st.ownItemCount(symbol_of_cronos) >= 1)
                        htmltext = "drunkard_treaf_q0214_05.htm";
                } else if (npcId == valkon) {
                    if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(valkons_request) == 0 && st.ownItemCount(crystal_of_purity2) == 0 && st.ownItemCount(scripture_chapter_2) == 0)
                        htmltext = "valkon_q0214_01.htm";
                    else if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(valkons_request) == 1 && st.ownItemCount(crystal_of_purity2) == 0 && st.ownItemCount(scripture_chapter_2) == 0)
                        htmltext = "valkon_q0214_05.htm";
                    else if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(valkons_request) == 0 && st.ownItemCount(crystal_of_purity2) == 1 && st.ownItemCount(scripture_chapter_2) == 0) {
                        st.takeItems(crystal_of_purity2, 1);
                        st.giveItems(scripture_chapter_2, 1);
                        htmltext = "valkon_q0214_06.htm";
                    } else if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(valkons_request) == 0 && st.ownItemCount(crystal_of_purity2) == 0 && st.ownItemCount(scripture_chapter_2) == 1)
                        htmltext = "valkon_q0214_07.htm";
                } else if (npcId == blacksmith_poitan) {
                    if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(poitans_notes) == 0 && st.ownItemCount(kasians_list) == 0 && st.ownItemCount(scripture_chapter_4) == 0) {
                        st.giveItems(poitans_notes, 1);
                        htmltext = "blacksmith_poitan_q0214_01.htm";
                    } else if (st.ownItemCount(treafs_ring) >= 1 && st.ownItemCount(poitans_notes) >= 1 && st.ownItemCount(kasians_list) == 0 && st.ownItemCount(scripture_chapter_4) == 0)
                        htmltext = "blacksmith_poitan_q0214_02.htm";
                    else if (st.ownItemCount(treafs_ring) >= 1 && st.ownItemCount(poitans_notes) >= 1 && st.ownItemCount(kasians_list) >= 1 && st.ownItemCount(scripture_chapter_4) == 0)
                        htmltext = "blacksmith_poitan_q0214_03.htm";
                    else if (st.ownItemCount(treafs_ring) >= 1 && st.ownItemCount(scripture_chapter_4) >= 1 && st.ownItemCount(poitans_notes) == 0 && st.ownItemCount(kasians_list) == 0)
                        htmltext = "blacksmith_poitan_q0214_04.htm";
                } else if (npcId == sage_kasian) {
                    if (st.ownItemCount(treafs_ring) >= 1 && st.ownItemCount(poitans_notes) >= 1 && st.ownItemCount(kasians_list) == 0) {
                        if (st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(scripture_chapter_2) >= 1 && st.ownItemCount(scripture_chapter_3) >= 1)
                            htmltext = "sage_kasian_q0214_02.htm";
                        else {
                            st.setCond(27);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "sage_kasian_q0214_01.htm";
                        }
                    } else if (st.ownItemCount(treafs_ring) >= 1 && st.ownItemCount(poitans_notes) >= 1 && st.ownItemCount(kasians_list) >= 1) {
                        if (st.ownItemCount(ghouls_skin) + st.ownItemCount(medusas_blood) + st.ownItemCount(crimsonbinds_ichor) + st.ownItemCount(encht_gargoyles_nail) < 32)
                            htmltext = "sage_kasian_q0214_05.htm";
                        else
                            htmltext = "sage_kasian_q0214_06.htm";
                    } else if (st.ownItemCount(poitans_notes) == 0 && st.ownItemCount(poitans_notes) == 0 && st.ownItemCount(kasians_list) == 0 && st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(scripture_chapter_1) >= 1 && st.ownItemCount(scripture_chapter_2) >= 1 && st.ownItemCount(scripture_chapter_3) >= 1 && st.ownItemCount(scripture_chapter_4) >= 1)
                        htmltext = "sage_kasian_q0214_08.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == beamer) {
            if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(gmagisters_sigil) == 1 && st.ownItemCount(jureks_list) == 1 && st.ownItemCount(beamers_skin) < 5) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(beamers_skin, 1);
                    if (st.ownItemCount(beamers_skin) >= 4 && st.ownItemCount(shamans_necklace) >= 5 && st.ownItemCount(shackles_scalp) >= 2) {
                        st.setCond(17);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == medusa) {
            if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(poitans_notes) == 1 && st.ownItemCount(kasians_list) == 1 && st.ownItemCount(medusas_blood) < 12) {
                st.giveItems(medusas_blood, 1);
                if (st.ownItemCount(medusas_blood) >= 12)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == ghoul) {
            if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(poitans_notes) == 1 && st.ownItemCount(kasians_list) == 1 && st.ownItemCount(ghouls_skin) < 10) {
                st.giveItems(ghouls_skin, 1);
                if (st.ownItemCount(ghouls_skin) >= 10) {
                    st.setCond(29);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == shackle || npcId == shackle_hold) {
            if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(gmagisters_sigil) == 1 && st.ownItemCount(jureks_list) == 1 && st.ownItemCount(shackles_scalp) < 2) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(shackles_scalp, 1);
                    if (st.ownItemCount(beamers_skin) >= 5 && st.ownItemCount(shamans_necklace) >= 5 && st.ownItemCount(shackles_scalp) >= 1) {
                        st.setCond(17);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == breka_orc_shaman) {
            if (st.ownItemCount(miriens_sigil2) == 1 && st.ownItemCount(gmagisters_sigil) == 1 && st.ownItemCount(jureks_list) == 1 && st.ownItemCount(shamans_necklace) < 5) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(shamans_necklace, 1);
                    if (st.ownItemCount(beamers_skin) >= 5 && st.ownItemCount(shamans_necklace) >= 4 && st.ownItemCount(shackles_scalp) >= 2) {
                        st.setCond(17);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == crimson_bind) {
            if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(poitans_notes) == 1 && st.ownItemCount(kasians_list) == 1 && st.ownItemCount(crimsonbinds_ichor) < 5) {
                st.giveItems(crimsonbinds_ichor, 1);
                if (st.ownItemCount(crimsonbinds_ichor) >= 5)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == grandis) {
            if (st.ownItemCount(miriens_sigil3) == 1 && st.ownItemCount(cronos_sigil) == 1 && st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(scripture_chapter_3) == 0) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(scripture_chapter_3, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == enchanted_gargoyle) {
            if (st.ownItemCount(treafs_ring) == 1 && st.ownItemCount(poitans_notes) == 1 && st.ownItemCount(kasians_list) == 1 && st.ownItemCount(encht_gargoyles_nail) < 5) {
                st.giveItems(encht_gargoyles_nail, 1);
                if (st.ownItemCount(encht_gargoyles_nail) >= 5)
                    st.soundEffect(SOUND_MIDDLE);
                else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == leto_lizardman_warrior) {
            if (st.ownItemCount(miriens_sigil1) == 1 && st.ownItemCount(highpriests_sigil) == 1 && st.ownItemCount(cretas_painting3) == 1 && st.ownItemCount(brown_scroll_scrap) < 5) {
                if (Rnd.get(100) < 100) {
                    st.giveItems(brown_scroll_scrap, 1);
                    if (st.ownItemCount(brown_scroll_scrap) >= 4) {
                        st.setCond(12);
                        st.soundEffect(SOUND_MIDDLE);
                    } else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}