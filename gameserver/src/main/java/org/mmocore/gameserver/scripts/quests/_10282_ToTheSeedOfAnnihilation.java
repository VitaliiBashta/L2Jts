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
public class _10282_ToTheSeedOfAnnihilation extends Quest {
    // npc
    private final static int kbarldire = 32733;
    private final static int clemis = 32734;

    // questitem
    private final static int q_letter_to_seed_of_annihilation = 15512;

    public _10282_ToTheSeedOfAnnihilation() {
        super(false);
        addStartNpc(kbarldire);
        addTalkId(kbarldire);
        addTalkId(clemis);
        addLevelCheck(84);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("toward_the_seed_of_annihilation", String.valueOf(1), true);
            st.giveItems(q_letter_to_seed_of_annihilation, 1);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "kbarldire_q10282_08.htm";
        } else if (event.equalsIgnoreCase("reply_1")) {
            st.giveItems(ADENA_ID, 212182);
            st.addExpAndSp(1148480, 99110);
            st.takeItems(q_letter_to_seed_of_annihilation, -1);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            st.removeMemo("toward_the_seed_of_annihilation");
            htmltext = "clemis_q10282_02.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("toward_the_seed_of_annihilation");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == kbarldire) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "kbarldire_q10282_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "kbarldire_q10282_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == kbarldire && GetMemoState == 1)
                    htmltext = "kbarldire_q10282_09.htm";
                else if (npcId == clemis)
                    if (GetMemoState == 1 && st.ownItemCount(q_letter_to_seed_of_annihilation) >= 1)
                        htmltext = "clemis_q10282_01.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_letter_to_seed_of_annihilation) < 1)
                        htmltext = "clemis_q10282_03.htm";
                break;
            case COMPLETED:
                if (npcId == kbarldire)
                    htmltext = "kbarldire_q10282_02.htm";
                else if (npcId == clemis)
                    htmltext = "clemis_q10282_03.htm";
                break;
        }
        return htmltext;
    }
}