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
public class _10281_MutatedKaneusRune extends Quest {
    // npc
    private static final int captain_mathias = 31340;
    private static final int magister_kayan = 31335;
    // mobs
    private static final int kanooth_73_boss = 18577;
    // questitem
    private static final int q_dna_of_kanooth11 = 13840;

    public _10281_MutatedKaneusRune() {
        super(true);
        addStartNpc(captain_mathias);
        addTalkId(magister_kayan);
        addKillId(kanooth_73_boss);
        addQuestItem(q_dna_of_kanooth11);
        addLevelCheck(68);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mutation_kanooth_rune");
        int npcId = npc.getNpcId();
        if (npcId == captain_mathias) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mutation_kanooth_rune", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "captain_mathias_q10281_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "captain_mathias_q10281_03.htm";
        } else if (npcId == magister_kayan) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_dna_of_kanooth11) >= 1) {
                        st.giveItems(ADENA_ID, 360000);
                        st.takeItems(q_dna_of_kanooth11, -1);
                        st.removeMemo("mutation_kanooth_rune");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "magister_kayan_q10281_03.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mutation_kanooth_rune");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == captain_mathias) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "captain_mathias_q10281_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "captain_mathias_q10281_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == captain_mathias) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth11) >= 1)
                            htmltext = "captain_mathias_q10281_07.htm";
                        else
                            htmltext = "captain_mathias_q10281_06.htm";
                    }
                } else if (npcId == magister_kayan) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth11) >= 1)
                            htmltext = "magister_kayan_q10281_02.htm";
                        else
                            htmltext = "magister_kayan_q10281_01.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == captain_mathias)
                    htmltext = "captain_mathias_q10281_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mutation_kanooth_rune");
        int npcId = npc.getNpcId();
        if (npcId == kanooth_73_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth11) == 0) {
                st.giveItems(q_dna_of_kanooth11, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}