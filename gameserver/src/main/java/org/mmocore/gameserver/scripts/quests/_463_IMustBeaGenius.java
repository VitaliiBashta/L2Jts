package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.htm.HtmCache;
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
 * @date 14/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _463_IMustBeaGenius extends Quest {
    // npc
    private static final int collecter_gutenhagen = 32069;
    // questitem
    private static final int q_log_of_golemgroup = 15510;
    private static final int q_log_roll_of_golemgroup = 15511;
    // mobs
    private static final int golem_cannon1_p = 22801;
    private static final int golem_cannon2_p = 22802;
    private static final int golem_cannon3_p = 22803;
    private static final int golem_prop1_p = 22804;
    private static final int golem_prop2_p = 22805;
    private static final int golem_prop3_p = 22806;
    private static final int golem_carrier_p = 22807;
    private static final int golem_guardian_p = 22809;
    private static final int golem_micro_p = 22810;
    private static final int golem_steel_p = 22811;
    private static final int golem_boom1_p = 22812;

    public _463_IMustBeaGenius() {
        super(false);
        addStartNpc(collecter_gutenhagen);
        addQuestItem(q_log_of_golemgroup, q_log_roll_of_golemgroup);
        addKillId(golem_cannon1_p, golem_cannon2_p, golem_cannon3_p, golem_prop1_p, golem_prop2_p, golem_prop3_p, golem_carrier_p, golem_guardian_p, golem_micro_p, golem_steel_p, golem_boom1_p);
        addLevelCheck(70);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("i_am_a_genius");
        int GetMemoStateEx_1 = st.getInt("i_am_a_genius_ex_1");
        int npcId = npc.getNpcId();
        if (npcId == collecter_gutenhagen) {
            int i8 = Rnd.get(10);
            if (event.equalsIgnoreCase("quest_accept")) {
                int i0 = Rnd.get(51);
                i0 = i0 + 550;
                int i1 = Rnd.get(4);
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.setMemoState("i_am_a_genius", String.valueOf(1), true);
                st.setMemoState("i_am_a_genius_ex_1", String.valueOf(i0), true);
                st.setMemoState("i_am_a_genius_ex_2", String.valueOf(i1), true);
                htmltext = HtmCache.getInstance().getHtml("quests/_463_IMustBeaGenius/collecter_gutenhagen_q0463_05.htm", st.getPlayer());
                htmltext = htmltext.replace("<?number?>", String.valueOf(i0));
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "collecter_gutenhagen_q0463_04.htm";
            else if (event.equalsIgnoreCase("reply_2")) {
                int i0 = GetMemoStateEx_1;
                if (GetMemoState == 1 && st.ownItemCount(q_log_of_golemgroup) != i0) {
                    htmltext = HtmCache.getInstance().getHtml("quests/_463_IMustBeaGenius/collecter_gutenhagen_q0463_07.htm", st.getPlayer());
                    htmltext = htmltext.replace("<?number?>", String.valueOf(i0));
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                int i6 = 0;
                int i7 = 0;
                if (GetMemoState == 3) {
                    if (i8 == 0) {
                        st.addExpAndSp(198725, 0);
                        i6 = 1;
                    } else if (i8 >= 1 && i8 < 5) {
                        st.addExpAndSp(278216, 0);
                        i6 = 1;
                    } else if (i8 >= 5 && i8 < 10) {
                        st.addExpAndSp(317961, 0);
                        i6 = 1;
                    } else if (i8 >= 10 && i8 < 25) {
                        st.addExpAndSp(357706, 0);
                        i6 = 2;
                    } else if (i8 >= 25 && i8 < 40) {
                        st.addExpAndSp(397451, 0);
                        i6 = 2;
                    } else if (i8 >= 40 && i8 < 60) {
                        st.addExpAndSp(596176, 0);
                        i6 = 2;
                    } else if (i8 >= 60 && i8 < 72) {
                        st.addExpAndSp(715411, 0);
                        i6 = 3;
                    } else if (i8 >= 72 && i8 < 81) {
                        st.addExpAndSp(794901, 0);
                        i6 = 3;
                    } else if (i8 >= 81 && i8 < 89) {
                        st.addExpAndSp(914137, 0);
                        i6 = 3;
                    } else {
                        st.addExpAndSp(1192352, 0);
                        i6 = 4;
                    }
                    if (i8 == 0) {
                        st.addExpAndSp(0, 15892);
                        i7 = 1;
                    } else if (i8 >= 1 && i8 < 5) {
                        st.addExpAndSp(0, 22249);
                        i7 = 1;
                    } else if (i8 >= 5 && i8 < 10) {
                        st.addExpAndSp(0, 25427);
                        i7 = 1;
                    } else if (i8 >= 10 && i8 < 25) {
                        st.addExpAndSp(0, 28606);
                        i7 = 2;
                    } else if (i8 >= 25 && i8 < 40) {
                        st.addExpAndSp(0, 31784);
                        i7 = 2;
                    } else if (i8 >= 40 && i8 < 60) {
                        st.addExpAndSp(0, 47677);
                        i7 = 2;
                    } else if (i8 >= 60 && i8 < 72) {
                        st.addExpAndSp(0, 57212);
                        i7 = 3;
                    } else if (i8 >= 72 && i8 < 81) {
                        st.addExpAndSp(0, 63569);
                        i7 = 3;
                    } else if (i8 >= 81 && i8 < 89) {
                        st.addExpAndSp(0, 73104);
                        i7 = 3;
                    } else {
                        st.addExpAndSp(0, 95353);
                        i7 = 4;
                    }
                    if (i6 == 1 && i7 == 1) {
                        htmltext = "collecter_gutenhagen_q0463_09.htm";
                    } else if ((i6 == 1 && i7 == 2) || (i6 == 2 && i7 == 1)) {
                        htmltext = "collecter_gutenhagen_q0463_10.htm";
                    } else if ((i7 == 1 && i6 == 3) || (i6 == 3 && i7 == 1) || (i6 == 1 && i7 == 4) || (i6 == 4 && i7 == 1)) {
                        htmltext = "collecter_gutenhagen_q0463_11.htm";
                    } else if ((i6 == 2 && i7 == 2) || (i6 == 2 && i7 == 3) || (i6 == 3 && i7 == 2)) {
                        htmltext = "collecter_gutenhagen_q0463_12.htm";
                    } else if ((i6 == 2 && i7 == 4) || (i6 == 4 && i7 == 2) || (i6 == 3 && i7 == 3)) {
                        htmltext = "collecter_gutenhagen_q0463_13.htm";
                    } else if ((i6 == 3 && i7 == 4) || (i6 == 4 && i7 == 3)) {
                        htmltext = "collecter_gutenhagen_q0463_14.htm";
                    } else {
                        htmltext = "collecter_gutenhagen_q0463_15.htm";
                    }
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(this);
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("i_am_a_genius");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == collecter_gutenhagen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "collecter_gutenhagen_q0463_02.htm";
                            break;
                        default:
                            if (st.isNowAvailable())
                                htmltext = "collecter_gutenhagen_q0463_01.htm";
                            else
                                htmltext = "collecter_gutenhagen_q0463_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == collecter_gutenhagen) {
                    if (GetMemoState != 2 && st.ownItemCount(q_log_roll_of_golemgroup) != 1 && GetMemoState != 3)
                        htmltext = "collecter_gutenhagen_q0463_06.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_log_roll_of_golemgroup) == 1) {
                        st.takeItems(q_log_of_golemgroup, -1);
                        st.takeItems(q_log_roll_of_golemgroup, -1);
                        st.setMemoState("i_am_a_genius", String.valueOf(3), true);
                        htmltext = "collecter_gutenhagen_q0463_08.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "collecter_gutenhagen_q0463_08a.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("i_am_a_genius");
        int GetMemoStateEx_1 = st.getInt("i_am_a_genius_ex_1");
        int GetMemoStateEx_2 = st.getInt("i_am_a_genius_ex_2");
        int npcId = npc.getNpcId();
        if (npcId == golem_cannon1_p || npcId == golem_cannon2_p || npcId == golem_cannon3_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = 0;
                if (GetMemoStateEx_2 == 0) {
                    i0 = Rnd.get(100);
                    i0 = i0 + 1;
                } else {
                    i0 = 5;
                }
                if (st.ownItemCount(q_log_of_golemgroup) + i0 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q_log_of_golemgroup, i0);
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        } else if (npcId == golem_prop1_p || npcId == golem_prop2_p || npcId == golem_prop3_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = 0;
                if (GetMemoStateEx_2 == 1) {
                    i0 = Rnd.get(100);
                    i0 = i0 + 1;
                } else {
                    i0 = -2;
                }
                if (st.ownItemCount(q_log_of_golemgroup) + i0 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    if (i0 > 0) {
                        st.giveItems(q_log_of_golemgroup, i0);
                    } else if (st.ownItemCount(q_log_of_golemgroup) > 2) {
                        st.takeItems(q_log_of_golemgroup, 2);
                    } else {
                        st.takeItems(q_log_of_golemgroup, -1);
                    }
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        } else if (npcId == golem_carrier_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = -1;
                if (st.ownItemCount(q_log_of_golemgroup) - 1 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    if (st.ownItemCount(q_log_of_golemgroup) > 1) {
                        st.takeItems(q_log_of_golemgroup, 1);
                    } else {
                        st.takeItems(q_log_of_golemgroup, -1);
                    }
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        } else if (npcId == golem_guardian_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = 0;
                if (GetMemoStateEx_2 == 2) {
                    i0 = Rnd.get(100);
                    i0 = i0 + 1;
                } else {
                    i0 = 2;
                }
                if (st.ownItemCount(q_log_of_golemgroup) + i0 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q_log_of_golemgroup, i0);
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        } else if (npcId == golem_micro_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = 0;
                if (GetMemoStateEx_2 == 3) {
                    i0 = Rnd.get(100);
                    i0 = i0 + 1;
                } else {
                    i0 = -3;
                }
                if (st.ownItemCount(q_log_of_golemgroup) + i0 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    if (i0 > 0) {
                        st.giveItems(q_log_of_golemgroup, i0);
                    } else if (st.ownItemCount(q_log_of_golemgroup) > 3) {
                        st.takeItems(q_log_of_golemgroup, 3);
                    } else {
                        st.takeItems(q_log_of_golemgroup, -1);
                    }
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        } else if (npcId == golem_steel_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = 3;
                if (st.ownItemCount(q_log_of_golemgroup) + i0 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q_log_of_golemgroup, i0);
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        } else if (npcId == golem_boom1_p) {
            if (GetMemoState == 1 && st.ownItemCount(q_log_roll_of_golemgroup) < 1) {
                int i0 = 1;
                if (st.ownItemCount(q_log_of_golemgroup) + i0 == GetMemoStateEx_1) {
                    st.takeItems(q_log_of_golemgroup, -1);
                    st.giveItems(q_log_roll_of_golemgroup, 1);
                    st.setMemoState("i_am_a_genius", String.valueOf(2), true);
                    st.soundEffect(SOUND_ITEMGET);
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(q_log_of_golemgroup, i0);
                    st.soundEffect(SOUND_ITEMGET);
                }
                Functions.npcSay(npc, NpcString.ATT__ATTACK__S1__RO__ROGUE__S2, st.getPlayer().getName(), String.valueOf(i0));
            }
        }
        return null;
    }
}