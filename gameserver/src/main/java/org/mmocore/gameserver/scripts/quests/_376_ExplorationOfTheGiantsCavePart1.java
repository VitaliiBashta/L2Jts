package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Квест проверен и работает, рейты применены путем увеличения шанса выпадения квестовых вещей
 */
public class _376_ExplorationOfTheGiantsCavePart1 extends Quest {
    // Ancient parchment drop rate in %
    private static final int DROP_RATE = 3;
    // Mysterious Book drop rate in %
    private static final int DROP_RATE_BOOK = 1;

    // Quest items
    private static final int ANCIENT_PARCHMENT = 14841;
    private static final int DICT1 = 5891;
    private static final int DICT2 = 5892; // Given as a proof for 2nd part
    private static final int MST_BK = 5890;

    private static final int[][] EXCHANGE = {
            // collection items list,     rnd_1, rnd_2
            {
                    5937,
                    5938,
                    5939,
                    5940,
                    5941
            },
            {
                    5346,
                    5354
            },
            // medical theory, tallum_tunic,     tallum_hose
            {
                    5932,
                    5933,
                    5934,
                    5935,
                    5936
            },
            {
                    5332,
                    5334
            },
            // architecture,   drk_crstl_leather,tallum_leather
            {
                    5922,
                    5923,
                    5924,
                    5925,
                    5926
            },
            {
                    5416,
                    5418
            },
            // golem plans,    drk_crstl_breastp,tallum_plate
            {
                    5927,
                    5928,
                    5929,
                    5930,
                    5931
            },
            {
                    5424,
                    5340
            }
            // basics of magic,drk_crstl_gaiters,dark_crystal_legg
    };

    // NPCs
    private static final int HR_SOBLING = 31147;
    private static final int WF_CLIFF = 30182;

    // Mobs
    private static final int[] MOBS = {
            // список мобов для квеста
            22670,
            // Cursed Lord L80
            22671,
            // Cursed Guardian L80
            22672,
            // Cursed Seer L80
            22673,
            // Hirokai L80
            22674,
            // Imagro L80
            22675,
            // Palit L80
            22676,
            // Hamlet L80
            22677,
            // Klennot L80
    };

    public _376_ExplorationOfTheGiantsCavePart1() {
        super(true);
        addStartNpc(HR_SOBLING);
        addTalkId(WF_CLIFF);
        addKillId(MOBS);
        addQuestItem(DICT1, MST_BK);
        addLevelCheck(79);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if (event.equalsIgnoreCase("yes")) {
            htmltext = "Starting.htm";
            st.setState(STARTED);
            st.setCond(1);
            st.giveItems(DICT1, 1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("no")) {
            htmltext = "ext_msg.htm";
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(true);
        } else if (event.equalsIgnoreCase("show")) {
            htmltext = "no_items.htm";
            for (int i = 0; i < EXCHANGE.length; i = i + 2) {
                long count = Long.MAX_VALUE;
                for (int j : EXCHANGE[i]) {
                    count = Math.min(count, st.ownItemCount(j));
                }
                if (count >= 1) {
                    htmltext = "tnx4items.htm";
                    for (int j : EXCHANGE[i]) {
                        st.takeItems(j, count);
                    }
                    for (int l = 0; l < count; l++) {
                        int item = EXCHANGE[i + 1][Rnd.get(EXCHANGE[i + 1].length)];
                        st.giveItems(item, 1);
                    }
                }
            }
        } else if (event.equalsIgnoreCase("myst")) {
            if (st.ownItemCount(MST_BK) > 0) {
                if (cond == 1) {
                    st.setState(STARTED);
                    st.setCond(2);
                    htmltext = "go_part2.htm";
                } else if (cond == 2) {
                    htmltext = "gogogo_2.htm";
                }
            } else {
                htmltext = "no_part2.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (npcId == HR_SOBLING) {
            if (id == CREATED) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "error_1.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "start.htm";
                        break;
                }
            } else if (id == STARTED) {
                if (st.ownItemCount(ANCIENT_PARCHMENT) != 0) {
                    htmltext = "checkout2.htm";
                } else {
                    htmltext = "checkout.htm";
                }
            }
        } else if (npcId == WF_CLIFF) {
            if (cond == 2 & st.ownItemCount(MST_BK) > 0) {
                htmltext = "ok_part2.htm";
                st.takeItems(MST_BK, -1);
                st.giveItems(DICT2, 1);
                st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond > 0) {
            st.rollAndGive(ANCIENT_PARCHMENT, 1, 1, DROP_RATE);

            if (cond == 1) {
                st.rollAndGive(MST_BK, 1, 1, 1, DROP_RATE_BOOK);
            }
        }
        return null;
    }
}