package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
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
public class _288_HandleWithCare extends Quest {
    // npc
    private static final int angkumi = 32741;

    // etcitem
    private static final int scrl_of_ench_wp_s = 959;
    private static final int scrl_of_ench_am_s = 960;
    private static final int high_ore_of_holy = 9557;

    // questitem
    private static final int q_scale_of_lizard_good = 15498;
    private static final int q_scale_of_lizard_highest = 15497;

    public _288_HandleWithCare() {
        super(false);
        addStartNpc(angkumi);
        addQuestItem(q_scale_of_lizard_good, q_scale_of_lizard_highest);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("take_care_please");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("take_care_please", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "angkumi_q0288_04.htm";
        }
        if (event.equalsIgnoreCase("reply_1"))
            if (st.getPlayer().getLevel() >= 82)
                htmltext = "angkumi_q0288_03.htm";
        if (event.equalsIgnoreCase("reply_2"))
            if (GetMemoState == 2)
                if (st.ownItemCount(q_scale_of_lizard_good) >= 1) {
                    st.takeItems(q_scale_of_lizard_good, -1);
                    st.setMemoState("take_care_please", String.valueOf(2), true);

                    int i0 = Rnd.get(6);
                    if (i0 == 0)
                        st.giveItems(scrl_of_ench_wp_s, 1);
                    else if (i0 >= 1 && i0 < 4)
                        st.giveItems(scrl_of_ench_am_s, 1);
                    else if (i0 >= 4 && i0 < 6)
                        st.giveItems(scrl_of_ench_am_s, 2);
                    else if (i0 >= 6 && i0 < 7)
                        st.giveItems(scrl_of_ench_am_s, 3);
                    else if (i0 >= 7 && i0 < 9)
                        st.giveItems(high_ore_of_holy, 1);
                    else
                        st.giveItems(high_ore_of_holy, 2);
                    st.removeMemo("take_care_please");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "angkumi_q0288_08.htm";
                } else if (st.ownItemCount(q_scale_of_lizard_highest) >= 1) {
                    st.takeItems(q_scale_of_lizard_highest, -1);
                    st.setMemoState("take_care_please", String.valueOf(2), true);
                    int i0 = Rnd.get(10);
                    if (i0 < 1) {
                        st.giveItems(scrl_of_ench_wp_s, 1);
                        st.giveItems(high_ore_of_holy, 1);
                    } else if (i0 <= 1 && i0 < 5) {
                        st.giveItems(scrl_of_ench_am_s, 1);
                        st.giveItems(high_ore_of_holy, 1);
                    } else if (i0 <= 5 && i0 < 8) {
                        st.giveItems(scrl_of_ench_am_s, 2);
                        st.giveItems(high_ore_of_holy, 1);
                    } else {
                        st.giveItems(scrl_of_ench_am_s, 3);
                        st.giveItems(high_ore_of_holy, 1);
                    }
                    st.removeMemo("take_care_please");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "angkumi_q0288_08.htm";
                }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("take_care_please");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == angkumi) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "angkumi_q0288_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "angkumi_q0288_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == angkumi)
                    if (st.ownItemCount(q_scale_of_lizard_highest) == 0 && st.ownItemCount(q_scale_of_lizard_good) == 0) {
                        if (GetMemoState != 1)
                            st.setMemoState("take_care_please", String.valueOf(1), true);
                        htmltext = "angkumi_q0288_05.htm";
                    } else if (GetMemoState == 2 && st.ownItemCount(q_scale_of_lizard_good) >= 1)
                        htmltext = "angkumi_q0288_06.htm";
                    else if (GetMemoState == 2 && st.ownItemCount(q_scale_of_lizard_highest) >= 1)
                        htmltext = "angkumi_q0288_07.htm";
                break;
        }
        return htmltext;
    }
}