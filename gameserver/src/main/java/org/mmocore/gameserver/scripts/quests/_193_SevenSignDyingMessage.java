package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 01/12/2014
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _193_SevenSignDyingMessage extends Quest {
    // npc
    private static final int hollin = 30191;
    private static final int priest_kein = 32569;
    private static final int herbalist_eric = 32570;
    private static final int sir_gustaf_athebaldt = 30760;
    // mobs
    private static final int evil_of_shilen3 = 27343;
    // questitem
    private static final int q_voucher_of_croop = 13813;
    private static final int q_necklace_of_jacob = 13814;
    private static final int q_herb_of_deadpeople = 13816;
    private static final int q_statue_of_shilen2 = 14353;
    // spawn evil_of_shilen3
    public static int spawn_current = 0;

    public _193_SevenSignDyingMessage() {
        super(false);
        addStartNpc(hollin);
        addTalkId(priest_kein, herbalist_eric, sir_gustaf_athebaldt);
        addKillId(evil_of_shilen3);
        addQuestItem(q_necklace_of_jacob, q_voucher_of_croop, q_statue_of_shilen2, q_herb_of_deadpeople);
        addLevelCheck(79);
        addQuestCompletedCheck(192);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("ssq_dying_massage");
        int npcId = npc.getNpcId();
        if (npcId == hollin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq_dying_massage", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(q_necklace_of_jacob, 1);
                htmltext = "hollin_q0193_04.htm";
            }
        } else if (npcId == priest_kein) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "priest_kein_q0193_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "priest_kein_q0193_03.htm";
                }
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                        htmltext = "priest_kein_q0193_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_necklace_of_jacob) >= 1) {
                        st.setCond(2);
                        st.setMemoState("ssq_dying_massage", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "priest_kein_q0193_05.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 3) {
                    if (st.ownItemCount(q_herb_of_deadpeople) >= 1) {
                        st.setCond(4);
                        st.setMemoState("ssq_dying_massage", String.valueOf(4), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.takeItems(q_herb_of_deadpeople, -1);
                        st.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ_DYING_MASSAGE);
                        return null;
                    }
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_statue_of_shilen2) >= 1)
                        htmltext = "priest_kein_q0193_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_statue_of_shilen2) >= 1)
                        htmltext = "priest_kein_q0193_14.htm";
                }
            } else if (event.equalsIgnoreCase("reply_9")) {
                if (GetMemoState == 5) {
                    if (st.ownItemCount(q_statue_of_shilen2) >= 1) {
                        st.setCond(6);
                        st.setMemoState("ssq_dying_massage", String.valueOf(6), true);
                        st.soundEffect(SOUND_MIDDLE);
                        st.takeItems(q_statue_of_shilen2, -1);
                        htmltext = "priest_kein_q0193_15.htm";
                    }
                }
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 4) {
                    if (spawn_current == 0) {
                        spawn_current++;
                        st.setMemoState("evil_of_shilen3_player_name", st.getPlayer().getName(), true);
                        Functions.npcSay(npc, NpcString.S1_THAT_STRANGER_MUST_BE_DEFEATED_HERE_IS_THE_ULTIMATE_HELP, st.getPlayer().getName());
                        NpcInstance shilen = st.addSpawn(evil_of_shilen3, 82425, 47232, -3216);
                        if (shilen != null)
                            Functions.npcSay(shilen, NpcString.YOU_ARE_NOT_THE_OWNER_OF_THAT_ITEM);
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                shilen.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 2000);
                            }
                        }, 2000L);
                        st.startQuestTimer("9999", 30000 - Rnd.get(20000), shilen);
                        return null;
                    } else
                        htmltext = "priest_kein_q0193_09b.htm";
                }
            }
        } else if (event.equalsIgnoreCase("9999")) {
            if (npc != null)
                st.removeMemo("evil_of_shilen3_player_name");
            Functions.npcSay(npc, NpcString.YOU_ARE_NOT_THE_OWNER_OF_THAT_ITEM);
            spawn_current = 0;
            npc.deleteMe();
            return null;
        } else if (npcId == herbalist_eric) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2) {
                    st.setCond(3);
                    st.setMemoState("ssq_dying_massage", String.valueOf(3), true);
                    st.soundEffect(SOUND_MIDDLE);
                    st.giveItems(q_herb_of_deadpeople, 1);
                    htmltext = "herbalist_eric_q0193_02.htm";
                }
            }
        } else if (npcId == sir_gustaf_athebaldt) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 6) {
                    if (st.getPlayer().getLevel() >= 79) {
                        st.addExpAndSp(52518015, 5817677);
                        st.removeMemo("ssq_dying_massage");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "sir_gustaf_athebaldt_q0193_02.htm";
                    } else
                        htmltext = "level_check_q0192_01.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ssq_dying_massage");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == hollin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "hollin_q0193_03.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "hollin_q0193_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "hollin_q0193_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == hollin) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                            htmltext = "hollin_q0193_05.htm";
                    }
                } else if (npcId == priest_kein) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_necklace_of_jacob) >= 1)
                            htmltext = "priest_kein_q0193_01.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "priest_kein_q0193_06.htm";
                    else if (GetMemoState == 3) {
                        if (st.ownItemCount(q_herb_of_deadpeople) >= 1)
                            htmltext = "priest_kein_q0193_07.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "priest_kein_q0193_09.htm";
                    else if (GetMemoState == 5) {
                        if (st.ownItemCount(q_statue_of_shilen2) >= 1)
                            htmltext = "priest_kein_q0193_12.htm";
                    }
                } else if (npcId == herbalist_eric) {
                    if (GetMemoState == 2)
                        htmltext = "herbalist_eric_q0193_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "herbalist_eric_q0193_03.htm";
                } else if (npcId == sir_gustaf_athebaldt) {
                    if (GetMemoState == 6)
                        htmltext = "sir_gustaf_athebaldt_q0193_01.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        NpcInstance kein = GameObjectsStorage.getByNpcId(priest_kein);
        String evil_of_shilen3_player_name = st.get("evil_of_shilen3_player_name");
        int GetMemoState = st.getInt("ssq_dying_massage");
        if (npcId == evil_of_shilen3) {
            if (GetMemoState == 4) {
                if (evil_of_shilen3_player_name == st.getPlayer().getName()) {
                    st.setCond(5);
                    st.setMemoState("ssq_dying_massage", String.valueOf(5), true);
                    st.removeMemo("evil_of_shilen3_player_name");
                    Functions.npcSay(npc, NpcString.S1_YOU_MAY_HAVE_WON_THIS_TIME_BUT_NEXT_TIME_I_WILL_SURELY_CAPTURE_YOU, st.getPlayer().getName());
                    Functions.npcSay(kein, NpcString.WELL_DONE_S1_YOUR_HELP_IS_MUCH_APPRECIATED, st.getPlayer().getName());
                    st.giveItems(q_statue_of_shilen2, 1);
                    if (st.isRunningQuestTimer("9999"))
                        st.cancelQuestTimer("9999");
                    st.soundEffect(SOUND_FINISH);
                    spawn_current = 0;
                }
            }
        }
        return null;
    }
}