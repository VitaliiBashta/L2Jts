package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 24/01/2015
 */
public class _904_DragonTrophyAntharas extends Quest {
    // npc
    private static final int watcher_antaras_theodric = 30755;
    // mobs
    private static final int antaras_max = 29068;
    // etcitem
    private static final int g_pvp_point_token = 21874;
    // questitem
    private static final int q_portal_stone_1 = 3865;

    public _904_DragonTrophyAntharas() {
        super(PARTY_ALL);
        addStartNpc(watcher_antaras_theodric);
        addKillId(antaras_max);
        addLevelCheck(84);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == watcher_antaras_theodric) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("proof_of_antaras_hunting", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_antaras_theodric_q0904_07.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=904&reply=1"))
                htmltext = "watcher_antaras_theodric_q0904_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=904&reply=2"))
                htmltext = "watcher_antaras_theodric_q0904_06.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("proof_of_antaras_hunting");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_antaras_theodric) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "watcher_antaras_theodric_q0904_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.isNowAvailable()) {
                                if (st.ownItemCount(q_portal_stone_1) > 0)
                                    htmltext = "watcher_antaras_theodric_q0904_01.htm";
                                else
                                    htmltext = "watcher_antaras_theodric_q0904_04.htm";
                            } else
                                htmltext = "watcher_antaras_theodric_q0904_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == watcher_antaras_theodric) {
                    if (GetMemoState == 1)
                        htmltext = "watcher_antaras_theodric_q0904_08.htm";
                    else if (GetMemoState == 2) {
                        st.giveItems(g_pvp_point_token, 1);
                        st.removeMemo("proof_of_antaras_hunting");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(this);
                        htmltext = "watcher_antaras_theodric_q0904_09.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("proof_of_antaras_hunting");
        int npcId = npc.getNpcId();
        if (npcId == antaras_max) {
            if (GetMemoState == 1) {
                st.setCond(2);
                st.setMemoState("proof_of_antaras_hunting", String.valueOf(2), true);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}