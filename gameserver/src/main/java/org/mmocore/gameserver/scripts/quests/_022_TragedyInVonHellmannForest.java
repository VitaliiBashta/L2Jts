package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _022_TragedyInVonHellmannForest extends Quest {
    // npc
    private final static int umul = 31527;
    private final static int grandmagister_tifaren = 31334;
    private final static int highpriest_innocentin = 31328;
    private final static int rune_ghost2 = 31528;
    private final static int rune_ghost3 = 31529;
    // quest mob
    private final static int ghost_of_umul = 27217;
    // questitem
    private final static int q_cross_of_einhasad2 = 7141;
    private final static int q_lost_elfs_skull = 7142;
    private final static int q_letter_of_innocentin = 7143;
    private final static int q_calling_treasure1 = 7144;
    private final static int q_calling_treasure2 = 7145;
    private final static int q_seal_report_box = 7146;
    private final static int q_report_box = 7147;
    // mobs
    private final static int oppressed_one = 21553;
    private final static int oppressed_one_a = 21554;
    private final static int agent_of_slaughter = 21555;
    private final static int agent_of_slaughter_a = 21556;
    private final static int sacrificed_one = 21561;

    public _022_TragedyInVonHellmannForest() {
        super(false);
        addStartNpc(grandmagister_tifaren);
        addTalkId(grandmagister_tifaren, rune_ghost2, highpriest_innocentin, rune_ghost3, umul);
        addAttackId(ghost_of_umul);
        addKillId(ghost_of_umul);
        addKillId(oppressed_one, oppressed_one_a, agent_of_slaughter, agent_of_slaughter_a, sacrificed_one);
        addQuestItem(q_lost_elfs_skull);
        addQuestCompletedCheck(21);
        addLevelCheck(63);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetMemoState = st.getInt("tragedy_of_helman_forest");
        final int spawned_rune_ghost2 = st.getInt("spawned_rune_ghost2");
        final int spawned_ghost_of_umul = st.getInt("spawned_ghost_of_umul");
        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(1), true);
            st.setMemoState("spawned_rune_ghost2", String.valueOf(0), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "grandmagister_tifaren_q0022_04.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 1) {
            if (st.ownItemCount(q_cross_of_einhasad2) >= 1) {
                htmltext = "grandmagister_tifaren_q0022_06.htm";
            } else {
                st.setCond(2);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "grandmagister_tifaren_q0022_07.htm";
            }
        } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 1) {
            if (st.ownItemCount(q_cross_of_einhasad2) >= 1) {
                st.setCond(4);
                st.setMemoState("tragedy_of_helman_forest", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "grandmagister_tifaren_q0022_08.htm";
            }
        } else if (event.equalsIgnoreCase("reply_5")) {
            if (GetMemoState == 2 && st.ownItemCount(q_cross_of_einhasad2) >= 1 && st.ownItemCount(q_lost_elfs_skull) >= 1) {
                if (spawned_rune_ghost2 == 0) {
                    st.setCond(7);
                    st.setMemoState("spawned_rune_ghost2", String.valueOf(1), true);
                    st.setMemoState("tragedy_of_helman_forest", String.valueOf(4), true);
                    st.setMemoState("rune_ghost2_player_name", st.getPlayer().getName(), true);
                    st.takeItems(q_lost_elfs_skull, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    NpcInstance ghost2 = st.addSpawn(rune_ghost2, 38354, -49777, -1128);
                    Functions.npcSay(ghost2, NpcString.DID_YOU_CALL_ME_S1, st.getPlayer().getName());
                    st.startQuestTimer("despawn_rune_ghost2", 120000, ghost2);
                    htmltext = "grandmagister_tifaren_q0022_13.htm";
                } else {
                    st.setCond(6);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "grandmagister_tifaren_q0022_14.htm";
                }
            } else if (GetMemoState == 4 && st.ownItemCount(q_cross_of_einhasad2) >= 1) {
                if (spawned_rune_ghost2 == 0) {
                    NpcInstance ghost2 = st.addSpawn(rune_ghost2, 38354, -49777, -1128);
                    st.startQuestTimer("despawn_rune_ghost2", 120000, ghost2);
                    htmltext = "grandmagister_tifaren_q0022_13.htm";
                } else {
                    st.setCond(6);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "grandmagister_tifaren_q0022_14.htm";
                }
            }
        } else if (event.equalsIgnoreCase("despawn_rune_ghost2")) {
            Functions.npcSay(npc, NpcString.IM_CONFUSED_MAYBE_ITS_TIME_TO_GO_BACK);
            st.removeMemo("spawned_rune_ghost2");
            if (npc != null) {
                npc.deleteMe();
            }
            st.cancelQuestTimer("despawn_rune_ghost2_2");
            return null;
        } else if (event.equalsIgnoreCase("despawn_rune_ghost2_2")) {
            //Functions.npcSay(npc, "My train of thought is chaotic. It goes back to the beginning of time..."); Возможно что ненужен диалог. Откуда взял немогу вспомнить. )))
            st.removeMemo("spawned_rune_ghost2");
            if (npc != null) {
                npc.deleteMe();
            }
            st.cancelQuestTimer("despawn_rune_ghost2");
            return null;
        } else if (event.equalsIgnoreCase("reply_6")) {
            st.soundEffect(SOUND_D_HORROR_03);
            htmltext = "rune_ghost2_q0022_04.htm";
        } else if (event.equalsIgnoreCase("reply_7")) {
            st.setCond(8);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(5), true);
            st.startQuestTimer("despawn_rune_ghost2_2", 3000, npc);
            htmltext = "rune_ghost2_q0022_08.htm";
        } else if (event.equalsIgnoreCase("reply_8") && GetMemoState == 5) {
            st.takeItems(q_cross_of_einhasad2, -1);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(6), true);
            htmltext = "highpriest_innocentin_q0022_03.htm";
        } else if (event.equalsIgnoreCase("reply_10") && GetMemoState == 6) {
            st.setCond(9);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(7), true);
            st.giveItems(q_letter_of_innocentin, 1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "highpriest_innocentin_q0022_09.htm";
        } else if (event.equalsIgnoreCase("reply_17") && GetMemoState == 12 && st.ownItemCount(q_report_box) >= 1) {
            st.setCond(15);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(13), true);
            st.takeItems(q_report_box, -1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "highpriest_innocentin_q0022_11.htm";
        } else if (event.equalsIgnoreCase("reply_19") && GetMemoState == 13) {
            st.setCond(16);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(14), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "highpriest_innocentin_q0022_19.htm";
        } else if (event.equalsIgnoreCase("reply_12") && GetMemoState == 7 && st.ownItemCount(q_letter_of_innocentin) >= 1) {
            st.takeItems(q_letter_of_innocentin, -1);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(8), true);
            htmltext = "rune_ghost3_q0022_03.htm";
        } else if (event.equalsIgnoreCase("reply_14") && GetMemoState == 8) {
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(9), true);
            htmltext = "rune_ghost3_q0022_08.htm";
        } else if (event.equalsIgnoreCase("reply_15") && GetMemoState == 9) {
            st.setCond(10);
            st.setMemoState("tragedy_of_helman_forest", String.valueOf(10), true);
            st.giveItems(q_calling_treasure1, 1);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "rune_ghost3_q0022_11.htm";
        } else if (event.equalsIgnoreCase("reply_16")) {
            if (spawned_ghost_of_umul == 0) {
                st.setMemoState("spawned_ghost_of_umul", String.valueOf(1), true);
                st.setMemoState("umul", String.valueOf(0), true);
                st.soundEffect(SOUND_ANTARAS_FEAR);
                final NpcInstance ghost_umul = st.addSpawn(ghost_of_umul, 34706, -54590, -2054);
                if (ghost_umul != null) {
                    ghost_umul.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 20000);
                }
                st.startQuestTimer("ghost_of_umul_1", 90000, ghost_umul);
                st.startQuestTimer("despawn_ghost_of_umul", 120000, ghost_umul);
                htmltext = "umul_q0022_02.htm";
            } else {
                htmltext = "umul_q0022_03.htm";
            }
        } else if (event.equalsIgnoreCase("ghost_of_umul_1")) {
            st.setMemoState("umul", String.valueOf(1), true);
            return null;
        } else if (event.equalsIgnoreCase("despawn_ghost_of_umul")) {
            st.removeMemo("spawned_ghost_of_umul");
            if (npc != null) {
                npc.deleteMe();
            }
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("tragedy_of_helman_forest");
        final int spawned_rune_ghost2 = st.getInt("spawned_rune_ghost2");
        String rune_ghost2_player_name = st.get("rune_ghost2_player_name");
        final int npcId = npc.getNpcId();
        final int id = st.getState();

        switch (id) {
            case CREATED: {
                if (npcId == grandmagister_tifaren) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL: {
                            htmltext = "grandmagister_tifaren_q0022_03.htm";
                            break;
                        }
                        default: {
                            htmltext = "grandmagister_tifaren_q0022_01.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == grandmagister_tifaren) {
                    if (GetMemoState == 1) {
                        htmltext = "grandmagister_tifaren_q0022_05.htm";
                    } else if (GetMemoState == 2) {
                        if (st.ownItemCount(q_cross_of_einhasad2) >= 1 && st.ownItemCount(q_lost_elfs_skull) > 0) {
                            if (spawned_rune_ghost2 == 0) {
                                htmltext = "grandmagister_tifaren_q0022_10.htm";
                            } else {
                                htmltext = "grandmagister_tifaren_q0022_11.htm";
                            }
                        } else {
                            htmltext = "grandmagister_tifaren_q0022_09.htm";
                        }
                    } else if (GetMemoState == 4 && st.ownItemCount(q_cross_of_einhasad2) >= 1) {
                        if (spawned_rune_ghost2 == 1) {
                            if (rune_ghost2_player_name == st.getPlayer().getName()) {
                                htmltext = "grandmagister_tifaren_q0022_15.htm";
                            } else {
                                st.setCond(6);
                                htmltext = "grandmagister_tifaren_q0022_16.htm";
                                st.soundEffect(SOUND_MIDDLE);
                            }
                        } else {
                            htmltext = "grandmagister_tifaren_q0022_17.htm";
                        }
                    } else if (GetMemoState == 5 && st.ownItemCount(q_cross_of_einhasad2) >= 1) {
                        htmltext = "grandmagister_tifaren_q0022_19.htm";
                    }
                } else if (npcId == rune_ghost2) {
                    if (rune_ghost2_player_name != st.getPlayer().getName()) {
                        htmltext = "rune_ghost2_q0022_01a.htm";
                        st.soundEffect(SOUND_D_HORROR_15);
                    } else if (rune_ghost2_player_name == st.getPlayer().getName()) {
                        htmltext = "rune_ghost2_q0022_01.htm";
                        st.soundEffect(SOUND_D_HORROR_15);
                    }
                } else if (npcId == highpriest_innocentin) {
                    if (GetMemoState < 5 && st.ownItemCount(q_cross_of_einhasad2) == 0) {
                        st.setCond(3);
                        st.giveItems(q_cross_of_einhasad2, 1);
                        htmltext = "highpriest_innocentin_q0022_01.htm";
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (GetMemoState < 5 && st.ownItemCount(q_cross_of_einhasad2) >= 1) {
                        htmltext = "highpriest_innocentin_q0022_01b.htm";
                    } else if (GetMemoState == 5) {
                        htmltext = "highpriest_innocentin_q0022_02.htm";
                    } else if (GetMemoState == 6) {
                        htmltext = "highpriest_innocentin_q0022_04.htm";
                    } else if (GetMemoState == 7) {
                        htmltext = "highpriest_innocentin_q0022_09a.htm";
                    } else if (GetMemoState == 12 && st.ownItemCount(q_report_box) >= 1) {
                        htmltext = "highpriest_innocentin_q0022_10.htm";
                    } else if (GetMemoState == 13) {
                        htmltext = "highpriest_innocentin_q0022_12.htm";
                    } else if (GetMemoState == 14 && st.getPlayer().getLevel() >= 64) {
                        st.removeMemo("rune_ghost2_player_name");
                        st.removeMemo("tragedy_of_helman_forest");
                        st.addExpAndSp(345966, 31578);
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                        htmltext = "highpriest_innocentin_q0022_20.htm";
                    } else if (GetMemoState == 14 && st.getPlayer().getLevel() < 64) {
                        st.removeMemo("rune_ghost2_player_name");
                        st.removeMemo("tragedy_of_helman_forest");
                        st.addExpAndSp(345966, 31578);
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                        htmltext = "highpriest_innocentin_q0022_21.htm";
                    }
                } else if (npcId == rune_ghost3) {
                    if (GetMemoState == 7 && st.ownItemCount(q_letter_of_innocentin) >= 1) {
                        htmltext = "rune_ghost3_q0022_01.htm";
                    } else if (GetMemoState == 8) {
                        htmltext = "rune_ghost3_q0022_03a.htm";
                    } else if (GetMemoState == 9) {
                        htmltext = "rune_ghost3_q0022_10.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_calling_treasure1) >= 1) {
                        htmltext = "rune_ghost3_q0022_14.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_calling_treasure2) >= 1 && st.ownItemCount(q_seal_report_box) == 0) {
                        st.setCond(12);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rune_ghost3_q0022_15.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_calling_treasure2) >= 1 && st.ownItemCount(q_seal_report_box) >= 1) {
                        st.setCond(14);
                        st.setMemoState("tragedy_of_helman_forest", String.valueOf(12), true);
                        st.giveItems(q_report_box, 1);
                        st.takeItems(q_seal_report_box, -1);
                        st.takeItems(q_calling_treasure2, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "rune_ghost3_q0022_16.htm";
                    } else if (GetMemoState == 12 && st.ownItemCount(q_report_box) >= 1) {
                        htmltext = "rune_ghost3_q0022_17.htm";
                    }
                } else if (npcId == umul) {
                    if ((GetMemoState == 10 || GetMemoState == 11) && st.ownItemCount(q_calling_treasure1) >= 1) {
                        st.soundEffect(SOUND_DD_HORROR_01);
                        htmltext = "umul_q0022_01.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_calling_treasure2) >= 1 && st.ownItemCount(q_seal_report_box) == 0) {
                        st.setCond(13);
                        st.giveItems(q_seal_report_box, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "umul_q0022_04.htm";
                    } else if (GetMemoState == 11 && st.ownItemCount(q_calling_treasure2) >= 1 && st.ownItemCount(q_seal_report_box) >= 1) {
                        htmltext = "umul_q0022_05.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }

    @Override
    public String onAttack(final NpcInstance npc, final QuestState st) {
        final int npcId = npc.getNpcId();
        final int GetMemoState = st.getInt("tragedy_of_helman_forest");
        final int spawned_ghost_of_umul = st.getInt("spawned_ghost_of_umul");
        if (npcId == ghost_of_umul) {
            if (GetMemoState == 10 && st.ownItemCount(q_calling_treasure1) >= 1) {
                st.setMemoState("tragedy_of_helman_forest", String.valueOf(11), true);
            } else if (GetMemoState == 11 && st.ownItemCount(q_calling_treasure1) >= 1 && Rnd.get(100) < 5) {
                if (spawned_ghost_of_umul == 1) {
                    st.setCond(11);
                    st.takeItems(q_calling_treasure1, -1);
                    st.giveItems(q_calling_treasure2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }

    @Override
    public String onKill(final NpcInstance npc, final QuestState st) {
        final int npcId = npc.getNpcId();
        final int GetMemoState = st.getInt("tragedy_of_helman_forest");
        if (npcId == ghost_of_umul) {
            st.removeMemo("spawned_ghost_of_umul");
        } else if ((npcId == oppressed_one || npcId == oppressed_one_a || npcId == agent_of_slaughter || npcId == agent_of_slaughter_a || npcId == sacrificed_one) && GetMemoState == 2 && st.ownItemCount(q_lost_elfs_skull) == 0) {
            if (Rnd.get(100) < 10) {
                st.giveItems(q_lost_elfs_skull, 1);
                st.soundEffect(SOUND_ITEMGET);
                st.setCond(5);
            }
        }
        return null;
    }
}