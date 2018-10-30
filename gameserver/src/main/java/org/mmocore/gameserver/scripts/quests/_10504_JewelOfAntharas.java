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
 * @date 07/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _10504_JewelOfAntharas extends Quest {
    // npc
    private static final int watcher_antaras_theodric = 30755;
    // mobs
    private static final int antaras_max = 29068;
    // questitem
    private static final int q_portal_stone_1 = 3865;
    private static final int q_g_empty_orb1 = 21905;
    private static final int q_brimful_orb1 = 21907;
    // etcitem
    private static final int g_orb_of_earth_dragon1 = 21898;

    public _10504_JewelOfAntharas() {
        super(PARTY_ALL);
        addStartNpc(watcher_antaras_theodric);
        addQuestItem(q_g_empty_orb1, q_brimful_orb1);
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
                st.setMemoState("stone_of_antaras", String.valueOf(1), true);
                st.giveItems(q_g_empty_orb1, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_antaras_theodric_q10504_07.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "watcher_antaras_theodric_q10504_05.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "watcher_antaras_theodric_q10504_06.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("stone_of_antaras");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_antaras_theodric) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "watcher_antaras_theodric_q10504_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_portal_stone_1) > 0)
                                htmltext = "watcher_antaras_theodric_q10504_01.htm";
                            else
                                htmltext = "watcher_antaras_theodric_q10504_04.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == watcher_antaras_theodric) {
                    if (GetMemoState == 1 && st.ownItemCount(q_g_empty_orb1) >= 1)
                        htmltext = "watcher_antaras_theodric_q10504_08.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_g_empty_orb1) == 0)
                        htmltext = "watcher_antaras_theodric_q10504_09.htm";
                    else if (GetMemoState == 2) {
                        st.giveItems(g_orb_of_earth_dragon1, 1);
                        st.takeItems(q_brimful_orb1, 1);
                        st.removeMemo("stone_of_antaras");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "watcher_antaras_theodric_q10504_10.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == watcher_antaras_theodric)
                    htmltext = "watcher_antaras_theodric_q10504_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("jewel_antharas");
        int npcId = npc.getNpcId();
        if (npcId == antaras_max) {
            if (GetMemoState == 1 && st.ownItemCount(q_g_empty_orb1) > 0) {
                st.setCond(2);
                st.setMemoState("stone_of_antaras", String.valueOf(2), true);
                st.takeItems(q_g_empty_orb1, -1);
                st.giveItems(q_brimful_orb1, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}