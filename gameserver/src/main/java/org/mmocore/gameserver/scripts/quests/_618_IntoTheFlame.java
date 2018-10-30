package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;

public class _618_IntoTheFlame extends Quest {
    //NPCs
    private static final int KLEIN = 31540;
    private static final int HILDA = 31271;

    //QUEST ITEMS
    private static final int VACUALITE_ORE = 7265;
    private static final int VACUALITE = 7266;
    private static final int FLOATING_STONE = 7267;

    //CHANCE
    private static final int CHANCE_FOR_QUEST_ITEMS = 50;

    public _618_IntoTheFlame() {
        super(false);

        addStartNpc(KLEIN);
        addTalkId(HILDA);
        addKillId(21274, 21275, 21276, 21278);
        addKillId(21282, 21283, 21284, 21286);
        addKillId(21290, 21291, 21292, 21294);
        addQuestItem(VACUALITE_ORE);
        addQuestItem(VACUALITE);
        addQuestItem(FLOATING_STONE);
        addLevelCheck(60, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if (event.equalsIgnoreCase("watcher_valakas_klein_q0618_0104.htm") && cond == 0) {
            st.setState(STARTED);
            st.setCond(1);
            st.soundEffect(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("watcher_valakas_klein_q0618_0401.htm")) {
            if (st.ownItemCount(VACUALITE) > 0 && cond == 4) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                st.giveItems(FLOATING_STONE, 1);
            } else {
                htmltext = "watcher_valakas_klein_q0618_0104.htm";
            }
        } else if (event.equalsIgnoreCase("blacksmith_hilda_q0618_0201.htm") && cond == 1) {
            st.setCond(2);
            st.soundEffect(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("blacksmith_hilda_q0618_0301.htm")) {
            if (cond == 3 && st.ownItemCount(VACUALITE_ORE) == 50) {
                st.takeItems(VACUALITE_ORE, -1);
                st.giveItems(VACUALITE, 1);
                st.setCond(4);
                st.soundEffect(SOUND_MIDDLE);
            } else {
                htmltext = "blacksmith_hilda_q0618_0203.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == KLEIN) {
            if (cond == 0) {
                switch (isAvailableFor(st.getPlayer())) {
                    case LEVEL:
                        htmltext = "watcher_valakas_klein_q0618_0103.htm";
                        st.exitQuest(true);
                        break;
                    default:
                        htmltext = "watcher_valakas_klein_q0618_0101.htm";
                        break;
                }
            } else if (cond == 4 && st.ownItemCount(VACUALITE) > 0) {
                htmltext = "watcher_valakas_klein_q0618_0301.htm";
            } else {
                htmltext = "watcher_valakas_klein_q0618_0104.htm";
            }
        } else if (npcId == HILDA) {
            if (cond == 1) {
                htmltext = "blacksmith_hilda_q0618_0101.htm";
            } else if (cond == 3 && st.ownItemCount(VACUALITE_ORE) >= 50) {
                htmltext = "blacksmith_hilda_q0618_0202.htm";
            } else if (cond == 4) {
                htmltext = "blacksmith_hilda_q0618_0303.htm";
            } else {
                htmltext = "blacksmith_hilda_q0618_0203.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        long count = st.ownItemCount(VACUALITE_ORE);
        if (Rnd.chance(CHANCE_FOR_QUEST_ITEMS) && count < 50) {
            st.giveItems(VACUALITE_ORE, 1);
            if (count == 49) {
                st.setCond(3);
                st.soundEffect(SOUND_MIDDLE);
            } else {
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}