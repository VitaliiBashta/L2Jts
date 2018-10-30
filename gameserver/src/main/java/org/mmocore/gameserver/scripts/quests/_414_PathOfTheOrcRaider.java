package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 04/10/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _414_PathOfTheOrcRaider extends Quest {
    // npc
    private final static int prefect_karukia = 30570;
    private final static int prefect_kasman = 30501;
    private final static int prefect_tazar = 31978;
    // mobs
    private final static int goblin_tomb_raider = 20320;
    private final static int kuruka_ratman_leader = 27045;
    private final static int orc_betrayer_umbar = 27054;
    private final static int orc_betrayer_timora = 27320;
    // questitem
    private final static int green_blood = 1578;
    private final static int goblin_dwelling_map = 1579;
    private final static int kuruka_ratman_tooth = 1580;
    private final static int betrayer_sue_report = 1581;
    private final static int betrayer_wanuk_report = 1582;
    private final static int betrayer_chewba_report = 1583;
    private final static int betrayer_heitafu_report = 1584;
    private final static int betrayer_picubo_report = 1585;
    private final static int betrayer_bumbum_report = 1586;
    private final static int betrayer_minsku_report = 1587;
    private final static int betrayer_chuchu_report = 1588;
    private final static int betrayer_umbar_report = 1589;
    private final static int betrayer_zakan_report = 1590;
    private final static int head_of_betrayer = 1591;
    private final static int q_head_of_timora = 8544;
    private final static int mark_of_raider = 1592;

    public _414_PathOfTheOrcRaider() {
        super(false);
        addStartNpc(prefect_karukia);
        addTalkId(prefect_kasman, prefect_tazar);
        addKillId(goblin_tomb_raider, kuruka_ratman_leader, orc_betrayer_umbar, orc_betrayer_timora);
        addQuestItem(green_blood, goblin_dwelling_map, kuruka_ratman_tooth, betrayer_umbar_report, head_of_betrayer, q_head_of_timora);
        addLevelCheck(18);
        addClassIdCheck(ClassId.orc_fighter);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("path_to_orc_raider");
        int npcId = npc.getNpcId();
        int talker_occupation = st.getPlayer().getPlayerClassComponent().getClassId().getId();
        int orc_fighter = 0x2c;
        int orc_raider = 0x2d;
        if (npcId == prefect_karukia) {
            if (event.equalsIgnoreCase("quest_accept")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "prefect_karukia_q0414_02.htm";
                        st.exitQuest(true);
                        break;
                    case CLASS_ID:
                        if (talker_occupation != orc_fighter) {
                            if (talker_occupation == orc_raider) {
                                htmltext = "prefect_karukia_q0414_02a.htm";
                                st.exitQuest(true);
                            } else {
                                htmltext = "prefect_karukia_q0414_03.htm";
                                st.exitQuest(true);
                            }
                        }
                        break;
                    default:
                        if (st.ownItemCount(mark_of_raider) == 1 && talker_occupation == orc_fighter)
                            htmltext = "prefect_karukia_q0414_04.htm";
                        else {
                            st.setCond(1);
                            st.setState(STARTED);
                            st.soundEffect(SOUND_ACCEPT);
                            st.giveItems(goblin_dwelling_map, 1);
                            htmltext = "prefect_karukia_q0414_05.htm";
                        }
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_1") && st.ownItemCount(goblin_dwelling_map) == 1 && st.ownItemCount(kuruka_ratman_tooth) >= 10) {
                st.setCond(3);
                st.takeItems(kuruka_ratman_tooth, -1);
                st.takeItems(goblin_dwelling_map, 1);
                st.giveItems(betrayer_umbar_report, 1);
                st.giveItems(betrayer_zakan_report, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "prefect_karukia_q0414_07a.htm";
            } else if (event.equalsIgnoreCase("reply_2") && st.ownItemCount(goblin_dwelling_map) == 1 && st.ownItemCount(kuruka_ratman_tooth) >= 10) {
                st.setCond(5);
                st.setMemoState("path_to_orc_raider", String.valueOf(2), true);
                st.takeItems(kuruka_ratman_tooth, -1);
                st.takeItems(goblin_dwelling_map, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "prefect_karukia_q0414_07b.htm";
            }
        } else if (npcId == prefect_tazar) {
            if (event.equalsIgnoreCase("reply_3") && GetMemoState == 2)
                htmltext = "prefect_tazar_q0414_01b.htm";
            else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 2) {
                st.setCond(6);
                st.setMemoState("path_to_orc_raider", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "prefect_tazar_q0414_02.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("path_to_orc_raider");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == prefect_karukia)
                    htmltext = "prefect_karukia_q0414_01.htm";
                break;
            case STARTED:
                if (npcId == prefect_karukia) {
                    if (st.ownItemCount(goblin_dwelling_map) == 1 && st.ownItemCount(kuruka_ratman_tooth) < 10)
                        htmltext = "prefect_karukia_q0414_06.htm";
                    else if (st.ownItemCount(goblin_dwelling_map) == 1 && st.ownItemCount(kuruka_ratman_tooth) >= 10 && st.ownItemCount(betrayer_sue_report) + st.ownItemCount(betrayer_chewba_report) + st.ownItemCount(betrayer_wanuk_report) + st.ownItemCount(betrayer_heitafu_report) + st.ownItemCount(betrayer_picubo_report) + st.ownItemCount(betrayer_bumbum_report) + st.ownItemCount(betrayer_minsku_report) + st.ownItemCount(betrayer_chuchu_report) + st.ownItemCount(betrayer_umbar_report) + st.ownItemCount(betrayer_zakan_report) == 0)
                        htmltext = "prefect_karukia_q0414_07.htm";
                    else if ((st.ownItemCount(betrayer_sue_report) + st.ownItemCount(betrayer_chewba_report) + st.ownItemCount(betrayer_wanuk_report) + st.ownItemCount(betrayer_heitafu_report) + st.ownItemCount(betrayer_picubo_report) + st.ownItemCount(betrayer_bumbum_report) + st.ownItemCount(betrayer_minsku_report) + st.ownItemCount(betrayer_chuchu_report) + st.ownItemCount(betrayer_umbar_report) + st.ownItemCount(betrayer_zakan_report) > 0) || st.ownItemCount(head_of_betrayer) > 0)
                        htmltext = "prefect_karukia_q0414_08.htm";
                    else if (GetMemoState == 2)
                        htmltext = "prefect_karukia_q0414_07b.htm";
                } else if (npcId == prefect_kasman) {
                    if (st.ownItemCount(betrayer_sue_report) + st.ownItemCount(betrayer_chewba_report) + st.ownItemCount(betrayer_wanuk_report) + st.ownItemCount(betrayer_heitafu_report) + st.ownItemCount(betrayer_picubo_report) + st.ownItemCount(betrayer_bumbum_report) + st.ownItemCount(betrayer_minsku_report) + st.ownItemCount(betrayer_chuchu_report) + st.ownItemCount(betrayer_umbar_report) + st.ownItemCount(betrayer_zakan_report) >= 2 && st.ownItemCount(head_of_betrayer) == 0)
                        htmltext = "prefect_kasman_q0414_01.htm";
                    else if (st.ownItemCount(head_of_betrayer) == 1)
                        htmltext = "prefect_kasman_q0414_02.htm";
                    else if (st.ownItemCount(head_of_betrayer) >= 2) {
                        st.giveItems(mark_of_raider, 1);
                        htmltext = "prefect_kasman_q0414_03.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 21312);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 28010);
                            else
                                st.addExpAndSp(591724, 34708);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.removeMemo("path_to_orc_raider");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                } else if (npcId == prefect_tazar) {
                    if (GetMemoState == 2)
                        htmltext = "prefect_tazar_q0414_01.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_head_of_timora) == 0)
                        htmltext = "prefect_tazar_q0414_03.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_head_of_timora) > 0) {
                        st.giveItems(mark_of_raider, 1);
                        st.takeItems(q_head_of_timora, -1);
                        htmltext = "prefect_tazar_q0414_05.htm";
                        if (!st.getPlayer().getPlayerVariables().getBoolean(PlayerVariables.profession_145)) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, "1", -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(160267, 10656);
                            else if (talker_level == 19)
                                st.addExpAndSp(228064, 14005);
                            else
                                st.addExpAndSp(295862, 17354);
                            st.giveItems(ADENA_ID, 81900);
                        }
                        st.removeMemo("path_to_orc_raider");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("path_to_orc_raider");
        int npcId = npc.getNpcId();
        if (npcId == goblin_tomb_raider) {
            if (st.ownItemCount(goblin_dwelling_map) == 1 && st.ownItemCount(kuruka_ratman_tooth) < 10 && st.ownItemCount(green_blood) <= 20) {
                if (Rnd.get(100) < st.ownItemCount(green_blood) * 5) {
                    st.takeItems(green_blood, -1);
                    st.addSpawn(kuruka_ratman_leader, npc.getX(), npc.getY(), npc.getZ());
                } else {
                    st.giveItems(green_blood, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == kuruka_ratman_leader) {
            if (st.ownItemCount(goblin_dwelling_map) == 1 && st.ownItemCount(kuruka_ratman_tooth) < 10) {
                st.takeItems(green_blood, -1);
                if (st.ownItemCount(kuruka_ratman_tooth) >= 10) {
                    st.setCond(2);
                    st.giveItems(kuruka_ratman_tooth, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else {
                    st.giveItems(kuruka_ratman_tooth, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == orc_betrayer_umbar) {
            if (st.ownItemCount(betrayer_sue_report) + st.ownItemCount(betrayer_chewba_report) + st.ownItemCount(betrayer_wanuk_report) + st.ownItemCount(betrayer_heitafu_report) + st.ownItemCount(betrayer_picubo_report) + st.ownItemCount(betrayer_bumbum_report) + st.ownItemCount(betrayer_minsku_report) + st.ownItemCount(betrayer_chuchu_report) + st.ownItemCount(betrayer_umbar_report) + st.ownItemCount(betrayer_zakan_report) > 0 && st.ownItemCount(head_of_betrayer) < 2 && Rnd.get(10) < 2) {
                st.giveItems(head_of_betrayer, 1);
                if (st.ownItemCount(betrayer_zakan_report) == 1)
                    st.takeItems(betrayer_zakan_report, 1);
                else if (st.ownItemCount(betrayer_umbar_report) == 1)
                    st.takeItems(betrayer_umbar_report, 1);
                else if (st.ownItemCount(betrayer_chuchu_report) == 1)
                    st.takeItems(betrayer_chuchu_report, 1);
                else if (st.ownItemCount(betrayer_minsku_report) == 1)
                    st.takeItems(betrayer_minsku_report, 1);
                else if (st.ownItemCount(betrayer_bumbum_report) == 1)
                    st.takeItems(betrayer_bumbum_report, 1);
                else if (st.ownItemCount(betrayer_picubo_report) == 1)
                    st.takeItems(betrayer_picubo_report, 1);
                else if (st.ownItemCount(betrayer_heitafu_report) == 1)
                    st.takeItems(betrayer_heitafu_report, 1);
                else if (st.ownItemCount(betrayer_wanuk_report) == 1)
                    st.takeItems(betrayer_wanuk_report, 1);
                else if (st.ownItemCount(betrayer_chewba_report) == 1)
                    st.takeItems(betrayer_chewba_report, 1);
                else if (st.ownItemCount(betrayer_sue_report) == 1)
                    st.takeItems(betrayer_sue_report, 1);
                if (st.ownItemCount(head_of_betrayer) >= 2) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == orc_betrayer_timora) {
            if (GetMemoState == 3 && st.ownItemCount(q_head_of_timora) == 0) {
                int i0 = Rnd.get(100);
                if (i0 < 60) {
                    st.setCond(7);
                    st.giveItems(q_head_of_timora, 1);
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        }
        return null;
    }
}