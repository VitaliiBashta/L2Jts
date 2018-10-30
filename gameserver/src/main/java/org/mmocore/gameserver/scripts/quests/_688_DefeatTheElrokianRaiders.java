package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
public class _688_DefeatTheElrokianRaiders extends Quest {
    // npc
    private final static int dindin = 32105;

    // mobs
    private final static int elcroki = 22214;

    // questitem
    private static int q_necklace_of_storming_party = 8785;

    public _688_DefeatTheElrokianRaiders() {
        super(true);
        addStartNpc(dindin);
        addKillId(elcroki);
        addQuestItem(q_necklace_of_storming_party);
        addLevelCheck(75, 99);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();

        if (npcId == dindin)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("repulse_the_elcroki", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "dindin_q0688_04.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "dindin_q0688_03.htm";
            else if (event.equalsIgnoreCase("reply_5")) {
                if (st.ownItemCount(q_necklace_of_storming_party) >= 1) {
                    if (st.ownItemCount(q_necklace_of_storming_party) >= 10)
                        st.giveItems(ADENA_ID, st.ownItemCount(q_necklace_of_storming_party) * 3000);
                    else
                        st.giveItems(ADENA_ID, st.ownItemCount(q_necklace_of_storming_party) * 3000);
                    st.takeItems(q_necklace_of_storming_party, -1);
                    htmltext = "dindin_q0688_07.htm";
                }
            } else if (event.equalsIgnoreCase("reply_7")) {
                if (st.ownItemCount(q_necklace_of_storming_party) >= 1) {
                    st.giveItems(ADENA_ID, st.ownItemCount(q_necklace_of_storming_party) * 3000);
                    st.removeMemo("repulse_the_elcroki");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dindin_q0688_08.htm";
                } else {
                    st.removeMemo("repulse_the_elcroki");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "dindin_q0688_09.htm";
                }
            } else if (event.equalsIgnoreCase("reply_8"))
                htmltext = "dindin_q0688_10.htm";
            else if (event.equalsIgnoreCase("reply_9")) {
                if (st.ownItemCount(q_necklace_of_storming_party) < 100)
                    htmltext = "dindin_q0688_11.htm";
                if (st.ownItemCount(q_necklace_of_storming_party) >= 100)
                    if (Rnd.get(1000) < 500) {
                        st.giveItems(ADENA_ID, 450000);
                        st.takeItems(q_necklace_of_storming_party, 100);
                        htmltext = "dindin_q0688_12.htm";
                    } else {
                        st.giveItems(ADENA_ID, 150000);
                        htmltext = "dindin_q0688_13.htm";
                        st.takeItems(q_necklace_of_storming_party, 100);
                    }
            }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("repulse_the_elcroki");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == dindin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "dindin_q0688_02.htm";
                            break;
                        default:
                            htmltext = "dindin_q0688_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == dindin)
                    if (GetMemoState == 1)
                        if (st.ownItemCount(q_necklace_of_storming_party) >= 1)
                            htmltext = "dindin_q0688_05.htm";
                        else if (st.ownItemCount(q_necklace_of_storming_party) < 1)
                            htmltext = "dindin_q0688_06.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("repulse_the_elcroki");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == elcroki) {
                int i4 = Rnd.get(1000);
                if (i4 < 448) {
                    st.giveItems(q_necklace_of_storming_party, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }

        return null;
    }
}