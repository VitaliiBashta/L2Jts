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
public class _019_GoToThePastureland extends Quest {
    // npc
    private final static int trader_vladimir = 31302;
    private final static int beast_herder_tunatun = 31537;
    // questitem
    private final static int q_youngmeat_of_beast = 7547;
    private final static int q_youngmeat_of_beast_re = 15532;

    public _019_GoToThePastureland() {
        super(false);
        addStartNpc(trader_vladimir);
        addTalkId(trader_vladimir, beast_herder_tunatun);
        addQuestItem(q_youngmeat_of_beast);
        addLevelCheck(82);
    }

    @Override
    public String onEvent(final String event, final QuestState st, final NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("head_to_beast_farm_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == trader_vladimir) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("head_to_beast_farm", String.valueOf(1), true);
                st.giveItems(q_youngmeat_of_beast_re, 1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_vladimir_q0019_0104.htm";
            }
        } else if (npcId == beast_herder_tunatun) {
            if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_youngmeat_of_beast) >= 1) {
                    st.takeItems(q_youngmeat_of_beast, 1);
                    st.giveItems(ADENA_ID, 50000);
                    st.addExpAndSp(136766, 12688);
                    st.removeMemo("head_to_beast_farm");
                    st.removeMemo("head_to_beast_farm_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "beast_herder_tunatun_q0019_0201.htm";
                } else if (st.ownItemCount(q_youngmeat_of_beast_re) >= 1) {

                    st.takeItems(q_youngmeat_of_beast_re, 1);
                    st.giveItems(ADENA_ID, 147200);
                    st.addExpAndSp(385040, 75250);
                    st.removeMemo("head_to_beast_farm");
                    st.removeMemo("head_to_beast_farm_cookie");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "beast_herder_tunatun_q0019_0201.htm";
                } else {
                    htmltext = "beast_herder_tunatun_q0019_0202.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(final NpcInstance npc, final QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        final int GetMemoState = st.getInt("head_to_beast_farm");
        final int npcId = npc.getNpcId();
        final int id = st.getState();
        switch (id) {
            case CREATED: {
                if (npcId == trader_vladimir) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL: {
                            htmltext = "trader_vladimir_q0019_0103.htm";
                            st.exitQuest(true);
                            break;
                        }
                        default: {
                            htmltext = "trader_vladimir_q0019_0101.htm";
                            break;
                        }
                    }
                }
                break;
            }
            case STARTED: {
                if (npcId == trader_vladimir) {
                    if (GetMemoState > 0) {
                        htmltext = "trader_vladimir_q0019_0105.htm";
                    }
                } else if (npcId == beast_herder_tunatun) {
                    if (GetMemoState > 0) {
                        st.setMemoState("head_to_beast_farm_cookie", String.valueOf(1), true);
                        htmltext = "beast_herder_tunatun_q0019_0101.htm";
                    }
                }
                break;
            }
        }
        return htmltext;
    }
}