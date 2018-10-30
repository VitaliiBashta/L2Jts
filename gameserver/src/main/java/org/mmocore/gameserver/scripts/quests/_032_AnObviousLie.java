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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _032_AnObviousLie extends Quest {
    // npc
    private final static int maximilian = 30120;
    private final static int gentler = 30094;
    private final static int miki_the_cat = 31706;

    // mobs
    private final static int crocodile = 20135;

    // questitem
    private final static int q_map_of_gentler = 7165;
    private final static int q_medicinal_herb = 7166;

    // etcitem
    private final static int spirit_ore = 3031;
    private final static int thread = 1868;
    private final static int suede = 1866;
    private final static int racoon_ear = 7680;
    private final static int cat_ear = 6843;
    private final static int rabbit_ear = 7683;

    public _032_AnObviousLie() {
        super(true);
        addStartNpc(maximilian);
        addTalkId(maximilian, gentler, miki_the_cat);
        addKillId(crocodile);
        addQuestItem(q_medicinal_herb, q_map_of_gentler);
        addLevelCheck(45);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("blatant_lie_cookie");
        int npcId = npc.getNpcId();

        if (npcId == maximilian) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("blatant_lie", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "maximilian_q0032_0104.htm";
            }
        } else if (npcId == gentler) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("blatant_lie", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_map_of_gentler, 1);
                htmltext = "gentler_q0032_0201.htm";
                st.soundEffect(SOUND_MIDDLE);
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_medicinal_herb) >= 20) {
                    st.setCond(5);
                    st.setMemoState("blatant_lie", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_medicinal_herb, 20);
                    htmltext = "gentler_q0032_0401.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "gentler_q0032_0402.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(spirit_ore) >= 500) {
                    st.setCond(6);
                    st.setMemoState("blatant_lie", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(spirit_ore, 500);
                    htmltext = "gentler_q0032_0501.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "gentler_q0032_0502.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 7 - 1) {
                st.setCond(8);
                st.setMemoState("blatant_lie", String.valueOf(7 * 10 + 1), true);
                htmltext = "gentler_q0032_0701.htm";
                st.soundEffect(SOUND_MIDDLE);
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 8 - 1)
                htmltext = "gentler_q0032_0801.htm";
            else if (event.equalsIgnoreCase("reply_11") && GetHTMLCookie == 8 - 1) {
                if (st.ownItemCount(thread) >= 1000 && st.ownItemCount(suede) >= 500) {
                    st.takeItems(thread, 1000);
                    st.takeItems(suede, 500);
                    st.giveItems(cat_ear, 1);
                    st.removeMemo("blatant_lie");
                    st.removeMemo("blatant_lie_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "gentler_q0032_0802.htm";
                } else
                    htmltext = "gentler_q0032_0803.htm";
            } else if (event.equalsIgnoreCase("reply_12") && GetHTMLCookie == 8 - 1) {
                if (st.ownItemCount(thread) >= 1000 && st.ownItemCount(suede) >= 500) {
                    st.takeItems(thread, 1000);
                    st.takeItems(suede, 500);
                    st.giveItems(racoon_ear, 1);
                    st.removeMemo("blatant_lie");
                    st.removeMemo("blatant_lie_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "gentler_q0032_0802.htm";
                } else
                    htmltext = "gentler_q0032_0803.htm";
            } else if (event.equalsIgnoreCase("reply_13") && GetHTMLCookie == 8 - 1)
                if (st.ownItemCount(thread) >= 1000 && st.ownItemCount(suede) >= 500) {
                    st.takeItems(thread, 1000);
                    st.takeItems(suede, 500);
                    st.giveItems(rabbit_ear, 1);
                    st.removeMemo("blatant_lie");
                    st.removeMemo("blatant_lie_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "gentler_q0032_0802.htm";
                } else
                    htmltext = "gentler_q0032_0803.htm";
        } else if (npcId == miki_the_cat)
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                st.setCond(3);
                st.setMemoState("blatant_lie", String.valueOf(3 * 10 + 1), true);
                st.takeItems(q_map_of_gentler, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "miki_the_cat_q0032_0301.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 6 - 1) {
                st.setCond(7);
                st.setMemoState("blatant_lie", String.valueOf(6 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "miki_the_cat_q0032_0601.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("blatant_lie");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == maximilian) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "maximilian_q0032_0103.htm";
                            break;
                        default:
                            htmltext = "maximilian_q0032_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == maximilian) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "maximilian_q0032_0105.htm";
                } else if (npcId == gentler) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("blatant_lie_cookie", String.valueOf(1), true);
                        htmltext = "gentler_q0032_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "gentler_q0032_0202.htm";
                    else if (GetMemoState == 3 * 10 + 2) {
                        if (st.ownItemCount(q_medicinal_herb) >= 20) {
                            st.setMemoState("blatant_lie_cookie", String.valueOf(3), true);
                            htmltext = "gentler_q0032_0301.htm";
                        } else
                            htmltext = "gentler_q0032_0302.htm";
                    } else if (GetMemoState == 4 * 10 + 1) {
                        if (st.ownItemCount(spirit_ore) >= 500) {
                            st.setMemoState("blatant_lie_cookie", String.valueOf(4), true);
                            htmltext = "gentler_q0032_0403.htm";
                        } else
                            htmltext = "gentler_q0032_0404.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "gentler_q0032_0503.htm";
                    else if (GetMemoState == 6 * 10 + 1) {
                        st.setMemoState("blatant_lie_cookie", String.valueOf(6), true);
                        htmltext = "gentler_q0032_0601.htm";
                    } else if (GetMemoState == 7 * 10 + 1)
                        if (st.ownItemCount(thread) >= 1000 && st.ownItemCount(suede) >= 500) {
                            st.setMemoState("blatant_lie_cookie", String.valueOf(7), true);
                            htmltext = "gentler_q0032_0702.htm";
                        } else
                            htmltext = "gentler_q0032_0703.htm";
                } else if (npcId == miki_the_cat)
                    if (st.ownItemCount(q_map_of_gentler) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("blatant_lie_cookie", String.valueOf(2), true);
                        htmltext = "miki_the_cat_q0032_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "miki_the_cat_q0032_0303.htm";
                    else if (GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("blatant_lie_cookie", String.valueOf(5), true);
                        htmltext = "miki_the_cat_q0032_0501.htm";
                    } else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "miki_the_cat_q0032_0602.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("blatant_lie");
        int npcId = npc.getNpcId();

        if (GetMemoState == 3 * 10 + 1)
            if (npcId == crocodile) {
                int i4 = Rnd.get(500);
                if (i4 < 500)
                    if (st.ownItemCount(q_medicinal_herb) + 1 >= 20) {
                        if (st.ownItemCount(q_medicinal_herb) <= 20) {
                            st.giveItems(q_medicinal_herb, 20 - st.ownItemCount(q_medicinal_herb));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(4);
                        st.setMemoState("blatant_lie", String.valueOf(3 * 10 + 2), true);
                    } else {
                        st.giveItems(q_medicinal_herb, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}