package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.htm.HtmCache;
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
 * @date 03/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _663_SeductiveWhispers extends Quest {
    // npc
    private final static int blacksmith_wilbert = 30846;
    // mobs
    private final static int doom_knight = 20674;
    private final static int punishment_of_undead = 20678;
    private final static int hungry_corpse = 20954;
    private final static int ghost_war = 20955;
    private final static int past_knight = 20956;
    private final static int nihil_invader = 20957;
    private final static int blade_death = 20958;
    private final static int dark_guard = 20959;
    private final static int bloody_ghost = 20960;
    private final static int bloody_knight = 20961;
    private final static int bloody_priest = 20962;
    private final static int bloody_lord = 20963;
    private final static int spite_soul_leader = 20974;
    private final static int spite_soul_wizard = 20975;
    private final static int spite_soul_fighter = 20976;
    private final static int manes_of_ruin = 20996;
    private final static int soldier_of_grief = 20997;
    private final static int cruel_punishment = 20998;
    private final static int roving_soul = 20999;
    private final static int soul_of_ruin = 21000;
    private final static int wrecked_archer = 21001;
    private final static int doom_scout = 21002;
    private final static int doom_servant = 21006;
    private final static int doom_guard = 21007;
    private final static int doom_archer = 21008;
    private final static int doom_trooper = 21009;
    private final static int doom_warrior = 21010;
    // etcitem
    private final static int q_orb_of_spirit = 8766;
    private final static int scrl_of_ench_wp_d = 955;
    private final static int scrl_of_ench_wp_c = 951;
    private final static int scrl_of_ench_wp_b = 947;
    private final static int scrl_of_ench_am_b = 948;
    private final static int scrl_of_ench_wp_a = 729;
    private final static int scrl_of_ench_am_a = 730;
    private final static int rp_great_sword_i = 4963;
    private final static int rp_heavy_war_axe_i = 4964;
    private final static int rp_sprites_staff_i = 4965;
    private final static int rp_kshanberk_i = 4966;
    private final static int rp_sword_of_valhalla_i = 4967;
    private final static int rp_kris_i = 4968;
    private final static int rp_hell_knife_i = 4969;
    private final static int rp_arthro_nail_i = 4970;
    private final static int rp_dark_elven_long_bow_i = 4971;
    private final static int rp_great_axe_i = 4972;
    private final static int rp_sword_of_damascus_i = 5000;
    private final static int rp_lancia_i = 5001;
    private final static int rp_deadmans_glory_i = 5002;
    private final static int rp_art_of_battle_axe_i = 5003;
    private final static int rp_staff_of_evil_sprit_i = 5004;
    private final static int rp_demons_sword_i = 5005;
    private final static int rp_bellion_cestus_i = 5006;
    private final static int rp_hazard_bow_i = 5007;
    private final static int great_sword_blade = 4104;
    private final static int heavy_war_axe_head = 4105;
    private final static int sprites_staff_head = 4106;
    private final static int kshanberk_blade = 4107;
    private final static int sword_of_valhalla_blade = 4108;
    private final static int kris_edge = 4109;
    private final static int hell_knife_edge = 4110;
    private final static int arthro_nail_blade = 4111;
    private final static int dark_elven_long_bow_shaft = 4112;
    private final static int great_axe_head = 4113;
    private final static int sword_of_damascus_blade = 4114;
    private final static int lancia_blade = 4115;
    private final static int deadmans_glory_stone = 4116;
    private final static int art_of_battle_axe_blade = 4117;
    private final static int staff_of_evil_sprit_head = 4118;
    private final static int demons_sword_edge = 4119;
    private final static int bellion_cestus_edge = 4120;
    private final static int hazard_bow_shaft = 4121;

    public _663_SeductiveWhispers() {
        super(true);
        addStartNpc(blacksmith_wilbert);
        addKillId(doom_knight, punishment_of_undead, hungry_corpse, ghost_war, past_knight, nihil_invader, blade_death, dark_guard, bloody_ghost, bloody_knight, bloody_priest, bloody_lord, spite_soul_leader, spite_soul_wizard, spite_soul_fighter, manes_of_ruin, soldier_of_grief, cruel_punishment, roving_soul, soul_of_ruin, wrecked_archer, doom_scout, doom_servant, doom_guard, doom_archer, doom_trooper, doom_warrior);
        addLevelCheck(50);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("whispers_of_temptation");
        int GetMemoStateEx = st.getInt("whispers_of_temptation_ex");
        int npcId = npc.getNpcId();
        int i0 = 0;
        int i1 = 0;
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        if (npcId == blacksmith_wilbert) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "blacksmith_wilbert_q0663_03.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "blacksmith_wilbert_q0663_01a.htm";
            else if (event.equalsIgnoreCase("reply_4") && GetMemoState % 10 <= 4) {
                if (GetMemoState / 10 < 1) {
                    if (st.ownItemCount(q_orb_of_spirit) >= 50) {
                        st.takeItems(q_orb_of_spirit, 50);
                        st.setMemoState("whispers_of_temptation", String.valueOf(5), true);
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                        htmltext = "blacksmith_wilbert_q0663_09.htm";
                    } else
                        htmltext = "blacksmith_wilbert_q0663_10.htm";
                } else {
                    i0 = GetMemoState / 10;
                    i1 = i0 * 10 + 5;
                    st.setMemoState("whispers_of_temptation", String.valueOf(i1), true);
                    st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                    htmltext = "blacksmith_wilbert_q0663_09a.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5") && GetMemoState % 10 == 5 && GetMemoState / 1000 == 0) {
                i0 = GetMemoStateEx;
                if (i0 < 0) {
                    i0 = 0;
                }
                i1 = i0 % 10;
                i2 = (i0 - i1) / 10;
                int talker_param1 = Rnd.get(2) + 1;
                int talker_param2 = Rnd.get(5) + 1;
                i5 = GetMemoState / 10;
                int talker_param3 = talker_param1 * 10 + talker_param2;
                if (talker_param1 == i2) {
                    i3 = talker_param2 + i1;
                    if (i3 % 5 == 0 && i3 != 10) {
                        if (GetMemoState % 100 / 10 >= 7) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_14.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                            htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                            htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                            st.setMemoState("whispers_of_temptation", String.valueOf(4), true);
                            st.giveItems(ADENA_ID, 2384000);
                            st.giveItems(scrl_of_ench_wp_a, 1);
                            st.giveItems(scrl_of_ench_am_a, 2);
                        } else {
                            htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_13.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                            htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                            htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                            htmltext = htmltext.replace("<?wincount?>", String.valueOf(i5 + 1));
                            i4 = GetMemoState / 10 * 10 + 7;
                            st.setMemoState("whispers_of_temptation", String.valueOf(i4), true);
                        }
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_12.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        i4 = GetMemoState / 10 * 10 + 6;
                        st.setMemoState("whispers_of_temptation", String.valueOf(i4), true);
                    }
                } else if (talker_param1 != i2) {
                    if (talker_param2 == 5 || i1 == 5) {
                        if (GetMemoState % 100 / 10 >= 7) {
                            htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_14.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                            htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                            htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                            st.giveItems(ADENA_ID, 2384000);
                            st.giveItems(scrl_of_ench_wp_a, 1);
                            st.giveItems(scrl_of_ench_am_a, 2);
                            st.setMemoState("whispers_of_temptation", String.valueOf(4), true);
                        } else {
                            htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_13.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                            htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                            htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                            htmltext = htmltext.replace("<?wincount?>", String.valueOf(i5 + 1));
                            i4 = GetMemoState / 10 * 10 + 7;
                            st.setMemoState("whispers_of_temptation", String.valueOf(i4), true);
                        }
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_12.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        talker_param3 = talker_param1 * 10 + talker_param2;
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        i4 = GetMemoState / 10 * 10 + 6;
                        st.setMemoState("whispers_of_temptation", String.valueOf(i4), true);
                    }
                }
            } else if (event.equalsIgnoreCase("reply_6") && GetMemoState % 10 == 6 && GetMemoState / 1000 == 0) {
                i0 = GetMemoStateEx;
                if (i0 < 0) {
                    i0 = 0;
                }
                i1 = i0 % 10;
                i2 = (i0 - i1) / 10;
                int talker_param1 = Rnd.get(2) + 1;
                int talker_param2 = Rnd.get(5) + 1;
                int talker_param3 = talker_param1 * 10 + talker_param2;
                if (talker_param1 == i2) {
                    i3 = talker_param2 + i1;
                    if (i3 % 5 == 0 && i3 != 10) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_19.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_18.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        talker_param3 = talker_param1 * 10 + talker_param2;
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        i4 = GetMemoState / 10 * 10 + 5;
                        st.setMemoState("whispers_of_temptation", String.valueOf(i4), true);
                    }
                } else if (talker_param1 != i2) {
                    i3 = talker_param1 + i1;
                    i4 = 66310 + i2;
                    i5 = 66310 + talker_param1;
                    if (talker_param2 == 5 || i1 == 5) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_19.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_18.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        talker_param3 = talker_param1 * 10 + talker_param2;
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        i4 = GetMemoState / 10 * 10 + 5;
                        st.setMemoState("whispers_of_temptation", String.valueOf(i4), true);
                    }
                }
            } else if (event.equalsIgnoreCase("reply_8") && GetMemoState % 10 == 7 && GetMemoState / 1000 == 0) {
                i0 = GetMemoState / 10;
                i1 = (i0 + 1) * 10 + 4;
                st.setMemoState("whispers_of_temptation", String.valueOf(i1), true);
                st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                htmltext = "blacksmith_wilbert_q0663_20.htm";
            } else if (event.equalsIgnoreCase("reply_9") && GetMemoState % 10 == 7 && GetMemoState / 1000 == 0) {
                i0 = GetMemoState / 10;
                if (i0 == 0)
                    st.giveItems(ADENA_ID, 40000);
                else if (i0 == 1)
                    st.giveItems(ADENA_ID, 80000);
                else if (i0 == 2) {
                    st.giveItems(ADENA_ID, 110000);
                    st.giveItems(scrl_of_ench_wp_d, 1);
                } else if (i0 == 3) {
                    st.giveItems(ADENA_ID, 199000);
                    st.giveItems(scrl_of_ench_wp_c, 1);
                } else if (i0 == 4) {
                    st.giveItems(ADENA_ID, 388000);
                    i1 = Rnd.get(18) + 1;
                    if (i1 == 1)
                        st.giveItems(rp_great_sword_i, 1);
                    else if (i1 == 2)
                        st.giveItems(rp_heavy_war_axe_i, 1);
                    else if (i1 == 3)
                        st.giveItems(rp_sprites_staff_i, 1);
                    else if (i1 == 4)
                        st.giveItems(rp_kshanberk_i, 1);
                    else if (i1 == 5)
                        st.giveItems(rp_sword_of_valhalla_i, 1);
                    else if (i1 == 6)
                        st.giveItems(rp_kris_i, 1);
                    else if (i1 == 7)
                        st.giveItems(rp_hell_knife_i, 1);
                    else if (i1 == 8)
                        st.giveItems(rp_arthro_nail_i, 1);
                    else if (i1 == 9)
                        st.giveItems(rp_dark_elven_long_bow_i, 1);
                    else if (i1 == 10)
                        st.giveItems(rp_great_axe_i, 1);
                    else if (i1 == 11)
                        st.giveItems(rp_sword_of_damascus_i, 1);
                    else if (i1 == 12)
                        st.giveItems(rp_lancia_i, 1);
                    else if (i1 == 13)
                        st.giveItems(rp_deadmans_glory_i, 1);
                    else if (i1 == 14)
                        st.giveItems(rp_art_of_battle_axe_i, 1);
                    else if (i1 == 15)
                        st.giveItems(rp_staff_of_evil_sprit_i, 1);
                    else if (i1 == 16)
                        st.giveItems(rp_demons_sword_i, 1);
                    else if (i1 == 17)
                        st.giveItems(rp_bellion_cestus_i, 1);
                    else if (i1 == 18)
                        st.giveItems(rp_hazard_bow_i, 1);
                } else if (i0 == 5) {
                    st.giveItems(ADENA_ID, 675000);
                    i1 = Rnd.get(18) + 1;
                    if (i1 == 1)
                        st.giveItems(great_sword_blade, 12);
                    else if (i1 == 2)
                        st.giveItems(great_axe_head, 12);
                    else if (i1 == 3)
                        st.giveItems(dark_elven_long_bow_shaft, 12);
                    else if (i1 == 4)
                        st.giveItems(sword_of_valhalla_blade, 12);
                    else if (i1 == 5)
                        st.giveItems(arthro_nail_blade, 12);
                    else if (i1 == 6)
                        st.giveItems(sprites_staff_head, 12);
                    else if (i1 == 7)
                        st.giveItems(kris_edge, 12);
                    else if (i1 == 8)
                        st.giveItems(kshanberk_blade, 12);
                    else if (i1 == 9)
                        st.giveItems(heavy_war_axe_head, 12);
                    else if (i1 == 10)
                        st.giveItems(hell_knife_edge, 12);
                    else if (i1 == 11)
                        st.giveItems(sword_of_damascus_blade, 13);
                    else if (i1 == 12)
                        st.giveItems(lancia_blade, 13);
                    else if (i1 == 13)
                        st.giveItems(bellion_cestus_edge, 13);
                    else if (i1 == 14)
                        st.giveItems(staff_of_evil_sprit_head, 13);
                    else if (i1 == 15)
                        st.giveItems(deadmans_glory_stone, 13);
                    else if (i1 == 16)
                        st.giveItems(art_of_battle_axe_blade, 13);
                    else if (i1 == 17)
                        st.giveItems(demons_sword_edge, 13);
                    else if (i1 == 18)
                        st.giveItems(hazard_bow_shaft, 13);
                } else if (i0 == 6) {
                    st.giveItems(ADENA_ID, 1284000);
                    st.giveItems(scrl_of_ench_wp_b, 2);
                    st.giveItems(scrl_of_ench_am_b, 2);
                }
                st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                htmltext = "blacksmith_wilbert_q0663_21.htm";
            } else if (event.equalsIgnoreCase("reply_10") && GetMemoState == 1 && GetMemoState / 1000 == 0)
                htmltext = "blacksmith_wilbert_q0663_21a.htm";
            else if (event.equalsIgnoreCase("reply_14") && GetMemoState % 10 == 1) {
                if (st.ownItemCount(q_orb_of_spirit) >= 1) {
                    st.setMemoState("whispers_of_temptation", String.valueOf(1005), true);
                    st.takeItems(q_orb_of_spirit, 1);
                    htmltext = "blacksmith_wilbert_q0663_22.htm";
                } else
                    htmltext = "blacksmith_wilbert_q0663_22a.htm";
            } else if (event.equalsIgnoreCase("reply_15") && GetMemoState == 1005) {
                i0 = GetMemoStateEx;
                if (i0 < 0) {
                    i0 = 0;
                }
                i1 = i0 % 10;
                i2 = (i0 - i1) / 10;
                int talker_param1 = Rnd.get(2) + 1;
                int talker_param2 = Rnd.get(5) + 1;
                int talker_param3 = talker_param1 * 10 + talker_param2;
                if (talker_param1 == i2) {
                    i3 = talker_param2 + i1;
                    i4 = 66310 + i2;
                    i5 = 66310 + talker_param1;
                    if (i3 % 5 == 0 && i3 != 10) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_25.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        // myself::FHTML_SetInt( fhtml0, "card1", i1 ); ???
                        st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                        st.giveItems(ADENA_ID, 800);
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_24.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        st.setMemoState("whispers_of_temptation", String.valueOf(1006), true);
                    }
                } else if (talker_param1 != i2) {
                    if (talker_param2 == 5 || i1 == 5) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_25.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                        st.giveItems(ADENA_ID, 800);
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_24.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        st.setMemoState("whispers_of_temptation", String.valueOf(1006), true);
                    }
                }
            } else if (event.equalsIgnoreCase("reply_16") && GetMemoState == 1006) {
                i0 = GetMemoStateEx;
                if (i0 < 0) {
                    i0 = 0;
                }
                i1 = i0 % 10;
                i2 = (i0 - i1) / 10;
                int talker_param1 = Rnd.get(2) + 1;
                int talker_param2 = Rnd.get(5) + 1;
                int talker_param3 = talker_param1 * 10 + talker_param2;
                if (talker_param1 == i2) {
                    i3 = talker_param2 + i1;
                    if (i3 % 5 == 0 && i3 != 10) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_29.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_28.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        talker_param3 = talker_param1 * 10 + talker_param2;
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        st.setMemoState("whispers_of_temptation", String.valueOf(1005), true);
                    }
                } else if (talker_param1 != i2) {
                    i3 = talker_param1 + i1;
                    i4 = 66310 + i2;
                    i5 = 66310 + talker_param1;
                    if (talker_param2 == 5 || i1 == 5) {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_29.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(0), true);
                    } else {
                        htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_28.htm", st.getPlayer());
                        htmltext = htmltext.replace("<?card1pic?>", String.valueOf(i0));
                        htmltext = htmltext.replace("<?card2pic?>", String.valueOf(talker_param3));
                        htmltext = htmltext.replace("<?name?>", st.getPlayer().getName());
                        talker_param3 = talker_param1 * 10 + talker_param2;
                        st.setMemoState("whispers_of_temptation_ex", String.valueOf(talker_param3), true);
                        st.setMemoState("whispers_of_temptation", String.valueOf(1005), true);
                    }
                }
            } else if (event.equalsIgnoreCase("reply_20")) {
                st.removeMemo("whispers_of_temptation");
                st.removeMemo("whispers_of_temptation_ex");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "blacksmith_wilbert_q0663_30.htm";
            } else if (event.equalsIgnoreCase("reply_21"))
                htmltext = "blacksmith_wilbert_q0663_31.htm";
            else if (event.equalsIgnoreCase("reply_22"))
                htmltext = "blacksmith_wilbert_q0663_32.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("whispers_of_temptation");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == blacksmith_wilbert) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "blacksmith_wilbert_q0663_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "blacksmith_wilbert_q0663_01.htm";
                    }
                }
                break;
            case STARTED:
                if (npcId == blacksmith_wilbert) {
                    if (GetMemoState < 4 && GetMemoState >= 1 && st.ownItemCount(q_orb_of_spirit) == 0)
                        htmltext = "blacksmith_wilbert_q0663_04.htm";
                    else if (GetMemoState < 4 && GetMemoState >= 1 && st.ownItemCount(q_orb_of_spirit) > 0)
                        htmltext = "blacksmith_wilbert_q0663_05.htm";
                    else if (GetMemoState % 10 == 4 && GetMemoState / 1000 == 0)
                        htmltext = "blacksmith_wilbert_q0663_05a.htm";
                    else if (GetMemoState % 10 == 5 && GetMemoState / 1000 == 0)
                        htmltext = "blacksmith_wilbert_q0663_11.htm";
                    else if (GetMemoState % 10 == 6 && GetMemoState / 1000 == 0)
                        htmltext = "blacksmith_wilbert_q0663_15.htm";
                    else if (GetMemoState % 10 == 7 && GetMemoState / 1000 == 0) {
                        int i0 = GetMemoState % 100;
                        if (i0 / 10 >= 7) {
                            st.setMemoState("whispers_of_temptation", String.valueOf(1), true);
                            st.giveItems(ADENA_ID, 2384000);
                            st.giveItems(scrl_of_ench_wp_a, 1);
                            st.giveItems(scrl_of_ench_am_a, 2);
                            htmltext = "blacksmith_wilbert_q0663_17.htm";
                        } else {
                            int i5 = GetMemoState / 10;
                            htmltext = HtmCache.getInstance().getHtml("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_16.htm", st.getPlayer());
                            htmltext = htmltext.replace("<?wincount?>", String.valueOf(i5 + 1));
                        }
                    } else if (GetMemoState == 1005)
                        htmltext = "blacksmith_wilbert_q0663_23.htm";
                    else if (GetMemoState == 1006)
                        htmltext = "blacksmith_wilbert_q0663_26.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("whispers_of_temptation");
        int npcId = npc.getNpcId();
        if (npcId == doom_knight) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 807) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == punishment_of_undead || npcId == bloody_ghost) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 372) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == hungry_corpse) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 460) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ghost_war) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 537) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == past_knight || npcId == doom_guard) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 540) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == nihil_invader) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 565) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == blade_death) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 425) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == dark_guard) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 682) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == bloody_knight) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 547) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == bloody_priest) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 522) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == bloody_lord) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 498) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == spite_soul_leader) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 100) {
                    st.giveItems(q_orb_of_spirit, 2);
                    st.soundEffect(SOUND_ITEMGET);
                } else {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == spite_soul_wizard) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 975) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == spite_soul_fighter) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 825) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == manes_of_ruin) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 385) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == soldier_of_grief) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 342) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == cruel_punishment) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 377) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == roving_soul) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 450) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == soul_of_ruin) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 395) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == wrecked_archer) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 535) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == doom_scout) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 472) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == doom_servant) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 502) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == doom_archer) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 692) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == doom_trooper) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 740) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == doom_warrior) {
            if (GetMemoState >= 1 && GetMemoState <= 4) {
                int i0 = Rnd.get(1000);
                if (i0 < 595) {
                    st.giveItems(q_orb_of_spirit, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}