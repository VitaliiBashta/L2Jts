package org.mmocore.gameserver.scripts.quests;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.0
 * @date 01/06/2016
 * @lastedit 01/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _246_PossessorOfaPreciousSoul3 extends Quest {
    //npc
    private static final int caradine = 31740;
    private static final int magister_ladd = 30721;
    private static final int ossian = 31741;
    //mobs
    private static final int brilliant_prophet = 21541;
    private static final int brilliant_justice = 21544;
    private static final int blinding_fire_barakiel = 25325;
    private static final int brilliant_mark = 21535;
    private static final int brilliant_fang = 21537;
    private static final int brilliant_crown = 21536;
    private static final int brilliant_anguish = 21539;
    //questitem
    private static final int q_ring_waterbinder = 7591;
    private static final int q_necklace_evergreen = 7592;
    private static final int q_staff_rainsong = 7593;
    private static final int q_red_dust = 7594;
    private static final int g_q_piece_of_staff_rainsong = 21725;
    //etcitem
    private static final int q_caradines_letter1 = 7678;
    private static final int q_caradines_letter2 = 7679;

    public _246_PossessorOfaPreciousSoul3() {
        super(false);
        addStartNpc(caradine);
        addTalkId(ossian, magister_ladd);
        addKillId(brilliant_prophet, brilliant_justice, blinding_fire_barakiel, brilliant_mark, brilliant_fang, brilliant_crown, brilliant_anguish);
        addQuestItem(q_ring_waterbinder, q_necklace_evergreen, q_staff_rainsong, q_red_dust, g_q_piece_of_staff_rainsong);
        addLevelCheck(1, 85);
        addQuestCompletedCheck(242);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        int GetHTMLCookie = st.getInt("noble_soul_noblesse_3_cookie");
        int npcId = npc.getNpcId();
        if (npcId == caradine) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("noble_soul_noblesse_3", String.valueOf(1 * 10 + 1), true);
                st.takeItems(q_caradines_letter1, -1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "caradine_q0246_0104.htm";
            }
        } else if (npcId == ossian) {
            if (event.equalsIgnoreCase("menu_select?ask=246&reply=1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("noble_soul_noblesse_3", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ossian_q0246_0201.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=246&reply=1") && GetHTMLCookie == 3 - 1) {
                if (st.ownItemCount(q_ring_waterbinder) >= 1 && st.ownItemCount(q_necklace_evergreen) >= 1) {
                    st.setCond(4);
                    st.setMemoState("noble_soul_noblesse_3", String.valueOf(3 * 10 + 1), true);
                    st.takeItems(q_ring_waterbinder, 1);
                    st.takeItems(q_necklace_evergreen, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ossian_q0246_0301.htm";
                } else {
                    htmltext = "ossian_q0246_0302.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=246&reply=1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_staff_rainsong) >= 1) {
                    st.setCond(6);
                    st.setMemoState("noble_soul_noblesse_3", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_staff_rainsong, 1);
                    st.takeItems(g_q_piece_of_staff_rainsong, -1);
                    st.giveItems(q_red_dust, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ossian_q0246_0401.htm";
                } else if (st.ownItemCount(g_q_piece_of_staff_rainsong) >= 100) {
                    st.setCond(6);
                    st.setMemoState("noble_soul_noblesse_3", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(g_q_piece_of_staff_rainsong, -1);
                    st.giveItems(q_red_dust, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "ossian_q0246_0401.htm";
                } else {
                    htmltext = "ossian_q0246_0402.htm";
                }
            }
        } else if (npcId == magister_ladd) {
            if (event.equalsIgnoreCase("menu_select?ask=246&reply=3") && GetHTMLCookie == 5 - 1) {
                if (st.ownItemCount(q_red_dust) >= 1) {
                    st.takeItems(q_red_dust, -1);
                    st.giveItems(q_caradines_letter2, 1);
                    st.addExpAndSp(719843, 0);
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(false);
                    player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                    htmltext = "magister_ladd_q0246_0501.htm";
                } else {
                    htmltext = "magister_ladd_q0246_0502.htm";
                }
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!st.getPlayer().getPlayerClassComponent().isSubClassActive()) {
            return "quest_not_subclass001.htm";
        }
        String htmltext = NO_QUEST_DIALOG;
        int GetMemoState = st.getInt("noble_soul_noblesse_3");
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == caradine) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case QUEST:
                        case LEVEL:
                            htmltext = "caradine_q0246_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(q_caradines_letter1) >= 0)
                                htmltext = "caradine_q0246_0101.htm";
                            else
                                htmltext = "caradine_q0246_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == caradine) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "caradine_q0246_0105.htm";
                } else if (npcId == ossian) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_3_cookie", String.valueOf(1), true);
                        htmltext = "ossian_q0246_0101.htm";
                    } else if (GetMemoState <= 2 * 10 + 2 && GetMemoState >= 2 * 10 + 1) {
                        if (GetMemoState == 2 * 10 + 2 && st.ownItemCount(q_ring_waterbinder) >= 1 && st.ownItemCount(q_necklace_evergreen) >= 1) {
                            st.setMemoState("noble_soul_noblesse_3_cookie", String.valueOf(2), true);
                            htmltext = "ossian_q0246_0202.htm";
                        } else {
                            htmltext = "ossian_q0246_0203.htm";
                        }
                    } else if (GetMemoState <= 3 * 10 + 2 && GetMemoState >= 3 * 10 + 1) {
                        if ((GetMemoState == 3 * 10 + 2 && st.ownItemCount(q_staff_rainsong) >= 1) || st.ownItemCount(g_q_piece_of_staff_rainsong) >= 100) {
                            st.setMemoState("noble_soul_noblesse_3_cookie", String.valueOf(3), true);
                            htmltext = "ossian_q0246_0303.htm";
                        } else {
                            htmltext = "ossian_q0246_0304.htm";
                        }
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "ossian_q0246_0403.htm";
                } else if (npcId == magister_ladd) {
                    if (st.ownItemCount(q_red_dust) >= 1 && GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_3_cookie", String.valueOf(4), true);
                        htmltext = "magister_ladd_q0246_0401.htm";
                    }
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (!st.getPlayer().getPlayerClassComponent().isSubClassActive()) {
            return null;
        }
        int GetMemoState = st.getInt("noble_soul_noblesse_3");
        int npcId = npc.getNpcId();
        if (npcId == brilliant_prophet) {
            if (GetMemoState == 2 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 400) {
                    if (st.ownItemCount(q_ring_waterbinder) + 1 >= 1) {
                        if (st.ownItemCount(q_ring_waterbinder) < 1) {
                            st.giveItems(q_ring_waterbinder, 1 - st.ownItemCount(q_ring_waterbinder));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_necklace_evergreen) >= 1) {
                            st.setCond(3);
                            st.setMemoState("noble_soul_noblesse_3", String.valueOf(2 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_ring_waterbinder, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == brilliant_justice) {
            if (GetMemoState == 2 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 400) {
                    if (st.ownItemCount(q_necklace_evergreen) + 1 >= 1) {
                        if (st.ownItemCount(q_necklace_evergreen) < 1) {
                            st.giveItems(q_necklace_evergreen, 1 - st.ownItemCount(q_necklace_evergreen));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                        if (st.ownItemCount(q_ring_waterbinder) >= 1) {
                            st.setCond(3);
                            st.setMemoState("noble_soul_noblesse_3", String.valueOf(2 * 10 + 2), true);
                        }
                    } else {
                        st.giveItems(q_necklace_evergreen, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }

            }
        } else if (npcId == blinding_fire_barakiel) {
            if (GetMemoState == 3 * 10 + 1) {
                st.setCond(5);
                st.setMemoState("noble_soul_noblesse_3", String.valueOf(3 * 10 + 2), true);
                st.giveItems(q_staff_rainsong, 1);
                st.soundEffect(SOUND_MIDDLE);
            }
        } else if (npcId == brilliant_mark || npcId == brilliant_fang || npcId == brilliant_crown || npcId == brilliant_anguish) {
            if (GetMemoState == 3 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 200) {
                    if (st.ownItemCount(g_q_piece_of_staff_rainsong) + 1 >= 100) {
                        if (st.ownItemCount(g_q_piece_of_staff_rainsong) < 100) {
                            st.setCond(5);
                            st.setMemoState("noble_soul_noblesse_3", String.valueOf(3 * 10 + 2), true);
                            st.giveItems(g_q_piece_of_staff_rainsong, 100 - st.ownItemCount(g_q_piece_of_staff_rainsong));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(g_q_piece_of_staff_rainsong, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }

            }
        }
        return null;
    }
}