package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public class _697_DefendtheHallofErosion extends Quest {
    private static final int TEPIOS = 32603;
    private static final int VesperNobleEnhanceStone = 14052;

    public _697_DefendtheHallofErosion() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        //Player player = st.getPlayer();
        String htmltext = event;

        if (event.equalsIgnoreCase("tepios_q697_3.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == TEPIOS) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "tepios_q697_0.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (SoIManager.getCurrentStage() != 4) {
                            htmltext = "tepios_q697_0a.htm";
                            st.exitQuest(true);
                        } else
                            htmltext = "tepios_q697_1.htm";
                        break;
                }
            } else if (cond == 1 && st.getInt("defenceDone") == 0) {
                htmltext = "tepios_q697_4.htm";
            } else if (cond == 1 && st.getInt("defenceDone") != 0) {
                st.giveItems(VesperNobleEnhanceStone, Rnd.get(12, 20));
                htmltext = "tepios_q697_5.htm";
                st.soundEffect(SOUND_FINISH);
                st.removeMemo("defenceDone");
                st.exitQuest(true);
            }

        }
        return htmltext;
    }


}