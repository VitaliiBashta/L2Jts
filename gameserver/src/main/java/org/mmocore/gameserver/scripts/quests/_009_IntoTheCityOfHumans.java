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
public class _009_IntoTheCityOfHumans extends Quest {
    // npc
    private static final int centurion_petukai = 30583;
    private static final int seer_tanapi = 30571;
    private static final int gatekeeper_tamil = 30576;
    // etcitem
    private static final int q_escape_scroll_giran = 7559;
    // questitem
    private static final int q_symbol_of_traveler = 7570;

    public _009_IntoTheCityOfHumans() {
        super(false);
        addStartNpc(centurion_petukai);
        addTalkId(seer_tanapi, gatekeeper_tamil);
        addRaceCheck(PlayerRace.orc);
        addLevelCheck(3, 10);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("city_of_man_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == centurion_petukai) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("city_of_man", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "centurion_petukai_q0009_0104.htm";
            }
        } else if (npcId == seer_tanapi) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("city_of_man", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "seer_tanapi_q0009_0201.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                st.giveItems(q_escape_scroll_giran, 1);
                st.giveItems(q_symbol_of_traveler, 1);
                st.removeMemo("city_of_man_cookie");
                st.removeMemo("city_of_man");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "gatekeeper_tamil_q0009_0301.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("city_of_man");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == centurion_petukai) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "centurion_petukai_q0009_0103.htm";
                            break;
                        }
                        case RACE: {
                            htmltext = "centurion_petukai_q0009_0102.htm";
                            break;
                        }
                        default: {
                            htmltext = "centurion_petukai_q0009_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == centurion_petukai) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "centurion_petukai_q0009_0105.htm";
                    }
                } else if (npcId == seer_tanapi) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("city_of_man_cookie", String.valueOf(1), true);
                        htmltext = "seer_tanapi_q0009_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "seer_tanapi_q0009_0202.htm";
                    }
                } else if (npcId == gatekeeper_tamil) {
                    if (GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("city_of_man_cookie", String.valueOf(2), true);
                        htmltext = "gatekeeper_tamil_q0009_0201.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}