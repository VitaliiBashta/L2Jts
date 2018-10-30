package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStartScenePlayer;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 11/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _198_SevenSignsEmbryo extends Quest {
    // npc
    private static final int priest_wood = 32593;
    private static final int inzone_frantz = 32597;
    // mobs
    private static final int evil_of_shilen6 = 27346;
    private static final int evil_of_shilen12 = 27399;
    private static final int evil_of_shilen15 = 27402;
    // questitem
    private static final int q_statue_of_shilen4 = 14355;
    // etcitem
    private static final int ssq_bracelet_of_dawn = 15312;
    private static final int adena_of_ancient = 5575;
    // zone_controller
    private static final int inzone_id = 113;
    // count spawn
    private static int av_quest0 = 0;

    public _198_SevenSignsEmbryo() {
        super(false);
        addStartNpc(priest_wood);
        addTalkId(inzone_frantz);
        addKillId(evil_of_shilen6);
        addQuestItem(q_statue_of_shilen4);
        addLevelCheck(79);
        addQuestCompletedCheck(197);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("ssq_embryo");
        int npcId = npc.getNpcId();
        if (npcId == priest_wood) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("ssq_embryo", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "priest_wood_q0198_04.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState >= 1 && GetMemoState < 3) {
                    InstantZone_Enter(st.getPlayer());
                    htmltext = "priest_wood_q0198_06.htm";
                }
            }
        } else if (npcId == inzone_frantz) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "inzone_frantz_q0198_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1)
                    htmltext = "inzone_frantz_q0198_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1)
                    htmltext = "inzone_frantz_q0198_04.htm";
            } else if (event.equalsIgnoreCase("reply_4")) {
                if (av_quest0 == 1)
                    htmltext = "inzone_frantz_q0198_05a.htm";
                else {
                    htmltext = "inzone_frantz_q0198_05.htm";
                    if (GetMemoState == 1) {
                        Functions.npcSay(npc, NpcString.S1_THAT_STRANGER_MUST_BE_DEFEATED_HERE_IS_THE_ULTIMATE_HELP, st.getPlayer().getName());
                        NpcInstance shilen6 = st.getPlayer().getReflection().addSpawnWithoutRespawn(evil_of_shilen6, new Location(-23734, -9184, -5384), 0);
                        Functions.npcSay(shilen6, NpcString.YOU_ARE_NOT_THE_OWNER_OF_THAT_ITEM);
                        NpcInstance shilen12 = st.getPlayer().getReflection().addSpawnWithoutRespawn(evil_of_shilen12, new Location(-23734, -9184, -5384), 0);
                        NpcInstance shilen15 = st.getPlayer().getReflection().addSpawnWithoutRespawn(evil_of_shilen15, new Location(-23734, -9184, -5384), 0);
                        ThreadPoolManager.getInstance().schedule(new Runnable() {
                            @Override
                            public void run() {
                                shilen6.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 2000);
                                shilen12.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 2000);
                                shilen15.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.getPlayer(), 2000);
                            }
                        }, 2000L);
                        av_quest0++;
                    }
                }
            } else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 2) {
                    if (st.ownItemCount(q_statue_of_shilen4) >= 1)
                        htmltext = "inzone_frantz_q0198_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 2) {
                    if (st.ownItemCount(q_statue_of_shilen4) >= 1)
                        htmltext = "inzone_frantz_q0198_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (GetMemoState == 2) {
                    if (st.ownItemCount(q_statue_of_shilen4) >= 1)
                        htmltext = "inzone_frantz_q0198_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8")) {
                if (GetMemoState == 2) {
                    if (st.ownItemCount(q_statue_of_shilen4) >= 1) {
                        st.setCond(3);
                        st.setMemoState("ssq_embryo", String.valueOf(3), true);
                        st.takeItems(q_statue_of_shilen4, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "inzone_frantz_q0198_12.htm";
                        Functions.npcSay(npc, NpcString.WE_WILL_BE_WITH_YOU_ALWAYS);
                    }
                }
            }
        } else if (event.equalsIgnoreCase("9989")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        } else if (event.equalsIgnoreCase("9990")) {
            if (npc != null)
                npc.deleteMe();
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("ssq_embryo");
        int talker_level = st.getPlayer().getLevel();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == priest_wood) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "priest_wood_q0198_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "priest_wood_q0198_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == priest_wood) {
                    if (GetMemoState >= 1 && GetMemoState < 3)
                        htmltext = "priest_wood_q0198_05.htm";
                    else if (GetMemoState == 3) {
                        if (talker_level >= 79) {
                            st.addExpAndSp(315108090, 34906059);
                            st.giveItems(adena_of_ancient, 1500000);
                            st.giveItems(ssq_bracelet_of_dawn, 1);
                            st.removeMemo("ssq_embryo");
                            st.soundEffect(SOUND_FINISH);
                            st.exitQuest(false);
                            htmltext = "priest_wood_q0198_07.htm";
                        } else
                            htmltext = "level_check_q0192_01.htm";
                    }
                } else if (npcId == inzone_frantz) {
                    if (GetMemoState == 1)
                        htmltext = "inzone_frantz_q0198_01.htm";
                    else if (GetMemoState == 2) {
                        if (st.ownItemCount(q_statue_of_shilen4) >= 1)
                            htmltext = "inzone_frantz_q0198_08.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "inzone_frantz_q0198_13.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        NpcInstance frantz = GameObjectsStorage.getByNpcId(inzone_frantz);
        NpcInstance shilen12 = GameObjectsStorage.getByNpcId(evil_of_shilen12);
        NpcInstance shilen15 = GameObjectsStorage.getByNpcId(evil_of_shilen15);
        int npcId = npc.getNpcId();
        if (npcId == evil_of_shilen6) {
            st.setCond(2);
            st.setMemoState("ssq_embryo", String.valueOf(2), true);
            st.giveItems(q_statue_of_shilen4, 1);
            st.soundEffect(SOUND_MIDDLE);
            Functions.npcSay(frantz, NpcString.WELL_DONE_S1_YOUR_HELP_IS_MUCH_APPRECIATED, st.getPlayer().getName());
            Functions.npcSay(npc, NpcString.S1_YOU_MAY_HAVE_WON_THIS_TIME_BUT_NEXT_TIME_I_WILL_SURELY_CAPTURE_YOU, st.getPlayer().getName());
            st.startQuestTimer("9989", 2000, shilen12);
            st.startQuestTimer("9990", 2000, shilen15);
            st.getPlayer().showQuestMovie(ExStartScenePlayer.SCENE_SSQ_EMBRYO);
            av_quest0 = 0;
        }
        return null;
    }

    private void InstantZone_Enter(Player player) {
        ReflectionUtils.simpleEnterInstancedZone(player, inzone_id);
    }
}