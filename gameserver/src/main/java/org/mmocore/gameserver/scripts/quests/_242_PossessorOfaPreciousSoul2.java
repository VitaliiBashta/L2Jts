package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 01/06/2016
 * @lastedit 01/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _242_PossessorOfaPreciousSoul2 extends Quest {
    //npc
    private static final int alchemist_matild = 30738;
    private static final int dead_angel = 31752;
    private static final int fallen_unicorn = 31746;
    private static final int holding_cornerstone = 31748;
    private static final int kasandra = 31743;
    private static final int mysterious_knight = 31751;
    private static final int ogmar = 31744;
    private static final int virgil = 31742;
    private static final int white_unicorn = 31747;
    private static final int witch_kalis = 30759;
    //mobs
    private static final int radiant_constrainer = 27317;
    //questitem
    private static final int q_nobelesse_mark_1 = 7595;
    private static final int q_nobelesse_mark_2 = 7596;
    private static final int q_golden_lock = 7590;
    //etcitem
    private static final int q_virgils_letter1 = 7677;
    private static final int q_caradines_letter1 = 7678;

    public _242_PossessorOfaPreciousSoul2() {
        super(false);
        addStartNpc(virgil);
        addTalkId(kasandra, ogmar, mysterious_knight, dead_angel, witch_kalis, alchemist_matild, fallen_unicorn, holding_cornerstone, white_unicorn);
        addKillId(radiant_constrainer);
        addQuestItem(q_nobelesse_mark_1, q_nobelesse_mark_2, q_golden_lock);
        addLevelCheck(1, 85);
        addQuestCompletedCheck(241);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("noble_soul_noblesse_2");
        int GetMemoStateEx = st.getInt("noble_soul_noblesse_2_ex");
        int npcId = npc.getNpcId();
        if (npcId == virgil) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(1), true);
                st.setMemoState("noble_soul_noblesse_2_ex", String.valueOf(0), true);
                st.takeItems(q_virgils_letter1, -1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "virgil_q0242_03.htm";
            }
        } else if (npcId == kasandra) {
            if (event.equalsIgnoreCase("menu_select?ask=242&reply=1"))
                htmltext = "kasandra_q0242_02.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=242&reply=2"))
                htmltext = "kasandra_q0242_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=242&reply=3"))
                htmltext = "kasandra_q0242_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=242&reply=4")) {
                st.setCond(2);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "kasandra_q0242_05.htm";
            }
        } else if (npcId == ogmar) {
            if (event.equalsIgnoreCase("menu_select?ask=242&reply=1") && GetMemoState == 2) {
                st.setCond(3);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ogmar_q0242_02.htm";
            }
        } else if (npcId == mysterious_knight) {
            if (event.equalsIgnoreCase("menu_select?ask=242&reply=1") && GetMemoState >= 3) {
                st.setCond(4);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(4), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "mysterious_knight_q0242_02.htm";
            }
        } else if (npcId == witch_kalis) {
            if (event.equalsIgnoreCase("menu_select?ask=242&reply=1") && GetMemoState == 6) {
                st.setCond(7);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(7), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "witch_kalis_q0242_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=242&reply=2") && GetMemoState == 8) {
                st.setCond(9);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(9), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "witch_kalis_q0242_05.htm";
            }
        } else if (npcId == alchemist_matild) {
            if (event.equalsIgnoreCase("menu_select?ask=242&reply=1") && GetMemoState == 7) {
                st.setCond(8);
                st.setMemoState("noble_soul_noblesse_2", String.valueOf(8), true);
                st.giveItems(q_nobelesse_mark_2, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "alchemist_matild_q0242_02.htm";
            }
        } else if (npcId == fallen_unicorn) {
            if (event.equalsIgnoreCase("24201")) {
                if (npc != null) {
                    npc.doDie(npc);
                    Location loc = Location.findPointToStay(npc.getX(), npc.getY(), npc.getZ(), 50, 150, npc.getGeoIndex());
                    st.addSpawn(white_unicorn, loc.getX(), loc.getY(), loc.getZ(), 300000);
                }
                return null;
            }
        } else if (npcId == holding_cornerstone) {
            if (event.equalsIgnoreCase("menu_select?ask=242&reply=1") && GetMemoState == 9) {
                if (GetMemoStateEx >= 3) {
                    st.setCond(10);
                    st.setMemoState("noble_soul_noblesse_2", String.valueOf(10), true);
                    st.takeItems(q_nobelesse_mark_1, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "holding_cornerstone_q0242_03.htm";
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4546, 1), st.getPlayer(), true);
                    if (npc != null)
                        npc.doDie(npc);
                } else {
                    st.setMemoState("noble_soul_noblesse_2_ex", String.valueOf(GetMemoStateEx + 1), true);
                    st.takeItems(q_nobelesse_mark_1, 1);
                    htmltext = "holding_cornerstone_q0242_03.htm";
                    npc.doCast(SkillTable.getInstance().getSkillEntry(4546, 1), st.getPlayer(), true);
                    if (npc != null)
                        npc.doDie(npc);
                }
            }
        } else if (npcId == white_unicorn) {
            if (event.equalsIgnoreCase("24202")) {
                if (npc != null) {
                    npc.doDie(npc);
                }
                return null;
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!st.getPlayer().getPlayerClassComponent().isSubClassActive()) {
            return "quest_not_subclass001.htm";
        }
        String htmltext = NO_QUEST_DIALOG;
        Player player = st.getPlayer();
        int GetMemoState = st.getInt("noble_soul_noblesse_2");
        int GetMemoStateEx = st.getInt("noble_soul_noblesse_2_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == virgil) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "virgil_q0242_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "virgil_q0242_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == virgil) {
                    if (GetMemoState == 1)
                        htmltext = "virgil_q0242_04.htm";
                    else if (GetMemoState > 1 && GetMemoState < 11)
                        htmltext = "virgil_q0242_05.htm";
                    else if (GetMemoState >= 11)
                        htmltext = "virgil_q0242_06.htm";
                } else if (npcId == kasandra) {
                    if (GetMemoState == 1)
                        htmltext = "kasandra_q0242_01.htm";
                    else if (GetMemoState >= 2 && GetMemoState < 11)
                        htmltext = "kasandra_q0242_06.htm";
                    else if (GetMemoState >= 11) {
                        st.giveItems(q_caradines_letter1, 1);
                        st.addExpAndSp(455764, 0);
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "kasandra_q0242_07.htm";
                        player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    }
                } else if (npcId == ogmar) {
                    if (GetMemoState == 2)
                        htmltext = "ogmar_q0242_01.htm";
                    else if (GetMemoState >= 3)
                        htmltext = "ogmar_q0242_03.htm";
                } else if (npcId == mysterious_knight) {
                    if (GetMemoState == 3)
                        htmltext = "mysterious_knight_q0242_01.htm";
                    else if (GetMemoState == 4)
                        htmltext = "mysterious_knight_q0242_03.htm";
                    else if (GetMemoState == 5) {
                        st.setCond(6);
                        st.setMemoState("noble_soul_noblesse_2", String.valueOf(6), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "mysterious_knight_q0242_04.htm";
                    } else if (GetMemoState == 6)
                        htmltext = "mysterious_knight_q0242_05.htm";
                } else if (npcId == dead_angel) {
                    if (GetMemoState == 4 && GetMemoStateEx < 3) {

                        st.setMemoState("noble_soul_noblesse_2_ex", String.valueOf(GetMemoStateEx + 1), true);
                        htmltext = "dead_angel_q0242_01.htm";
                        npc.doDie(npc);
                    } else {
                        st.setCond(5);
                        st.setMemoState("noble_soul_noblesse_2", String.valueOf(5), true);
                        st.setMemoState("noble_soul_noblesse_2_ex", String.valueOf(0), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "dead_angel_q0242_02.htm";
                    }
                } else if (npcId == witch_kalis) {
                    if (GetMemoState == 6)
                        htmltext = "witch_kalis_q0242_01.htm";
                    else if (GetMemoState == 7)
                        htmltext = "witch_kalis_q0242_03.htm";
                    else if (GetMemoState == 8) {
                        st.takeItems(q_nobelesse_mark_2, -1);
                        htmltext = "witch_kalis_q0242_04.htm";
                    } else if (GetMemoState >= 9)
                        htmltext = "witch_kalis_q0242_06.htm";
                } else if (npcId == alchemist_matild) {
                    if (GetMemoState == 7)
                        htmltext = "alchemist_matild_q0242_01.htm";
                    else if (GetMemoState == 8)
                        htmltext = "alchemist_matild_q0242_03.htm";
                } else if (npcId == fallen_unicorn) {
                    if (GetMemoState < 10)
                        htmltext = "fallen_unicorn_q0242_01.htm";
                    else if (GetMemoState >= 10) {
                        htmltext = "fallen_unicorn_q0242_02.htm";
                        st.startQuestTimer("24201", 1000 * 3, npc);
                    }
                } else if (npcId == holding_cornerstone) {
                    if (st.ownItemCount(q_nobelesse_mark_1) < 1 && GetMemoState == 9)
                        htmltext = "holding_cornerstone_q0242_01.htm";
                    else if (st.ownItemCount(q_nobelesse_mark_1) >= 1 && GetMemoState == 9)
                        htmltext = "holding_cornerstone_q0242_02.htm";
                } else if (npcId == white_unicorn) {
                    if (GetMemoState == 10) {
                        st.setCond(11);
                        st.setMemoState("noble_soul_noblesse_2", String.valueOf(11), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "white_unicorn_q0242_01.htm";
                        st.startQuestTimer("24202", 1000 * 3, npc);
                    } else if (GetMemoState == 11)
                        htmltext = "white_unicorn_q0242_02.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (!st.getPlayer().getPlayerClassComponent().isSubClassActive()) {
            return null;
        }
        int GetMemoState = st.getInt("noble_soul_noblesse_2");
        int npcId = npc.getNpcId();
        if (npcId == radiant_constrainer) {
            if (GetMemoState == 9 && st.ownItemCount(q_nobelesse_mark_1) < 4) {
                st.giveItems(q_nobelesse_mark_1, 1);
                if (st.ownItemCount(q_nobelesse_mark_1) >= 4) {
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}