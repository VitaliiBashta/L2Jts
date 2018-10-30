package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 19/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _420_LittleWings extends Quest {
    // NPCs
    private final static int pet_manager_cooper = 30829;
    private final static int sage_cronos = 30610;
    private final static int marya = 30608;
    private final static int guard_byron = 30711;
    private final static int fairy_mymyu = 30747;
    private final static int drake_exarion = 30748;
    private final static int drake_zwov = 30749;
    private final static int drake_kalibran = 30750;
    private final static int wyrm_suzet = 30751;
    private final static int wyrm_shamhai = 30752;
    // Mobs
    private final static int dead_seeker = 20202;
    private final static int inpicio = 20231;
    private final static int marsh_spider = 20233;
    private final static int breka_orc_overlord = 20270;
    private final static int road_scavenger = 20551;
    private final static int leto_lizardman_warrior = 20580;
    private final static int fline = 20589;
    private final static int liele = 20590;
    private final static int ti_mi_kran = 20591;
    private final static int pan_ruem = 20592;
    private final static int unicorn = 20593;
    private final static int forest_runner = 20594;
    private final static int fline_elder = 20595;
    private final static int liele_elder = 20596;
    private final static int ti_mi_kran_elder = 20597;
    private final static int pan_ruem_elder = 20598;
    private final static int unicorn_elder = 20599;
    // etcitem
    private final static int coal = 1870;
    private final static int charcoal = 1871;
    private final static int silver_nugget = 1873;
    private final static int stone_of_purity = 1875;
    private final static int gemstone_d = 2130;
    private final static int gemstone_c = 2131;
    private final static int dragonflute_of_wind = 3500;
    private final static int dragonflute_of_star = 3501;
    private final static int dragonflute_of_twilight = 3502;
    private final static int hatchlings_soft_leather = 3912;
    private final static int food_for_hatchling = 4038;
    // questitem
    private final static int q_fairy_dust = 3499;
    private final static int q_fairy_stone = 3816;
    private final static int q_fairy_stone_delux = 3817;
    private final static int q_list_of_stuff_for_fs = 3818;
    private final static int q_list_of_stuff_for_fsd = 3819;
    private final static int q_inpicios_back_skin = 3820;
    private final static int q_juice_of_monkshood = 3821;
    private final static int q_scale_of_drake_exarion = 3822;
    private final static int q_egg_of_drake_exarion = 3823;
    private final static int q_scale_of_drake_zwov = 3824;
    private final static int q_egg_of_drake_zwov = 3825;
    private final static int q_scale_of_drake_kalibran = 3826;
    private final static int q_egg_of_drake_kalibran = 3827;
    private final static int q_scale_of_wyrm_suzet = 3828;
    private final static int q_egg_of_wyrm_suzet = 3829;
    private final static int q_scale_of_wyrm_shamhai = 3830;
    private final static int q_egg_of_wyrm_shamhai = 3831;
    private static int count_fairy_mymyu = 0;

    public _420_LittleWings() {
        super(false);
        addStartNpc(pet_manager_cooper);
        addTalkId(sage_cronos, marya, guard_byron, fairy_mymyu, drake_exarion, drake_zwov, drake_kalibran, wyrm_suzet, wyrm_shamhai);
        addKillId(dead_seeker, inpicio, marsh_spider, breka_orc_overlord, road_scavenger, leto_lizardman_warrior, fline, liele, ti_mi_kran, pan_ruem, unicorn, forest_runner, fline_elder, liele_elder, ti_mi_kran_elder, pan_ruem_elder, unicorn_elder);
        addQuestItem(q_fairy_dust, q_fairy_stone, q_fairy_stone_delux, q_list_of_stuff_for_fs, q_list_of_stuff_for_fsd, q_inpicios_back_skin, q_juice_of_monkshood, q_scale_of_drake_exarion, q_scale_of_drake_zwov, q_scale_of_drake_kalibran, q_scale_of_wyrm_suzet, q_scale_of_wyrm_shamhai, q_egg_of_drake_exarion, q_egg_of_drake_zwov, q_egg_of_drake_kalibran, q_egg_of_wyrm_suzet, q_egg_of_wyrm_shamhai);
        addLevelCheck(35);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == pet_manager_cooper) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("little_wings", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pet_manager_cooper_q0420_02.htm";
            }
        } else if (npcId == sage_cronos) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "sage_cronos_q0420_02.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "sage_cronos_q0420_03.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "sage_cronos_q0420_04.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                st.setCond(2);
                st.setMemoState("little_wings", String.valueOf(2), true);
                st.giveItems(q_list_of_stuff_for_fs, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_cronos_q0420_05.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                st.setCond(2);
                st.setMemoState("little_wings", String.valueOf(2), true);
                st.giveItems(q_list_of_stuff_for_fsd, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_cronos_q0420_06.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                st.setCond(2);
                st.setMemoState("little_wings", String.valueOf(11), true);
                st.giveItems(q_list_of_stuff_for_fs, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_cronos_q0420_12.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                st.setCond(2);
                st.setMemoState("little_wings", String.valueOf(11), true);
                st.giveItems(q_list_of_stuff_for_fsd, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sage_cronos_q0420_13.htm";
            }
        } else if (npcId == marya) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.takeItems(q_list_of_stuff_for_fs, -1);
                st.takeItems(coal, 10);
                st.takeItems(charcoal, 10);
                st.takeItems(gemstone_d, 1);
                st.takeItems(silver_nugget, 3);
                st.takeItems(q_inpicios_back_skin, -1);
                st.giveItems(q_fairy_stone, 1);
                htmltext = "marya_q0420_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                st.takeItems(q_list_of_stuff_for_fsd, -1);
                st.takeItems(coal, 10);
                st.takeItems(charcoal, 10);
                st.takeItems(gemstone_c, 1);
                st.takeItems(stone_of_purity, 1);
                st.takeItems(silver_nugget, 5);
                st.takeItems(q_inpicios_back_skin, -1);
                st.giveItems(q_fairy_stone_delux, 1);
                htmltext = "marya_q0420_05.htm";
            }
        } else if (npcId == guard_byron) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "guard_byron_q0420_02.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                if (st.ownItemCount(q_fairy_stone) == 1) {
                    st.setCond(4);
                    st.setMemoState("little_wings", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "guard_byron_q0420_03.htm";
                } else {
                    st.setCond(4);
                    st.setMemoState("little_wings", String.valueOf(4), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "guard_byron_q0420_04.htm";
                }
            }
        } else if (npcId == fairy_mymyu) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.takeItems(q_fairy_stone, -1);
                st.setMemoState("little_wings", String.valueOf(5), true);
                htmltext = "fairy_mymyu_q0420_03.htm";
            } else if (event.equalsIgnoreCase("reply_3") && st.ownItemCount(q_fairy_stone_delux) > 0) {
                st.giveItems(q_fairy_dust, 1);
                st.takeItems(q_fairy_stone_delux, -1);
                st.setMemoState("little_wings", String.valueOf(5), true);
                htmltext = "fairy_mymyu_q0420_05.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "fairy_mymyu_q0420_06.htm";
            else if (event.equalsIgnoreCase("reply_4")) {
                st.setCond(5);
                st.setMemoState("little_wings", String.valueOf(6), true);
                st.giveItems(q_juice_of_monkshood, 1);
                htmltext = "fairy_mymyu_q0420_08.htm";
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (st.ownItemCount(q_fairy_dust) == 1)
                    htmltext = "fairy_mymyu_q0420_13.htm";
                int i0 = Rnd.get(100);
                if (st.ownItemCount(q_egg_of_drake_exarion) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 45)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 75)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 50)
                        st.giveItems(dragonflute_of_wind, 1);
                    else if (i0 < 85)
                        st.giveItems(dragonflute_of_star, 1);
                    else
                        st.giveItems(dragonflute_of_twilight, 1);
                    st.takeItems(q_egg_of_drake_exarion, -1);
                } else if (st.ownItemCount(q_egg_of_wyrm_suzet) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 55)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 85)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 65)
                        st.giveItems(dragonflute_of_wind, 1);
                    else if (i0 < 95)
                        st.giveItems(dragonflute_of_star, 1);
                    else
                        st.giveItems(dragonflute_of_twilight, 1);
                    st.takeItems(q_egg_of_wyrm_suzet, -1);
                } else if (st.ownItemCount(q_egg_of_drake_kalibran) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 60)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 90)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 70)
                        st.giveItems(dragonflute_of_wind, 1);
                    else
                        st.giveItems(dragonflute_of_star, 1);
                    st.takeItems(q_egg_of_drake_kalibran, -1);
                } else if (st.ownItemCount(q_egg_of_wyrm_shamhai) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 70)
                            st.giveItems(dragonflute_of_wind, 1);
                        else
                            st.giveItems(dragonflute_of_star, 1);
                    } else if (i0 < 85)
                        st.giveItems(dragonflute_of_wind, 1);
                    else
                        st.giveItems(dragonflute_of_star, 1);
                    st.takeItems(q_egg_of_wyrm_shamhai, -1);
                } else if (st.ownItemCount(q_egg_of_drake_zwov) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 90)
                            st.giveItems(dragonflute_of_wind, 1);
                        else
                            st.giveItems(dragonflute_of_star, 1);
                    } else
                        st.giveItems(dragonflute_of_wind, 1);
                    st.takeItems(q_egg_of_drake_zwov, -1);
                }
                st.removeMemo("little_wings");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "fairy_mymyu_q0420_16.htm";
            } else if (event.equalsIgnoreCase("reply_7")) {
                int i0 = Rnd.get(100);
                if (st.ownItemCount(q_egg_of_drake_exarion) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 45)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 75)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 50)
                        st.giveItems(dragonflute_of_wind, 1);
                    else if (i0 < 85)
                        st.giveItems(dragonflute_of_star, 1);
                    else
                        st.giveItems(dragonflute_of_twilight, 1);
                    st.takeItems(q_egg_of_drake_exarion, -1);
                } else if (st.ownItemCount(q_egg_of_wyrm_suzet) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 55)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 85)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 65)
                        st.giveItems(dragonflute_of_wind, 1);
                    else if (i0 < 95)
                        st.giveItems(dragonflute_of_star, 1);
                    else
                        st.giveItems(dragonflute_of_twilight, 1);
                    st.takeItems(q_egg_of_wyrm_suzet, -1);
                } else if (st.ownItemCount(q_egg_of_drake_kalibran) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 60)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 90)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 70)
                        st.giveItems(dragonflute_of_wind, 1);
                    else
                        st.giveItems(dragonflute_of_star, 1);
                    st.takeItems(q_egg_of_drake_kalibran, -1);
                } else if (st.ownItemCount(q_egg_of_wyrm_shamhai) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 70)
                            st.giveItems(dragonflute_of_wind, 1);
                        else
                            st.giveItems(dragonflute_of_star, 1);
                    } else if (i0 < 85)
                        st.giveItems(dragonflute_of_wind, 1);
                    else
                        st.giveItems(dragonflute_of_star, 1);
                    st.takeItems(q_egg_of_wyrm_shamhai, -1);
                } else if (st.ownItemCount(q_egg_of_drake_zwov) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 90)
                            st.giveItems(dragonflute_of_wind, 1);
                        else
                            st.giveItems(dragonflute_of_star, 1);
                    } else
                        st.giveItems(dragonflute_of_wind, 1);
                    st.takeItems(q_egg_of_drake_zwov, -1);
                }
                st.removeMemo("little_wings");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "fairy_mymyu_q0420_14.htm";
            } else if (event.equalsIgnoreCase("reply_6")) {
                int i0 = Rnd.get(100);
                if (st.ownItemCount(q_egg_of_drake_exarion) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 45)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 75)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 50)
                        st.giveItems(dragonflute_of_wind, 1);
                    else if (i0 < 85)
                        st.giveItems(dragonflute_of_star, 1);
                    else
                        st.giveItems(dragonflute_of_twilight, 1);
                    st.takeItems(q_egg_of_drake_exarion, -1);
                } else if (st.ownItemCount(q_egg_of_wyrm_suzet) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 55)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 85)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 65)
                        st.giveItems(dragonflute_of_wind, 1);
                    else if (i0 < 95)
                        st.giveItems(dragonflute_of_star, 1);
                    else
                        st.giveItems(dragonflute_of_twilight, 1);
                    st.takeItems(q_egg_of_wyrm_suzet, -1);
                } else if (st.ownItemCount(q_egg_of_drake_kalibran) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 60)
                            st.giveItems(dragonflute_of_wind, 1);
                        else if (i0 < 90)
                            st.giveItems(dragonflute_of_star, 1);
                        else
                            st.giveItems(dragonflute_of_twilight, 1);
                    } else if (i0 < 70)
                        st.giveItems(dragonflute_of_wind, 1);
                    else
                        st.giveItems(dragonflute_of_star, 1);
                    st.takeItems(q_egg_of_drake_kalibran, -1);
                } else if (st.ownItemCount(q_egg_of_wyrm_shamhai) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 70)
                            st.giveItems(dragonflute_of_wind, 1);
                        else
                            st.giveItems(dragonflute_of_star, 1);
                    } else if (i0 < 85)
                        st.giveItems(dragonflute_of_wind, 1);
                    else
                        st.giveItems(dragonflute_of_star, 1);
                    st.takeItems(q_egg_of_wyrm_shamhai, -1);
                } else if (st.ownItemCount(q_egg_of_drake_zwov) >= 1) {
                    if (st.ownItemCount(q_fairy_dust) == 1) {
                        if (i0 < 90)
                            st.giveItems(dragonflute_of_wind, 1);
                        else
                            st.giveItems(dragonflute_of_star, 1);
                    } else {
                        st.giveItems(dragonflute_of_wind, 1);
                    }
                    st.takeItems(q_egg_of_drake_zwov, -1);
                }
                st.removeMemo("little_wings");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "fairy_mymyu_q0420_14.htm";
                if (st.ownItemCount(q_fairy_dust) == 0)
                    htmltext = "fairy_mymyu_q0420_14.htm";
                else {
                    if (i0 < 5) {
                        htmltext = "fairy_mymyu_q0420_15.htm";
                        st.giveItems(hatchlings_soft_leather, 1);
                    } else {
                        htmltext = "fairy_mymyu_q0420_15t.htm";
                        st.giveItems(food_for_hatchling, 20);
                    }
                    st.takeItems(q_fairy_dust, -1);
                }
            }
        } else if (npcId == drake_exarion) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(6);
                st.setMemoState("little_wings", String.valueOf(7), true);
                st.giveItems(q_scale_of_drake_exarion, 1);
                st.takeItems(q_juice_of_monkshood, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "drake_exarion_q0420_03.htm";
            }
        } else if (npcId == drake_zwov) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(6);
                st.setMemoState("little_wings", String.valueOf(7), true);
                st.giveItems(q_scale_of_drake_zwov, 1);
                st.takeItems(q_juice_of_monkshood, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "drake_zwov_q0420_03.htm";
            }
        } else if (npcId == drake_kalibran) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(6);
                st.setMemoState("little_wings", String.valueOf(7), true);
                st.giveItems(q_scale_of_drake_kalibran, 1);
                st.takeItems(q_juice_of_monkshood, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "drake_kalibran_q0420_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                st.setMemoState("little_wings", String.valueOf(8), true);
                st.takeItems(q_egg_of_drake_kalibran, st.ownItemCount(q_egg_of_drake_kalibran) - 1);
                st.takeItems(q_scale_of_drake_kalibran, -1);
                htmltext = "drake_kalibran_q0420_06.htm";
            }
        } else if (npcId == wyrm_suzet) {
            if (event.equalsIgnoreCase("reply_1"))
                htmltext = "wyrm_suzet_q0420_03.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                st.setCond(6);
                st.setMemoState("little_wings", String.valueOf(7), true);
                st.giveItems(q_scale_of_wyrm_suzet, 1);
                st.takeItems(q_juice_of_monkshood, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "wyrm_suzet_q0420_04.htm";
            }
        } else if (npcId == wyrm_shamhai) {
            if (event.equalsIgnoreCase("reply_1")) {
                st.setCond(6);
                st.setMemoState("little_wings", String.valueOf(7), true);
                st.giveItems(q_scale_of_wyrm_shamhai, 1);
                st.takeItems(q_juice_of_monkshood, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "wyrm_shamhai_q0420_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("little_wings");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pet_manager_cooper) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pet_manager_cooper_q0420_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "pet_manager_cooper_q0420_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pet_manager_cooper) {
                    if (GetMemoState == 1)
                        htmltext = "pet_manager_cooper_q0420_04.htm";
                } else if (npcId == sage_cronos) {
                    if (GetMemoState == 1)
                        htmltext = "sage_cronos_q0420_01.htm";
                    else if ((GetMemoState == 2 || GetMemoState == 11) && st.ownItemCount(q_fairy_stone) == 0 && st.ownItemCount(q_fairy_stone_delux) == 0)
                        htmltext = "sage_cronos_q0420_07.htm";
                    else if (GetMemoState == 2 && (st.ownItemCount(q_fairy_stone) == 1 || st.ownItemCount(q_fairy_stone_delux) == 1)) {
                        st.setCond(3);
                        st.setMemoState("little_wings", String.valueOf(3), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "sage_cronos_q0420_08.htm";
                    } else if (GetMemoState == 11 && (st.ownItemCount(q_fairy_stone) == 1 || st.ownItemCount(q_fairy_stone_delux) == 1))
                        htmltext = "sage_cronos_q0420_14.htm";
                    else if (GetMemoState == 3)
                        htmltext = "sage_cronos_q0420_09.htm";
                    else if (GetMemoState == 10)
                        htmltext = "sage_cronos_q0420_10.htm";
                    else if (GetMemoState == 4 && (st.ownItemCount(q_fairy_stone) == 1 || st.ownItemCount(q_fairy_stone_delux) == 1))
                        htmltext = "sage_cronos_q0420_11.htm";
                } else if (npcId == marya) {
                    if (((GetMemoState == 2 || GetMemoState == 11) && ((st.ownItemCount(q_list_of_stuff_for_fs) == 1 && (st.ownItemCount(coal) < 10 || st.ownItemCount(charcoal) < 10 || st.ownItemCount(gemstone_d) == 0 || st.ownItemCount(silver_nugget) < 3 || st.ownItemCount(q_inpicios_back_skin) < 10)) || (st.ownItemCount(q_list_of_stuff_for_fsd) == 1 && (st.ownItemCount(coal) < 10 || st.ownItemCount(charcoal) < 10 || st.ownItemCount(gemstone_c) == 0 || st.ownItemCount(stone_of_purity) == 0 || st.ownItemCount(silver_nugget) < 5 || st.ownItemCount(q_inpicios_back_skin) < 20)))))
                        htmltext = "marya_q0420_01.htm";
                    else if (((GetMemoState == 2 || GetMemoState == 11) && st.ownItemCount(q_list_of_stuff_for_fs) == 1 && st.ownItemCount(coal) >= 10 && st.ownItemCount(charcoal) >= 10 && st.ownItemCount(gemstone_d) >= 1 && st.ownItemCount(silver_nugget) >= 3 && st.ownItemCount(q_inpicios_back_skin) >= 10))
                        htmltext = "marya_q0420_02.htm";
                    else if ((GetMemoState == 2 || GetMemoState == 11) && st.ownItemCount(q_list_of_stuff_for_fsd) == 1 && st.ownItemCount(coal) >= 10 && st.ownItemCount(charcoal) >= 10 && st.ownItemCount(gemstone_c) >= 1 && st.ownItemCount(stone_of_purity) >= 1 && st.ownItemCount(silver_nugget) >= 5 && st.ownItemCount(q_inpicios_back_skin) >= 20)
                        htmltext = "marya_q0420_04.htm";
                    else if (st.ownItemCount(q_fairy_stone) == 1 || st.ownItemCount(q_fairy_stone_delux) == 1)
                        htmltext = "marya_q0420_06.htm";
                } else if (npcId == guard_byron) {
                    if (GetMemoState == 3 && (st.ownItemCount(q_fairy_stone) == 1 || st.ownItemCount(q_fairy_stone_delux) == 1))
                        htmltext = "guard_byron_q0420_01.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_fairy_stone) == 1) {
                        st.setCond(4);
                        st.setMemoState("little_wings", String.valueOf(4), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "guard_byron_q0420_05.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_fairy_stone_delux) == 1) {
                        st.setCond(4);
                        st.setMemoState("little_wings", String.valueOf(4), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "guard_byron_q0420_06.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_fairy_stone) == 1)
                        htmltext = "guard_byron_q0420_07.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_fairy_stone_delux) == 1)
                        htmltext = "guard_byron_q0420_08.htm";
                    else if (GetMemoState == 10)
                        htmltext = "guard_byron_q0420_09.htm";
                    else if (GetMemoState == 11 && st.ownItemCount(q_fairy_stone) == 0 && st.ownItemCount(q_fairy_stone_delux) == 0 && (st.ownItemCount(q_list_of_stuff_for_fs) == 1 || st.ownItemCount(q_list_of_stuff_for_fsd) == 1))
                        htmltext = "guard_byron_q0420_10.htm";
                } else if (npcId == fairy_mymyu) {
                    if (st.ownItemCount(q_fairy_stone) == 1)
                        htmltext = "fairy_mymyu_q0420_02.htm";
                    else if (st.ownItemCount(q_fairy_stone_delux) == 1)
                        htmltext = "fairy_mymyu_q0420_04.htm";
                    else if (GetMemoState == 5)
                        htmltext = "fairy_mymyu_q0420_07.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_juice_of_monkshood) == 1)
                        htmltext = "fairy_mymyu_q0420_09.htm";
                    else if (GetMemoState == 7) {
                        if (st.ownItemCount(q_egg_of_drake_exarion) < 20 && st.ownItemCount(q_egg_of_drake_zwov) < 20 && st.ownItemCount(q_egg_of_drake_kalibran) < 20 && st.ownItemCount(q_egg_of_wyrm_suzet) < 20 && st.ownItemCount(q_egg_of_wyrm_shamhai) < 20)
                            htmltext = "fairy_mymyu_q0420_10.htm";
                        else
                            htmltext = "fairy_mymyu_q0420_11.htm";
                    } else if (GetMemoState == 8)
                        htmltext = "fairy_mymyu_q0420_12.htm";
                    else if ((GetMemoState < 4 || GetMemoState > 8) && st.ownItemCount(q_fairy_stone) == 0 && st.ownItemCount(q_fairy_stone_delux) == 0) {
                        count_fairy_mymyu++;
                        int i0 = count_fairy_mymyu % 3;
                        if (i0 == 0) {
                            // count_fairy_mymyu = 0;
                            st.getPlayer().teleToLocation(109816, 40854, -4640);
                        } else if (i0 == 1) {
                            // count_fairy_mymyu = 0;
                            st.getPlayer().teleToLocation(108940, 41615, -4643);
                        } else {
                            // count_fairy_mymyu = 0;
                            st.getPlayer().teleToLocation(110395, 41625, -4642);
                        }
                    }
                } else if (npcId == drake_exarion) {
                    if (GetMemoState == 6 && st.ownItemCount(q_juice_of_monkshood) == 1)
                        htmltext = "drake_exarion_q0420_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_egg_of_drake_exarion) < 20 && st.ownItemCount(q_scale_of_drake_exarion) == 1)
                        htmltext = "drake_exarion_q0420_04.htm";
                    else if (st.ownItemCount(q_egg_of_drake_exarion) >= 20) {
                        st.setCond(7);
                        st.setMemoState("little_wings", String.valueOf(8), true);
                        st.takeItems(q_egg_of_drake_exarion, st.ownItemCount(q_egg_of_drake_exarion) - 1);
                        st.takeItems(q_scale_of_drake_exarion, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "drake_exarion_q0420_05.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_egg_of_drake_exarion) == 1)
                        htmltext = "drake_exarion_q0420_06.htm";
                } else if (npcId == drake_zwov) {
                    if (GetMemoState == 6 && st.ownItemCount(q_juice_of_monkshood) == 1)
                        htmltext = "drake_zwov_q0420_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_egg_of_drake_zwov) < 20 && st.ownItemCount(q_scale_of_drake_zwov) == 1)
                        htmltext = "drake_zwov_q0420_04.htm";
                    else if (st.ownItemCount(q_egg_of_drake_zwov) >= 20) {
                        st.setCond(7);
                        st.setMemoState("little_wings", String.valueOf(8), true);
                        st.takeItems(q_egg_of_drake_zwov, st.ownItemCount(q_egg_of_drake_zwov) - 1);
                        st.takeItems(q_scale_of_drake_zwov, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "drake_zwov_q0420_05.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_egg_of_drake_zwov) == 1)
                        htmltext = "drake_zwov_q0420_06.htm";
                } else if (npcId == drake_kalibran) {
                    if (GetMemoState == 6 && st.ownItemCount(q_juice_of_monkshood) == 1)
                        htmltext = "drake_kalibran_q0420_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_egg_of_drake_kalibran) < 20 && st.ownItemCount(q_scale_of_drake_kalibran) == 1)
                        htmltext = "drake_kalibran_q0420_04.htm";
                    else if (st.ownItemCount(q_egg_of_drake_kalibran) >= 20) {
                        st.setCond(7);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "drake_kalibran_q0420_05.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_egg_of_drake_kalibran) == 1)
                        htmltext = "drake_kalibran_q0420_07.htm";
                } else if (npcId == wyrm_suzet) {
                    if (GetMemoState == 6 && st.ownItemCount(q_juice_of_monkshood) == 1)
                        htmltext = "wyrm_suzet_q0420_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_egg_of_wyrm_suzet) < 20 && st.ownItemCount(q_scale_of_wyrm_suzet) == 1)
                        htmltext = "wyrm_suzet_q0420_05.htm";
                    else if (st.ownItemCount(q_egg_of_wyrm_suzet) >= 20) {
                        st.setCond(7);
                        st.setMemoState("little_wings", String.valueOf(8), true);
                        st.takeItems(q_egg_of_wyrm_suzet, st.ownItemCount(q_egg_of_wyrm_suzet) - 1);
                        st.takeItems(q_scale_of_wyrm_suzet, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "wyrm_suzet_q0420_06.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_egg_of_wyrm_suzet) == 1)
                        htmltext = "wyrm_suzet_q0420_07.htm";
                } else if (npcId == wyrm_shamhai) {
                    if (GetMemoState == 6 && st.ownItemCount(q_juice_of_monkshood) == 1)
                        htmltext = "wyrm_shamhai_q0420_02.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_egg_of_wyrm_shamhai) < 20 && st.ownItemCount(q_scale_of_wyrm_shamhai) == 1)
                        htmltext = "wyrm_shamhai_q0420_04.htm";
                    else if (st.ownItemCount(q_egg_of_wyrm_shamhai) >= 20) {
                        st.setCond(7);
                        st.setMemoState("little_wings", String.valueOf(8), true);
                        st.takeItems(q_egg_of_wyrm_shamhai, st.ownItemCount(q_egg_of_wyrm_shamhai) - 1);
                        st.takeItems(q_scale_of_wyrm_shamhai, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "wyrm_shamhai_q0420_05.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_egg_of_wyrm_shamhai) == 1)
                        htmltext = "wyrm_shamhai_q0420_06.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == dead_seeker) {
            if (st.ownItemCount(q_scale_of_wyrm_shamhai) == 1 && st.ownItemCount(q_egg_of_wyrm_shamhai) < 20) {
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_egg_of_wyrm_shamhai, 1);
                    if (st.ownItemCount(q_egg_of_wyrm_shamhai) >= 19)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == inpicio) {
            if (((st.ownItemCount(q_list_of_stuff_for_fs) == 1 && st.ownItemCount(q_inpicios_back_skin) < 10) || (st.ownItemCount(q_list_of_stuff_for_fsd) == 1 && st.ownItemCount(q_inpicios_back_skin) < 20))) {
                if (Rnd.get(100) < 30) {
                    int i0 = 19;
                    if (st.ownItemCount(q_list_of_stuff_for_fs) == 1) {
                        i0 = 9;
                    }
                    if (st.ownItemCount(q_inpicios_back_skin) >= i0)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                    st.giveItems(q_inpicios_back_skin, 1);
                }
            }
        } else if (npcId == marsh_spider) {
            if (st.ownItemCount(q_scale_of_drake_zwov) == 1 && st.ownItemCount(q_egg_of_drake_zwov) < 20) {
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_egg_of_drake_zwov, 1);
                    if (st.ownItemCount(q_egg_of_drake_zwov) >= 19)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == breka_orc_overlord) {
            if (st.ownItemCount(q_scale_of_wyrm_suzet) == 1 && st.ownItemCount(q_egg_of_wyrm_suzet) < 20) {
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_egg_of_wyrm_suzet, 1);
                    if (st.ownItemCount(q_egg_of_wyrm_suzet) >= 19)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                    Functions.npcSay(npc, NpcString.I_THOUGHT_ID_CAUGHT_ONE_SHARE_WHEW);
                }
            }
        } else if (npcId == road_scavenger) {
            if (st.ownItemCount(q_scale_of_drake_kalibran) == 1 && st.ownItemCount(q_egg_of_drake_kalibran) < 20) {
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_egg_of_drake_kalibran, 1);
                    if (st.ownItemCount(q_egg_of_drake_kalibran) >= 19)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                    Functions.npcSay(npc, NpcString.HEY_EVERYBODY_WATCH_THE_EGGS);
                }
            }
        } else if (npcId == leto_lizardman_warrior) {
            if (st.ownItemCount(q_scale_of_drake_exarion) == 1 && st.ownItemCount(q_egg_of_drake_exarion) < 20) {
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_egg_of_drake_exarion, 1);
                    if (st.ownItemCount(q_egg_of_drake_exarion) >= 19)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                    Functions.npcSay(npc, NpcString.IF_THE_EGGS_GET_TAKEN_WERE_DEAD);
                }
            }
        } else if (npcId == fline || npcId == liele || npcId == ti_mi_kran || npcId == pan_ruem || npcId == unicorn || npcId == unicorn || npcId == forest_runner || npcId == fline_elder || npcId == liele_elder || npcId == ti_mi_kran_elder || npcId == pan_ruem_elder || npcId == unicorn_elder) {
            if (st.ownItemCount(q_fairy_stone_delux) == 1) {
                if (Rnd.get(100) < 30) {
                    st.setMemoState("little_wings", String.valueOf(10), true);
                    st.takeItems(q_fairy_stone_delux, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    Functions.npcSay(npc, NpcString.THE_STONE_THE_ELVEN_STONE_BROKE);
                }
            }
        }
        return null;
    }
}