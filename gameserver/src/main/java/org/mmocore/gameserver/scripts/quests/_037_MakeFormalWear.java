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
public class _037_MakeFormalWear extends Quest {
    // npc
    private static final int trader_alexis = 30842;
    private static final int leikar = 31520;
    private static final int jeremy = 31521;
    private static final int mist = 31627;
    // questitem
    private static final int q_mysterious_cloth = 7076;
    private static final int q_box_of_jewel = 7077;
    private static final int q_workbox = 7078;
    private static final int q_box_of_dress_shoes = 7113;
    private static final int q_seal_of_stock = 7164;
    private static final int q_luxury_wine = 7160;
    private static final int q_box_of_cookies = 7159;
    // etcitem
    private static final int wedding_dress = 6408;

    public _037_MakeFormalWear() {
        super(false);
        addStartNpc(trader_alexis);
        addTalkId(leikar, jeremy, mist);
        addLevelCheck(60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("make_my_wedding_dress_cookie");
        int npcId = npc.getNpcId();
        if (npcId == trader_alexis) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("make_my_wedding_dress", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_alexis_q0037_0104.htm";
            }
        } else if (npcId == leikar) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("make_my_wedding_dress", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_seal_of_stock, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "leikar_q0037_0201.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 6 - 1) {
                if (st.ownItemCount(q_box_of_cookies) >= 1) {
                    st.setCond(6);
                    st.setMemoState("make_my_wedding_dress", String.valueOf(6 * 10 + 1), true);
                    st.takeItems(q_box_of_cookies, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "leikar_q0037_0601.htm";
                } else
                    htmltext = "leikar_q0037_0602.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 7 - 1) {
                if (st.ownItemCount(q_mysterious_cloth) >= 1 && st.ownItemCount(q_box_of_jewel) >= 1 && st.ownItemCount(q_workbox) >= 1) {
                    st.setCond(7);
                    st.setMemoState("make_my_wedding_dress", String.valueOf(7 * 10 + 1), true);
                    st.takeItems(q_mysterious_cloth, 1);
                    st.takeItems(q_box_of_jewel, 1);
                    st.takeItems(q_workbox, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "leikar_q0037_0701.htm";
                } else
                    htmltext = "leikar_q0037_0702.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 8 - 1) {
                if (st.ownItemCount(q_box_of_dress_shoes) >= 1 && st.ownItemCount(q_seal_of_stock) >= 1) {
                    st.takeItems(q_box_of_dress_shoes, -1);
                    st.takeItems(q_seal_of_stock, -1);
                    st.giveItems(wedding_dress, 1);
                    st.removeMemo("make_my_wedding_dress");
                    st.removeMemo("make_my_wedding_dress_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "leikar_q0037_0801.htm";
                } else
                    htmltext = "leikar_q0037_0802.htm";
            }
        } else if (npcId == jeremy) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                st.setCond(3);
                st.setMemoState("make_my_wedding_dress", String.valueOf(3 * 10 + 1), true);
                st.giveItems(q_luxury_wine, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "jeremy_q0037_0301.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 5 - 1) {
                st.setCond(5);
                st.setMemoState("make_my_wedding_dress", String.valueOf(5 * 10 + 1), true);
                st.giveItems(q_box_of_cookies, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "jeremy_q0037_0501.htm";
            }
        } else if (npcId == mist) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_luxury_wine) >= 1) {
                    st.setCond(4);
                    st.setMemoState("make_my_wedding_dress", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_luxury_wine, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "mist_q0037_0401.htm";
                } else
                    htmltext = "mist_q0037_0402.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("make_my_wedding_dress");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == trader_alexis) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_alexis_q0037_0103.htm";
                            break;
                        default:
                            htmltext = "trader_alexis_q0037_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == trader_alexis) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "trader_alexis_q0037_0105.htm";
                } else if (npcId == leikar) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(1), true);
                        htmltext = "leikar_q0037_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "leikar_q0037_0202.htm";
                    else if (st.ownItemCount(q_box_of_cookies) >= 1 && GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(5), true);
                        htmltext = "leikar_q0037_0501.htm";
                    } else if (GetMemoState == 6 * 10 + 1) {
                        if (st.ownItemCount(q_mysterious_cloth) >= 1 && st.ownItemCount(q_box_of_jewel) >= 1 && st.ownItemCount(q_workbox) >= 1) {
                            st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(6), true);
                            htmltext = "leikar_q0037_0603.htm";
                        } else
                            htmltext = "leikar_q0037_0604.htm";
                    } else if (GetMemoState == 7 * 10 + 1) {
                        if (st.ownItemCount(q_box_of_dress_shoes) >= 1) {
                            st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(7), true);
                            htmltext = "leikar_q0037_0703.htm";
                        } else
                            htmltext = "leikar_q0037_0704.htm";
                    }
                } else if (npcId == jeremy) {
                    if (st.ownItemCount(q_seal_of_stock) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(2), true);
                        htmltext = "jeremy_q0037_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "jeremy_q0037_0303.htm";
                    else if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(4), true);
                        htmltext = "jeremy_q0037_0401.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "jeremy_q0037_0502.htm";
                } else if (npcId == mist) {
                    if (st.ownItemCount(q_luxury_wine) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("make_my_wedding_dress_cookie", String.valueOf(3), true);
                        htmltext = "mist_q0037_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "mist_q0037_0403.htm";
                }
                break;
        }
        return htmltext;
    }
}