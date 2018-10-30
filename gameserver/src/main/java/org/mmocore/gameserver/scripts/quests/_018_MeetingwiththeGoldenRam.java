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
public class _018_MeetingwiththeGoldenRam extends Quest {
    // npc
    private static final int warehouse_chief_donal = 31314;
    private static final int freighter_daisy = 31315;
    private static final int supplier_abercrombie = 31555;
    // questitem
    private static final int q_mercs_supplies = 7245;

    public _018_MeetingwiththeGoldenRam() {
        super(false);
        addStartNpc(warehouse_chief_donal);
        addTalkId(freighter_daisy, supplier_abercrombie);
        addQuestItem(q_mercs_supplies);
        addLevelCheck(66, 76);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("meet_golden_rams_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == warehouse_chief_donal) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("meet_golden_rams", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "warehouse_chief_donal_q0018_0104.htm";
            }
        } else if (npcId == freighter_daisy) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("meet_golden_rams", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_mercs_supplies, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "freighter_daisy_q0018_0201.htm";
            }
        } else if (npcId == supplier_abercrombie) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_mercs_supplies) >= 1) {
                    st.takeItems(q_mercs_supplies, -1);
                    st.giveItems(ADENA_ID, 40000);
                    st.addExpAndSp(126668, 11731);
                    st.removeMemo("meet_golden_rams");
                    st.removeMemo("meet_golden_rams_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "supplier_abercrombie_q0018_0301.htm";
                } else {
                    htmltext = "supplier_abercrombie_q0018_0302.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("meet_golden_rams");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == warehouse_chief_donal) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "warehouse_chief_donal_q0018_0103.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "warehouse_chief_donal_q0018_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == warehouse_chief_donal) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "warehouse_chief_donal_q0018_0105.htm";
                    }
                } else if (npcId == freighter_daisy) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("meet_golden_rams_cookie", String.valueOf(1), true);
                        htmltext = "freighter_daisy_q0018_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "freighter_daisy_q0018_0202.htm";
                    }
                } else if (npcId == supplier_abercrombie) {
                    if (st.ownItemCount(q_mercs_supplies) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("meet_golden_rams_cookie", String.valueOf(2), true);
                        htmltext = "supplier_abercrombie_q0018_0201.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}