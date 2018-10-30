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
public class _249_PoisonedPlainsOfTheLizardmen extends Quest {
    // NPC
    private static final int mouen = 30196;
    private static final int johny = 32744;

    public _249_PoisonedPlainsOfTheLizardmen() {
        super(false);
        addStartNpc(mouen);
        addTalkId(mouen, johny);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("poisoning_lizard_grasslands");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("poisoning_lizard_grasslands", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "mouen_q0249_04.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 1) {
            st.giveItems(ADENA_ID, 83056);
            st.addExpAndSp(477496, 58743);
            st.soundEffect(SOUND_FINISH);
            st.exitQuest(false);
            st.removeMemo("poisoning_lizard_grasslands");
            htmltext = "johny_q0249_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("poisoning_lizard_grasslands");
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == mouen) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "mouen_q0249_02.htm";
                            break;
                        default:
                            htmltext = "mouen_q0249_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == mouen && GetMemoState == 1)
                    htmltext = "mouen_q0249_05.htm";
                else if (npcId == johny && GetMemoState == 1)
                    htmltext = "johny_q0249_01.htm";
                else
                    htmltext = "johny_q0249_02.htm";
                break;
            case COMPLETED:
                if (npcId == mouen)
                    htmltext = "mouen_q0249_06.htm";
                else if (npcId == johny)
                    htmltext = "johny_q0249_03.htm";
                break;
        }
        return htmltext;
    }
}