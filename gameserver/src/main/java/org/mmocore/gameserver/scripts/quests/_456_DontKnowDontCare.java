package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 22/04/2016
 * @lastedit 22/04/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _456_DontKnowDontCare extends Quest {
    // npc
    private static final int separated_soul_01 = 32864;
    private static final int separated_soul_02 = 32865;
    private static final int separated_soul_03 = 32866;
    private static final int separated_soul_04 = 32867;
    private static final int separated_soul_05 = 32868;
    private static final int separated_soul_06 = 32869;
    private static final int separated_soul_07 = 32870;
    private static final int separated_soul_08 = 32891;
    // raidboss npc
    private static final int corpse_drake_lord = 32884;
    private static final int corpse_behemoth_leader = 32885;
    private static final int corpse_dragon_beast = 32886;
    // questitem
    private static final int q_marrow_of_drake_lord = 17251;
    private static final int q_marrow_of_behemoth_leader = 17252;
    private static final int q_marrow_of_dragon_beast = 17253;
    // etcitem
    private static final int sealed_verpes_helmet = 15743;
    private static final int sealed_verpes_leather_helmet = 15744;
    private static final int sealed_verpes_circlet = 15745;
    private static final int sealed_verpes_cuirass = 15746;
    private static final int sealed_verpes_houberk = 15747;
    private static final int sealed_verpes_jaket = 15748;
    private static final int sealed_verpes_gaiter = 15749;
    private static final int sealed_verpes_leather_legging = 15750;
    private static final int sealed_verpes_hose = 15751;
    private static final int sealed_verpes_gauntlet = 15752;
    private static final int sealed_verpes_leather_gloves = 15753;
    private static final int sealed_verpes_gloves = 15754;
    private static final int sealed_verpes_boots = 15755;
    private static final int sealed_verpes_leather_boots = 15756;
    private static final int sealed_verpes_shoes = 15757;
    private static final int sealed_verpes_shield = 15758;
    private static final int sealed_verpes_sigil = 15759;
    private static final int sealed_verpes_ring = 15763;
    private static final int sealed_verpes_earring = 15764;
    private static final int sealed_verpes_necklace = 15765;
    private static final int peri_el_sword = 15558;
    private static final int skul_edge = 15559;
    private static final int vigwik_axe = 15560;
    private static final int evildeity_maul = 15561;
    private static final int feathereye_blade = 15562;
    private static final int octo_claw = 15563;
    private static final int doubletopa_spear = 15564;
    private static final int cuticle = 15565;
    private static final int blackvisage = 15566;
    private static final int veniplant_sword = 15567;
    private static final int skullcarnium_bow = 15568;
    private static final int gemtail_rapier = 15569;
    private static final int fin_invincible_blade = 15570;
    private static final int rubecutter_crossbow = 15571;
    private static final int blessed_scrl_of_ench_wp_s = 6577;
    private static final int blessed_scrl_of_ench_am_s = 6578;
    private static final int high_ore_of_fire = 9552;
    private static final int high_ore_of_water = 9553;
    private static final int high_ore_of_earth = 9554;
    private static final int high_ore_of_wind = 9555;
    private static final int high_ore_of_unholy = 9556;
    private static final int high_ore_of_holy = 9557;
    private static final int scrl_of_ench_wp_s = 959;
    private static final int gemstone_s = 2134;

    public _456_DontKnowDontCare() {
        super(PARTY_CC);
        addStartNpc(separated_soul_01, separated_soul_02, separated_soul_03, separated_soul_04, separated_soul_05, separated_soul_06, separated_soul_07, separated_soul_08);
        addTalkId(corpse_drake_lord, corpse_behemoth_leader, corpse_dragon_beast);
        addFirstTalkId(corpse_drake_lord, corpse_behemoth_leader, corpse_dragon_beast);
        addQuestItem(q_marrow_of_drake_lord, q_marrow_of_behemoth_leader, q_marrow_of_dragon_beast);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_spirit_with_no_name");
        int npcId = npc.getNpcId();
        if (npcId == separated_soul_01 || npcId == separated_soul_02 || npcId == separated_soul_03 || npcId == separated_soul_04 || npcId == separated_soul_05 || npcId == separated_soul_06 || npcId == separated_soul_07 || npcId == separated_soul_08) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("the_spirit_with_no_name", String.valueOf(1), true);
                st.soundEffect(SOUND_ACCEPT);
                st.setState(STARTED);
                htmltext = "separated_soul_01_q0456_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=456&reply=1")) {
                if (st.getPlayer().getLevel() < 80 && !st.isNowAvailable()) {
                    htmltext = "separated_soul_01_q0456_03.htm";
                } else if (st.getPlayer().getLevel() >= 80 && st.isNowAvailable()) {
                    htmltext = "separated_soul_01_q0456_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=456&reply=2")) {
                htmltext = "separated_soul_01_q0456_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=456&reply=3")) {
                htmltext = "separated_soul_01_q0456_06.htm";
            }
        } else if (npcId == corpse_drake_lord) {
            if (event.equalsIgnoreCase("menu_select?ask=456&reply=1")) {
                if (GetMemoState == 1 && npc != null && st.getInt("RaidKilled") == npc.getObjectId()) {
                    if (st.ownItemCount(q_marrow_of_behemoth_leader) == 0 || st.ownItemCount(q_marrow_of_dragon_beast) == 0) {
                        st.giveItems(q_marrow_of_drake_lord, 1);
                        htmltext = "corpse_drake_lord_q0456_01.htm";
                    }
                    if (st.ownItemCount(q_marrow_of_behemoth_leader) == 1 && st.ownItemCount(q_marrow_of_dragon_beast) == 1) {
                        st.setCond(2);
                        st.giveItems(q_marrow_of_drake_lord, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "corpse_drake_lord_q0456_01.htm";
                    }
                }
            }
        } else if (npcId == corpse_behemoth_leader) {
            if (event.equalsIgnoreCase("menu_select?ask=456&reply=1")) {
                if (GetMemoState == 1 && npc != null && st.getInt("RaidKilled") == npc.getObjectId()) {
                    if (st.ownItemCount(q_marrow_of_drake_lord) == 0 || st.ownItemCount(q_marrow_of_dragon_beast) == 0) {
                        st.giveItems(q_marrow_of_behemoth_leader, 1);
                        htmltext = "corpse_behemoth_leader_q0456_01.htm";
                    }
                    if (st.ownItemCount(q_marrow_of_drake_lord) == 1 && st.ownItemCount(q_marrow_of_dragon_beast) == 1) {
                        st.setCond(2);
                        st.giveItems(q_marrow_of_behemoth_leader, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "corpse_behemoth_leader_q0456_01.htm";
                    }
                }
            }
        } else if (npcId == corpse_dragon_beast) {
            if (event.equalsIgnoreCase("menu_select?ask=456&reply=1")) {
                if (GetMemoState == 1 && npc != null && st.getInt("RaidKilled") == npc.getObjectId()) {
                    if (st.ownItemCount(q_marrow_of_drake_lord) == 0 || st.ownItemCount(q_marrow_of_behemoth_leader) == 0) {
                        st.giveItems(q_marrow_of_dragon_beast, 1);
                        htmltext = "corpse_dragon_beast_q0456_01.htm";
                    }
                    if (st.ownItemCount(q_marrow_of_drake_lord) == 1 && st.ownItemCount(q_marrow_of_behemoth_leader) == 1) {
                        st.setCond(2);
                        st.giveItems(q_marrow_of_dragon_beast, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "corpse_dragon_beast_q0456_01.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("the_spirit_with_no_name");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == separated_soul_01 || npcId == separated_soul_02 || npcId == separated_soul_03 || npcId == separated_soul_04 || npcId == separated_soul_05 || npcId == separated_soul_06 || npcId == separated_soul_07 || npcId == separated_soul_08) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "separated_soul_01_q0456_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.isNowAvailable()) {
                                htmltext = "separated_soul_01_q0456_01.htm";
                            } else {
                                htmltext = "separated_soul_01_q0456_02.htm";
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == separated_soul_01 || npcId == separated_soul_02 || npcId == separated_soul_03 || npcId == separated_soul_04 || npcId == separated_soul_05 || npcId == separated_soul_06 || npcId == separated_soul_07 || npcId == separated_soul_08) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_marrow_of_drake_lord) == 0 && st.ownItemCount(q_marrow_of_behemoth_leader) == 0 && st.ownItemCount(q_marrow_of_dragon_beast) == 0) {
                            htmltext = "separated_soul_01_q0456_08.htm";
                        } else if (st.ownItemCount(q_marrow_of_drake_lord) == 0 || st.ownItemCount(q_marrow_of_behemoth_leader) == 0 || st.ownItemCount(q_marrow_of_dragon_beast) == 0) {
                            htmltext = "separated_soul_01_q0456_09.htm";
                        } else if (st.ownItemCount(q_marrow_of_drake_lord) >= 1 && st.ownItemCount(q_marrow_of_behemoth_leader) >= 1 && st.ownItemCount(q_marrow_of_dragon_beast) >= 1) {
                            double i0 = Rnd.get(100000d * (1d / ServerConfig.RATE_QUESTS_REWARD));
                            if (i0 < 100) {
                                st.giveItems(sealed_verpes_helmet, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_helmet).getName()));
                            } else if (i0 < 200) {
                                st.giveItems(sealed_verpes_leather_helmet, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_leather_helmet).getName()));
                            } else if (i0 < 300) {
                                st.giveItems(sealed_verpes_circlet, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_circlet).getName()));
                            } else if (i0 < 400) {
                                st.giveItems(sealed_verpes_cuirass, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_cuirass).getName()));
                            } else if (i0 < 500) {
                                st.giveItems(sealed_verpes_houberk, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_houberk).getName()));
                            } else if (i0 < 600) {
                                st.giveItems(sealed_verpes_jaket, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_jaket).getName()));
                            } else if (i0 < 700) {
                                st.giveItems(sealed_verpes_gaiter, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_gaiter).getName()));
                            } else if (i0 < 800) {
                                st.giveItems(sealed_verpes_leather_legging, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_leather_legging).getName()));
                            } else if (i0 < 900) {
                                st.giveItems(sealed_verpes_hose, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_hose).getName()));
                            } else if (i0 < 1000) {
                                st.giveItems(sealed_verpes_gauntlet, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_gauntlet).getName()));
                            } else if (i0 < 1100) {
                                st.giveItems(sealed_verpes_leather_gloves, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_leather_gloves).getName()));
                            } else if (i0 < 1200) {
                                st.giveItems(sealed_verpes_gloves, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_gloves).getName()));
                            } else if (i0 < 1300) {
                                st.giveItems(sealed_verpes_boots, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_boots).getName()));
                            } else if (i0 < 1400) {
                                st.giveItems(sealed_verpes_leather_boots, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_leather_boots).getName()));
                            } else if (i0 < 1500) {
                                st.giveItems(sealed_verpes_shoes, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_shoes).getName()));
                            } else if (i0 < 1600) {
                                st.giveItems(sealed_verpes_shield, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_shield).getName()));
                            } else if (i0 < 1700) {
                                st.giveItems(sealed_verpes_sigil, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_sigil).getName()));
                            } else if (i0 < 1800) {
                                st.giveItems(sealed_verpes_ring, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_ring).getName()));
                            } else if (i0 < 1900) {
                                st.giveItems(sealed_verpes_earring, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_earring).getName()));
                            } else if (i0 < 2000) {
                                st.giveItems(sealed_verpes_necklace, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(sealed_verpes_necklace).getName()));
                            } else if (i0 < 2050) {
                                st.giveItems(peri_el_sword, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(peri_el_sword).getName()));
                            } else if (i0 < 2100) {
                                st.giveItems(skul_edge, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(skul_edge).getName()));
                            } else if (i0 < 2150) {
                                st.giveItems(vigwik_axe, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(vigwik_axe).getName()));
                            } else if (i0 < 2200) {
                                st.giveItems(evildeity_maul, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(evildeity_maul).getName()));
                            } else if (i0 < 2250) {
                                st.giveItems(feathereye_blade, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(feathereye_blade).getName()));
                            } else if (i0 < 2300) {
                                st.giveItems(octo_claw, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(octo_claw).getName()));
                            } else if (i0 < 2350) {
                                st.giveItems(doubletopa_spear, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(doubletopa_spear).getName()));
                            } else if (i0 < 2400) {
                                st.giveItems(cuticle, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(cuticle).getName()));
                            } else if (i0 < 2450) {
                                st.giveItems(blackvisage, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(blackvisage).getName()));
                            } else if (i0 < 2500) {
                                st.giveItems(veniplant_sword, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(veniplant_sword).getName()));
                            } else if (i0 < 2550) {
                                st.giveItems(skullcarnium_bow, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(skullcarnium_bow).getName()));
                            } else if (i0 < 2600) {
                                st.giveItems(gemtail_rapier, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(gemtail_rapier).getName()));
                            } else if (i0 < 2650) {
                                st.giveItems(fin_invincible_blade, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(fin_invincible_blade).getName()));
                            } else if (i0 < 2700) {
                                st.giveItems(rubecutter_crossbow, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(rubecutter_crossbow).getName()));
                            } else if (i0 < 3250) {
                                st.giveItems(blessed_scrl_of_ench_wp_s, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(blessed_scrl_of_ench_wp_s).getName()));
                            } else if (i0 < 4250) {
                                st.giveItems(blessed_scrl_of_ench_am_s, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(blessed_scrl_of_ench_am_s).getName()));
                            } else if (i0 < 9250) {
                                st.giveItems(high_ore_of_fire, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(high_ore_of_fire).getName()));
                            } else if (i0 < 14250) {
                                st.giveItems(high_ore_of_water, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(high_ore_of_water).getName()));
                            } else if (i0 < 19250) {
                                st.giveItems(high_ore_of_earth, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(high_ore_of_earth).getName()));
                            } else if (i0 < 24250) {
                                st.giveItems(high_ore_of_wind, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(high_ore_of_wind).getName()));
                            } else if (i0 < 29250) {
                                st.giveItems(high_ore_of_holy, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(high_ore_of_holy).getName()));
                            } else if (i0 < 31250) {
                                st.giveItems(high_ore_of_unholy, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(high_ore_of_unholy).getName()));
                            } else if (i0 < 33000) {
                                st.giveItems(scrl_of_ench_wp_s, 1);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(scrl_of_ench_wp_s).getName()));
                            } else if (i0 < 100000) {
                                st.giveItems(gemstone_s, 3);
                                npc.broadcastPacketToOthers(new ExShowScreenMessage(NpcString.S1_RECEIVED_A_S2_ITEM_AS_A_REWARD_FROM_THE_SEPARATED_SOUL, 6000, ScreenMessageAlign.BOTTOM_CENTER, st.getPlayer().getName(), ItemTemplateHolder.getInstance().getTemplate(gemstone_s).getName()));
                            }
                            st.takeItems(q_marrow_of_drake_lord, 1);
                            st.takeItems(q_marrow_of_behemoth_leader, 1);
                            st.takeItems(q_marrow_of_dragon_beast, 1);
                            st.exitQuest(this);
                            st.soundEffect(SOUND_FINISH);
                            htmltext = "separated_soul_01_q0456_10.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        String htmltext = NO_QUEST_DIALOG;
        final QuestState st = player.getQuestState(this);
        switch (npc.getNpcId()) {
            case corpse_drake_lord:
                if (st != null && st.getInt("RaidKilled") == npc.getObjectId()) {
                    if (st.ownItemCount(q_marrow_of_drake_lord) >= 1) {
                        htmltext = "corpse_drake_lord_q0456_03.htm";
                    }
                    if (st.ownItemCount(q_marrow_of_drake_lord) == 0) {
                        htmltext = "corpse_drake_lord001.htm";
                    }
                } else {
                    htmltext = "corpse_drake_lord_q0456_02.htm";
                }
                break;
            case corpse_behemoth_leader:
                if (st != null && st.getInt("RaidKilled") == npc.getObjectId()) {
                    if (st.ownItemCount(q_marrow_of_behemoth_leader) >= 1) {
                        htmltext = "corpse_behemoth_leader_q0456_03.htm";
                    }
                    if (st.ownItemCount(q_marrow_of_behemoth_leader) == 0) {
                        htmltext = "corpse_behemoth_leader001.htm";
                    }
                } else {
                    htmltext = "corpse_behemoth_leader_q0456_02.htm";
                }
                break;
            case corpse_dragon_beast:
                if (st != null && st.getInt("RaidKilled") == npc.getObjectId()) {
                    if (st.ownItemCount(q_marrow_of_dragon_beast) >= 1) {
                        htmltext = "corpse_dragon_beast_q0456_03.htm";
                    }
                    if (st.ownItemCount(q_marrow_of_dragon_beast) == 0) {
                        htmltext = "corpse_dragon_beast001.htm";
                    }
                } else {
                    htmltext = "corpse_dragon_beast_q0456_02.htm";
                }
                break;
        }
        return htmltext;
    }
}