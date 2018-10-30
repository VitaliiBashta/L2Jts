package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
public class _004_LongLivethePaagrioLord extends Quest {
    // npc
    private final static int centurion_nakusin = 30578;
    private final static int trader_kunai = 30559;
    private final static int trader_uska = 30560;
    private final static int warehouse_grookin = 30562;
    private final static int atuba_chief_varkees = 30566;
    private final static int tataru_zu_hestui = 30585;
    private final static int gantaki_zu_urutu = 30587;
    // questitem
    private final static int honey_khandar = 1541;
    private final static int bear_fur_cloak = 1542;
    private final static int bloody_axe = 1543;
    private final static int ancestor_skull = 1544;
    private final static int spider_dust = 1545;
    private final static int deep_sea_orb = 1546;
    // weapon
    private final static int club = 4;

    public _004_LongLivethePaagrioLord() {
        super(false);
        addStartNpc(centurion_nakusin);
        addTalkId(trader_kunai, trader_uska, warehouse_grookin, atuba_chief_varkees, tataru_zu_hestui, gantaki_zu_urutu);
        addQuestItem(honey_khandar, bear_fur_cloak, bloody_axe, ancestor_skull, spider_dust, deep_sea_orb);
        addLevelCheck(2, 5);
        addRaceCheck(PlayerRace.orc);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int npcId = npc.getNpcId();
        if (npcId == centurion_nakusin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "centurion_nakusin_q0004_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final Player player = st.getPlayer();
        if (player == null) {
            return null;
        }
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == centurion_nakusin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "centurion_nakusin_q0004_01.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "centurion_nakusin_q0004_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "centurion_nakusin_q0004_02.htm";
                            break;
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == centurion_nakusin) {
                    if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) < 6) {
                        htmltext = "centurion_nakusin_q0004_04.htm";
                    } else if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 6) {
                        htmltext = "centurion_nakusin_q0004_06.htm";
                        st.giveItems(club, 1);
                        st.addExpAndSp(4254, 335);
                        st.giveItems(ADENA_ID, 1850);
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
                    }
                } else if (npcId == atuba_chief_varkees) {
                    if (st.ownItemCount(honey_khandar) < 1) {
                        htmltext = "atuba_chief_varkees_q0004_01.htm";
                        st.giveItems(honey_khandar, 1);
                        if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 5) {
                            st.setCond(2);
                        }
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(honey_khandar) >= 1) {
                        htmltext = "atuba_chief_varkees_q0004_02.htm";
                    }
                } else if (npcId == tataru_zu_hestui) {
                    if (st.ownItemCount(bear_fur_cloak) < 1) {
                        htmltext = "tataru_zu_hestui_q0004_01.htm";
                        st.giveItems(bear_fur_cloak, 1);
                        if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 5) {
                            st.setCond(2);
                        }
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(bear_fur_cloak) >= 1) {
                        htmltext = "tataru_zu_hestui_q0004_02.htm";
                    }
                } else if (npcId == trader_uska) {
                    if (st.ownItemCount(ancestor_skull) < 1) {
                        htmltext = "trader_uska_q0004_01.htm";
                        st.giveItems(ancestor_skull, 1);
                        if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 5) {
                            st.setCond(2);
                        }
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(ancestor_skull) >= 1) {
                        htmltext = "trader_uska_q0004_02.htm";
                    }
                } else if (npcId == warehouse_grookin) {
                    if (st.ownItemCount(bloody_axe) < 1) {
                        htmltext = "warehouse_grookin_q0004_01.htm";
                        st.giveItems(bloody_axe, 1);
                        if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 5) {
                            st.setCond(2);
                        }
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(bloody_axe) >= 1) {
                        htmltext = "warehouse_grookin_q0004_02.htm";
                    }
                } else if (npcId == gantaki_zu_urutu) {
                    if (st.ownItemCount(deep_sea_orb) < 1) {
                        htmltext = "gantaki_zu_urutu_q0004_01.htm";
                        st.giveItems(deep_sea_orb, 1);
                        if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 5) {
                            st.setCond(2);
                        }
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(deep_sea_orb) >= 1) {
                        htmltext = "gantaki_zu_urutu_q0004_02.htm";
                    }
                } else if (npcId == trader_kunai) {
                    if (st.ownItemCount(spider_dust) < 1) {
                        htmltext = "trader_kunai_q0004_01.htm";
                        st.giveItems(spider_dust, 1);
                        if (st.ownItemCount(honey_khandar) + st.ownItemCount(bear_fur_cloak) + st.ownItemCount(bloody_axe) + st.ownItemCount(ancestor_skull) + st.ownItemCount(spider_dust) + st.ownItemCount(deep_sea_orb) >= 5) {
                            st.setCond(2);
                        }
                        st.soundEffect(SOUND_ITEMGET);
                    } else if (st.ownItemCount(spider_dust) >= 1) {
                        htmltext = "trader_kunai_q0004_02.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}