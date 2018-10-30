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
public class _029_ChestCaughtWithABaitOfEarth extends Quest {
    // npc
    private static final int fisher_willeri = 31574;
    private static final int magister_anabel = 30909;
    // etcitem
    private static final int small_violet_treasure_box = 6507;
    private static final int plate_leather_gloves = 2455;
    // questitem
    private static final int q_small_glass_box = 7627;

    public _029_ChestCaughtWithABaitOfEarth() {
        super(false);
        addStartNpc(fisher_willeri);
        addTalkId(magister_anabel);
        addQuestItem(q_small_glass_box);
        addQuestCompletedCheck(52);
        addLevelCheck(48, 50);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("strange_box_of_willeri_cookie");
        int npcId = npc.getNpcId();
        if (npcId == fisher_willeri) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("strange_box_of_willeri", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "fisher_willeri_q0029_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(small_violet_treasure_box) >= 1) {
                    st.setCond(2);
                    st.setMemoState("strange_box_of_willeri", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(small_violet_treasure_box, 1);
                    st.giveItems(q_small_glass_box, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "fisher_willeri_q0029_0201.htm";
                } else
                    htmltext = "fisher_willeri_q0029_0202.htm";
            }
        } else if (npcId == magister_anabel) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_small_glass_box) >= 1) {
                    st.takeItems(q_small_glass_box, 1);
                    st.giveItems(plate_leather_gloves, 1);
                    st.removeMemo("strange_box_of_willeri");
                    st.removeMemo("strange_box_of_willeri_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "magister_anabel_q0029_0301.htm";
                } else
                    htmltext = "magister_anabel_q0029_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("strange_box_of_willeri");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == fisher_willeri) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "fisher_willeri_q0029_0103.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "fisher_willeri_q0029_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "fisher_willeri_q0029_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == fisher_willeri) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(small_violet_treasure_box) >= 1) {
                            st.setMemoState("strange_box_of_willeri_cookie", String.valueOf(1), true);
                            htmltext = "fisher_willeri_q0029_0105.htm";
                        } else
                            htmltext = "fisher_willeri_q0029_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "fisher_willeri_q0029_0203.htm";
                } else if (npcId == magister_anabel) {
                    if (st.ownItemCount(q_small_glass_box) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("strange_box_of_willeri_cookie", String.valueOf(2), true);
                        htmltext = "magister_anabel_q0029_0201.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}
