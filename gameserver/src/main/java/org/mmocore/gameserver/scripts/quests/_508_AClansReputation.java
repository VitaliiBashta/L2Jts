package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 17/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _508_AClansReputation extends Quest {
    // npc
    private static final int sir_eric_rodemai = 30868;
    // mobs
    private static final int rahha = 25051;
    private static final int last_lesser_glaki = 25245;
    private static final int palibati_queen_themis = 25252;
    private static final int gargoyle_lord_tiphon = 25255;
    private static final int priest_hisilrome = 25478;
    private static final int flame_stone_golem = 25524;
    // questitem
    private static final int q_center_of_flmgolem = 8494;
    private static final int q_heart_of_hisilrome = 14883;
    private static final int q_fragment_of_tiphon = 8280;
    private static final int q_scale_of_themis = 8277;
    private static final int q_center_of_hekaton = 8279;
    private static final int q_center_of_glaki = 8281;
    private static final int q_fang_of_rahha = 8282;

    public _508_AClansReputation() {
        super(true);
        addStartNpc(sir_eric_rodemai);
        addKillId(rahha, last_lesser_glaki, palibati_queen_themis, gargoyle_lord_tiphon, priest_hisilrome, flame_stone_golem);
        addQuestItem(q_center_of_flmgolem, q_heart_of_hisilrome, q_fragment_of_tiphon, q_scale_of_themis, q_center_of_glaki, q_fang_of_rahha, q_center_of_hekaton);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("pledge_gain_fame");
        int npcId = npc.getNpcId();
        if (npcId == sir_eric_rodemai) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sir_eric_rodemai_q0508_04.htm";
            } else if (event.equalsIgnoreCase("reply=100"))
                htmltext = "sir_eric_rodemai_q0508_06.htm";
            else if (event.equalsIgnoreCase("reply=101")) {
                st.removeMemo("pledge_gain_fame");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "sir_eric_rodemai_q0508_07.htm";
            } else if (event.equalsIgnoreCase("reply=102")) {
                st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                htmltext = "sir_eric_rodemai_q0508_08.htm";
            } else if (event.equalsIgnoreCase("reply=110"))
                htmltext = "sir_eric_rodemai_q0508_01a.htm";
            else if (event.equalsIgnoreCase("reply=2")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_gain_fame", String.valueOf(2), true);
                    htmltext = "sir_eric_rodemai_q0508_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply=4")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_gain_fame", String.valueOf(4), true);
                    htmltext = "sir_eric_rodemai_q0508_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply=5")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_gain_fame", String.valueOf(5), true);
                    htmltext = "sir_eric_rodemai_q0508_13.htm";
                }
            } else if (event.equalsIgnoreCase("reply=6")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_gain_fame", String.valueOf(6), true);
                    htmltext = "sir_eric_rodemai_q0508_14.htm";
                }
            } else if (event.equalsIgnoreCase("reply=7")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_gain_fame", String.valueOf(7), true);
                    htmltext = "sir_eric_rodemai_q0508_15.htm";
                }
            } else if (event.equalsIgnoreCase("reply=8")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_gain_fame", String.valueOf(8), true);
                    htmltext = "sir_eric_rodemai_q0508_15a.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("pledge_gain_fame");
        Clan clan = st.getPlayer().getClan();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sir_eric_rodemai) {
                    if (clan != null && clan.getLeader().getPlayer() == st.getPlayer()) {
                        if (clan.getLevel() >= 5)
                            htmltext = "sir_eric_rodemai_q0508_01.htm";
                        else {
                            htmltext = "sir_eric_rodemai_q0508_02.htm";
                            st.exitQuest(true);
                        }
                    } else {
                        htmltext = "sir_eric_rodemai_q0508_03.htm";
                        st.exitQuest(true);
                    }
                }
                break;
            case STARTED:
                if (npcId == sir_eric_rodemai) {
                    if (GetMemoState == 0)
                        htmltext = "sir_eric_rodemai_q0508_05.htm";
                    else if (clan.getLeader().getPlayer() != st.getPlayer()) {
                        st.removeMemo("pledge_gain_fame");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "sir_eric_rodemai_q0508_05a.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_scale_of_themis) == 0)
                        htmltext = "sir_eric_rodemai_q0508_18.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_scale_of_themis) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(280, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_scale_of_themis, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_19.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_heart_of_hisilrome) == 0)
                        htmltext = "sir_eric_rodemai_q0508_22.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_heart_of_hisilrome) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(292, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_heart_of_hisilrome, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_23a.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_center_of_hekaton) >= 1) // FIXME: нужно ли?
                    {
                        int UpdatePledgeNameValue = clan.incReputation(309, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_center_of_hekaton, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_23.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_fragment_of_tiphon) == 0)
                        htmltext = "sir_eric_rodemai_q0508_24.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_fragment_of_tiphon) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(301, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_fragment_of_tiphon, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_25.htm";
                    } else if (GetMemoState == 6 && st.ownItemCount(q_center_of_glaki) == 0)
                        htmltext = "sir_eric_rodemai_q0508_26.htm";
                    else if (GetMemoState == 6 && st.ownItemCount(q_center_of_glaki) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(392, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_center_of_glaki, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_27.htm";
                    } else if (GetMemoState == 7 && st.ownItemCount(q_fang_of_rahha) == 0)
                        htmltext = "sir_eric_rodemai_q0508_28.htm";
                    else if (GetMemoState == 7 && st.ownItemCount(q_fang_of_rahha) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(279, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_fang_of_rahha, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_29.htm";
                    } else if (GetMemoState == 8 && st.ownItemCount(q_center_of_flmgolem) == 0)
                        htmltext = "sir_eric_rodemai_q0508_30.htm";
                    else if (GetMemoState == 8 && st.ownItemCount(q_center_of_flmgolem) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(384, false, "_508_TheClansReputation");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_center_of_flmgolem, 1);
                        st.setMemoState("pledge_gain_fame", String.valueOf(0), true);
                        htmltext = "sir_eric_rodemai_q0508_31.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        Player clan_leader;
        try {
            clan_leader = st.getPlayer().getClan().getLeader().getPlayer();
        } catch (Exception E) {
            return null;
        }
        if (clan_leader == null) {
            return null;
        }
        if (!st.getPlayer().equals(clan_leader) && clan_leader.getDistance(npc) > AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) {
            return null;
        }
        QuestState qs = clan_leader.getQuestState(getId());
        if (qs == null || !qs.isStarted() || qs.getCond() != 1) {
            return null;
        }
        int GetMemoState = st.getInt("pledge_gain_fame");
        int npcId = npc.getNpcId();
        if (npcId == rahha) {
            if (GetMemoState == 7 && st.ownItemCount(q_fang_of_rahha) == 0) {
                st.giveItems(q_fang_of_rahha, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == last_lesser_glaki) {
            if (GetMemoState == 6 && st.ownItemCount(q_center_of_glaki) == 0) {
                st.giveItems(q_center_of_glaki, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == palibati_queen_themis) {
            if (GetMemoState == 2 && st.ownItemCount(q_scale_of_themis) == 0) {
                st.giveItems(q_scale_of_themis, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == gargoyle_lord_tiphon) {
            if (GetMemoState == 5 && st.ownItemCount(q_fragment_of_tiphon) == 0) {
                st.giveItems(q_fragment_of_tiphon, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == priest_hisilrome) {
            if (GetMemoState == 4 && st.ownItemCount(q_heart_of_hisilrome) == 0) {
                st.giveItems(q_heart_of_hisilrome, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == flame_stone_golem) {
            if (GetMemoState == 8 && st.ownItemCount(q_center_of_flmgolem) == 0) {
                st.giveItems(q_center_of_flmgolem, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}