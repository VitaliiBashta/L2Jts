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
public class _10279_MutatedKaneusOren extends Quest {
    // npc
    private static final int mouen = 30196;
    private static final int rovia = 30189;
    // mobs
    private static final int kanooth_53_boss = 18566;
    private static final int kanooth_56_boss = 18568;
    // questitem
    private static final int q_dna_of_kanooth7 = 13836;
    private static final int q_dna_of_kanooth8 = 13837;

    public _10279_MutatedKaneusOren() {
        super(true);
        addStartNpc(mouen);
        addTalkId(rovia);
        addKillId(kanooth_53_boss, kanooth_56_boss);
        addQuestItem(q_dna_of_kanooth7, q_dna_of_kanooth8);
        addLevelCheck(48);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mutation_kanooth_oren");
        int npcId = npc.getNpcId();
        if (npcId == mouen) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mutation_kanooth_oren", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "mouen_q10279_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "mouen_q10279_03.htm";
        } else if (npcId == rovia) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_dna_of_kanooth7) >= 1 && st.ownItemCount(q_dna_of_kanooth8) >= 1) {
                        st.giveItems(ADENA_ID, 100000);
                        st.takeItems(q_dna_of_kanooth7, -1);
                        st.takeItems(q_dna_of_kanooth8, -1);
                        st.removeMemo("mutation_kanooth_oren");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "rovia_q10279_03.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mutation_kanooth_oren");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == mouen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "mouen_q10279_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "mouen_q10279_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == mouen) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth7) >= 1 && st.ownItemCount(q_dna_of_kanooth8) >= 1)
                            htmltext = "mouen_q10279_07.htm";
                        else
                            htmltext = "mouen_q10279_06.htm";
                    }
                } else if (npcId == rovia) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth7) >= 1 && st.ownItemCount(q_dna_of_kanooth8) >= 1)
                            htmltext = "rovia_q10279_02.htm";
                        else
                            htmltext = "rovia_q10279_01.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == mouen)
                    htmltext = "mouen_q10279_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mutation_kanooth_oren");
        int npcId = npc.getNpcId();
        if (npcId == kanooth_53_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth7) == 0) {
                st.giveItems(q_dna_of_kanooth7, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kanooth_56_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth8) == 0) {
                st.giveItems(q_dna_of_kanooth8, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}