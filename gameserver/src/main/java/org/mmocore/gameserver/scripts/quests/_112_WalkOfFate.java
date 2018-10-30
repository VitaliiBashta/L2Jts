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
public class _112_WalkOfFate extends Quest {
    // npc
    private static final int seer_livina = 30572;
    private static final int karuda = 32017;

    // etcitem
    private static final int scrl_of_ench_am_d = 956;

    public _112_WalkOfFate() {
        super(false);
        addStartNpc(seer_livina);
        addTalkId(karuda);
        addLevelCheck(20, 36);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("step_of_destiny");
        int npcId = npc.getNpcId();

        if (npcId == seer_livina) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("step_of_destiny", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "seer_livina_q0112_0104.htm";
            }
        } else if (npcId == karuda)
            if (event.equalsIgnoreCase("reply_3"))
                if (GetMemoState >= (2 - 1) * 10 + 1) {
                    st.giveItems(ADENA_ID, 22308);
                    st.addExpAndSp(112876, 5774);
                    st.giveItems(scrl_of_ench_am_d, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "karuda_q0112_0201.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("step_of_destiny");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == seer_livina) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "seer_livina_q0112_0103.htm";
                            break;
                        default:
                            htmltext = "seer_livina_q0112_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == seer_livina) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "seer_livina_q0112_0105.htm";
                } else if (npcId == karuda)
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "karuda_q0112_0101.htm";
                break;
        }
        return htmltext;
    }
}