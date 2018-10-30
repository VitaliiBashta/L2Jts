package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 06/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _108_JumbleTumbleDiamondFuss extends Quest {
    // npc
    private static final int collector_gouph = 30523;
    private static final int trader_reep = 30516;
    private static final int carrier_torocco = 30555;
    private static final int miner_maron = 30529;
    private static final int blacksmith_bronp = 30526;
    private static final int warehouse_murphrin = 30521;
    private static final int warehouse_airy = 30522;
    // mobs
    private static final int goblin_brigand_leader = 20323;
    private static final int goblin_brigand_sub_ldr = 20324;
    private static final int blade_bat = 20480;
    // questitem
    private static final int gouphs_contract = 1559;
    private static final int reeps_contract = 1560;
    private static final int elven_wine = 1561;
    private static final int bronps_dice = 1562;
    private static final int bronps_contract = 1563;
    private static final int aquamarine = 1564;
    private static final int chrysoberyl = 1565;
    private static final int gem_box1 = 1566;
    private static final int coal_piece = 1567;
    private static final int bronps_letter = 1568;
    private static final int berry_tart = 1569;
    private static final int bat_diagram = 1570;
    private static final int star_diamond = 1571;
    // etcitem
    private static final int soulshot_none_for_rookie = 5789;
    private static final int lesser_healing_potion = 1060;
    private static final int echo_crystal_solitude = 4414;
    private static final int echo_crystal_feast = 4415;
    private static final int echo_crystal_celebration = 4416;
    private static final int echo_crystal_love = 4413;
    private static final int echo_crystal_battle = 4412;
    private static final int silversmith_hammer = 1511;

    public _108_JumbleTumbleDiamondFuss() {
        super(false);
        addStartNpc(collector_gouph);
        addTalkId(trader_reep, carrier_torocco, miner_maron, blacksmith_bronp, warehouse_murphrin, warehouse_airy);
        addKillId(goblin_brigand_leader, goblin_brigand_sub_ldr, blade_bat);
        addQuestItem(gem_box1, star_diamond, gouphs_contract, reeps_contract, elven_wine, bronps_contract, aquamarine, chrysoberyl, coal_piece, bronps_dice, bronps_letter, berry_tart, bat_diagram);
        addLevelCheck(10, 14);
        addRaceCheck(PlayerRace.dwarf);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == collector_gouph) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(gouphs_contract, 1);
                htmltext = "collector_gouph_q0108_03.htm";
            }
        } else if (npcId == carrier_torocco) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(reeps_contract) > 0) {
                htmltext = "carrier_torocco_q0108_02.htm";
                st.takeItems(reeps_contract, 1);
                st.giveItems(elven_wine, 1);
                st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == blacksmith_bronp) {
            if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(bronps_dice) > 0) {
                htmltext = "blacksmith_bronp_q0108_02.htm";
                st.takeItems(bronps_dice, 1);
                st.giveItems(bronps_contract, 1);
                st.setCond(5);
                st.soundEffect(SOUND_MIDDLE);
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
        int talker_level = st.getPlayer().getLevel();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == collector_gouph) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "collector_gouph_q0108_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "collector_gouph_q0108_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "collector_gouph_q0108_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == collector_gouph) {
                    if (st.ownItemCount(gouphs_contract) > 0)
                        htmltext = "collector_gouph_q0108_04.htm";
                    else if (st.ownItemCount(reeps_contract) > 0 || st.ownItemCount(elven_wine) > 0 || st.ownItemCount(bronps_dice) > 0 || st.ownItemCount(bronps_contract) > 0)
                        htmltext = "collector_gouph_q0108_05.htm";
                    else if (st.ownItemCount(gem_box1) > 0) {
                        htmltext = "collector_gouph_q0108_06.htm";
                        st.setCond(8);
                        st.soundEffect(SOUND_MIDDLE);
                        st.takeItems(gem_box1, 1);
                        st.giveItems(coal_piece, 1);
                    } else if (st.ownItemCount(bronps_letter) > 0 || st.ownItemCount(coal_piece) > 0 || st.ownItemCount(berry_tart) > 0 || st.ownItemCount(bat_diagram) > 0)
                        htmltext = "collector_gouph_q0108_07.htm";
                    else if (st.ownItemCount(star_diamond) > 0) {
                        if (talker_level < 25) {
                            st.giveItems(soulshot_none_for_rookie, 7000);
                            st.playTutorialVoice("tutorial_voice_026");
                        }
                        if (player.getQuestState(41) == null) {
                            final Quest q = QuestManager.getQuest(41);
                            q.newQuestState(player, Quest.STARTED);
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        } else if (player.getQuestState(41).getInt("guide_mission") % 1000000 / 100000 != 1) {
                            player.getQuestState(41).setMemoState("guide_mission", String.valueOf(player.getQuestState(41).getInt("guide_mission") + 100000), true);
                            st.getPlayer().sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_RACE_SPECIFIC_WEAPON_COMPLETE_GO_FIND_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                        }
                        st.addExpAndSp(34565, 2962);
                        st.giveItems(ADENA_ID, 14666);
                        st.giveItems(lesser_healing_potion, 100);
                        st.giveItems(echo_crystal_solitude, 10);
                        st.giveItems(echo_crystal_feast, 10);
                        st.giveItems(echo_crystal_celebration, 10);
                        st.giveItems(echo_crystal_love, 10);
                        st.giveItems(echo_crystal_battle, 10);
                        htmltext = "collector_gouph_q0108_08.htm";
                        st.giveItems(silversmith_hammer, 1);
                        st.takeItems(star_diamond, 1);
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                    }
                } else if (npcId == trader_reep) {
                    if (st.ownItemCount(gouphs_contract) > 0 && st.ownItemCount(reeps_contract) == 0) {
                        htmltext = "trader_reep_q0108_01.htm";
                        st.giveItems(reeps_contract, 1);
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                        st.takeItems(gouphs_contract, 1);
                    } else if (st.ownItemCount(gouphs_contract) == 0 && st.ownItemCount(reeps_contract) > 0)
                        htmltext = "trader_reep_q0108_02.htm";
                    else if (st.ownItemCount(gouphs_contract) == 0 && st.ownItemCount(reeps_contract) == 0)
                        htmltext = "trader_reep_q0108_03.htm";
                } else if (npcId == carrier_torocco) {
                    if (st.ownItemCount(reeps_contract) > 0 && st.ownItemCount(elven_wine) == 0)
                        htmltext = "carrier_torocco_q0108_01.htm";
                    else if (st.ownItemCount(reeps_contract) == 0 && st.ownItemCount(elven_wine) > 0)
                        htmltext = "carrier_torocco_q0108_03.htm";
                    else if (st.ownItemCount(gem_box1) == 1)
                        htmltext = "carrier_torocco_q0108_04.htm";
                    else if (st.ownItemCount(gem_box1) == 0 && st.ownItemCount(reeps_contract) == 0 && st.ownItemCount(elven_wine) == 0)
                        htmltext = "carrier_torocco_q0108_05.htm";
                } else if (npcId == miner_maron) {
                    if (st.ownItemCount(elven_wine) > 0 && st.ownItemCount(bronps_dice) == 0) {
                        htmltext = "miner_maron_q0108_01.htm";
                        st.giveItems(bronps_dice, 1);
                        st.takeItems(elven_wine, 1);
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(elven_wine) == 0 && st.ownItemCount(bronps_dice) > 0)
                        htmltext = "miner_maron_q0108_02.htm";
                    else if (st.ownItemCount(elven_wine) == 0 && st.ownItemCount(bronps_dice) == 0)
                        htmltext = "miner_maron_q0108_03.htm";
                } else if (npcId == blacksmith_bronp) {
                    if (st.ownItemCount(bronps_dice) > 0)
                        htmltext = "blacksmith_bronp_q0108_01.htm";
                    else if (st.ownItemCount(bronps_contract) > 0 && st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) < 20)
                        htmltext = "blacksmith_bronp_q0108_03.htm";
                    else if (st.ownItemCount(bronps_contract) > 0 && st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) >= 20) {
                        htmltext = "blacksmith_bronp_q0108_04.htm";
                        st.takeItems(bronps_contract, 1);
                        st.takeItems(aquamarine, -1);
                        st.takeItems(chrysoberyl, -1);
                        st.giveItems(gem_box1, 1);
                        st.setCond(7);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(gem_box1) > 0)
                        htmltext = "blacksmith_bronp_q0108_05.htm";
                    else if (st.ownItemCount(coal_piece) > 0) {
                        htmltext = "blacksmith_bronp_q0108_06.htm";
                        st.takeItems(coal_piece, 1);
                        st.giveItems(bronps_letter, 1);
                        st.setCond(9);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(bronps_letter) > 0)
                        htmltext = "blacksmith_bronp_q0108_07.htm";
                    else if (st.ownItemCount(berry_tart) > 0 || st.ownItemCount(bat_diagram) > 0 || st.ownItemCount(star_diamond) > 0)
                        htmltext = "blacksmith_bronp_q0108_08.htm";
                } else if (npcId == warehouse_murphrin) {
                    if (st.ownItemCount(bronps_letter) > 0 && st.ownItemCount(berry_tart) == 0) {
                        htmltext = "warehouse_murphrin_q0108_01.htm";
                        st.giveItems(berry_tart, 1);
                        st.takeItems(bronps_letter, 1);
                        st.setCond(10);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(bronps_letter) == 0 && st.ownItemCount(berry_tart) > 0)
                        htmltext = "warehouse_murphrin_q0108_02.htm";
                    else if (st.ownItemCount(bronps_letter) == 0 && st.ownItemCount(berry_tart) == 0)
                        htmltext = "warehouse_murphrin_q0108_03.htm";
                } else if (npcId == warehouse_airy) {
                    if (st.ownItemCount(bat_diagram) == 0 && st.ownItemCount(berry_tart) > 0 && st.ownItemCount(star_diamond) == 0) {
                        htmltext = "warehouse_airy_q0108_01.htm";
                        st.giveItems(bat_diagram, 1);
                        st.takeItems(berry_tart, 1);
                        st.setCond(11);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (st.ownItemCount(bat_diagram) > 0 && st.ownItemCount(berry_tart) == 0 && st.ownItemCount(star_diamond) == 0)
                        htmltext = "warehouse_airy_q0108_02.htm";
                    else if (st.ownItemCount(bat_diagram) == 0 && st.ownItemCount(berry_tart) == 0 && st.ownItemCount(star_diamond) > 0)
                        htmltext = "warehouse_airy_q0108_03.htm";
                    else if (st.ownItemCount(bat_diagram) == 0 && st.ownItemCount(berry_tart) == 0 && st.ownItemCount(star_diamond) == 0)
                        htmltext = "warehouse_airy_q0108_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == goblin_brigand_leader) {
            if (st.ownItemCount(bronps_contract) > 0) {
                if (Rnd.get(10) < 8) {
                    if (st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) >= 19) {
                        if (st.ownItemCount(aquamarine) < 10) {
                            st.giveItems(aquamarine, 1);
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(aquamarine) < 10) {
                        st.giveItems(aquamarine, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                if (Rnd.get(10) < 8) {
                    if (st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) >= 19) {
                        if (st.ownItemCount(chrysoberyl) < 10) {
                            st.giveItems(chrysoberyl, 1);
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) < 20) {
                        if (st.ownItemCount(chrysoberyl) < 10) {
                            st.giveItems(chrysoberyl, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    }
                }
            }
        } else if (npcId == goblin_brigand_sub_ldr) {
            if (st.ownItemCount(bronps_contract) > 0) {
                if (Rnd.get(10) < 6) {
                    if (st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) >= 19) {
                        if (st.ownItemCount(aquamarine) < 10) {
                            st.giveItems(aquamarine, 1);
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(aquamarine) < 10) {
                        st.giveItems(aquamarine, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
                if (Rnd.get(10) < 6) {
                    if (st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) >= 19) {
                        if (st.ownItemCount(chrysoberyl) < 10) {
                            st.giveItems(chrysoberyl, 1);
                            st.setCond(6);
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else if (st.ownItemCount(aquamarine) + st.ownItemCount(chrysoberyl) < 20) {
                        if (st.ownItemCount(chrysoberyl) < 10) {
                            st.giveItems(chrysoberyl, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
                    }
                }
            }
        } else if (npcId == blade_bat) {
            if (st.ownItemCount(bat_diagram) > 0 && st.ownItemCount(star_diamond) == 0) {
                if (Rnd.get(10) < 2) {
                    st.giveItems(star_diamond, 1);
                    st.takeItems(bat_diagram, 1);
                    st.setCond(12);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}