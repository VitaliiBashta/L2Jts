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
public class _10280_MutatedKaneusSchuttgart extends Quest {
    // npc
    private static final int captain_vishotuki = 31981;
    private static final int magister_atraxia = 31972;
    // mobs
    private static final int kanooth_63_boss = 18571;
    private static final int kanooth_66_boss = 18573;
    // questitem
    private static final int q_dna_of_kanooth9 = 13838;
    private static final int q_dna_of_kanooth10 = 13839;

    public _10280_MutatedKaneusSchuttgart() {
        super(true);
        addStartNpc(captain_vishotuki);
        addTalkId(magister_atraxia);
        addKillId(kanooth_63_boss, kanooth_66_boss);
        addQuestItem(q_dna_of_kanooth9, q_dna_of_kanooth10);
        addLevelCheck(58);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mutation_kanooth_schuttgart");
        int npcId = npc.getNpcId();
        if (npcId == captain_vishotuki) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mutation_kanooth_schuttgart", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "captain_vishotuki_q10280_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "captain_vishotuki_q10280_03.htm";
        } else if (npcId == magister_atraxia) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_dna_of_kanooth9) >= 1 && st.ownItemCount(q_dna_of_kanooth10) >= 1) {
                        st.giveItems(ADENA_ID, 210000);
                        st.takeItems(q_dna_of_kanooth9, -1);
                        st.takeItems(q_dna_of_kanooth10, -1);
                        st.removeMemo("mutation_kanooth_schuttgart");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "magister_atraxia_q10280_03.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mutation_kanooth_schuttgart");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == captain_vishotuki) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "captain_vishotuki_q10280_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "captain_vishotuki_q10280_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == captain_vishotuki) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth9) >= 1 && st.ownItemCount(q_dna_of_kanooth10) >= 1)
                            htmltext = "captain_vishotuki_q10280_07.htm";
                        else
                            htmltext = "captain_vishotuki_q10280_06.htm";
                    }
                } else if (npcId == magister_atraxia) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth9) >= 1 && st.ownItemCount(q_dna_of_kanooth10) >= 1)
                            htmltext = "magister_atraxia_q10280_02.htm";
                        else
                            htmltext = "magister_atraxia_q10280_01.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == captain_vishotuki)
                    htmltext = "captain_vishotuki_q10280_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mutation_kanooth_schuttgart");
        int npcId = npc.getNpcId();
        if (npcId == kanooth_63_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth9) == 0) {
                st.giveItems(q_dna_of_kanooth9, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kanooth_66_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth10) == 0) {
                st.giveItems(q_dna_of_kanooth10, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}