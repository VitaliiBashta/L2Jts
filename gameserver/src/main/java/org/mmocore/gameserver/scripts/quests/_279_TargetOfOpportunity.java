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
public class _279_TargetOfOpportunity extends Quest {
    // npc
    private static final int zerian = 32302;
    private static final int teleporter_a01 = 32745;
    private static final int teleporter_a03 = 32747;

    // mobs
    private static final int guard_of_space = 22373;
    private static final int beholder_of_space = 22374;
    private static final int priest_of_space = 22375;
    private static final int ruler_of_space = 22376;

    // questitem
    private static final int q_seal_breaker_5f = 15515;
    private static final int q_seal_breaker_10f = 15516;
    private static final int q_seal_components_part1 = 15517;
    private static final int q_seal_components_part2 = 15518;
    private static final int q_seal_components_part3 = 15519;
    private static final int q_seal_components_part4 = 15520;

    public _279_TargetOfOpportunity() {
        super(PARTY_ALL);
        addStartNpc(zerian);
        addTalkId(teleporter_a01, teleporter_a03);
        addKillId(guard_of_space, beholder_of_space, priest_of_space, ruler_of_space);
        addQuestItem(q_seal_components_part1, q_seal_components_part2, q_seal_components_part3, q_seal_components_part4);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("catch_the_blind_side");

        if (event.equalsIgnoreCase("quest_accept")) {
            st.setCond(1);
            st.setMemoState("catch_the_blind_side", String.valueOf(1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "zerian_q0279_05.htm";
        } else if (event.equalsIgnoreCase("reply_1"))
            htmltext = "zerian_q0279_03.htm";
        else if (event.equalsIgnoreCase("reply_2"))
            htmltext = "zerian_q0279_04.htm";
        else if (event.equalsIgnoreCase("reply_3"))
            if (GetMemoState < 2 && st.ownItemCount(q_seal_components_part1) >= 1 && st.ownItemCount(q_seal_components_part2) >= 1 && st.ownItemCount(q_seal_components_part3) >= 1 && st.ownItemCount(q_seal_components_part4) >= 1) {
                st.giveItems(q_seal_breaker_5f, 1);
                st.giveItems(q_seal_breaker_10f, 1);
                st.takeItems(q_seal_components_part1, -1);
                st.takeItems(q_seal_components_part2, -1);
                st.takeItems(q_seal_components_part3, -1);
                st.takeItems(q_seal_components_part4, -1);
                st.removeMemo("catch_the_blind_side");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "zerian_q0279_08.htm";
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("catch_the_blind_side");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == zerian) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "zerian_q0279_02.htm";
                            break;
                        default:
                            htmltext = "zerian_q0279_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == zerian)
                    if (GetMemoState == 1 && (st.ownItemCount(q_seal_components_part1) == 0 || st.ownItemCount(q_seal_components_part2) == 0 || st.ownItemCount(q_seal_components_part3) == 0 || st.ownItemCount(q_seal_components_part4) == 0))
                        htmltext = "zerian_q0279_06.htm";
                    else if (GetMemoState < 2 && st.ownItemCount(q_seal_components_part1) >= 1 && st.ownItemCount(q_seal_components_part2) >= 1 && st.ownItemCount(q_seal_components_part3) >= 1 && st.ownItemCount(q_seal_components_part4) >= 1)
                        htmltext = "zerian_q0279_07.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("catch_the_blind_side");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1)
            if (npcId == guard_of_space || npcId == beholder_of_space || npcId == priest_of_space || npcId == ruler_of_space) {
                int i0 = Rnd.get(1000);
                if (i0 < 311)
                    if (st.ownItemCount(q_seal_components_part1) < 1)
                        if (st.ownItemCount(q_seal_components_part2) >= 1 && st.ownItemCount(q_seal_components_part3) >= 1 && st.ownItemCount(q_seal_components_part4) >= 1) {
                            st.setCond(2);
                            st.giveItems(q_seal_components_part1, 1);
                            st.soundEffect(SOUND_MIDDLE);
                        } else {
                            st.giveItems(q_seal_components_part1, 1);
                            st.soundEffect(SOUND_ITEMGET);
                        }
            }
        return null;
    }
}