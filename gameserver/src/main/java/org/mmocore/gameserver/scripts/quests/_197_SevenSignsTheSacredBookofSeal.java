package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 10/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _197_SevenSignsTheSacredBookofSeal extends Quest {
    // npc
    private static final int priest_wood = 32593;
    private static final int highpriest_orven = 30857;
    private static final int ciper_officer_leopard = 32594;
    private static final int master_lawrence = 32595;
    private static final int director_sophia = 32596;
    // mobs
    private static final int evil_of_shilen9 = 27396;
    // questitem
    private static final int q_statue_of_shilen3 = 14354;
    private static final int q_mysterious_scriptuer_copy = 13829;
    // count spawn
    private static int av_quest0 = 0;
    // player name
    private static String _myself;

    public _197_SevenSignsTheSacredBookofSeal() {
        super(false);
        addStartNpc(priest_wood);
        addTalkId(highpriest_orven, ciper_officer_leopard, master_lawrence, director_sophia);
        addKillId(evil_of_shilen9);
        addQuestItem(q_statue_of_shilen3, q_mysterious_scriptuer_copy);
        addLevelCheck(79);
        addQuestCompletedCheck(196);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int talker_level = st.getPlayer().getLevel();
        int GetMemoState = st.getInt("ssq_scripture_of_sealing");
        int npcId = npc.getNpcId();
        if (npcId == priest_wood) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq_scripture_of_sealing", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "priest_wood_q0197_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "priest_wood_q0197_04.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "priest_wood_q0197_05.htm";
            else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 6) {
                    if (st.ownItemCount(q_mysterious_scriptuer_copy) >= 1 && st.ownItemCount(q_statue_of_shilen3) >= 1)
                        htmltext = "priest_wood_q0197_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 6) {
                    if (st.ownItemCount(q_mysterious_scriptuer_copy) >= 1 && st.ownItemCount(q_statue_of_shilen3) >= 1 && talker_level >= 79) {
                        st.addExpAndSp(52518015, 5817677);
                        st.takeItems(q_mysterious_scriptuer_copy, -1);
                        st.takeItems(q_statue_of_shilen3, -1);
                        st.removeMemo("ssq_scripture_of_sealing");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "priest_wood_q0197_09.htm";
                    }
                    if (st.ownItemCount(q_mysterious_scriptuer_copy) >= 1 && st.ownItemCount(q_statue_of_shilen3) >= 1 && talker_level < 79)
                        htmltext = "level_check_q0192_01.htm";
                }
            }
        } else if (npcId == highpriest_orven) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "highpriest_orven_q0197_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1)
                    htmltext = "highpriest_orven_q0197_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("ssq_scripture_of_sealing", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "highpriest_orven_q0197_04.htm";
                }
            }
        } else if (npcId == ciper_officer_leopard) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2)
                    htmltext = "ciper_officer_leopard_q0197_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("ssq_scripture_of_sealing", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ciper_officer_leopard_q0197_03.htm";
                }
            }
        } else if (npcId == master_lawrence) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 3)
                    htmltext = "great_master_lawrence_q0197_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 3)
                    htmltext = "great_master_lawrence_q0197_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (av_quest0 == 1)
                    htmltext = "great_master_lawrence_q0197_04a.htm";
                else {
                    htmltext = "great_master_lawrence_q0197_04.htm";
                    if (GetMemoState == 3) {
                        _myself = st.getPlayer().getName();
                        NpcInstance shilen9 = st.addSpawn(evil_of_shilen9, 152520, -57502, -3408);
                        Functions.npcSay(shilen9, NpcString.YOU_ARE_NOT_THE_OWNER_OF_THAT_ITEM);
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                shilen9.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 2000);
                            }
                        }, 2000L);
                        st.startQuestTimer("9979", 5 * 60 * 1000, shilen9);
                        av_quest0++;
                        System.out.println("Имя игрока в _myself: " + _myself);
                    }
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 4) {
                    if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                        htmltext = "great_master_lawrence_q0197_08.htm";
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 4) {
                    if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                        htmltext = "great_master_lawrence_q0197_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 4) {
                    if (st.ownItemCount(q_statue_of_shilen3) >= 1) {
                        st.setCond(5);
                        st.setMemoState("ssq_scripture_of_sealing", String.valueOf(5), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "great_master_lawrence_q0197_10.htm";
                    }
                }
            }
        } else if (event.equalsIgnoreCase("9979")) {
            if (npc != null)
                Functions.npcSay(npc, NpcString.NEXT_TIME_YOU_WILL_NOT_ESCAPE);
            av_quest0 = 0;
            npc.deleteMe();
            return null;
        } else if (npcId == director_sophia) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                        htmltext = "director_sophia_q0197_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                        htmltext = "director_sophia_q0197_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_statue_of_shilen3) >= 1) {
                        st.setCond(6);
                        st.setMemoState("ssq_scripture_of_sealing", String.valueOf(6), true);
                        st.giveItems(q_mysterious_scriptuer_copy, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "great_master_lawrence_q0197_10.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ssq_scripture_of_sealing");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == priest_wood) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "priest_wood_q0197_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "priest_wood_q0197_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == priest_wood) {
                    if (GetMemoState >= 1 && GetMemoState < 6)
                        htmltext = "priest_wood_q0197_10.htm";
                    else if (GetMemoState == 6) {
                        if (st.ownItemCount(q_mysterious_scriptuer_copy) >= 1 && st.ownItemCount(q_statue_of_shilen3) >= 1)
                            htmltext = "priest_wood_q0197_07.htm";
                    }
                } else if (npcId == highpriest_orven) {
                    if (GetMemoState == 1)
                        htmltext = "highpriest_orven_q0197_01.htm";
                    else if (GetMemoState >= 2)
                        htmltext = "highpriest_orven_q0197_05.htm";
                } else if (npcId == ciper_officer_leopard) {
                    if (GetMemoState == 2)
                        htmltext = "ciper_officer_leopard_q0197_01.htm";
                    else if (GetMemoState >= 3)
                        htmltext = "ciper_officer_leopard_q0197_04.htm";
                } else if (npcId == master_lawrence) {
                    if (GetMemoState == 3)
                        htmltext = "great_master_lawrence_q0197_01.htm";
                    else if (GetMemoState == 4) {
                        if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                            htmltext = "great_master_lawrence_q0197_07.htm";
                    } else if (GetMemoState >= 5) {
                        if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                            htmltext = "great_master_lawrence_q0197_11.htm";
                    }
                } else if (npcId == director_sophia) {
                    if (GetMemoState == 5) {
                        if (st.ownItemCount(q_statue_of_shilen3) >= 1)
                            htmltext = "director_sophia_q0197_01.htm";
                    } else if (GetMemoState >= 6) {
                        if (st.ownItemCount(q_statue_of_shilen3) >= 1 && st.ownItemCount(q_mysterious_scriptuer_copy) >= 1)
                            htmltext = "director_sophia_q0197_05.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        NpcInstance lawrence = GameObjectsStorage.getByNpcId(master_lawrence);
        int npcId = npc.getNpcId();
        if (npcId == evil_of_shilen9) {
            if (_myself == st.getPlayer().getName()) {
                st.setCond(4);
                st.setMemoState("ssq_scripture_of_sealing", String.valueOf(4), true);
                st.giveItems(q_statue_of_shilen3, 1);
                Functions.npcSay(npc, NpcString.S1_YOU_MAY_HAVE_WON_THIS_TIME_BUT_NEXT_TIME_I_WILL_SURELY_CAPTURE_YOU, st.getPlayer().getName());
                Functions.npcSay(lawrence, NpcString.WELL_DONE_S1_YOUR_HELP_IS_MUCH_APPRECIATED, st.getPlayer().getName());
                st.soundEffect(SOUND_MIDDLE);
                if (st.isRunningQuestTimer("9979"))
                    st.cancelQuestTimer("9979");
                av_quest0 = 0;
                _myself = "";
            }
        }
        return null;
    }
}