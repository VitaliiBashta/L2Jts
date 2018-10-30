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
 * @version 1.1
 * @date 26/07/2016
 * @lastedit 26/07/2016
 * @tested OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _035_FindGlitteringJewelry extends Quest {
    // npc
    private final static int elliany = 30091;
    private final static int wharf_manager_felton = 30879;
    // mobs
    private final static int crocodile = 20135;
    // questitem
    private final static int q_rough_jewel = 7162;
    private final static int q_seal_of_stock = 7164;
    // etcitem
    private final static int oriharukon = 1893;
    private final static int silver_nugget = 1873;
    private final static int thons = 4044;
    private final static int q_box_of_jewel = 7077;

    public _035_FindGlitteringJewelry() {
        super(true);
        addStartNpc(elliany);
        addTalkId(wharf_manager_felton);
        addKillId(crocodile);
        addQuestItem(q_rough_jewel);
        addLevelCheck(60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("find_jewel_cookie");
        int npcId = npc.getNpcId();
        if (npcId == elliany) {
            if (event.equalsIgnoreCase("quest_accept") && st.ownItemCount(q_seal_of_stock) >= 1) {
                st.setCond(1);
                st.setMemoState("find_jewel", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "elliany_q0035_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_rough_jewel) >= 10) {
                    st.setCond(4);
                    st.setMemoState("find_jewel", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_rough_jewel, 10);
                    htmltext = "elliany_q0035_0301.htm";
                    st.soundEffect(SOUND_MIDDLE);
                } else
                    htmltext = "elliany_q0035_0302.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(oriharukon) >= 5 && st.ownItemCount(silver_nugget) >= 500 && st.ownItemCount(thons) >= 150) {
                    st.takeItems(oriharukon, 5);
                    st.takeItems(silver_nugget, 500);
                    st.takeItems(thons, 150);
                    st.giveItems(q_box_of_jewel, 1);
                    st.exitQuest(false);
                    st.soundEffect(SOUND_FINISH);
                    htmltext = "elliany_q0035_0401.htm";
                } else
                    htmltext = "elliany_q0035_0402.htm";
            }
        } else if (npcId == wharf_manager_felton) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("find_jewel", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "wharf_manager_felton_q0035_0201.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("find_jewel");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == elliany) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "elliany_q0035_0103.htm";
                            break;
                        default:
                            if (st.getPlayer().getQuestState(37) != null && st.getPlayer().getQuestState(37).isStarted() && st.ownItemCount(q_seal_of_stock) >= 1)
                                htmltext = "elliany_q0035_0101.htm";
                            else
                                htmltext = "elliany_q0035_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == elliany) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "elliany_q0035_0105.htm";
                    else if (GetMemoState == 2 * 10 + 2) {
                        if (st.ownItemCount(q_rough_jewel) >= 10) {
                            st.setMemoState("find_jewel_cookie", String.valueOf(2), true);
                            htmltext = "elliany_q0035_0201.htm";
                        } else
                            htmltext = "elliany_q0035_0202.htm";
                    } else if (GetMemoState == 3 * 10 + 1) {
                        if (st.ownItemCount(oriharukon) >= 5 && st.ownItemCount(silver_nugget) >= 500 && st.ownItemCount(thons) >= 150) {
                            st.setMemoState("find_jewel_cookie", String.valueOf(3), true);
                            htmltext = "elliany_q0035_0303.htm";
                        } else
                            htmltext = "elliany_q0035_0304.htm";
                    }
                } else if (npcId == wharf_manager_felton) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("find_jewel_cookie", String.valueOf(1), true);
                        htmltext = "wharf_manager_felton_q0035_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "wharf_manager_felton_q0035_0202.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("find_jewel");
        int npcId = npc.getNpcId();
        if (npcId == crocodile) {
            if (GetMemoState == 2 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_rough_jewel) + 1 >= 10) {
                        if (st.ownItemCount(q_rough_jewel) < 10) {
                            st.giveItems(q_rough_jewel, 10 - st.ownItemCount(q_rough_jewel));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(3);
                        st.setMemoState("find_jewel", String.valueOf(2 * 10 + 2), true);
                    } else {
                        st.giveItems(q_rough_jewel, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}