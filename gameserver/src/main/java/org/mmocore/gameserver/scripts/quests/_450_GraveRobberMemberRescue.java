package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Location;

public class _450_GraveRobberMemberRescue extends Quest {
    private static final int KANEMIKA = 32650;
    private static final int WARRIOR_NPC = 32651;

    public _450_GraveRobberMemberRescue() {
        super(false);

        addStartNpc(KANEMIKA);
        addTalkId(WARRIOR_NPC);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("32650-05.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(final NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        Player player = st.getPlayer();

        int EVIDENCE_OF_MIGRATION = 14876;
        if (npcId == KANEMIKA) {
            if (id == CREATED) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "32650-00.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (!canEnter(player)) {
                            htmltext = "32650-09.htm";
                            st.exitQuest(true);
                        } else {
                            htmltext = "32650-01.htm";
                        }
                        break;
                }
            } else if (cond == 1) {
                if (st.ownItemCount(EVIDENCE_OF_MIGRATION) >= 1) {
                    htmltext = "32650-07.htm";
                } else {
                    htmltext = "32650-06.htm";
                }
            } else if (cond == 2 && st.ownItemCount(EVIDENCE_OF_MIGRATION) == 10) {
                htmltext = "32650-08.htm";
                st.giveItems(ADENA_ID, 65000);
                st.takeItems(EVIDENCE_OF_MIGRATION, -1);
                st.exitQuest(true);
                st.soundEffect(SOUND_FINISH);
                st.getPlayer().getPlayerVariables().set(PlayerVariables.QUEST_STATE_OF_450, String.valueOf(System.currentTimeMillis()), -1);
            }
        } else if (cond == 1 && npcId == WARRIOR_NPC) {
            if (Rnd.chance(50)) {
                htmltext = "32651-01.htm";
                st.giveItems(EVIDENCE_OF_MIGRATION, 1);
                st.soundEffect(SOUND_ITEMGET);
                npc.moveToLocation(new Location(npc.getX() + 200, npc.getY() + 200, npc.getZ()), 0, false);

                ThreadPoolManager.getInstance().schedule(new RunnableImpl() {

                    @Override
                    public void runImpl() {
                        npc.deleteMe();
                    }

                }, 2500L);

                if (st.ownItemCount(EVIDENCE_OF_MIGRATION) == 10) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                }
            } else {
                htmltext = "";
                player.sendPacket(new ExShowScreenMessage(NpcString.THE_GRAVE_ROBBER_WARRIOR_HAS_BEEN_FILLED_WITH_DARK_ENERGY_AND_IS_ATTACKING_YOU, 4000, ScreenMessageAlign.MIDDLE_CENTER, false));
                int WARRIOR_MON = 22741;
                NpcInstance warrior = st.addSpawn(WARRIOR_MON, npc.getX(), npc.getY(), npc.getZ(), npc.getHeading(), 100, 120000);
                warrior.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Rnd.get(1, 100));

                if (Rnd.chance(50)) {
                    Functions.npcSay(warrior, NpcString.GRUNT_OH);
                } else {
                    Functions.npcSay(warrior, NpcString.GRUNT_WHAT_S_WRONG_WITH_ME);
                }

                npc.decayMe();

                return null;
            }
        }

        return htmltext;
    }

    private boolean canEnter(Player player) {
        if (player.isGM()) {
            return true;
        }
        String var = player.getPlayerVariables().get(PlayerVariables.QUEST_STATE_OF_450);
        if (var == null) {
            return true;
        }
        return Long.parseLong(var) - System.currentTimeMillis() > 24 * 60 * 60 * 1000;
    }
}