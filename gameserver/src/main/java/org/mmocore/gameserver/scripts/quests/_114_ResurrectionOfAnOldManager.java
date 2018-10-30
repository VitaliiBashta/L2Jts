package org.mmocore.gameserver.scripts.quests;

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
 * @version 1.1
 * @date 26/10/2015
 * @LastEdit 10/10/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _114_ResurrectionOfAnOldManager extends Quest {
    // npc
    private static final int head_blacksmith_newyear = 31961;
    private static final int collecter_yumi = 32041;
    private static final int pavel_atlanta = 32046;
    private static final int chaos_secretary_wendy = 32047;
    private static final int chaos_box2 = 32050;
    // mobs
    private static final int q_wendy_guardian = 27318;
    // questitem
    private static final int q_electronic_wave_checker_1 = 8090;
    private static final int q_electronic_wave_checker_2 = 8091;
    private static final int q_chaos_starstone = 8287;
    private static final int q_yumis_letter = 8288;
    private static final int q_chaos_starstone2 = 8289;
    // spawn memo
    private int myself_i_quest0 = 0;
    // player name
    private String myself_i_quest1;

    public _114_ResurrectionOfAnOldManager() {
        super(false);
        addStartNpc(collecter_yumi);
        addTalkId(chaos_secretary_wendy, chaos_box2, pavel_atlanta, head_blacksmith_newyear);
        addKillId(q_wendy_guardian);
        addQuestItem(q_electronic_wave_checker_1, q_electronic_wave_checker_2, q_chaos_starstone, q_yumis_letter, q_chaos_starstone2);
        addLevelCheck(70);
        addQuestCompletedCheck(121);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("return_of_old_susceptor");
        int GetMemoStateEx = st.getInt("return_of_old_susceptor_ex");
        int npcId = npc.getNpcId();
        if (npcId == collecter_yumi) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("return_of_old_susceptor", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "collecter_yumi_q0114_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                if (GetMemoState == 0) {
                    st.setMemoState("return_of_old_susceptor", String.valueOf(1), true);
                    htmltext = "collecter_yumi_q0114_08.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(2), true);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(0), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0114_09.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=7")) {
                if (GetMemoState == 3 && GetMemoStateEx == 10011)
                    htmltext = "collecter_yumi_q0114_12.htm";
                else if (GetMemoState == 3 && GetMemoStateEx == 20011)
                    htmltext = "collecter_yumi_q0114_13.htm";
                else if (GetMemoState == 3 && GetMemoStateEx == 30011)
                    htmltext = "collecter_yumi_q0114_14.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=8")) {
                if (GetMemoState == 3) {
                    st.setMemoState("return_of_old_susceptor", String.valueOf(4), true);
                    htmltext = "collecter_yumi_q0114_15.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=10")) {
                if (GetMemoState == 4) {
                    st.setMemoState("return_of_old_susceptor", String.valueOf(5), true);
                    htmltext = "collecter_yumi_q0114_23.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=11")) {
                if (GetMemoState == 5) {
                    st.setCond(6);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(6), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0114_26.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=18"))
                htmltext = "collecter_yumi_q0114_29.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=12"))
                htmltext = "collecter_yumi_q0114_30.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=13")) {
                if (GetMemoState == 7) {
                    st.setCond(17);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(8), true);
                    st.giveItems(q_electronic_wave_checker_1, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0114_31.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=15")) {
                if (GetMemoState == 9 && st.ownItemCount(q_electronic_wave_checker_2) >= 1) {
                    st.takeItems(q_electronic_wave_checker_2, -1);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(10), true);
                    htmltext = "collecter_yumi_q0114_34.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=14")) {
                if (GetMemoState == 10 && GetMemoStateEx == 20211)
                    htmltext = "collecter_yumi_q0114_37.htm";
                if (GetMemoState == 10 && (GetMemoStateEx == 10111 || GetMemoStateEx == 30411))
                    htmltext = "collecter_yumi_q0114_38.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=16")) {
                if (GetMemoState == 10 && (GetMemoStateEx == 10111 || GetMemoStateEx == 30411)) {
                    st.setCond(20);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(101), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0114_39.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=17")) {
                if (GetMemoState == 10) {
                    st.setCond(21);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(11), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "collecter_yumi_q0114_40.htm";
                }
            }
        } else if (npcId == chaos_secretary_wendy) {
            if (event.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                if (GetMemoState == 2) {
                    if (GetMemoStateEx % 10 == 0)
                        st.setMemoState("return_of_old_susceptor_ex", String.valueOf(GetMemoStateEx + 1), true);
                    htmltext = "chaos_secretary_wendy_q0114_02.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=2")) {
                if (GetMemoState == 2) {
                    int i1 = GetMemoStateEx % 100;
                    i1 = i1 / 10;
                    if (i1 == 0)
                        st.setMemoState("return_of_old_susceptor_ex", String.valueOf(GetMemoStateEx + 10), true);
                    htmltext = "chaos_secretary_wendy_q0114_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=3")) {
                if (GetMemoState == 2 && GetMemoStateEx < 11)
                    htmltext = "chaos_secretary_wendy_q0114_04.htm";
                else if (GetMemoState == 2 && GetMemoStateEx == 11)
                    htmltext = "chaos_secretary_wendy_q0114_05.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=4")) {
                if (GetMemoState == 2 && GetMemoStateEx == 11) {
                    st.setCond(3);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(3), true);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(10011), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=401"))
                htmltext = "chaos_secretary_wendy_q0114_06a.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=5")) {
                if (GetMemoState == 2 && GetMemoStateEx == 11) {
                    st.setCond(4);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(3), true);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(20011), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_07.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=6")) {
                if (GetMemoState == 2 && GetMemoStateEx == 11) {
                    st.setCond(5);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(3), true);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(30011), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_09.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=402"))
                htmltext = "chaos_secretary_wendy_q0114_12a.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=403"))
                htmltext = "chaos_secretary_wendy_q0114_13a.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=405")) {
                if (GetMemoState == 6 && GetMemoStateEx == 10011) {
                    st.setCond(7);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(30011), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_14ab.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=404"))
                htmltext = "chaos_secretary_wendy_q0114_14a.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=407"))
                htmltext = "chaos_secretary_wendy_q0114_17a.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=408")) {
                if (GetMemoState == 6 && GetMemoStateEx == 10011) {
                    st.setCond(8);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(10111), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_20a.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=409")) {
                if (GetMemoState == 6 && GetMemoStateEx == 10111) {
                    st.setCond(9);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(7), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_21a.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=501"))
                htmltext = "chaos_secretary_wendy_q0114_12b.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=502"))
                htmltext = "chaos_secretary_wendy_q0114_13b.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=503")) {
                if (GetMemoState == 6 && GetMemoStateEx == 20011) {
                    st.setCond(10);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(20111), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_14b.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=504")) {
                if (myself_i_quest0 == 0) {
                    myself_i_quest0 = 1;
                    myself_i_quest1 = st.getPlayer().getName();
                    NpcInstance wendy_guardian = st.addSpawn(q_wendy_guardian, 96977, -110625, -3322);
                    st.startQuestTimer("11401", 500, wendy_guardian);
                    st.startQuestTimer("11402", 1000 * 300, wendy_guardian);
                    htmltext = "chaos_secretary_wendy_q0114_15b.htm";
                } else if (myself_i_quest1 == st.getPlayer().getName())
                    htmltext = "chaos_secretary_wendy_q0114_17b.htm";
                else
                    htmltext = "chaos_secretary_wendy_q0114_16b.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=505")) {
                if (GetMemoState == 6 && GetMemoStateEx == 20211) {
                    st.setCond(12);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(7), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_20b.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=506"))
                htmltext = "chaos_secretary_wendy_q0114_21b.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=601"))
                htmltext = "chaos_secretary_wendy_q0114_12c.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=602"))
                htmltext = "chaos_secretary_wendy_q0114_17c.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=603"))
                htmltext = "chaos_secretary_wendy_q0114_18c.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=604"))
                htmltext = "chaos_secretary_wendy_q0114_19c.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=605")) {
                if (GetMemoState == 6 && GetMemoStateEx == 30011) {
                    st.setCond(13);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(30111), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_20c.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=607")) {
                if (GetMemoState == 6 && GetMemoStateEx == 30311) {
                    st.setCond(15);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(30411), true);
                    st.takeItems(q_chaos_starstone, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_23c.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=410")) {
                if (GetMemoState == 101 && GetMemoStateEx == 10111) {
                    st.setCond(23);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(102), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_secretary_wendy_q0114_23a.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=610"))
                htmltext = "chaos_secretary_wendy_q0114_27c.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=611"))
                htmltext = "chaos_secretary_wendy_q0114_28c.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=612")) {
                if ((GetMemoState == 101 || GetMemoState == 103) && GetMemoStateEx == 30411) {
                    if (st.ownItemCount(ADENA_ID) >= 3000) {
                        st.setCond(26);
                        st.setMemoState("return_of_old_susceptor", String.valueOf(12), true);
                        st.giveItems(q_chaos_starstone2, 1);
                        st.takeItems(ADENA_ID, 3000);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "chaos_secretary_wendy_q0114_29c.htm";
                    } else
                        htmltext = "chaos_secretary_wendy_q0114_29ca.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=613")) {
                if ((GetMemoState == 101 || GetMemoState == 103) && GetMemoStateEx == 30411) {
                    st.setMemoState("return_of_old_susceptor", String.valueOf(103), true);
                    htmltext = "chaos_secretary_wendy_q0114_30c.htm";
                }
            }
        } else if (event.equalsIgnoreCase("11401")) {
            Functions.npcSay(npc, NpcString.YOU_S1_YOU_ATTACKED_WENDY_PREPARE_TO_DIE, st.getPlayer().getName());
            if (npc != null)
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 2000);
            return null;
        } else if (event.equalsIgnoreCase("11402")) {
            if (npc != null) {
                myself_i_quest0 = 0;
                myself_i_quest1 = null;
                Functions.npcSay(npc, NpcString.S1_YOUR_ENEMY_WAS_DRIVEN_OUT_I_WILL_NOW_WITHDRAW_AND_AWAIT_YOUR_NEXT_COMMAND, st.getPlayer().getName());
                npc.deleteMe();
            }
            return null;
        } else if (npcId == chaos_box2) {
            if (event.equalsIgnoreCase("menu_select?ask=114&reply=999")) {
                if (GetMemoState == 6 && GetMemoStateEx == 30111) {
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(30211), true);
                    st.soundEffect(SOUND_ARMOR_WOOD_3);
                    htmltext = "chaos_box2_q0114_01r.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=606")) {
                if (GetMemoState == 6 && GetMemoStateEx == 30211) {
                    st.setCond(14);
                    st.giveItems(q_chaos_starstone, 1);
                    st.setMemoState("return_of_old_susceptor_ex", String.valueOf(30311), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_box2_q0114_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=411")) {
                if (GetMemoState == 102 && GetMemoStateEx == 10111 && st.ownItemCount(q_chaos_starstone2) == 0) {
                    st.setCond(24);
                    st.giveItems(q_chaos_starstone2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "chaos_box2_q0114_05.htm";
                }
            }
        } else if (npcId == pavel_atlanta) {
            if (event.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                if (GetMemoState == 8 && st.ownItemCount(q_electronic_wave_checker_2) >= 1) {
                    st.setCond(19);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(9), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pavel_atlanta_q0114_03.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=114&reply=2"))
                htmltext = "pavel_atlanta_q0114_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=3"))
                htmltext = "pavel_atlanta_q0114_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=114&reply=4")) {
                if (GetMemoState == 13) {
                    st.takeItems(q_chaos_starstone2, -1);
                    st.addExpAndSp(1846611, 144270);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "pavel_atlanta_q0114_07.htm";
                }
            }
        } else if (npcId == head_blacksmith_newyear) {
            if (event.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                if (GetMemoState == 11) {
                    st.setCond(22);
                    st.setMemoState("return_of_old_susceptor", String.valueOf(12), true);
                    st.giveItems(q_chaos_starstone2, 1);
                    st.takeItems(q_yumis_letter, -1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "head_blacksmith_newyear_q0114_02.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("return_of_old_susceptor");
        int GetMemoStateEx = st.getInt("return_of_old_susceptor_ex");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == collecter_yumi) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "collecter_yumi_q0114_03.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "collecter_yumi_q0114_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "collecter_yumi_q0114_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == collecter_yumi) {
                    if (GetMemoState == 0)
                        htmltext = "collecter_yumi_q0114_04.htm";
                    else if (GetMemoState == 1)
                        htmltext = "collecter_yumi_q0114_08.htm";
                    else if (GetMemoState == 2)
                        htmltext = "collecter_yumi_q0114_10.htm";
                    else if (GetMemoState == 3)
                        htmltext = "collecter_yumi_q0114_11.htm";
                    else if (GetMemoState == 4)
                        htmltext = "collecter_yumi_q0114_16.htm";
                    else if (GetMemoState == 5)
                        htmltext = "collecter_yumi_q0114_24.htm";
                    else if (GetMemoState == 6)
                        htmltext = "collecter_yumi_q0114_27.htm";
                    else if (GetMemoState == 7)
                        htmltext = "collecter_yumi_q0114_28.htm";
                    else if (GetMemoState == 8)
                        htmltext = "collecter_yumi_q0114_32.htm";
                    else if (GetMemoState == 9 && st.ownItemCount(q_electronic_wave_checker_2) >= 1)
                        htmltext = "collecter_yumi_q0114_33.htm";
                    else if (GetMemoState == 10)
                        htmltext = "collecter_yumi_q0114_34z.htm";
                    else if (GetMemoState == 101)
                        htmltext = "collecter_yumi_q0114_39z.htm";
                    else if (GetMemoState == 11)
                        htmltext = "collecter_yumi_q0114_40z.htm";
                    else if (GetMemoState == 12) {
                        st.setCond(27);
                        st.setMemoState("return_of_old_susceptor", String.valueOf(13), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "collecter_yumi_q0114_41.htm";
                    } else if (GetMemoState == 13)
                        htmltext = "collecter_yumi_q0114_42.htm";
                } else if (npcId == chaos_secretary_wendy) {
                    if (GetMemoState == 2 && GetMemoStateEx < 11)
                        htmltext = "chaos_secretary_wendy_q0114_01.htm";
                    else if (GetMemoState == 2 && GetMemoStateEx == 11)
                        htmltext = "chaos_secretary_wendy_q0114_05.htm";
                    else if (GetMemoState == 3 && GetMemoStateEx == 10011)
                        htmltext = "chaos_secretary_wendy_q0114_06b.htm";
                    else if (GetMemoState == 3 && GetMemoStateEx == 20011)
                        htmltext = "chaos_secretary_wendy_q0114_08.htm";
                    else if (GetMemoState == 3 && GetMemoStateEx == 30011)
                        htmltext = "chaos_secretary_wendy_q0114_10.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 10011)
                        htmltext = "chaos_secretary_wendy_q0114_11a.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 10111)
                        htmltext = "chaos_secretary_wendy_q0114_17a.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 20011)
                        htmltext = "chaos_secretary_wendy_q0114_11b.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 20111)
                        htmltext = "chaos_secretary_wendy_q0114_18b.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 20211)
                        htmltext = "chaos_secretary_wendy_q0114_19b.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 30011)
                        htmltext = "chaos_secretary_wendy_q0114_11c.htm";
                    else if (GetMemoState == 6 && (GetMemoStateEx == 30111 || GetMemoStateEx == 30211))
                        htmltext = "chaos_secretary_wendy_q0114_21c.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 30311)
                        htmltext = "chaos_secretary_wendy_q0114_22c.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 30411) {
                        st.setCond(16);
                        st.setMemoState("return_of_old_susceptor", String.valueOf(7), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "chaos_secretary_wendy_q0114_24c.htm";
                    } else if (GetMemoState == 7)
                        htmltext = "chaos_secretary_wendy_q0114_25c.htm";
                    else if (GetMemoState == 101 && GetMemoStateEx == 10111)
                        htmltext = "chaos_secretary_wendy_q0114_22a.htm";
                    else if (GetMemoState == 102 && GetMemoStateEx == 10111 && st.ownItemCount(q_chaos_starstone2) == 0)
                        htmltext = "chaos_secretary_wendy_q0114_23z.htm";
                    else if (GetMemoState == 102 && GetMemoStateEx == 10111 && st.ownItemCount(q_chaos_starstone2) >= 1) {
                        st.setCond(25);
                        st.setMemoState("return_of_old_susceptor", String.valueOf(12), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "chaos_secretary_wendy_q0114_24a.htm";
                    } else if (GetMemoState == 12 && GetMemoStateEx == 10111)
                        htmltext = "chaos_secretary_wendy_q0114_24a.htm";
                    else if (GetMemoState == 101 && GetMemoStateEx == 30411)
                        htmltext = "chaos_secretary_wendy_q0114_26c.htm";
                    else if (GetMemoState == 103 && GetMemoStateEx == 30411)
                        htmltext = "chaos_secretary_wendy_q0114_31c.htm";
                    else if (GetMemoState == 12 && GetMemoStateEx == 30411)
                        htmltext = "chaos_secretary_wendy_q0114_32c.htm";
                } else if (npcId == chaos_box2) {
                    if (GetMemoState == 6 && GetMemoStateEx == 30111)
                        htmltext = "chaos_box2_q0114_01.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 30211)
                        htmltext = "chaos_box2_q0114_02.htm";
                    else if (GetMemoState == 6 && GetMemoStateEx == 30311)
                        htmltext = "chaos_box2_q0114_04.htm";
                    else if (GetMemoState == 102 && GetMemoStateEx == 10111 && st.ownItemCount(q_chaos_starstone2) == 0)
                        htmltext = "chaos_box2_q0114_04b.htm";
                    else if (GetMemoState == 102 && GetMemoStateEx == 10111 && st.ownItemCount(q_chaos_starstone2) >= 1)
                        htmltext = "chaos_box2_q0114_05z.htm";
                } else if (npcId == pavel_atlanta) {
                    if (GetMemoState == 8 && st.ownItemCount(q_electronic_wave_checker_2) >= 1)
                        htmltext = "pavel_atlanta_q0114_02.htm";
                    else if (GetMemoState == 9)
                        htmltext = "pavel_atlanta_q0114_03.htm";
                    else if (GetMemoState == 13)
                        htmltext = "pavel_atlanta_q0114_04.htm";
                } else if (npcId == head_blacksmith_newyear) {
                    if (GetMemoState == 11)
                        htmltext = "head_blacksmith_newyear_q0114_01.htm";
                    else if (GetMemoState == 12)
                        htmltext = "head_blacksmith_newyear_q0114_03.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("return_of_old_susceptor");
        int GetMemoStateEx = st.getInt("return_of_old_susceptor_ex");
        int npcId = npc.getNpcId();
        if (npcId == q_wendy_guardian) {
            if (GetMemoState == 6 && GetMemoStateEx == 20111) {
                st.setCond(11);
                st.setMemoState("return_of_old_susceptor_ex", String.valueOf(20211), true);
                Functions.npcSay(npc, NpcString.THIS_ENEMY_IS_FAR_TOO_POWERFUL_FOR_ME_TO_FIGHT_I_MUST_WITHDRAW);
                st.soundEffect(SOUND_MIDDLE);
                myself_i_quest0 = 0;
                myself_i_quest1 = null;
            }
        }
        return null;
    }
}