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
public class _510_AClansPrestige extends Quest {
    // npc
    private static final int grandmagister_valdis = 31331;
    // mobs
    private static final int tyrannosaurus = 22215;
    private static final int tyrannosaurus_soul = 22216;
    private static final int tyrannosaurus_s = 22217;
    // questitem
    private static final int q_claw_of_tyranno = 8767;

    public _510_AClansPrestige() {
        super(true);
        addStartNpc(grandmagister_valdis);
        addKillId(tyrannosaurus, tyrannosaurus_soul, tyrannosaurus_s);
        addQuestItem(q_claw_of_tyranno);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == grandmagister_valdis) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("pledge_get_reputation", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "grandmagister_valdis_q0510_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "grandmagister_valdis_q0510_02.htm";
            else if (event.equalsIgnoreCase("reply=2"))
                htmltext = "grandmagister_valdis_q0510_08.htm";
            else if (event.equalsIgnoreCase("reply=3")) {
                st.removeMemo("pledge_get_reputation");
                st.takeItems(q_claw_of_tyranno, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "grandmagister_valdis_q0510_09.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("pledge_get_reputation");
        Clan clan = st.getPlayer().getClan();
        int npcId = npc.getNpcId();
        int id = st.getState();
        long i0;
        switch (id) {
            case CREATED:
                if (npcId == grandmagister_valdis) {
                    if (clan != null && clan.getLeader().getPlayer() == st.getPlayer()) {
                        if (clan.getLevel() >= 5)
                            htmltext = "grandmagister_valdis_q0510_01.htm";
                        else {
                            htmltext = "grandmagister_valdis_q0510_04.htm";
                            st.exitQuest(true);
                        }
                    } else {
                        htmltext = "grandmagister_valdis_q0510_03.htm";
                        st.exitQuest(true);
                    }
                }
                break;
            case STARTED:
                if (npcId == grandmagister_valdis) {
                    if (GetMemoState == 1 && st.ownItemCount(q_claw_of_tyranno) == 0)
                        htmltext = "grandmagister_valdis_q0510_06.htm";
                    else if (clan.getLeader().getPlayer() != st.getPlayer()) {
                        st.removeMemo("pledge_get_reputation");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(true);
                        htmltext = "grandmagister_valdis_q0510_07a.htm";
                    } else if (GetMemoState == 1 && st.ownItemCount(q_claw_of_tyranno) >= 1) {
                        if (st.ownItemCount(q_claw_of_tyranno) < 10)
                            i0 = 15 * st.ownItemCount(q_claw_of_tyranno);
                        else
                            i0 = 59 + 15 * st.ownItemCount(q_claw_of_tyranno);
                        int UpdatePledgeNameValue = clan.incReputation((int) i0, false, "_510_AClansPrestige");
                        st.getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_SUCCESSFULLY_COMPLETED_A_CLAN_QUEST_S1_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE).addNumber(UpdatePledgeNameValue));
                        st.soundEffect(SOUND_FANFARE1);
                        st.takeItems(q_claw_of_tyranno, -1);
                        htmltext = "grandmagister_valdis_q0510_07.htm";
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
        int GetMemoState = st.getInt("pledge_get_reputation");
        int npcId = npc.getNpcId();
        if (npcId == tyrannosaurus || npcId == tyrannosaurus_soul || npcId == tyrannosaurus_s) {
            if (GetMemoState == 1) {
                st.giveItems(q_claw_of_tyranno, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
