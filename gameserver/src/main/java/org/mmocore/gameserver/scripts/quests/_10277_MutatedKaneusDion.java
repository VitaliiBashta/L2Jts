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
 * @version 1.0
 * @date 16/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10277_MutatedKaneusDion extends Quest {
    // npc
    private static final int lucas = 30071;
    private static final int magister_mirien = 30461;
    // mobs
    private static final int kanooth_33_boss = 18558;
    private static final int kanooth_36_boss = 18559;
    // questitem
    private static final int q_dna_of_kanooth3 = 13832;
    private static final int q_dna_of_kanooth4 = 13833;

    public _10277_MutatedKaneusDion() {
        super(true);
        addStartNpc(lucas);
        addTalkId(magister_mirien);
        addKillId(kanooth_33_boss, kanooth_36_boss);
        addQuestItem(q_dna_of_kanooth3, q_dna_of_kanooth4);
        addLevelCheck(28);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mutation_kanooth_dion");
        int npcId = npc.getNpcId();
        if (npcId == lucas) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mutation_kanooth_dion", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "lucas_q10277_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "lucas_q10277_03.htm";
        } else if (npcId == magister_mirien) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_dna_of_kanooth3) >= 1 && st.ownItemCount(q_dna_of_kanooth4) >= 1) {
                        st.giveItems(ADENA_ID, 20000);
                        st.takeItems(q_dna_of_kanooth3, -1);
                        st.takeItems(q_dna_of_kanooth4, -1);
                        st.removeMemo("mutation_kanooth_dion");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "magister_mirien_q10277_03.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mutation_kanooth_dion");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == lucas) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "lucas_q10277_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "lucas_q10277_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == lucas) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth3) >= 1 && st.ownItemCount(q_dna_of_kanooth4) >= 1)
                            htmltext = "lucas_q10277_07.htm";
                        else
                            htmltext = "lucas_q10277_06.htm";
                    }
                } else if (npcId == magister_mirien) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth3) >= 1 && st.ownItemCount(q_dna_of_kanooth4) >= 1)
                            htmltext = "magister_mirien_q10277_02.htm";
                        else
                            htmltext = "magister_mirien_q10277_01.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == lucas)
                    htmltext = "lucas_q10277_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mutation_kanooth_dion");
        int npcId = npc.getNpcId();
        if (npcId == kanooth_33_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth3) == 0) {
                st.giveItems(q_dna_of_kanooth3, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kanooth_36_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth4) == 0) {
                st.giveItems(q_dna_of_kanooth4, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}