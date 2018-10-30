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
public class _362_BardsMandolin extends Quest {
    // npc
    private static final int galion = 30958;
    private static final int nanarin = 30956;
    private static final int swan = 30957;
    private static final int trader_woodrow = 30837;
    // questitem
    private static final int q_swans_flute = 4316;
    private static final int q_letter_of_swan = 4317;
    // etcitem
    private static final int q_musicnote_journey = 4410;

    public _362_BardsMandolin() {
        super(false);
        addStartNpc(swan);
        addTalkId(trader_woodrow, galion, nanarin);
        addQuestItem(q_swans_flute, q_letter_of_swan);
        addLevelCheck(15);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mandolin_of_minstrel");
        int npcId = npc.getNpcId();
        if (npcId == swan) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mandolin_of_minstrel", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "swan_q0362_03.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetMemoState == 5) {
                st.giveItems(ADENA_ID, 10000);
                st.giveItems(q_musicnote_journey, 1);
                st.removeMemo("mandolin_of_minstrel");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "swan_q0362_07.htm";
            } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 5) {
                st.giveItems(ADENA_ID, 10000);
                st.giveItems(q_musicnote_journey, 1);
                st.removeMemo("mandolin_of_minstrel");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "swan_q0362_08.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mandolin_of_minstrel");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == swan) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "swan_q0362_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "swan_q0362_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == swan) {
                    if (GetMemoState >= 1 && GetMemoState < 3)
                        htmltext = "swan_q0362_04.htm";
                    else if (GetMemoState == 3 || GetMemoState == 4) {
                        if (GetMemoState == 3) {
                            st.setCond(4);
                            st.setMemoState("mandolin_of_minstrel", String.valueOf(4), true);
                            st.giveItems(q_letter_of_swan, 1);
                            st.soundEffect(SOUND_MIDDLE);
                            htmltext = "swan_q0362_05.htm";
                        } else
                            htmltext = "swan_q0362_05.htm";
                    } else if (GetMemoState == 5)
                        htmltext = "swan_q0362_06.htm";
                } else if (npcId == trader_woodrow) {
                    if (GetMemoState == 1) {
                        st.setCond(2);
                        st.setMemoState("mandolin_of_minstrel", String.valueOf(2), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "woodrow_q0362_01.htm";
                    } else if (GetMemoState == 2)
                        htmltext = "woodrow_q0362_02.htm";
                    else if (GetMemoState >= 3)
                        htmltext = "woodrow_q0362_03.htm";
                } else if (npcId == galion) {
                    if (GetMemoState == 2) {
                        st.setCond(3);
                        st.setMemoState("mandolin_of_minstrel", String.valueOf(3), true);
                        st.giveItems(q_swans_flute, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "galion_q0362_01.htm";
                    } else if (GetMemoState >= 3)
                        htmltext = "galion_q0362_02.htm";
                } else if (npcId == nanarin) {
                    if (GetMemoState == 4 && st.ownItemCount(q_letter_of_swan) > 0 && st.ownItemCount(q_swans_flute) > 0) {
                        st.setCond(5);
                        st.setMemoState("mandolin_of_minstrel", String.valueOf(5), true);
                        st.takeItems(q_letter_of_swan, 1);
                        st.takeItems(q_swans_flute, 1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "nanarin_q0362_01.htm";
                    } else if (GetMemoState >= 5)
                        htmltext = "nanarin_q0362_02.htm";
                }
                break;
        }
        return htmltext;
    }
}