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
public class _621_EggDelivery extends Quest {
    // npc
    private static final int jeremy = 31521;
    private static final int pulin = 31543;
    private static final int naff = 31544;
    private static final int crocus = 31545;
    private static final int kuber = 31546;
    private static final int beolin = 31547;
    private static final int brewer_valentine = 31584;
    // questitem
    private static final int q_boiled_eggs = 7195;
    private static final int q_eggs_price = 7196;
    // etcitem
    private static final int quick_step_potion = 734;
    private static final int rp_sealed_ring_of_aurakyria_i = 6849;
    private static final int rp_sealed_sanddragons_earing_i = 6847;
    private static final int rp_sealed_dragon_necklace_i = 6851;

    public _621_EggDelivery() {
        super(false);
        addStartNpc(jeremy);
        addTalkId(pulin, naff, crocus, kuber, beolin, brewer_valentine);
        addQuestItem(q_boiled_eggs, q_eggs_price);
        addLevelCheck(68, 73);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetHTMLCookie = st.getInt("boiled_eggs_delivery_cookie");
        int npcId = npc.getNpcId();
        if (npcId == jeremy) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("boiled_eggs_delivery", String.valueOf(1 * 10 + 1), true);
                st.giveItems(q_boiled_eggs, 5);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "jeremy_q0621_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=621&reply=1") && GetHTMLCookie == 7 - 1) {
                if (st.ownItemCount(q_eggs_price) >= 5) {
                    st.setCond(7);
                    st.setMemoState("boiled_eggs_delivery", String.valueOf(7 * 10 + 1), true);
                    st.takeItems(q_eggs_price, 5);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "jeremy_q0621_0701.htm";
                } else
                    htmltext = "jeremy_q0621_0702.htm";
            }
        } else if (npcId == pulin) {
            if (event.equalsIgnoreCase("menu_select?ask=621&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_boiled_eggs) >= 1) {
                    st.setCond(2);
                    st.setMemoState("boiled_eggs_delivery", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(q_boiled_eggs, 1);
                    st.giveItems(q_eggs_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pulin_q0621_0201.htm";
                } else
                    htmltext = "pulin_q0621_0202.htm";
            }
        } else if (npcId == naff) {
            if (event.equalsIgnoreCase("menu_select?ask=621&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_boiled_eggs) >= 1) {
                    st.setCond(3);
                    st.setMemoState("boiled_eggs_delivery", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_boiled_eggs, 1);
                    st.giveItems(q_eggs_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "naff_q0621_0301.htm";
                } else
                    htmltext = "naff_q0621_0302.htm";
            }
        } else if (npcId == crocus) {
            if (event.equalsIgnoreCase("menu_select?ask=621&reply=1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_boiled_eggs) >= 1) {
                    st.setCond(4);
                    st.setMemoState("boiled_eggs_delivery", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_boiled_eggs, 1);
                    st.giveItems(q_eggs_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "crocus_q0621_0401.htm";
                } else
                    htmltext = "crocus_q0621_0402.htm";
            }
        } else if (npcId == kuber) {
            if (event.equalsIgnoreCase("menu_select?ask=621&reply=1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(q_boiled_eggs) >= 1) {
                    st.setCond(5);
                    st.setMemoState("boiled_eggs_delivery", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(q_boiled_eggs, 1);
                    st.giveItems(q_eggs_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "kuber_q0621_0501.htm";
                } else
                    htmltext = "kuber_q0621_0502.htm";
            }
        } else if (npcId == beolin) {
            if (event.equalsIgnoreCase("menu_select?ask=621&reply=1") && GetHTMLCookie == 6 - 1) {
                if (st.ownItemCount(q_boiled_eggs) >= 1) {
                    st.setCond(6);
                    st.setMemoState("boiled_eggs_delivery", String.valueOf(6 * 10 + 1), true);
                    st.takeItems(q_boiled_eggs, 1);
                    st.giveItems(q_eggs_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "beolin_q0621_0601.htm";
                } else
                    htmltext = "beolin_q0621_0602.htm";
            }
        } else if (npcId == brewer_valentine) {
            if (event.equalsIgnoreCase("menu_select?ask=621&reply=3") && GetHTMLCookie == 8 - 1) {
                int i1 = Rnd.get(1000);
                if (i1 < 800) {
                    st.giveItems(quick_step_potion, 1);
                    st.giveItems(ADENA_ID, 18800);
                } else if (i1 < 800 + 80)
                    st.giveItems(rp_sealed_ring_of_aurakyria_i, 1);
                else if (i1 < 800 + 80 + 80)
                    st.giveItems(rp_sealed_sanddragons_earing_i, 1);
                else if (i1 < 800 + 80 + 80 + 40)
                    st.giveItems(rp_sealed_dragon_necklace_i, 1);
                st.removeMemo("boiled_eggs_delivery");
                st.removeMemo("boiled_eggs_delivery_cookie");
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "brewer_valentine_q0621_0801.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("boiled_eggs_delivery");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == jeremy) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "jeremy_q0621_0103.htm";
                            st.exitQuest(true);
                        default:
                            htmltext = "jeremy_q0621_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == jeremy) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "jeremy_q0621_0105.htm";
                    else if (GetMemoState == 6 * 10 + 1 && st.ownItemCount(q_eggs_price) >= 1) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(6), true);
                        htmltext = "jeremy_q0621_0601.htm";
                    } else if (GetMemoState == 7 * 10 + 1)
                        htmltext = "jeremy_q0621_0703.htm";
                } else if (npcId == pulin) {
                    if (GetMemoState == 1 * 10 + 1 && st.ownItemCount(q_boiled_eggs) >= 5) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(1), true);
                        htmltext = "pulin_q0621_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "pulin_q0621_0203.htm";
                } else if (npcId == naff) {
                    if (GetMemoState == 2 * 10 + 1 && st.ownItemCount(q_eggs_price) >= 1) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(2), true);
                        htmltext = "naff_q0621_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "naff_q0621_0303.htm";
                } else if (npcId == crocus) {
                    if (GetMemoState == 3 * 10 + 1 && st.ownItemCount(q_eggs_price) >= 1) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(3), true);
                        htmltext = "crocus_q0621_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "crocus_q0621_0403.htm";
                } else if (npcId == kuber) {
                    if (GetMemoState == 4 * 10 + 1 && st.ownItemCount(q_eggs_price) >= 1) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(4), true);
                        htmltext = "kuber_q0621_0401.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "kuber_q0621_0503.htm";
                } else if (npcId == beolin) {
                    if (GetMemoState == 5 * 10 + 1 && st.ownItemCount(q_eggs_price) >= 1) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(5), true);
                        htmltext = "beolin_q0621_0501.htm";
                    } else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "beolin_q0621_0603.htm";
                } else if (npcId == brewer_valentine) {
                    if (GetMemoState == 7 * 10 + 1) {
                        st.setMemoState("boiled_eggs_delivery_cookie", String.valueOf(7), true);
                        htmltext = "brewer_valentine_q0621_0701.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}