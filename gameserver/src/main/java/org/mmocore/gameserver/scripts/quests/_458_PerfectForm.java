package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * @author pchayka
 */

public class _458_PerfectForm extends Quest {
    private static final int Kelleyia = 32768;

    private static final String KOOKABURA_VAR = "kookaburacount";
    private static final String COUGAR_VAR = "cougarcount";
    private static final String BUFFALO_VAR = "buffalocount";
    private static final String GRENDEL_VAR = "grendelcount";

    private static final int COUNT = 10;

    // client  4    "1018879|1018886|1018893|1018900"    4    "10|10|10|10"
    private static final int[] GrownKookabura = {
            18879,
            18878
    };
    private static final int[] GrownCougar = {
            18886,
            18885
    };
    private static final int[] GrownBuffalo = {
            18893,
            18892
    };
    private static final int[] GrownGrendel = {
            18900,
            18899
    };

    private static final int[][][] Rewards = {
            {
                    {
                            10373,
                            1
                    },
                    {
                            10374,
                            1
                    },
                    {
                            10375,
                            1
                    },
                    {
                            10376,
                            1
                    },
                    {
                            10377,
                            1
                    },
                    {
                            10378,
                            1
                    },
                    {
                            10379,
                            1
                    },
                    {
                            10380,
                            1
                    },
                    {
                            10381,
                            1
                    }
            },
            {
                    {
                            10397,
                            5
                    },
                    {
                            10398,
                            5
                    },
                    {
                            10399,
                            5
                    },
                    {
                            10400,
                            5
                    },
                    {
                            10401,
                            5
                    },
                    {
                            10402,
                            5
                    },
                    {
                            10403,
                            5
                    },
                    {
                            10404,
                            5
                    },
                    {
                            10405,
                            5
                    }
            },
            {
                    {
                            10397,
                            2
                    },
                    {
                            10398,
                            2
                    },
                    {
                            10399,
                            2
                    },
                    {
                            10400,
                            2
                    },
                    {
                            10401,
                            2
                    },
                    {
                            10402,
                            2
                    },
                    {
                            10403,
                            2
                    },
                    {
                            10404,
                            2
                    },
                    {
                            10405,
                            2
                    }
            }
    };

    public _458_PerfectForm() {
        super(false);

        addKillNpcWithLog(1, KOOKABURA_VAR, COUNT, GrownKookabura);
        addKillNpcWithLog(1, COUGAR_VAR, COUNT, GrownCougar);
        addKillNpcWithLog(1, BUFFALO_VAR, COUNT, GrownBuffalo);
        addKillNpcWithLog(1, GRENDEL_VAR, COUNT, GrownGrendel);

        addStartNpc(Kelleyia);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equalsIgnoreCase("kelleyia_q458_05.htm")) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("request_1stresults")) {
            switch (st.getInt("normaloverhits")) {
                case 1:
                    htmltext = "kelleyia_q458_08a.htm";
                    break;
                case 2:
                    htmltext = "kelleyia_q458_08b.htm";
                    break;
                case 3:
                    htmltext = "kelleyia_q458_08c.htm";
                    break;
            }
        } else if (event.equalsIgnoreCase("request_2ndresults")) {
            switch (st.getInt("critoverhits")) {
                case 1:
                    htmltext = "kelleyia_q458_09a.htm";
                    break;
                case 2:
                    htmltext = "kelleyia_q458_09b.htm";
                    break;
                case 3:
                    htmltext = "kelleyia_q458_09c.htm";
                    break;
            }
        } else if (event.equalsIgnoreCase("request_3rdresults")) {
            switch (st.getInt("contoverhits")) {
                case 1:
                    htmltext = "kelleyia_q458_10a.htm";
                    break;
                case 2:
                    htmltext = "kelleyia_q458_10b.htm";
                    break;
                case 3:
                    htmltext = "kelleyia_q458_10c.htm";
                    break;
            }
        } else if (event.equalsIgnoreCase("request_reward")) {
            int[] reward;
            switch (st.getInt("contoverhits")) {
                case 1:
                    reward = Rewards[0][Rnd.get(Rewards[0].length)];
                    st.giveItems(reward[0], reward[1]);
                    break;
                case 2:
                    reward = Rewards[1][Rnd.get(Rewards[1].length)];
                    st.giveItems(reward[0], reward[1]);
                    break;
                case 3:
                    reward = Rewards[2][Rnd.get(Rewards[2].length)];
                    st.giveItems(reward[0], reward[1]);
                    st.giveItems(15482, 10);
                    st.giveItems(15483, 10);
                    break;
            }
            htmltext = "kelleyia_q458_11.htm";
            st.removeMemo(KOOKABURA_VAR);
            st.removeMemo(COUGAR_VAR);
            st.removeMemo(BUFFALO_VAR);
            st.removeMemo(GRENDEL_VAR);
            st.removeMemo("normaloverhits");
            st.removeMemo("critoverhits");
            st.removeMemo("contoverhits");
            st.setState(COMPLETED);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(this);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() == Kelleyia) {
            switch (st.getState()) {
                case CREATED:
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kelleyia_q458_00.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.isNowAvailable())
                                htmltext = "kelleyia_q458_01.htm";
                            else
                                htmltext = "kelleyia_q458_00a.htm";
                            break;
                    }
                    break;
                case STARTED:
                    if (st.getCond() == 1) {
                        htmltext = "kelleyia_q458_06.htm";
                    } else if (st.getCond() == 2) {
                        htmltext = "kelleyia_q458_07.htm";
                    }
                    break;
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        boolean doneCond = updateKill(npc, st);
        if (doneCond) {
            st.setMemoState("normaloverhits", Rnd.get(1, 3));
            st.setMemoState("critoverhits", Rnd.get(1, 3));
            st.setMemoState("contoverhits", Rnd.get(1, 3));
            st.setCond(2);
        }
        return null;
    }


}