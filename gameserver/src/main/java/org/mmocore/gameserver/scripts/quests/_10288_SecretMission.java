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
public class _10288_SecretMission extends Quest {
    // npc
    private final static int falsepriest_dominic = 31350;
    private final static int falsepriest_aquilani = 32780;
    private final static int new_falsepriest_gremory = 32757;

    // questitem
    private final static int q_letter_of_falsepriest_dominic = 15529;

    public _10288_SecretMission() {
        super(false);
        addStartNpc(falsepriest_dominic);
        addTalkId(falsepriest_aquilani, new_falsepriest_gremory);
        addQuestItem(q_letter_of_falsepriest_dominic);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("secret_mission");
        int npcId = npc.getNpcId();

        if (npcId == falsepriest_dominic) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("secret_mission", String.valueOf(1), true);
                st.giveItems(q_letter_of_falsepriest_dominic, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "falsepriest_dominic_q10288_06.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                if (st.getPlayer().getLevel() < 82)
                    htmltext = "falsepriest_dominic_q10288_02a.htm";
                else if (st.getPlayer().getLevel() >= 82)
                    htmltext = "falsepriest_dominic_q10288_03.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (st.getPlayer().getLevel() >= 82)
                    htmltext = "falsepriest_dominic_q10288_04.htm";
            } else if (event.equalsIgnoreCase("reply_3"))
                if (st.getPlayer().getLevel() >= 82)
                    htmltext = "falsepriest_dominic_q10288_05.htm";
        } else if (npcId == falsepriest_aquilani) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1 && st.ownItemCount(q_letter_of_falsepriest_dominic) >= 1) {
                    st.setMemoState("secret_mission", String.valueOf(2), true);
                    htmltext = "falsepriest_aquilani_q10288_02.htm";
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 2) {
                    st.setCond(2);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "falsepriest_aquilani_q10288_03.htm";
                }
        } else if (npcId == new_falsepriest_gremory)
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 2 && st.ownItemCount(q_letter_of_falsepriest_dominic) >= 1)
                    htmltext = "new_falsepriest_gremory_q10288_02.htm";
            } else if (event.equalsIgnoreCase("reply_2"))
                if (GetMemoState == 2 && st.ownItemCount(q_letter_of_falsepriest_dominic) >= 1) {
                    st.giveItems(ADENA_ID, 106583);
                    st.addExpAndSp(417788, 46320);
                    st.removeMemo("secret_mission");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "new_falsepriest_gremory_q10288_03.htm";
                }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("secret_mission");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == falsepriest_dominic) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "falsepriest_dominic_q10288_02a.htm";
                            break;
                        default:
                            htmltext = "falsepriest_dominic_q10288_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == falsepriest_dominic) {
                    if (GetMemoState == 1)
                        htmltext = "falsepriest_dominic_q10288_07.htm";
                } else if (npcId == falsepriest_aquilani) {
                    if (GetMemoState == 1 && st.ownItemCount(q_letter_of_falsepriest_dominic) >= 1)
                        htmltext = "falsepriest_aquilani_q10288_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "falsepriest_aquilani_q10288_04.htm";
                } else if (npcId == new_falsepriest_gremory)
                    if (GetMemoState == 2 && st.ownItemCount(q_letter_of_falsepriest_dominic) >= 1)
                        htmltext = "new_falsepriest_gremory_q10288_01.htm";
                break;
            case COMPLETED:
                if (npcId == falsepriest_dominic)
                    htmltext = "falsepriest_dominic_q10288_02.htm";
                break;
        }

        return htmltext;
    }
}