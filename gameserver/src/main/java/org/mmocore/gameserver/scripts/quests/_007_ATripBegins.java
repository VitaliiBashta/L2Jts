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
public class _007_ATripBegins extends Quest {
    // npc
    private final static int mint = 30146;
    private final static int ariel = 30148;
    private final static int ozzy = 30154;
    // questitem
    private final static int q_recommendation_elf = 7572;
    private final static int q_symbol_of_traveler = 7570;
    // etcitem
    private final static int q_escape_scroll_giran = 7559;

    public _007_ATripBegins() {
        super(false);
        addStartNpc(mint);
        addTalkId(ariel, ozzy);
        addQuestItem(q_recommendation_elf);
        addLevelCheck(3, 10);
        addRaceCheck(PlayerRace.elf);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("journey_begins_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == mint) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("journey_begins", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "mint_q0007_0104.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 4 - 1) {
                st.giveItems(q_escape_scroll_giran, 1);
                st.giveItems(q_symbol_of_traveler, 1);
                st.removeMemo("journey_begins_cookie");
                st.removeMemo("journey_begins");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "mint_q0007_0401.htm";
            }
        } else if (npcId == ariel) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("journey_begins", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_recommendation_elf, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ariel_q0007_0201.htm";
            }
        } else if (npcId == ozzy) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_recommendation_elf) >= 1) {
                    st.setCond(3);
                    st.setMemoState("journey_begins", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_recommendation_elf, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ozzy_q0007_0301.htm";
                } else {
                    htmltext = "ozzy_q0007_0302.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("journey_begins");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == mint) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "mint_q0007_0103.htm";
                            break;
                        }
                        case RACE: {
                            htmltext = "mint_q0007_0102.htm";
                            break;
                        }
                        default: {
                            htmltext = "mint_q0007_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == mint) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "mint_q0007_0105.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("journey_begins_cookie", String.valueOf(3), true);
                        htmltext = "mint_q0007_0301.htm";
                    }
                } else if (npcId == ariel) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("journey_begins_cookie", String.valueOf(1), true);
                        htmltext = "ariel_q0007_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "ariel_q0007_0202.htm";
                    }
                } else if (npcId == ozzy) {
                    if (st.ownItemCount(q_recommendation_elf) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("journey_begins_cookie", String.valueOf(2), true);
                        htmltext = "ozzy_q0007_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        htmltext = "ozzy_q0007_0303.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}