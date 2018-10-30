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
public class _011_SecretMeetingWithKetraOrcs extends Quest {
    // npc
    private static final int guard_cadmon = 31296;
    private static final int trader_leon = 31256;
    private static final int herald_wakan = 31371;
    // questitem
    private static final int q_cargo_for_ketra = 7231;

    public _011_SecretMeetingWithKetraOrcs() {
        super(false);
        addStartNpc(guard_cadmon);
        addTalkId(trader_leon, herald_wakan);
        addQuestItem(q_cargo_for_ketra);
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("meet_ketra_orcs_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == guard_cadmon) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("meet_ketra_orcs", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "guard_cadmon_q0011_0104.htm";
            }
        } else if (npcId == trader_leon) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("meet_ketra_orcs", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_cargo_for_ketra, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_leon_q0011_0201.htm";
            }
        } else if (npcId == herald_wakan) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_cargo_for_ketra) >= 1) {
                    st.takeItems(q_cargo_for_ketra, -1);
                    st.addExpAndSp(82045, 6047);
                    st.removeMemo("meet_ketra_orcs");
                    st.removeMemo("meet_ketra_orcs_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "herald_wakan_q0011_0301.htm";
                } else {
                    htmltext = "herald_wakan_q0011_0302.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("meet_ketra_orcs");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == guard_cadmon) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "guard_cadmon_q0011_0103.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "guard_cadmon_q0011_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == guard_cadmon) {
                    if (GetMemoState == 1 * 10 + 1) {
                        htmltext = "guard_cadmon_q0011_0105.htm";
                    }
                } else if (npcId == trader_leon) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("meet_ketra_orcs_cookie", String.valueOf(1), true);
                        htmltext = "trader_leon_q0011_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1) {
                        htmltext = "trader_leon_q0011_0202.htm";
                    }
                } else if (npcId == herald_wakan) {
                    if (st.ownItemCount(q_cargo_for_ketra) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("meet_ketra_orcs_cookie", String.valueOf(2), true);
                        htmltext = "herald_wakan_q0011_0201.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}
