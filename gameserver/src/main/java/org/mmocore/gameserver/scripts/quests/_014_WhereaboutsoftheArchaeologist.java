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
public class _014_WhereaboutsoftheArchaeologist extends Quest {
    // npc
    private static final int trader_liesel = 31263;
    private static final int explorer_ghost_a = 31538;
    // questitem
    private static final int q_letter_to_explorer = 7253;

    public _014_WhereaboutsoftheArchaeologist() {
        super(false);
        addStartNpc(trader_liesel);
        addTalkId(explorer_ghost_a);
        addQuestItem(q_letter_to_explorer);
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("a_missing_archeologist_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == trader_liesel) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("a_missing_archeologist", String.valueOf(1 * 10 + 1), true);
                st.giveItems(q_letter_to_explorer, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_liesel_q0014_0104.htm";
            }
        } else if (npcId == explorer_ghost_a) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_letter_to_explorer) >= 1) {
                    st.takeItems(q_letter_to_explorer, -1);
                    st.giveItems(ADENA_ID, 136928);
                    st.addExpAndSp(325881, 32524);
                    st.removeMemo("a_missing_archeologist");
                    st.removeMemo("a_missing_archeologist_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "explorer_ghost_a_q0014_0201.htm";
                } else {
                    htmltext = "explorer_ghost_a_q0014_0202.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("a_missing_archeologist");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == trader_liesel) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "trader_liesel_q0014_0103.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "trader_liesel_q0014_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == trader_liesel) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "trader_liesel_q0014_0105.htm";
                    }
                } else if (npcId == explorer_ghost_a) {
                    if (st.ownItemCount(q_letter_to_explorer) >= 1 && GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("a_missing_archeologist_cookie", String.valueOf(1), true);
                        htmltext = "explorer_ghost_a_q0014_0101.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}