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
public class _013_ParcelDelivery extends Quest {
    // npc
    private static final int mineral_trader_fundin = 31274;
    private static final int warsmith_vulcan = 31539;
    // questitem
    private static final int q_package_to_vulcan = 7263;

    public _013_ParcelDelivery() {
        super(false);
        addStartNpc(mineral_trader_fundin);
        addTalkId(warsmith_vulcan);
        addQuestItem(q_package_to_vulcan);
        addLevelCheck(74, 80);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("delivering_the_package_cookie");
        int npcId = npc.getNpcId();
        if (npcId == mineral_trader_fundin) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("delivering_the_package", String.valueOf(1 * 10 + 1), true);
                st.giveItems(q_package_to_vulcan, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "mineral_trader_fundin_q0013_0104.htm";
            }
        } else if (npcId == warsmith_vulcan) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_package_to_vulcan) >= 1) {
                    st.takeItems(q_package_to_vulcan, -1);
                    st.giveItems(ADENA_ID, 157834);
                    st.addExpAndSp(589092, 58794);
                    st.removeMemo("delivering_the_package");
                    st.removeMemo("delivering_the_package_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "warsmith_vulcan_q0013_0201.htm";
                } else
                    htmltext = "warsmith_vulcan_q0013_0202.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("delivering_the_package");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == mineral_trader_fundin) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "mineral_trader_fundin_q0013_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "mineral_trader_fundin_q0013_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == mineral_trader_fundin) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "mineral_trader_fundin_q0013_0105.htm";
                } else if (npcId == warsmith_vulcan) {
                    if (st.ownItemCount(q_package_to_vulcan) >= 1 && GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("delivering_the_package_cookie", String.valueOf(1), true);
                        htmltext = "warsmith_vulcan_q0013_0101.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}