package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.object.Player;

public class _694_BreakThroughTheHallOfSuffering extends Quest {
    // NPC
    private static final int TEPIOS = 32603;

    // Item rewards
    private static final int MARK_OF_KEUCEREUS_STAGE_1 = 13691;
    private static final int MARK_OF_KEUCEREUS_STAGE_2 = 13692;

    public _694_BreakThroughTheHallOfSuffering() {
        super(PARTY_ALL);
        addStartNpc(TEPIOS);
        addLevelCheck(75, 82);
    }

    @Override
    public String onEvent(String event, QuestState qs, NpcInstance npc) {
        if (event.equalsIgnoreCase("32603-04.htm")) {
            qs.setCond(1);
            qs.setState(STARTED);
            qs.soundEffect(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.getPlayer();

        if (npcId == TEPIOS) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        if (st.ownItemCount(MARK_OF_KEUCEREUS_STAGE_1) == 0 && st.ownItemCount(MARK_OF_KEUCEREUS_STAGE_2) == 0 && player.getLevel() > 82)
                            st.giveItems(MARK_OF_KEUCEREUS_STAGE_1, 1);
                        st.exitQuest(true);
                        htmltext = "32603-00.htm";
                        break;
                    default:
                        htmltext = "32603-01.htm";
                        break;
                }
            } else if (cond == 1) {
                htmltext = "32603-05.htm";
            }
        }
        return htmltext;
    }
}