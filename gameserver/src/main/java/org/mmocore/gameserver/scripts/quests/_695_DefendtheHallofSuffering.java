package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */
public class _695_DefendtheHallofSuffering extends Quest {
    private static final int TEPIOS = 32603;

    public _695_DefendtheHallofSuffering() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
        addLevelCheck(75, 82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("tepios_q695_3.htm")) {
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
                        htmltext = "tepios_q695_0.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        if (SoIManager.getCurrentStage() == 4)
                            htmltext = "tepios_q695_1.htm";
                        else {
                            htmltext = "tepios_q695_0a.htm";
                            st.exitQuest(true);
                        }
                        break;
                }
            } else if (cond == 1) {
                htmltext = "tepios_q695_4.htm";
            }
        }

        return htmltext;
    }
}