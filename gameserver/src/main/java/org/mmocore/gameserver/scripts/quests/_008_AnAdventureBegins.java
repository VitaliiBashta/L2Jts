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
public class _008_AnAdventureBegins extends Quest {
    // npc
    private final static int jasmine = 30134;
    private final static int sentry_roseline = 30355;
    private final static int harne = 30144;
    // questitem
    private final static int q_roseline_paper = 7573;
    private final static int q_symbol_of_traveler = 7570;
    // etcitem
    private final static int q_escape_scroll_giran = 7559;

    public _008_AnAdventureBegins() {
        super(false);
        addStartNpc(jasmine);
        addTalkId(sentry_roseline, harne);
        addQuestItem(q_roseline_paper);
        addLevelCheck(3, 10);
        addRaceCheck(PlayerRace.darkelf);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("adventure_begins_cookie");
        final int npcId = npc.getNpcId();

        if (npcId == jasmine) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("adventure_begins", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "jasmine_q0008_0104.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 4 - 1) {
                st.giveItems(q_escape_scroll_giran, 1);
                st.giveItems(q_symbol_of_traveler, 1);
                st.removeMemo("adventure_begins_cookie");
                st.removeMemo("adventure_begins");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "jasmine_q0008_0401.htm";
            }
        } else if (npcId == sentry_roseline) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("adventure_begins", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_roseline_paper, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "sentry_roseline_q0008_0201.htm";
            }
        } else if (npcId == harne) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_roseline_paper) >= 1) {
                    st.setCond(3);
                    st.setMemoState("adventure_begins", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_roseline_paper, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "harne_q0008_0301.htm";
                } else {
                    htmltext = "harne_q0008_0302.htm";
                }
            }
        }

        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("adventure_begins");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == jasmine) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "jasmine_q0008_0103.htm";
                            break;
                        }
                        case RACE: {
                            htmltext = "jasmine_q0008_0102.htm";
                            break;
                        }
                        default: {
                            htmltext = "jasmine_q0008_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == jasmine) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "jasmine_q0008_0105.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("adventure_begins_cookie", String.valueOf(3), true);
                        htmltext = "jasmine_q0008_0301.htm";
                    }
                } else if (npcId == sentry_roseline) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("adventure_begins_cookie", String.valueOf(1), true);
                        htmltext = "sentry_roseline_q0008_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "sentry_roseline_q0008_0202.htm";
                    }
                } else if (npcId == harne) {
                    if (st.ownItemCount(q_roseline_paper) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("adventure_begins_cookie", String.valueOf(2), true);
                        htmltext = "harne_q0008_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        htmltext = "harne_q0008_0303.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}