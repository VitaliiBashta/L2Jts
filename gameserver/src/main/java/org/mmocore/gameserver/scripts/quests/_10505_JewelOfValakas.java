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
public class _10505_JewelOfValakas extends Quest {
    // npc
    private static final int watcher_valakas_klein = 31540;
    // mobs
    private static final int valakas = 29028;
    // questitem
    private static final int q_g_empty_orb2 = 21906;
    private static final int q_brimful_orb2 = 21908;
    private static final int q_floating_stone = 7267;
    // etcitem
    private static final int g_orb_of_fire_dragon1 = 21896;

    public _10505_JewelOfValakas() {
        super(PARTY_ALL);
        addStartNpc(watcher_valakas_klein);
        addQuestItem(q_g_empty_orb2, q_brimful_orb2);
        addKillId(valakas);
        addLevelCheck(84);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int npcId = npc.getNpcId();
        if (npcId == watcher_valakas_klein) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("stone_of_valarakas", String.valueOf(1), true);
                st.giveItems(q_g_empty_orb2, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "watcher_valakas_klein_q10505_07.htm";
            } else if (event.equalsIgnoreCase("reply_1"))
                htmltext = "watcher_valakas_klein_q10505_05.htm";
            else if (event.equalsIgnoreCase("reply_2"))
                htmltext = "watcher_valakas_klein_q10505_06.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("stone_of_valarakas");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == watcher_valakas_klein) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "watcher_valakas_klein_q10505_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_floating_stone) > 0)
                                htmltext = "watcher_valakas_klein_q10505_01.htm";
                            else
                                htmltext = "watcher_valakas_klein_q10505_04.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == watcher_valakas_klein) {
                    if (GetMemoState == 1 && st.ownItemCount(q_g_empty_orb2) >= 1)
                        htmltext = "watcher_valakas_klein_q10505_08.htm";
                    else if (GetMemoState == 1 && st.ownItemCount(q_g_empty_orb2) == 0)
                        htmltext = "watcher_valakas_klein_q10505_09.htm";
                    else if (GetMemoState == 2) {
                        st.giveItems(g_orb_of_fire_dragon1, 1);
                        st.takeItems(q_brimful_orb2, 1);
                        st.removeMemo("stone_of_valarakas");
                        st.soundEffect(SOUND_FINISH);
                        st.exitQuest(false);
                        htmltext = "watcher_valakas_klein_q10505_10.htm";
                    }
                }
                break;
            case COMPLETED:
                if (npcId == watcher_valakas_klein)
                    htmltext = "watcher_valakas_klein_q10505_03.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("stone_of_valarakas");
        int npcId = npc.getNpcId();
        if (npcId == valakas) {
            if (GetMemoState == 1 && st.ownItemCount(q_g_empty_orb2) > 0) {
                st.setCond(2);
                st.setMemoState("stone_of_valarakas", String.valueOf(2), true);
                st.takeItems(q_g_empty_orb2, -1);
                st.giveItems(q_brimful_orb2, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        }
        return null;
    }
}