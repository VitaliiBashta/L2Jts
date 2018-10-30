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
public class _613_ProveYourCourage extends Quest {
    // npc
    private final static int elder_ashas_barka_durai = 31377;

    // mobs
    private final static int ketra_hero_hekaton = 25299;

    // questitem
    private final static int q_barka_friendship_3 = 7223;
    private final static int q_hekaton_head = 7240;
    private final static int q_feather_of_valor = 7229;

    public _613_ProveYourCourage() {
        super(true);
        addStartNpc(elder_ashas_barka_durai);
        addKillId(ketra_hero_hekaton);
        addQuestItem(q_hekaton_head);
        addLevelCheck(75, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;

        if (event.equals("quest_accept")) {
            st.setCond(1);
            st.setMemoState("prove_your_courage_varka", String.valueOf(1 * 10 + 1), true);
            st.setState(STARTED);
            st.soundEffect(SOUND_ACCEPT);
            htmltext = "elder_ashas_barka_durai_q0613_0104.htm";
        } else if (event.equals("reply_3"))
            if (st.ownItemCount(q_hekaton_head) >= 1) {
                st.takeItems(q_hekaton_head, -1);
                st.giveItems(q_feather_of_valor, 1);
                st.addExpAndSp(10000, 0);
                st.removeMemo("prove_your_courage_varka");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "elder_ashas_barka_durai_q0613_0201.htm";
            } else
                htmltext = "elder_ashas_barka_durai_q0613_0202.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("prove_your_courage_varka");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == elder_ashas_barka_durai) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elder_ashas_barka_durai_q0613_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_barka_friendship_3) >= 1)
                                htmltext = "elder_ashas_barka_durai_q0613_0101.htm";
                            else {
                                st.exitQuest(true);
                                htmltext = "elder_ashas_barka_durai_q0613_0102.htm";
                            }
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elder_ashas_barka_durai)
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2)
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_hekaton_head) >= 1)
                            htmltext = "elder_ashas_barka_durai_q0613_0105.htm";
                        else
                            htmltext = "elder_ashas_barka_durai_q0613_0106.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("prove_your_courage_varka");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1 && npcId == ketra_hero_hekaton) {
            int i4 = Rnd.get(1000);
            if (i4 < 1000)
                if (st.ownItemCount(q_hekaton_head) + 1 >= 1) {
                    if (st.ownItemCount(q_hekaton_head) < 1) {
                        st.setCond(2);
                        st.setMemoState("prove_your_courage_varka", String.valueOf(1 * 10 + 2), true);
                        st.giveItems(q_hekaton_head, 1);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                } else {
                    st.giveItems(q_hekaton_head, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
        }
        return null;
    }
}