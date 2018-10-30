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
public class _119_LastImperialPrince extends Quest {
    // npc
    private static final int printessa_spirit = 31453;
    private static final int frintessa_nurse = 32009;

    // questitem
    private static final int q_antique_brooch = 7262;

    public _119_LastImperialPrince() {
        super(false);
        addStartNpc(printessa_spirit);
        addTalkId(frintessa_nurse);
        addQuestItem(q_antique_brooch);
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("the_last_imperial_prince");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("the_last_imperial_prince", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "printessa_spirit_q0119_06.htm";
        } else if (event.equalsIgnoreCase("reply_4") && GetMemoState == 2) {
            st.giveItems(ADENA_ID, 150292, true);
            st.addExpAndSp(902439, 90067);
            st.soundEffect(SOUND_FINISH);
            st.removeMemo("the_last_imperial_prince");
            htmltext = "printessa_spirit_q0119_10.htm";
            st.exitQuest(false);
        } else if (event.equalsIgnoreCase("reply_1")) {
            if (st.ownItemCount(q_antique_brooch) >= 1)
                htmltext = "frintessa_nurse_q0119_02.htm";
            else
                htmltext = "frintessa_nurse_q0119_02a.htm";
        } else if (event.equalsIgnoreCase("reply_2") && GetMemoState == 1 && st.ownItemCount(q_antique_brooch) >= 1) {
            st.setCond(2);
            st.setMemoState("the_last_imperial_prince", String.valueOf(2), true);
            st.soundEffect(SOUND_MIDDLE);
            htmltext = "frintessa_nurse_q0119_03.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("the_last_imperial_prince");
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == printessa_spirit) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "printessa_spirit_q0119_02.htm";
                            break;
                        default:
                            if (st.ownItemCount(q_antique_brooch) >= 1)
                                htmltext = "printessa_spirit_q0119_01.htm";
                            else
                                htmltext = "printessa_spirit_q0119_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == printessa_spirit) {
                    if (GetMemoState == 1 && st.ownItemCount(q_antique_brooch) >= 1)
                        htmltext = "printessa_spirit_q0119_07.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_antique_brooch) == 0) {
                        htmltext = "printessa_spirit_q0119_07a.htm";
                        st.exitQuest(true);
                    } else if (GetMemoState == 2)
                        htmltext = "printessa_spirit_q0119_08.htm";
                } else if (npcId == frintessa_nurse)
                    if (GetMemoState == 1)
                        htmltext = "frintessa_nurse_q0119_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "frintessa_nurse_q0119_04.htm";
                break;
            case COMPLETED:
                if (npcId == printessa_spirit)
                    htmltext = "printessa_spirit_q0119_03.htm";
                break;
        }
        return htmltext;
    }
}