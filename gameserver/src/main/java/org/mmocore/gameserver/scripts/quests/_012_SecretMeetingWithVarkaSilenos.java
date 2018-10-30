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
public class _012_SecretMeetingWithVarkaSilenos extends Quest {
    // npc
    private static final int guard_cadmon = 31296;
    private static final int trader_helmut = 31258;
    private static final int herald_naran = 31378;
    // questitem
    private static final int q_cargo_for_barka = 7232;

    public _012_SecretMeetingWithVarkaSilenos() {
        super(false);
        addStartNpc(guard_cadmon);
        addTalkId(trader_helmut, herald_naran);
        addQuestItem(q_cargo_for_barka);
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("meet_barka_orcs_cookie");
        int npcId = npc.getNpcId();
        if (npcId == guard_cadmon) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("meet_barka_orcs", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "guard_cadmon_q0012_0104.htm";
            }
        } else if (npcId == trader_helmut) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("meet_barka_orcs", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_cargo_for_barka, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_helmut_q0012_0201.htm";
            }
        } else if (npcId == herald_naran) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_cargo_for_barka) >= 1) {
                    st.takeItems(q_cargo_for_barka, -1);
                    st.addExpAndSp(233125, 18142);
                    st.removeMemo("meet_barka_orcs");
                    st.removeMemo("meet_barka_orcs_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "herald_naran_q0012_0301.htm";
                } else
                    htmltext = "herald_naran_q0012_0302.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("meet_barka_orcs");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == guard_cadmon) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "guard_cadmon_q0012_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "guard_cadmon_q0012_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == guard_cadmon) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "guard_cadmon_q0012_0105.htm";
                } else if (npcId == trader_helmut) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("meet_barka_orcs_cookie", String.valueOf(1), true);
                        htmltext = "trader_helmut_q0012_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "trader_helmut_q0012_0202.htm";
                } else if (npcId == herald_naran) {
                    if (st.ownItemCount(q_cargo_for_barka) >= 0 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("meet_barka_orcs_cookie", String.valueOf(2), true);
                        htmltext = "herald_naran_q0012_0201.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}
