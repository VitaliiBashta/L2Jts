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
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _005_MinersFavor extends Quest {
    // npc
    private final static int miner_bolter = 30554;
    private final static int trader_garita = 30518;
    private final static int trader_chali = 30517;
    private final static int warehouse_chief_reed = 30520;
    private final static int blacksmith_bronp = 30526;
    // questitem
    private final static int bolters_list = 1547;
    private final static int mining_boots = 1548;
    private final static int miners_pick = 1549;
    private final static int boomboom_powder = 1550;
    private final static int redstone_beer = 1551;
    private final static int bolters_smelly_socks = 1552;
    // accessary
    private final static int necklace_of_knowledge = 906;

    public _005_MinersFavor() {
        super(false);
        addStartNpc(miner_bolter);
        addTalkId(trader_garita, trader_chali, warehouse_chief_reed, blacksmith_bronp);
        addQuestItem(bolters_list, mining_boots, miners_pick, boomboom_powder, redstone_beer, bolters_smelly_socks);
        addLevelCheck(2, 5);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int npcId = npc.getNpcId();
        if (npcId == miner_bolter) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.giveItems(bolters_list, 1);
                st.giveItems(bolters_smelly_socks, 1);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "miner_bolter_q0005_03.htm";
            }
        } else if (npcId == blacksmith_bronp) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (st.ownItemCount(bolters_smelly_socks) > 0) {
                    htmltext = "blacksmith_bronp_q0005_02.htm";
                    st.takeItems(bolters_smelly_socks, 1);
                    st.giveItems(miners_pick, 1);
                    if (st.ownItemCount(mining_boots) + st.ownItemCount(miners_pick) + st.ownItemCount(boomboom_powder) + st.ownItemCount(redstone_beer) >= 3) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                } else {
                    htmltext = "blacksmith_bronp_q0005_04.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        final int npcId = npc.getNpcId();
        final int id = st.getState();

        switch (id) {
            case CREATED: {
                if (npcId == miner_bolter) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "miner_bolter_q0005_01.htm";
                            break;
                        }
                        default: {
                            htmltext = "miner_bolter_q0005_02.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == miner_bolter) {
                    if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(mining_boots) + st.ownItemCount(miners_pick) + st.ownItemCount(boomboom_powder) + st.ownItemCount(redstone_beer) < 4) {
                        htmltext = "miner_bolter_q0005_04.htm";
                    } else if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(mining_boots) + st.ownItemCount(miners_pick) + st.ownItemCount(boomboom_powder) + st.ownItemCount(redstone_beer) >= 4) {
                        st.giveItems(necklace_of_knowledge, 1);
                        st.addExpAndSp(5672, 446);
                        st.giveItems(ADENA_ID, 2466);
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 10 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 1), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.DELIVERY_DUTY_COMPLETE_GO_FING_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "miner_bolter_q0005_06.htm";
                    }
                } else if (npcId == trader_garita) {
                    if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(mining_boots) == 0) {
                        htmltext = "trader_garita_q0005_01.htm";
                        st.giveItems(mining_boots, 1);
                        if (st.ownItemCount(mining_boots) + st.ownItemCount(miners_pick) + st.ownItemCount(boomboom_powder) + st.ownItemCount(redstone_beer) >= 3) {
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(mining_boots) > 0) {
                        htmltext = "trader_garita_q0005_02.htm";
                    }
                } else if (npcId == trader_chali) {
                    if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(boomboom_powder) == 0) {
                        htmltext = "trader_chali_q0005_01.htm";
                        st.giveItems(boomboom_powder, 1);
                        if (st.ownItemCount(mining_boots) + st.ownItemCount(miners_pick) + st.ownItemCount(boomboom_powder) + st.ownItemCount(redstone_beer) >= 3) {
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(boomboom_powder) > 0) {
                        htmltext = "trader_chali_q0005_02.htm";
                    }
                } else if (npcId == warehouse_chief_reed) {
                    if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(redstone_beer) == 0) {
                        htmltext = "warehouse_chief_reed_q0005_01.htm";
                        st.giveItems(redstone_beer, 1);
                        if (st.ownItemCount(mining_boots) + st.ownItemCount(miners_pick) + st.ownItemCount(boomboom_powder) + st.ownItemCount(redstone_beer) >= 3) {
                            st.setCond(2);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(redstone_beer) > 0) {
                        htmltext = "warehouse_chief_reed_q0005_02.htm";
                    }
                } else if (npcId == blacksmith_bronp) {
                    if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(miners_pick) == 0) {
                        htmltext = "blacksmith_bronp_q0005_01.htm";
                    } else if (st.ownItemCount(bolters_list) > 0 && st.ownItemCount(miners_pick) > 0) {
                        htmltext = "blacksmith_bronp_q0005_03.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}
