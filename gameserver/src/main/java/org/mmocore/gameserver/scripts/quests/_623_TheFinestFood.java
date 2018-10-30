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
public class _623_TheFinestFood extends Quest {
    // npc
    private final static int jeremy = 31521;

    // mobs
    private final static int thermal_buffalo = 21315;
    private final static int thermal_flava = 21316;
    private final static int thermal_antelope = 21318;

    // questitem
    private final static int q_leaf_of_flava = 7199;
    private final static int q_buffalo_meat = 7200;
    private final static int q_horn_of_antelope = 7201;

    // etcitem
    private final static int rp_sealed_ring_of_aurakyria_i = 6849;
    private final static int rp_sealed_sanddragons_earing_i = 6847;
    private final static int rp_sealed_dragon_necklace_i = 6851;

    public _623_TheFinestFood() {
        super(true);
        addStartNpc(jeremy);
        addTalkId(jeremy);
        addKillId(thermal_buffalo, thermal_flava, thermal_antelope);
        addQuestItem(q_leaf_of_flava, q_buffalo_meat, q_horn_of_antelope);
        addLevelCheck(71, 78);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("for_the_best_cook_cookie");
        int npcId = npc.getNpcId();

        if (npcId == jeremy) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("for_the_best_cook", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "jeremy_q0623_0104.htm";
            }
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1)
                if (st.ownItemCount(q_leaf_of_flava) >= 100 && st.ownItemCount(q_buffalo_meat) >= 100 && st.ownItemCount(q_horn_of_antelope) >= 100) {
                    int i1 = Rnd.get(1000);
                    st.takeItems(q_leaf_of_flava, -1);
                    st.takeItems(q_buffalo_meat, -1);
                    st.takeItems(q_horn_of_antelope, -1);
                    if (i1 < 120) {
                        st.giveItems(ADENA_ID, 25000);
                        st.giveItems(rp_sealed_ring_of_aurakyria_i, 1);
                    } else if (i1 < 120 + 120) {
                        st.giveItems(ADENA_ID, 65000);
                        st.giveItems(rp_sealed_sanddragons_earing_i, 1);
                    } else if (i1 < 120 + 120 + 100) {
                        st.giveItems(rp_sealed_dragon_necklace_i, 1);
                        st.giveItems(ADENA_ID, 25000);
                    } else if (i1 < 120 + 120 + 100 + 660) {
                        st.giveItems(ADENA_ID, 73000);
                        st.addExpAndSp(230000, 18200);
                    }
                    st.removeMemo("for_the_best_cook");
                    st.removeMemo("for_the_best_cook_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "jeremy_q0623_0201.htm";
                } else
                    htmltext = "jeremy_q0623_0202.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("for_the_best_cook");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == jeremy) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "jeremy_q0623_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "jeremy_q0623_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == jeremy)
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2)
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_leaf_of_flava) >= 100 && st.ownItemCount(q_buffalo_meat) >= 100 && st.ownItemCount(q_horn_of_antelope) >= 100) {
                            st.setMemoState("for_the_best_cook_cookie", String.valueOf(1), true);
                            htmltext = "jeremy_q0623_0105.htm";
                        } else
                            htmltext = "jeremy_q0623_0106.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("for_the_best_cook");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == thermal_buffalo) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000)
                    if (st.ownItemCount(q_buffalo_meat) + 1 >= 100) {
                        if (st.ownItemCount(q_buffalo_meat) < 100) {
                            st.giveItems(q_buffalo_meat, 100 - st.ownItemCount(q_buffalo_meat));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_leaf_of_flava) >= 100 && st.ownItemCount(q_horn_of_antelope) >= 100) {
                            st.setCond(2);
                            st.setMemoState("for_the_best_cook", String.valueOf(1 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_buffalo_meat, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == thermal_flava) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000)
                    if (st.ownItemCount(q_leaf_of_flava) + 1 >= 100) {
                        if (st.ownItemCount(q_leaf_of_flava) < 100) {
                            st.giveItems(q_leaf_of_flava, 100 - st.ownItemCount(q_leaf_of_flava));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_buffalo_meat) >= 100 && st.ownItemCount(q_horn_of_antelope) >= 100) {
                            st.setCond(2);
                            st.setMemoState("for_the_best_cook", String.valueOf(1 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_leaf_of_flava, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            } else if (npcId == thermal_antelope) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000)
                    if (st.ownItemCount(q_horn_of_antelope) + 1 >= 100) {
                        if (st.ownItemCount(q_horn_of_antelope) < 100) {
                            st.giveItems(q_horn_of_antelope, 100 - st.ownItemCount(q_horn_of_antelope));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_buffalo_meat) >= 100 && st.ownItemCount(q_leaf_of_flava) >= 100) {
                            st.setCond(2);
                            st.setMemoState("for_the_best_cook", String.valueOf(1 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_horn_of_antelope, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }
        return null;
    }
}