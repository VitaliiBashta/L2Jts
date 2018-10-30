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
 * @date 17/06/2016
 * @lastedit 17/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _622_DeliveryofSpecialLiquor extends Quest {
    //npc
    private static final int jeremy = 31521;
    private static final int warehouse_keeper_lietta = 31267;
    private static final int pulin = 31543;
    private static final int naff = 31544;
    private static final int crocus = 31545;
    private static final int kuber = 31546;
    private static final int beolin = 31547;
    //questitem
    private static final int q_special_drink = 7197;
    private static final int q_special_drink_price = 7198;
    //etcitem
    private static final int rp_sealed_ring_of_aurakyria_i = 6849;
    private static final int rp_sealed_sanddragons_earing_i = 6847;
    private static final int rp_sealed_dragon_necklace_i = 6851;
    private static final int quick_step_potion = 734;

    public _622_DeliveryofSpecialLiquor() {
        super(false);
        addStartNpc(jeremy);
        addTalkId(beolin, kuber, crocus, naff, pulin, warehouse_keeper_lietta);
        addQuestItem(q_special_drink, q_special_drink_price);
        addLevelCheck(68, 73);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        final int GetHTMLCookie = st.getInt("special_drink_delivery_cookie");
        final int npcId = npc.getNpcId();
        if (npcId == jeremy) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("special_drink_delivery", String.valueOf(1 * 10 + 1), true);
                st.giveItems(q_special_drink, 5);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "jeremy_q0622_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=622&reply=1") && GetHTMLCookie == 7 - 1) {
                if (st.ownItemCount(q_special_drink_price) >= 5) {
                    st.setCond(7);
                    st.setMemoState("special_drink_delivery", String.valueOf(7 * 10 + 1), true);
                    st.takeItems(q_special_drink_price, 5);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "jeremy_q0622_0701.htm";
                } else
                    htmltext = "jeremy_q0622_0702.htm";
            }
        } else if (npcId == beolin) {
            if (event.equalsIgnoreCase("menu_select?ask=622&reply=1") && GetHTMLCookie == 2 - 1) {
                if (st.ownItemCount(q_special_drink) >= 1) {
                    st.setCond(2);
                    st.setMemoState("special_drink_delivery", String.valueOf(2 * 10 + 1), true);
                    st.takeItems(q_special_drink, 1);
                    st.giveItems(q_special_drink_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "beolin_q0622_0201.htm";
                } else
                    htmltext = "beolin_q0622_0202.htm";
            }
        } else if (npcId == kuber) {
            if (event.equalsIgnoreCase("menu_select?ask=622&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_special_drink) >= 1) {
                    st.setCond(3);
                    st.setMemoState("special_drink_delivery", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_special_drink, 1);
                    st.giveItems(q_special_drink_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "kuber_q0622_0301.htm";
                } else
                    htmltext = "kuber_q0622_0302.htm";
            }
        } else if (npcId == crocus) {
            if (event.equalsIgnoreCase("menu_select?ask=622&reply=1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_special_drink) >= 1) {
                    st.setCond(4);
                    st.setMemoState("special_drink_delivery", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_special_drink, 1);
                    st.giveItems(q_special_drink_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "crocus_q0622_0401.htm";
                } else
                    htmltext = "crocus_q0622_0402.htm";
            }
        } else if (npcId == naff) {
            if (event.equalsIgnoreCase("menu_select?ask=622&reply=1") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(q_special_drink) >= 1) {
                    st.setCond(5);
                    st.setMemoState("special_drink_delivery", String.valueOf(5 * 10 + 1), true);
                    st.takeItems(q_special_drink, 1);
                    st.giveItems(q_special_drink_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "naff_q0622_0501.htm";
                } else
                    htmltext = "naff_q0622_0502.htm";
            }
        } else if (npcId == pulin) {
            if (event.equalsIgnoreCase("menu_select?ask=622&reply=1") && GetHTMLCookie == 6 - 1) {
                if (st.ownItemCount(q_special_drink) >= 1) {
                    st.setCond(6);
                    st.setMemoState("special_drink_delivery", String.valueOf(6 * 10 + 1), true);
                    st.takeItems(q_special_drink, 1);
                    st.giveItems(q_special_drink_price, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "pulin_q0622_0601.htm";
                } else
                    htmltext = "pulin_q0622_0602.htm";
            }
        } else if (npcId == warehouse_keeper_lietta) {
            if (event.equalsIgnoreCase("menu_select?ask=622&reply=3") && GetHTMLCookie == 8 - 1) {
                int i1 = Rnd.get(1000);
                if (i1 < 800) {
                    st.giveItems(quick_step_potion, 1);
                    st.giveItems(ADENA_ID, 18800);
                } else if (i1 < 800 + 80) {
                    st.giveItems(rp_sealed_ring_of_aurakyria_i, 1);
                } else if (i1 < 800 + 80 + 80) {
                    st.giveItems(rp_sealed_sanddragons_earing_i, 1);
                } else if (i1 < 800 + 80 + 80 + 40) {
                    st.giveItems(rp_sealed_dragon_necklace_i, 1);
                }
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(true);
                htmltext = "warehouse_keeper_lietta_q0622_0801.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("special_drink_delivery");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == jeremy) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "jeremy_q0622_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            htmltext = "jeremy_q0622_0101.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == jeremy) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "jeremy_q0622_0105.htm";
                    else if (st.ownItemCount(q_special_drink_price) >= 1 && GetMemoState == 6 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(6), true);
                        htmltext = "jeremy_q0622_0601.htm";
                    } else if (GetMemoState == 7 * 10 + 1)
                        htmltext = "jeremy_q0622_0703.htm";
                } else if (npcId == beolin) {
                    if (st.ownItemCount(q_special_drink) >= 5 && GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(1), true);
                        htmltext = "beolin_q0622_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "beolin_q0622_0203.htm";
                } else if (npcId == kuber) {
                    if (st.ownItemCount(q_special_drink_price) >= 1 && GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(2), true);
                        htmltext = "kuber_q0622_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "kuber_q0622_0303.htm";
                } else if (npcId == crocus) {
                    if (st.ownItemCount(q_special_drink_price) >= 1 && GetMemoState == 3 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(3), true);
                        htmltext = "crocus_q0622_0301.htm";
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "crocus_q0622_0403.htm";
                } else if (npcId == naff) {
                    if (st.ownItemCount(q_special_drink_price) >= 1 && GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(4), true);
                        htmltext = "naff_q0622_0401.htm";
                    } else if (GetMemoState == 5 * 10 + 1)
                        htmltext = "naff_q0622_0503.htm";
                } else if (npcId == pulin) {
                    if (st.ownItemCount(q_special_drink_price) >= 1 && GetMemoState == 5 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(5), true);
                        htmltext = "pulin_q0622_0501.htm";
                    } else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "pulin_q0622_0603.htm";
                } else if (npcId == warehouse_keeper_lietta) {
                    if (GetMemoState == 7 * 10 + 1) {
                        st.setMemoState("special_drink_delivery_cookie", String.valueOf(7), true);
                        htmltext = "warehouse_keeper_lietta_q0622_0701.htm";
                    }
                }
                break;
        }
        return htmltext;
    }
}