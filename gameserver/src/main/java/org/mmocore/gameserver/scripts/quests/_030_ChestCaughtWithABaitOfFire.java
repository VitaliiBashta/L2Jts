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
public class _030_ChestCaughtWithABaitOfFire extends Quest {
    // npc
    private static final int fisher_linneaus = 31577;
    private static final int bard_rukal = 30629;
    // etcitem
    private static final int red_treasure_box = 6511;
    private static final int necklace_of_protection = 916;
    // questitem
    private static final int q_note_of_bard_rukal = 7628;

    public _030_ChestCaughtWithABaitOfFire() {
        super(false);
        addStartNpc(fisher_linneaus);
        addTalkId(bard_rukal);
        addQuestItem(q_note_of_bard_rukal);
        addQuestCompletedCheck(53);
        addLevelCheck(60, 62);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("strange_box_of_linneaus_cookie");
        int npcId = npc.getNpcId();
        if (npcId == fisher_linneaus) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("strange_box_of_linneaus", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "fisher_linneaus_q0030_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(red_treasure_box) >= 1) {
                    st.setCond(2);
                    st.setMemoState("strange_box_of_linneaus", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(red_treasure_box, 1);
                    st.giveItems(q_note_of_bard_rukal, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "fisher_linneaus_q0030_0201.htm";
                } else
                    htmltext = "fisher_linneaus_q0030_0202.htm";
            }
        } else if (npcId == bard_rukal) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_note_of_bard_rukal) >= 1) {
                    st.takeItems(q_note_of_bard_rukal, 1);
                    st.giveItems(necklace_of_protection, 1);
                    st.removeMemo("strange_box_of_linneaus");
                    st.removeMemo("strange_box_of_linneaus_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "bard_rukal_q0030_0301.htm";
                } else
                    htmltext = "bard_rukal_q0030_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("strange_box_of_linneaus");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == fisher_linneaus) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "fisher_linneaus_q0030_0103.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "fisher_linneaus_q0030_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "fisher_linneaus_q0030_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == fisher_linneaus) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(red_treasure_box) >= 1) {
                            st.setMemoState("strange_box_of_linneaus_cookie", String.valueOf(1), true);
                            htmltext = "fisher_linneaus_q0030_0105.htm";
                        } else
                            htmltext = "fisher_linneaus_q0030_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "fisher_linneaus_q0030_0203.htm";
                } else if (npcId == bard_rukal) {
                    if (st.ownItemCount(q_note_of_bard_rukal) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("strange_box_of_linneaus_cookie", String.valueOf(2), true);
                        htmltext = "bard_rukal_q0030_0201.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}