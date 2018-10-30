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
public class _10268_ToTheSeedOfInfinity extends Quest {
    // npc
    private final static int kserth = 32548;
    private final static int officer_tepios = 32603;

    // questitem
    private final static int q_letter_to_seed_of_immortality = 13811;

    public _10268_ToTheSeedOfInfinity() {
        super(false);
        addStartNpc(kserth);
        addTalkId(officer_tepios);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("toward_the_seed_of_immortality", String.valueOf(1), true);
            st.giveItems(q_letter_to_seed_of_immortality, 1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "kserth_q10268_07.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("toward_the_seed_of_immortality");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == kserth) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kserth_q10268_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kserth_q10268_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kserth && GetMemoState == 1)
                    htmltext = "kserth_q10268_08.htm";
                else if (npcId == officer_tepios && GetMemoState == 1 && st.ownItemCount(q_letter_to_seed_of_immortality) >= 1) {
                    st.addExpAndSp(100640, 10098);
                    st.giveItems(ADENA_ID, 16671);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("toward_the_seed_of_immortality");
                    htmltext = "officer_tepios_q10268_01.htm";
                }
                break;
            case COMPLETED:
                if (npcId == kserth)
                    htmltext = "kserth_q10268_03.htm";
                else if (npcId == officer_tepios)
                    htmltext = "officer_tepios_q10268_02.htm";
                break;
        }
        return htmltext;
    }
}