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
public class _10278_MutatedKaneusHeine extends Quest {
    // npc
    private static final int captain_gosta = 30916;
    private static final int magister_minevia = 30907;
    // mobs
    private static final int kanooth_43_boss = 18562;
    private static final int kanooth_46_boss = 18564;
    // questitem
    private static final int q_dna_of_kanooth5 = 13834;
    private static final int q_dna_of_kanooth6 = 13835;

    public _10278_MutatedKaneusHeine() {
        super(true);
        addStartNpc(captain_gosta);
        addTalkId(magister_minevia);
        addKillId(kanooth_43_boss, kanooth_46_boss);
        addQuestItem(q_dna_of_kanooth5, q_dna_of_kanooth6);
        addLevelCheck(38);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mutation_kanooth_heiness");
        int npcId = npc.getNpcId();
        if (npcId == captain_gosta) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mutation_kanooth_heiness", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "captain_gosta_q10278_05.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "captain_gosta_q10278_03.htm";
        } else if (npcId == magister_minevia) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1) {
                    if (st.ownItemCount(q_dna_of_kanooth5) >= 1 && st.ownItemCount(q_dna_of_kanooth6) >= 1) {
                        st.giveItems(ADENA_ID, 50000);
                        st.takeItems(q_dna_of_kanooth5, -1);
                        st.takeItems(q_dna_of_kanooth6, -1);
                        st.removeMemo("mutation_kanooth_heiness");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "magister_minevia_q10278_03.htm";
                    }
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("mutation_kanooth_heiness");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == captain_gosta) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "captain_gosta_q10278_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "captain_gosta_q10278_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == captain_gosta) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth5) >= 1 && st.ownItemCount(q_dna_of_kanooth6) >= 1)
                            htmltext = "captain_gosta_q10278_07.htm";
                        else
                            htmltext = "captain_gosta_q10278_06.htm";
                    }
                } else if (npcId == magister_minevia) {
                    if (GetMemoState == 1) {
                        if (st.ownItemCount(q_dna_of_kanooth5) >= 1 && st.ownItemCount(q_dna_of_kanooth6) >= 1)
                            htmltext = "magister_minevia_q10278_02.htm";
                        else
                            htmltext = "magister_minevia_q10278_01.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == captain_gosta)
                    htmltext = "captain_gosta_q10278_02.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mutation_kanooth_heiness");
        int npcId = npc.getNpcId();
        if (npcId == kanooth_43_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth5) == 0) {
                st.giveItems(q_dna_of_kanooth5, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == kanooth_46_boss) {
            if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth6) == 0) {
                st.giveItems(q_dna_of_kanooth6, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}