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
 * @version 1.0
 * @date 27/10/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _284_MuertosFeather extends Quest {
    // npc
    private static final int trader_treauvi = 32166;
    // mobs
    private static final int mostro_guard = 22239;
    private static final int mostro_ranger = 22240;
    private static final int mostro_fighter = 22242;
    private static final int mostro_guard_chief = 22243;
    private static final int mostro_sub_leader = 22245;
    private static final int mostro_leader = 22246;
    // questitem
    private static final int q_feather_of_mostro = 9748;

    public _284_MuertosFeather() {
        super(false);
        addStartNpc(trader_treauvi);
        addKillId(mostro_guard, mostro_ranger, mostro_fighter, mostro_guard_chief, mostro_sub_leader, mostro_leader);
        addQuestItem(q_feather_of_mostro);
        addLevelCheck(11, 18);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("an_desirable_feather_cookie");
        int npcId = npc.getNpcId();
        if (npcId == trader_treauvi) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("an_desirable_feather", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "trader_treauvi_q0284_0103.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=284&reply=1") && GetHTMLCookie == 2 - 1)
                htmltext = "trader_treauvi_q0284_0201.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=284&reply=3") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_feather_of_mostro) == 0)
                    htmltext = "trader_treauvi_q0284_0202.htm";
                else if (st.ownItemCount(q_feather_of_mostro) >= 10)
                    st.giveItems(ADENA_ID, 45 * st.ownItemCount(q_feather_of_mostro));
                st.takeItems(q_feather_of_mostro, -1);
                htmltext = "trader_treauvi_q0284_0203.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=284&reply=4") && GetHTMLCookie == 2 - 1) {
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "trader_treauvi_q0284_0204.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("an_desirable_feather");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == trader_treauvi) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_treauvi_q0284_0102.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "trader_treauvi_q0284_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == trader_treauvi) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(q_feather_of_mostro) == 0)
                            htmltext = "trader_treauvi_q0284_0106.htm";
                        else {
                            st.setMemoState("an_desirable_feather_cookie", String.valueOf(1), true);
                            htmltext = "trader_treauvi_q0284_0105.htm";
                        }
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("an_desirable_feather");
        int npcId = npc.getNpcId();
        if (npcId == mostro_guard) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    st.giveItems(q_feather_of_mostro, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == mostro_ranger) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 533) {
                    st.giveItems(q_feather_of_mostro, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == mostro_fighter) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 566) {
                    st.giveItems(q_feather_of_mostro, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == mostro_guard_chief) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 600) {
                    st.giveItems(q_feather_of_mostro, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == mostro_sub_leader || npcId == mostro_leader) {
            if (GetMemoState == 1 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 633) {
                    st.giveItems(q_feather_of_mostro, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}