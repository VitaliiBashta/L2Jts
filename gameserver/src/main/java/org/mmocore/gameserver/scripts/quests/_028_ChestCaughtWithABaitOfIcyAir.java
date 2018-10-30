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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _028_ChestCaughtWithABaitOfIcyAir extends Quest {
    // npc
    private static final int fisher_ofulle = 31572;
    private static final int mineral_trader_kiki = 31442;
    // etcitem
    private static final int big_yellow_treasure_box = 6503;
    private static final int elven_ring = 881;
    // questitem
    private static final int q_letter_of_kiki = 7626;

    public _028_ChestCaughtWithABaitOfIcyAir() {
        super(false);
        addStartNpc(fisher_ofulle);
        addTalkId(mineral_trader_kiki);
        addQuestItem(q_letter_of_kiki);
        addQuestCompletedCheck(51);
        addLevelCheck(36, 38);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("strange_box_of_ofulle_cookie");
        int npcId = npc.getNpcId();
        if (npcId == fisher_ofulle) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("strange_box_of_ofulle", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "fisher_ofulle_q0028_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(big_yellow_treasure_box) >= 1) {
                    st.setCond(2);
                    st.setMemoState("strange_box_of_ofulle", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(big_yellow_treasure_box, 1);
                    st.giveItems(q_letter_of_kiki, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "fisher_ofulle_q0028_0201.htm";
                } else
                    htmltext = "fisher_ofulle_q0028_0202.htm";
            }
        } else if (npcId == mineral_trader_kiki) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_letter_of_kiki) >= 1) {
                    st.takeItems(q_letter_of_kiki, 1);
                    st.giveItems(elven_ring, 1);
                    st.removeMemo("strange_box_of_ofulle");
                    st.removeMemo("strange_box_of_ofulle_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "mineral_trader_kiki_q0028_0301.htm";
                } else
                    htmltext = "mineral_trader_kiki_q0028_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("strange_box_of_ofulle");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == fisher_ofulle) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "fisher_ofulle_q0028_0103.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "fisher_ofulle_q0028_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "fisher_ofulle_q0028_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == fisher_ofulle) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(big_yellow_treasure_box) >= 1) {
                            st.setMemoState("strange_box_of_ofulle_cookie", String.valueOf(1), true);
                            htmltext = "fisher_ofulle_q0028_0105.htm";
                        } else
                            htmltext = "fisher_ofulle_q0028_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "fisher_ofulle_q0028_0203.htm";
                } else if (npcId == mineral_trader_kiki) {
                    if (st.ownItemCount(q_letter_of_kiki) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("strange_box_of_ofulle_cookie", String.valueOf(2), true);
                        htmltext = "mineral_trader_kiki_q0028_0201.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}
