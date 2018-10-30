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
public class _027_ChestCaughtWithABaitOfWind extends Quest {
    // npc
    private static final int fisher_lanosco = 31570;
    private static final int blueprint_seller_shaling = 31434;

    // questitem
    private static final int q_shalings_golem_blueprint = 7625;

    // etcitem
    private static final int big_blue_treasure_box = 6500;
    private static final int black_pearl_ring = 880;

    public _027_ChestCaughtWithABaitOfWind() {
        super(false);
        addStartNpc(fisher_lanosco);
        addTalkId(blueprint_seller_shaling);
        addQuestItem(q_shalings_golem_blueprint);
        addQuestCompletedCheck(50);
        addLevelCheck(27, 29);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("strange_box_of_lanosco_cookie");
        int npcId = npc.getNpcId();

        if (npcId == fisher_lanosco) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("strange_box_of_lanosco", String.valueOf(1 * 10 + 1), true);
                st.soundEffect(SOUND_ACCEPT);
                st.setState(STARTED);
                htmltext = "fisher_lanosco_q0027_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1)
                if (st.ownItemCount(big_blue_treasure_box) >= 1) {
                    st.setCond(2);
                    st.setMemoState("strange_box_of_lanosco", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(big_blue_treasure_box, 1);
                    st.giveItems(q_shalings_golem_blueprint, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "fisher_lanosco_q0027_0201.htm";
                } else
                    htmltext = "fisher_lanosco_q0027_0202.htm";
        } else if (npcId == blueprint_seller_shaling)
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 3 - 1)
                if (st.ownItemCount(q_shalings_golem_blueprint) >= 1) {
                    st.takeItems(q_shalings_golem_blueprint, 1);
                    st.giveItems(black_pearl_ring, 1);
                    st.removeMemo("strange_box_of_lanosco");
                    st.removeMemo("strange_box_of_lanosco_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "blueprint_seller_shaling_q0027_0301.htm";
                } else
                    htmltext = "blueprint_seller_shaling_q0027_0302.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("strange_box_of_lanosco");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == fisher_lanosco) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "fisher_lanosco_q0027_0103.htm";
                            st.exitQuest(true);
                            break;
                        case QUEST:
                            htmltext = "fisher_lanosco_q0027_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "fisher_lanosco_q0027_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == fisher_lanosco) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(big_blue_treasure_box) >= 1) {
                            st.setMemoState("strange_box_of_lanosco_cookie", String.valueOf(1), true);
                            htmltext = "fisher_lanosco_q0027_0105.htm";
                        } else
                            htmltext = "fisher_lanosco_q0027_0106.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "fisher_lanosco_q0027_0203.htm";
                } else if (npcId == blueprint_seller_shaling)
                    if (st.ownItemCount(q_shalings_golem_blueprint) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("strange_box_of_lanosco_cookie", String.valueOf(2), true);
                        htmltext = "blueprint_seller_shaling_q0027_0201.htm";
                    }
                break;
        }
        return htmltext;
    }
}