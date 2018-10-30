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
 * @date 16/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _109_InSearchOfTheNest extends Quest {
    // npc
    private static final int merc_cap_peace = 31553;
    private static final int corpse_of_scout = 32015;
    private static final int merc_kahmun = 31554;
    // questitem
    private static final int q_letter_of_scouter = 8083;
    private static final int q_letter_of_scouter_re = 14858;

    public _109_InSearchOfTheNest() {
        super(false);
        addStartNpc(merc_cap_peace);
        addTalkId(corpse_of_scout, merc_kahmun);
        addQuestItem(q_letter_of_scouter, q_letter_of_scouter_re);
        addLevelCheck(81);
        addQuestCompletedCheck(628);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("in_search_of_nest");
        int GetHTMLCookie = st.getInt("in_search_of_nest_cookie");
        int npcId = npc.getNpcId();
        if (npcId == merc_cap_peace) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("in_search_of_nest", String.valueOf(21), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "merc_cap_peace_q0109_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_letter_of_scouter) >= 1) {
                    st.setCond(3);
                    st.setMemoState("in_search_of_nest", String.valueOf(31), true);
                    st.takeItems(q_letter_of_scouter, -1);
                    htmltext = "merc_cap_peace_q0109_0301a.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(q_letter_of_scouter_re) >= 1) {
                    st.setCond(3);
                    st.setMemoState("in_search_of_nest", String.valueOf(35), true);
                    st.takeItems(q_letter_of_scouter_re, -1);
                    htmltext = "merc_cap_peace_q0109_0301a.htm";
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == corpse_of_scout) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                if (GetMemoState == 1 * 10 + 1) {
                    st.setCond(2);
                    st.setMemoState("in_search_of_nest", String.valueOf(25), true);
                    st.giveItems(q_letter_of_scouter, 1);
                    htmltext = "corpse_of_scout_q0109_0201.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 21) {
                    st.setCond(2);
                    st.setMemoState("in_search_of_nest", String.valueOf(25), true);
                    st.giveItems(q_letter_of_scouter_re, 1);
                    htmltext = "corpse_of_scout_q0109_0201.htm";
                    st.soundEffect(SOUND_MIDDLE);
                }
            }
        } else if (npcId == merc_kahmun) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 4 - 1) {
                if (GetMemoState == 31) {
                    st.addExpAndSp(146113, 13723);
                    st.giveItems(ADENA_ID, 25461);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "merc_kahmun_q0109_0401.htm";
                } else if (GetMemoState == 35) {
                    st.addExpAndSp(701500, 50000);
                    st.giveItems(ADENA_ID, 161500);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "merc_kahmun_q0109_0401.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("in_search_of_nest");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == merc_cap_peace) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "merc_cap_peace_q0109_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "merc_cap_peace_q0109_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == merc_cap_peace) {
                    if (GetMemoState == 21)
                        htmltext = "merc_cap_peace_q0109_0105.htm";
                    else if (GetMemoState == 25 && st.ownItemCount(q_letter_of_scouter) >= 1) {
                        st.setMemoState("in_search_of_nest_cookie", String.valueOf(2), true);
                        htmltext = "merc_cap_peace_q0109_0201.htm";
                    } else if (GetMemoState == 25 && st.ownItemCount(q_letter_of_scouter_re) >= 1) {
                        st.setMemoState("in_search_of_nest_cookie", String.valueOf(2), true);
                        htmltext = "merc_cap_peace_q0109_0201.htm";
                    } else if (GetMemoState > 30)
                        htmltext = "merc_cap_peace_q0109_0303.htm";
                } else if (npcId == corpse_of_scout) {
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState < 25) {
                        st.setMemoState("in_search_of_nest_cookie", String.valueOf(1), true);
                        htmltext = "corpse_of_scout_q0109_0101.htm";
                    } else if (GetMemoState == 25)
                        htmltext = "corpse_of_scout_q0109_0203.htm";
                } else if (npcId == merc_kahmun) {
                    if (GetMemoState > 30) {
                        st.setMemoState("in_search_of_nest_cookie", String.valueOf(3), true);
                        htmltext = "merc_kahmun_q0109_0301.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}