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
public class _10271_TheEnvelopingDarkness extends Quest {
    // npc
    private static final int wharf_soldier_orbiu = 32560;
    private static final int soldier_el = 32556;
    private static final int corpse_of_medival = 32528;

    // questitem
    private static final int q_doc_of_investigator = 13852;

    public _10271_TheEnvelopingDarkness() {
        super(false);
        addStartNpc(wharf_soldier_orbiu);
        addTalkId(wharf_soldier_orbiu, soldier_el, corpse_of_medival);
        addQuestItem(q_doc_of_investigator);
        addLevelCheck(75);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("spreaded_darkness");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("spreaded_darkness", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "wharf_soldier_orbiu_q10271_05.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 1) {
            st.setCond(2);
            st.setMemoState("spreaded_darkness", String.valueOf(2), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "soldier_el_q10271_06.htm";
        } else if (event.equalsIgnoreCase("reply_10") && GetMemoState == 3 && st.ownItemCount(q_doc_of_investigator) >= 1) {
            st.setCond(4);
            st.setMemoState("spreaded_darkness", String.valueOf(4), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "soldier_el_q10271_09.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("spreaded_darkness");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == wharf_soldier_orbiu) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "wharf_soldier_orbiu_q10271_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "wharf_soldier_orbiu_q10271_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == wharf_soldier_orbiu) {
                    if (GetMemoState == 1)
                        htmltext = "wharf_soldier_orbiu_q10271_06.htm";
                    else if (GetMemoState == 2 || GetMemoState == 3)
                        htmltext = "wharf_soldier_orbiu_q10271_07.htm";
                    else if (GetMemoState == 4 && st.ownItemCount(q_doc_of_investigator) >= 1) {
                        st.giveItems(ADENA_ID, 62516);
                        st.addExpAndSp(377403, 37867);
                        st.exitQuest(false);
                        st.soundEffect(SOUND_FINISH);
                        st.removeMemo("spreaded_darkness");
                        htmltext = "wharf_soldier_orbiu_q10271_08.htm";
                    }
                } else if (npcId == soldier_el) {
                    if (GetMemoState == 1)
                        htmltext = "soldier_el_q10271_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "soldier_el_q10271_07.htm";
                    else if (GetMemoState == 3 && st.ownItemCount(q_doc_of_investigator) >= 1)
                        htmltext = "soldier_el_q10271_08.htm";
                } else if (npcId == corpse_of_medival)
                    if (GetMemoState == 2) {
                        st.setCond(3);
                        st.setMemoState("spreaded_darkness", String.valueOf(3), true);
                        st.giveItems(q_doc_of_investigator, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "corpse_of_medival_q10271_01.htm";
                    } else if (GetMemoState == 3)
                        htmltext = "corpse_of_medival_q10271_03.htm";
                break;
            case COMPLETED:
                if (npcId == wharf_soldier_orbiu)
                    htmltext = "wharf_soldier_orbiu_q10271_03.htm";
                else if (npcId == soldier_el)
                    htmltext = "soldier_el_q10271_02.htm";
                else if (npcId == corpse_of_medival)
                    htmltext = "corpse_of_medival_q10271_02.htm";
                break;
        }
        return htmltext;
    }
}