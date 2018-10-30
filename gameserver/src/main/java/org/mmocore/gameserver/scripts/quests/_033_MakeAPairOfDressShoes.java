package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.1
 * @date 26/07/2016
 * @lastedit 26/07/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _033_MakeAPairOfDressShoes extends Quest {
    // npc
    private final static int trader_woodley = 30838;
    private final static int iz = 30164;
    private final static int leikar = 31520;
    // questitem
    private final static int q_seal_of_stock = 7164;
    // etcitem
    private final static int leather = 1882;
    private final static int thread = 1868;
    private final static int q_box_of_dress_shoes = 7113;

    public _033_MakeAPairOfDressShoes() {
        super(false);
        addStartNpc(trader_woodley);
        addTalkId(iz, leikar);
        addLevelCheck(60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("make_the_dress_shoes_cookie");
        int npcId = npc.getNpcId();
        if (npcId == trader_woodley) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("make_the_dress_shoes", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_woodley_q0033_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                st.setCond(3);
                st.setMemoState("make_the_dress_shoes", String.valueOf(3 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_woodley_q0033_0301.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(leather) >= 200 && st.ownItemCount(thread) >= 600 && st.ownItemCount(ADENA_ID) >= 200000) {
                    st.setCond(4);
                    st.setMemoState("make_the_dress_shoes", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(leather, 200);
                    st.takeItems(thread, 600);
                    st.takeItems(ADENA_ID, 200000);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "trader_woodley_q0033_0401.htm";
                } else
                    htmltext = "trader_woodley_q0033_0402.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 6 - 1) {
                st.giveItems(q_box_of_dress_shoes, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "trader_woodley_q0033_0601.htm";
            }
        } else if (npcId == iz) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(ADENA_ID) >= 300000) {

                    st.setCond(5);
                    st.setMemoState("make_the_dress_shoes", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(ADENA_ID, 300000);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "iz_q0033_0501.htm";
                } else
                    htmltext = "iz_q0033_0502.htm";
            }
        } else if (npcId == leikar) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("make_the_dress_shoes", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "leikar_q0033_0201.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("make_the_dress_shoes");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == trader_woodley) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_woodley_q0033_0103.htm";
                            break;
                        default:
                            if (st.getPlayer().getQuestState(37) != null && st.getPlayer().getQuestState(37).isStarted() && st.ownItemCount(q_seal_of_stock) >= 1)
                                htmltext = "trader_woodley_q0033_0101.htm";
                            else
                                htmltext = "trader_woodley_q0033_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == trader_woodley) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "trader_woodley_q0033_0105.htm";
                    else if (GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("make_the_dress_shoes_cookie", String.valueOf(2), true);
                        htmltext = "trader_woodley_q0033_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        if (st.ownItemCount(leather) >= 200 && st.ownItemCount(thread) >= 600 && st.ownItemCount(ADENA_ID) >= 500000) {
                            st.setMemoState("make_the_dress_shoes_cookie", String.valueOf(3), true);
                            htmltext = "trader_woodley_q0033_0302.htm";
                        } else
                            htmltext = "trader_woodley_q0033_0303.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "trader_woodley_q0033_0403.htm";
                    else if (GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("make_the_dress_shoes_cookie", String.valueOf(5), true);
                        htmltext = "trader_woodley_q0033_0501.htm";
                    }
                } else if (npcId == iz) {
                    if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("make_the_dress_shoes_cookie", String.valueOf(4), true);
                        htmltext = "iz_q0033_0401.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "iz_q0033_0503.htm";
                } else if (npcId == leikar) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("make_the_dress_shoes_cookie", String.valueOf(1), true);
                        htmltext = "leikar_q0033_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "leikar_q0033_0202.htm";
                }
                break;
        }
        return htmltext;
    }
}