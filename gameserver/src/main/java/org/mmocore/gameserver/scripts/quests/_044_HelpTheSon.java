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
 * @date 31/01/2015
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _044_HelpTheSon extends Quest {
    // npc
    private static final int pet_manager_lundy = 30827;
    private static final int high_prefect_drikus = 30505;
    // mobs
    private static final int male_lizardman = 20919;
    private static final int male_lizardman_scout = 20920;
    private static final int male_lizardman_guard = 20921;
    // questitem
    private static final int work_hammer = 168;
    private static final int q_dummy_kukaburo = 7552;
    private static final int q_dummy_kukaburo2 = 7553;
    private static final int ticket_kukaburo_ocarina = 7585;

    public _044_HelpTheSon() {
        super(false);
        addStartNpc(pet_manager_lundy);
        addTalkId(high_prefect_drikus);
        addKillId(male_lizardman, male_lizardman_scout, male_lizardman_guard);
        addQuestItem(q_dummy_kukaburo);
        addLevelCheck(24);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("plz_help_my_son_cookie");
        int npcId = npc.getNpcId();
        if (npcId == pet_manager_lundy) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("plz_help_my_son", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "pet_manager_lundy_q0044_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=44&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(work_hammer) >= 1) {
                    st.setCond(2);
                    st.setMemoState("plz_help_my_son", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(work_hammer, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pet_manager_lundy_q0044_0201.htm";
                } else
                    htmltext = "pet_manager_lundy_q0044_0202.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=44&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_dummy_kukaburo) >= 30) {
                    st.setCond(4);
                    st.setMemoState("plz_help_my_son", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_dummy_kukaburo, 30);
                    st.giveItems(q_dummy_kukaburo2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pet_manager_lundy_q0044_0301.htm";
                } else
                    htmltext = "pet_manager_lundy_q0044_0302.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=44&reply=3") && GetHTMLCookie == 5 - 1) {
                st.giveItems(ticket_kukaburo_ocarina, 1);
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "pet_manager_lundy_q0044_0501.htm";
            }
        } else if (npcId == high_prefect_drikus) {
            if (event.equalsIgnoreCase("menu_select?ask=44&reply=1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_dummy_kukaburo2) >= 1) {
                    st.setCond(5);
                    st.setMemoState("plz_help_my_son", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_dummy_kukaburo2, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "high_prefect_drikus_q0044_0401.htm";
                } else
                    htmltext = "high_prefect_drikus_q0044_0402.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("plz_help_my_son");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == pet_manager_lundy) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "pet_manager_lundy_q0044_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "pet_manager_lundy_q0044_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == pet_manager_lundy) {
                    if (GetMemoState == 1 * 10 + 1) {
                        if (st.ownItemCount(work_hammer) >= 1) {
                            st.setMemoState("plz_help_my_son_cookie", String.valueOf(1), true);
                            htmltext = "pet_manager_lundy_q0044_0105.htm";
                        } else
                            htmltext = "pet_manager_lundy_q0044_0106.htm";
                    } else if (GetMemoState <= 2 * 10 + 2 && GetMemoState >= 2 * 10 + 1) {
                        if (GetMemoState == 2 * 10 + 2 && st.ownItemCount(q_dummy_kukaburo) >= 30) {
                            st.setMemoState("plz_help_my_son_cookie", String.valueOf(2), true);
                            htmltext = "pet_manager_lundy_q0044_0203.htm";
                        } else
                            htmltext = "pet_manager_lundy_q0044_0204.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "pet_manager_lundy_q0044_0303.htm";
                    else if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("plz_help_my_son_cookie", String.valueOf(4), true);
                        htmltext = "pet_manager_lundy_q0044_0401.htm";
                    }
                } else if (npcId == high_prefect_drikus) {
                    if (st.ownItemCount(q_dummy_kukaburo2) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("plz_help_my_son_cookie", String.valueOf(3), true);
                        htmltext = "high_prefect_drikus_q0044_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "high_prefect_drikus_q0044_0403.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int GetMemoState = st.getInt("plz_help_my_son");
        if (npcId == male_lizardman || npcId == male_lizardman_scout || npcId == male_lizardman_guard) {
            if (GetMemoState == 2 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000 && 1000 != 0) {
                    if (st.ownItemCount(q_dummy_kukaburo) + 1 >= 30) {
                        if (st.ownItemCount(q_dummy_kukaburo) < 30) {
                            st.setCond(3);
                            st.giveItems(q_dummy_kukaburo, 30 - st.ownItemCount(q_dummy_kukaburo));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setMemoState("plz_help_my_son", String.valueOf(2 * 10 + 2), true);
                    } else {
                        st.giveItems(q_dummy_kukaburo, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}