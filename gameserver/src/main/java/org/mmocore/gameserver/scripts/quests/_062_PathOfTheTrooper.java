package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
 * @version 1.1
 * @date 04/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _062_PathOfTheTrooper extends Quest {
    // npc
    private final static int master_tbwain = 32197;
    private final static int master_shubain = 32194;
    // mobs
    private final static int felim_lizardman_warrior = 20014;
    private final static int poison_spider = 20038;
    private final static int tumran_bugbear = 20062;
    // questitem
    private final static int q_ol_mahum_rk_head = 9749;
    private final static int q_psn_spider_leg = 9750;
    private final static int q_tmrn_bugbear_heart = 9751;
    private final static int q_recom_of_shubain = 9752;
    private final static int q_recom_of_tbwain = 9753;

    public _062_PathOfTheTrooper() {
        super(false);
        addStartNpc(master_tbwain);
        addTalkId(master_shubain);
        addKillId(felim_lizardman_warrior, poison_spider, tumran_bugbear);
        addQuestItem(q_ol_mahum_rk_head, q_psn_spider_leg, q_tmrn_bugbear_heart, q_recom_of_shubain);
        addLevelCheck(18);
        addClassIdCheck(ClassId.kamael_m_soldier);
        addRaceCheck(PlayerRace.kamael);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("road_to_trooper");
        int npcId = npc.getNpcId();
        if (npcId == master_tbwain) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("road_to_trooper", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "master_tbwain_q0062_06.htm";
            }
        } else if (npcId == master_shubain) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("road_to_trooper", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "master_shubain_q0062_02.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("road_to_trooper");
        int talker_level = st.getPlayer().getLevel();
        int GetOneTimeQuestFlag = st.getPlayer().getPlayerVariables().getInt(PlayerVariables.profession_145);
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == master_tbwain) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "master_tbwain_q0062_02.htm";
                            st.exitQuest(true);
                            break;
                        case CLASS_ID:
                            htmltext = "master_tbwain_q0062_03.htm";
                            st.exitQuest(true);
                            break;
                        case RACE:
                            htmltext = "master_tbwain_q0062_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "master_tbwain_q0062_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == master_tbwain) {
                    if (GetMemoState == 1 || GetMemoState == 2 || GetMemoState == 3)
                        htmltext = "master_tbwain_q0062_07.htm";
                    else if (GetMemoState == 4) {
                        st.setCond(5);
                        st.setMemoState("road_to_trooper", String.valueOf(5), true);
                        st.takeItems(q_recom_of_shubain, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_tbwain_q0062_08.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_tmrn_bugbear_heart) == 0)
                        htmltext = "master_tbwain_q0062_09.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_tmrn_bugbear_heart) >= 1) {
                        st.takeItems(q_tmrn_bugbear_heart, -1);
                        st.giveItems(q_recom_of_tbwain, 1);
                        htmltext = "master_tbwain_q0062_10.htm";
                        if (GetOneTimeQuestFlag == 0) {
                            st.getPlayer().getPlayerVariables().set(PlayerVariables.profession_145, String.valueOf(1), -1);
                            if (talker_level >= 20)
                                st.addExpAndSp(320534, 20848);
                            else if (talker_level == 19)
                                st.addExpAndSp(456128, 27546);
                            else
                                st.addExpAndSp(591724, 34244);
                            st.giveItems(ADENA_ID, 163800);
                        }
                        st.removeMemo("road_to_trooper");
                        st.getPlayer().sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                    }
                }
                if (npcId == master_shubain) {
                    if (GetMemoState == 1)
                        htmltext = "master_shubain_q0062_01.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_ol_mahum_rk_head) < 5)
                        htmltext = "master_shubain_q0062_03.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_ol_mahum_rk_head) >= 5) {
                        st.setCond(3);
                        st.setMemoState("road_to_trooper", String.valueOf(3), true);
                        st.takeItems(q_ol_mahum_rk_head, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_shubain_q0062_04.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(q_psn_spider_leg) < 10)
                        htmltext = "master_shubain_q0062_05.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_psn_spider_leg) >= 10) {
                        st.setCond(4);
                        st.setMemoState("road_to_trooper", String.valueOf(4), true);
                        st.takeItems(q_psn_spider_leg, -1);
                        st.giveItems(q_recom_of_shubain, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "master_shubain_q0062_06.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "master_shubain_q0062_07.htm";
                }
                break;
            case COMPLETED:
                if (npcId == master_tbwain)
                    htmltext = "master_tbwain_q0062_05.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("road_to_trooper");
        int npcId = npc.getNpcId();
        if (npcId == felim_lizardman_warrior) {
            if (GetMemoState == 2 && st.ownItemCount(q_ol_mahum_rk_head) < 5) {
                if (st.ownItemCount(q_ol_mahum_rk_head) < 5) {
                    st.giveItems(q_ol_mahum_rk_head, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == poison_spider) {
            if (GetMemoState == 3 && st.ownItemCount(q_psn_spider_leg) < 10) {
                if (st.ownItemCount(q_psn_spider_leg) < 10) {
                    st.giveItems(q_psn_spider_leg, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == tumran_bugbear) {
            int i4 = Rnd.get(1000);
            if (GetMemoState == 5 && st.ownItemCount(q_tmrn_bugbear_heart) < 1) {
                if (i4 < 500 && st.ownItemCount(q_tmrn_bugbear_heart) < 1) {
                    st.giveItems(q_tmrn_bugbear_heart, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}