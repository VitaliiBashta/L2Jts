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
 * @date 21/03/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _372_LegacyOfInsolence extends Quest {
    // npc
    private static final int whouse_keeper_walderal = 30844;
    private static final int trader_holly = 30839;
    private static final int magister_desmond = 30855;
    private static final int patrin = 30929;
    private static final int claudia_a = 31001;
    // mobs
    private static final int corrypt_sage = 20817;
    private static final int erin_ediunce = 20821;
    private static final int hallates_inspector = 20825;
    private static final int platinum_tribe_lord = 20829;
    private static final int plat_protect_warlord = 21069;
    private static final int messenger_angel_r = 21063;
    // etcitem
    private static final int tower_blueprint1 = 5989;
    private static final int tower_blueprint2 = 5990;
    private static final int tower_blueprint3 = 5991;
    private static final int tower_blueprint4 = 5992;
    private static final int tower_blueprint5 = 5993;
    private static final int tower_blueprint6 = 5994;
    private static final int tower_blueprint7 = 5995;
    private static final int tower_blueprint8 = 5996;
    private static final int tower_blueprint9 = 5997;
    private static final int tower_blueprint10 = 5998;
    private static final int tower_blueprint11 = 5999;
    private static final int tower_blueprint12 = 6000;
    private static final int tower_blueprint13 = 6001;
    private static final int sealed_dark_crystal_boots_lining = 5496;
    private static final int sealed_dark_crystal_gloves_design = 5508;
    private static final int sealed_dark_crystal_helmet_design = 5525;
    private static final int rp_sealed_dark_crystal_boots_i = 5368;
    private static final int rp_sealed_dark_crystal_gloves_i = 5392;
    private static final int rp_sealed_dark_crystal_helmet_i = 5426;
    private static final int sealed_tallum_boots_lining = 5497;
    private static final int sealed_tallum_gloves_design = 5509;
    private static final int sealed_tallum_bonnet_design = 5526;
    private static final int rp_sealed_tallum_boots_i = 5370;
    private static final int rp_sealed_tallum_gloves_i = 5394;
    private static final int rp_sealed_tallum_bonnet_i = 5428;
    private static final int sealed_boots_of_nightmare_lining = 5502;
    private static final int sealed_gloves_of_nightmare_design = 5514;
    private static final int sealed_helm_of_nightmare_design = 5527;
    private static final int rp_sealed_boots_of_nightmare_i = 5380;
    private static final int rp_sealed_gloves_of_nightmare_i = 5404;
    private static final int rp_sealed_helm_of_nightmare_i = 5430;
    private static final int sealed_magestic_boots_lining = 5503;
    private static final int sealed_magestic_gloves_design = 5515;
    private static final int sealed_magestic_circlet_design = 5528;
    private static final int rp_sealed_magestic_boots_i = 5382;
    private static final int rp_sealed_magestic_gloves_i = 5406;
    private static final int rp_sealed_magestic_circlet_i = 5432;
    private static final int imperial_genealogy1 = 5984;
    private static final int imperial_genealogy2 = 5985;
    private static final int imperial_genealogy3 = 5986;
    private static final int imperial_genealogy4 = 5987;
    private static final int imperial_genealogy5 = 5988;
    private static final int revelation_avarice = 5972;
    private static final int revelation_gnosis = 5973;
    private static final int revelation_strife = 5974;
    private static final int revelation_vengeance = 5975;
    private static final int revelation_awakening = 5976;
    private static final int revelation_calamity = 5977;
    private static final int revelation_descent = 5978;
    private static final int ancient_epic_part1 = 5979;
    private static final int ancient_epic_part2 = 5980;
    private static final int ancient_epic_part3 = 5981;
    private static final int ancient_epic_part4 = 5982;
    private static final int ancient_epic_part5 = 5983;
    private static final int ancient_papyrus1 = 5966;
    private static final int ancient_papyrus2 = 5967;
    private static final int ancient_papyrus3 = 5968;
    private static final int ancient_papyrus4 = 5969;

    public _372_LegacyOfInsolence() {
        super(true);
        addStartNpc(whouse_keeper_walderal);
        addTalkId(trader_holly, magister_desmond, patrin, claudia_a);
        addKillId(corrypt_sage, erin_ediunce, hallates_inspector, platinum_tribe_lord, plat_protect_warlord, messenger_angel_r);
        addLevelCheck(59, 75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == whouse_keeper_walderal) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("legacy_of_insolence", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "whouse_keeper_walderal_q0372_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=1"))
                htmltext = "whouse_keeper_walderal_q0372_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=372&reply=3")) {
                if (st.ownItemCount(tower_blueprint1) < 1 || st.ownItemCount(tower_blueprint2) < 1 || st.ownItemCount(tower_blueprint3) < 1 || st.ownItemCount(tower_blueprint4) < 1 || st.ownItemCount(tower_blueprint5) < 1 || st.ownItemCount(tower_blueprint6) < 1 || st.ownItemCount(tower_blueprint7) < 1 || st.ownItemCount(tower_blueprint8) < 1 || st.ownItemCount(tower_blueprint9) < 1 || st.ownItemCount(tower_blueprint10) < 1 || st.ownItemCount(tower_blueprint11) < 1 || st.ownItemCount(tower_blueprint12) < 1 || st.ownItemCount(tower_blueprint13) < 1)
                    htmltext = "whouse_keeper_walderal_q0372_06.htm";
                else if (st.ownItemCount(tower_blueprint1) >= 1 && st.ownItemCount(tower_blueprint2) >= 1 && st.ownItemCount(tower_blueprint3) >= 1 && st.ownItemCount(tower_blueprint4) >= 1 && st.ownItemCount(tower_blueprint5) >= 1 && st.ownItemCount(tower_blueprint6) >= 1 && st.ownItemCount(tower_blueprint7) >= 1 && st.ownItemCount(tower_blueprint8) >= 1 && st.ownItemCount(tower_blueprint9) >= 1 && st.ownItemCount(tower_blueprint10) >= 1 && st.ownItemCount(tower_blueprint11) >= 1 && st.ownItemCount(tower_blueprint12) >= 1 && st.ownItemCount(tower_blueprint13) >= 1)
                    htmltext = "whouse_keeper_walderal_q0372_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=4"))
                htmltext = "whouse_keeper_walderal_q0372_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=372&reply=5")) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "whouse_keeper_walderal_q0372_09.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=6"))
                htmltext = "whouse_keeper_walderal_q0372_11.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=372&reply=7")) {
                if (st.ownItemCount(tower_blueprint1) < 1 || st.ownItemCount(tower_blueprint2) < 1 || st.ownItemCount(tower_blueprint3) < 1 || st.ownItemCount(tower_blueprint4) < 1 || st.ownItemCount(tower_blueprint5) < 1 || st.ownItemCount(tower_blueprint6) < 1 || st.ownItemCount(tower_blueprint7) < 1 || st.ownItemCount(tower_blueprint8) < 1 || st.ownItemCount(tower_blueprint9) < 1 || st.ownItemCount(tower_blueprint10) < 1 || st.ownItemCount(tower_blueprint11) < 1 || st.ownItemCount(tower_blueprint12) < 1 || st.ownItemCount(tower_blueprint13) < 1)
                    htmltext = "whouse_keeper_walderal_q0372_07e.htm";
                else if (st.ownItemCount(tower_blueprint1) >= 1 && st.ownItemCount(tower_blueprint2) >= 1 && st.ownItemCount(tower_blueprint3) >= 1 && st.ownItemCount(tower_blueprint4) >= 1 && st.ownItemCount(tower_blueprint5) >= 1 && st.ownItemCount(tower_blueprint6) >= 1 && st.ownItemCount(tower_blueprint7) >= 1 && st.ownItemCount(tower_blueprint8) >= 1 && st.ownItemCount(tower_blueprint9) >= 1 && st.ownItemCount(tower_blueprint10) >= 1 && st.ownItemCount(tower_blueprint11) >= 1 && st.ownItemCount(tower_blueprint12) >= 1 && st.ownItemCount(tower_blueprint13) >= 1) {
                    st.takeItems(tower_blueprint1, 1);
                    st.takeItems(tower_blueprint2, 1);
                    st.takeItems(tower_blueprint3, 1);
                    st.takeItems(tower_blueprint4, 1);
                    st.takeItems(tower_blueprint5, 1);
                    st.takeItems(tower_blueprint6, 1);
                    st.takeItems(tower_blueprint7, 1);
                    st.takeItems(tower_blueprint8, 1);
                    st.takeItems(tower_blueprint9, 1);
                    st.takeItems(tower_blueprint10, 1);
                    st.takeItems(tower_blueprint11, 1);
                    st.takeItems(tower_blueprint12, 1);
                    st.takeItems(tower_blueprint13, 1);
                    int i1 = Rnd.get(100);
                    if (i1 < 10)
                        st.giveItems(sealed_dark_crystal_boots_lining, 1);
                    else if (i1 < 20)
                        st.giveItems(sealed_dark_crystal_gloves_design, 1);
                    else if (i1 < 30)
                        st.giveItems(sealed_dark_crystal_helmet_design, 1);
                    else if (i1 < 40) {
                        st.giveItems(sealed_dark_crystal_boots_lining, 1);
                        st.giveItems(sealed_dark_crystal_gloves_design, 1);
                        st.giveItems(sealed_dark_crystal_helmet_design, 1);
                    } else if (i1 < 51)
                        st.giveItems(rp_sealed_dark_crystal_boots_i, 1);
                    else if (i1 < 62)
                        st.giveItems(rp_sealed_dark_crystal_gloves_i, 1);
                    else if (i1 < 79)
                        st.giveItems(rp_sealed_dark_crystal_helmet_i, 1);
                    else if (i1 < 100) {
                        st.giveItems(rp_sealed_dark_crystal_boots_i, 1);
                        st.giveItems(rp_sealed_dark_crystal_gloves_i, 1);
                        st.giveItems(rp_sealed_dark_crystal_helmet_i, 1);
                    }
                    htmltext = "whouse_keeper_walderal_q0372_07a.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=8")) {
                if (st.ownItemCount(tower_blueprint1) < 1 || st.ownItemCount(tower_blueprint2) < 1 || st.ownItemCount(tower_blueprint3) < 1 || st.ownItemCount(tower_blueprint4) < 1 || st.ownItemCount(tower_blueprint5) < 1 || st.ownItemCount(tower_blueprint6) < 1 || st.ownItemCount(tower_blueprint7) < 1 || st.ownItemCount(tower_blueprint8) < 1 || st.ownItemCount(tower_blueprint9) < 1 || st.ownItemCount(tower_blueprint10) < 1 || st.ownItemCount(tower_blueprint11) < 1 || st.ownItemCount(tower_blueprint12) < 1 || st.ownItemCount(tower_blueprint13) < 1)
                    htmltext = "whouse_keeper_walderal_q0372_07e.htm";
                else if (st.ownItemCount(tower_blueprint1) >= 1 && st.ownItemCount(tower_blueprint2) >= 1 && st.ownItemCount(tower_blueprint3) >= 1 && st.ownItemCount(tower_blueprint4) >= 1 && st.ownItemCount(tower_blueprint5) >= 1 && st.ownItemCount(tower_blueprint6) >= 1 && st.ownItemCount(tower_blueprint7) >= 1 && st.ownItemCount(tower_blueprint8) >= 1 && st.ownItemCount(tower_blueprint9) >= 1 && st.ownItemCount(tower_blueprint10) >= 1 && st.ownItemCount(tower_blueprint11) >= 1 && st.ownItemCount(tower_blueprint12) >= 1 && st.ownItemCount(tower_blueprint13) >= 1) {
                    st.takeItems(tower_blueprint1, 1);
                    st.takeItems(tower_blueprint2, 1);
                    st.takeItems(tower_blueprint3, 1);
                    st.takeItems(tower_blueprint4, 1);
                    st.takeItems(tower_blueprint5, 1);
                    st.takeItems(tower_blueprint6, 1);
                    st.takeItems(tower_blueprint7, 1);
                    st.takeItems(tower_blueprint8, 1);
                    st.takeItems(tower_blueprint9, 1);
                    st.takeItems(tower_blueprint10, 1);
                    st.takeItems(tower_blueprint11, 1);
                    st.takeItems(tower_blueprint12, 1);
                    st.takeItems(tower_blueprint13, 1);
                    int i1 = Rnd.get(100);
                    if (i1 < 10)
                        st.giveItems(sealed_tallum_boots_lining, 1);
                    else if (i1 < 20)
                        st.giveItems(sealed_tallum_gloves_design, 1);
                    else if (i1 < 30)
                        st.giveItems(sealed_tallum_bonnet_design, 1);
                    else if (i1 < 40) {
                        st.giveItems(sealed_tallum_boots_lining, 1);
                        st.giveItems(sealed_tallum_gloves_design, 1);
                        st.giveItems(sealed_tallum_bonnet_design, 1);
                    } else if (i1 < 51)
                        st.giveItems(rp_sealed_tallum_boots_i, 1);
                    else if (i1 < 62)
                        st.giveItems(rp_sealed_tallum_gloves_i, 1);
                    else if (i1 < 79)
                        st.giveItems(rp_sealed_tallum_bonnet_i, 1);
                    else if (i1 < 100) {
                        st.giveItems(rp_sealed_tallum_boots_i, 1);
                        st.giveItems(rp_sealed_tallum_gloves_i, 1);
                        st.giveItems(rp_sealed_tallum_bonnet_i, 1);
                    }
                    htmltext = "whouse_keeper_walderal_q0372_07b.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=9")) {
                if (st.ownItemCount(tower_blueprint1) < 1 || st.ownItemCount(tower_blueprint2) < 1 || st.ownItemCount(tower_blueprint3) < 1 || st.ownItemCount(tower_blueprint4) < 1 || st.ownItemCount(tower_blueprint5) < 1 || st.ownItemCount(tower_blueprint6) < 1 || st.ownItemCount(tower_blueprint7) < 1 || st.ownItemCount(tower_blueprint8) < 1 || st.ownItemCount(tower_blueprint9) < 1 || st.ownItemCount(tower_blueprint10) < 1 || st.ownItemCount(tower_blueprint11) < 1 || st.ownItemCount(tower_blueprint12) < 1 || st.ownItemCount(tower_blueprint13) < 1)
                    htmltext = "whouse_keeper_walderal_q0372_07e.htm";
                else if (st.ownItemCount(tower_blueprint1) >= 1 && st.ownItemCount(tower_blueprint2) >= 1 && st.ownItemCount(tower_blueprint3) >= 1 && st.ownItemCount(tower_blueprint4) >= 1 && st.ownItemCount(tower_blueprint5) >= 1 && st.ownItemCount(tower_blueprint6) >= 1 && st.ownItemCount(tower_blueprint7) >= 1 && st.ownItemCount(tower_blueprint8) >= 1 && st.ownItemCount(tower_blueprint9) >= 1 && st.ownItemCount(tower_blueprint10) >= 1 && st.ownItemCount(tower_blueprint11) >= 1 && st.ownItemCount(tower_blueprint12) >= 1 && st.ownItemCount(tower_blueprint13) >= 1) {
                    st.takeItems(tower_blueprint1, 1);
                    st.takeItems(tower_blueprint2, 1);
                    st.takeItems(tower_blueprint3, 1);
                    st.takeItems(tower_blueprint4, 1);
                    st.takeItems(tower_blueprint5, 1);
                    st.takeItems(tower_blueprint6, 1);
                    st.takeItems(tower_blueprint7, 1);
                    st.takeItems(tower_blueprint8, 1);
                    st.takeItems(tower_blueprint9, 1);
                    st.takeItems(tower_blueprint10, 1);
                    st.takeItems(tower_blueprint11, 1);
                    st.takeItems(tower_blueprint12, 1);
                    st.takeItems(tower_blueprint13, 1);
                    int i1 = Rnd.get(100);
                    if (i1 < 17)
                        st.giveItems(sealed_boots_of_nightmare_lining, 1);
                    else if (i1 < 34)
                        st.giveItems(sealed_gloves_of_nightmare_design, 1);
                    else if (i1 < 49)
                        st.giveItems(sealed_helm_of_nightmare_design, 1);
                    else if (i1 < 58) {
                        st.giveItems(sealed_boots_of_nightmare_lining, 1);
                        st.giveItems(sealed_gloves_of_nightmare_design, 1);
                        st.giveItems(sealed_helm_of_nightmare_design, 1);
                    } else if (i1 < 70)
                        st.giveItems(rp_sealed_boots_of_nightmare_i, 1);
                    else if (i1 < 82)
                        st.giveItems(rp_sealed_gloves_of_nightmare_i, 1);
                    else if (i1 < 92)
                        st.giveItems(rp_sealed_helm_of_nightmare_i, 1);
                    else if (i1 < 100) {
                        st.giveItems(rp_sealed_boots_of_nightmare_i, 1);
                        st.giveItems(rp_sealed_gloves_of_nightmare_i, 1);
                        st.giveItems(rp_sealed_helm_of_nightmare_i, 1);
                    }
                    htmltext = "whouse_keeper_walderal_q0372_07c.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=10")) {
                if (st.ownItemCount(tower_blueprint1) < 1 || st.ownItemCount(tower_blueprint2) < 1 || st.ownItemCount(tower_blueprint3) < 1 || st.ownItemCount(tower_blueprint4) < 1 || st.ownItemCount(tower_blueprint5) < 1 || st.ownItemCount(tower_blueprint6) < 1 || st.ownItemCount(tower_blueprint7) < 1 || st.ownItemCount(tower_blueprint8) < 1 || st.ownItemCount(tower_blueprint9) < 1 || st.ownItemCount(tower_blueprint10) < 1 || st.ownItemCount(tower_blueprint11) < 1 || st.ownItemCount(tower_blueprint12) < 1 || st.ownItemCount(tower_blueprint13) < 1)
                    htmltext = "whouse_keeper_walderal_q0372_07e.htm";
                else if (st.ownItemCount(tower_blueprint1) >= 1 && st.ownItemCount(tower_blueprint2) >= 1 && st.ownItemCount(tower_blueprint3) >= 1 && st.ownItemCount(tower_blueprint4) >= 1 && st.ownItemCount(tower_blueprint5) >= 1 && st.ownItemCount(tower_blueprint6) >= 1 && st.ownItemCount(tower_blueprint7) >= 1 && st.ownItemCount(tower_blueprint8) >= 1 && st.ownItemCount(tower_blueprint9) >= 1 && st.ownItemCount(tower_blueprint10) >= 1 && st.ownItemCount(tower_blueprint11) >= 1 && st.ownItemCount(tower_blueprint12) >= 1 && st.ownItemCount(tower_blueprint13) >= 1) {
                    st.takeItems(tower_blueprint1, 1);
                    st.takeItems(tower_blueprint2, 1);
                    st.takeItems(tower_blueprint3, 1);
                    st.takeItems(tower_blueprint4, 1);
                    st.takeItems(tower_blueprint5, 1);
                    st.takeItems(tower_blueprint6, 1);
                    st.takeItems(tower_blueprint7, 1);
                    st.takeItems(tower_blueprint8, 1);
                    st.takeItems(tower_blueprint9, 1);
                    st.takeItems(tower_blueprint10, 1);
                    st.takeItems(tower_blueprint11, 1);
                    st.takeItems(tower_blueprint12, 1);
                    st.takeItems(tower_blueprint13, 1);
                    int i1 = Rnd.get(100);
                    if (i1 < 17)
                        st.giveItems(sealed_magestic_boots_lining, 1);
                    else if (i1 < 34)
                        st.giveItems(sealed_magestic_gloves_design, 1);
                    else if (i1 < 49)
                        st.giveItems(sealed_magestic_circlet_design, 1);
                    else if (i1 < 58) {
                        st.giveItems(sealed_magestic_boots_lining, 1);
                        st.giveItems(sealed_magestic_gloves_design, 1);
                        st.giveItems(sealed_magestic_circlet_design, 1);
                    } else if (i1 < 70)
                        st.giveItems(rp_sealed_magestic_boots_i, 1);
                    else if (i1 < 82)
                        st.giveItems(rp_sealed_magestic_gloves_i, 1);
                    else if (i1 < 92)
                        st.giveItems(rp_sealed_magestic_circlet_i, 1);
                    else if (i1 < 100) {
                        st.giveItems(rp_sealed_magestic_boots_i, 1);
                        st.giveItems(rp_sealed_magestic_gloves_i, 1);
                        st.giveItems(rp_sealed_magestic_circlet_i, 1);
                    }
                    htmltext = "whouse_keeper_walderal_q0372_07d.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=372&reply=99")) {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "whouse_keeper_walderal_q0372_05b.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("legacy_of_insolence");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == whouse_keeper_walderal) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "whouse_keeper_walderal_q0372_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "whouse_keeper_walderal_q0372_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == whouse_keeper_walderal) {
                    if (GetMemoState == 1)
                        htmltext = "whouse_keeper_walderal_q0372_05.htm";
                } else if (npcId == trader_holly) {
                    if (st.ownItemCount(imperial_genealogy1) < 1 || st.ownItemCount(imperial_genealogy2) < 1 || st.ownItemCount(imperial_genealogy3) < 1 || st.ownItemCount(imperial_genealogy4) < 1 || st.ownItemCount(imperial_genealogy5) < 1)
                        htmltext = "trader_holly_q0372_01.htm";
                    else if (st.ownItemCount(imperial_genealogy1) >= 1 && st.ownItemCount(imperial_genealogy2) >= 1 && st.ownItemCount(imperial_genealogy3) >= 1 && st.ownItemCount(imperial_genealogy4) >= 1 && st.ownItemCount(imperial_genealogy5) >= 1) {
                        st.takeItems(imperial_genealogy1, 1);
                        st.takeItems(imperial_genealogy2, 1);
                        st.takeItems(imperial_genealogy3, 1);
                        st.takeItems(imperial_genealogy4, 1);
                        st.takeItems(imperial_genealogy5, 1);
                        int i1 = Rnd.get(100);
                        if (i1 < 30)
                            st.giveItems(sealed_dark_crystal_boots_lining, 1);
                        else if (i1 < 60)
                            st.giveItems(sealed_dark_crystal_gloves_design, 1);
                        else if (i1 < 80)
                            st.giveItems(sealed_dark_crystal_helmet_design, 1);
                        else if (i1 < 90) {
                            st.giveItems(sealed_dark_crystal_boots_lining, 1);
                            st.giveItems(sealed_dark_crystal_gloves_design, 1);
                            st.giveItems(sealed_dark_crystal_helmet_design, 1);
                        } else if (i1 < 100)
                            st.giveItems(ADENA_ID, 4000);
                        htmltext = "trader_holly_q0372_02.htm";
                    }
                } else if (npcId == magister_desmond) {
                    if (st.ownItemCount(revelation_avarice) < 1 || st.ownItemCount(revelation_gnosis) < 1 || st.ownItemCount(revelation_strife) < 1 || st.ownItemCount(revelation_vengeance) < 1 || st.ownItemCount(revelation_awakening) < 1 || st.ownItemCount(revelation_calamity) < 1 || st.ownItemCount(revelation_descent) < 1)
                        htmltext = "magister_desmond_q0372_01.htm";
                    else if (st.ownItemCount(revelation_avarice) >= 1 && st.ownItemCount(revelation_gnosis) >= 1 && st.ownItemCount(revelation_strife) >= 1 && st.ownItemCount(revelation_vengeance) >= 1 && st.ownItemCount(revelation_awakening) >= 1 && st.ownItemCount(revelation_calamity) >= 1 && st.ownItemCount(revelation_descent) >= 1) {
                        st.takeItems(revelation_avarice, 1);
                        st.takeItems(revelation_gnosis, 1);
                        st.takeItems(revelation_strife, 1);
                        st.takeItems(revelation_vengeance, 1);
                        st.takeItems(revelation_awakening, 1);
                        st.takeItems(revelation_calamity, 1);
                        st.takeItems(revelation_descent, 1);
                        int i1 = Rnd.get(100);
                        if (i1 < 31)
                            st.giveItems(sealed_magestic_boots_lining, 1);
                        else if (i1 < 62)
                            st.giveItems(sealed_magestic_gloves_design, 1);
                        else if (i1 < 75)
                            st.giveItems(sealed_magestic_circlet_design, 1);
                        else if (i1 < 83) {
                            st.giveItems(sealed_magestic_boots_lining, 1);
                            st.giveItems(sealed_magestic_gloves_design, 1);
                            st.giveItems(sealed_magestic_circlet_design, 1);
                        } else if (i1 < 100)
                            st.giveItems(ADENA_ID, 4000);
                        htmltext = "magister_desmond_q0372_02.htm";
                    }
                } else if (npcId == patrin) {
                    if (st.ownItemCount(ancient_epic_part1) < 1 || st.ownItemCount(ancient_epic_part2) < 1 || st.ownItemCount(ancient_epic_part3) < 1 || st.ownItemCount(ancient_epic_part4) < 1 || st.ownItemCount(ancient_epic_part5) < 1)
                        htmltext = "patrin_q0372_01.htm";
                    else if (st.ownItemCount(ancient_epic_part1) >= 1 && st.ownItemCount(ancient_epic_part2) >= 1 && st.ownItemCount(ancient_epic_part3) >= 1 && st.ownItemCount(ancient_epic_part4) >= 1 && st.ownItemCount(ancient_epic_part5) >= 1) {
                        st.takeItems(ancient_epic_part1, 1);
                        st.takeItems(ancient_epic_part2, 1);
                        st.takeItems(ancient_epic_part3, 1);
                        st.takeItems(ancient_epic_part4, 1);
                        st.takeItems(ancient_epic_part5, 1);
                        int i1 = Rnd.get(100);
                        if (i1 < 30)
                            st.giveItems(sealed_tallum_boots_lining, 1);
                        else if (i1 < 60)
                            st.giveItems(sealed_tallum_gloves_design, 1);
                        else if (i1 < 80)
                            st.giveItems(sealed_tallum_bonnet_design, 1);
                        else if (i1 < 90) {
                            st.giveItems(sealed_tallum_boots_lining, 1);
                            st.giveItems(sealed_tallum_gloves_design, 1);
                            st.giveItems(sealed_tallum_bonnet_design, 1);
                        } else if (i1 < 100)
                            st.giveItems(ADENA_ID, 4000);
                        htmltext = "patrin_q0372_02.htm";
                    }
                } else if (npcId == claudia_a) {
                    if (st.ownItemCount(revelation_avarice) < 1 || st.ownItemCount(revelation_gnosis) < 1 || st.ownItemCount(revelation_strife) < 1 || st.ownItemCount(revelation_vengeance) < 1 || st.ownItemCount(revelation_awakening) < 1 || st.ownItemCount(revelation_calamity) < 1 || st.ownItemCount(revelation_descent) < 1)
                        htmltext = "claudia_a_q0372_01.htm";
                    else if (st.ownItemCount(revelation_avarice) >= 1 && st.ownItemCount(revelation_gnosis) >= 1 && st.ownItemCount(revelation_strife) >= 1 && st.ownItemCount(revelation_vengeance) >= 1 && st.ownItemCount(revelation_awakening) >= 1 && st.ownItemCount(revelation_calamity) >= 1 && st.ownItemCount(revelation_descent) >= 1) {
                        st.takeItems(revelation_avarice, 1);
                        st.takeItems(revelation_gnosis, 1);
                        st.takeItems(revelation_strife, 1);
                        st.takeItems(revelation_vengeance, 1);
                        st.takeItems(revelation_awakening, 1);
                        st.takeItems(revelation_calamity, 1);
                        st.takeItems(revelation_descent, 1);
                        int i1 = Rnd.get(100);
                        if (i1 < 31)
                            st.giveItems(sealed_boots_of_nightmare_lining, 1);
                        else if (i1 < 62)
                            st.giveItems(sealed_gloves_of_nightmare_design, 1);
                        else if (i1 < 75)
                            st.giveItems(sealed_helm_of_nightmare_design, 1);
                        else if (i1 < 83) {
                            st.giveItems(sealed_boots_of_nightmare_lining, 1);
                            st.giveItems(sealed_gloves_of_nightmare_design, 1);
                            st.giveItems(sealed_helm_of_nightmare_design, 1);
                        } else if (i1 < 100)
                            st.giveItems(ADENA_ID, 4000);
                        htmltext = "claudia_a_q0372_02.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == corrypt_sage) {
            if (Rnd.get(1000) < 302) {
                st.giveItems(ancient_papyrus1, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == erin_ediunce) {
            if (Rnd.get(100) < 41) {
                st.giveItems(ancient_papyrus1, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == hallates_inspector) {
            if (Rnd.get(1000) < 447) {
                st.giveItems(ancient_papyrus1, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == platinum_tribe_lord) {
            if (Rnd.get(1000) < 451) {
                st.giveItems(ancient_papyrus2, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == plat_protect_warlord) {
            if (Rnd.get(100) < 28) {
                st.giveItems(ancient_papyrus3, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == messenger_angel_r) {
            if (Rnd.get(100) < 29) {
                st.giveItems(ancient_papyrus4, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}