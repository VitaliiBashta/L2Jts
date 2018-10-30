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
public class _607_ProveYourCourage extends Quest {
    // npc
    private final static int elder_kadun_zu_ketra = 31370;

    // mob
    private final static int varka_hero_shadith = 25309;

    // questitem
    private final static int q_shadith_head = 7235;
    private final static int q_totem_of_valor = 7219;
    private final static int q_ketra_friendship_3 = 7213;

    public _607_ProveYourCourage() {
        super(true);
        addStartNpc(elder_kadun_zu_ketra);
        addKillId(varka_hero_shadith);
        addQuestItem(q_shadith_head);
        addLevelCheck(75, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("prove_your_courage_ketra_cookie");
        int npcId = npc.getNpcId();

        if (npcId == elder_kadun_zu_ketra)
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("prove_your_courage_ketra", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "elder_kadun_zu_ketra_q0607_0104.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1)
                if (st.ownItemCount(q_shadith_head) >= 1) {
                    st.takeItems(q_shadith_head, -1);
                    st.giveItems(q_totem_of_valor, 1);
                    st.addExpAndSp(10000, 0);
                    st.removeMemo("prove_your_courage_ketra");
                    st.removeMemo("prove_your_courage_ketra_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "elder_kadun_zu_ketra_q0607_0201.htm";
                } else
                    htmltext = "elder_kadun_zu_ketra_q0607_0202.htm";

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("prove_your_courage_ketra");
        int npcId = npc.getNpcId();
        int id = st.getState();

        switch (id) {
            case CREATED:
                if (npcId == elder_kadun_zu_ketra) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elder_kadun_zu_ketra_q0607_0103.htm";
                            break;
                        default:
                            if (st.ownItemCount(q_ketra_friendship_3) >= 1)
                                htmltext = "elder_kadun_zu_ketra_q0607_0101.htm";
                            else
                                htmltext = "elder_kadun_zu_ketra_q0607_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elder_kadun_zu_ketra)
                    if (GetMemoState >= 1 * 10 + 1 && GetMemoState <= 1 * 10 + 2)
                        if (GetMemoState == 1 * 10 + 2 && st.ownItemCount(q_shadith_head) >= 1) {
                            st.setMemoState("prove_your_courage_ketra_cookie", String.valueOf(1), true);
                            htmltext = "elder_kadun_zu_ketra_q0607_0105.htm";
                        } else
                            htmltext = "elder_kadun_zu_ketra_q0607_0106.htm";
                break;
        }

        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("prove_your_courage_ketra");
        int npcId = npc.getNpcId();

        if (GetMemoState == 1 * 10 + 1)
            if (npcId == varka_hero_shadith) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000)
                    if (st.ownItemCount(q_shadith_head) + 1 >= 1) {
                        if (st.ownItemCount(q_shadith_head) < 1) {
                            st.setCond(2);
                            st.setMemoState("prove_your_courage_ketra", String.valueOf(1 * 10 + 2), true);
                            st.giveItems(q_shadith_head, 1 - st.ownItemCount(q_shadith_head));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_shadith_head, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
            }

        return null;
    }
}