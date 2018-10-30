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
public class _010_IntoTheWorld extends Quest {
    // npc
    private static final int elder_balanki = 30533;
    private static final int warehouse_chief_reed = 30520;
    private static final int gerald_priest_of_earth = 30650;
    // questitem
    private static final int q_symbol_of_traveler = 7570;
    private static final int q_expensive_necklace = 7574;
    // etcitem
    private static final int q_escape_scroll_giran = 7559;

    public _010_IntoTheWorld() {
        super(false);
        addStartNpc(elder_balanki);
        addTalkId(warehouse_chief_reed, gerald_priest_of_earth);
        addQuestItem(q_expensive_necklace);
        addLevelCheck(3, 10);
        addRaceCheck(PlayerRace.dwarf);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("my_first_adventure_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == elder_balanki) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("my_first_adventure", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "elder_balanki_q0010_0104.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 5 - 1) {
                st.giveItems(q_escape_scroll_giran, 1);
                st.giveItems(q_symbol_of_traveler, 1);
                st.removeMemo("my_first_adventure");
                st.removeMemo("my_first_adventure_cookie");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "elder_balanki_q0010_0501.htm";
            }
        } else if (npcId == warehouse_chief_reed) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("my_first_adventure", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_expensive_necklace, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "warehouse_chief_reed_q0010_0201.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1) {
                st.setCond(4);
                st.setMemoState("my_first_adventure", String.valueOf(4 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "warehouse_chief_reed_q0010_0401.htm";
            }
        } else if (npcId == gerald_priest_of_earth) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_expensive_necklace) >= 1) {
                    st.setCond(3);
                    st.setMemoState("my_first_adventure", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_expensive_necklace, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "gerald_priest_of_earth_q0010_0301.htm";
                } else {
                    htmltext = "gerald_priest_of_earth_q0010_0302.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("my_first_adventure");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == elder_balanki) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "elder_balanki_q0010_0103.htm";
                            st.exitQuest(true);
                            break;
                        }
                        case RACE: {
                            htmltext = "elder_balanki_q0010_0102.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "elder_balanki_q0010_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == elder_balanki) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "elder_balanki_q0010_0105.htm";
                    } else if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("my_first_adventure_cookie", String.valueOf(4), true);
                        htmltext = "elder_balanki_q0010_0401.htm";
                    }
                } else if (npcId == warehouse_chief_reed) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("my_first_adventure_cookie", String.valueOf(1), true);
                        htmltext = "warehouse_chief_reed_q0010_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "warehouse_chief_reed_q0010_0202.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("my_first_adventure_cookie", String.valueOf(3), true);
                        htmltext = "warehouse_chief_reed_q0010_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1) {
                        htmltext = "warehouse_chief_reed_q0010_0402.htm";
                    }
                } else if (npcId == gerald_priest_of_earth) {
                    if (st.ownItemCount(q_expensive_necklace) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("my_first_adventure_cookie", String.valueOf(2), true);
                        htmltext = "gerald_priest_of_earth_q0010_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        htmltext = "gerald_priest_of_earth_q0010_0303.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}