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
 * @version 1.0
 * @date 11/04/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _046_OnceMoreInTheArmsOfTheMotherTree extends Quest {
    // npc
    private static final int galladuchi = 30097;
    private static final int gentler = 30094;
    private static final int sandra = 30090;
    private static final int dustin = 30116;
    // questitem
    private static final int q_galladuchi_order_a = 7563;
    private static final int q_galladuchi_order_b = 7564;
    private static final int q_galladuchi_order_c = 7565;
    private static final int q_magic_sword_hilt = 7568;
    private static final int q_powdered_jewel = 7567;
    private static final int q_purified_necklace = 7566;
    private static final int q_symbol_of_traveler = 7570;
    private static final int q_escape_scroll_elf = 7555;

    public _046_OnceMoreInTheArmsOfTheMotherTree() {
        super(false);
        addStartNpc(galladuchi);
        addTalkId(gentler, sandra, dustin);
        addQuestItem(q_galladuchi_order_a, q_galladuchi_order_b, q_galladuchi_order_c, q_magic_sword_hilt, q_powdered_jewel, q_purified_necklace);
        addLevelCheck(3, 10);
        addQuestCompletedCheck(7);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("embracing_worldtree_cookie");
        int npcId = npc.getNpcId();
        if (npcId == galladuchi) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("embracing_worldtree", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(q_galladuchi_order_a, 1);
                htmltext = "galladuchi_q0046_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=46&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_magic_sword_hilt) >= 1) {
                    st.setCond(3);
                    st.setMemoState("embracing_worldtree", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_magic_sword_hilt, 1);
                    st.giveItems(q_galladuchi_order_b, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "galladuchi_q0046_0301.htm";
                } else
                    htmltext = "galladuchi_q0046_0302.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=46&reply=1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(q_powdered_jewel) >= 1) {
                    st.setCond(5);
                    st.setMemoState("embracing_worldtree", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(q_powdered_jewel, 1);
                    st.giveItems(q_galladuchi_order_c, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "galladuchi_q0046_0501.htm";
                } else
                    htmltext = "galladuchi_q0046_0502.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=46&reply=3") && GetHTMLCookie == 7 - 1) {
                if (st.ownItemCount(q_purified_necklace) >= 1) {
                    st.takeItems(q_purified_necklace, 1);
                    st.giveItems(q_escape_scroll_elf, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "galladuchi_q0046_0701.htm";
                } else
                    htmltext = "galladuchi_q0046_0702.htm";
            }
        } else if (npcId == gentler) {
            if (event.equalsIgnoreCase("menu_select?ask=46&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_galladuchi_order_a) >= 1) {
                    st.setCond(2);
                    st.setMemoState("embracing_worldtree", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(q_galladuchi_order_a, 1);
                    st.giveItems(q_magic_sword_hilt, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "gentler_q0046_0201.htm";
                } else
                    htmltext = "gentler_q0046_0202.htm";
            }
        } else if (npcId == sandra) {
            if (event.equalsIgnoreCase("menu_select?ask=46&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_galladuchi_order_b) >= 1) {
                    st.setCond(4);
                    st.setMemoState("embracing_worldtree", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_galladuchi_order_b, 1);
                    st.giveItems(q_powdered_jewel, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sandra_q0046_0401.htm";
                } else
                    htmltext = "sandra_q0046_0402.htm";
            }
        } else if (npcId == dustin) {
            if (event.equalsIgnoreCase("menu_select?ask=46&reply=1") && GetHTMLCookie == 6 - 1) {
                if (st.ownItemCount(q_galladuchi_order_c) >= 1) {
                    st.setCond(6);
                    st.setMemoState("embracing_worldtree", String.valueOf(6 * 10 + 1), true);
                    st.takeItems(q_galladuchi_order_c, 1);
                    st.giveItems(q_purified_necklace, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "dustin_q0046_0601.htm";
                } else
                    htmltext = "dustin_q0046_0602.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("embracing_worldtree");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == galladuchi) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "galladuchi_q0046_0103.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "galladuchi_q0046_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_symbol_of_traveler) >= 1)
                                htmltext = "galladuchi_q0046_0101.htm";
                            else {
                                htmltext = "galladuchi_q0046_0102.htm";
                                st.exitQuest(true);
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == galladuchi) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "galladuchi_q0046_0105.htm";
                    else if (st.ownItemCount(q_magic_sword_hilt) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("embracing_worldtree_cookie", String.valueOf(2), true);
                        htmltext = "galladuchi_q0046_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "galladuchi_q0046_0303.htm";
                    else if (st.ownItemCount(q_powdered_jewel) >= 1 && GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("embracing_worldtree_cookie", String.valueOf(4), true);
                        htmltext = "galladuchi_q0046_0401.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "galladuchi_q0046_0503.htm";
                    else if (st.ownItemCount(q_purified_necklace) >= 1 && GetMemoState == 6 * 10 + 1) {
                        st.setMemoState("embracing_worldtree_cookie", String.valueOf(6), true);
                        htmltext = "galladuchi_q0046_0601.htm";
                    }
                } else if (npcId == gentler) {
                    if (st.ownItemCount(q_galladuchi_order_a) >= 1 && GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("embracing_worldtree_cookie", String.valueOf(1), true);
                        htmltext = "gentler_q0046_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "gentler_q0046_0203.htm";
                } else if (npcId == sandra) {
                    if (st.ownItemCount(q_galladuchi_order_b) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("embracing_worldtree_cookie", String.valueOf(3), true);
                        htmltext = "sandra_q0046_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "sandra_q0046_0403.htm";
                } else if (npcId == dustin) {
                    if (st.ownItemCount(q_galladuchi_order_c) >= 1 && GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("embracing_worldtree_cookie", String.valueOf(5), true);
                        htmltext = "dustin_q0046_0501.htm";
                    } else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "dustin_q0046_0603.htm";
                }
                break;
        }
        return htmltext;
    }
}