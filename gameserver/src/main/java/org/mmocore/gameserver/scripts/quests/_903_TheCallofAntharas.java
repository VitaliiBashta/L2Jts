package org.mmocore.gameserver.scripts.quests;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

/**
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 15/01/2015
 */
public class _903_TheCallofAntharas extends Quest {
    // npc
    private static final int watcher_antaras_theodric = 30755;
    // mobs
    private static final int tarasque_dragon = 29190;
    private static final int behemoth_dragon = 29069;
    // questitem
    private static final int q_g_skin_of_tarasque_dragon = 21991;
    private static final int q_g_skin_of_behemoth_dragon = 21992;
    // etcitem
    private static final int g_call_of_earth_dragon1 = 21897;
    private static final int q_portal_stone_1 = 3865;

    public _903_TheCallofAntharas() {
        super(PARTY_ALL);
        addStartNpc(watcher_antaras_theodric);
        addKillId(tarasque_dragon, behemoth_dragon);
        addQuestItem(q_g_skin_of_behemoth_dragon, q_g_skin_of_tarasque_dragon);
        addLevelCheck(83);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == watcher_antaras_theodric) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("call_of_earth_dragon", String.valueOf(1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_antaras_theodric_q0903_06.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "watcher_antaras_theodric_q0903_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("call_of_earth_dragon");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_antaras_theodric) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "watcher_antaras_theodric_q0903_03.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.isNowAvailable()) {
                                if (st.ownItemCount(q_portal_stone_1) > 0)
                                    htmltext = "watcher_antaras_theodric_q0903_01.htm";
                                else
                                    htmltext = "watcher_antaras_theodric_q0903_04.htm";
                            } else
                                htmltext = "watcher_antaras_theodric_q0903_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == watcher_antaras_theodric) {
                    if (GetMemoState == 1 && (st.ownItemCount(q_g_skin_of_tarasque_dragon) < 1 || st.ownItemCount(q_g_skin_of_behemoth_dragon) < 1))
                        htmltext = "watcher_antaras_theodric_q0903_07.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_g_skin_of_tarasque_dragon) >= 1 && st.ownItemCount(q_g_skin_of_behemoth_dragon) >= 1) {
                        st.giveItems(g_call_of_earth_dragon1, 1);
                        st.takeItems(q_g_skin_of_tarasque_dragon, -1);
                        st.takeItems(q_g_skin_of_behemoth_dragon, -1);
                        st.removeMemo("call_of_earth_dragon");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(this);
                        htmltext = "watcher_antaras_theodric_q0903_08.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("call_of_earth_dragon");
        int npcId = npc.getNpcId();
        if (npcId == tarasque_dragon) {
            if (GetMemoState == 1 && st.ownItemCount(q_g_skin_of_tarasque_dragon) < 1) {
                if (st.ownItemCount(q_g_skin_of_behemoth_dragon) >= 1 && st.ownItemCount(q_g_skin_of_tarasque_dragon) >= 0) {
                    st.setCond(2);
                    st.setMemoState("call_of_earth_dragon", String.valueOf(2), true);
                    st.giveItems(q_g_skin_of_tarasque_dragon, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(q_g_skin_of_tarasque_dragon) < 1) {
                    st.setCond(1);
                    st.giveItems(q_g_skin_of_tarasque_dragon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == behemoth_dragon) {
            if (GetMemoState == 1 && st.ownItemCount(q_g_skin_of_behemoth_dragon) < 1) {
                if (st.ownItemCount(q_g_skin_of_behemoth_dragon) >= 0 && st.ownItemCount(q_g_skin_of_tarasque_dragon) >= 1) {
                    st.setCond(2);
                    st.setMemoState("call_of_earth_dragon", String.valueOf(2), true);
                    st.giveItems(q_g_skin_of_behemoth_dragon, 1);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (st.ownItemCount(q_g_skin_of_behemoth_dragon) < 1) {
                    st.setCond(1);
                    st.giveItems(q_g_skin_of_behemoth_dragon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}