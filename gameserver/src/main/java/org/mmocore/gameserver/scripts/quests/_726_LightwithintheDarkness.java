package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author: pchayka
 * @date: 26.09.2010
 */
public class _726_LightwithintheDarkness extends Quest {

    // MOB's
    private static final int KanadisGuide3 = 25661;

    public _726_LightwithintheDarkness() {
        super(true);

        addStartNpc(35666, 35698, 35735, 35767, 35804, 35835, 35867, 35904, 35936, 35974, 36011, 36043, 36081, 36118, 36149, 36181, 36219, 36257, 36294, 36326, 36364);
        addKillId(KanadisGuide3);

        addLevelCheck(70);
        addQuestCompletedCheck(727);
    }

    private static boolean checkAllDestroyed(int mobId, int refId) {
        for (NpcInstance npc : GameObjectsStorage.getAllByNpcId(mobId, true)) {
            if (npc.getReflectionId() == refId) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;
        Player player = st.getPlayer();

        if (event.equals("dcw_q726_4.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("reward") && cond == 1 && player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_726).equalsIgnoreCase("done")) {
            player.getPlayerVariables().remove(PlayerVariables.QUEST_STATE_OF_726);
            // ITEMS
            int knightsEpaulette = 9912;
            st.giveItems(knightsEpaulette, 152);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
            return null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        Player player = st.getPlayer();

        if (!check(st.getPlayer())) {
            st.exitQuest(true);
            return "dcw_q726_1a.htm";
        } else if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    st.exitQuest(true);
                    htmltext = "dcw_q726_0.htm";
                    break;
                case QUEST:
                    st.exitQuest(true);
                    htmltext = "dcw_q726_1b.htm";
                    break;
                default:
                    htmltext = "dcw_q726_1.htm";
                    break;
            }
        } else if (cond == 1) {
            if (player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_726) != null
                    && player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_726).equalsIgnoreCase("done")) {
                htmltext = "dcw_q726_6.htm";
            } else {
                htmltext = "dcw_q726_5.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.getPlayer();
        Party party = player.getParty();

        if (cond == 1 && npcId == KanadisGuide3 && checkAllDestroyed(KanadisGuide3, player.getReflectionId())) {
            if (player.isInParty()) {
                for (Player member : party.getPartyMembers()) {
                    if (!member.isDead() && member.getParty().isInReflection()) {
                        member.sendPacket(new SystemMessage(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(5));
                        member.getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_726, "done", -1);
                    }
                }
            }
            player.getReflection().startCollapseTimer(5 * 60 * 1000L);
        }
        return null;
    }

    private boolean check(Player player) {
        Fortress fort = ResidenceHolder.getInstance().getResidenceByObject(Fortress.class, player);
        if (fort == null) {
            return false;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }
        if (clan.getClanId() != fort.getOwnerId()) {
            return false;
        }
        if (fort.getContractState() != 1) {
            return false;
        }
        return true;
    }
}