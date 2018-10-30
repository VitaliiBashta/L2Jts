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
 * @date 25/01/2015
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _347_GoGetTheCalculator extends Quest {
    // npc
    private static final int blacksmith_bronp = 30526;
    private static final int blacksmith_silvery = 30527;
    private static final int elder_spiron = 30532;
    private static final int elder_balanki = 30533;
    // mobs
    private static final int gemstone_beast = 20540;
    // questitem
    private static final int q_gemstone = 4286;
    private static final int q_calculator = 4285;
    // etcitem
    private static final int calculator = 4393;

    public _347_GoGetTheCalculator() {
        super(false);
        addStartNpc(blacksmith_bronp);
        addTalkId(blacksmith_silvery, elder_spiron, elder_balanki);
        addKillId(gemstone_beast);
        addQuestItem(q_gemstone);
        addLevelCheck(12);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("get_calculator");
        int npcId = npc.getNpcId();
        if (npcId == blacksmith_bronp) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("get_calculator", String.valueOf(100), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "blacksmith_bronp_q0347_08.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=347&reply=2"))
                htmltext = "blacksmith_bronp_q0347_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=347&reply=3"))
                htmltext = "blacksmith_bronp_q0347_04.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=347&reply=4"))
                htmltext = "blacksmith_bronp_q0347_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=347&reply=5"))
                htmltext = "blacksmith_bronp_q0347_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=347&reply=6"))
                htmltext = "blacksmith_bronp_q0347_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=347&reply=7")) {
                if (GetMemoState >= 700) {
                    st.giveItems(calculator, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    st.removeMemo("get_calculator");
                    htmltext = "blacksmith_bronp_q0347_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=347&reply=8")) {
                if (GetMemoState >= 700) {
                    st.giveItems(ADENA_ID, 1500);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                    st.removeMemo("get_calculator");
                    htmltext = "blacksmith_bronp_q0347_11.htm";
                }
            }
        } else if (npcId == elder_spiron) {
            if (event.equalsIgnoreCase("menu_select?ask=347&reply=3")) {
                st.setMemoState("get_calculator", String.valueOf(200 + GetMemoState), true);
                if (GetMemoState == 100) {
                    st.setCond(3);
                    st.soundEffect(SOUND_MIDDLE);
                } else if (GetMemoState == 200) {
                    st.setCond(4);
                    st.soundEffect(SOUND_MIDDLE);
                }
                htmltext = "elder_spiron_q0347_02.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=347&reply=4"))
                htmltext = "elder_spiron_q0347_03.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=347&reply=5"))
                htmltext = "elder_spiron_q0347_04.htm";
        } else if (npcId == elder_balanki) {
            if (event.equalsIgnoreCase("menu_select?ask=347&reply=2")) {
                if (st.ownItemCount(ADENA_ID) >= 100) {
                    st.setMemoState("get_calculator", String.valueOf(100 + GetMemoState), true);
                    st.takeItems(ADENA_ID, 100);
                    if (GetMemoState == 100) {
                        st.setCond(2);
                        st.soundEffect(SOUND_MIDDLE);
                    } else if (GetMemoState == 300) {
                        st.setCond(4);
                        st.soundEffect(SOUND_MIDDLE);
                    }
                    htmltext = "elder_balanki_q0347_02.htm";
                } else
                    htmltext = "elder_balanki_q0347_03.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("get_calculator");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == blacksmith_bronp) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "blacksmith_bronp_q0347_02.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "blacksmith_bronp_q0347_01.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == blacksmith_bronp) {
                    if (st.ownItemCount(q_calculator) >= 1) {
                        st.takeItems(q_calculator, -1);
                        st.setMemoState("get_calculator", String.valueOf(700), true);
                        htmltext = "blacksmith_bronp_q0347_09.htm";
                    } else if (st.ownItemCount(q_calculator) == 0 && GetMemoState == 700)
                        htmltext = "blacksmith_bronp_q0347_12.htm";
                    else if (st.ownItemCount(q_calculator) == 0 && GetMemoState == 100)
                        htmltext = "blacksmith_bronp_q0347_13.htm";
                    else if (st.ownItemCount(q_calculator) == 0 && (GetMemoState == 200 || GetMemoState == 300))
                        htmltext = "blacksmith_bronp_q0347_14.htm";
                    else if (st.ownItemCount(q_calculator) == 0 && (GetMemoState == 400 || GetMemoState == 500 || GetMemoState == 600))
                        htmltext = "blacksmith_bronp_q0347_15.htm";
                } else if (npcId == blacksmith_silvery) {
                    if (GetMemoState == 100 || GetMemoState == 200 || GetMemoState == 300)
                        htmltext = "blacksmith_silvery_q0347_01.htm";
                    else if (GetMemoState == 400) {
                        st.setCond(5);
                        st.setMemoState("get_calculator", String.valueOf(500), true);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_silvery_q0347_02.htm";
                    } else if (GetMemoState == 500 && st.ownItemCount(q_gemstone) >= 10) {
                        st.setCond(6);
                        st.setMemoState("get_calculator", String.valueOf(600), true);
                        st.giveItems(q_calculator, 1);
                        st.takeItems(q_gemstone, -1);
                        st.soundEffect(SOUND_MIDDLE);
                        htmltext = "blacksmith_silvery_q0347_03.htm";
                    } else if (GetMemoState == 500 && st.ownItemCount(q_gemstone) < 10)
                        htmltext = "blacksmith_silvery_q0347_04.htm";
                    else if (GetMemoState == 600 || GetMemoState == 700)
                        htmltext = "blacksmith_silvery_q0347_05.htm";
                } else if (npcId == elder_spiron) {
                    if (GetMemoState == 100 || GetMemoState == 200)
                        htmltext = "elder_spiron_q0347_01.htm";
                    else if (GetMemoState == 300 || GetMemoState == 400 || GetMemoState == 500 || GetMemoState == 600 || GetMemoState == 700)
                        htmltext = "elder_spiron_q0347_05.htm";
                } else if (npcId == elder_balanki) {
                    if (GetMemoState == 100 || GetMemoState == 300)
                        htmltext = "elder_balanki_q0347_01.htm";
                    else if (GetMemoState == 200 || GetMemoState == 400 || GetMemoState == 500 || GetMemoState == 600 || GetMemoState == 700)
                        htmltext = "elder_balanki_q0347_04.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("get_calculator");
        if (npcId == gemstone_beast) {
            if (GetMemoState == 500 && st.ownItemCount(q_gemstone) < 10) {
                if (Rnd.get(10) <= 4) {
                    st.giveItems(q_gemstone, 1);
                    if (st.ownItemCount(q_gemstone) >= 10)
                        st.soundEffect(SOUND_MIDDLE);
                    else
                        st.soundEffect(SOUND_ITEMGET);
                }
            }
        }
        return null;
    }
}