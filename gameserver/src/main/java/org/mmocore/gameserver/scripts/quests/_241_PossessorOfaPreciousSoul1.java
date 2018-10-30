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
 * Based on official High Five
 *
 * @author Magister
 * @version 1.0
 * @date 31/05/2016
 * @lastedit 01/06/2016
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _241_PossessorOfaPreciousSoul1 extends Quest {
    //npc
    private static final int caradine = 31740;
    private static final int gabrielle = 30753;
    private static final int highseer_rahorakti = 31336;
    private static final int kasandra = 31743;
    private static final int master_stedmiel = 30692;
    private static final int muzyk = 31042;
    private static final int ogmar = 31744;
    private static final int talien = 31739;
    private static final int virgil = 31742;
    private static final int watcher_antaras_gilmore = 30754;
    //mobs
    private static final int malruk_succubus = 20244;
    private static final int malruk_succubus_turen = 20245;
    private static final int malruk_succubus_hold = 20283;
    private static final int h_malruk_succubus_turen = 20284;
    private static final int taik_orc_supply_leader = 20669;
    private static final int baraham = 27113;
    //questitem
    private static final int q_legend_of_seventeen = 7587;
    private static final int q_nobelesse_mark_3 = 7597;
    private static final int q_crystal_of_lost_song = 7589;
    private static final int q_faded_poetrybook = 7588;
    private static final int q_nobelesse_mark_4 = 7598;
    private static final int q_nobelesse_mark_5 = 7599;
    //etcitem
    private static final int q_virgils_letter1 = 7677;

    public _241_PossessorOfaPreciousSoul1() {
        super(false);
        addStartNpc(talien);
        addTalkId(gabrielle, watcher_antaras_gilmore, master_stedmiel, muzyk, virgil, ogmar, highseer_rahorakti, kasandra, caradine);
        addKillId(malruk_succubus, malruk_succubus_turen, malruk_succubus_hold, h_malruk_succubus_turen, taik_orc_supply_leader, baraham);
        addQuestItem(q_legend_of_seventeen, q_nobelesse_mark_3, q_crystal_of_lost_song, q_faded_poetrybook, q_nobelesse_mark_4, q_nobelesse_mark_5);
        addLevelCheck(1, 85);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        Player player = st.getPlayer();
        int GetHTMLCookie = st.getInt("noble_soul_noblesse_1_cookie");
        int npcId = npc.getNpcId();
        if (npcId == talien) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(1 * 10 + 1), true);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "talien_q0241_0104.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 4 - 1) {
                if (st.ownItemCount(q_legend_of_seventeen) >= 1) {
                    st.setCond(5);
                    st.setMemoState("noble_soul_noblesse_1", String.valueOf(4 * 10 + 1), true);
                    st.takeItems(q_legend_of_seventeen, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "talien_q0241_0401.htm";
                } else {
                    htmltext = "talien_q0241_0402.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 7 - 1) {
                if (st.ownItemCount(q_crystal_of_lost_song) >= 1) {
                    st.setCond(9);
                    st.setMemoState("noble_soul_noblesse_1", String.valueOf(7 * 10 + 1), true);
                    st.takeItems(q_crystal_of_lost_song, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "talien_q0241_0701.htm";
                } else {
                    htmltext = "talien_q0241_0702.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 9 - 1) {
                if (st.ownItemCount(q_faded_poetrybook) >= 1) {
                    st.setCond(11);
                    st.setMemoState("noble_soul_noblesse_1", String.valueOf(9 * 10 + 1), true);
                    st.takeItems(q_faded_poetrybook, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "talien_q0241_0901.htm";
                } else {
                    htmltext = "talien_q0241_0902.htm";
                }
            }
        } else if (npcId == gabrielle) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 2 - 1) {
                st.setCond(2);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(2 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "gabrielle_q0241_0201.htm";
            }
        } else if (npcId == watcher_antaras_gilmore) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 3 - 1) {
                st.setCond(3);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(3 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "watcher_antaras_gilmore_q0241_0301.htm";
            }
        } else if (npcId == master_stedmiel) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 8 - 1) {
                st.setCond(10);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(8 * 10 + 1), true);
                st.giveItems(q_faded_poetrybook, 1);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "master_stedmiel_q0241_0801.htm";
            }
        } else if (npcId == muzyk) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 5 - 1) {
                st.setCond(6);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(5 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "muzyk_q0241_0501.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 6 - 1) {
                if (st.ownItemCount(q_nobelesse_mark_3) >= 10) {
                    st.setCond(8);
                    st.setMemoState("noble_soul_noblesse_1", String.valueOf(6 * 10 + 1), true);
                    st.takeItems(q_nobelesse_mark_3, 10);
                    st.giveItems(q_crystal_of_lost_song, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "muzyk_q0241_0601.htm";
                } else {
                    htmltext = "muzyk_q0241_0602.htm";
                }
            }
        } else if (npcId == virgil) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 10 - 1) {
                st.setCond(12);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(10 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "virgil_q0241_1001.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 15 - 1) {
                st.setCond(18);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(15 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "virgil_q0241_1501.htm";
            }
        } else if (npcId == ogmar) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 11 - 1) {
                st.setCond(13);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(11 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "ogmar_q0241_1101.htm";
            }
        } else if (npcId == highseer_rahorakti) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 12 - 1) {
                st.setCond(14);
                st.setMemoState("noble_soul_noblesse_1", String.valueOf(12 * 10 + 1), true);
                st.soundEffect(SOUND_MIDDLE);
                htmltext = "highseer_rahorakti_q0241_1201.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 13 - 1) {
                if (st.ownItemCount(q_nobelesse_mark_4) >= 5) {
                    st.setCond(16);
                    st.setMemoState("noble_soul_noblesse_1", String.valueOf(13 * 10 + 1), true);
                    st.takeItems(q_nobelesse_mark_4, 5);
                    st.giveItems(q_nobelesse_mark_5, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "highseer_rahorakti_q0241_1301.htm";
                } else {
                    htmltext = "highseer_rahorakti_q0241_1302.htm";
                }
            }
        } else if (npcId == kasandra) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=1") && GetHTMLCookie == 14 - 1) {
                if (st.ownItemCount(q_nobelesse_mark_5) >= 1) {
                    st.setCond(17);
                    st.setMemoState("noble_soul_noblesse_1", String.valueOf(14 * 10 + 1), true);
                    st.takeItems(q_nobelesse_mark_5, 1);
                    st.soundEffect(SOUND_MIDDLE);
                    htmltext = "kasandra_q0241_1401.htm";
                } else {
                    htmltext = "kasandra_q0241_1402.htm";
                }
            }
        } else if (npcId == caradine) {
            if (event.equalsIgnoreCase("menu_select?ask=241&reply=3") && GetHTMLCookie == 19 - 1) {
                st.giveItems(q_virgils_letter1, 1);
                st.addExpAndSp(263043, 0);
                player.sendPacket(new SocialAction(st.getPlayer().getObjectId(), 3));
                st.soundEffect(SOUND_FINISH);
                st.exitQuest(false);
                htmltext = "caradine_q0241_1901.htm";
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
        int GetMemoState = st.getInt("noble_soul_noblesse_1");
        boolean talker_subjob_id = st.getPlayer().getPlayerClassComponent().isSubClassActive();
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == talien) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "talien_q0241_0103.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (talker_subjob_id)
                                htmltext = "talien_q0241_0101.htm";
                            else
                                htmltext = "talien_q0241_0102.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == talien) {
                    if (GetMemoState == 1 * 10 + 1)
                        htmltext = "talien_q0241_0105.htm";
                    else if (GetMemoState == 3 * 10 + 2) {
                        if (st.ownItemCount(q_legend_of_seventeen) >= 1) {
                            st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(3), true);
                            htmltext = "talien_q0241_0301.htm";
                        } else {
                            htmltext = "talien_q0241_0302.htm";
                        }
                    } else if (GetMemoState == 4 * 10 + 1)
                        htmltext = "talien_q0241_0403.htm";
                    else if (st.ownItemCount(q_crystal_of_lost_song) >= 1 && GetMemoState == 6 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(6), true);
                        htmltext = "talien_q0241_0601.htm";
                    } else if (GetMemoState == 7 * 10 + 1)
                        htmltext = "talien_q0241_0703.htm";
                    else if (st.ownItemCount(q_faded_poetrybook) >= 1 && GetMemoState == 8 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(8), true);
                        htmltext = "talien_q0241_0801.htm";
                    } else if (GetMemoState == 9 * 10 + 1)
                        htmltext = "talien_q0241_0903.htm";
                } else if (npcId == gabrielle) {
                    if (GetMemoState == 1 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(1), true);
                        htmltext = "gabrielle_q0241_0101.htm";
                    } else if (GetMemoState == 2 * 10 + 1)
                        htmltext = "gabrielle_q0241_0202.htm";
                } else if (npcId == watcher_antaras_gilmore) {
                    if (GetMemoState == 2 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(2), true);
                        htmltext = "watcher_antaras_gilmore_q0241_0201.htm";
                    } else if (GetMemoState == 3 * 10 + 1)
                        htmltext = "watcher_antaras_gilmore_q0241_0302.htm";
                } else if (npcId == master_stedmiel) {
                    if (GetMemoState == 7 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(7), true);
                        htmltext = "master_stedmiel_q0241_0701.htm";
                    } else if (GetMemoState == 8 * 10 + 1)
                        htmltext = "master_stedmiel_q0241_0802.htm";
                } else if (npcId == muzyk) {
                    if (GetMemoState == 4 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(4), true);
                        htmltext = "muzyk_q0241_0401.htm";
                    } else if (GetMemoState <= 5 * 10 + 2 && GetMemoState >= 5 * 10 + 1) {
                        if (GetMemoState == 5 * 10 + 2 && st.ownItemCount(q_nobelesse_mark_3) >= 10) {
                            st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(5), true);
                            htmltext = "muzyk_q0241_0502.htm";
                        } else
                            htmltext = "muzyk_q0241_0503.htm";
                    } else if (GetMemoState == 6 * 10 + 1)
                        htmltext = "muzyk_q0241_0603.htm";
                } else if (npcId == virgil) {
                    if (GetMemoState == 9 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(9), true);
                        htmltext = "virgil_q0241_0901.htm";
                    } else if (GetMemoState == 10 * 10 + 1)
                        htmltext = "virgil_q0241_1002.htm";
                    else if (GetMemoState == 14 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(14), true);
                        htmltext = "virgil_q0241_1401.htm";
                    } else if (GetMemoState == 15 * 10 + 1)
                        htmltext = "virgil_q0241_1502.htm";
                } else if (npcId == ogmar) {
                    if (GetMemoState == 10 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(10), true);
                        htmltext = "ogmar_q0241_1001.htm";
                    } else if (GetMemoState == 11 * 10 + 1)
                        htmltext = "ogmar_q0241_1102.htm";
                } else if (npcId == highseer_rahorakti) {
                    if (GetMemoState == 11 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(11), true);
                        htmltext = "highseer_rahorakti_q0241_1101.htm";
                    } else if (GetMemoState <= 12 * 10 + 2 && GetMemoState >= 12 * 10 + 1) {
                        if (GetMemoState == 12 * 10 + 2 && st.ownItemCount(q_nobelesse_mark_4) >= 5) {
                            st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(12), true);
                            htmltext = "highseer_rahorakti_q0241_1202.htm";
                        } else
                            htmltext = "highseer_rahorakti_q0241_1203.htm";
                    } else if (GetMemoState == 13 * 10 + 1)
                        htmltext = "highseer_rahorakti_q0241_1303.htm";
                } else if (npcId == kasandra) {
                    if (st.ownItemCount(q_nobelesse_mark_5) >= 1 && GetMemoState == 13 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(13), true);
                        htmltext = "kasandra_q0241_1301.htm";
                    } else if (GetMemoState == 14 * 10 + 1)
                        htmltext = "kasandra_q0241_1403.htm";
                } else if (npcId == caradine) {
                    if (GetMemoState == 18 * 10 + 1 || GetMemoState == 17 * 10 + 1 || GetMemoState == 16 * 10 + 1 || GetMemoState == 15 * 10 + 1) {
                        st.setMemoState("noble_soul_noblesse_1_cookie", String.valueOf(18), true);
                        htmltext = "caradine_q0241_1501.htm";
                    }
                }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (!st.getPlayer().getPlayerClassComponent().isSubClassActive()) {
            return null;
        }
        int GetMemoState = st.getInt("noble_soul_noblesse_1");
        int npcId = npc.getNpcId();
        if (npcId == malruk_succubus || npcId == malruk_succubus_hold) {
            if (GetMemoState == 5 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 450) {
                    if (st.ownItemCount(q_nobelesse_mark_3) + 1 >= 10) {
                        if (st.ownItemCount(q_nobelesse_mark_3) < 10) {
                            st.setCond(7);
                            st.setMemoState("noble_soul_noblesse_1", String.valueOf(5 * 10 + 2), true);
                            st.giveItems(q_nobelesse_mark_3, 10 - st.ownItemCount(q_nobelesse_mark_3));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_nobelesse_mark_3, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == malruk_succubus_turen || npcId == h_malruk_succubus_turen) {
            if (GetMemoState == 5 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 500) {
                    if (st.ownItemCount(q_nobelesse_mark_3) + 1 >= 10) {
                        if (st.ownItemCount(q_nobelesse_mark_3) < 10) {
                            st.setCond(7);
                            st.setMemoState("noble_soul_noblesse_1", String.valueOf(5 * 10 + 2), true);
                            st.giveItems(q_nobelesse_mark_3, 10 - st.ownItemCount(q_nobelesse_mark_3));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_nobelesse_mark_3, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == taik_orc_supply_leader) {
            if (GetMemoState == 12 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 300) {
                    if (st.ownItemCount(q_nobelesse_mark_4) + 1 >= 5) {
                        if (st.ownItemCount(q_nobelesse_mark_4) < 5) {
                            st.setCond(15);
                            st.setMemoState("noble_soul_noblesse_1", String.valueOf(12 * 10 + 2), true);
                            st.giveItems(q_nobelesse_mark_4, 5 - st.ownItemCount(q_nobelesse_mark_4));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_nobelesse_mark_4, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        } else if (npcId == baraham) {
            if (GetMemoState == 3 * 10 + 1) {
                int i4 = Rnd.get(1000);
                if (i4 < 1000) {
                    if (st.ownItemCount(q_legend_of_seventeen) + 1 >= 1) {
                        if (st.ownItemCount(q_legend_of_seventeen) < 1) {
                            st.setCond(4);
                            st.setMemoState("noble_soul_noblesse_1", String.valueOf(3 * 10 + 2), true);
                            st.giveItems(q_legend_of_seventeen, 1 - st.ownItemCount(q_legend_of_seventeen));
                            st.soundEffect(SOUND_MIDDLE);
                        }
                    } else {
                        st.giveItems(q_legend_of_seventeen, 1);
                        st.soundEffect(SOUND_ITEMGET);
                    }
                }
            }
        }
        return null;
    }
}