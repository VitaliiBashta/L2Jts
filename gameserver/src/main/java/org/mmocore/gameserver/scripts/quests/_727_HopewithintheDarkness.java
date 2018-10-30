package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.model.entity.residence.Castle;
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
public class _727_HopewithintheDarkness extends Quest {
    // ITEMS
    private static int KnightsEpaulette = 9912;

    // MOB's
    private static int KanadisGuide3 = 25661;

    public _727_HopewithintheDarkness() {
        super(true);

        addStartNpc(36403, 36404, 36405, 36406, 36407, 36408, 36409, 36410, 36411);
        addKillId(KanadisGuide3);

        addLevelCheck(80);
        addQuestCompletedCheck(726);
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

        if (event.equals("dcw_q727_4.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equals("reward") && cond == 1 && player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_727).equalsIgnoreCase("done")) {
            player.getPlayerVariables().remove(PlayerVariables.QUEST_STATE_OF_727);
            st.giveItems(KnightsEpaulette, 159);
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
            return "dcw_q727_1a.htm";
        } else if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    st.exitQuest(true);
                    htmltext = "dcw_q727_0.htm";
                    break;
                case QUEST:
                    st.exitQuest(true);
                    htmltext = "dcw_q727_1b.htm";
                    break;
                default:
                    htmltext = "dcw_q727_1.htm";
                    break;
            }
        } else if (cond == 1) {
            if (player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_727) != null
                    && player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_727).equalsIgnoreCase("done")) {
                htmltext = "dcw_q727_6.htm";
            } else {
                htmltext = "dcw_q727_5.htm";
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
                        member.getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_727, "done", -1);
                    }
                }
            }
            player.getReflection().startCollapseTimer(5 * 60 * 1000L);
        }
        return null;
    }

    private boolean check(Player player) {
        Castle castle = ResidenceHolder.getInstance().getResidenceByObject(Castle.class, player);
        if (castle == null) {
            return false;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }
        if (clan.getClanId() != castle.getOwnerId()) {
            return false;
        }
        return true;
    }
}