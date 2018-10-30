package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 04/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _026_TiredOfWaiting extends Quest {
    // npc
    private final static int isael_silvershadow = 30655;
    private final static int kitzka = 31045;
    // questitem
    private final static int q_delivery_box = 17281;
    // etcitem
    private final static int big_dragon_bone_summon = 17248;
    private final static int volition_antaras = 17266;
    private final static int sealed_blood_crystal = 17267;

    public _026_TiredOfWaiting() {
        super(true);
        addStartNpc(isael_silvershadow);
        addTalkId(kitzka);
        addQuestItem(q_delivery_box);
        addLevelCheck(80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("tired_of_waiting");
        int npcId = npc.getNpcId();
        if (npcId == isael_silvershadow) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("tired_of_waiting", String.valueOf(1), true);
                st.giveItems(q_delivery_box, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "isael_silvershadow_q0026_08.htm";
            } else if (event.equalsIgnoreCase("reply_1")) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "isael_silvershadow_q0026_03.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "isael_silvershadow_q0026_04.htm";
                        break;
                }
            } else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "isael_silvershadow_q0026_05.htm";
            else if (event.equalsIgnoreCase("reply_3"))
                htmltext = "isael_silvershadow_q0026_06.htm";
            else if (event.equalsIgnoreCase("reply_4"))
                htmltext = "isael_silvershadow_q0026_07.htm";
        } else if (npcId == kitzka) {
            if (event.equalsIgnoreCase("reply_1")) {
                if (GetMemoState == 1)
                    htmltext = "kitzka_q0026_02.htm";
            } else if (event.equalsIgnoreCase("reply_2")) {
                if (GetMemoState == 1)
                    htmltext = "kitzka_q0026_03.htm";
            } else if (event.equalsIgnoreCase("reply_3")) {
                if (GetMemoState == 1) {
                    st.takeItems(q_delivery_box, -1);
                    st.setMemoState("tired_of_waiting", String.valueOf(2), true);
                    htmltext = "kitzka_q0026_04.htm";
                }
            } else if (event.equalsIgnoreCase("reply_10")) {
                if (GetMemoState == 2)
                    htmltext = "kitzka_q0026_05.htm";
            } else if (event.equalsIgnoreCase("reply_11")) {
                if (GetMemoState == 2)
                    htmltext = "kitzka_q0026_07.htm";
            } else if (event.equalsIgnoreCase("reply_12")) {
                if (GetMemoState == 2)
                    htmltext = "kitzka_q0026_08.htm";
            } else if (event.equalsIgnoreCase("reply_13")) {
                if (GetMemoState == 2)
                    htmltext = "kitzka_q0026_09.htm";
            } else if (event.equalsIgnoreCase("reply_21")) {
                if (GetMemoState == 2) {
                    st.giveItems(big_dragon_bone_summon, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("tired_of_waiting");
                    htmltext = "kitzka_q0026_10.htm";
                }
            } else if (event.equalsIgnoreCase("reply_22")) {
                if (GetMemoState == 2) {
                    st.giveItems(volition_antaras, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("tired_of_waiting");
                    htmltext = "kitzka_q0026_11.htm";
                }
            } else if (event.equalsIgnoreCase("reply_23")) {
                if (GetMemoState == 2) {
                    st.giveItems(sealed_blood_crystal, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("tired_of_waiting");
                    htmltext = "kitzka_q0026_12.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("tired_of_waiting");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == isael_silvershadow)
                    htmltext = "isael_silvershadow_q0026_01.htm";
                break;
            case STARTED:
                if (npcId == isael_silvershadow) {
                    if (GetMemoState == 1)
                        htmltext = "isael_silvershadow_q0026_09.htm";
                } else if (npcId == kitzka) {
                    if (GetMemoState == 1)
                        htmltext = "kitzka_q0026_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "kitzka_q0026_06.htm";
                }
                break;
            case COMPLETED:
                if (npcId == isael_silvershadow)
                    htmltext = "isael_silvershadow_q0026_02.htm";
                break;
        }
        return htmltext;
    }
}