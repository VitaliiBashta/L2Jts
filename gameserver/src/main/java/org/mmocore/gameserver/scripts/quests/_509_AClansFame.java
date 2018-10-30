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
public class _509_AClansFame extends Quest {
    // npc
    private static final int grandmagister_valdis = 31331;
    // mobs
    private static final int degeneration_golem = 25523;
    private static final int spike_stakato_qn_shyid = 25671;
    private static final int demonic_agent_falston = 25322;
    private static final int geyser_guardian_hestia = 25293;
    private static final int daemon_of_hundred_eyes = 25290;
    // questitem
    private static final int q_center_of_degolem = 8491;
    private static final int q_claw_of_shyid = 8493;
    private static final int q_fang_of_falston = 8492;
    private static final int q_fairystone_of_hestia = 8490;
    private static final int q_eye_of_daemon = 8489;

    public _509_AClansFame() {
        super(true);
        addStartNpc(grandmagister_valdis);
        addKillId(degeneration_golem, spike_stakato_qn_shyid, demonic_agent_falston, geyser_guardian_hestia, daemon_of_hundred_eyes);
        addQuestItem(q_center_of_degolem, q_claw_of_shyid, q_fang_of_falston, q_fairystone_of_hestia, q_eye_of_daemon);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("pledge_make_well_known");
        int npcId = npc.getNpcId();
        if (npcId == grandmagister_valdis) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "grandmagister_valdis_q0509_04.htm";
            } else if (event.equalsIgnoreCase("reply=100"))
                htmltext = "grandmagister_valdis_q0509_06.htm";
            else if (event.equalsIgnoreCase("reply=101")) {
                st.removeMemo("pledge_make_well_known");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "grandmagister_valdis_q0509_07.htm";
            } else if (event.equalsIgnoreCase("reply=102")) {
                st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                htmltext = "grandmagister_valdis_q0509_08.htm";
            } else if (event.equalsIgnoreCase("reply=110"))
                htmltext = "grandmagister_valdis_q0509_01a.htm";
            else if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_make_well_known", String.valueOf(1), true);
                    htmltext = "grandmagister_valdis_q0509_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply=2")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_make_well_known", String.valueOf(2), true);
                    htmltext = "grandmagister_valdis_q0509_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply=3")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_make_well_known", String.valueOf(3), true);
                    htmltext = "grandmagister_valdis_q0509_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply=4")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_make_well_known", String.valueOf(4), true);
                    htmltext = "grandmagister_valdis_q0509_12.htm";
                }
            } else if (event.equalsIgnoreCase("reply=5")) {
                if (GetMemoState == 0) {
                    st.setMemoState("pledge_make_well_known", String.valueOf(5), true);
                    htmltext = "grandmagister_valdis_q0509_13.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("pledge_make_well_known");
        Clan clan = st.getPlayer().getClan();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == grandmagister_valdis) {
                    if (clan != null && clan.getLeader().getPlayer() == st.getPlayer()) {
                        if (clan.getLevel() >= 6)
                            htmltext = "grandmagister_valdis_q0509_01.htm";
                        else {
                            htmltext = "grandmagister_valdis_q0509_02.htm";
                            st.exitQuest(true);
                        }
                    } else {
                        htmltext = "grandmagister_valdis_q0509_03.htm";
                        st.exitQuest(true);
                    }
                }
                break;
            case STARTED:
                if (npcId == grandmagister_valdis) {
                    if (GetMemoState == 0)
                        htmltext = "grandmagister_valdis_q0509_05.htm";
                    else if (clan.getLeader().getPlayer() != st.getPlayer()) {
                        st.removeMemo("pledge_gain_fame");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "grandmagister_valdis_q0509_05a.htm";
                    } else if (GetMemoState == 1 && st.ownItemCount(q_eye_of_daemon) == 0)
                        htmltext = "grandmagister_valdis_q0509_16.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_eye_of_daemon) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(689, false, "_509_AClansFame");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_eye_of_daemon, 1);
                        st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                        htmltext = "grandmagister_valdis_q0509_17.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_fairystone_of_hestia) == 0)
                        htmltext = "grandmagister_valdis_q0509_18.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_fairystone_of_hestia) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(689, false, "_509_AClansFame");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_fairystone_of_hestia, 1);
                        st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                        htmltext = "grandmagister_valdis_q0509_19.htm";
                    } else if (GetMemoState == 3 && st.ownItemCount(q_center_of_degolem) == 0)
                        htmltext = "grandmagister_valdis_q0509_20.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_center_of_degolem) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(535, false, "_509_AClansFame");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_center_of_degolem, 1);
                        st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                        htmltext = "grandmagister_valdis_q0509_21.htm";
                    } else if (GetMemoState == 4 && st.ownItemCount(q_fang_of_falston) == 0)
                        htmltext = "grandmagister_valdis_q0509_22.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_fang_of_falston) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(391, false, "_509_AClansFame");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_fang_of_falston, 1);
                        st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                        htmltext = "grandmagister_valdis_q0509_23.htm";
                    } else if (GetMemoState == 5 && st.ownItemCount(q_claw_of_shyid) == 0)
                        htmltext = "grandmagister_valdis_q0509_24.htm";
                    else if (GetMemoState == 5 && st.ownItemCount(q_claw_of_shyid) >= 1) {
                        int UpdatePledgeNameValue = clan.incReputation(674, false, "_509_AClansFame");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_claw_of_shyid, 1);
                        st.setMemoState("pledge_make_well_known", String.valueOf(0), true);
                        htmltext = "grandmagister_valdis_q0509_25.htm";
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
        int GetMemoState = st.getInt("pledge_make_well_known");
        int npcId = npc.getNpcId();
        if (npcId == degeneration_golem) {
            if (GetMemoState == 3 && st.ownItemCount(q_center_of_degolem) == 0) {
                st.giveItems(q_center_of_degolem, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == spike_stakato_qn_shyid) {
            if (GetMemoState == 5 && st.ownItemCount(q_claw_of_shyid) == 0) {
                st.giveItems(q_claw_of_shyid, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == demonic_agent_falston) {
            if (GetMemoState == 4 && st.ownItemCount(q_fang_of_falston) == 0) {
                st.giveItems(q_fang_of_falston, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == geyser_guardian_hestia) {
            if (GetMemoState == 2 && st.ownItemCount(q_fairystone_of_hestia) == 0) {
                st.giveItems(q_fairystone_of_hestia, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == daemon_of_hundred_eyes) {
            if (GetMemoState == 1 && st.ownItemCount(q_eye_of_daemon) == 0) {
                st.giveItems(q_eye_of_daemon, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}