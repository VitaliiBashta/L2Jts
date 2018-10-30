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
public class _10276_MutatedKaneusGludio extends Quest {
    // npc
    private static final int captain_bathia = 30332;
    private static final int magister_rohmer = 30344;

    // mobs
    private static final int kanooth_23_boss = 18554;
    private static final int kanooth_26_boss = 18555;

    // questitem
    private static final int q_dna_of_kanooth1 = 13830;
    private static final int q_dna_of_kanooth2 = 13831;

    public _10276_MutatedKaneusGludio() {
        super(true);
        addStartNpc(captain_bathia);
        addTalkId(magister_rohmer);
        addKillId(kanooth_23_boss, kanooth_26_boss);
        addQuestItem(q_dna_of_kanooth1, q_dna_of_kanooth2);
        addLevelCheck(18);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("mutation_kanooth_gludio");
        int npcId = npc.getNpcId();

        if (npcId == captain_bathia) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("mutation_kanooth_gludio", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "captain_bathia_q10276_05.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "captain_bathia_q10276_03.htm";
        } else if (npcId == magister_rohmer)
            if (GetMemoState == 1)
                if (st.ownItemCount(q_dna_of_kanooth1) >= 1 && st.ownItemCount(q_dna_of_kanooth2) >= 1) {
                    st.giveItems(ADENA_ID, 8500);
                    st.takeItems(q_dna_of_kanooth1, -1);
                    st.takeItems(q_dna_of_kanooth2, -1);
                    st.removeMemo("mutation_kanooth_gludio");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "magister_rohmer_q10276_03.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetOneTimeQuestFlag = st.getState();
        int GetMemoState = st.getInt("mutation_kanooth_gludio");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED: {
                if (npcId == captain_bathia) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "captain_bathia_q10276_04.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "captain_bathia_q10276_01.htm";
                            break;
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == captain_bathia) {
                    if (GetOneTimeQuestFlag == 1)
                        htmltext = "captain_bathia_q10276_02.htm";
                    else if (GetMemoState == 1)
                        if (st.ownItemCount(q_dna_of_kanooth1) >= 1 && st.ownItemCount(q_dna_of_kanooth2) >= 1)
                            htmltext = "captain_bathia_q10276_07.htm";
                        else
                            htmltext = "captain_bathia_q10276_06.htm";
                } else if (npcId == magister_rohmer)
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_dna_of_kanooth1) >= 1 && st.ownItemCount(q_dna_of_kanooth2) >= 1)
                            htmltext = "magister_rohmer_q10276_02.htm";
                        else
                            htmltext = "magister_rohmer_q10276_01.htm";
            }
            break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("mutation_kanooth_gludio");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth1) == 0) {
            if (npcId == kanooth_23_boss) {
                st.giveItems(q_dna_of_kanooth1, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (GetMemoState == 1 && st.ownItemCount(q_dna_of_kanooth2) == 0)
            if (npcId == kanooth_26_boss) {
                st.giveItems(q_dna_of_kanooth2, 1);
                st.soundEffect(SOUND_ITEMGET);
            }
        return null;
    }
}