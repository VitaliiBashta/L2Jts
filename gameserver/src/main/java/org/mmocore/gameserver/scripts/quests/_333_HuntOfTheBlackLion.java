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
 * @date 31/10/2015
 * @testing OK
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class _333_HuntOfTheBlackLion extends Quest {
    // npc
    private static final int sophia = 30735;
    private static final int redfoot = 30736;
    private static final int blacksmith_rupio = 30471;
    private static final int undres = 30130;
    private static final int first_elder_lockirin = 30531;
    private static final int morgan = 30737;
    // mobs
    private static final int marsh_stakato = 20157;
    private static final int neer_crawler = 20160;
    private static final int specter = 20171;
    private static final int lemures = 20197;
    private static final int neer_crawler_frak = 20198;
    private static final int strain = 20200;
    private static final int ghoul = 20201;
    private static final int ol_mahum_guerilla = 20207;
    private static final int ol_mahum_raider = 20208;
    private static final int ol_mahum_sniper = 20209;
    private static final int ol_mahum_sergeant = 20210;
    private static final int ol_mahum_chief_leader = 20211;
    private static final int marsh_stakato_worker = 20230;
    private static final int marsh_stakato_soldier = 20232;
    private static final int marsh_stakato_drone = 20234;
    private static final int delu_lizardman = 20251;
    private static final int delu_lizardman_scout = 20252;
    private static final int delu_lizardman_warrior = 20253;
    private static final int delu_lizardm_headhunter = 27151;
    private static final int marsh_stakato_marquess = 27152;
    // questitem
    private static final int black_lion_mark = 1369;
    private static final int q_lions_claw = 3675;
    private static final int q_lions_eye = 3676;
    private static final int q_guild_coin = 3677;
    private static final int q_undead_ash = 3848;
    private static final int q_bloodyaxe_insignia = 3849;
    private static final int q_delu_lizardman_fang = 3850;
    private static final int q_stakato_talon = 3851;
    private static final int q_sophias_order1 = 3671;
    private static final int q_sophias_order2 = 3672;
    private static final int q_sophias_order3 = 3673;
    private static final int q_sophias_order4 = 3674;
    // etcitem
    private static final int q_cargo_box1 = 3440;
    private static final int q_cargo_box2 = 3441;
    private static final int q_cargo_box3 = 3442;
    private static final int q_cargo_box4 = 3443;
    private static final int healing_potion = 727;
    private static final int soulshot_d = 1463;
    private static final int spiritshot_d = 2510;
    private static final int scroll_of_escape = 736;
    private static final int swift_attack_potion = 735;
    private static final int q_loot_1 = 3444;
    private static final int q_loot_2 = 3445;
    private static final int q_loot_3 = 3446;
    private static final int q_loot_4 = 3447;
    private static final int q_loot_5 = 3448;
    private static final int q_loot_6 = 3449;
    private static final int q_loot_7 = 3450;
    private static final int q_loot_8 = 3451;
    private static final int q_loot_9 = 3452;
    private static final int q_loot_10 = 3453;
    private static final int q_loot_11 = 3454;
    private static final int q_loot_12 = 3455;
    private static final int q_loot_13 = 3456;
    private static final int q_loot_14 = 3457;
    private static final int q_loot_15 = 3458;
    private static final int q_loot_16 = 3459;
    private static final int q_loot_17 = 3460;
    private static final int q_loot_18 = 3461;
    private static final int q_loot_19 = 3462;
    private static final int q_loot_20 = 3463;
    private static final int q_loot_21 = 3464;
    private static final int q_loot_22 = 3465;
    private static final int q_loot_23 = 3466;

    public _333_HuntOfTheBlackLion() {
        super(false);
        addStartNpc(sophia);
        addTalkId(morgan, redfoot, blacksmith_rupio, undres, first_elder_lockirin);
        addKillId(marsh_stakato, neer_crawler, specter, lemures, neer_crawler_frak, strain, ghoul, ol_mahum_guerilla, ol_mahum_raider, ol_mahum_sniper, ol_mahum_sergeant, ol_mahum_chief_leader, marsh_stakato_worker, marsh_stakato_soldier, marsh_stakato_drone, delu_lizardman, delu_lizardman_scout, delu_lizardman_warrior, delu_lizardm_headhunter, marsh_stakato_marquess);
        addQuestItem(q_lions_claw, q_lions_eye, q_guild_coin, q_undead_ash, q_bloodyaxe_insignia, q_delu_lizardman_fang, q_stakato_talon, q_sophias_order1, q_sophias_order2, q_sophias_order3, q_sophias_order4);
        addLevelCheck(25, 39);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int GetMemoState = st.getInt("hunt_of_blacklion");
        int npcId = npc.getNpcId();
        if (npcId == sophia) {
            if (event.equalsIgnoreCase("quest_accept")) {
                st.setCond(1);
                st.setState(STARTED);
                st.soundEffect(SOUND_ACCEPT);
                htmltext = "sophia_q0333_04.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=1"))
                htmltext = "sophia_q0333_05.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=2"))
                htmltext = "sophia_q0333_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=3"))
                htmltext = "sophia_q0333_07.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=4"))
                htmltext = "sophia_q0333_08.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=5"))
                htmltext = "sophia_q0333_09.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=6")) {
                if (st.ownItemCount(q_sophias_order1) == 0) {
                    st.giveItems(q_sophias_order1, 1);
                    htmltext = "sophia_q0333_10.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=7")) {
                if (st.ownItemCount(q_sophias_order2) == 0) {
                    st.giveItems(q_sophias_order2, 1);
                    htmltext = "sophia_q0333_11.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=8")) {
                if (st.ownItemCount(q_sophias_order3) == 0) {
                    st.giveItems(q_sophias_order3, 1);
                    htmltext = "sophia_q0333_12.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=9")) {
                if (st.ownItemCount(q_sophias_order4) == 0) {
                    st.giveItems(q_sophias_order4, 1);
                    htmltext = "sophia_q0333_13.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=10")) {
                if (st.ownItemCount(q_lions_claw) < 10) {
                    htmltext = "sophia_q0333_16.htm";
                } else if (st.ownItemCount(q_lions_claw) >= 10 && st.ownItemCount(q_lions_eye) < 4) {
                    st.giveItems(q_lions_eye, 1);
                    int i0 = Rnd.get(100);
                    if (i0 < 25) {
                        st.giveItems(healing_potion, 20);
                    } else if (i0 < 50) {
                        if (!st.getPlayer().getPlayerClassComponent().getClassId().isMage()) {
                            st.giveItems(soulshot_d, 100);
                        } else {
                            st.giveItems(spiritshot_d, 50);
                        }
                    } else if (i0 < 75) {
                        st.giveItems(scroll_of_escape, 20);
                    } else {
                        st.giveItems(swift_attack_potion, 3);
                    }
                    st.takeItems(q_lions_claw, 10);
                    htmltext = "sophia_q0333_17a.htm";
                } else if (st.ownItemCount(q_lions_claw) >= 10 && st.ownItemCount(q_lions_eye) >= 4 && st.ownItemCount(q_lions_eye) <= 7) {
                    st.giveItems(q_lions_eye, 1);
                    int i0 = Rnd.get(100);
                    if (i0 < 25) {
                        st.giveItems(healing_potion, 25);
                    } else if (i0 < 50) {
                        if (!st.getPlayer().getPlayerClassComponent().getClassId().isMage()) {
                            st.giveItems(soulshot_d, 200);
                        } else {
                            st.giveItems(spiritshot_d, 100);
                        }
                    } else if (i0 < 75) {
                        st.giveItems(scroll_of_escape, 20);
                    } else {
                        st.giveItems(swift_attack_potion, 3);
                    }
                    st.takeItems(q_lions_claw, 10);
                    htmltext = "sophia_q0333_18b.htm";
                } else if (st.ownItemCount(q_lions_claw) >= 10 && st.ownItemCount(q_lions_eye) >= 8) {
                    if (st.ownItemCount(q_lions_eye) > 8) {
                        st.takeItems(q_lions_eye, st.ownItemCount(q_lions_eye) - 8);
                    }
                    int i0 = Rnd.get(100);
                    if (i0 < 25) {
                        st.giveItems(healing_potion, 50);
                    } else if (i0 < 50) {
                        if (!st.getPlayer().getPlayerClassComponent().getClassId().isMage()) {
                            st.giveItems(soulshot_d, 400);
                        } else {
                            st.giveItems(spiritshot_d, 200);
                        }
                    } else if (i0 < 75) {
                        st.giveItems(scroll_of_escape, 30);
                    } else {
                        st.giveItems(swift_attack_potion, 4);
                    }
                    st.takeItems(q_lions_claw, 10);
                    htmltext = "sophia_q0333_19b.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=11")) {
                st.takeItems(q_sophias_order1, -1);
                st.takeItems(q_sophias_order2, -1);
                st.takeItems(q_sophias_order3, -1);
                st.takeItems(q_sophias_order4, -1);
                htmltext = "sophia_q0333_20.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=12"))
                htmltext = "sophia_q0333_21.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=13"))
                htmltext = "sophia_q0333_24a.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=14"))
                htmltext = "sophia_q0333_25b.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=15")) {
                if (st.ownItemCount(black_lion_mark) >= 1) {
                    st.giveItems(ADENA_ID, 12400);
                    st.takeItems(black_lion_mark, -1);
                    htmltext = "sophia_q0333_26.htm";
                    st.soundEffect(SOUND_FINISH);
                    st.exitQuest(true);
                }
            }
        } else if (npcId == morgan) {
            if (event.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1) {
                    if (st.ownItemCount(q_cargo_box1) >= 1) {
                        st.takeItems(q_cargo_box1, 1);
                    } else if (st.ownItemCount(q_cargo_box2) >= 1) {
                        st.takeItems(q_cargo_box2, 1);
                    } else if (st.ownItemCount(q_cargo_box3) >= 1) {
                        st.takeItems(q_cargo_box3, 1);
                    } else if (st.ownItemCount(q_cargo_box4) >= 1) {
                        st.takeItems(q_cargo_box4, 1);
                    }
                    if (st.ownItemCount(q_guild_coin) < 80) {
                        st.giveItems(q_guild_coin, 1);
                    } else if (st.ownItemCount(q_guild_coin) > 80) {
                        st.takeItems(q_guild_coin, st.ownItemCount(q_guild_coin) - 80);
                    }
                    if (st.ownItemCount(q_guild_coin) < 40) {
                        st.giveItems(ADENA_ID, 100);
                        htmltext = "morgan_q0333_03.htm";
                    } else if (st.ownItemCount(q_guild_coin) >= 40 && st.ownItemCount(q_guild_coin) < 80) {
                        st.giveItems(ADENA_ID, 200);
                        htmltext = "morgan_q0333_04.htm";
                    } else {
                        st.giveItems(ADENA_ID, 300);
                        htmltext = "morgan_q0333_05.htm";
                    }
                } else
                    htmltext = "morgan_q0333_06.htm";
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=2"))
                htmltext = "morgan_q0333_07.htm";
        } else if (npcId == redfoot) {
            if (event.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                int i0 = Rnd.get(100);
                int i1 = Rnd.get(100);
                if (st.ownItemCount(ADENA_ID) < 650 && st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1) {
                    htmltext = "redfoot_q0333_03.htm";
                } else if (st.ownItemCount(ADENA_ID) >= 650 && st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1) {
                    if (i0 < 40) {
                        if (i1 < 33) {
                            st.giveItems(q_loot_1, 1);
                            htmltext = "redfoot_q0333_04a.htm";
                        } else if (i1 < 66) {
                            st.giveItems(q_loot_2, 1);
                            htmltext = "redfoot_q0333_04b.htm";
                        } else {
                            st.giveItems(q_loot_3, 1);
                            htmltext = "redfoot_q0333_04c.htm";
                        }
                    } else if (i0 < 60) {
                        if (i1 < 33) {
                            st.giveItems(q_loot_4, 1);
                            htmltext = "redfoot_q0333_04d.htm";
                        } else if (i1 < 66) {
                            st.giveItems(q_loot_5, 1);
                            htmltext = "redfoot_q0333_04e.htm";
                        } else {
                            st.giveItems(q_loot_6, 1);
                            htmltext = "redfoot_q0333_04f.htm";
                        }
                    } else if (i0 < 70) {
                        if (i1 < 33) {
                            st.giveItems(q_loot_7, 1);
                            htmltext = "redfoot_q0333_04g.htm";
                        } else if (i1 < 66) {
                            st.giveItems(q_loot_8, 1);
                            htmltext = "redfoot_q0333_04h.htm";
                        } else {
                            st.giveItems(q_loot_9, 1);
                            htmltext = "redfoot_q0333_04i.htm";
                        }
                    } else if (i0 < 75) {
                        if (i1 < 33) {
                            st.giveItems(q_loot_10, 1);
                            htmltext = "redfoot_q0333_04j.htm";
                        } else if (i1 < 66) {
                            st.giveItems(q_loot_11, 1);
                            htmltext = "redfoot_q0333_04k.htm";
                        } else {
                            st.giveItems(q_loot_12, 1);
                            htmltext = "redfoot_q0333_04l.htm";
                        }
                    } else if (i0 < 76) {
                        st.giveItems(q_loot_13, 1);
                        htmltext = "redfoot_q0333_04m.htm";
                    } else if (Rnd.get(100) < 50) {
                        if (i1 < 25) {
                            st.giveItems(q_loot_14, 1);
                        } else if (i1 < 50) {
                            st.giveItems(q_loot_15, 1);
                        } else if (i1 < 75) {
                            st.giveItems(q_loot_16, 1);
                        } else {
                            st.giveItems(q_loot_17, 1);
                        }
                        htmltext = "redfoot_q0333_04n.htm";
                    } else {
                        if (i1 < 25) {
                            st.giveItems(q_loot_19, 1);
                        } else if (i1 < 50) {
                            st.giveItems(q_loot_20, 1);
                        } else if (i1 < 75) {
                            st.giveItems(q_loot_21, 1);
                        } else {
                            st.giveItems(q_loot_22, 1);
                        }
                        htmltext = "redfoot_q0333_04o.htm";
                    }
                    st.takeItems(ADENA_ID, 650);
                    if (st.ownItemCount(q_cargo_box1) >= 1) {
                        st.takeItems(q_cargo_box1, 1);
                    } else if (st.ownItemCount(q_cargo_box2) >= 1) {
                        st.takeItems(q_cargo_box2, 1);
                    } else if (st.ownItemCount(q_cargo_box3) >= 1) {
                        st.takeItems(q_cargo_box3, 1);
                    } else if (st.ownItemCount(q_cargo_box4) >= 1) {
                        st.takeItems(q_cargo_box4, 1);
                    }
                } else if (st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) < 1) {
                    htmltext = "redfoot_q0333_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=2"))
                htmltext = "redfoot_q0333_06.htm";
            else if (event.equalsIgnoreCase("menu_select?ask=333&reply=3")) {
                int i0 = Rnd.get(100);
                if (st.ownItemCount(ADENA_ID) < 200 + GetMemoState * 200) {
                    htmltext = "redfoot_q0333_07.htm";
                } else if (GetMemoState * 100 > 200) {
                    htmltext = "redfoot_q0333_08.htm";
                }
                if (i0 < 5) {
                    htmltext = "redfoot_q0333_08a.htm";
                } else if (i0 < 10) {
                    htmltext = "redfoot_q0333_08b.htm";
                } else if (i0 < 15) {
                    htmltext = "redfoot_q0333_08c.htm";
                } else if (i0 < 20) {
                    htmltext = "redfoot_q0333_08d.htm";
                } else if (i0 < 25) {
                    htmltext = "redfoot_q0333_08e.htm";
                } else if (i0 < 30) {
                    htmltext = "redfoot_q0333_08f.htm";
                } else if (i0 < 35) {
                    htmltext = "redfoot_q0333_08g.htm";
                } else if (i0 < 40) {
                    htmltext = "redfoot_q0333_08h.htm";
                } else if (i0 < 45) {
                    htmltext = "redfoot_q0333_08i.htm";
                } else if (i0 < 50) {
                    htmltext = "redfoot_q0333_08j.htm";
                } else if (i0 < 55) {
                    htmltext = "redfoot_q0333_08k.htm";
                } else if (i0 < 60) {
                    htmltext = "redfoot_q0333_08l.htm";
                } else if (i0 < 65) {
                    htmltext = "redfoot_q0333_08m.htm";
                } else if (i0 < 70) {
                    htmltext = "redfoot_q0333_08n.htm";
                } else if (i0 < 75) {
                    htmltext = "redfoot_q0333_08o.htm";
                } else if (i0 < 80) {
                    htmltext = "redfoot_q0333_08p.htm";
                } else if (i0 < 85) {
                    htmltext = "redfoot_q0333_08q.htm";
                } else if (i0 < 90) {
                    htmltext = "redfoot_q0333_08r.htm";
                } else if (i0 < 95) {
                    htmltext = "redfoot_q0333_08s.htm";
                } else {
                    htmltext = "redfoot_q0333_08t.htm";
                }
                st.takeItems(ADENA_ID, 200 + GetMemoState * 200);
                st.setMemoState("hunt_of_blacklion", String.valueOf(GetMemoState + 1), true);
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=4"))
                htmltext = "redfoot_q0333_09.htm";
        } else if (npcId == blacksmith_rupio) {
            if (event.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (st.ownItemCount(q_loot_14) == 0 || st.ownItemCount(q_loot_15) == 0 || st.ownItemCount(q_loot_16) == 0 || st.ownItemCount(q_loot_17) == 0) {
                    htmltext = "blacksmith_rupio_q0333_03.htm";
                }
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_loot_18, 1);
                    st.takeItems(q_loot_14, 1);
                    st.takeItems(q_loot_15, 1);
                    st.takeItems(q_loot_16, 1);
                    st.takeItems(q_loot_17, 1);
                    htmltext = "blacksmith_rupio_q0333_04.htm";
                } else {
                    st.takeItems(q_loot_14, 1);
                    st.takeItems(q_loot_15, 1);
                    st.takeItems(q_loot_16, 1);
                    st.takeItems(q_loot_17, 1);
                    htmltext = "blacksmith_rupio_q0333_05.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                if (st.ownItemCount(q_loot_19) == 0 || st.ownItemCount(q_loot_20) == 0 || st.ownItemCount(q_loot_21) == 0 || st.ownItemCount(q_loot_22) == 0) {
                    htmltext = "blacksmith_rupio_q0333_06.htm";
                }
                if (Rnd.get(100) < 50) {
                    st.giveItems(q_loot_23, 1);
                    st.takeItems(q_loot_19, 1);
                    st.takeItems(q_loot_20, 1);
                    st.takeItems(q_loot_21, 1);
                    st.takeItems(q_loot_22, 1);
                    htmltext = "blacksmith_rupio_q0333_07.htm";
                } else {
                    st.takeItems(q_loot_19, 1);
                    st.takeItems(q_loot_20, 1);
                    st.takeItems(q_loot_21, 1);
                    st.takeItems(q_loot_22, 1);
                    htmltext = "blacksmith_rupio_q0333_08.htm";
                }
            }
        } else if (npcId == undres) {
            if (event.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (st.ownItemCount(q_loot_18) >= 1) {
                    st.giveItems(ADENA_ID, 30000);
                    st.takeItems(q_loot_18, 1);
                    htmltext = "undres_q0333_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=2"))
                htmltext = "undres_q0333_05.htm";
        } else if (npcId == first_elder_lockirin) {
            if (event.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (st.ownItemCount(q_loot_23) >= 1) {
                    st.giveItems(ADENA_ID, 30000);
                    st.takeItems(q_loot_23, 1);
                    htmltext = "first_elder_lockirin_q0333_04.htm";
                }
            } else if (event.equalsIgnoreCase("menu_select?ask=333&reply=2"))
                htmltext = "first_elder_lockirin_q0333_05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = NO_QUEST_DIALOG;
        int npcId = npc.getNpcId();
        int id = st.getState();
        switch (id) {
            case CREATED:
                if (npcId == sophia) {
                    switch (isAvailableFor(st.getPlayer())) {
                        case LEVEL:
                            htmltext = "sophia_q0333_01.htm";
                            st.exitQuest(true);
                            break;
                        default:
                            if (st.ownItemCount(black_lion_mark) == 0)
                                htmltext = "sophia_q0333_02.htm";
                            else
                                htmltext = "sophia_q0333_03.htm";
                            break;
                    }
                }
                break;
            case STARTED:
                if (npcId == sophia) {
                    if (st.ownItemCount(q_sophias_order1) + st.ownItemCount(q_sophias_order2) + st.ownItemCount(q_sophias_order3) + st.ownItemCount(q_sophias_order4) == 0)
                        htmltext = "sophia_q0333_14.htm";
                    else if (st.ownItemCount(q_sophias_order1) + st.ownItemCount(q_sophias_order2) + st.ownItemCount(q_sophias_order3) + st.ownItemCount(q_sophias_order4) == 1 && st.ownItemCount(q_undead_ash) + st.ownItemCount(q_bloodyaxe_insignia) + st.ownItemCount(q_delu_lizardman_fang) + st.ownItemCount(q_stakato_talon) < 1 && st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) < 1)
                        htmltext = "sophia_q0333_15.htm";
                    else if (st.ownItemCount(q_sophias_order1) + st.ownItemCount(q_sophias_order2) + st.ownItemCount(q_sophias_order3) + st.ownItemCount(q_sophias_order4) == 1 && st.ownItemCount(q_undead_ash) + st.ownItemCount(q_bloodyaxe_insignia) + st.ownItemCount(q_delu_lizardman_fang) + st.ownItemCount(q_stakato_talon) < 1 && st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1)
                        htmltext = "sophia_q0333_15a.htm";
                    else if (st.ownItemCount(q_sophias_order1) + st.ownItemCount(q_sophias_order2) + st.ownItemCount(q_sophias_order3) + st.ownItemCount(q_sophias_order4) == 1 && st.ownItemCount(q_undead_ash) + st.ownItemCount(q_bloodyaxe_insignia) + st.ownItemCount(q_delu_lizardman_fang) + st.ownItemCount(q_stakato_talon) >= 1 && st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) == 0) {
                        long i0 = st.ownItemCount(q_undead_ash) + st.ownItemCount(q_bloodyaxe_insignia) + st.ownItemCount(q_delu_lizardman_fang) + st.ownItemCount(q_stakato_talon);
                        if (i0 < 20) {
                        } else if (i0 < 50)
                            st.giveItems(q_lions_claw, 1);
                        else if (i0 < 100)
                            st.giveItems(q_lions_claw, 2);
                        else
                            st.giveItems(q_lions_claw, 3);
                        st.giveItems(ADENA_ID, st.ownItemCount(q_undead_ash) * 35 + st.ownItemCount(q_stakato_talon) * 35 + st.ownItemCount(q_delu_lizardman_fang) * 35 + st.ownItemCount(q_bloodyaxe_insignia) * 35);
                        st.takeItems(q_undead_ash, -1);
                        st.takeItems(q_bloodyaxe_insignia, -1);
                        st.takeItems(q_delu_lizardman_fang, -1);
                        st.takeItems(q_stakato_talon, -1);
                        htmltext = "sophia_q0333_22.htm";
                        st.setMemoState("hunt_of_blacklion", String.valueOf(0), true);
                    } else if (st.ownItemCount(q_sophias_order1) + st.ownItemCount(q_sophias_order2) + st.ownItemCount(q_sophias_order3) + st.ownItemCount(q_sophias_order4) == 1 && st.ownItemCount(q_undead_ash) + st.ownItemCount(q_bloodyaxe_insignia) + st.ownItemCount(q_delu_lizardman_fang) + st.ownItemCount(q_stakato_talon) >= 1 && st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1) {
                        long i0 = st.ownItemCount(q_undead_ash) + st.ownItemCount(q_bloodyaxe_insignia) + st.ownItemCount(q_delu_lizardman_fang) + st.ownItemCount(q_stakato_talon);
                        if (i0 < 20) {
                        } else if (i0 < 50)
                            st.giveItems(q_lions_claw, 1);
                        else if (i0 < 100)
                            st.giveItems(q_lions_claw, 2);
                        else
                            st.giveItems(q_lions_claw, 3);
                        st.giveItems(ADENA_ID, st.ownItemCount(q_undead_ash) * 35);
                        st.giveItems(ADENA_ID, st.ownItemCount(q_bloodyaxe_insignia) * 35);
                        st.giveItems(ADENA_ID, st.ownItemCount(q_delu_lizardman_fang) * 35);
                        st.giveItems(ADENA_ID, st.ownItemCount(q_stakato_talon) * 35);
                        st.takeItems(q_undead_ash, -1);
                        st.takeItems(q_bloodyaxe_insignia, -1);
                        st.takeItems(q_delu_lizardman_fang, -1);
                        st.takeItems(q_stakato_talon, -1);
                        htmltext = "sophia_q0333_23.htm";
                        st.setMemoState("hunt_of_blacklion", String.valueOf(0), true);
                    }
                } else if (npcId == morgan) {
                    if (st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) == 0)
                        htmltext = "morgan_q0333_01.htm";
                    else if (st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1)
                        htmltext = "morgan_q0333_02.htm";
                } else if (npcId == redfoot) {
                    if (st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) == 0)
                        htmltext = "redfoot_q0333_01.htm";
                    else if (st.ownItemCount(q_cargo_box1) + st.ownItemCount(q_cargo_box2) + st.ownItemCount(q_cargo_box3) + st.ownItemCount(q_cargo_box4) >= 1)
                        htmltext = "redfoot_q0333_02.htm";
                } else if (npcId == blacksmith_rupio) {
                    if (st.ownItemCount(q_loot_14) + st.ownItemCount(q_loot_15) + st.ownItemCount(q_loot_16) + st.ownItemCount(q_loot_17) < 1 && st.ownItemCount(q_loot_19) + st.ownItemCount(q_loot_20) + st.ownItemCount(q_loot_21) + st.ownItemCount(q_loot_22) < 1)
                        htmltext = "blacksmith_rupio_q0333_01.htm";
                    else if (st.ownItemCount(q_loot_14) + st.ownItemCount(q_loot_15) + st.ownItemCount(q_loot_16) + st.ownItemCount(q_loot_17) >= 1 || st.ownItemCount(q_loot_19) + st.ownItemCount(q_loot_20) + st.ownItemCount(q_loot_21) + st.ownItemCount(q_loot_22) >= 1)
                        htmltext = "blacksmith_rupio_q0333_02.htm";
                } else if (npcId == undres) {
                    if (st.ownItemCount(q_loot_14) + st.ownItemCount(q_loot_15) + st.ownItemCount(q_loot_16) + st.ownItemCount(q_loot_17) < 1 && st.ownItemCount(q_loot_18) == 0)
                        htmltext = "undres_q0333_01.htm";
                    else if (st.ownItemCount(q_loot_14) + st.ownItemCount(q_loot_15) + st.ownItemCount(q_loot_16) + st.ownItemCount(q_loot_17) >= 1 && st.ownItemCount(q_loot_18) == 0)
                        htmltext = "undres_q0333_02.htm";
                    else if (st.ownItemCount(q_loot_18) >= 1)
                        htmltext = "undres_q0333_03.htm";
                } else if (npcId == first_elder_lockirin) {
                    if (st.ownItemCount(q_loot_19) + st.ownItemCount(q_loot_20) + st.ownItemCount(q_loot_21) + st.ownItemCount(q_loot_22) < 1 && st.ownItemCount(q_loot_23) == 0)
                        htmltext = "first_elder_lockirin_q0333_01.htm";
                    else if (st.ownItemCount(q_loot_19) + st.ownItemCount(q_loot_20) + st.ownItemCount(q_loot_21) + st.ownItemCount(q_loot_22) >= 1 && st.ownItemCount(q_loot_23) == 0)
                        htmltext = "first_elder_lockirin_q0333_02.htm";
                    else if (st.ownItemCount(q_loot_23) >= 1)
                        htmltext = "first_elder_lockirin_q0333_03.htm";
                }
                break;
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == marsh_stakato) {
            if (st.ownItemCount(q_sophias_order4) >= 1) {
                if (Rnd.get(100) < 55) {
                    st.giveItems(q_stakato_talon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 12) {
                    st.giveItems(q_cargo_box4, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 2 && st.ownItemCount(q_sophias_order4) > 0) {
                    st.addSpawn(marsh_stakato_marquess);
                }
            }
        } else if (npcId == neer_crawler) {
            if (st.ownItemCount(q_sophias_order1) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_undead_ash, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 11) {
                    st.giveItems(q_cargo_box1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == specter) {
            if (st.ownItemCount(q_sophias_order1) >= 1) {
                if (Rnd.get(100) < 60) {
                    st.giveItems(q_undead_ash, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 8) {
                    st.giveItems(q_cargo_box1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == lemures) {
            if (st.ownItemCount(q_sophias_order1) >= 1) {
                if (Rnd.get(100) < 60) {
                    st.giveItems(q_undead_ash, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 9) {
                    st.giveItems(q_cargo_box1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == neer_crawler_frak) {
            if (st.ownItemCount(q_sophias_order1) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_undead_ash, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 12) {
                    st.giveItems(q_cargo_box1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == strain) {
            if (st.ownItemCount(q_sophias_order1) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_undead_ash, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 13) {
                    st.giveItems(q_cargo_box1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ghoul) {
            if (st.ownItemCount(q_sophias_order1) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_undead_ash, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 15) {
                    st.giveItems(q_cargo_box1, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_guerilla) {
            if (st.ownItemCount(q_sophias_order2) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_bloodyaxe_insignia, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 9) {
                    st.giveItems(q_cargo_box2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_raider) {
            if (st.ownItemCount(q_sophias_order2) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_bloodyaxe_insignia, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 10) {
                    st.giveItems(q_cargo_box2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_sniper) {
            if (st.ownItemCount(q_sophias_order2) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_bloodyaxe_insignia, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 11) {
                    st.giveItems(q_cargo_box2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_sergeant) {
            if (st.ownItemCount(q_sophias_order2) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_bloodyaxe_insignia, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 12) {
                    st.giveItems(q_cargo_box2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == ol_mahum_chief_leader) {
            if (st.ownItemCount(q_sophias_order2) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_bloodyaxe_insignia, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 13) {
                    st.giveItems(q_cargo_box2, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
            }
        } else if (npcId == marsh_stakato_worker) {
            if (st.ownItemCount(q_sophias_order4) >= 1) {
                if (Rnd.get(100) < 60) {
                    st.giveItems(q_stakato_talon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 13) {
                    st.giveItems(q_cargo_box4, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 2 && st.ownItemCount(q_sophias_order4) > 0) {
                    st.addSpawn(marsh_stakato_marquess);
                }
            }
        } else if (npcId == marsh_stakato_soldier) {
            if (st.ownItemCount(q_sophias_order4) >= 1) {
                if (Rnd.get(100) < 56) {
                    st.giveItems(q_stakato_talon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 14) {
                    st.giveItems(q_cargo_box4, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 2 && st.ownItemCount(q_sophias_order4) > 0) {
                    st.addSpawn(marsh_stakato_marquess);
                }
            }
        } else if (npcId == marsh_stakato_drone) {
            if (st.ownItemCount(q_sophias_order4) >= 1) {
                if (Rnd.get(100) < 60) {
                    st.giveItems(q_stakato_talon, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 15) {
                    st.giveItems(q_cargo_box4, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 2 && st.ownItemCount(q_sophias_order4) > 0) {
                    st.addSpawn(marsh_stakato_marquess);
                }
            }
        } else if (npcId == delu_lizardman || npcId == delu_lizardman_scout) {
            if (st.ownItemCount(q_sophias_order3) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_delu_lizardman_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 14) {
                    st.giveItems(q_cargo_box3, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 3 && st.ownItemCount(q_sophias_order3) > 0) {
                    st.addSpawn(delu_lizardm_headhunter);
                    st.addSpawn(delu_lizardm_headhunter);
                }
            }
        } else if (npcId == delu_lizardman_warrior) {
            if (st.ownItemCount(q_sophias_order3) >= 1) {
                if (Rnd.get(2) == 0) {
                    st.giveItems(q_delu_lizardman_fang, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 15) {
                    st.giveItems(q_cargo_box3, 1);
                    st.soundEffect(SOUND_ITEMGET);
                }
                if (Rnd.get(100) < 3 && st.ownItemCount(q_sophias_order3) > 0) {
                    st.addSpawn(delu_lizardm_headhunter);
                    st.addSpawn(delu_lizardm_headhunter);
                }
            }
        } else if (npcId == delu_lizardm_headhunter) {
            if (st.ownItemCount(q_sophias_order3) >= 1) {
                st.giveItems(q_delu_lizardman_fang, 4);
                st.soundEffect(SOUND_ITEMGET);
            }
        } else if (npcId == marsh_stakato_marquess) {
            if (st.ownItemCount(q_sophias_order4) >= 1) {
                st.giveItems(q_stakato_talon, 8);
                st.soundEffect(SOUND_ITEMGET);
            }
        }
        return null;
    }
}
