package org.mmocore.gameserver.scripts.quests;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
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
public class _006_StepIntoTheFuture extends Quest {
    // npc
    private final static int rapunzel = 30006;
    private final static int baul = 30033;
    private final static int sir_collin_windawood = 30311;
    // questitem
    private final static int q_symbol_of_traveler = 7570;
    private final static int q_letter_paulo = 7571;
    // etcitem
    private final static int q_escape_scroll_giran = 7559;

    public _006_StepIntoTheFuture() {
        super(false);
        addStartNpc(rapunzel);
        addTalkId(baul, sir_collin_windawood);
        addQuestItem(q_letter_paulo);
        addLevelCheck(3, 10);
        addRaceCheck(PlayerRace.human);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("first_step_future_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == rapunzel) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("first_step_future", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "rapunzel_q0006_0104.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 4 - 1) {
                st.giveItems(q_escape_scroll_giran, 1);
                st.giveItems(q_symbol_of_traveler, 1);
                st.removeMemo("first_step_future_cookie");
                st.removeMemo("first_step_future");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "rapunzel_q0006_0401.htm";
            }
        } else if (npcId == baul) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("first_step_future", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_letter_paulo, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "baul_q0006_0201.htm";
            }
        } else if (npcId == sir_collin_windawood) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_letter_paulo) >= 1) {
                    st.setCond(3);
                    st.setMemoState("first_step_future", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_letter_paulo, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "sir_collin_windawood_q0006_0301.htm";
                } else {
                    htmltext = "sir_collin_windawood_q0006_0302.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("first_step_future");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == rapunzel) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "rapunzel_q0006_0103.htm";
                            break;
                        }
                        case RACE: {
                            htmltext = "rapunzel_q0006_0102.htm";
                            break;
                        }
                        default: {
                            htmltext = "rapunzel_q0006_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == rapunzel) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "rapunzel_q0006_0105.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("first_step_future_cookie", String.valueOf(3), true);
                        htmltext = "rapunzel_q0006_0301.htm";
                    }
                } else if (npcId == baul) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("first_step_future_cookie", String.valueOf(1), true);
                        htmltext = "baul_q0006_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "baul_q0006_0202.htm";
                    }
                } else if (npcId == sir_collin_windawood) {
                    if (st.ownItemCount(q_letter_paulo) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("first_step_future_cookie", String.valueOf(2), true);
                        htmltext = "sir_collin_windawood_q0006_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        htmltext = "sir_collin_windawood_q0006_0303.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}
