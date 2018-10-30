package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _365_DevilsLegacy extends Quest {
    //NPC
    private static final int RANDOLF = 30095;
    //VARIABLES
    private static final int CHANCE_OF_DROP = 25;
    private static final int REWARD_PER_ONE = 5070;
    //ITEMS
    private static final int TREASURE_CHEST = 5873;
    //MOBS
    final int[] MOBS = new int[]{
            20836,
            29027,
            20845,
            21629,
            21630,
            29026
    };


    public _365_DevilsLegacy() {
        super(false);
        addStartNpc(RANDOLF);
        addKillId(MOBS);
        addQuestItem(TREASURE_CHEST);
        addLevelCheck(39, 56);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("30095-1.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("30095-5.htm")) {
            long count = st.ownItemCount(TREASURE_CHEST);
            if (count > 0) {
                long reward = count * REWARD_PER_ONE;
                st.takeItems(TREASURE_CHEST, -1);
                st.giveItems(ADENA_ID, reward);
            } else {
                htmltext = "You don't have required items";
            }
        } else if (event.equalsIgnoreCase("30095-6.htm")) {
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            switch (isAvailableFor(st.getPlayer())) {
                case LEVEL:
                    htmltext = "30095-0a.htm";
                    st.exitQuest(true);
                    break;
                default:
                    htmltext = "30095-0.htm";
                    break;
            }
        } else if (cond == 1) {
            if (st.ownItemCount(TREASURE_CHEST) == 0) {
                htmltext = "30095-2.htm";
            } else {
                htmltext = "30095-4.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (Rnd.chance(CHANCE_OF_DROP)) {
            st.giveItems(TREASURE_CHEST, 1);
            st.soundEffect(SOUND_ITEMGET);
        }
        return null;
    }
}