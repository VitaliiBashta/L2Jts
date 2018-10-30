package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.QuestManager;
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
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 28/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _174_SupplyCheck extends Quest {
    // npc
    private final static int zerstorer_morsell = 32173;
    private final static int warehouse_keeper_benis = 32170;
    private final static int trader_neagel = 32167;
    private final static int trader_erinu = 32164;
    private final static int subelder_casca = 32139;
    // questitem
    private final static int q_warehouse_inventory_list = 9792;
    private final static int q_grocery_inventory_list = 9793;
    private final static int q_weaponshop_inventory_list = 9794;
    private final static int q_supplyment_report = 9795;
    // etcitem
    private final static int wooden_breastplate = 23;
    private final static int wooden_gaiters = 2386;
    private final static int wooden_helmet = 43;
    private final static int leather_shoes = 37;
    private final static int gloves = 49;

    public _174_SupplyCheck() {
        super(false);
        addStartNpc(zerstorer_morsell);
        addTalkId(warehouse_keeper_benis, trader_neagel, trader_erinu, subelder_casca);
        addQuestItem(q_warehouse_inventory_list, q_grocery_inventory_list);
        addLevelCheck(2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == zerstorer_morsell) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("check_the_supplyment", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "zerstorer_morsell_q0174_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        int GetMemoState = st.getInt("check_the_supplyment");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == zerstorer_morsell) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            st.exitQuest(true);
                            htmltext = "zerstorer_morsell_q0174_02.htm";
                            break;
                        default:
                            htmltext = "zerstorer_morsell_q0174_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == zerstorer_morsell) {
                    if (GetMemoState == 1)
                        htmltext = "zerstorer_morsell_q0174_05.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_warehouse_inventory_list) >= 1) {
                        st.setCond(3);
                        st.setMemoState("check_the_supplyment", String.valueOf(3), true);
                        st.takeItems(q_warehouse_inventory_list, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "zerstorer_morsell_q0174_06.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "zerstorer_morsell_q0174_07.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_grocery_inventory_list) >= 1) {
                        st.setCond(5);
                        st.setMemoState("check_the_supplyment", String.valueOf(5), true);
                        st.takeItems(q_grocery_inventory_list, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "zerstorer_morsell_q0174_08.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "zerstorer_morsell_q0174_09.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_weaponshop_inventory_list) >= 1) {
                        st.setCond(7);
                        st.setMemoState("check_the_supplyment", String.valueOf(7), true);
                        st.giveItems(q_supplyment_report, 1);
                        st.takeItems(q_weaponshop_inventory_list, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "zerstorer_morsell_q0174_10.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "zerstorer_morsell_q0174_11.htm";
                    else if (GetMemoState == 8) {
                        st.takeItems(q_grocery_inventory_list, -1);
                        st.giveItems(wooden_breastplate, 1);
                        st.giveItems(wooden_helmet, 1);
                        st.giveItems(gloves, 1);
                        st.giveItems(wooden_gaiters, 1);
                        st.giveItems(leather_shoes, 1);
                        st.removeMemo("check_the_supplyment");
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.addExpAndSp(5672, 446);
                        st.giveItems(ADENA_ID, 446);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "zerstorer_morsell_q0174_12.htm";
                    }
                } else if (npcId == warehouse_keeper_benis) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("check_the_supplyment", String.valueOf(2), true);
                        st.giveItems(q_warehouse_inventory_list, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "warehouse_keeper_benis_q0174_01.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "warehouse_keeper_benis_q0174_02.htm";
                    else if (GetMemoState > 2)
                        htmltext = "warehouse_keeper_benis_q0174_03.htm";
                } else if (npcId == trader_neagel) {
                    if (GetMemoState < 3)
                        htmltext = "trader_neagel_q0174_01.htm";
                    else if (GetMemoState == 3) {
                        st.setCond(4);
                        st.setMemoState("check_the_supplyment", String.valueOf(8), true);
                        st.giveItems(q_grocery_inventory_list, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "trader_neagel_q0174_02.htm";
                    } else if (GetMemoState == 8)
                        htmltext = "trader_neagel_q0174_03.htm";
                    else if (GetMemoState > 8)
                        htmltext = "trader_neagel_q0174_04.htm";
                } else if (npcId == trader_erinu) {
                    if (GetMemoState < 5)
                        htmltext = "trader_erinu_q0174_01.htm";
                    else if (GetMemoState == 5) {
                        st.setCond(6);
                        st.setMemoState("check_the_supplyment", String.valueOf(6), true);
                        st.giveItems(q_weaponshop_inventory_list, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "trader_erinu_q0174_02.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "trader_erinu_q0174_03.htm";
                    else if (GetMemoState > 6)
                        htmltext = "trader_erinu_q0174_04.htm";
                } else if (npcId == subelder_casca) {
                    if (GetMemoState < 7)
                        htmltext = "subelder_casca_q0174_01.htm";
                    else if (GetMemoState == 7) {
                        st.setCond(8);
                        st.setMemoState("check_the_supplyment", String.valueOf(8), true);
                        st.takeItems(q_supplyment_report, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "subelder_casca_q0174_02.htm";
                    } else if (GetMemoState == 8)
                        htmltext = "subelder_casca_q0174_03.htm";
                }
                break;
        }
        return htmltext;
    }
}