package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 22/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _649_ALooterandaRailroadMan extends Quest {
    // npc
    private static final int railman_obi = 32052;
    // mobs
    private static final int brigand_sweeper = 22017;
    private static final int brigand_hound = 22018;
    private static final int brigand_watcher = 22019;
    private static final int brigand_undertaker = 22021;
    private static final int brigand_assassin = 22022;
    private static final int brigand_fighter = 22023;
    private static final int brigand_inspector = 22024;
    private static final int brigand_captain = 22026;
    // questitem
    private static final int q_fang_of_bandit = 8099;

    public _649_ALooterandaRailroadMan() {
        super(true);
        addStartNpc(railman_obi);
        addKillId(brigand_sweeper, brigand_hound, brigand_watcher, brigand_undertaker, brigand_assassin, brigand_fighter, brigand_inspector, brigand_captain);
        addQuestItem(q_fang_of_bandit);
        addLevelCheck(30);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("bandit_and_railman_cookie");
        int npcId = npc.getNpcId();
        if (npcId == railman_obi) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "railman_obi_q0649_0103.htm";
            } else if (event.equalsIgnoreCase("reply=3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_fang_of_bandit) >= 200) {
                    st.takeItems(q_fang_of_bandit, -1);
                    st.giveItems(ADENA_ID, 21698);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "railman_obi_q0649_0201.htm";
                } else
                    htmltext = "railman_obi_q0649_0202.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("bandit_and_railman");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == railman_obi) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "railman_obi_q0649_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "railman_obi_q0649_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == railman_obi) {
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2) {
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_fang_of_bandit) >= 200) {
                            st.setMemoState("bandit_and_railman_cookie", String.valueOf(1), true);
                            htmltext = "railman_obi_q0649_0105.htm";
                        } else
                            htmltext = "railman_obi_q0649_0106.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("bandit_and_railman");
        int npcId = npc.getNpcId();
        if (npcId == brigand_sweeper) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 529) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_hound) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 452) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_watcher) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 606) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_undertaker) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 615) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_assassin) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 721) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_fighter) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 827) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_inspector) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 779) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brigand_captain) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000) {
                    if (st.ownItemCount(q_fang_of_bandit) + 1 >= 200) {
                        if (st.ownItemCount(q_fang_of_bandit) < 200) {
                            st.giveItems(q_fang_of_bandit, 200 - st.ownItemCount(q_fang_of_bandit));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(2);
                        st.setMemoState("bandit_and_railman", String.valueOf(1 * 10 + 2), true);
                    } else {
                        st.giveItems(q_fang_of_bandit, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}
