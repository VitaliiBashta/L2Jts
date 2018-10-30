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
 * @date 31/03/2016
 * @LastEdit 31/03/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _187_NikolasHeart extends Quest {
    // npc
    private static final int head_blacksmith_kusto = 30512;
    private static final int researcher_lorain = 30673;
    private static final int maestro_nikola = 30621;
    // questitem
    private static final int q_certificate_of_lorain = 10362;
    private static final int q_slate_of_metal_q0187 = 10368;

    public _187_NikolasHeart() {
        super(false);
        addStartNpc(researcher_lorain);
        addTalkId(maestro_nikola, head_blacksmith_kusto);
        addQuestItem(q_certificate_of_lorain, q_slate_of_metal_q0187);
        addLevelCheck(41);
        addQuestCompletedCheck(185);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("heart_of_nikola");
        int npcId = npc.getNpcId();
        if (npcId == researcher_lorain) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("heart_of_nikola", String.valueOf(1), true);
                st.giveItems(q_slate_of_metal_q0187, 1);
                st.takeItems(q_certificate_of_lorain, -1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "researcher_lorain_q0187_03.htm";
            }
        } else if (npcId == maestro_nikola) {
            if (event.equalsIgnoreCase("menu_select?ask=187&reply=1")) {
                if (GetMemoState == 1) {
                    htmltext = "maestro_nikola_q0187_02.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=187&reply=2")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("heart_of_nikola", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "maestro_nikola_q0187_03.htm";
                }
            }
        } else if (npcId == head_blacksmith_kusto) {
            if (event.equalsIgnoreCase("menu_select?ask=187&reply=1")) {
                if (GetMemoState == 2) {
                    htmltext = "head_blacksmith_kusto_q0187_02.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=187&reply=2")) {
                if (st.getPlayer().getLevel() < 47) {
                    st.giveItems(ADENA_ID, 93383);
                    st.addExpAndSp(285935, 18711);
                } else {
                    st.giveItems(ADENA_ID, 93383);
                }
                st.takeItems(q_slate_of_metal_q0187, -1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "head_blacksmith_kusto_q0187_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("heart_of_nikola");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == researcher_lorain) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "researcher_lorain_q0187_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_certificate_of_lorain) >= 1)
                                htmltext = "researcher_lorain_q0187_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == researcher_lorain) {
                    if (GetMemoState >= 1)
                        htmltext = "researcher_lorain_q0187_04.htm";
                } else if (npcId == maestro_nikola) {
                    if (GetMemoState == 1)
                        htmltext = "maestro_nikola_q0187_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "maestro_nikola_q0187_04.htm";
                } else if (npcId == head_blacksmith_kusto) {
                    if (GetMemoState == 2)
                        htmltext = "head_blacksmith_kusto_q0187_01.htm";
                }
                break;
        }
        return htmltext;
    }
}