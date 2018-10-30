package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 20/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _183_RelicExploration extends Quest {
    // NPC
    private static final int head_blacksmith_kusto = 30512;
    private static final int researcher_lorain = 30673;
    private static final int maestro_nikola = 30621;

    public _183_RelicExploration() {
        super(false);
        addStartNpc(head_blacksmith_kusto);
        addTalkId(researcher_lorain, maestro_nikola);
        addLevelCheck(40);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("probe_luins_of_giant");
        int npcId = npc.getNpcId();
        if (npcId == head_blacksmith_kusto) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("probe_luins_of_giant", String.valueOf(1), true);
                st.soundEffect(SOUND_ACCEPT);
                st.setState(STARTED);
                htmltext = "head_blacksmith_kusto_q0183_04.htm";
            } else if (event.equalsIgnoreCase("reply=1"))
                htmltext = "head_blacksmith_kusto_q0183_02.htm";
        } else if (npcId == researcher_lorain) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 1)
                    htmltext = "researcher_lorain_q0183_02.htm";
            } else if (event.equalsIgnoreCase("reply=2")) {
                if (GetMemoState == 1)
                    htmltext = "researcher_lorain_q0183_03.htm";
            } else if (event.equalsIgnoreCase("reply=3")) {
                if (GetMemoState == 1) {
                    st.setCond(2);
                    st.setMemoState("probe_luins_of_giant", String.valueOf(2), true);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "researcher_lorain_q0183_04.htm";
                }
            }
        } else if (npcId == maestro_nikola) {
            if (event.equalsIgnoreCase("reply=1")) {
                if (GetMemoState == 2) {
                    st.giveItems(ADENA_ID, 18100);
                    if (st.getPlayer().getLevel() < 46)
                        st.addExpAndSp(60000, 3000);
                    st.soundEffect(SOUND_FINISH);
                    st.removeMemo("probe_luins_of_giant");
                    htmltext = "maestro_nikola_q0183_02.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=184&reply=1")) {
                Quest q1 = QuestManager.getQuest(_184_ArtOfPersuasion.class);
                if (q1 != null) {
                    QuestState qs1 = q1.newQuestState(st.getPlayer(), STARTED);
                    q1.notifyEvent("maestro_nikola_q0184_03.htm", qs1, npc);
                    st.exitQuest(false);
                }
                return null;
            } else if (event.equalsIgnoreCase("menu_select?ask=185&reply=1")) {
                Quest q2 = QuestManager.getQuest(_185_NikolasCooperation.class);
                if (q2 != null) {
                    QuestState qs2 = q2.newQuestState(st.getPlayer(), STARTED);
                    q2.notifyEvent("maestro_nikola_q0185_03.htm", qs2, npc);
                    st.exitQuest(false);
                }
                return null;
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int GetMemoState = st.getInt("probe_luins_of_giant");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == head_blacksmith_kusto) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "head_blacksmith_kusto_q0183_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "head_blacksmith_kusto_q0183_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == head_blacksmith_kusto) {
                    if (GetMemoState == 1)
                        htmltext = "head_blacksmith_kusto_q0183_05.htm";
                } else if (npcId == researcher_lorain) {
                    if (GetMemoState == 1)
                        htmltext = "researcher_lorain_q0183_01.htm";
                    else if (GetMemoState == 2)
                        htmltext = "researcher_lorain_q0183_05.htm";
                } else if (npcId == maestro_nikola) {
                    if (GetMemoState == 2)
                        htmltext = "maestro_nikola_q0183_01.htm";
                }
                break;
        }
        return htmltext;
    }
}