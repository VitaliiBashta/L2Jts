package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
public class _654_JourneytoaSettlement extends Quest {
    // npc
    private static final int printessa_spirit = 31453;

    // mobs
    private static final int valley_antelope = 21294;
    private static final int antelope_slave = 21295;

    // questitem
    private static final int q_antelope_leather = 8072;
    private static final int q_ticket_to_frintessa = 8073;

    public _654_JourneytoaSettlement() {
        super(false);
        addStartNpc(printessa_spirit);
        addKillId(valley_antelope, antelope_slave);
        addQuestItem(q_antelope_leather);
        addLevelCheck(74, 80);
        addQuestCompletedCheck(119);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("to_reach_an_ending");
        int npcId = npc.getNpcId();

        if (npcId == printessa_spirit)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("to_reach_an_ending", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "printessa_spirit_q0654_03.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("to_reach_an_ending", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "printessa_spirit_q0654_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 2 && st.ownItemCount(q_antelope_leather) >= 1) {
                    st.giveItems(q_ticket_to_frintessa, 1);
                    st.takeItems(q_antelope_leather, -1);
                    st.removeMemo("to_reach_an_ending");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "printessa_spirit_q0654_07.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("to_reach_an_ending");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == printessa_spirit) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "printessa_spirit_q0654_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "printessa_spirit_q0654_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == printessa_spirit)
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("to_reach_an_ending", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "printessa_spirit_q0654_04.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_antelope_leather) == 0)
                        htmltext = "printessa_spirit_q0654_05.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_antelope_leather) >= 1)
                        htmltext = "printessa_spirit_q0654_06.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("to_reach_an_ending");
        int npcId = npc.getNpcId();

        if (GetMemoState == 2 && st.ownItemCount(q_antelope_leather) == 0)
            if (npcId == valley_antelope) {
                if (Rnd.get(100) < 84) {
                    st.setCond(3);
                    st.giveItems(q_antelope_leather, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            } else if (npcId == antelope_slave)
                if (Rnd.get(1000) < 893) {
                    st.setCond(3);
                    st.giveItems(q_antelope_leather, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
        return null;
    }
}