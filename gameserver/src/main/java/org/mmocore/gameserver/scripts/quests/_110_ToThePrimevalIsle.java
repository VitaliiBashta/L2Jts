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
 * @date 14/12/2014
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _110_ToThePrimevalIsle extends Quest {
    // npc
    private static final int scroll_seller_anton = 31338;
    private static final int marquez = 32113;
    // questitem
    private static final int q_book_of_primitive_island = 8777;

    public _110_ToThePrimevalIsle() {
        super(false);
        addStartNpc(scroll_seller_anton);
        addTalkId(marquez);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("let_go_primitive_island");
        int npcId = npc.getNpcId();
        if (npcId == scroll_seller_anton) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("let_go_primitive_island", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                st.giveItems(q_book_of_primitive_island, 1);
                htmltext = "scroll_seller_anton_q0110_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "scroll_seller_anton_q0110_03.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "scroll_seller_anton_q0110_04.htm";
        } else if (npcId == marquez) {
            if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1)
                    htmltext = "marquez_q0110_03.htm";
            } else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "marquez_q0110_04.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                if (GetMemoState == 1) {
                    st.giveItems(ADENA_ID, 191678);
                    st.addExpAndSp(251602, 25245);
                    st.takeItems(q_book_of_primitive_island, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("let_go_primitive_island");
                    st.exitQuest(false);
                    htmltext = "marquez_q0110_05.htm";
                }
            } else if (event.equalsIgnoreCase("reply_6")) {
                if (GetMemoState == 1) {
                    st.giveItems(ADENA_ID, 191678);
                    st.addExpAndSp(251602, 25245);
                    st.takeItems(q_book_of_primitive_island, -1);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("let_go_primitive_island");
                    st.exitQuest(false);
                    htmltext = "marquez_q0110_06.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("let_go_primitive_island");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == scroll_seller_anton) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "scroll_seller_anton_q0110_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "scroll_seller_anton_q0110_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == scroll_seller_anton) {
                    if (GetMemoState == 1)
                        htmltext = "scroll_seller_anton_q0110_07.htm";
                } else if (npcId == marquez) {
                    if (GetMemoState == 1)
                        htmltext = "marquez_q0110_01.htm";
                }
                break;
        }
        return htmltext;
    }
}