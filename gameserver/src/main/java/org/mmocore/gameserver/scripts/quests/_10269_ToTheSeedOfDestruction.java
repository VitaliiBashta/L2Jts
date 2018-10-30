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
public class _10269_ToTheSeedOfDestruction extends Quest {
    // npc
    private final static int kserth = 32548;
    private final static int servant_of_kserth = 32526;

    // questitem
    private final static int q_letter_to_seed_of_destruction = 13812;

    public _10269_ToTheSeedOfDestruction() {
        super(false);
        addStartNpc(kserth);
        addTalkId(servant_of_kserth);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("toward_the_seed_of_Destruction", String.valueOf(1), true);
            st.giveItems(q_letter_to_seed_of_destruction, 1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "kserth_q10269_07.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("toward_the_seed_of_Destruction");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == kserth) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kserth_q10269_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kserth_q10269_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kserth && GetMemoState == 1)
                    htmltext = "kserth_q10269_08.htm";
                else if (npcId == servant_of_kserth && GetMemoState == 1 && st.ownItemCount(q_letter_to_seed_of_destruction) >= 1) {
                    st.addExpAndSp(176121, 17671);
                    st.giveItems(ADENA_ID, 29174);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("toward_the_seed_of_Destruction");
                    htmltext = "servant_of_kserth_q10269_01.htm";
                }
                break;
            case COMPLETED:
                if (npcId == kserth)
                    htmltext = "kserth_q10269_03.htm";
                else if (npcId == servant_of_kserth)
                    htmltext = "servant_of_kserth_q10269_02.htm";
                break;
        }
        return htmltext;
    }
}