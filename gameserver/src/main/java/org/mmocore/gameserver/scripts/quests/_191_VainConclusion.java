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
public class _191_VainConclusion extends Quest {
    // npc
    private static final int head_blacksmith_kusto = 30512;
    private static final int researcher_lorain = 30673;
    private static final int dorothy_the_locksmith = 30970;
    private static final int shegfield = 30068;

    // questitem
    private static final int q_fragment_q0191 = 10371;

    public _191_VainConclusion() {
        super(false);
        addStartNpc(dorothy_the_locksmith);
        addTalkId(dorothy_the_locksmith, head_blacksmith_kusto, researcher_lorain, shegfield);
        addQuestItem(q_fragment_q0191);
        addLevelCheck(42);
        addQuestCompletedCheck(188);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("state_of_despondency");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("state_of_despondency", String.valueOf(1), true);
            st.giveItems(q_fragment_q0191, 1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "dorothy_the_locksmith_q0191_04.htm";
        } else if (event.equalsIgnoreCase("reply_1")) {
            if (GetMemoState == 4) {
                st.giveItems(ADENA_ID, 117327);
                if (st.getPlayer().getLevel() < 48)
                    st.addExpAndSp(309467, 20614);
                st.removeMemo("state_of_despondency");
                st.exitQuest(false);
                st.soundEffect(SOUND_FINISH);
                htmltext = "head_blacksmith_kusto_q0191_02.htm";
            }
        } else if (event.equalsIgnoreCase("reply_1a")) {
            if (GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("state_of_despondency", String.valueOf(2), true);
                st.takeItems(q_fragment_q0191, -1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "researcher_lorain_q0191_02.htm";
            }
        } else if (event.equalsIgnoreCase("reply_2"))
            if (GetMemoState == 2) {
                st.setCond(3);
                st.setMemoState("state_of_despondency", String.valueOf(3), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "shegfield_q0191_03.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("state_of_despondency");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == dorothy_the_locksmith) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                        case QUEST:
                            htmltext = "dorothy_the_locksmith_q0191_02.htm";
                            break;
                        default:
                            htmltext = "dorothy_the_locksmith_q0191_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == dorothy_the_locksmith) {
                    if (GetMemoState >= 1)
                        htmltext = "dorothy_the_locksmith_q0191_05.htm";
                } else if (npcId == head_blacksmith_kusto) {
                    if (GetMemoState == 4)
                        htmltext = "head_blacksmith_kusto_q0191_01.htm";
                } else if (npcId == researcher_lorain) {
                    if (GetMemoState == 1)
                        htmltext = "researcher_lorain_q0191_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "researcher_lorain_q0191_03.htm";
                    else if (GetMemoState == 3) {
                        st.setCond(4);
                        st.setMemoState("state_of_despondency", String.valueOf(4), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "researcher_lorain_q0191_04.htm";
                    } else if (GetMemoState == 4)
                        htmltext = "researcher_lorain_q0191_05.htm";
                } else if (npcId == shegfield)
                    if (GetMemoState == 2)
                        htmltext = "shegfield_q0191_01.htm";
                    else if (GetMemoState == 3)
                        htmltext = "shegfield_q0191_04.htm";
                break;
        }
        return htmltext;
    }
}