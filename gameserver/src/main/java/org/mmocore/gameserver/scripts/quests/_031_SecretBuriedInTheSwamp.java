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
public class _031_SecretBuriedInTheSwamp extends Quest {
    // npc
    private final static int supplier_abercrombie = 31555;
    private final static int old_slate1 = 31661;
    private final static int old_slate2 = 31662;
    private final static int old_slate3 = 31663;
    private final static int old_slate4 = 31664;
    private final static int dead_dwarf = 31665;

    // questitem
    private final static int q_krolins_journal = 7252;

    public _031_SecretBuriedInTheSwamp() {
        super(false);
        addStartNpc(supplier_abercrombie);
        addTalkId(old_slate1, old_slate2, old_slate3, old_slate4, dead_dwarf);
        addQuestItem(q_krolins_journal);
        addLevelCheck(66, 76);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("secret_in_swamp_cookie");
        int npcId = npc.getNpcId();

        if (npcId == supplier_abercrombie) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("secret_in_swamp", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "supplier_abercrombie_q0031_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_krolins_journal) >= 1) {
                    st.setCond(3);
                    st.setMemoState("secret_in_swamp", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_krolins_journal, 1);
                    htmltext = "supplier_abercrombie_q0031_0301.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "supplier_abercrombie_q0031_0302.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 8 - 1) {
                st.giveItems(ADENA_ID, 120000);
                st.addExpAndSp(490000, 45880);
                st.removeMemo("secret_in_swamp");
                st.removeMemo("secret_in_swamp_cookie");
                st.exitQuest(false);
                st.soundEffect(SOUND_FINISH);
                htmltext = "supplier_abercrombie_q0031_0801.htm";
            }
        } else if (npcId == old_slate1) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1) {
                st.setCond(4);
                st.setMemoState("secret_in_swamp", String.valueOf(4 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "old_slate1_q0031_0401.htm";
            }
        } else if (npcId == old_slate2) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 5 - 1) {
                st.setCond(5);
                st.setMemoState("secret_in_swamp", String.valueOf(5 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "old_slate2_q0031_0501.htm";
            }
        } else if (npcId == old_slate3) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 6 - 1) {
                st.setCond(6);
                st.setMemoState("secret_in_swamp", String.valueOf(6 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "old_slate3_q0031_0601.htm";
            }
        } else if (npcId == old_slate4) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 7 - 1) {
                st.setCond(7);
                st.setMemoState("secret_in_swamp", String.valueOf(7 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "old_slate4_q0031_0701.htm";
            }
        } else if (npcId == dead_dwarf)
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("secret_in_swamp", String.valueOf(2 * 10 + 1), true);
                st.giveItems(q_krolins_journal, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "dead_dwarf_q0031_0201.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("secret_in_swamp");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == supplier_abercrombie) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "supplier_abercrombie_q0031_0103.htm";
                            break;
                        default:
                            htmltext = "supplier_abercrombie_q0031_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == supplier_abercrombie) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "supplier_abercrombie_q0031_0105.htm";
                    else if (st.ownItemCount(q_krolins_journal) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(2), true);
                        htmltext = "supplier_abercrombie_q0031_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "supplier_abercrombie_q0031_0303.htm";
                    else if (GetMemoState == 7 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(7), true);
                        htmltext = "supplier_abercrombie_q0031_0701.htm";
                    }
                } else if (npcId == old_slate1) {
                    if (GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(3), true);
                        htmltext = "old_slate1_q0031_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "old_slate1_q0031_0402.htm";
                } else if (npcId == old_slate2) {
                    if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(4), true);
                        htmltext = "old_slate2_q0031_0401.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "old_slate2_q0031_0502.htm";
                } else if (npcId == old_slate3) {
                    if (GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(5), true);
                        htmltext = "old_slate3_q0031_0501.htm";
                    } else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "old_slate3_q0031_0602.htm";
                } else if (npcId == old_slate4) {
                    if (GetMemoState == 6 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(6), true);
                        htmltext = "old_slate4_q0031_0601.htm";
                    } else if (GetMemoState == 7 * 10 + 1)
                        htmltext = "old_slate4_q0031_0702.htm";
                } else if (npcId == dead_dwarf)
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("secret_in_swamp_cookie", String.valueOf(1), true);
                        htmltext = "dead_dwarf_q0031_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "dead_dwarf_q0031_0203.htm";
                break;
        }
        return htmltext;
    }
}