package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public class _698_BlocktheLordsEscape extends Quest {
    // NPC
    private static final int TEPIOS = 32603;
    private static final int VesperNobleEnhanceStone = 14052;

    public _698_BlocktheLordsEscape() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
        addLevelCheck(80);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();

        if (npcId == TEPIOS) {
            if (st.getState() == CREATED) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "tepios_q698_0.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (SoIManager.getCurrentStage() != 5) {
                            htmltext = "tepios_q698_0a.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "tepios_q698_1.htm";
                        break;
                }
            } else if (st.getCond() == 1 && st.getInt("defenceDone") == 1) {
                htmltext = "tepios_q698_5.htm";
                st.giveItems(VesperNobleEnhanceStone, (int) ServerConfig.RATE_QUESTS_REWARD * Rnd.get(5, 8));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
            } else {
                return "tepios_q698_4.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        //Player player = st.getPlayer();
        String htmltext = event;
        //int cond = st.getCond();

        if (event.equalsIgnoreCase("tepios_q698_3.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }


}