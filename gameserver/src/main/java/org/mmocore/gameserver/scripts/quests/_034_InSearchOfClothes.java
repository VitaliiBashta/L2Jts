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
public class _034_InSearchOfClothes extends Quest {
    // npc
    private final static int radia = 30088;
    private final static int rapin = 30165;
    private final static int trader_varanket = 30294;
    // mobs
    private final static int trisalim = 20560;
    private final static int trisalim_tote = 20561;
    // questitem
    private final static int q_base_of_cobweb = 7528;
    private final static int q_skein_of_yarn = 7161;
    private final static int q_seal_of_stock = 7164;
    // etcitem
    private final static int suede = 1866;
    private final static int thread = 1868;
    private final static int q_mysterious_cloth = 7076;

    public _034_InSearchOfClothes() {
        super(true);
        addStartNpc(radia);
        addTalkId(rapin, trader_varanket);
        addKillId(trisalim, trisalim_tote);
        addQuestItem(q_base_of_cobweb);
        addLevelCheck(60);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("find_cloth_cookie");
        int npcId = npc.getNpcId();
        if (npcId == radia) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("find_cloth", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "radia_q0034_0104.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 3 - 1) {
                st.setCond(3);
                st.setMemoState("find_cloth", String.valueOf(3 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "radia_q0034_0301.htm";
            } else if (event.equalsIgnoreCase("reply_3") && GetHTMLCookie == 6 - 1) {
                if (st.ownItemCount(q_skein_of_yarn) >= 1 && st.ownItemCount(thread) >= 5000 && st.ownItemCount(suede) >= 3000) {
                    st.takeItems(q_skein_of_yarn, 1);
                    st.takeItems(thread, 5000);
                    st.takeItems(suede, 3000);
                    st.giveItems(q_mysterious_cloth, 1);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    htmltext = "radia_q0034_0601.htm";
                } else
                    htmltext = "radia_q0034_0602.htm";
            }
        } else if (npcId == rapin) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 4 - 1) {
                st.setCond(4);
                st.setMemoState("find_cloth", String.valueOf(4 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "rapin_q0034_0401.htm";
            } else if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(q_base_of_cobweb) >= 10) {
                    st.setCond(6);
                    st.setMemoState("find_cloth", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(q_base_of_cobweb, 10);
                    st.giveItems(q_skein_of_yarn, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "rapin_q0034_0501.htm";
                } else
                    htmltext = "rapin_q0034_0502.htm";
            }
        } else if (npcId == trader_varanket) {
            if (event.equalsIgnoreCase("reply_1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("find_cloth", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "trader_varanket_q0034_0201.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("find_cloth");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == radia) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "radia_q0034_0103.htm";
                            break;
                        default:
                            if (st.getPlayer().getQuestState(37) != null && st.getPlayer().getQuestState(37).isStarted() && st.ownItemCount(q_seal_of_stock) >= 1)
                                htmltext = "radia_q0034_0101.htm";
                            else
                                htmltext = "radia_q0034_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == radia) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "radia_q0034_0105.htm";
                    else if (GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("find_cloth_cookie", String.valueOf(2), true);
                        htmltext = "radia_q0034_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "radia_q0034_0302.htm";
                    else if (st.ownItemCount(q_skein_of_yarn) >= 1 && GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("find_cloth_cookie", String.valueOf(5), true);
                        if (st.ownItemCount(suede) >= 3000 && st.ownItemCount(thread) >= 5000)
                            htmltext = "radia_q0034_0501.htm";
                        else
                            htmltext = "radia_q0034_0502.htm";
                    }
                } else if (npcId == rapin) {
                    if (GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("find_cloth_cookie", String.valueOf(3), true);
                        htmltext = "rapin_q0034_0301.htm";
                    } else if (GetMemoState <= 4 * 10 + 2 && GetMemoState >= 4 * 10 + 1) {
                        if (GetMemoState == 4 * 10 + 2 && st.ownItemCount(q_base_of_cobweb) >= 10) {
                            st.setMemoState("find_cloth_cookie", String.valueOf(4), true);
                            htmltext = "rapin_q0034_0402.htm";
                        } else
                            htmltext = "rapin_q0034_0403.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "rapin_q0034_0503.htm";
                } else if (npcId == trader_varanket) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("find_cloth_cookie", String.valueOf(1), true);
                        htmltext = "trader_varanket_q0034_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "trader_varanket_q0034_0202.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int GetMemoState = st.getInt("find_cloth");
        int npcId = npc.getNpcId();
        if (npcId == trisalim || npcId == trisalim_tote) {
            if (GetMemoState == 4 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_base_of_cobweb) + 1 >= 10) {
                        if (st.ownItemCount(q_base_of_cobweb) <= 10) {
                            st.giveItems(q_base_of_cobweb, 10 - st.ownItemCount(q_base_of_cobweb));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        st.setCond(5);
                        st.setMemoState("find_cloth", String.valueOf(4 * 10 + 2), true);
                    } else {
                        st.giveItems(q_base_of_cobweb, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}