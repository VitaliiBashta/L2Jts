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
 * @date 27/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _338_AlligatorHunter extends Quest {
    // npc
    private static final int trader_enverun = 30892;
    // mobs
    private static final int crocodile = 20135;
    // questitem
    private static final int q0338_crocodile_leather = 4337;

    public _338_AlligatorHunter() {
        super(false);
        addStartNpc(trader_enverun);
        addKillId(crocodile);
        addQuestItem(q0338_crocodile_leather);
        addLevelCheck(40, 47);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoStateEx = st.getInt("crocodile_hunter_ex");
        int npcId = npc.getNpcId();
        if (npcId == trader_enverun) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("crocodile_hunter", String.valueOf(1), true);
                st.setMemoState("crocodile_hunter_ex", String.valueOf(0), true);
                st.soundEffect(SOUND_ACCEPT);
                st.setState(STARTED);
                htmltext = "trader_enverun_q0338_03.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=338&reply=1")) {
                if (st.ownItemCount(q0338_crocodile_leather) == 0)
                    htmltext = "trader_enverun_q0338_05.htm";
                else if (st.ownItemCount(q0338_crocodile_leather) > 0) {
                    if (st.ownItemCount(q0338_crocodile_leather) >= 10)
                        st.giveItems(ADENA_ID, 3430 + st.ownItemCount(q0338_crocodile_leather) * 60);
                    else
                        st.giveItems(ADENA_ID, st.ownItemCount(q0338_crocodile_leather) * 60);
                    st.setMemoState("crocodile_hunter_ex", String.valueOf(GetMemoStateEx + st.ownItemCount(q0338_crocodile_leather)), true);
                    st.takeItems(q0338_crocodile_leather, -1);
                    htmltext = "trader_enverun_q0338_06.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=338&reply=2"))
                htmltext = "trader_enverun_q0338_11.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=338&reply=3"))
                htmltext = "trader_enverun_q0338_12.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=338&reply=4"))
                htmltext = "trader_enverun_q0338_15.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=338&reply=5")) {
                if (GetMemoStateEx >= 10) {
                    st.removeMemo("crocodile_hunter");
                    st.removeMemo("crocodile_hunter_ex");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "trader_enverun_q0338_16.htm";
                } else {
                    st.removeMemo("crocodile_hunter");
                    st.removeMemo("crocodile_hunter_ex");
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    htmltext = "trader_enverun_q0338_16t.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("crocodile_hunter");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == trader_enverun) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "trader_enverun_q0338_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "trader_enverun_q0338_02.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == trader_enverun) {
                    if (GetMemoState == 1)
                        htmltext = "trader_enverun_q0338_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("crocodile_hunter");
        int npcId = npc.getNpcId();
        if (npcId == crocodile) {
            if (GetMemoState == 1) {
                st.giveItems(q0338_crocodile_leather, 1);
                st.soundEffect(SOUND_ITEMGET);
                if (Rnd.get(100) < 19) {
                    st.giveItems(q0338_crocodile_leather, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}