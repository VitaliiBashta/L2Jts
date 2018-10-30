package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.spawn.PeriodOfDay;

import java.util.ArrayList;
import java.util.List;

public class _653_WildMaiden extends Quest {
    // Npc
    public final int SUKI = 32013;
    public final int GALIBREDO = 30181;

    // Items
    public final int SOE = 736;

    public _653_WildMaiden() {
        super(false);

        addStartNpc(SUKI);

        addTalkId(SUKI);
        addTalkId(GALIBREDO);
        addLevelCheck(36);
    }

    private NpcInstance findNpc(int npcId, Player player) {
        NpcInstance instance = null;
        List<NpcInstance> npclist = new ArrayList<NpcInstance>();
        for (Spawner spawn : SpawnManager.getInstance().getSpawners(PeriodOfDay.NONE.name())) {
            if (spawn.getCurrentNpcId() == npcId) {
                instance = spawn.getLastSpawn();
                npclist.add(instance);
            }
        }

        for (NpcInstance npc : npclist) {
            if (player.isInRange(npc, 1600)) {
                return npc;
            }
        }

        return instance;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        if (event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm")) {
            if (st.ownItemCount(SOE) > 0) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.takeItems(SOE, 1);
                htmltext = "spring_girl_sooki_q0653_04a.htm";
                NpcInstance n = findNpc(SUKI, player);
                n.broadcastPacket(new MagicSkillUse(n, n, 2013, 1, 20000, 0));
                st.startQuestTimer("suki_timer", 20000);
            }
        } else if (event.equalsIgnoreCase("spring_girl_sooki_q0653_03.htm")) {
            st.exitQuest(false);
            st.soundEffect(SOUND_GIVEUP);
        } else if (event.equalsIgnoreCase("suki_timer")) {
            NpcInstance n = findNpc(SUKI, player);
            n.deleteMe();
            htmltext = null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";

        int npcId = npc.getNpcId();
        int id = st.getState();
        if (npcId == SUKI && id == CREATED) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "spring_girl_sooki_q0653_01a.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "spring_girl_sooki_q0653_01.htm";
                    break;
            }
        } else if (npcId == GALIBREDO && st.getCond() == 1) {
            htmltext = "galicbredo_q0653_01.htm";
            st.giveItems(ADENA_ID, 2883);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
        }
        return htmltext;
    }
}