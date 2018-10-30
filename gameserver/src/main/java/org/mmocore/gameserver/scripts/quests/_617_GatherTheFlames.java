package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _617_GatherTheFlames extends Quest {
    public static final int[] Recipes = {
            6881,
            6883,
            6885,
            6887,
            7580,
            6891,
            6893,
            6895,
            6897,
            6899
    };
    //npc
    private final static int VULCAN = 31539;
    private final static int HILDA = 31271;
    //items
    private final static int TORCH = 7264;
    //DROPLIST (MOB_ID, CHANCE)
    private final static int[][] DROPLIST = {
            {
                    22634,
                    58
            },
            {
                    22635,
                    58
            },
            {
                    22636,
                    58
            },
            {
                    22637,
                    58
            },
            {
                    22638,
                    58
            },
            {
                    22639,
                    61
            },
            {
                    22640,
                    61
            },
            {
                    22641,
                    61
            },
            {
                    22642,
                    61
            },
            {
                    22643,
                    61
            },
            {
                    22644,
                    61
            },
            {
                    22645,
                    63
            },
            {
                    22646,
                    63
            },
            {
                    22647,
                    63
            },
            {
                    22648,
                    66
            },
            {
                    22649,
                    66
            },
            {
                    18799,
                    66
            },
            {
                    18800,
                    66
            },
            {
                    18801,
                    66
            },
            {
                    18802,
                    74
            },
            {
                    18803,
                    74
            }
    };


    public _617_GatherTheFlames() {
        super(true);

        addStartNpc(VULCAN);
        addStartNpc(HILDA);

        for (int[] element : DROPLIST) {
            addKillId(element[0]);
        }
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("warsmith_vulcan_q0617_03.htm")) //VULCAN
        {
            if (st.getPlayer().getLevel() < 74) {
                return "warsmith_vulcan_q0617_02.htm";
            }
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            st.setCond(1);
        } else if (event.equalsIgnoreCase("blacksmith_hilda_q0617_03.htm")) //HILDA
        {
            if (st.getPlayer().getLevel() < 74) {
                return "blacksmith_hilda_q0617_02.htm";
            }
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            st.setCond(1);
        } else if (event.equalsIgnoreCase("warsmith_vulcan_q0617_08.htm")) {
            st.soundEffect(SOUND_FINISH);
            st.takeItems(TORCH, -1);
            st.exitQuest(true);
        } else if (event.equalsIgnoreCase("warsmith_vulcan_q0617_07.htm")) {
            if (st.ownItemCount(TORCH) < 1000) {
                return "warsmith_vulcan_q0617_05.htm";
            }
            st.takeItems(TORCH, 1000);
            st.giveItems(Recipes[Rnd.get(Recipes.length)], 1);
            st.soundEffect(SOUND_MIDDLE);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == VULCAN) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "warsmith_vulcan_q0617_02.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "warsmith_vulcan_q0617_01.htm";
                        break;
                }
            } else {
                htmltext = st.ownItemCount(TORCH) < 1000 ? "warsmith_vulcan_q0617_05.htm" : "warsmith_vulcan_q0617_04.htm";
            }
        } else if (npcId == HILDA) {
            if (cond < 1) {
                htmltext = st.getPlayer().getLevel() < 74 ? "blacksmith_hilda_q0617_02.htm" : "blacksmith_hilda_q0617_01.htm";
            } else {
                htmltext = "blacksmith_hilda_q0617_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        for (int[] element : DROPLIST) {
            if (npc.getNpcId() == element[0]) {
                st.rollAndGive(TORCH, 1, element[1]);
                return null;
            }
        }
        return null;
    }
}